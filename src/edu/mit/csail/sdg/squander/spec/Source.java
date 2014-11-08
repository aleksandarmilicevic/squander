/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;

import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.parser.JFSLLexer;
import edu.mit.csail.sdg.squander.parser.JFSLParser;
import edu.mit.csail.sdg.squander.parser.JFSLParserException;
import edu.mit.csail.sdg.squander.parser.JFSLParser.Node;
import edu.mit.csail.sdg.squander.parser.JFSLParser.NodeAdaptor;
import edu.mit.csail.sdg.squander.spec.typeerrors.TypeCheckException;
import forge.program.ForgeExpression;

/**
 * Wraps a source string and presents a type checked abstract syntax tree.
 * The source is parsed and type-checked lazily.
 * 
 * @author kuat
 * @author Aleksandar Milicevic
 */
public class Source {
    public static enum Rule {
        CLAUSE, DECLARATION, FRAME
    };

    // source
    public final String source;
    public final NameSpace ns;
    public final Rule rule;
    public final boolean isFuncFlag;

    // to be initialized
    private Node node;
    private boolean typechecked = false;
    private ForgeExpression translation = null;
    
    /** Unparsed specification */
    public Source(String source, NameSpace ns, Rule kind) {
        this(source, ns, kind, false);
    }
    
    public Source(String source, NameSpace ns, Rule kind, boolean isFuncFlag) {
        this.source = source;
        this.rule = kind;
        this.ns = ns;
        this.node = null;
        this.isFuncFlag = isFuncFlag;
        if (isFuncFlag)
            assert kind == Rule.DECLARATION;
    }
    
    public boolean isClause()            { return rule == Rule.CLAUSE; }
    public boolean isFrame()             { return rule == Rule.FRAME; }
    public boolean isDecl()              { return rule == Rule.DECLARATION; }
    public boolean isFuncField()         { return isFuncFlag; }
    public boolean isTypechecked()       { return typechecked; }
    public boolean isTranslated()        { return translation != null; }
    public ForgeExpression translation() { return translation; }
    
    public Node node() {
        try {
            if (node == null) 
                parse();
        } catch (JFSLParserException e) {
            if (source != null)
                throw new JFSLParserException(e.getMessage(), source);
            else
                throw e;        
        }
        return node;
    }
    
    public void parse() {
        // execute ANTLR parser
        Log.trace("parsing: " + source);
        final Node node;

        // prepare ANTLR parser
        final ANTLRStringStream cs = new ANTLRStringStream(source);
        final JFSLLexer lexer = new JFSLLexer(cs);
        final CommonTokenStream tokens = new CommonTokenStream();
        tokens.setTokenSource(lexer);
        final JFSLParser parser = new JFSLParser(tokens);
        final TreeAdaptor adaptor = new NodeAdaptor();
        parser.setTreeAdaptor(adaptor);

        // run parser assuming it is a whole expression
        try {
            final Object result;
            switch(rule) {
            case CLAUSE: result = parser.clause().getTree(); break;
            case DECLARATION: result = parser.specField().getTree(); break;
            case FRAME: result = parser.modifies().getTree(); break;
            default: result = null;
            }

            if (!(result instanceof Node)) {
                if (result instanceof Tree) 
                    throw new JFSLParserException("cannot produce AST", (Tree) result);
                else 
                    throw new JFSLParserException("cannot produce AST");
            } else {
                node = (Node) result;
            }
        } catch (JFSLParserException e) {
            e.setSource(source);
            throw e;
        } catch (RuntimeException e) {
            throw new JFSLParserException(e);
        } catch (RecognitionException e) {
            throw new JFSLParserException(e);
        }

        this.node = node;
    }

    public void typecheck(TypeChecker checker) {
        if (typechecked)
            return;
        if (node == null)
            parse();
        Log.trace("typechecking: " + source);
        try {
            checker.visit(ns, this.node);
        } catch (TypeCheckException e) {
            e.setSource(source);
            throw e;
        } 
        typechecked = true;
    }
    
    /**
     * MUST CALL "typecheck" PRIOR TO CALLING THIS METHOD
     */
    public ForgeExpression translate(Tr tr, ForgeEnv env) {
        assert typechecked : "must typecheck before translating to ForgeExpression";
        try {
            Log.trace("translating with " + tr.getClass().getSimpleName() + ": " + source);
            ForgeExpression expr = tr.visit(env, node());
            this.translation = expr;
            return expr;
        } catch (Exception e) {
            throw new JFSLParserException("Error translating: " + source, e);
        }
    }
    
    @Override
    public String toString() {
        return rule + ": \"" + source + "\"";
    }
    
}
/*! @} */
