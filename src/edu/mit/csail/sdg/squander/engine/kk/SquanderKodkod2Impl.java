/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine.kk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.engine.Solution;
import kodkod.engine.config.Options;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;
import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.engine.ForgeConverter;
import edu.mit.csail.sdg.squander.engine.ISquander;
import edu.mit.csail.sdg.squander.engine.SquanderEval2;
import edu.mit.csail.sdg.squander.engine.ISquanderResult.IEvaluator;
import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.spec.Spec.SpecCase;
import forge.program.ForgeExpression;
import forge.program.ForgeLiteral;
import forge.program.ForgeType;
import forge.program.ForgeVariable;
import forge.program.GlobalVariable;
import forge.program.InstanceLiteral;
import forge.program.LocalVariable;
import forge.program.ForgeType.Unary;

/**
 * An alternative implementation of the {@link ISquander} interface that
 * uses a different translation to Kodkod.
 * 
 * @author Aleksandar Milicevic
 */
///TODO: doesn't work for complicated frame conditions
@Deprecated
public class SquanderKodkod2Impl extends SquanderKodkodImpl {
    
    static class FldRelElem {
        InstanceLiteral l; 
        GlobalVariable g; 
        Relation rel;
        Relation rel_pre;
        
        FldRelElem(InstanceLiteral l, GlobalVariable g, Relation rel, Relation rel_pre) {
            this.l = l;
            this.g = g;
            this.rel = rel;
            this.rel_pre = rel_pre;
        }
        
        Relation preRel()  { return rel_pre != null ? rel_pre : rel; }
        Relation postRel() { return rel; }
        boolean isMod()    { return rel_pre != null; }
        
    }
    
    // ==============================================================
    
    private HashMap<String, FldRelElem> fldRels;

    public SquanderKodkod2Impl() { }
    
    @Override
    protected Set<GlobalVariable> getModsForPostState(ForgeConverter fconv, SpecCase sc) {
        return sc.frame().modifiable();
    }

    @Override
    protected ForgeExpression getPreSpec(SpecCase cs) {
        this.cs = cs;
        return cs.pre().and(cs.spec().abstractConstraint());
    }

    @Override
    protected ForgeExpression getPostSpec(SpecCase cs, ForgeConverter fconv) {
        this.cs = cs;
        return cs.spec().abstractConstraint()
                .and(cs.spec().funcConstraint())
                .and(cs.post());
    }
    
    @Override
    protected Formula convertSpec(ForgeExpression spec) {
        Formula f = super.convertSpec(spec);
        f = f.and(wellformed());
        return f;
    }

    @Override
    protected IEvaluator getEval(Iterator<Solution> solutions, Options options) {
        return new KodkodEval(solutions, options);
    }

    @Override
    protected void init() {
        super.init();
        fldRels = new HashMap<String, FldRelElem>();
    }

    private Formula wellformed() {
        Formula f = Formula.TRUE;
        for (FldRelElem fre : fldRels.values()) {
            if (!modifies.contains(fre.g))
                continue;
            if (forgeScene.isSpecField(fre.g))
                continue;
            String varName = fre.g.name();
            if (varName.endsWith("[].elts"))
                f = f.and(arrElemsConstr(fre));
            else if (varName.endsWith("[].length"))
                f = f.and(arrLenConstr(fre));
            else
                f = f.and(one(fre.postRel())); //TODO
        }
        // "this", "return" and method parameters
        if (forgeScene.returnVar() != null)
            f = f.and(one(var2rel.get(relName(forgeScene.returnVar()))));
        return f;
    }

    private Formula one(Expression rel) {
        return rel.one(); // TODO 
    }

    private Formula arrLenConstr(FldRelElem fre) {
        Relation postRel = fre.postRel();
        return one(postRel).and(postRel.sum().gte(IntConstant.constant(0)));
    }

    private Formula arrElemsConstr(FldRelElem fre) {
        Relation postRel = fre.postRel();
        String lenVarName = postRel.name().replace("[].elts", "[].length");
        Relation lenRel = fldRels.get(lenVarName).postRel();
        Variable idxVar = Variable.unary("i");
        IntExpression idx = idxVar.sum();
        IntExpression len = lenRel.sum();
        Formula cond = idx.lt(len).and(idx.gte(IntConstant.constant(0)));
        Formula thenCond = one(idxVar.join(postRel));
        Formula elseCond = idxVar.join(postRel).no();
        return cond.implies(thenCond).and(cond.not().implies(elseCond)).forAll(idxVar.oneOf(intsExpr()));
    }

    private Expression intsExpr() {
        return Expression.INTS;
    }
  
    @Override
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
            Expression domType = type2expr.get(lit.type());
            if (domType == null)
                domType = rel;
            else 
                domType = domType.union(rel);
            type2expr.put(lit.type(), domType);
        }
        this.trueRelation = lit2rel.get(program.trueLiteral().name());
        this.falseRelation = lit2rel.get(program.falseLiteral().name());
        
        // create relations for global variables
        for (GlobalVariable g : program.globalVariables()) {
            if (forgeScene.isConst(g))
                continue;
            ForgeExpression expr = cs.frame().instSelector(g);
            List<InstanceLiteral> modLits = new LinkedList<InstanceLiteral>();
            if (modifies.contains(g)) 
                modLits = getModLits(g, expr);
            ForgeType t = g.type();
            assert t.arity() >= 2 : "static fields not supported yet";
            ForgeType colType = t.projectType(0);
            List<InstanceLiteral> instLiterals = fconv.findInstLiteralsForType((Unary) colType);
            if (instLiterals.isEmpty()) {
                Expression emptyRel = Relation.NONE;
                for (int i = 1; i < g.arity(); i++)
                    emptyRel = emptyRel.product(Relation.NONE);
                addRelForVar(relName(g), emptyRel);
            } else {
                for (InstanceLiteral l : instLiterals) {
                    addFldRel(l, g, modLits);
                }
            }
        }
        
        // create relations for "this", "return" and method parameters
        if (forgeScene.thisVar() != null)
            addRelForVar(forgeScene.thisVar(), relName(forgeScene.thisVar()));
        for (LocalVariable var : forgeScene.args())
            addRelForVar(var, relName(var));
        if (forgeScene.returnVar() != null) 
            addRelForVar(forgeScene.returnVar(), relName(forgeScene.returnVar()));
        // create relations for constant relations
        for (GlobalVariable g : forgeScene.consts()) {
            addRelForVar(g, g.name());
        }
    }
    
    private List<InstanceLiteral> getModLits(GlobalVariable g, ForgeExpression expr) {
        if (expr == null)
            return fconv.findInstLiteralsForType((Unary) g.type().projectType(0));
        try {
            SquanderEval2 se = new SquanderEval2();
            ObjTupleSet fc = se.eval(expr, fconv);
            List<InstanceLiteral> lits = new ArrayList<InstanceLiteral>();
            for (ObjTuple t : fc.tuples()) {
                if (t.arity() == 1) {
                    InstanceLiteral l = forgeScene.instLitForObj(t.get(0));
                    if (l != null)
                        lits.add(l);
                }
            }
            return lits;
        } catch (Throwable t) {
            Log.warn("could not evaluate: " + expr + ". Reason: " + t.getMessage());
            return fconv.findInstLiteralsForType((Unary) g.type().projectType(0));
        }
    }

    // ************************************************************************************* \\
    
    protected void addRelForVar(ForgeVariable var, String name) {
        addRelForVar(name, Relation.nary(name, var.arity()));
    }
    
    private void addRelForVar(String name, Expression expr) {
        Expression varRel = var2rel.get(name);
        if (varRel == null) 
            varRel = expr;
        else 
            varRel = varRel.union(expr);
        var2rel.put(name, varRel);
    }
    
    private void addFldRel(InstanceLiteral l, GlobalVariable g, List<InstanceLiteral> modLits) {
        String varName = relName(g); 
        String name = l.name() + "_" + varName;
        Relation rel = Relation.nary(name, g.arity() - 1);
        Relation rel_pre = null;
        if (modLits.contains(l))
            rel_pre = Relation.nary(name + "_pre", g.arity() - 1);
        FldRelElem fre = new FldRelElem(l, g, rel, rel_pre);
        fldRels.put(rel.name(), fre);
        addRelForVar(varName, lit2rel.get(l.name()).product(rel));
        if (modifies.contains(g))
            addRelForVar(varName + "_pre", lit2rel.get(l.name()).product(fre.preRel()));
    }

    private List<FldRelElem> findFldRelsForVar(GlobalVariable var) {
        List<FldRelElem> l = new LinkedList<FldRelElem>();
        for (FldRelElem e : fldRels.values()) {
            if (e.g == var)
                l.add(e);
        }
        return l;
    }

    @Override
    protected Bounds createBounds() {
        Universe univ = createUniverse();
        TupleFactory f = univ.factory();
        Bounds b = new Bounds(univ);
        
        // bound literals
        for (Entry<String, Relation> e : lit2rel.entrySet()) {
            b.boundExactly(e.getValue(), f.setOf(e.getKey()));
        }
        
        // bound global variables
        for (GlobalVariable var : program.globalVariables()) {
            ObjTupleSet[] lowup = getBounds(var);
            if (forgeScene.isConst(var)) {
                Expression rel = var2rel.get(relName(var));
                b.boundExactly((Relation)rel, conv2tuples(lowup[0], f));
            } else {
                List<FldRelElem> fldRelsForVar = findFldRelsForVar(var);
                if (!fldRelsForVar.isEmpty()) {
                    for (FldRelElem fldRelElem : fldRelsForVar) {
                        Object atom = fconv.lit2obj(fldRelElem.l);
                        ObjTupleSet ots = ObjTupleSet.singleTuple(atom);
                        TupleSet lowerTupleSet = conv2tuples(ots.join(lowup[0]), f);
                        TupleSet upperTupleSet = conv2tuples(ots.join(lowup[1]), f);
                        b.bound(fldRelElem.preRel(), lowerTupleSet, upperTupleSet);
                        if (fldRelElem.isMod()) {
                            b.bound(fldRelElem.postRel(), conv2tuples(ots.join(getExtent(var)), f));
                        }
                    }
                }
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
    
    protected void boundLocalVar(LocalVariable var, TupleFactory f, Bounds b) {
        if (var == null)
            return;
        ObjTupleSet[] lowup = getBounds(var);
        TupleSet lowerBound = conv2tuples(lowup[0], f);
        TupleSet upperBound = conv2tuples(lowup[1], f);
        Relation rel = (Relation) var2rel.get(relName(var));
        b.bound(rel, lowerBound, upperBound);
    }
    
    private ObjTupleSet[] getBounds(ForgeVariable var) {
        ObjTupleSet[] result = new ObjTupleSet[2];
        ObjTupleSet bnd = fconv.heap2Lit().bounds().get(var.name());
        ObjTupleSet lowerTupleSet;
        ObjTupleSet upperTupleSet;
        if (bnd != null) {
            lowerTupleSet = bnd;
            upperTupleSet = lowerTupleSet;
        } else {
            lowerTupleSet = new ObjTupleSet(var.arity());
            upperTupleSet = getExtent(var);
        }
        result[0] = lowerTupleSet;
        result[1] = upperTupleSet;
        return result;
    }

    private ObjTupleSet getExtent(ForgeVariable var) {
        // TODO: bound arrays tighter etc.
        ForgeType t = var.type();
        ObjTupleSet result = null;
        for (int i = 0; i < t.arity(); i++) {
            ForgeType.Unary colType = (Unary) t.projectType(i);
            ObjTupleSet tuple = new ObjTupleSet(1);
            for (ForgeLiteral lit : fconv.findLiteralsForType(colType)) {
                tuple.add(new ObjTuple(fconv.lit2obj(lit)));
            }
            if (result == null)
                result = tuple; 
            else 
                result = result.product(tuple);
        }
        if (result == null)
            result = new ObjTupleSet(var.arity());
        return result;
    }

}
/*! @} */
