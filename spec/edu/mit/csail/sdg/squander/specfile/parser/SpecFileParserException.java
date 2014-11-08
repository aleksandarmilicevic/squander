package edu.mit.csail.sdg.squander.specfile.parser;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

/**
 * Parsing exception.
 * 
 * @author kuat 
 */
@SuppressWarnings("serial")
public class SpecFileParserException extends RuntimeException {    
    /** line and column of the error, -1 if not specified */
    private int line = -1, column = -1;
        
    /** token at which the error occurred */
    private Tree token;
    
    /** source string, may not correspond to the token */
    private String source;

    public SpecFileParserException(String msg) {
        super(msg);
    }
    
    public SpecFileParserException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    public SpecFileParserException(String msg, Tree token) {
        super(msg);
        setToken(token);
    }
    
    public SpecFileParserException(String msg, String source) {
        super(msg);
        setSource(source);
    }
    
    public SpecFileParserException(RecognitionException cause) {    
        super("parsing error on token '" + cause.token.getText() +"' at column " + cause.charPositionInLine + ", line " + cause.line, cause);
        this.column = cause.charPositionInLine;
        this.line = cause.line;
        this.source = cause.input.toString();
    }
    
    public SpecFileParserException(Throwable cause) {
        super(cause);
    }
    
    public SpecFileParserException(Throwable cause, Tree token) {
        super(cause);
        setToken(token);
    }

    public void setToken(Tree token) {
        if (token != null) {
            this.token = token;
            this.column = token.getCharPositionInLine();            
            this.line = token.getLine();
        }
    }
    
    public void setSource(String source) {
        this.source = source;
    }
        
    public String source() {
        return source;
    }

    public int column() {
        return column;
    }
    
    public int line() {
        return line;
    }
    
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
