/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec.constant;

public interface ConstRelVisitor<E> {

    E visitIdent();
    E visitUniv();
    E visitNone();
    E visitInc();
    E visitDec();

}
/*! @} */
