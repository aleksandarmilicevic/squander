/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import static edu.mit.csail.sdg.squander.parser.JFSLParser.DECL_NONE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.DECL_SEQ;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.FRAME_ALL;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.MOD_DISJ;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_AND;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_BIT_AND_OR_INTERSECTION;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_BIT_NOT_OR_TRANSPOSE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_BIT_OR;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_BIT_XOR;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_CLOSURE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_DIFFERENCE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_DIVIDE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_EQ;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_EQUIV;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_GEQ;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_GT;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_IMPLIES;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_INTERSECTION;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_LEQ;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_LT;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_MINUS;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_MINUS_OR_DIFFERENCE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_MOD;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_NEQ;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_NEQUIV;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_NOT;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_NSET_SUBSET;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_OR;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_PLUS;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_PLUS_OR_UNION;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_RANGE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_RELATIONAL_COMPOSE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_RELATIONAL_OVERRIDE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_ALL;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_COMPREHENSION;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_EXISTS;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_LONE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_NO;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_NUM;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_ONE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_SOME;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_SUBSET;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_SUM;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SHL;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SHR;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_TIMES;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_TRANSPOSE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_UNION;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_USHR;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_XOR;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.engine.ForgeConverter;
import edu.mit.csail.sdg.squander.engine.SquanderEval2;
import edu.mit.csail.sdg.squander.parser.JFSLParser;
import edu.mit.csail.sdg.squander.parser.JFSLParser.Decision;
import edu.mit.csail.sdg.squander.parser.JFSLParser.Node;
import forge.program.BinaryExpression;
import forge.program.ExpressionVisitor;
import forge.program.ForgeDomain;
import forge.program.ForgeExpression;
import forge.program.ForgeProgram;
import forge.program.ForgeType;
import forge.program.ForgeType.Tuple;
import forge.program.ForgeVariable;
import forge.program.GlobalVariable;
import forge.program.IntegerLiteral;
import forge.program.LocalDecls;
import forge.program.LocalVariable;
import forge.program.UnaryExpression;
import forge.program.UnaryExpression.Op;
import forge.transform.ExpressionReplacer;

class RangeExpression implements ForgeExpression {
    public final ForgeExpression lhs, rhs;

    public RangeExpression(ForgeExpression lhs, ForgeExpression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public ForgeType type()                             { return lhs.type(); }

    ////////////////////////////////////////////////////
    public <T> T accept(ExpressionVisitor<T> visitor)   { throw new UnsupportedOperationException(); }
    public ForgeExpression and(ForgeExpression expr)    { throw new UnsupportedOperationException(); }
    public ForgeExpression apply(Op op)                 { throw new UnsupportedOperationException(); }
    public int arity()                                  { throw new UnsupportedOperationException(); }
    public ForgeExpression bitAnd(ForgeExpression expr) { throw new UnsupportedOperationException(); }
    public ForgeExpression bitNot()                     { throw new UnsupportedOperationException(); }
    public ForgeExpression bitOr(ForgeExpression expr)  { throw new UnsupportedOperationException(); }
    public ForgeExpression bitXor(ForgeExpression expr) { throw new UnsupportedOperationException(); }
    public ForgeExpression bool()                       { throw new UnsupportedOperationException(); }
    public ForgeExpression closure()                    { throw new UnsupportedOperationException(); }
    public ForgeExpression compose(forge.program.BinaryExpression.Op op, ForgeExpression expr) { throw new UnsupportedOperationException(); }
    public ForgeExpression comprehension(LocalDecls decls)  { throw new UnsupportedOperationException(); }
    public ForgeExpression difference(ForgeExpression expr) { throw new UnsupportedOperationException(); }
    public ForgeExpression divide(ForgeExpression expr) { throw new UnsupportedOperationException(); }
    public ForgeExpression domain()                     { throw new UnsupportedOperationException(); }
    public ForgeExpression eq(ForgeExpression expr)     { throw new UnsupportedOperationException(); }
    public ForgeExpression forAll(LocalDecls decls)     { throw new UnsupportedOperationException(); }
    public ForgeExpression forSome(LocalDecls decls)    { throw new UnsupportedOperationException(); }
    public ForgeExpression gt(ForgeExpression expr)     { throw new UnsupportedOperationException(); }
    public ForgeExpression gte(ForgeExpression expr)    { throw new UnsupportedOperationException(); }
    public ForgeExpression iden()                       { throw new UnsupportedOperationException(); }
    public ForgeExpression iff(ForgeExpression expr)    { throw new UnsupportedOperationException(); }
    public ForgeExpression implies(ForgeExpression expr){ throw new UnsupportedOperationException(); }
    public ForgeExpression in(ForgeExpression expr)     { throw new UnsupportedOperationException(); }
    public ForgeExpression intersection(ForgeExpression expr) { throw new UnsupportedOperationException(); }
    public boolean isUnary()                            { throw new UnsupportedOperationException(); }
    public ForgeExpression join(ForgeExpression expr)   { throw new UnsupportedOperationException(); }
    public ForgeExpression lone()                       { throw new UnsupportedOperationException(); }
    public ForgeExpression lt(ForgeExpression expr)     { throw new UnsupportedOperationException(); }
    public ForgeExpression lte(ForgeExpression expr)    { throw new UnsupportedOperationException(); }
    public ForgeExpression minus(ForgeExpression expr)  { throw new UnsupportedOperationException(); }
    public ForgeExpression modulo(ForgeExpression expr) { throw new UnsupportedOperationException(); }
    public ForgeExpression neg()                        { throw new UnsupportedOperationException(); }
    public ForgeExpression no()                         { throw new UnsupportedOperationException(); }
    public ForgeExpression not()                        { throw new UnsupportedOperationException(); }
    public ForgeExpression one()                        { throw new UnsupportedOperationException(); }
    public ForgeExpression or(ForgeExpression expr)     { throw new UnsupportedOperationException(); }
    public ForgeExpression override(ForgeExpression expr) { throw new UnsupportedOperationException(); }
    public ForgeExpression plus(ForgeExpression expr)   { throw new UnsupportedOperationException(); }
    public ForgeExpression product(ForgeExpression expr){ throw new UnsupportedOperationException(); }
    public ForgeExpression projection(int... columns)   { throw new UnsupportedOperationException(); }
    public ForgeExpression quantify(forge.program.QuantifyExpression.Op arg0, LocalDecls arg1) { throw new UnsupportedOperationException(); }
    public ForgeExpression range()                      { throw new UnsupportedOperationException(); }
    public ForgeExpression shiftLeft(ForgeExpression expr) { throw new UnsupportedOperationException(); }
    public ForgeExpression shiftRight(ForgeExpression expr){ throw new UnsupportedOperationException(); }
    public ForgeExpression size()                       { throw new UnsupportedOperationException(); }
    public ForgeExpression some()                       { throw new UnsupportedOperationException(); }
    public ForgeExpression sum()                        { throw new UnsupportedOperationException(); }
    public ForgeExpression summation(LocalDecls decls)  { throw new UnsupportedOperationException(); }
    public ForgeExpression thenElse(ForgeExpression thenExpr, ForgeExpression elseExpr) { throw new UnsupportedOperationException(); }
    public ForgeExpression times(ForgeExpression expr)  { throw new UnsupportedOperationException(); }
    public ForgeExpression union(ForgeExpression expr)  { throw new UnsupportedOperationException(); }
    public ForgeExpression unsignedShiftRight(ForgeExpression expr) { throw new UnsupportedOperationException(); }
    public ForgeExpression xor(ForgeExpression expr)    { throw new UnsupportedOperationException(); }
    public ForgeProgram program()                       { throw new UnsupportedOperationException(); }
}

public class Tr extends Visitor<ForgeExpression, ForgeEnv> {
    
    public static final Map<LocalDecls, String> absolutelyTerribleHack = new IdentityHashMap<LocalDecls, String>();
    public static ForgeConverter teribleHack2;

    public Tr() { }
    
    @Override
    protected ForgeExpression visitBinary(ForgeEnv env, Node tree, int op, Node leftTree, Node rightTree) {
        final ForgeExpression result;
        @SuppressWarnings("unused") 
        boolean singleton = true;

        final ForgeExpression left = visit(env, leftTree);
        final ForgeExpression right = visit(env, rightTree);
        
        // apply operator
        switch (op) {
        case OP_SHL: result = left.shiftLeft(right); env.ensureAllInts(); break;
        case OP_SHR: result = left.shiftRight(right); env.ensureAllInts(); break;
        case OP_USHR: result = left.unsignedShiftRight(right); env.ensureAllInts(); break;
        case OP_PLUS: result = left.plus(right); env.ensureAllInts(); break;
        case OP_MINUS: result = left.minus(right); env.ensureAllInts(); break;
        case OP_TIMES: result = left.times(right); env.ensureAllInts(); break;
        case OP_DIVIDE: result = left.divide(right); env.ensureAllInts(); break;
        case OP_MOD: result = left.modulo(right); env.ensureAllInts(); break;
        case OP_BIT_XOR: result = left.bitXor(right); env.ensureAllInts(); break;
        case OP_BIT_OR: result = left.bitOr(right); env.ensureAllInts(); break;
        case OP_RANGE: result = new RangeExpression(left, right); break;
            
        case OP_LT: result = left.lt(right); break;
        case OP_GT: result = left.gt(right); break;
        case OP_LEQ: result = left.lte(right); break;
        case OP_GEQ: result = left.gte(right); break;

        case OP_OR: result = left.or(right); break;
        case OP_AND: result = left.and(right); break;
        case OP_XOR: result = left.xor(right); break;
        case OP_EQUIV: result = left.iff(right); break;
        case OP_NEQUIV: result = (left.iff(right)).not(); break;
        case OP_IMPLIES: result = left.implies(right); break;

        case OP_EQ:
            if (isBinEqNeq(leftTree, op)) {
                ForgeExpression leftRight = visit(env, leftTree.getChild(2));
                result = left.and(leftRight.eq(right));
                break;
            }
            result = left.eq(right); 
            break;
        case OP_NEQ:
            if (isBinEqNeq(leftTree, op)) {
                ForgeExpression leftRight = visit(env, leftTree.getChild(2));
                result = left.and(leftRight.eq(right).not());
                break;
            }
            result = (left.eq(right)).not(); 
            break;
        case OP_SET_SUBSET: result = left.in(right); break;
        case OP_NSET_SUBSET: result = (left.in(right)).not(); break;

        case OP_RELATIONAL_OVERRIDE:
            result = left.override(right);
            singleton = false;
            break;          
        case OP_RELATIONAL_COMPOSE:
            result = left.product(right);
            singleton = false;
            break;

        case OP_INTERSECTION:
            result = left.intersection(right);
            singleton = false;
            break;
        case OP_UNION:
            result = left.union(right);
            singleton = false;
            break;
        case OP_DIFFERENCE:
            result = left.difference(right);
            singleton = false;
            break;

        case OP_PLUS_OR_UNION:
            if (tree.flag())  {
                env.ensureAllInts();
                result = left.plus(right);
            } else {
                result = left.union(right);
                singleton = false;
            }           
            break;
        case OP_MINUS_OR_DIFFERENCE:
            if (tree.flag()) {
                env.ensureAllInts();
                result = left.minus(right);
            } else {
                result = left.difference(right);
                singleton = false;
            }
            break;
        case OP_BIT_AND_OR_INTERSECTION:
            if (tree.flag()) 
                result = left.bitAnd(right);
            else {
                result = left.intersection(right);
                singleton = false;
            }
            break;
        default:
            assert false;
        result = null;
        }

        return result;
    }

    private boolean isBinEqNeq(Node node, int op) {
        if (node.getToken().getType() != JFSLParser.BINARY)
            return false;
        return cast(node.getChild(0)).getToken().getType() == op;
    }

    @Override
    protected ForgeExpression visitConditional(ForgeEnv env, Node condTree, Node leftTree, Node rightTree) {
        ForgeExpression cond = visit(env, condTree);
        ForgeExpression left = visit(env, leftTree);
        ForgeExpression right = visit(env, rightTree);
        return cond.thenElse(left, right);
    }

    @Override
    protected ForgeExpression visitDecimal(ForgeEnv env, int i) {
        return env.intExpr(i);
    }
    
    @Override
    protected ForgeExpression visitString(ForgeEnv env, String text) {
        return env.stringExpr(text);
    }

    @Override
    protected ForgeExpression visitTrue(ForgeEnv env) {
        return env.trueExpr();  
    }

    @Override
    protected ForgeExpression visitFalse(ForgeEnv env) {
        return env.falseExpr();
    }

    @Override
    protected ForgeExpression visitNull(ForgeEnv env) {
        return env.nullType();
    }

    @Override
    protected ForgeExpression visitReturn(ForgeEnv env) {
        return env.returnVar();
    }
    
    @Override
    protected ForgeExpression visitThrow(ForgeEnv env) {
        return env.throwVar();
    }

    @Override
    protected ForgeExpression visitLambda(ForgeEnv env) {
        return env.findLocal(LAMBDA_VAR_NAME);
    }

    @Override
    protected ForgeExpression visitThis(ForgeEnv env) { 
        return env.thisVar();
    }

    @Override
    protected ForgeExpression visitSuper(ForgeEnv env) {
        return null;  
    }

    @Override
    protected ForgeExpression visitArgument(ForgeEnv env, int i) {
        return env.arg(i);
    }

    @Override
    protected ForgeExpression visitOld(ForgeEnv env, Node sub) {
        return visit(env.setPreStateMode(), sub);
    }
    
    @Override
    protected ForgeExpression visitQuantification(ForgeEnv env, int op, int mod, List<String> names, List<String> mults, List<Node> sets, Node expr) {        
        switch (op) {
        case OP_SET_ALL:
        case OP_SET_EXISTS:
        case OP_SET_SOME:
            ForgeExpression ret = visitQuantificationRec(env, 0, op, mod, names, mults, sets, expr);           
            // optimization
            return applyLoneTransformation(env, mod, names, sets, expr, ret);
        default:    
            return visitQuantificationMutliDecl(env, op, mod, names, mults, sets, expr);
        }
    }
    
    protected ForgeExpression visitQuantificationRec(ForgeEnv env, int idx, int op, int mod, List<String> names, List<String> mults, List<Node> sets, Node expr) {
        // base case
        if (idx >= names.size()) {
            return visit(env, expr);
        }
        
        // translate domains
        final ForgeExpression set = visit(env, sets.get(idx));
        final LocalVariable var = env.newLocalVar(names.get(idx), set.type());
        absolutelyTerribleHack.put(var, mults.get(idx));
        
        ForgeExpression sub = visitQuantificationRec(env.addLocal(var), idx+1, op, mod, names, mults, sets, expr);
        
        if (var.arity() > 1) {
            try {
                SquanderEval2 eval = new SquanderEval2();
                ForgeExpression ret = env.trueExpr();
                ObjTupleSet res = eval.eval(set, teribleHack2);
                for (ObjTuple t : res.tuples()) {
                    final ForgeExpression varVal = teribleHack2.conv2fe(t);
                    ForgeExpression x = sub.accept(new ExpressionReplacer() {
                        @Override
                        protected ForgeExpression visit(ForgeVariable expr) {
                            ForgeExpression result = expr.equals(var) ? varVal : expr; 
                            super.putCache(expr, result);
                            return result;
                        }
                        
                    });
                    if (op == OP_SET_ALL)
                        ret = ret.and(x);
                    else
                        ret = ret.or(x);
                }
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }       

        // condition
        boolean implies = false;
        ForgeExpression condition = env.trueExpr();
        if (!(set instanceof ForgeDomain)) {
            implies = true; 
            condition = var.in(set);
        }
        
        // quantification
        switch (op) {
        case OP_SET_ALL:
            if (implies)
                return condition.implies(sub).forAll(var);
            else
                return sub.forAll(var);
        case OP_SET_SOME:
        case OP_SET_EXISTS:
            if (implies)
                return condition.and(sub).forSome(var);
            else
                return sub.forSome(var);
        default:
            return null;
        }
    }

    protected ForgeExpression visitQuantificationMutliDecl(ForgeEnv env, int op, int mod, List<String> names, List<String> mults, List<Node> sets, Node expr) {        
        // translate domains
        LocalDecls decl = env.emptyDecls();
        List<LocalVariable> vars = new ArrayList<LocalVariable>();
        List<ForgeExpression> domains = new ArrayList<ForgeExpression>();       
        for (int i = 0; i < names.size(); i++) {
            ForgeExpression set = visit(env, sets.get(i));                          
            LocalVariable var = env.newLocalVar(names.get(i), set.type());
            env = env.addLocal(var);

            vars.add(var);
            domains.add(set);
            decl = decl.and(var);
        }
        
        absolutelyTerribleHack.put(decl, pickMult(mults));

        // ForgeExpression
        ForgeExpression sub = visit(env, expr);

        // condition
        ForgeExpression condition = env.trueExpr();     
        boolean implies = false;
        for (int i = 0; i < names.size(); i++) {
            ForgeExpression dom = domains.get(i);
            if (!(dom instanceof ForgeDomain)) {
                implies = true;
                condition = condition.and(vars.get(i).in(dom));
            }
        }
        
        ForgeExpression result;

        // quantification
        switch (op) {
        case OP_SET_ALL:
            if (implies)
                return condition.implies(sub).forAll(decl);
            else
                return sub.forAll(decl);
        case OP_SET_SOME:
        case OP_SET_EXISTS:
            if (implies)
                result = condition.and(sub).forSome(decl);
            else
                result = sub.forSome(decl);
            break;
        case OP_SET_COMPREHENSION:
            if (implies) 
                result = condition.and(sub).comprehension(decl);
            else
                result = sub.comprehension(decl);
            break;
        case OP_SET_ONE:
            if (implies)
                result = condition.and(sub).comprehension(decl).one();
            else
                result = sub.comprehension(decl).one();
            break;
        case OP_SET_LONE:
            if (implies)
                result = condition.and(sub).comprehension(decl).lone();
            else 
                result = sub.comprehension(decl).lone();
            break;
        case OP_SET_NO:
            if (implies) 
                result = condition.and(sub).comprehension(decl).no();
            else
                result = sub.comprehension(decl).no();
            break;
        case OP_SET_NUM:
            if (implies)
                result = condition.and(sub).comprehension(decl).size();
            else
                result = sub.comprehension(decl).size();
            break;          
        case OP_SET_SUM:
            result = condition.thenElse(sub, env.intExpr(0)).summation(decl);
            break;
        default:
            result = null;
        }

        return result;
    }

    private String pickMult(List<String> mults) {
        for (String s: mults) {
            if ("DECL_SET".equals(s))
                return "DECL_SET";
        }
        return "DECL_NONE";
    }

    @Override
    protected ForgeExpression visitComprehensionEnum(ForgeEnv env, List<Node> exprs) {
        List<ForgeExpression> fexprs = new ArrayList<ForgeExpression>(exprs.size());
        ForgeType type = null;
        for (Node n : exprs) {
            ForgeExpression fexpr = visit(env, n);
            fexprs.add(fexpr);
            if (type == null)
                type = fexpr.type();
            else
                type = type.union(fexpr.type());
        }
        LocalVariable var = env.newLocalVar("__x__", type);
        ForgeExpression comprExpr = null;
        for (ForgeExpression fe : fexprs) {
            ForgeExpression e; 
            if (fe instanceof RangeExpression)
                e = var.gte(((RangeExpression) fe).lhs).and(var.lte(((RangeExpression) fe).rhs));
            else 
                e = var.eq(fe);
            if (comprExpr == null)
                comprExpr = e; 
            else 
                comprExpr = comprExpr.or(e);
        }
        return comprExpr.comprehension(env.emptyDecls().and(var));
    }

    @Override
    protected ForgeExpression visitUnary(ForgeEnv env, Node tree, int op, Node expr) {           
        final ForgeExpression sub = visit(env, expr);

        final ForgeExpression result;
        @SuppressWarnings("unused") 
        boolean singleton = true;

        switch (op) {
        case OP_PLUS: result = sub; break;
        case OP_MINUS: 
            result = sub.neg(); 
            if (sub instanceof IntegerLiteral) 
                env.ensureInt(-((IntegerLiteral) sub).value());
            break;
        case OP_NOT: result = sub.not(); break;

        case OP_SET_SOME: result = sub.some(); break;
        case OP_SET_NO: result = sub.no(); break;
        case OP_SET_SUM: result = sub.sum(); break;
        case OP_SET_ONE: result = sub.one(); break;
        case OP_SET_LONE: result = sub.lone(); break;
        case OP_SET_NUM: result = sub.size(); break;

        case OP_CLOSURE: result = sub.closure(); break;                     
        case OP_TRANSPOSE: result = sub.projection(reverse(sub.arity())); break;
        case OP_BIT_NOT_OR_TRANSPOSE:
            if (tree.flag()) 
                result = sub.bitNot();
            else
                result = sub.projection(reverse(sub.arity())); 
            break;
        default:
            assert false;
        result = null;
        }

        return result;
    }

    private int[] reverse(int i) {
        int[] columns = new int[i];
        for (int j = 0; j < i; j++)
            columns[j] = i - 1 - j;
        return columns;
    }

    @Override
    protected ForgeExpression visitName(ForgeEnv env, Node ident) {
        if (ident == null) {
            throw new NullPointerException("ident is null " + env);
        }
        if (ident.decision() == null) {
            throw new NullPointerException("ident.decision() == null for " + ident.toStringTree());
        }
        String name = asText(ident);
        switch (ident.decision()) {
        case LOCAL:
            LocalVariable local = env.findLocal(name);
            assert local != null : "Local variable " + name + " not found";
            return local;
        case GLOBAL:
            GlobalVariable global = env.ensureGlobal(ident.field);
            assert global != null : "Global variable " + name + " not found";
            return env.globalVar(global);
        case TYPE:
            assert ident.jtype != null;
            assert ident.jtype.arity() == 1;
            ForgeType.Unary domain = env.typeForCls(ident.jtype.domain(), false); // TODO: include null or not???
            assert domain != null : "domain " + name + " not found";
            return domain;
        case CONST: 
            GlobalVariable globalConst = env.ensureConst(name);
            assert globalConst != null : "Global constant " + name + " not found"; 
            return globalConst;  
        case ENUM:
            ForgeExpression expr = env.enumConst(ident.enumConst);
            assert expr != null : "Literal for enum constant '" + ident.enumConst.name() + "' not created";
            return expr;
        default:
            return null;
        }       
    }

    @Override
    protected ForgeExpression visitAmbiguous(ForgeEnv env, List<Node> idents) {
        if (idents.get(0).decision() == Decision.FRAGMENT) 
            return visitAmbiguous(env, idents.subList(1, idents.size()));
        
        ForgeExpression primary = visitName(env, idents.get(0));
        // should we join it?
        for (int i = 1; i < idents.size(); i++)
            if (idents.get(i).decision() != Decision.FRAGMENT) {
                ForgeExpression selector = visitAmbiguous(env, idents.subList(i, idents.size()));   
                return primary.join(selector);
            }
        return primary;
    }   

    @Override
    protected ForgeExpression visitBracket(ForgeEnv env, ForgeExpression primary, Node tree) {
        ForgeExpression expr = visit(env, child(tree));
        // flag is true, elems is implicit
        if (tree.flag()) {
            ForgeExpression elems = null;
            for (Tuple t : primary.type().tupleTypes()) {
                JType.Unary cls = env.classForDomain(t.domain());
                if (cls != null) { 
                    ForgeExpression el = env.bracketElems(cls);
                    if (elems == null)
                        elems = el;
                    else   
                        elems = elems.union(el);
                }
            }
            assert elems != null : "elts relation not found for " + primary.type();
            return expr.join(primary.join(elems));
        } else            
            return expr.join(primary);                      
    }

    @Override
    protected ForgeExpression visitProjection(ForgeEnv env, ForgeExpression primary, int[] indexes) {
        return primary.projection(indexes);
    }

    @Override
    protected ForgeExpression visitJoin(ForgeEnv env, ForgeExpression primary, Node tree) {
        return primary.join(visit(env, child(tree)));
    }
    
    @Override
    protected ForgeExpression visitJoinReflexive(ForgeEnv env, ForgeExpression primary, Node tree) {
        return primary.union(primary.join(visit(env, child(tree)).closure()));      
    }
    
    @Override
    protected ForgeExpression visitMethodCall(ForgeEnv env, ForgeExpression receiver, String id, Node arguments) {
        throw new RuntimeException("method calls not supported");
    }   
    
    /** Returns field declaration set expression for type information */
    @Override
    protected ForgeExpression visitFieldDeclaration(ForgeEnv env, Node ident, int op, Node set, Node frame, Node constraint) {
        final ForgeExpression domain = visit(env, set);
        return (op == DECL_SEQ ? env.integerType().product(domain) : domain);       
    }

    @Override
    protected ForgeExpression visitCastExpression(ForgeEnv env, Node type, Node sub) { 
        return visit(env, sub).intersection(visit(env, type));
    }

    @Override
    protected ForgeExpression visitFieldRelation(ForgeEnv env, Node type, Node ident) {
        GlobalVariable global = env.ensureGlobal(ident.field);
        assert global != null : "global variable " + asText(ident) + " not found";
        return env.globalVar(global);
    }
    
    @Override
    protected ForgeExpression visitArrayType(ForgeEnv env, Node base) {
        ForgeType.Unary type = env.ensureDomain(base.jtype.domain().mkArray());
        assert type != null : "type " + asText(base) + " not found";
        return type;
    }

    @Override
    protected ForgeExpression visitBooleanType(ForgeEnv env) {
        return env.booleanType();
    }

    @Override
    protected ForgeExpression visitIntegralType(ForgeEnv env) {
        return env.integerType();
    }

    @Override
    protected ForgeExpression visitRefType(ForgeEnv env, Node source, List<Node> idents) {
        ForgeType.Unary type = env.ensureDomain(source.jtype.domain());
        assert type != null : "type " + asText(source) + " not found";
        return type;
    }

    @Override
    protected ForgeExpression visitFrame(ForgeEnv env, List<ForgeExpression> locations, 
            List<Node> fields, List<ForgeExpression> selectors, 
            List<ForgeExpression> uppers, List<Node> filters) {
        // don't care about frame for spec fields
        return env.trueExpr();
    }
    
    // =======================================================================
    // --------------------- class SpecFieldTranslator -----------------------
    // =======================================================================
    
    /** 
     * Returns field abstraction function 
     */
    public static class SpecFieldTranslator extends Tr {
        
        public SpecFieldTranslator() { }
    
        @Override
        protected ForgeExpression visitFieldDeclaration(ForgeEnv env, Node ident, int op, Node set, Node frame, Node constraint) {
            return constraint !=  null ? visit(env, constraint) : env.trueExpr();                       
        }
    }
    
    // =======================================================================
    // ------------------- class SpecFieldBoundTranslator --------------------
    // =======================================================================
 
    /** 
     * Returns field constraint expression assuming the constraint is on "this" 
     */
    public static class SpecFieldBoundTranslator extends Tr {
    
        private final JField field; 
        
        public SpecFieldBoundTranslator(JField field) {
            this.field = field;
        }

        @Override
        protected ForgeExpression visitFieldDeclaration(ForgeEnv env, Node ident, int op, Node set, Node frame, Node constraint) {
            LocalVariable x = env.thisVar();
            GlobalVariable var = env.ensureGlobal(field);
            ForgeExpression upper = visit(env, set);                      
            ForgeExpression upperDeclared = x.join(var).in(op == DECL_SEQ ? env.integerType().product(upper) : upper);
            ForgeExpression domain = upper.equals(upper.type()) ? env.trueExpr() : upperDeclared;
            
            // translate multiplicity
            ForgeExpression multiplicity;
            switch (op) {
            case OP_SET_ONE:
                multiplicity = x.join(var).one();
                break;
            case OP_SET_SOME:
                multiplicity = x.join(var).some();
                break;
            case OP_SET_LONE:
                multiplicity = x.join(var).lone();
                break;
            case DECL_SEQ:
                // all i : int | i < 0 ? no i.(x.var) : lone i.(x.var)
                LocalVariable i = env.newLocalVar("i", env.integerType());                                
                ForgeExpression positive = i.lt(env.intExpr(0)).thenElse(i.join(x.join(var)).no(), i.join(x.join(var)).lone()).forAll(i);
    
                // all j : int | all k : int | j < k && j >= 0 && no x.var[j] => no x.var[k]
                LocalVariable j = env.newLocalVar("j", env.integerType());
                LocalVariable k = env.newLocalVar("k", env.integerType());
                ForgeExpression continuous = (j.lt(k).and(j.gte(env.intExpr(0))).and(j.join(x.join(var)).no())).implies(k.join(x.join(var)).no()).forAll(j).forAll(k);
                multiplicity = positive.and(continuous);
                break;              
            case DECL_NONE:
                multiplicity = var.arity() == 2 ? x.join(var).one() : env.trueExpr();
                break;
            default:
                multiplicity = env.trueExpr();
            }
    
            return multiplicity.and(domain);
        }       
    }
    
    // =======================================================================
    // ----------------------- class FrameConstructor ------------------------
    // =======================================================================
    
    /** Constructs frame axiom */
    static final class FrameConstructor extends Tr {
        private final Frame frame;      
        private final ForgeScene forgeScene;
        
        public FrameConstructor(Frame frame, ForgeScene forgeScene) {
            this.frame = frame;
            this.forgeScene = forgeScene;
        }               
        
        @Override
        protected ForgeExpression visitFrame(ForgeEnv env, List<ForgeExpression> locations, 
                List<Node> fields, List<ForgeExpression> selectors, 
                List<ForgeExpression> uppers, List<Node> filters) {
            int size = locations.size();

            for (int i = 0; i < size; i++) {
                Node node = fields.get(i);
                ForgeExpression loc = locations.get(i);
                ForgeExpression sub = selectors.get(i);
                //ForgeExpression lower = lowers.get(i);
                ForgeExpression upper = uppers.get(i);
                Node filter = filters.get(i);
                if (node.getType() == FRAME_ALL) {
//                    Class<?> type = node.jtype.range();
//                    for (JField f : javaScene.getClassSpec(type).usedFields()) {
//                        frame.add(forgeScene.global(f), locations.get(i));
//                    }
                    throw new RuntimeException("FRAME_ALL not supported");
                } else {
                    switch (node.decision()) {
                    case GLOBAL:
                        GlobalVariable var = forgeScene.global(node.field);
                        if (var == null) continue; // var not used, so skip it
                        ForgeExpression filterExpr = null;
                        if (filter != null) {
                            LocalVariable lambda = env.newLocalVar(LAMBDA_VAR_NAME, var.type());
                            filterExpr = visit(env.addLocal(lambda), child(filter));
                        }
                        frame.add(var, loc, sub, upper, filterExpr);
                        break;
                    default: 
                        throw new RuntimeException("Wrong node kind. Expected: GLOBAL, actual: " + node.decision());
                    } 
                }
            }

            return env.trueExpr();
        }
        
        @Override
        protected ForgeExpression visitFieldDeclaration(ForgeEnv env, Node ident, int op, Node set, Node frame, Node constraint) {
            if (frame != null) 
                visit(env, frame);
            return env.trueExpr();
        }
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    
    // ============================================================================
    //   Optimization. Transforms
    //
    //     (all disj x, y: T | x.f != y.f) 
    //   to
    //     (all v: F | lone f.v)
    //
    //   and
    //
    //     (all x, y: T | x.f = y.f)
    //   to
    //     (lone T.f)
    // ============================================================================
    
    private ForgeExpression applyLoneTransformation(ForgeEnv env, int mod, List<String> names, List<Node> sets,
            Node expr, ForgeExpression ret) {
        if (mod == MOD_DISJ && names.size() == 2) {
            for (int i = 0; i < names.size(); i++) {
                ForgeExpression set = visit(env, sets.get(i));                          
                LocalVariable var = env.newLocalVar(names.get(i), set.type());
                env = env.addLocal(var);
            }
            ForgeExpression sub = visit(env, expr);
            ForgeExpression x = visitSubExpr(env, mod, new HashSet<String>(names), sub);
            if (x != null)
                ret = ret.and(x);
        }
        return ret;
    }
    
    private ForgeExpression visitSubExpr(ForgeEnv env, int mod, Set<String> disjVarNames, ForgeExpression expr) {
        if (expr instanceof BinaryExpression) {
            BinaryExpression bexpr = (BinaryExpression) expr;
            switch (bexpr.op()) {
            case AND: 
                ForgeExpression l = visitSubExpr(env, mod, disjVarNames, bexpr.left());
                ForgeExpression r = visitSubExpr(env, mod, disjVarNames, bexpr.right());
                return and(l, r);
            case EQUALS: 
                GlobalVariable field = checkEq(disjVarNames, bexpr.left(), bexpr.right());
                if (field == null)
                    return null;
                return field.projection(1).lone();
            default:
                return null;
            }
        } else if (expr instanceof UnaryExpression) {
            UnaryExpression uexpr = (UnaryExpression) expr;
            switch (uexpr.op()) {
            case NOT:
                if (mod != MOD_DISJ)
                    return null;
                try {
                    BinaryExpression bexpr = (BinaryExpression) uexpr.sub();
                    if (bexpr.op() != forge.program.BinaryExpression.Op.EQUALS)
                        return null;
                    GlobalVariable field = checkEq(disjVarNames, bexpr.left(), bexpr.right());
                    if (field == null)
                        return null;
                    LocalVariable v = env.newLocalVar("__v__", field.range().type());
                    return field.join(v).lone().forAll(env.emptyDecls().and(v));
                } catch (ClassCastException e) {
                    return null;
                }
            default: 
                return null;
            }
        }
        return null;
    }

    private GlobalVariable checkEq(Set<String> disjVarNames, ForgeExpression left, ForgeExpression right) {
        try {
            BinaryExpression l = (BinaryExpression) left;
            BinaryExpression r = (BinaryExpression) right;
            GlobalVariable leftField = checkJoin(disjVarNames, l);
            GlobalVariable rightField = checkJoin(disjVarNames, r);
            if (leftField == rightField)
                return leftField;
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private GlobalVariable checkJoin(Set<String> disjVarNames, BinaryExpression joinExpr) {
        if (joinExpr.op() != forge.program.BinaryExpression.Op.JOIN)
            return null;
        if (!(joinExpr.left() instanceof LocalVariable))
            return null;
        LocalVariable var = (LocalVariable) joinExpr.left();
        if (!disjVarNames.contains(var.name()))
            return null;
        if (joinExpr.right() instanceof GlobalVariable)
            return (GlobalVariable) joinExpr.right();
        else 
            return null;
    }

    private ForgeExpression and(ForgeExpression l, ForgeExpression r) {
        if (l != null)
            if (r != null)
                return l.and(r);
            else
                return l; 
        else if (r != null)
            return r; 
        else
            return null;
    }
    // ============================================================================
}
/*! @} */
