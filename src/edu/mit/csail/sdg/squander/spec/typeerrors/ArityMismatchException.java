/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec.typeerrors;

public class ArityMismatchException extends TypeCheckException {

    private static final long serialVersionUID = 9078354475670245756L;

    public ArityMismatchException(int left, int right) {
        super("arity mismatch: left arity " + left + " , right arity " + right);
    }

    public ArityMismatchException(int left, int right, String msg) {
        super("arity mismatch: left arity " + left + " , right arity " + right
                + " " + msg);
    }
}
/*! @} */
