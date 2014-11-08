/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec.typeerrors;

import edu.mit.csail.sdg.squander.spec.JType;

public class IncompatibleTypesException extends TypeCheckException {

    private static final long serialVersionUID = 9096734998607169611L;
    
    private final JType t1, t2; 
    private final String expr; 
    private final int pos;
    
    public IncompatibleTypesException(JType t1, JType t2, int pos, String expr) {
        super(String.format("Incompatible types: %s and %s%s%s", t1, t2, 
                expr != null ? " in " + expr : "", pos != -1 ? " at position " + pos : ""));
        this.t1 = t1;
        this.t2 = t2;
        this.expr = expr;
        this.pos = pos;
    }

    public JType getT1() {
        return t1;
    }

    public JType getT2() {
        return t2;
    }

    public String getExpr() {
        return expr;
    }

    public int getPos() {
        return pos;
    }
    
}
/*! @} */
