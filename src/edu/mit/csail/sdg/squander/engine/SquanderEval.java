/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.engine.ISquanderResult.IEvaluator;
import edu.mit.csail.sdg.squander.utils.Utils;
import edu.mit.csail.sdg.util.collections.UniqueList;
import forge.program.BinaryExpression;
import forge.program.BooleanDomain;
import forge.program.BooleanLiteral;
import forge.program.ConditionalExpression;
import forge.program.ExpressionVisitor;
import forge.program.ForgeExpression;
import forge.program.ForgeLiteral;
import forge.program.ForgeType;
import forge.program.ForgeVariable;
import forge.program.InstanceLiteral;
import forge.program.IntegerDomain;
import forge.program.IntegerLiteral;
import forge.program.LocalVariable;
import forge.program.OldExpression;
import forge.program.ProjectionExpression;
import forge.program.QuantifyExpression;
import forge.program.UnaryExpression;
import forge.solve.ForgeBounds;
import forge.solve.ForgeConstant;
import forge.solve.ForgeConstant.Tuple;

/**
 * Used to evaluate relational Forge expressions against the heap.
 * 
 * @author Aleksandar Milicevic
 * @deprecated Use SquanderEval2
 */
@Deprecated
public class SquanderEval extends ExpressionVisitor<ForgeConstant> {
    
    static interface BinFunc<R, T> {
        public static BinFunc<Boolean, Boolean> AND     = new BinFunc<Boolean, Boolean>() { public Boolean exe(Boolean b1, Boolean b2) { return b1 && b2; }};
        public static BinFunc<Boolean, Boolean> OR      = new BinFunc<Boolean, Boolean>() { public Boolean exe(Boolean b1, Boolean b2) { return b1 || b2; }};
        public static BinFunc<Boolean, Boolean> XOR     = new BinFunc<Boolean, Boolean>() { public Boolean exe(Boolean b1, Boolean b2) { return !b1.equals(b2); }};
        public static BinFunc<Boolean, Boolean> IFF     = new BinFunc<Boolean, Boolean>() { public Boolean exe(Boolean b1, Boolean b2) { return b1.equals(b2); }};
        public static BinFunc<Boolean, Boolean> IMPLIES = new BinFunc<Boolean, Boolean>() { public Boolean exe(Boolean b1, Boolean b2) { return b1 || !b2; }};
        
        public static BinFunc<Boolean, Integer> GT      = new BinFunc<Boolean, Integer>() { public Boolean exe(Integer b1, Integer b2) { return b1 > b2; }};
        public static BinFunc<Boolean, Integer> GTE     = new BinFunc<Boolean, Integer>() { public Boolean exe(Integer b1, Integer b2) { return b1 >= b2; }};
        public static BinFunc<Boolean, Integer> LT      = new BinFunc<Boolean, Integer>() { public Boolean exe(Integer b1, Integer b2) { return b1 < b2; }};
        public static BinFunc<Boolean, Integer> LTE     = new BinFunc<Boolean, Integer>() { public Boolean exe(Integer b1, Integer b2) { return b1 <= b2; }};

        public static BinFunc<Integer, Integer> PLUS    = new BinFunc<Integer, Integer>() { public Integer exe(Integer b1, Integer b2) { return b1 + b2; }};
        public static BinFunc<Integer, Integer> MINUS   = new BinFunc<Integer, Integer>() { public Integer exe(Integer b1, Integer b2) { return b1 - b2; }};
        public static BinFunc<Integer, Integer> TIMES   = new BinFunc<Integer, Integer>() { public Integer exe(Integer b1, Integer b2) { return b1 * b2; }};
        public static BinFunc<Integer, Integer> DIVIDE  = new BinFunc<Integer, Integer>() { public Integer exe(Integer b1, Integer b2) { return b1 / b2; }};
        public static BinFunc<Integer, Integer> MOD     = new BinFunc<Integer, Integer>() { public Integer exe(Integer b1, Integer b2) { return b1 % b2; }};
        
        public R exe(T b1, T b2); 
    }
    
    private ForgeBounds bounds;
    private ForgeConverter fconv;
    private boolean hasSolution = false;
    private Stack<StackElem> quantStack = new Stack<StackElem>();
    private Map<ForgeVariable, ForgeConstant> assignments = new HashMap<ForgeVariable, ForgeConstant>();

    public Map<ForgeVariable, ForgeConstant> getAssignments() {
        return assignments;
    }
    
    public ISquanderResult.IEvaluator getEvaluator() {
        return new ISquanderResult.IEvaluator() {

            @Override
            public String trace() {
                return assignments.toString();
            }
            
            @Override
            public boolean hasSolution() {
                return hasSolution;
            }
            
            @Override
            public String unsatCore() {
                return "unknown";
            }

            @Override
            public IEvaluator nextSolution() {
                throw new RuntimeException("not supported");
            }

            @Override
            public String stats() {
                return "unknown stats";
            }
            
            @Override
            public ObjTupleSet evaluate(ForgeExpression var) {
                ForgeConstant val = assignments.get(var);
                return ObjTupleSet.convertFrom(val, fconv);
            }

            @Override
            public ForgeExpression parse(String expr) {
                return new PostExeTranslator(fconv).translate(expr);
            }
            
        };
    }
    
    /**
     * Evaluates the given Forge expression against the given bounds (which represent the heap)
     * and returns a Forge constant as the result of the evaluation. 
     */
    public ForgeConstant eval(ForgeExpression expr, ForgeBounds bounds, ForgeConverter fconv) {
        this.bounds = bounds;
        this.fconv = fconv;
        this.quantStack = new Stack<StackElem>();
        ForgeConstant val = expr.accept(this);
        hasSolution = true;
        return val;
    }

    @Override
    protected ForgeConstant visit(BinaryExpression expr) {
        switch (expr.op()) {
            case AND:
                return visitBoolBinExpr(expr, BinFunc.AND); // TODO: short-circuit
            case OR: 
                return visitBoolBinExpr(expr, BinFunc.OR);  // TODO: short-circuit
            case XOR: 
                return visitBoolBinExpr(expr, BinFunc.XOR);
            case IMPLIES: 
                return visitBoolBinExpr(expr, BinFunc.IMPLIES);
            case IFF: 
                return visitBoolBinExpr(expr, BinFunc.IFF);
            case EQUALS: 
                return visitEquals(expr);
            case GT: 
                return visitIntBinExpr(expr, BinFunc.GT);
            case GTE: 
                return visitIntBinExpr(expr, BinFunc.GTE);
            case LT: 
                return visitIntBinExpr(expr, BinFunc.LT);
            case LTE: 
                return visitIntBinExpr(expr, BinFunc.LTE);
            case PLUS: 
                return visitIntBinExpr(expr, BinFunc.PLUS);
            case MINUS: 
                return visitIntBinExpr(expr, BinFunc.MINUS);
            case TIMES: 
                return visitIntBinExpr(expr, BinFunc.TIMES);
            case DIVIDE: 
                return visitIntBinExpr(expr, BinFunc.DIVIDE);
            case MODULO: 
                return visitIntBinExpr(expr, BinFunc.MOD);
            case JOIN:
                return visitJoin(expr);
            case PRODUCT: 
                return visitProduct(expr);
            case UNION: 
                return visitRelUnion(expr);
            case DIFFERENCE:
                return visitDiff(expr);
            case SUBSET: 
                return visitSubset(expr);
            case INTERSECTION:
                return visitIntersection(expr);
            default:
                throw new RuntimeException("Unsupported bin operation: " + expr.op() + " in " + expr);
        }
    }

    @Override
    protected ForgeConstant visit(ConditionalExpression expr) {
        ForgeExpression cond = expr.condition();
        ForgeConstant condEval = cond.accept(this);
        if (Utils.boolValue(condEval))
            return expr.thenExpr().accept(this);
        else
            return expr.elseExpr().accept(this);
    }

    @Override
    protected ForgeConstant visit(ForgeLiteral expr) {
        if (expr instanceof IntegerLiteral)
            return bounds.intAtom(((IntegerLiteral) expr).value());
        if (expr instanceof BooleanLiteral)
            return bounds.boolAtom(((BooleanLiteral) expr).value());
        return bounds.instanceAtom((InstanceLiteral) expr);
    }

    @Override
    protected ForgeConstant visit(ForgeType expr) {
        return bounds.extent(expr);
    }

    @Override
    protected ForgeConstant visit(ForgeVariable expr) {
        if (expr instanceof LocalVariable)
            return visitLocalVariable((LocalVariable) expr);
        else
            return visitVariable(expr);
    }

    @Override
    protected ForgeConstant visit(OldExpression expr) {
        return visit(expr.variable());
    }

    @Override
    protected ForgeConstant visit(ProjectionExpression expr) {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented");
    }

    @Override
    protected ForgeConstant visit(QuantifyExpression expr) {
        switch (expr.op()) {
            case UNION: 
                return visitUnion(expr);
            case ALL: 
                return visitAll(expr);
            case SOME: 
                return visitExists(expr);
            default: 
                throw new RuntimeException("Unsupported quantify expression: " + expr.op() + " in " + expr);
        } 
    }

    @Override
    protected ForgeConstant visit(UnaryExpression expr) {
        switch (expr.op()) {
            case NOT: {
                ForgeConstant res = expr.sub().accept(this);
                return bounds.boolAtom(!Utils.boolValue(res));
            }
            case SOME: {
                ForgeConstant res = expr.sub().accept(this);
                return bounds.boolAtom(!res.isEmpty());                
            }
            case NO: {
                ForgeConstant res = expr.sub().accept(this);
                return bounds.boolAtom(res.isEmpty());
            }
            case LONE: {
                ForgeConstant res = expr.sub().accept(this);
                return bounds.boolAtom(res.isEmpty() || res.isTuple());
            }
            case ONE: {
                ForgeConstant res = expr.sub().accept(this);
                return bounds.boolAtom(res.isTuple());
            }
            case CLOSURE: {
                ForgeConstant res = expr.sub().accept(this);
                return visitClosure(res);
                // TODO: It would be much more efficient to implement CLOSURE inside 
                //       the enclosing JOIN, but this is more general
            }
            default: {
                throw new RuntimeException("Unsupported unary operation: " + expr.op() + " in " + expr);
            }
        }
    }
    
    // ***********************************************************************************

    private ForgeConstant visitVariable(ForgeVariable var) {
        ForgeConstant lower = bounds.initialLowerBound(var);
        ForgeConstant upper = bounds.initialUpperBound(var);
        assert lower.equals(upper) : "var=" + var +"; lower=" + lower + "; upper=" + upper;
        return lower;
    }

    private ForgeConstant visitLocalVariable(LocalVariable var) {
        StackElem elem = searchStack(var);
        if (elem != null)
            return elem.val;
        else 
            return visitVariable(var);
    }
    
    private StackElem searchStack(LocalVariable var) {
        for (int i = quantStack.size() - 1; i >= 0; i--) {
            StackElem elem = quantStack.get(i);
            if (elem.var.equals(var))
                return elem;
        }
        return null;
    }

    @SuppressWarnings("unused")
    private ForgeConstant visitAssignment(BinaryExpression expr) {
        ForgeExpression lhs = expr.left();
        assert lhs instanceof ForgeVariable : "lhs of an assignment expression must be ForgeVariable;";
        ForgeConstant val = expr.right().accept(this);
        assignments.put((ForgeVariable)lhs, val);
        return bounds.boolAtom(true);
    }
    
    private ForgeConstant visitEquals(BinaryExpression expr) {
        // TODO: this is ok for formula evaluations, but what about assignments?
        ForgeConstant lhsVal = expr.left().accept(this);
        ForgeConstant rhsVal = expr.right().accept(this);
        return bounds.boolAtom(lhsVal.equals(rhsVal));
    }

    private ForgeConstant visitJoin(BinaryExpression expr) {
        ForgeConstant lhs = expr.left().accept(this);
        ForgeConstant rhs = expr.right().accept(this);
        return lhs.join(rhs);
    }
    
    private ForgeConstant visitProduct(BinaryExpression expr) {
        ForgeConstant lhs = expr.left().accept(this);
        ForgeConstant rhs = expr.right().accept(this);
        return lhs.product(rhs);
    }
    
    private ForgeConstant visitRelUnion(BinaryExpression expr) {
        ForgeConstant lhs = expr.left().accept(this);
        ForgeConstant rhs = expr.right().accept(this);
        return lhs.union(rhs);
    }

    private ForgeConstant visitDiff(BinaryExpression expr) {
        ForgeConstant lhs = expr.left().accept(this);
        ForgeConstant rhs = expr.right().accept(this);
        return lhs.difference(rhs);
    }


    private ForgeConstant visitSubset(BinaryExpression expr) {
        ForgeConstant lhs = expr.left().accept(this);
        ForgeConstant rhs = expr.right().accept(this);
        return bounds.boolAtom(lhs.subsetOf(rhs)); 
    }
    
    private ForgeConstant visitIntersection(BinaryExpression expr) {
        ForgeConstant lhs = expr.left().accept(this);
        ForgeConstant rhs = expr.right().accept(this);
        return lhs.intersection(rhs);
    }
    
    private ForgeConstant visitClosure(ForgeConstant res) {
        assert res.arity() == 2 : "Closure is allowed only on binary relations: " + res;
        ForgeConstant ret = res;
        boolean changed;
        do {
            int size = ret.tuples().size();
            for (Tuple t : ret.tuples()) {
                ret = ret.union(t.join(ret));
            }
            changed = ret.tuples().size() != size;
        } while (changed);
        return ret;
    }
    
    private ForgeConstant visitBoolBinExpr(BinaryExpression expr, BinFunc<Boolean, Boolean> f) {
        assert expr.left().type().getClass() == BooleanDomain.class;
        assert expr.right().type().getClass() == BooleanDomain.class;
        ForgeConstant lhs = expr.left().accept(this);
        ForgeConstant rhs = expr.right().accept(this);
        Boolean b1 = Utils.boolValue(lhs); 
        Boolean b2 = Utils.boolValue(rhs);
        boolean b = f.exe(b1, b2);
        return bounds.boolAtom(b);
    }
    
    private <R> ForgeConstant visitIntBinExpr(BinaryExpression expr, BinFunc<R, Integer> f) {
        assert expr.left().type().getClass() == IntegerDomain.class;
        assert expr.right().type().getClass() == IntegerDomain.class;
        ForgeConstant lhs = expr.left().accept(this);
        ForgeConstant rhs = expr.right().accept(this);
        Integer b1 = Utils.intValue(lhs); 
        Integer b2 = Utils.intValue(rhs);
        R ret = f.exe(b1, b2);
        if (ret instanceof Integer)
            return bounds.intAtom((Integer) ret);
        else if (ret instanceof Boolean)
            return bounds.boolAtom((Boolean) ret); 
        else 
            throw new RuntimeException();
    }

    private static class StackElem {
        LocalVariable var;
        ForgeConstant val; 

        StackElem(LocalVariable var, ForgeConstant val) {
            this.var = var;
            this.val = val;
        } 
    }
    
    private ForgeConstant visitUnion(QuantifyExpression expr) {
        UniqueList<LocalVariable> locals = expr.decls().locals();
        assert locals.size() == 1 : "don't know how to handle UNION with more than 1 local var: " + expr;
        LocalVariable var = locals.get(0);
        ForgeConstant dom = bounds.extent(var.type());
        ForgeConstant result = bounds.empty(dom.arity());        
        for (Tuple val : dom.tuples()) {
            quantStack.push(new StackElem(var, val));
            ForgeConstant sub = expr.sub().accept(this);
            if (Utils.boolValue(sub)) {
                result = result.union(val);
            }
            quantStack.pop();
        }
        return result;
    }
    
    private ForgeConstant visitAll(QuantifyExpression expr) {
        UniqueList<LocalVariable> locals = expr.decls().locals();
        assert locals.size() == 1 : "don't know how to handle ALL with more than 1 local var: " + expr;
        LocalVariable var = locals.get(0);
        ForgeConstant dom = bounds.extent(var.type());
        for (Tuple val : dom.tuples()) {
            quantStack.push(new StackElem(var, val));
            ForgeConstant sub = expr.sub().accept(this);
            quantStack.pop();
            if (!Utils.boolValue(sub))
                return bounds.boolAtom(false); 
        }
        return bounds.boolAtom(true);
    }

    private ForgeConstant visitExists(QuantifyExpression expr) {
        UniqueList<LocalVariable> locals = expr.decls().locals();
        assert locals.size() == 1 : "don't know how to handle SOME with more than 1 local var: " + expr;
        LocalVariable var = locals.get(0);
        ForgeConstant dom = bounds.extent(var.type());
        for (Tuple val : dom.tuples()) {
            quantStack.push(new StackElem(var, val));
            ForgeConstant sub = expr.sub().accept(this);
            quantStack.pop();
            if (Utils.boolValue(sub))
                return bounds.boolAtom(true); 
        }
        return bounds.boolAtom(false);
    }
    
}
/*! @} */
