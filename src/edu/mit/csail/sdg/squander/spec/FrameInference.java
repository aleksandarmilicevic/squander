/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.util.HashSet;
import java.util.Set;

import forge.program.BinaryExpression;
import forge.program.ConditionalExpression;
import forge.program.ExpressionVisitor;
import forge.program.ForgeLiteral;
import forge.program.ForgeType;
import forge.program.ForgeVariable;
import forge.program.GlobalVariable;
import forge.program.OldExpression;
import forge.program.ProjectionExpression;
import forge.program.QuantifyExpression;
import forge.program.UnaryExpression;

public class FrameInference extends ExpressionVisitor<Void> {

    private Set<GlobalVariable> globals = new HashSet<GlobalVariable>();

    public Set<GlobalVariable> globals() { return this.globals; }
    
    @Override
    protected Void visit(ForgeType expr) {
        return null;
    }

    @Override
    protected Void visit(ForgeLiteral expr) {
        return null;
    }

    @Override
    protected Void visit(ForgeVariable expr) {
        if (expr.isGlobal())
            globals.add((GlobalVariable) expr);
        return null;
    }

    @Override
    protected Void visit(UnaryExpression expr) {
        expr.sub().accept(this);
        return null;
    }

    @Override
    protected Void visit(BinaryExpression expr) {
        expr.left().accept(this);
        expr.right().accept(this);
        return null;
    }

    @Override
    protected Void visit(ConditionalExpression expr) {
        expr.condition().accept(this);
        expr.thenExpr().accept(this);
        expr.elseExpr().accept(this);
        return null;
    }

    @Override
    protected Void visit(ProjectionExpression expr) {
        expr.sub().accept(this);
        return null;
    }

    @Override
    protected Void visit(QuantifyExpression expr) {
        expr.sub().accept(this);
        return null;
    }

    @Override
    protected Void visit(OldExpression expr) {
        expr.variable().accept(this);
        return null;
    }

}
/*! @} */
