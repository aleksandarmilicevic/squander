/*! \addtogroup JFSLParser JFSL Parser 
 * This module contains classes for parsing JFSL specification and converting them to an AST. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.parser;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

/**
 * Parsing exception.
 * 
 * @author kuat 
 */
@SuppressWarnings("serial")
public class JFSLParserException extends RuntimeException {    
    /** line and column of the error, -1 if not specified */
    private int line = -1, startIdx = -1, stopIdx = -1, column = -1;
        
    /** token at which the error occurred */
    private Tree token;
    
    /** source string, may not correspond to the token */
    private String source;

    public JFSLParserException(String msg) {
        super(msg);
    }
    
    public JFSLParserException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    public JFSLParserException(String msg, Tree token) {
        super(msg);
        setToken(token);
    }
    
    public JFSLParserException(String msg, String source) {
        super(msg);
        setSource(source);
    }
    
    public JFSLParserException(RecognitionException cause) {    
        super("parsing error on token '" + cause.token.getText() +"' at column " + cause.charPositionInLine + ", line " + cause.line, cause);
        this.column = cause.charPositionInLine;
        this.line = cause.line;
        this.source = cause.input.toString();
    }
    
    public JFSLParserException(Throwable cause) {
        super(cause);
    }
    
    public JFSLParserException(Throwable cause, Tree token) {
        super(cause);
        setToken(token);
    }
    

    @Override
    public String getMessage() {
        return super.getMessage() + "\nwhile parsing: \n  " + source;
    }

    public void setToken(Tree token) {
        if (token != null) {
            this.token = token;
            this.column = token.getCharPositionInLine();
            this.startIdx = token.getTokenStartIndex();            
            this.stopIdx = token.getTokenStopIndex();
            this.line = token.getLine();
        }
    }
    
    public void setSource(String source) {
        this.source = source;
    }
        
    public String source() {
        return source;
    }

    public int column()   { return column; }    
    public int startIdx() { return startIdx; }
    public int stopIdx()  { return stopIdx; }
    public int line()     { return line; }
    
    /** Report the deepest subtree in the tree as text */
    public String token() {
        if (token == null) return null;
        
        StringBuilder sb = new StringBuilder();
        return layout(token, sb, "").toString();
    }
    
    /** Token tree output */
    private StringBuilder layout(Tree t, StringBuilder sb, String buf) {
        sb.append(buf).append(t).append('\n');
        for (int i = 0; i < t.getChildCount(); i++)
            layout(t.getChild(i), sb, buf + '\t');
        return sb;
    }
}
/*! @} */
