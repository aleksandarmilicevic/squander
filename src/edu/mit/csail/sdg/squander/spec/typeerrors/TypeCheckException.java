/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec.typeerrors;

import edu.mit.csail.sdg.squander.parser.JFSLParserException;


/**
 * Indicates an error in type checking phase.
 * 
 * @author kuat
 */
public class TypeCheckException extends JFSLParserException {
    private static final long serialVersionUID = -6673030100868748117L;
    private final String src;
    
    public TypeCheckException(String msg, Throwable t) {
        super(msg, t);
        this.src = null;
    }
    
    public TypeCheckException(String msg) {
        this(msg, (String)null);
    }
    
    public TypeCheckException(String msg, String src) {
        super(msg);
        this.src = src;
    }

    public String getSrc() {
        return src;
    }

}
/*! @} */
