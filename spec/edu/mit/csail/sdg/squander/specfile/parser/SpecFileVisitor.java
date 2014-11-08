package edu.mit.csail.sdg.squander.specfile.parser;

import static edu.mit.csail.sdg.squander.specfile.parser.SpecFileParser.FUNCFIELD;
import static edu.mit.csail.sdg.squander.specfile.parser.SpecFileParser.INVARIANT;
import static edu.mit.csail.sdg.squander.specfile.parser.SpecFileParser.PARAMS;
import static edu.mit.csail.sdg.squander.specfile.parser.SpecFileParser.SPECFIELD;
import static edu.mit.csail.sdg.squander.specfile.parser.SpecFileParser.SPECFILE;


import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.tree.Tree;

import edu.mit.csail.sdg.squander.specfile.parser.SpecFileParser.Node;


public abstract class SpecFileVisitor<N> {

    /** Casts a Tree to a Node*/
    protected Node cast(Tree tree) {
        if (!(tree instanceof Node)) 
            throw new SpecFileParserException("malformed node in the AST");
        return (Node) tree;
    }
    
    /** Gets a single child */
    protected Node child(Node parent) {
        return cast(parent.getChild(0));
    }

    protected String trimQuotes(String s) {
        return s.replaceAll("^\"", "").replaceAll("\"$", "");
    }
    
    /** Returns the text content of a token node */
    protected String asText(Tree node) {
        if (node.getChildCount() == 0)
            return node.getText();
        if (node.getChildCount() == 1) 
            return node.getChild(0).getText();
        throw new SpecFileParserException("node cannot be viewed as a text");
    }
    
    public final N visit(Tree tree) {
        try {
            final Node source = cast(tree);

            if (source.token == null)
                throw new SpecFileParserException("missing token type in the AST node; failed to parse correctly");

            switch (source.token.getType()) {
            case SPECFILE:
                assert source.getChildCount() >= 2;
                String mod = asText(source.getChild(0));
                String name = asText(source.getChild(1));
                List<String> paramTypes = new LinkedList<String>();
                int idxStart = 2; 
                if (source.getChild(2).getType() == PARAMS) {
                    paramTypes = visitParams(cast(source.getChild(2)));
                    idxStart = 3;
                }
                List<N> children = new LinkedList<N>();
                for (int i = idxStart; i < source.getChildCount(); i++)
                    children.add(visit(source.getChild(i)));
                return visitSpecFile(mod, name, paramTypes, children);
            case SPECFIELD:
                assert source.getChildCount() > 0;
                return visitSpecField(source);
            case FUNCFIELD:
                assert source.getChildCount() > 0;
                return visitSpecField(source);
            case INVARIANT:
                assert source.getChildCount() > 0;
                return visitInvariant(source);
            }
            
            throw new SpecFileParserException("unknown AST type");

        } catch (SpecFileParserException e) {
            // ensure that deepest token is saved
            if (e.token() == null) e.setToken(tree);
            throw e;            
        } catch (RuntimeException e) {
            // unknown exception
            throw new SpecFileParserException(e, tree);
        }
    }

    protected abstract N visitSpecFile(String modifiers, String name, List<String> paramTypes, List<N> children);
    protected abstract N visitSpecField(Node source);
    protected abstract N visitFuncField(Node n);
    protected abstract N visitInvariant(Node source);
    protected List<String> visitParams(Node paramsNode) {
        List<String> params = new LinkedList<String>();
        for (int i = 0; i < paramsNode.getChildCount(); i++)
            params.add(asText(paramsNode.getChild(i)));
        return params;
    }
    
}
