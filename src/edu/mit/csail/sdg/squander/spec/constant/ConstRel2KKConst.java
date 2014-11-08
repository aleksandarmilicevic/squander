package edu.mit.csail.sdg.squander.spec.constant;

import kodkod.ast.ConstantExpression;
import kodkod.ast.Expression;

public class ConstRel2KKConst implements ConstRelVisitor<Expression> {

    @Override
    public Expression visitIdent() {
        return ConstantExpression.IDEN;
    }

    @Override
    public Expression visitUniv() {
        return ConstantExpression.UNIV;
    }

    @Override
    public Expression visitNone() {
        return ConstantExpression.NONE;
    }

    @Override
    public Expression visitInc() {
        return null;
    }

    @Override
    public Expression visitDec() {
        return null;
    }

    public Expression convert(String name) {
        ConstRel cst = ConstRels.findRel(name);
        if (cst == null)
            return null;
        return cst.accept(this);
    }

}
