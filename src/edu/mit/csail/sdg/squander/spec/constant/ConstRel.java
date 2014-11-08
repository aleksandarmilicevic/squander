/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec.constant;

import edu.mit.csail.sdg.squander.spec.JType;


public enum ConstRel {

    IDEN (JType.Factory.instance.newJType(Object.class, Object.class)),
    UNIV (JType.Factory.instance.newJType(Object.class)), 
    NONE (JType.Factory.instance.newJType(Object.class)), 
    INC  (JType.Factory.instance.newJType(int.class, int.class)), 
    DEC  (JType.Factory.instance.newJType(int.class, int.class));
    
    private final JType type;
    
    private ConstRel(JType type) {
        this.type = type;
    }
    
    public JType type() { return type; }
    
    public <E> E accept(ConstRelVisitor<E> visitor) {
        switch (this) {
        case IDEN:  return visitor.visitIdent();
        case UNIV:  return visitor.visitUniv();
        case NONE:  return visitor.visitNone();
        case INC:   return visitor.visitInc();
        case DEC:   return visitor.visitDec();
        }
        throw new RuntimeException("Unknown ConstRel type: " + this.name());
    }
    
    
}
/*! @} */
