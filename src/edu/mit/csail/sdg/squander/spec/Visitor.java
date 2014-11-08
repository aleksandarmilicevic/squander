/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import static edu.mit.csail.sdg.squander.parser.JFSLParser.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

import edu.mit.csail.sdg.squander.parser.JFSLParser;
import edu.mit.csail.sdg.squander.parser.JFSLParserException;
import edu.mit.csail.sdg.squander.parser.JFSLParser.Node;
import edu.mit.csail.sdg.squander.spec.typeerrors.TypeCheckException;


/**
 * Abstract visitor of unparsed specification expressions.
 * 
 * @author kuat
 * 
 * @param <N>
 *            type of the output value
 * @param <M>
 *            type of the input value, presumably a context of evaluation
 */
public abstract class Visitor<N, M> {

    public static final String LAMBDA_VAR_NAME = "_";
    
    /** 
     * Invoking a visitor method based on the type of the tree nodes.
     */
    public
    final N visit(M env, Tree tree) {        
        try {
            final Node source = cast(tree);

            if (source.token == null)
                throw new JFSLParserException("missing token type in the AST node; failed to parse correctly");

            final N result;

            switch (source.token.getType()) {
            /** Top-level expression */
            case DECLARATION:
                assert source.getChildCount() >= 3;
                Node frame = source.getChildCount() >= 4 ? cast(source.getChild(3)) : null;
                if (frame.getType() == JFSLParser.NULL)
                    frame = null;
                Node constraint = source.getChildCount() >= 5 ? cast(source.getChild(4)) : null;
                result = visitFieldDeclaration(env, cast(source.getChild(0)), source.getChild(1).getType(), cast(source.getChild(2)), frame, constraint);
                break;
            case FRAME:            
                final List<N> locations = new ArrayList<N>();
                final List<Node> fields = new ArrayList<Node>();
                final List<N> selectors = new ArrayList<N>();
                final List<N> uppers = new ArrayList<N>();
                final List<Node> filters = new ArrayList<Node>();

                for (int i = 0; i < source.getChildCount(); i++) {
                    final Tree frameNode = source.getChild(i);
                    assert frameNode.getType() == FRAME_LOCATION;                                    
                    int nChildren = frameNode.getChildCount();
                    Node filter = null;
                    N upper = null;
                    for (int k = 0; k < nChildren; k++) {
                        Node n = cast(frameNode.getChild(k));
                        switch (n.token.getType()) {
                        case FRAME_FIELD:
                            FrameMemento<N> fr = processFrameField(env, n);
                            locations.add(fr.location);
                            fields.add(fr.field);
                            selectors.add(fr.selector);
                            upper = fr.upper;
                            break;
                        case FRAME_FILTER:
                            filter = n;
                            break;
                        case FRAME_DOMAIN:
                            upper = visit(env, child(n));
                            break;
                        default:    
                            throw new JFSLParserException("Illegal node type inside FRAME_LOCATION: " + frameNode.toStringTree());
                        }
                    }
                    filters.add(filter);
                    uppers.add(upper);
                }
                                
                result = visitFrame(env, locations, fields, selectors, uppers, filters);
                break;
                
            /** Composite expression */
            case BINARY:
                result = visitBinary(env, source, source.getChild(0).getType(), cast(source.getChild(1)),
                        cast(source.getChild(2)));
                break;
            case UNARY:
                result = visitUnary(env, source, source.getChild(0).getType(), cast(source.getChild(1)));
                break;
            case QUANTIFY: {
                // declarations
                Node decls = cast(source.getChild(1));
                
                final List<String> names = new ArrayList<String>();
                final List<String> mults = new ArrayList<String>();
                final List<Node> sets = new ArrayList<Node>();

                int num = decls.getChildCount();
                int mod = MOD_NONE;
                if (decls.token.getType() == DECLS) {
                    assert num % 3 == 0;
                    int count = num / 3;
                    assert count > 0;

                    // populate
                    for (int i = 0; i < count; i++) {
                        names.add(decls.getChild(3 * i).getText());
                        mults.add(decls.getChild(3 * i + 1).getText());                    
                        sets.add(cast(decls.getChild(3 * i + 2)));
                    }
                } else if (decls.token.getType() == MDECLS) {
                    assert num > 4;
                    mod = cast(decls.getChild(0)).token.getType();
                    String mult = decls.getChild(num - 2).getText();
                    Node set = cast(decls.getChild(num - 1));
                    Node prevset = null;
                    for (int i = 1; i < num - 2; i++) {
                        String name = decls.getChild(i).getText();
                        names.add(name);
                        mults.add(mult);
                        Node sset;
                        if (mod == MOD_DISJ && i > 1) {
                            Token t = new CommonToken(BINARY, "BINARY");
                            Node n = new Node(t);
                            n.addChild(new Node(new CommonToken(OP_DIFFERENCE, "OP_DIFFERENCE")));
                            n.addChild(prevset);
                            Node id = new Node(new CommonToken(IDENTIFIER, "IDENTIFIER"));
                            id.setDecision(Decision.LOCAL);
                            id.addChild(decls.getChild(i-1));
                            n.addChild(id);
                            sset = n;
                        } else {
                            sset = set;
                        }
                        prevset = sset;
                        sets.add(sset);
                    }
                }

                result = visitQuantification(env, source.getChild(0).getType(), mod, names, mults, sets, cast(source.getChild(2)));
                break;
            } case OP_SET_COMPREHENSION_ENUM: {
                List<Node> exprs = new ArrayList<Node>(source.getChildCount());
                for (int i = 0; i < source.getChildCount(); i++)
                    exprs.add(cast(source.getChild(i)));
                result = visitComprehensionEnum(env, exprs);
                break;
            } case CONDITIONAL:
                result = visitConditional(env, 
                        cast(source.getChild(0)), 
                        cast(source.getChild(1)), 
                        cast(source.getChild(2)));
                break;
            case CHAIN:
                result = visitChain(env, children(source));                
                break;
            case OLD:
                result = visitOld(env, child(source));
                break;
            case AMBIGUOUS:
                result = visitAmbiguous(env, children(source));
                break;
            case IDENTIFIER:
                result = visitName(env, source);
                break;
            case CAST:
                result = visitCastExpression(env, cast(source.getChild(0)), cast(source.getChild(1)));
                break;
            case FIELD:
                result = visitFieldRelation(env, cast(source.getChild(0)), cast(source.getChild(1)));
                break;
            case TYPE_INT: case TYPE_BYTE: case TYPE_SHORT: case TYPE_LONG: case TYPE_CHAR:
                result = visitIntegralType(env);
                break;
            case TYPE_BOOLEAN:
                result = visitBooleanType(env);
                break;
            case TYPE_ARRAY:
                result = visitArrayType(env, child(source));
                break;
            case TYPE_REF:
                result = visitRefType(env, source, children(source));
                break;
            case CLASS_DESIGNATOR:
                assert false;
            case CALL:
                // primary expression method call
                result = visitMethodCall(env, null, source.getChild(0).getText(), cast(source.getChild(1)));
                break;
            case ARGUMENT:
                result = visitArgument(env, parseInt(source.getChild(0)));
                break;

            /** Simple expressions */
            case LAMBDA_VAR:
                result = visitLambda(env);
                break;
            case THIS_VAR:
                result = visitThis(env);
                break;
            case SUPER_VAR:
                result = visitSuper(env);
                break;
            case RETURN_VAR:
                result = visitReturn(env);
                break;
            case THROW_VAR:
                result = visitThrow(env);
                break;
            case LIT_TRUE:
                result = visitTrue(env);
                break;
            case LIT_FALSE:
                result = visitFalse(env);
                break;
            case LIT_NULL:
                result = visitNull(env);
                break;
            case DecimalLiteral:
                result = visitDecimal(env, parseInt(source));
                break;
            case StringLiteral:
                result = visitString(env, source.getText());
                break;
            default:
                result = null;
            }

            // validate the result of parsing
            validate(source, result);
            return result;
        } catch (JFSLParserException e) {
            // ensure that deepest token is saved
            if (e.token() == null) e.setToken(tree);
            throw e;   
        } catch (RuntimeException e) {
            // unknown exception
            throw new JFSLParserException(e, tree);
        }
    }
    
    private static class FrameMemento<N> {
        N location;
        N selector;
        N upper;
        Node field;
    }
    
    //TODO: simplify syntax for this
    private FrameMemento<N> processFrameField(M env, Tree frameNode) {
        FrameMemento<N> ret = new FrameMemento<N>();
        List<Node> trailingBrackets = new LinkedList<Node>();
        int nChildren = frameNode.getChildCount();
        for (int idx = nChildren - 1; idx > 0; idx--) {
            Node node = cast(frameNode.getChild(idx));
            if (node.getType() != BRACKET)
                break;
            trailingBrackets.add(0, node);
        }
        
        nChildren -= trailingBrackets.size();
        
        // process instance selector if exists
        if (!trailingBrackets.isEmpty()) {
            Node selNode = trailingBrackets.remove(0);
            if (selNode.getChildCount() > 0)
                ret.selector = visit(env, child(selNode));
        }
        
        // process upper bound if exists
        if (!trailingBrackets.isEmpty()) {
            Node up = trailingBrackets.remove(0);
            if (up.getChildCount() > 0)
                ret.upper = visit(env, child(up));
        }
        
        if (!trailingBrackets.isEmpty())
            throw new JFSLParserException("too many trailing brackets");
        
        List<Node> join = new ArrayList<Node>();
        for (int j = 0; j < nChildren - 1; j++)
            join.add(cast(frameNode.getChild(j)));
                            
        ret.location = visitChain(env, join);
        Node relation = cast(frameNode.getChild(nChildren - 1));
        if (relation.getType() == FRAME_ALL) 
            ret.field = relation;
        else if (relation.getType() == JOIN) 
            ret.field = child(relation);
        else
            throw new JFSLParserException("wrong selector in frame condition");
        
        return ret;
    }

    /** Process chain expression in EBNF */
    private final N visitChain(M env, List<Node> children) {
        // resolve primary expression
        N result = visit(env, children.get(0));        
        
        // resolve selector expressions
        for (int i = 1; i < children.size(); i++) {
            Node child = children.get(i);
    
            switch (child.getType()) {
            case PROJECTION: 
                int[] indexes = new int[child.getChildCount()];
                for (int k = 0; k < indexes.length; k++) {
                    indexes[k] = parseInt(child.getChild(k));
                }
                result = visitProjection(env, result, indexes);
                break;
            case BRACKET:
                result = visitBracket(env, result, child);
                break;
            case JOIN:
                result = visitJoin(env, result, child);
                break;
            case JOIN_REFLEXIVE:
                result = visitJoinReflexive(env, result, child);
                break;
            case CALL:
                result = visitMethodCall(env, result, child.getChild(0).getText(), cast(child.getChild(1)));
                break;
            default:
                return null;
            }            
        }
        
        return result;
    }

    /** Validate the result of a visit */
    protected void validate(Node node, N result) {
        if (result == null) 
            throw new TypeCheckException("not implemented or failed to resolve: " + node.toStringTree());
    }
    
    // top-level expressions
    
    protected N visitFieldDeclaration(M env, Node ident, int op, Node set, Node frame, Node constraint) {return null;}
    protected N visitFrame(M env, List<N> joins, List<Node> fields, List<N> selectors, List<N> uppers, List<Node> filters) {return null;}

    // composite expressions

    protected N visitBinary(M env, Node tree, int op, Node leftTree, Node rightTree) {return null;}
    protected N visitUnary(M env, Node tree, int op, Node expr) {return null;}
    protected N visitQuantification(M env, int op, int quantIdMod, List<String> names, List<String> mults, List<Node> sets, Node expr) {return null;}
    protected N visitComprehensionEnum(M env, List<Node> exprs) { return null; }
    protected N visitConditional(M env, Node condTree, Node leftTree, Node rightTree) {return null;}
    protected N visitOld(M env, Node sub) {return null;}
    protected N visitName(M env, Node tree) {return null;}
    protected N visitAmbiguous(M env, List<Node> idents) {return null;}    
    protected N visitCastExpression(M env, Node type, Node sub) {return null;}
    protected N visitFieldRelation(M env, Node type, Node ident) {return null;}
    
    // selectors
    
    protected N visitBracket(M env, N primary, Node selector) {return null;}
    protected N visitProjection(M env, N result, int[] indexes) {return null;}
    protected N visitJoin(M env, N primary, Node selector) {return null;}
    protected N visitJoinReflexive(M env, N primary, Node selector) {return null;}
    protected N visitMethodCall(M env, N receiver, String id, Node arguments) {return null;}
    
    
    // leaf expressions

    protected N visitLambda(M env) {return null;}
    protected N visitThis(M env) {return null;}
    protected N visitSuper(M env) {return null;}
    protected N visitReturn(M env) {return null;}
    protected N visitThrow(M env) {return null;}
    protected N visitDecimal(M env, int i) {return null;}
    protected N visitString(M env, String s) {return null;}
    protected N visitTrue(M env) {return null;}
    protected N visitFalse(M env) {return null;}
    protected N visitNull(M env) {return null;}
    protected N visitArgument(M env, int i) {return null;}

    // type expressions
    
    protected N visitIntegralType(M env) {return null;}
    protected N visitBooleanType(M env) {return null;}
    protected N visitArrayType(M env, Node base) {return null;}
    protected N visitRefType(M env, Node source, List<Node> idents) {return null;}
     
    // helpers

    protected List<Node> children(Tree node) {
        List<Node> lst = new ArrayList<Node>();
        for (int i = 0; i < node.getChildCount(); i++) {
            lst.add(cast(node.getChild(i)));
        }
        return lst;
    }

    /** Performs casting */
    protected Node cast(Tree tree) {
        if (!(tree instanceof Node)) 
            throw new JFSLParserException("malformed node in the AST: " + tree);
        return (Node) tree;
    }
    
    /** Get a single child */
    protected Node child(Node parent) {
        return cast(parent.getChild(0));
    }

    /** Returns the text content of a token node */
    protected String asText(Tree node) {
        if (node.getChildCount() != 1) 
            throw new JFSLParserException("node cannot be viewed as a text");
        return node.getChild(0).getText();
    }

    /** Assumes the content is a decimal expression text */
    public static int parseInt(Tree tree) {
        try {
            return Integer.parseInt(tree.getText());
        } catch (NumberFormatException e) {
            throw new JFSLParserException("malformed decimal expression: " + tree.toStringTree());
        }
    }
    
}
/*! @} */
