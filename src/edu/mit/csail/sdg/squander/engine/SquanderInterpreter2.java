package edu.mit.csail.sdg.squander.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.engine.SquanderEval2.BinFunc;
import edu.mit.csail.sdg.squander.spec.Visitor;
import edu.mit.csail.sdg.util.collections.UniqueList;
import forge.program.BinaryExpression;
import forge.program.BooleanDomain;
import forge.program.ConditionalExpression;
import forge.program.ExpressionVisitor;
import forge.program.ForgeExpression;
import forge.program.ForgeLiteral;
import forge.program.ForgeType;
import forge.program.ForgeVariable;
import forge.program.IntegerDomain;
import forge.program.LocalVariable;
import forge.program.OldExpression;
import forge.program.ProjectionExpression;
import forge.program.QuantifyExpression;
import forge.program.UnaryExpression;

public class SquanderInterpreter2 extends ExpressionVisitor<ObjTupleSet> {
    private boolean allowExtent = true;
    private ForgeConverter fconv;
    private Heap2Bounds heap2lit;
    
    private Stack<StackElem> quantStack = new Stack<StackElem>();
    private Map<ForgeVariable, ObjTupleSet> assignments = new HashMap<ForgeVariable, ObjTupleSet>();

    public ObjTupleSet getAssignmentFor(ForgeVariable var) {
        return assignments.get(var);
    }
      
    public boolean isAllowExtent()                  { return allowExtent; }
    public void setAllowExtent(boolean allowExtent) { this.allowExtent = allowExtent; }

    /**
     * Evaluates the given Forge expression against the given bounds (which represent the heap)
     * and returns an {@link ObjTupleSet} as the result of the evaluation. 
     */
    public ObjTupleSet interpret(ForgeExpression expr, ForgeConverter fconv) {
        this.fconv = fconv;
        this.heap2lit = fconv.heap2Lit();
        this.quantStack = new Stack<StackElem>();
        ObjTupleSet val = expr.accept(this);
        return val;
    }

    public ObjTupleSet filter(ForgeExpression filterExpr, ForgeExpression domain, ForgeConverter fconv) {
        return filterSelect(filterExpr, domain, fconv, true);
    }
    
    public ObjTupleSet select(ForgeExpression filterExpr, ForgeExpression domain, ForgeConverter fconv) {
        return filterSelect(filterExpr, domain, fconv, false);
    }
    
    public boolean isKnown(ForgeExpression expr, ForgeConverter fconv) {
        try {
            interpret(expr, fconv);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
    
    private ObjTupleSet filterSelect(ForgeExpression filterExpr, ForgeExpression domain, ForgeConverter fconv, boolean filterSelect) {
        this.fconv = fconv;
        this.heap2lit = fconv.heap2Lit();
        this.quantStack = new Stack<StackElem>();
        ObjTupleSet ret = new ObjTupleSet(domain.arity());
        ObjTupleSet domainValues;
        try {
            domainValues = domain.accept(this);
        } catch (Throwable t) {
            // use extent
            domainValues = domain.type().accept(this);
        }

        LocalVariable lambdaVar = fconv.forgeScene().program().newLocalVariable(Visitor.LAMBDA_VAR_NAME, domain.type());
        for (ObjTuple tuple : domainValues.tuples()) {
            StackElem item = new StackElem(lambdaVar, tuple);
            quantStack.push(item);
            ObjTupleSet filterResult = filterExpr.accept(this);
            if (boolValue(filterResult) != filterSelect)
                ret.add(tuple);
            quantStack.pop();
        }
        return ret;
    }
    
    @Override
    protected ObjTupleSet visit(BinaryExpression expr) {
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
    protected ObjTupleSet visit(ConditionalExpression expr) {
        ForgeExpression cond = expr.condition();
        ObjTupleSet condEval = cond.accept(this);
        if (boolValue(condEval))
            return expr.thenExpr().accept(this);
        else
            return expr.elseExpr().accept(this);
    }

    @Override
    protected ObjTupleSet visit(ForgeLiteral expr) {
        return objAtom(fconv.lit2obj(expr));
    }
    
    @Override
    protected ObjTupleSet visit(ForgeType expr) {
        if (!allowExtent && expr != fconv.forgeScene().nullType())
            throw new InterpretationError("getting the extent of a type not allowed");
        return fconv.extent(expr);
    }

    @Override
    protected ObjTupleSet visit(ForgeVariable expr) {
        if (expr instanceof LocalVariable)
            return visitLocalVariable((LocalVariable) expr);
        else
            return visitVariable(expr);
    }

    @Override
    protected ObjTupleSet visit(OldExpression expr) {
        return visit(expr.variable());
    }

    @Override
    protected ObjTupleSet visit(ProjectionExpression expr) {
        ObjTupleSet sub = expr.sub().accept(this);
        return sub.projection(expr.columns());
    }

    @Override
    protected ObjTupleSet visit(QuantifyExpression expr) {
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
    protected ObjTupleSet visit(UnaryExpression expr) {
        switch (expr.op()) {
            case NOT: {
                ObjTupleSet res = expr.sub().accept(this);
                return boolAtom(!boolValue(res));
            }
            case SOME: {
                ObjTupleSet res = expr.sub().accept(this);
                return boolAtom(!res.isEmpty());                
            }
            case NO: {
                ObjTupleSet res = expr.sub().accept(this);
                return boolAtom(res.isEmpty());
            }
            case LONE: {
                ObjTupleSet res = expr.sub().accept(this);
                return boolAtom(res.isEmpty() || res.isTuple());
            }
            case ONE: {
                ObjTupleSet res = expr.sub().accept(this);
                return boolAtom(res.isTuple());
            }
            case CLOSURE: {
                ObjTupleSet res = expr.sub().accept(this);
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

    private ObjTupleSet visitVariable(ForgeVariable var) {
        ObjTupleSet b = heap2lit.getBound(var); 
        if (b == null) 
            throw new InterpretationError("bound not specified for variable " + var);
        return b;
    }

    private ObjTupleSet visitLocalVariable(LocalVariable var) {
        StackElem elem = searchStack(var);
        if (elem != null)
            return new ObjTupleSet(elem.val);
        else 
            return visitVariable(var);
    }
    
    private StackElem searchStack(LocalVariable var) {
        for (int i = quantStack.size() - 1; i >= 0; i--) {
            StackElem elem = quantStack.get(i);
            if (elem.var.name().equals(var.name()))
                return elem;
        }
        return null;
    }
        
    private ObjTupleSet visitEquals(BinaryExpression expr) {
        ObjTupleSet lhsVal = null; 
        InterpretationError err = null;
        try { lhsVal = expr.left().accept(this); } catch (InterpretationError e) { err = e; }
        
        if (expr.left() instanceof ForgeVariable && lhsVal == null) {
            // interpret as assignment
            assignments.put((ForgeVariable) expr.left(), expr.right().accept(this));   
            return boolAtom(true);
        } 
        
        // interpret as equality check
        if (err != null) throw err;
        return boolAtom(lhsVal.equals(expr.right().accept(this)));
        
    }

    private ObjTupleSet visitJoin(BinaryExpression expr) {
        ObjTupleSet lhs = expr.left().accept(this);
        ObjTupleSet rhs = expr.right().accept(this);
        return lhs.join(rhs);
    }
    
    private ObjTupleSet visitProduct(BinaryExpression expr) {
        ObjTupleSet lhs = expr.left().accept(this);
        ObjTupleSet rhs = expr.right().accept(this);
        return lhs.product(rhs);
    }
    
    private ObjTupleSet visitRelUnion(BinaryExpression expr) {
        ObjTupleSet lhs = expr.left().accept(this);
        ObjTupleSet rhs = expr.right().accept(this);
        return lhs.union(rhs);
    }

    private ObjTupleSet visitDiff(BinaryExpression expr) {
        ObjTupleSet lhs = expr.left().accept(this);
        ObjTupleSet rhs = expr.right().accept(this);
        return lhs.diff(rhs);
    }


    private ObjTupleSet visitSubset(BinaryExpression expr) {
        ObjTupleSet lhs = expr.left().accept(this);
        ObjTupleSet rhs = expr.right().accept(this);
        return boolAtom(lhs.subsetOf(rhs)); 
    }
    
    private ObjTupleSet visitIntersection(BinaryExpression expr) {
        ObjTupleSet lhs = expr.left().accept(this);
        ObjTupleSet rhs = expr.right().accept(this);
        return lhs.intersection(rhs);
    }
    
    private ObjTupleSet visitClosure(ObjTupleSet res) {
        if (res.arity() != 2) throw new InterpretationError("Closure is allowed only on binary relations: " + res);
        ObjTupleSet ret = res;
        boolean changed;
        do {
            int size = ret.tuples().size();
            for (ObjTuple t : ret.tuples()) {
                ret = ret.union(new ObjTupleSet(t).join(ret));
            }
            changed = ret.tuples().size() != size;
        } while (changed);
        return ret;
    }
    
    private ObjTupleSet visitBoolBinExpr(BinaryExpression expr, BinFunc<Boolean, Boolean> f) {
        if (expr.left().type().getClass() != BooleanDomain.class) throw new InterpretationError("not a bool");
        if (expr.right().type().getClass() != BooleanDomain.class) throw new InterpretationError("not a bool");
        ObjTupleSet lhs = expr.left().accept(this);
        ObjTupleSet rhs = expr.right().accept(this);
        Boolean b1 = boolValue(lhs); 
        Boolean b2 = boolValue(rhs);
        boolean b = f.exe(b1, b2);
        return boolAtom(b);
    }
    
    private <R> ObjTupleSet visitIntBinExpr(BinaryExpression expr, BinFunc<R, Integer> f) {
        if (expr.left().type().getClass() != IntegerDomain.class) throw new InterpretationError("not an int");
        if (expr.right().type().getClass() == IntegerDomain.class) throw new InterpretationError("not an int");
        ObjTupleSet lhs = expr.left().accept(this);
        ObjTupleSet rhs = expr.right().accept(this);
        Integer b1 = intValue(lhs); 
        Integer b2 = intValue(rhs);
        R ret = f.exe(b1, b2);
        if (ret instanceof Integer)
            return intAtom((Integer) ret);
        else if (ret instanceof Boolean)
            return boolAtom((Boolean) ret); 
        else 
            throw new RuntimeException();
    }

    private static class StackElem {
        LocalVariable var;
        ObjTuple val; 
        
        StackElem(LocalVariable var, ObjTuple val) {
            this.var = var;
            this.val = val;
        } 
    }
    
    private ObjTupleSet visitUnion(QuantifyExpression expr) {
        UniqueList<LocalVariable> locals = expr.decls().locals();
        if (locals.size() != 1)  
            throw new InterpretationError("don't know how to handle UNION with more than 1 local var: " + expr);
        LocalVariable var = locals.get(0);
        ObjTupleSet dom = var.type().accept(this);
        ObjTupleSet result = new ObjTupleSet(dom.arity());        
        for (ObjTuple val : dom.tuples()) {
            quantStack.push(new StackElem(var, val));
            ObjTupleSet sub = expr.sub().accept(this);
            if (boolValue(sub)) {
                result = result.union(val);
            }
            quantStack.pop();
        }
        return result;
    }
    
    private ObjTupleSet visitAll(QuantifyExpression expr) {
        UniqueList<LocalVariable> locals = expr.decls().locals();
        if (locals.size() != 1)  
            throw new InterpretationError("don't know how to handle ALL with more than 1 local var: " + expr);
        LocalVariable var = locals.get(0);
        ObjTupleSet dom = var.type().accept(this);
        for (ObjTuple val : dom.tuples()) {
            quantStack.push(new StackElem(var, val));
            ObjTupleSet sub = expr.sub().accept(this);
            quantStack.pop();
            if (!boolValue(sub))
                return boolAtom(false); 
        }
        return boolAtom(true);
    }

    private ObjTupleSet visitExists(QuantifyExpression expr) {
        UniqueList<LocalVariable> locals = expr.decls().locals();
        if (locals.size() != 1)  
            throw new InterpretationError("don't know how to handle SOME with more than 1 local var: " + expr);
        LocalVariable var = locals.get(0);
        ObjTupleSet dom = var.type().accept(this);
        for (ObjTuple val : dom.tuples()) {
            quantStack.push(new StackElem(var, val));
            ObjTupleSet sub = expr.sub().accept(this);
            quantStack.pop();
            if (boolValue(sub))
                return boolAtom(true); 
        }
        return boolAtom(false);
    }

    private static ObjTupleSet boolAtom(boolean val) {
        ObjTupleSet ots = new ObjTupleSet(1);
        ots.add(new ObjTuple(val));
        return ots;
    }

    private static ObjTupleSet intAtom(int val) {
        ObjTupleSet ots = new ObjTupleSet(1);
        ots.add(new ObjTuple(val));
        return ots;
    }
    
    private static ObjTupleSet objAtom(Object obj) {
        ObjTupleSet ots = new ObjTupleSet(1);
        ots.add(new ObjTuple(obj));
        return ots;
    }
    
    private static boolean boolValue(ObjTupleSet ots) {
        if (ots.isEmpty())
            return false;
        if (ots.arity() != 1 || ots.tuples().size() != 1)
            throw new InterpretationError("not a bool tuple set"); 
        return (Boolean) ots.tuples().iterator().next().get(0);
    }
    
    private static int intValue(ObjTupleSet ots) {
        if (ots.arity() != 1 || ots.tuples().size() != 1)
            throw new InterpretationError("not an int tuple set");
        return (Integer) ots.tuples().iterator().next().get(0);
    }

    public boolean isNull(ObjTupleSet ots) {
        if (ots == null || ots.arity() != 1 || ots.tuples().size() != 1)
            return false;
        return ots.tuples().iterator().next().get(0) == null;
    }
    
    // ==================================================================
    
    @SuppressWarnings("serial")
    public static class InterpretationError extends RuntimeException {

        public InterpretationError(String message) {
            super(message);
        }
        
    }
    
}
