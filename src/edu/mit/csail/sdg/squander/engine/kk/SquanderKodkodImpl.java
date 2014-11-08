/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine.kk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import kodkod.ast.Decl;
import kodkod.ast.Decls;
import kodkod.ast.ExprToIntCast;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IfExpression;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.IntToExprCast;
import kodkod.ast.Node;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.engine.Evaluator;
import kodkod.engine.Proof;
import kodkod.engine.Solution;
import kodkod.engine.Solver;
import kodkod.engine.satlab.ReductionStrategy;
import kodkod.engine.satlab.SATFactory;
import kodkod.engine.ucore.AdaptiveRCEStrategy;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;
import kodkod.util.nodes.PrettyPrinter;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.engine.ForgeConverter;
import edu.mit.csail.sdg.squander.engine.ISquander;
import edu.mit.csail.sdg.squander.engine.PostExeTranslator;
import edu.mit.csail.sdg.squander.engine.ISquanderResult.IEvaluator;
import edu.mit.csail.sdg.squander.engine.SquanderEval2;
import edu.mit.csail.sdg.squander.engine.SquanderImpl;
import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;
import edu.mit.csail.sdg.squander.spec.ForgeScene;
import edu.mit.csail.sdg.squander.spec.Spec.SpecCase;
import edu.mit.csail.sdg.squander.spec.Tr;
import edu.mit.csail.sdg.squander.spec.constant.ConstRel2KKConst;
import forge.program.BinaryExpression;
import forge.program.BooleanLiteral;
import forge.program.ConditionalExpression;
import forge.program.ExpressionVisitor;
import forge.program.ForgeExpression;
import forge.program.ForgeLiteral;
import forge.program.ForgeProgram;
import forge.program.ForgeType;
import forge.program.ForgeType.Tuple;
import forge.program.ForgeType.Unary;
import forge.program.ForgeVariable;
import forge.program.GlobalVariable;
import forge.program.InstanceDomain;
import forge.program.InstanceLiteral;
import forge.program.IntegerDomain;
import forge.program.IntegerLiteral;
import forge.program.LocalDecls;
import forge.program.LocalVariable;
import forge.program.OldExpression;
import forge.program.ProjectionExpression;
import forge.program.QuantifyExpression;
import forge.program.QuantifyExpression.Op;
import forge.program.UnaryExpression;
import forge.solve.ForgeAtom;
import forge.solve.ForgeBounds;
import forge.solve.ForgeConstant;
import forge.solve.IntegerAtom;
import forge.transform.ExpressionDescender;

/**
 * An implementation of the {@link ISquander} interface that
 * uses Kodkod as the back-end.
 * 
 * @author Aleksandar Milicevic
 */
public class SquanderKodkodImpl extends SquanderImpl {

    // ===========================================================================
    // ---------------------- DiscoverHigherOrderQuant ---------------------------
    // ===========================================================================
    
    static class DiscoverHigherOrderQuant extends ExpressionDescender {
        static final DiscoverHigherOrderQuant instance = new DiscoverHigherOrderQuant();
        
        boolean higherOrderFound = false;

        @Override
        protected void descend(QuantifyExpression expr) {
            String declMult = Tr.absolutelyTerribleHack.get(expr.decls());
            if (declMult == null) {
                super.descend(expr);
                return; 
            }
            if ("DECL_SET".equals(declMult)) {
                higherOrderFound = true; 
                return;
            }
            super.descend(expr);
        }
    }
    
    
    // ===========================================================================
    // ----------------------------- KodkodEval ----------------------------------
    // ===========================================================================
    
    /**
     * An implementation of the {@link IEvaluator} interface that 
     * wraps a Kodkod solution.
     */
    class KodkodEval implements IEvaluator {

        protected final Iterator<Solution> solutions;
        protected Solution solution;
        private kodkod.engine.config.Options options;
        
        public KodkodEval(Iterator<Solution> solutions, kodkod.engine.config.Options options) {
            this.solutions = solutions;
            this.options = options;
            this.solution = solutions.next();
        }

        @Override
        public String unsatCore() {
            Proof proof = solution.proof();
            if (proof == null)
                return "unknown";
            ReductionStrategy strategy = new AdaptiveRCEStrategy(proof.log()); 
            proof.minimize(strategy);
            int minCore = proof.highLevelCore().size();
            StringBuilder sb = new StringBuilder(); 
            sb.append("Core (size="+minCore+"):\n");
            for(Node n : proof.highLevelCore().values()) { 
                sb.append(PrettyPrinter.print(n, 0)).append("\n");
            }
            return sb.toString();
        }

        @Override
        public ObjTupleSet evaluate(ForgeExpression expr) {
            Evaluator eval = new Evaluator(solution.instance(), options);
            Tr2KK tr = new Tr2KK();
            Node n = expr.accept(tr);
            if (n instanceof Formula) {
                boolean res = eval.evaluate((Formula)n);
                return ObjTupleSet.singleTuple(res);
            } else if (n instanceof Expression) {
                TupleSet ts = eval.evaluate((Expression) n); 
                return makeConst(ts);
            } else if (n instanceof IntExpression) {
                int res = eval.evaluate((IntExpression) n);
                return ObjTupleSet.singleTuple(res);
            }
            throw new RuntimeException("unknown expression");
        }
        
        @Override
        public ForgeExpression parse(String expr) {
            return new PostExeTranslator(fconv).translate(expr);
        }

        protected ObjTupleSet makeConst(TupleSet ts) {
            ObjTupleSet res = new ObjTupleSet(ts.arity());
            for (kodkod.instance.Tuple t : ts) {
                ObjTuple fTuple = new ObjTuple();
                for (int i = 0; i < t.arity(); i++) {
                    Object obj = t.atom(i);
                    Object fAtom;
                    if (obj instanceof Integer)
                        fAtom = obj;
                    else if (obj instanceof Boolean)
                        fAtom = obj;
                    else if ("true".equals(obj))
                        fAtom = true;
                    else if ("false".equals(obj))
                        fAtom = false;
                    else
                        fAtom = forgeScene.objForLit(obj.toString());
                    fTuple = ObjTuple.product(fTuple, fAtom);
                }
                if (fTuple.arity() != 0)
                    res.add(fTuple);
            }
            return res;
        }

        @Override
        public boolean hasSolution() {
            return solution != null && solution.instance() != null;
        }

        @Override
        public IEvaluator nextSolution() {
            if (solutions.hasNext()) {
                solution = solutions.next();
            } else {
                solution = null;
            }
            return this;
        }

        @Override
        public String trace() {
            return solution.toString();
        }

        @Override
        public String stats() {
            return solution.stats().toString();
        } 

    }

    // ===========================================================================
    // -------------------------- Translator to Kodkod ---------------------------
    // ===========================================================================
    
    /**
     * Translates Forge expressions to Kodkod expressions/formulas
     */
    class Tr2KK extends ExpressionVisitor<Node> {
        
        private class StackElem {
            final String name;
            final Expression var;

            StackElem(String name, Expression var) {
                this.name = name;
                this.var = var;
            }
        }            

        private final Stack<StackElem> quantStack = new Stack<StackElem>();

        public Tr2KK() { }

        @Override
        protected Expression visit(ForgeType ftype) {
            Expression expr = type2expr.get(ftype);
            if (expr == null) {
                expr = visitForgeType(ftype);
                type2expr.put(ftype, expr);
            }
            return expr;
        }

        private Expression visitForgeType(ForgeType ftype) {
            Expression expr = null;
            for (Tuple t : ftype.tupleTypes()) {
                Expression tt = null;
                for (int i = 0; i < t.arity(); i++) {
                    Expression projType = type2expr.get(t.projectType(i));
                    if (projType == null)
                        projType = Expression.NONE;
                    if (tt == null)
                        tt = projType;
                    else 
                        tt = tt.product(projType);
                }
                if (expr == null)
                    expr = tt; 
                else
                    expr = expr.union(tt);
            }
            return expr;
        }

        @Override
        protected Node visit(ForgeLiteral lit) {
            if (lit instanceof IntegerLiteral) {
                int val = ((IntegerLiteral) lit).value();
                return IntConstant.constant(val);
            }
            return lit2rel.get(lit.name());
        }

        @Override
        protected Expression visit(ForgeVariable var) {
            Expression constExpr = new ConstRel2KKConst().convert(var.name());
            if (constExpr != null)
                return constExpr;
            
            if (var.isGlobal())
                return getRelForVarAndCheck(var);            
            
            StackElem elem = searchStack(var.name());
            if (elem != null)
                return elem.var;

            // else, it must be "this", "return" or method parameter, for which we have relations
            return getRelForVarAndCheck(var);
        }

        @Override
        protected Node visit(UnaryExpression expr) {
            switch (expr.op()) {
                case NOT: {
                    Formula sub = node2form(expr.sub().accept(this));
                    return sub.not();
                }
                case SOME: {
                    Expression sub = node2expr(expr.sub().accept(this));
                    return sub.some();
                }
                case NO: {
                    Expression sub = node2expr(expr.sub().accept(this));
                    return sub.no();
                }
                case LONE: {
                    Expression sub = node2expr(expr.sub().accept(this));
                    return sub.lone();
                }
                case ONE: {
                    Expression sub = node2expr(expr.sub().accept(this));
                    return sub.one();
                }
                case CLOSURE: {
                    Expression sub = node2expr(expr.sub().accept(this));
                    return sub.closure();
                }
                case NEG: {
                    Node subNode = expr.sub().accept(this);
                    return node2int(subNode).negate();
                } 
                case CARDINALITY: { 
                    Node subNode = expr.sub().accept(this);
                    return node2expr(subNode).count();
                }
                case BIT_NOT: {
                    Node subNode = expr.sub().accept(this);
                    return node2int(subNode).not();
                }
                case SUM: {
                    Node sumNode = expr.sub().accept(this);
                    Variable v = Variable.unary("k");
                    return node2int(v).sum(v.oneOf(node2expr(sumNode)));
                }
                default: {
                    throw new RuntimeException("Unsupported unary operation: " + expr.op() + " in "
                            + expr);
                }
            }
        }

        @Override
        protected Node visit(BinaryExpression expr) {
            switch (expr.op()) {
            case AND: {
                Formula lhs = node2form(expr.left().accept(this));
                Formula rhs = node2form(expr.right().accept(this));
                return lhs.and(rhs);
            } case OR: { 
                Formula lhs = node2form(expr.left().accept(this));
                Formula rhs = node2form(expr.right().accept(this));
                return lhs.or(rhs);
            } case XOR: { 
                Formula lhs = node2form(expr.left().accept(this));
                Formula rhs = node2form(expr.right().accept(this));
                return lhs.and(rhs.not()).or(lhs.not().and(rhs));
            } case IMPLIES: { 
                Formula lhs = node2form(expr.left().accept(this));
                Formula rhs = node2form(expr.right().accept(this));
                return lhs.implies(rhs);
            } case IFF: {
                Formula lhs = node2form(expr.left().accept(this));
                Formula rhs = node2form(expr.right().accept(this));
                return lhs.iff(rhs);
            } case EQUALS: {
                Node leftNode = expr.left().accept(this);
                Node rightNode = expr.right().accept(this);
                if ((leftNode instanceof IntExpression) && (rightNode instanceof IntExpression)) {
                    IntExpression lhs = (IntExpression) leftNode;
                    IntExpression rhs = (IntExpression) rightNode;
                    return lhs.eq(rhs);
                }
                Expression lhs = node2expr(leftNode);
                Expression rhs = node2expr(rightNode);
                return lhs.eq(rhs);
            } case GT: { 
                Node lhs = expr.left().accept(this);
                Node rhs = expr.right().accept(this);
                return node2int(lhs).gt(node2int(rhs));
            } case GTE: {
                Node lhs = expr.left().accept(this);
                Node rhs = expr.right().accept(this);
                return node2int(lhs).gte(node2int(rhs));
            } case LT: {
                Node lhs = expr.left().accept(this);
                Node rhs = expr.right().accept(this);
                return node2int(lhs).lt(node2int(rhs));
            } case LTE: { 
                Node lhs = expr.left().accept(this);
                Node rhs = expr.right().accept(this);
                return node2int(lhs).lte(node2int(rhs));
            } case PLUS: { 
                Node lhs = expr.left().accept(this);
                Node rhs = expr.right().accept(this);
                return node2int(lhs).plus(node2int(rhs));
            } case MINUS: {
                Node lhs = expr.left().accept(this);
                Node rhs = expr.right().accept(this);
                return node2int(lhs).minus(node2int(rhs));
            } case TIMES: {
                Node lhs = expr.left().accept(this);
                Node rhs = expr.right().accept(this);
                return node2int(lhs).multiply(node2int(rhs));
            } case DIVIDE: {
                Node lhs = expr.left().accept(this);
                Node rhs = expr.right().accept(this);
                return node2int(lhs).divide(node2int(rhs));
            } case MODULO: {
                Node lhs = expr.left().accept(this);
                Node rhs = expr.right().accept(this);
                return node2int(lhs).modulo(node2int(rhs));
            } case JOIN: {
                Expression lhs = node2expr(expr.left().accept(this));
                Expression rhs = node2expr(expr.right().accept(this));
                return lhs.join(rhs);
            } case PRODUCT: {
                Expression lhs = node2expr(expr.left().accept(this));
                Expression rhs = node2expr(expr.right().accept(this));
                return lhs.product(rhs);
            } case UNION: {
                Expression lhs = node2expr(expr.left().accept(this));
                Expression rhs = node2expr(expr.right().accept(this));
                return lhs.union(rhs);
            } case DIFFERENCE: {
                Expression lhs = node2expr(expr.left().accept(this));
                Expression rhs = node2expr(expr.right().accept(this));
                return lhs.difference(rhs);
            } case SUBSET: { 
                Expression lhs = node2expr(expr.left().accept(this));
                Expression rhs = node2expr(expr.right().accept(this));
                return lhs.in(rhs);
            } case OVERRIDE: {
                Expression lhs = node2expr(expr.left().accept(this));
                Expression rhs = node2expr(expr.right().accept(this));
                return lhs.override(rhs);
            } case INTERSECTION: {
                Expression lhs = node2expr(expr.left().accept(this));
                Expression rhs = node2expr(expr.right().accept(this));
                return lhs.intersection(rhs);
            } default:
                throw new RuntimeException("Unsupported bin operation: " + expr.op() + " in " + expr);
            }
        }

        @Override
        protected Node visit(ConditionalExpression expr) {
            Formula cond = node2form(expr.condition().accept(this));
            Node thenNode = expr.thenExpr().accept(this);
            Node elseNode = expr.elseExpr().accept(this);
            return cond.thenElse(node2expr(thenNode), node2expr(elseNode));
        }

        @Override
        protected Node visit(ProjectionExpression expr) {
            Node sub = expr.sub().accept(this);
            IntExpression[] cols = new IntExpression[expr.columns().size()];
            int idx = 0; 
            for (int col : expr.columns()) {
                cols[idx++] = IntConstant.constant(col);
            }
            return node2expr(sub).project(cols);
        }

        @Override
        protected Node visit(QuantifyExpression expr) {
            if (!SquanderGlobalOptions.INSTANCE.desugar_quants)
                return visit2(expr);
            if (expr.op() == Op.UNION)
                return visit2(expr);  //don't know how to handle union
            LocalDecls localDecls = expr.decls();
            // if there are any "set" multiplicities use Kodkod's quantifiers 
            String mult = Tr.absolutelyTerribleHack.get(localDecls);
            if ("DECL_SET".equals(mult))
                return visit2(expr);
            
            // check if there are nested "DECL_SET" quantifiers (if not, no need to desugar)
            expr.sub().accept(DiscoverHigherOrderQuant.instance);
            if (!DiscoverHigherOrderQuant.instance.higherOrderFound)
                return visit2(expr);
            
            // desugar
            List<LocalVariable> locals = new ArrayList<LocalVariable>(localDecls.locals().size());
            List<List<ForgeLiteral>> literals = new ArrayList<List<ForgeLiteral>>(locals.size());
            for (LocalVariable var : localDecls.locals()) {
                List<ForgeLiteral> lits = fconv.findLiteralsForType((Unary) var.type());
                if (lits.size() > 0) {
                    locals.add(var);
                    literals.add(lits);
                }
            }
            int nLocals = locals.size();
            if (nLocals == 0)
                return trueRelation;
            Node result = null;
            int[] idx = new int[nLocals];
            while (true) {
                for (int i = 0; i < nLocals; i++) {
                    ForgeLiteral lit = literals.get(i).get(idx[i]);
                    Expression litExpr = node2expr(lit.accept(this));
                    quantStack.push(new StackElem(locals.get(i).name(), litExpr));
                }
                Node f = expr.sub().accept(this);
                if (result == null) {
                    result = f;
                } else {
                    switch (expr.op()) {
//                    case UNION: 
//                        result = node2expr(result).union(node2expr(f));
//                        break;
                    case ALL: 
                        result = node2form(result).and(node2form(f));
                        break;
                    case SOME: 
                        result = node2form(result).or(node2form(f));
                        break;
                    default: 
                        throw new RuntimeException("Unsupported quantify expression: " + expr.op() + " in " + expr);
                    }
                }
                for (int i = 0; i < nLocals; i++)
                    quantStack.pop();
                boolean broke = false;
                for (int i = nLocals - 1; i >= 0; i--) {
                    if (idx[i] + 1 < literals.get(i).size()) {
                        idx[i]++;
                        broke = true;
                        break;
                    }
                }
                if (!broke)
                    break;
            }
            return result;
        }
        
        protected Node visit2(QuantifyExpression expr) {
            LocalDecls localDecls = expr.decls();
            final String mult = Tr.absolutelyTerribleHack.get(localDecls);
            int nLocals = localDecls.locals().size();
            List<Variable> vars = new ArrayList<Variable>(nLocals);
            Decls decls = null;
            for (LocalVariable var : localDecls.locals()) {
                Variable kkVar = Variable.nary(var.name(), var.arity());
                Expression varType = visit(var.type());
                Decl decl;
                if ("DECL_SET".equals(mult))
                    decl = kkVar.setOf(varType);
                else 
                    decl = kkVar.oneOf(varType);
                if (decls == null)
                    decls = decl;
                else 
                    decls = decls.and(decl);
                quantStack.push(new StackElem(kkVar.name(), kkVar));
                vars.add(kkVar);
            }
            try {
                Node node = expr.sub().accept(this);
                Formula f = node2form(node);
                switch (expr.op()) {
                    case UNION: 
                        return f.comprehension(decls);
                    case ALL: 
                        return fix(f.forAll(decls));
                    case SOME: 
                        return fix(f.forSome(decls));
                    case SUM: 
                        return node2int(node).sum(decls);
                    default: 
                        throw new RuntimeException("Unsupported quantify expression: " + expr.op() + " in " + expr);
                }
            } finally {
                for (int i = 0; i < nLocals; i++)
                    quantStack.pop();
            }
        }

        /*
         * changes the following stuff:
         * 
         * (1)
         *   all c : Cell | c in <expr> => <formula> 
         * to
         *   all c : <expr> | <formula>
         *   
         * (2) 
         *   all c1 : Cell | all c2 : Cell | <formula>
         * to 
         *   all c1 : Cell, c2 : Cell | <formula> 
         *   
         * neither seems to make any difference in terms of performance
         */
        private Node fix(Formula quantFormula) {
//            QuantifiedFormula qf = (QuantifiedFormula) quantFormula;
//            if (qf.decls().size() == 1) {
//                if (qf.formula() instanceof BinaryFormula) {
//                    BinaryFormula bf = (BinaryFormula) qf.formula();
//                    if (bf.op() == FormulaOperator.IMPLIES) {
//                        Decl v = qf.decls().get(0);
//                        Variable var = v.variable();
//                        if (bf.left() instanceof ComparisonFormula) {
//                            ComparisonFormula cf = (ComparisonFormula) bf.left();
//                            if (cf.op() == ExprCompOperator.SUBSET && cf.left().equals(var)) {
//                                Decls newDecls = var.oneOf(node2expr(cf.right()));
//                                return bf.right().quantify(qf.quantifier(), newDecls);
//                            }
//                        }
//                    }
//                } else if (qf.formula() instanceof QuantifiedFormula) {
//                    QuantifiedFormula qf2 = (QuantifiedFormula) qf.formula();
//                    if (qf.quantifier() == qf2.quantifier()) {
//                        Decls newDecls = qf.decls().and(qf2.decls());
//                        return qf2.formula().quantify(qf.quantifier(), newDecls);
//                    }
//                }
//            }
            return quantFormula;
        }

        @Override
        protected Expression visit(OldExpression expr) {
            ForgeVariable var = expr.variable();
            assert var.isGlobal() : "you can only refer to global variables in the pre-state";
            Expression rel = var2rel.get(relName(var) + "_pre");
            if (rel == null) // variable is not modifiable, so it's safe to use values in the post state
                rel = getRelForVarAndCheck(var);
            return rel; 
        }
        
        private Expression getRelForVarAndCheck(ForgeVariable var) {
            Expression rel = var2rel.get(relName(var));
            assert rel != null : "relation for " + var.name() + " not found";
            return rel;
        }
        
        private StackElem searchStack(String varName) {
            for (int i = quantStack.size() - 1; i >= 0; i--) {
                StackElem elem = quantStack.get(i);
                if (elem.name.equals(varName))
                    return elem;
            }
            return null;
        }
        
    }

    // =========================================================================== 
    
    protected Set<GlobalVariable> modifies; 
    protected ForgeConverter fconv;
    protected ForgeScene forgeScene;
    protected ForgeProgram program;

    protected Map<String, Relation> lit2rel;
    protected Map<ForgeType, Expression> type2expr;
    protected Map<String, Expression> var2rel;
    protected Map<ForgeVariable, ObjTupleSet> modVal;
    protected Map<ForgeVariable, ObjTupleSet> lowerBounds;
    protected Map<ForgeVariable, ObjTupleSet> upperBounds;
    
    protected Set<Integer> ints;
    protected Relation trueRelation;
    protected Relation falseRelation;
    protected SpecCase cs;

    public SquanderKodkodImpl() {
        modVal = new HashMap<ForgeVariable, ObjTupleSet>();
        lowerBounds = new HashMap<ForgeVariable, ObjTupleSet>();
        upperBounds = new HashMap<ForgeVariable, ObjTupleSet>();
    }
    
    @Override
    protected Set<GlobalVariable> getModsForPostState(ForgeConverter fconv, SpecCase sc) {
        return sc.frame().modifiable();
    }

    @Override
    protected ForgeExpression getPreSpec(SpecCase cs) {
        this.cs = cs;
        // NOTE: none of the fields (program, forgeScene, ...) are initializes at this point
        return cs.pre().and(cs.spec().abstractConstraint());
    }

    @Override
    protected ForgeExpression getPostSpec(SpecCase cs, ForgeConverter fconv) {
        this.cs = cs;
        Set<GlobalVariable> unmod = new HashSet<GlobalVariable>();
        unmod.addAll(fconv.forgeScene().program().globalVariables());
        unmod.removeAll(cs.frame().modifiable());
        ForgeExpression well = cs.spec().wellformedPost(unmod);

        ForgeExpression post = cs.spec().abstractConstraint()
                 .and(cs.post())
                 .and(cs.spec().funcConstraint())
                 .and(well);
        
        for (GlobalVariable g : cs.frame().modifiable()) {
            // old stuff: instance selector + upper bound
            ForgeExpression instSelector = cs.frame().instSelector(g);
            ForgeExpression upper = cs.frame().upperBound(g);
            SquanderEval2 se = new SquanderEval2();
            try {
                if (instSelector != null) {
                    ObjTupleSet instMod = se.eval(instSelector, fconv);
                    modVal.put(g, instMod);
                    if (upper != null) {
                        try {
                            ObjTupleSet up = se.eval(upper, fconv);
                            upperBounds.put(g, instMod.product(up));
                        } catch (Throwable t) {
                            Log.warn("Could not evaluate upper bound expression " + upper + ": " + t.getMessage());
                            post = post.and(cs.frame().upperCond(g));
                        }
                    }
                }
            } catch (Throwable t) {
                Log.warn("Could not evaluate instance selector expression " + instSelector + ": " + t.getMessage());
                post = post.and(cs.frame().modCond(g));
            }
            // new stuff: frame filter
            ForgeExpression filter = cs.frame().filter(g);
            if (filter != null) {
                try {
                    ForgeExpression loc = cs.frame().location(g);
                    // loc x (loc.g)
                    ForgeExpression domain = loc.product(loc.join(g));
                    ObjTupleSet low = se.select(filter, domain, fconv);
                    lowerBounds.put(g, low);                    
                } catch (Throwable t) {
                    throw new RuntimeException("Could not evaluate frame filter expression " + filter, t);
                    // TODO: post = post.and(cs.frame().filterCond(g));
                }
            }
        }
        
        return post;
    }
    
    @Override
    protected IEvaluator exeSpec(ForgeExpression spec, Set<GlobalVariable> modifies, ForgeConverter fconv) {
        this.modifies = modifies;
        this.fconv = fconv;
        this.forgeScene = fconv.forgeScene();
        this.program = forgeScene.program();

        init();
        
        createRelations();
          
        Bounds bounds = createBounds();
        Log.log(printBoundsSummary(bounds));
        
        Formula formula = convertSpec(spec);
        
        Solver solver = new Solver();
        Log.log("Using bitwidth: " + fconv.bw()); 
        solver.options().setBitwidth(fconv.bw());
//        solver.options().setFlatten(false);
        solver.options().setReporter(SquanderGlobalOptions.INSTANCE.reporter.kkReporter());
        solver.options().setNoOverflow(SquanderGlobalOptions.INSTANCE.noOverflow);
        if (SquanderGlobalOptions.INSTANCE.unsat_core) {
            solver.options().setLogTranslation(1);
            solver.options().setSolver(SATFactory.MiniSatProver); // TODO: read from global options
            solver.options().setCoreGranularity(3);
        } else {
            solver.options().setLogTranslation(0);
            solver.options().setSolver(SquanderGlobalOptions.INSTANCE.sat_solver); 
        }
        
        boolean solveAll = false;
        Options opts = fconv.javaScene().methodSpec().options();
        if (opts != null)
            solveAll = opts.solveAll();
        
        if (solveAll)
            solver.options().setSymmetryBreaking(1000);
        
        Log.debug("==============================================");
        Log.debug("------------- Solving kk formula -------------");
        Log.debug("==============================================");
        Log.debug(PrettyPrinter.print(formula, 2));
        Log.debug("\n" + bounds);
        IEvaluator eval;
        //new KKSimplifier().simplify(formula, bounds);
        if (solveAll) {
            Iterator<Solution> solutions = solver.solveAll(formula, bounds);
            eval = getEval(solutions, solver.options());
        } else {
            Solution solution = solver.solve(formula, bounds);
            eval = getEval(Collections.singleton(solution).iterator(), solver.options());
        }
        return eval;
    }
    
    private String printBoundsSummary(Bounds bounds) {
        List<Entry<Relation, TupleSet>> bnds = new ArrayList<Entry<Relation,TupleSet>>();
        bnds.addAll(bounds.upperBounds().entrySet());
        Collections.sort(bnds, new Comparator<Entry<Relation, TupleSet>>() {
            @Override
            public int compare(Entry<Relation, TupleSet> o1, Entry<Relation, TupleSet> o2) {
                return -(new Integer(o1.getValue().size()).compareTo(o2.getValue().size()));
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append("Bound stats: \n");
        sb.append("-----------------------------------------------------\n");
        for (Entry<Relation, TupleSet> e : bnds) {
            if (e.getKey().arity() == 1 && e.getValue().size() <= 1)
                continue;
            sb.append(String.format("%40s (%s) : %s\n", e.getKey().name(), e.getKey().arity(), e.getValue().size()));
        }
        return sb.toString();
    }

    protected IEvaluator getEval(Iterator<Solution> solution, kodkod.engine.config.Options options) {
        KodkodEval kodkodEval = new KodkodEval(solution, options);
        return kodkodEval;
    }

    protected void init() {
        lit2rel = new LinkedHashMap<String, Relation>();
        type2expr = new LinkedHashMap<ForgeType, Expression>();
        type2expr.put(program.integerDomain(), Expression.INTS);
        type2expr.put(program.booleanDomain(), Relation.unary("boolean"));
        var2rel = new LinkedHashMap<String, Expression>();
        ints = new HashSet<Integer>();
    }
    
    protected Formula convertSpec(ForgeExpression spec) {
        Node node = spec.accept(new Tr2KK());
        return node2form(node);
    }

    protected void createRelations() {
        // create sigs (unary relations corresponding to individual objects)
        // also create union expressions for instance domains
        List<ForgeLiteral> literals = new LinkedList<ForgeLiteral>();
        literals.addAll(program.instanceLiterals());
        literals.add(program.trueLiteral());
        literals.add(program.falseLiteral());
        for (ForgeLiteral lit : literals) {
            Relation rel = Relation.unary(lit.name());
            lit2rel.put(lit.name(), rel);
//            Expression domType = type2expr.get(lit.type());
//            if (domType == null)
//                domType = rel;
//            else 
//                domType = domType.union(rel);
//            type2expr.put(lit.type(), domType);
        }
        this.trueRelation = lit2rel.get(program.trueLiteral().name());
        this.falseRelation = lit2rel.get(program.falseLiteral().name());
        
        // create relations for instance domains
        for (InstanceDomain dom : program.instanceDomains()) {
            type2expr.put(dom, Relation.nary(dom.name(), dom.arity()));
        }
        
        // create relations for global variables
        for (GlobalVariable g : program.globalVariables()) {
            // don't create relations for constant relations already built-in in Kodkod 
            if (new ConstRel2KKConst().convert(g.name()) != null)
                continue;
            addRelForVar(g, relName(g));
            if (modifies.contains(g))
                addRelForVar(g, relName(g) + "_pre");
        }
        
        // create relations for "this", "return" and method parameters
        if (forgeScene.thisVar() != null)
            addRelForVar(forgeScene.thisVar(), relName(forgeScene.thisVar()));
        for (LocalVariable var : forgeScene.args())
            addRelForVar(var, relName(var));
        if (forgeScene.returnVar() != null) 
            addRelForVar(forgeScene.returnVar(), relName(forgeScene.returnVar()));
    }
    
    protected Bounds createBounds() {
        ForgeBounds forgeBounds = fconv.forgeBounds();
        reporter.creatingKodkodUniverse();
        Universe univ = createUniverse();
        
        reporter.creatingKodkodBounds();
        TupleFactory f = univ.factory();
        Bounds b = new Bounds(univ);
        
        // bound relations for literals
        for (Entry<String, Relation> e : lit2rel.entrySet()) {
            b.boundExactly(e.getValue(), f.setOf(e.getKey()));
        }
        
        // bound relations for instance domains
        for (InstanceDomain dom : program.instanceDomains()) {
            List<ForgeLiteral> instLiterals = fconv.findLiteralsForType(dom);
            TupleSet bound = f.noneOf(dom.arity());
            if (!instLiterals.isEmpty()) {
                Object[] atoms = new Object[instLiterals.size()];
                int idx = 0;
                for (ForgeLiteral lit : instLiterals) {
                    atoms[idx++] = lit.name();
                }
                bound = f.setOf(atoms);
            }
            b.boundExactly((Relation)type2expr.get(dom), bound);
        }

        // bound boolean
        b.boundExactly((Relation)type2expr.get(program.booleanDomain()), 
                f.setOf(program.trueLiteral().name(), program.falseLiteral().name()));
        
        // bound global variables
        for (GlobalVariable var : program.globalVariables()) {
            Relation rel = (Relation) var2rel.get(relName(var));
            // if a relation was not created for this var (e.g. because it's built-in in Kodkod) skip creating bounds
            if (rel == null)
                continue; 
            ForgeConstant lower = forgeBounds.initialLowerBound(var);
            ForgeConstant upper = lower; 
            if (forgeScene.isSpecField(var)) {
                upper = forgeBounds.initialUpperBound(var);
            }
            TupleSet lowerTupleSet = conv2tuples(lower, f);
            TupleSet upperTupleSet = conv2tuples(upper, f);
            if (modifies.contains(var)) {
                Relation preRel = (Relation) var2rel.get(relName(var) + "_pre");
                b.bound(preRel, lowerTupleSet, upperTupleSet);
                ForgeConstant postLower = getPostLower(var, lower);
                ForgeConstant postUpper = getPostUpper(var, postLower);
                b.bound(rel, conv2tuples(postLower, f), conv2tuples(postUpper, f));
            } else {
                b.bound(rel, lowerTupleSet, upperTupleSet);
            }
        }
        
        // bound relations for "this", "return" and method parameters
        boundLocalVar(forgeScene.thisVar(), f, b);
        for (LocalVariable var : forgeScene.args())
            boundLocalVar(var, f, b);
        boundLocalVar(forgeScene.returnVar(), f, b);

        // bound integers
        for (Integer i : ints) {
            b.boundExactly(i, f.setOf(i));
        }
        
        return b;
    }
    
    protected ForgeConstant getPostLower(GlobalVariable g, ForgeConstant initialBound) {
        ForgeBounds forgeBounds = fconv.forgeBounds();
        ObjTupleSet instSelector = modVal.get(g);
        ObjTupleSet filter = lowerBounds.get(g);
        if (filter != null) { 
            ForgeConstant modifiableTuples = fconv.conv2fc(filter, forgeBounds);
            return initialBound.difference(modifiableTuples);
        } else if (instSelector != null) {
            List<ForgeLiteral> lits = new LinkedList<ForgeLiteral>(fconv.findLiteralsForType(g
                    .type().domain()));
            for (ObjTuple t : instSelector.tuples()) {
                assert t.arity() == 1;
                if (t.arity() == 1) {
                    InstanceLiteral l = forgeScene.instLitForObj(t.get(0));
                    assert l != null;
                    lits.remove(l);
                }
            }
            ForgeConstant res = forgeBounds.empty(g.arity());
            for (ForgeLiteral lit : lits) {
                ForgeAtom atom = lit2atom(lit, forgeBounds);
                res = res.union(atom.product(atom.join(initialBound)));
            }
            return res;
        } else {
            return forgeBounds.empty(g.arity());
        }
    }

    private ForgeAtom lit2atom(ForgeLiteral lit, ForgeBounds forgeBounds) {
        ForgeAtom atom;
        if (lit instanceof IntegerLiteral)
            atom = forgeBounds.intAtom(((IntegerLiteral) lit).value());
        else if (lit instanceof BooleanLiteral)
            atom = forgeBounds.boolAtom(((BooleanLiteral) lit).value());
        else
            atom = forgeBounds.instanceAtom((InstanceLiteral) lit);
        return atom;
    }
    
    protected ForgeConstant getPostUpper(GlobalVariable var, ForgeConstant postLower) {
        ForgeConstant extent = getExtent(var, postLower);
        ObjTupleSet upper = upperBounds.get(var);
        if (upper != null) 
            extent = fconv.conv2fc(upper, fconv.forgeBounds());
        ObjTupleSet ots = modVal.get(var); 
        if (ots == null)
            return extent; 
        ForgeConstant mod = fconv.conv2fc(ots, fconv.forgeBounds());
        return mod.product(mod.join(extent)).union(postLower);
    }
    
    protected Universe createUniverse() {
        Collection<Object> atoms = new LinkedList<Object>();
        // add all literals
        for (InstanceLiteral lit : program.instanceLiterals()) 
            atoms.add(lit.name());
        // add integers
        if (forgeScene.isEnsureAllInts()) {
            for (int val = fconv.minInt(); val <= fconv.maxInt(); val++) {
                atoms.add(val);
                ints.add(val);
            }
        } else {
            int maxInt = fconv.maxInt();
            int minInt = fconv.minInt();
            for (int intValue : forgeScene.ints()) {
                if (intValue > maxInt || intValue < minInt)
                    continue;
                atoms.add(intValue);
                ints.add(intValue);
            }
        }
        // add booleans
        atoms.add(program.trueLiteral().name());
        atoms.add(program.falseLiteral().name());
        return new Universe(atoms);
    }

    // ==========================================================================
    // ---------------------------- Helpers -------------------------------------
    // ==========================================================================
    
    /** converts a Kodkod node to a Kodkod formula. */
    protected Formula node2form(Node node) {
        if (node == trueRelation)  return Formula.TRUE;
        if (node == falseRelation) return Formula.FALSE;
        if (node instanceof Formula)
            return (Formula) node;
        if (node instanceof IfExpression) {
            IfExpression ifExpr = (IfExpression) node;
            if (ifExpr.thenExpr().equals(trueRelation) &&
                ifExpr.elseExpr().equals(falseRelation)) {
                return ifExpr.condition();
            }
        } 
        if (node instanceof Expression)
            return ((Expression) node).in(trueRelation);
        throw new RuntimeException("don't know how to convert " + node.getClass().getSimpleName() + " to formula: " + node);
    }
    
    /** converts a Kodkod expression to a Kodkod integer. */
    protected IntExpression node2int(Node node) {
        if (node instanceof Expression)
            return ((Expression) node).sum();
        if (node instanceof IntExpression)
            return (IntExpression) node;
        if (node instanceof IntToExprCast)
            return ((IntToExprCast) node).intExpr();
        throw new RuntimeException("don't know how to convert " + node.getClass().getSimpleName() + " to int");
    }
    
    /** converts a Kodkod node to a Kodkod expression. */
    protected Expression node2expr(Node node) {
        if (node instanceof Expression)
            return (Expression)node;
        if (node instanceof IntExpression) 
            return ((IntExpression) node).toExpression();
        if (node instanceof ExprToIntCast)
            return ((ExprToIntCast) node).expression();
        if (node instanceof Formula)
            return form2expr((Formula) node);
        throw new RuntimeException("don't know how to convert " + node.getClass().getSimpleName() + " to expression");
    }
    
    /** converts a Kodkod formula to a Kodkod expressoin */
    protected Expression form2expr(Formula form) {
        if (form == Formula.TRUE)  return trueRelation;
        if (form == Formula.FALSE) return falseRelation;
        return form.thenElse(trueRelation, falseRelation);
    }
    
    protected ForgeConstant getExtent(GlobalVariable var, ForgeConstant lower) {
        String name = var.name();
        ForgeType type = var.type();
        ForgeBounds forgeBounds = fconv.forgeBounds();
        if (name.endsWith("[]__elts")) { //TODO
            // make bounds for array elems tighter (indexes should be non-negative integers)
            assert type.arity() == 3 : 
                "expected arity 3 for array elts but actual arity is " + var.arity();
            assert type.projectType(1) instanceof IntegerDomain : 
                "expected the 2nd col to be IntegerDomain but is " + type.projectType(1).getClass();
            int upperBound = findMaxArrayLength(var);
            return forgeBounds.extent(type.projectType(0))
                    .product(nonnegs(upperBound))
                    .product(forgeBounds.extent(type.projectType(2)));
        } else if (name.endsWith("[]__length")) { //TODO
            // make bounds for array length tighter (length can only be non-negative)
            assert type.arity() == 2 : 
                "expected arity 2 for array length relation but actual arity is " + var.arity();
            assert type.projectType(1) instanceof IntegerDomain : 
                "expected the 2nd col to be IntegerDomain but is " + type.projectType(1).getClass();
            return forgeBounds.extent(type.projectType(0)).product(nonnegs());
        } else {
            ForgeConstant fc = forgeBounds.extent(var.type());  
            if (forgeScene.isOneField(var)) {
                for (forge.solve.ForgeConstant.Tuple t : lower.tuples()) {
                    ForgeAtom src = t.atoms().get(0);
                    fc = fc.difference(src.product(src.join(fc))).union(t);
                }
            }
            return fc;
        }
    }
    
    protected int findMaxArrayLength(GlobalVariable var) {
        String lenVarName = var.name().replace("[].elts", "[].length");
        GlobalVariable len = forgeScene.global(lenVarName);
        assert len != null : "could not find the corresponding length variable for " + var.name();
        if (modifies.contains(len))
            return Integer.MAX_VALUE;
        ForgeConstant fc = fconv.forgeBounds().initialUpperBound(len);
        int max = 0;
        for (forge.solve.ForgeConstant.Tuple t : fc.tuples()) {
            IntegerAtom a = (IntegerAtom) t.atoms().get(1);
            if (a.value() > max)
                max = a.value();
        }
        return max - 1;
    }

    protected ForgeConstant.Unary nonnegs() {
        return nonnegs(fconv.forgeBounds().maxIntValue());
    }
    
    protected ForgeConstant.Unary nonnegs(int upperBound) {
        ForgeBounds forgeBounds = fconv.forgeBounds();
        ForgeConstant.Unary nonnegs = forgeBounds.empty();
        // create set of non-negative integers
        int n = Math.min(forgeBounds.maxIntValue(), upperBound);
        for (int i = 0; i <= n; i++) {
            nonnegs = nonnegs.union(forgeBounds.intAtom(i));
        }
        return nonnegs;
    }
    
    protected void boundLocalVar(LocalVariable var, TupleFactory f, Bounds b) {
        if (var == null)
            return;
        ForgeConstant lower = fconv.forgeBounds().initialLowerBound(var);
        ForgeConstant upper = fconv.forgeBounds().initialUpperBound(var);
        TupleSet lowerBound = conv2tuples(lower, f);
        TupleSet upperBound = conv2tuples(upper, f);
        Relation rel = (Relation) var2rel.get(relName(var));
        b.bound(rel, lowerBound, upperBound);
    }

    protected void addRelForVar(ForgeVariable var, String name) {
        Relation rel = Relation.nary(name, var.arity());
        var2rel.put(name, rel);
    }
    
    protected String relName(ForgeVariable var) {
        return var.name();
//        if (var.arity() == 1 || var.name().contains("[]"))
//            return var.name();
//        return var.type().domain().toString() + "_" + var.name();
    }

    protected TupleSet conv2tuples(ForgeConstant fc, TupleFactory f) {
        List<kodkod.instance.Tuple> kkTuples = new LinkedList<kodkod.instance.Tuple>();
        l: for (ForgeConstant.Tuple t : fc.tuples()) {
            Object[] atoms = new Object[t.arity()];
            int idx = 0;
            for (ForgeAtom a : t.atoms()) {
                Object atom = convAtom(a);
                if (atom == null)
                    continue l;
                atoms[idx++] = atom;
            }
            kkTuples.add(f.tuple(atoms));
        }
        // kodkod won't create TupleSet with zero tuples
        if (kkTuples.isEmpty())
            return f.noneOf(fc.arity());
        else
            return f.setOf(kkTuples);
    }

    protected Object convAtom(ForgeAtom a) {
        if (a instanceof IntegerAtom) {
            int val = ((IntegerAtom) a).value();
            if (!ints.contains(val))
                return null;
            return val;
        }
        return a.name();
    }
    
    protected TupleSet conv2tuples(ObjTupleSet fc, TupleFactory f) {
        List<kodkod.instance.Tuple> kkTuples = new LinkedList<kodkod.instance.Tuple>();
        for (ObjTuple t : fc.tuples()) {
            Object[] atoms = new Object[t.arity()];
            int idx = 0;
            for (Object a : t.atoms()) {
                ForgeLiteral lit = forgeScene.forgeLitForObj(a);
                assert lit != null;
                Object atom = lit.name();
                if (lit instanceof IntegerLiteral)
                    atom = ((IntegerLiteral)lit).value();
                atoms[idx++] = atom;
            }
            kkTuples.add(f.tuple(atoms));
        }
        // kodkod won't create TupleSet with zero tuples
        if (kkTuples.isEmpty())
            return f.noneOf(fc.arity());
        else
            return f.setOf(kkTuples);
    }

}
/*! @} */
