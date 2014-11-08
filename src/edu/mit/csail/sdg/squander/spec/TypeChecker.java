/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import static edu.mit.csail.sdg.squander.parser.JFSLParser.FRAME_ALL;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_AND;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_BIT_AND_OR_INTERSECTION;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_BIT_NOT_OR_TRANSPOSE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_BIT_OR;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_BIT_XOR;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_CLOSURE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_DIFFERENCE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_DIVIDE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_EQ;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_EQUIV;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_GEQ;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_GT;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_IMPLIES;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_INTERSECTION;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_LEQ;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_LT;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_MINUS;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_MINUS_OR_DIFFERENCE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_MOD;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_NEQ;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_NEQUIV;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_NOT;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_NSET_SUBSET;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_OR;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_PLUS;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_PLUS_OR_UNION;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_RANGE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_RELATIONAL_COMPOSE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_RELATIONAL_OVERRIDE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_ALL;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_COMPREHENSION;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_EXISTS;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_LONE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_NO;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_NUM;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_ONE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_SOME;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_SUBSET;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SET_SUM;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SHL;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_SHR;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_TIMES;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_TRANSPOSE;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_UNION;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_USHR;
import static edu.mit.csail.sdg.squander.parser.JFSLParser.OP_XOR;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.tree.Tree;

import edu.mit.csail.sdg.squander.parser.JFSLParser;
import edu.mit.csail.sdg.squander.parser.JFSLParser.Decision;
import edu.mit.csail.sdg.squander.parser.JFSLParser.Node;
import edu.mit.csail.sdg.squander.parser.JFSLParserException;
import edu.mit.csail.sdg.squander.spec.JType.Factory;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import edu.mit.csail.sdg.squander.spec.constant.ConstRel;
import edu.mit.csail.sdg.squander.spec.typeerrors.ArityMismatchException;
import edu.mit.csail.sdg.squander.spec.typeerrors.IncompatibleTypesException;
import edu.mit.csail.sdg.squander.spec.typeerrors.TypeCheckException;
import edu.mit.csail.sdg.squander.spec.typeerrors.UnresolvedStringException;
import edu.mit.csail.sdg.squander.utils.ReflectionUtils;


/**
 * Analysis that tries to determine the type of expressions in the expression
 * tree. In addition, it resolves all name identifiers to corresponding fields,
 * determines the meaning of syntactically ambiguous expressions (which may
 * depend on semantic type information.)
 * 
 * <p>
 * <strong>The analysis runs without any knowledge of relevant sub types of any
 * given Java type (unlike subsequent translation phase.)</strong> The goal is
 * to determine the scope of specification expressions in terms of elements of a 
 * Java program. The scope includes:
 * <ol>
 * <li> all relevant types (most importantly the type of fields) that are directly or
 * indirectly referenced in the source;;
 * <li> all relevant concrete and abstract fields;
 * </ol>
 * 
 * <p>
 * The analysis mutates the tree by augmenting nodes with type information that is reused
 * in translation. 
 * 
 * <p>
 * Type inference is performed by the rules described in the corresponding
 * comment sections. The rules follow Java guide lines of static type checking
 * in combination of Alloy-style higher-arity types and set operations on types.
 *
 * @author kuat
 * @author Aleksandar Milicevic
 */
public class TypeChecker extends Visitor<JType, NameSpace> {

    protected final JavaScene scene; 
    
    /**
     * should be non-null only when type-checking spec fields, in which cases the value of 
     * this field should the declaring class of the spec field. 
     */
    private ClassSpec clsSpec;

    /**
     * @param scene
     * @param clsSpec
     *            <b>should be non-null only when type-checking spec fields, in
     *            which cases the value of this field should the declaring class
     *            of the spec field.</b>
     */
    public TypeChecker(JavaScene scene) {
        this.scene = scene;
    }

    /**
     * 
     * @param <b>should be non-null only when type-checking spec fields, in
     *            which cases the value of this field should the declaring class
     *            of the spec field.</b>
     */
    public void setClsSpecForSpecField(ClassSpec clsSpec) {
        this.clsSpec = clsSpec;
    }

    @Override
    protected void validate(Node tree, JType type) {
        super.validate(tree, type);
        tree.setJType(type);
    }

    // ===================================================
    // Type checking
    // ===================================================

    protected void checkSubtype(String expr, JType sub, JType supr) throws TypeCheckException {
        checkArity(expr, sub, supr); 
        for (int i = 0; i < sub.arity(); i++) {
            Unary t1 = supr.projection(i);
            Unary t2 = sub.projection(i);
            if (!t1.isAssignableFrom(t2) && !t2.isAssignableFrom(t1))
                throw new IncompatibleTypesException(sub, supr, i, expr);
        }
    }
    
    protected void checkJoin(JType primary, JType rest, Tree token) throws TypeCheckException {
        JType.Unary t1 = primary.projection(primary.arity() - 1);
        JType.Unary t2 = rest.projection(0);
        if (!t1.isAssignableFrom(t2) && !t2.isAssignableFrom(t1)) {
            TypeCheckException e = new IncompatibleTypesException(t1, t2, -1, null);
            e.setToken(token);
            throw e;
        }
    }
    
    protected void checkArity(String inExpr, JType ... expressions) throws TypeCheckException {
        int i = -1;
        for (JType expr : expressions) {
            if (i == -1)
                i = expr.arity();
            else if (i != expr.arity())
                throw new ArityMismatchException(i, expr.arity(), Arrays.toString(expressions) + "\nin expression " + inExpr);            
        }
    }

    protected void checkBoolean(JType expr, Node node) throws TypeCheckException {
        if (!expr.isBoolean())
            throw new TypeCheckException("expected a boolean expression, actual: " + expr, 
                    node.toStringTree());         
    }

    protected void checkInteger(JType expr, Node node) throws TypeCheckException {
        if (!expr.isInteger())
            throw new TypeCheckException("expected an integer expression, actual: " + expr, 
                    node.toStringTree());         
    }
    
    protected void checkUnary(JType expr, Node node) throws TypeCheckException {
//        if (expr.arity() != 1) 
//            throw new TypeCheckException("expected a unary type, actual: " + expr, 
//                    node.toStringTree());
    }
    
    /**
     * Implementation of a visitor
     */

    @Override
    protected JType visitArgument(NameSpace env, int i) {
        JMethod root = env.method();
        if (root == null) throw new TypeCheckException("no parameters in the context"); 
        if (i >= 0 && i < root.paramTypes().size())
            return root.paramTypes().get(i);
        else
            throw new TypeCheckException("procedure has no " + i + "th argument"); 
    }

    @Override
    protected JType visitBracket(NameSpace env, JType primary, Node tree) {
        final Node selector = child(tree);
        final JType expr = visit(env, selector);        
        
        env = env.addScope(primary.range());
        
        // flag is true, elems is implicit
        if (primary.arity() == 1 && env.inArray()) {
            tree.setFlag(true);
            checkInteger(expr, selector);
            return Factory.instance.newJType(env.scope().domain().clazz().getComponentType());
        } else if (primary.arity() == 1 && env.inList()) {
            tree.setFlag(true);
            checkInteger(expr, selector);
            Unary domain = env.scope().domain();
            if (domain.typeParams().length > 0)
                return domain.typeParams()[0];
            else
                return JType.Factory.instance.objectType();
        } else if (primary.arity() == 1 && env.inMap()) {
            tree.setFlag(true);
            Unary domain = env.scope().domain();
            JType tp0 = domain.typeParams().length > 1 ? domain.typeParams()[0] : JType.Factory.instance.objectType();
            checkSubtype("", expr, tp0);
            if (domain.typeParams().length > 1)
                return domain.typeParams()[1];
            else
                return JType.Factory.instance.objectType();
        } else {
            tree.setFlag(false);
            return expr.join(primary);
        }            
    }
    
    @Override
    protected JType visitProjection(NameSpace env, JType primary, int[] indexes) {
        return primary.projection(indexes);
    }

    @Override
    protected JType visitJoin(NameSpace env, JType primary, Node tree) {
        JType rest = visit(env.addScope(primary.range()), child(tree));
        checkJoin(primary, rest, child(tree));
        return primary.join(rest);
    }
    
    @Override
    protected JType visitJoinReflexive(NameSpace env, JType primary, Node tree) {
        final JType relation = visit(env.addScope(primary.range()), child(tree));
        
        if (relation.arity() != 2)
            throw new TypeCheckException("reflexive transitive join expects a relation of arity 2", 
                    tree.toStringTree());
        
        return primary.union(primary.join(relation)); 
    }
    
    @Override
    protected JType visitMethodCall(NameSpace env, JType receiver, String name, Node arguments) {
        throw new RuntimeException("method calls not supported");
    }

    @Override
    protected JType visitBinary(NameSpace env, Node tree, int op, Node leftTree, Node rightTree) {        
        final JType result;
        final JType left = visit(env, leftTree);
        final JType right = visit(env, rightTree);
        
        final String expr = leftTree.toStringTree() + op + rightTree.toStringTree(); // TODO

        switch (op) {
        case OP_SHL:
        case OP_SHR:
        case OP_USHR:
        case OP_PLUS:
        case OP_MINUS:
        case OP_TIMES:
        case OP_DIVIDE:
        case OP_MOD:
        case OP_BIT_XOR:
        case OP_BIT_OR:
        case OP_RANGE:
            checkInteger(left, leftTree);
            checkInteger(right, rightTree);
            result = Factory.instance.integerType();            
            break;

        case OP_LT:
        case OP_GT:
        case OP_LEQ:
        case OP_GEQ:
            checkInteger(left, leftTree);
            checkInteger(right, rightTree);
            result = Factory.instance.booleanType();
            break;

        case OP_OR:
        case OP_AND:
        case OP_EQUIV:
        case OP_NEQUIV:
        case OP_IMPLIES:
        case OP_XOR:
            checkBoolean(left, leftTree);
            checkBoolean(right, rightTree);
            result = Factory.instance.booleanType();
            break;

        case OP_EQ:
        case OP_NEQ:
            if (leftTree.getToken().getType() == JFSLParser.BINARY &&
                cast(leftTree.getChild(0)).getToken().getType() == op) {
                JType leftRight = visit(env, leftTree.getChild(2));
                checkSubtype(expr, right, leftRight);
                result = Factory.instance.booleanType();
            } else {
                checkSubtype(expr, right, left); 
                result = Factory.instance.booleanType();
            }
            break;
        case OP_SET_SUBSET:
        case OP_NSET_SUBSET:
            checkSubtype(expr, left, right);
            result = Factory.instance.booleanType();
            break;

        case OP_RELATIONAL_OVERRIDE:
            result = left.union(right);
            break;            
        case OP_RELATIONAL_COMPOSE:
            result = left.product(right);
            break;

        case OP_INTERSECTION:
            result = left.intersection(right);
            break;
        case OP_UNION:
            result = left.union(right);
            break;
        case OP_DIFFERENCE:
            result = left.difference(right);
            break;

        case OP_PLUS_OR_UNION:
            if (left.isInteger() && right.isInteger())  
                tree.setFlag(true);
            else 
                tree.setFlag(false);
            result = tree.flag() ? Factory.instance.integerType() : left.union(right);
            break;
        case OP_MINUS_OR_DIFFERENCE:
            if (left.isInteger() && right.isInteger())  
                tree.setFlag(true);
            else 
                tree.setFlag(false);
            result = tree.flag() ? Factory.instance.integerType() : left.difference(right);
            break;
        case OP_BIT_AND_OR_INTERSECTION:
            if (left.isInteger() && right.isInteger())  
                tree.setFlag(true);
            else 
                tree.setFlag(false);
            result = tree.flag() ? Factory.instance.integerType() : left.intersection(right);
            break;
        default:
            result = null;
        }

        return result;
    }

    @Override
    protected JType visitConditional(NameSpace env, Node condTree, Node leftTree, Node rightTree) {
        JType cond = visit(env, condTree);
        JType left = visit(env, leftTree);
        JType right = visit(env, rightTree);        
        checkBoolean(cond, condTree);                
        return left.union(right);
    }

    @Override
    protected JType visitDecimal(NameSpace env, int i) {
        return Factory.instance.integerType();
    }
    
    @Override
    protected JType visitString(NameSpace env, String s) {
        return Factory.instance.newJType(String.class);
    }

    @Override
    protected JType visitFalse(NameSpace env) {
        return Factory.instance.booleanType();
    }

    @Override
    protected JType visitNull(NameSpace env) {
        return Factory.instance.newJType(Null.class);
    }

    @Override
    protected JType visitOld(NameSpace env, Node sub) {
        return visit(env, sub);
    }

    @Override
    protected JType visitQuantification(NameSpace env, int op, int mod, List<String> names, List<String> mults, List<Node> sets, Node expr) {
        final List<JType> decls = new ArrayList<JType>();
        for (int i = 0; i < names.size(); i++) {
            JType type = visit(env, sets.get(i));
            decls.add(type);
            checkUnary(type, sets.get(i));
            env = env.addLocal(names.get(i), type);
        }
        
        switch (op) {
        case OP_SET_ALL:
        case OP_SET_EXISTS:
        case OP_SET_SOME:
        case OP_SET_ONE:
        case OP_SET_LONE:
        case OP_SET_NO:
            checkBoolean(visit(env, expr), expr);
            return Factory.instance.booleanType();
        case OP_SET_NUM:
            checkBoolean(visit(env, expr), expr);
            return Factory.instance.integerType();
        case OP_SET_SUM:
            checkInteger(visit(env, expr), expr);
            return Factory.instance.integerType();
        case OP_SET_COMPREHENSION:    
            checkBoolean(visit(env, expr), expr);
            JType type = decls.get(0);
            for (int i = 1; i < decls.size(); i++)
                type = type.product(decls.get(i));
            return type;
        default:
            return null;
        }
    }

    @Override
    protected JType visitComprehensionEnum(NameSpace env, List<Node> exprs) {
        JType retType = null;
        for (Node n : exprs) {
            JType jtype = visit(env, n);
            if (retType == null) {
                retType = jtype; 
            } else {
                retType = retType.union(jtype);
            }
        }
        return retType;
    }

    @Override
    protected JType visitReturn(NameSpace env) {
        return env.method().returnType();
    }
    
    @Override
    protected JType visitThrow(NameSpace env) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JType visitSuper(NameSpace env) {
      throw new UnsupportedOperationException();
    }
    
    @Override
    protected JType visitLambda(NameSpace env) {
        JType type = env.findLocal(LAMBDA_VAR_NAME);
        if (type == null) 
            throw new TypeCheckException("no lambda variable in the context");
        return type;
    }

    @Override
    protected JType visitThis(NameSpace env) {
        JType type = env.declarer();
        JMethod m = env.method();
        if (m != null && m.isStatic()) 
            throw new TypeCheckException("no \"this\" variable in the context");
        return type;
    }

    @Override
    protected JType visitTrue(NameSpace env) {
        return Factory.instance.booleanType();
    }

    @Override
    protected JType visitUnary(NameSpace env, Node tree, int op, Node expr) {
        final JType sub = visit(env, expr);
        final JType result;

        switch (op) {
        case OP_PLUS:
        case OP_MINUS:
            checkInteger(sub, expr);
            result = sub;
            break;
        case OP_NOT:
            checkBoolean(sub, expr);
            result = sub;
            break;        

        case OP_SET_SOME:
        case OP_SET_NO:
        case OP_SET_ONE:
        case OP_SET_LONE:
            result = Factory.instance.booleanType();
            break;
        case OP_SET_NUM:
            this.scene.addNumType(sub);
            result = Factory.instance.integerType();
            break;

        case OP_SET_SUM:
            checkInteger(sub, expr);
            result = Factory.instance.integerType();
            break;

        case OP_CLOSURE:
            result = sub;
            break;                        
        case OP_TRANSPOSE:            
            result = sub.transpose();
            break;

        case OP_BIT_NOT_OR_TRANSPOSE:
            if (sub.isInteger()) 
                tree.setFlag(true);
            else
                tree.setFlag(false);
            result = tree.flag() ? Factory.instance.integerType() : sub.transpose();
            break;
        default:        
            result = null;
        }

        return result;
    }

    @Override
    protected JType visitFieldDeclaration(NameSpace env, Node ident, int op, Node set, Node frame, Node constraint) {
        // Declaration
        JType declType = visit(env, set);
        String fldName = asText(ident);
        if (clsSpec != null) {
            clsSpec.addSpecField(JField.newSpecField(fldName, getFldOwner(fldName), clsSpec.jtype(), declType));
        }
        
        // Frame of a spec field
        if (frame != null) visit(env, frame);

        // Constraint
        if (constraint != null) checkBoolean(visit(env, constraint), constraint);
        
        // dummy type
        return Factory.instance.booleanType();
    }

    private JType.Unary getFldOwner(String fldName) {
        Unary declarer = clsSpec.jtype();
        // see if one of the super classes declares a field with the same name
        List<Class<?>> parents = new LinkedList<Class<?>>();
        parents.addAll(ReflectionUtils.getImmParents(clsSpec.clz()));
        while (!parents.isEmpty()) {
            Class<?> p = parents.remove(0);
            JType.Unary jp = Factory.instance.newJType(p, clsSpec.typeParams());
            if (scene.ensureClass(jp).hasSpecField(fldName))
                declarer = jp;
            parents.addAll(ReflectionUtils.getImmParents(p));
        }
        return declarer;
    }

    @Override
    protected JType visitCastExpression(NameSpace env, Node type, Node sub) {
        visit(env, sub);
        return visit(env, type);
    }

    @Override
    protected JType visitFieldRelation(NameSpace env, Node type, Node ident) {
        final JType expr = visit(env, type);
        final JType result = resolveField(env.addScope(expr.range()), ident);        
        return result;
    }

    @Override
    protected JType visitFrame(NameSpace env, List<JType> joins, List<Node> fields, List<JType> selectors, List<JType> uppers, List<Node> filters) {
        for (int i = 0; i < joins.size(); i++) {
            final Node field = fields.get(i);            
            final JType type = joins.get(i);
            if (type.arity() != 1) 
                throw new TypeCheckException("frame location LHS expression has arity greater than 1: " + type + " " + field.toStringTree());
            
            if (field.getType() == FRAME_ALL) {
                field.setJType(type);
            } else {
                NameSpace env2 = env.addScope(type.range());
                JType selector = resolveField(env2, field);
                if (selector == null)
                    throw new TypeCheckException("frame location selector cannot be resolved");
                JType sub = selectors.get(i);
                if (sub != null) {
                    if (!sub.isSubtypeOf(selector.domain()))
                        throw new TypeCheckException("frame instance selector should be of type "
                                + selector.domain() + "; actual instance selector type: " + sub);
                }
                JType upper = uppers.get(i);
                if (upper != null) {
                    JType dom = selector.projectionFromTo(1, selector.arity() - 1);
                    if (!upper.isSubtypeOf(dom))
                        throw new TypeCheckException("frame upper bound should be of type "
                                + dom + "; actual instance selector type: " + upper);
                }
                Node filter = filters.get(i);
                if (filter != null) {
                    JType filterType = visit(env.addLocal(LAMBDA_VAR_NAME, selector), child(filter));
                    checkBoolean(filterType, filter);
                }
            }
        }                
        return Factory.instance.booleanType();
    }
    
    @Override
    protected JType visitArrayType(NameSpace env, Node base) {
        final JType sub = visit(env, base);
        checkUnary(sub, base);
        return Factory.instance.newJType(Array.newInstance(sub.domain().clazz(), 0).getClass());
    }

    @Override
    protected JType visitBooleanType(NameSpace env) {
        return Factory.instance.booleanType();
    }

    @Override
    protected JType visitIntegralType(NameSpace env) {
        return Factory.instance.integerType();
    }

    @Override
    protected JType visitRefType(NameSpace env, Node source, List<Node> idents) {
        return visitAmbiguous(env, idents);
//        StringBuilder sb = new StringBuilder();
//        boolean first = true;
//        Unary[] typeParams = null;
//        for (Node node : idents) {
//            if (node.getType() == JFSLParser.TYPE_PARAMETERS) {
//                typeParams = visitTypeParams(env, node);
//            } else {
//                if (first)
//                    first = false;
//                else 
//                    sb.append('.');
//                sb.append(asText(node));    
//            }
//        }
//        JType t = resolveType(env, sb.toString());
//        if (t == null)
//            return null;
//        if (typeParams != null) {
//            t.range().setTypeParams(typeParams);
//        }
//        scene.ensureClass(t.range());
//        return t;
    }
    
    
    /**
     *    This part is somewhat tricky due to ambiguity between package names, type names,
     * and field dereferences. The best strategy that we may follow is to consult JLS 3.0.
     * Refer to section 6.5 for details of classification of ambiguous names.
     *
     *  Ambiguous qualified names A.n where n is a simple name are classified as follows:
     *  - if A is a package name, then check if n is a type declared in the package, in which case
     *  reclassify as type name; otherwise, leave as a package name
     *  - if A is a type name, then check if n is a field of A; else check if n is a member type of A; else
     *  report an error
     *  - if A is an expression, then let T be its type. If n is a field of T, classify as an expression, else
     *  if n is a member type, classify of a type name, else report an error
     *  
     */
    @Override
    public JType visitAmbiguous(NameSpace env, List<Node> idents) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(asText(idents.get(0)));
        JType primary = visitName(env, idents.get(0));

        // package fragment
        if (primary == null) { 
            while (primary == null && i < idents.size() - 1) {
                i++;
                idents.get(i).setDecision(Decision.FRAGMENT);
                sb.append(".").append(asText(idents.get(i))); 
                primary = resolveType(env, sb.toString());
            }
            if (primary != null) {
                idents.get(0).setDecision(Decision.TYPE);
            } else {
                UnresolvedStringException e = new UnresolvedStringException(sb.toString());
                e.setToken(idents.get(0));
                throw e;
            }
        }
        
        idents.get(0).jtype = primary;
        if (i < idents.size() - 1 && idents.get(i+1).token.getType() == JFSLParser.TYPE_PARAMETERS) {
            Node nextChild = idents.get(i+1);
            JType.Unary[] typeParams = visitTypeParams(env, nextChild);
            idents.get(0).jtype = setTypeParams(primary, typeParams);
            nextChild.setDecision(Decision.FRAGMENT);
            i++;
        }
        scene.ensureClass(idents.get(0).jtype.range());
        
        // join with the selector
        if (i == idents.size() - 1)
            return idents.get(0).jtype;
        else {            
            // continue as in join
            NameSpace extended = env.addScope(primary.range()); 
            List<Node> rest = idents.subList(i + 1, idents.size());
            JType selector = visitAmbiguous(extended, rest);
            try {
                if ((rest.get(0).field != null && rest.get(0).field.isStatic()) 
                        || (rest.get(0).decision() == Decision.ENUM)) {
                    idents.get(0).setDecision(Decision.FRAGMENT);
                    return selector;
                } else
                    return primary.join(selector);
            } catch (AssertionError e) {
                String msg = e.getMessage();
                msg = msg + "\nlhs = " + idents.get(0).toStringTree() + "\nrhs = " + print(rest); 
                JFSLParserException ex = new JFSLParserException(msg, e);
                throw ex;
            }
        }
        
    }

    private String print(List<Node> rest) {
        String ret = "";
        for (Node n : rest) {
            ret = ret + n.toStringTree() + " JOIN ";
        }
        return ret.substring(0, ret.length() - 6);
    }

    private JType setTypeParams(JType primary, JType.Unary[] typeParams) {
        List<Unary> result = new ArrayList<Unary>(primary.arity());
        for (int i = 0; i < primary.arity(); i++) {
            Unary t = primary.projection(i);
            if (i == primary.arity() - 1)
                result.add(Factory.instance.newJType(t.clazz(), typeParams));
            else 
                result.add(t);
        }
        return Factory.instance.newJType(result);
    }

    private JType.Unary[] visitTypeParams(NameSpace env, Node node) {
        List<Node> children = children(node);
        JType.Unary[] result = new JType.Unary[children.size()];
        int idx = 0;
        for (Node n : children) {
            result[idx++] = (Unary) visit(env, n);
        }
        return result;
    }

    @Override
    public JType visitName(NameSpace env, Node ident) {
        String text = asText(ident);
        
        // check if it is a local 
        JType localType = env.findLocal(text);
        if (localType != null) {
            ident.setDecision(Decision.LOCAL);
            return localType;
        }
        
        // check if it is an enum constant
        // NOTE: it is important to do this before 'resolveField', because
        //       Java thinks that enum constants are fields of the declaring enum
        JType enumType = resolveEnum(env, ident);
        if (enumType != null)
            return enumType;
        
        // check if it is a field dereference
        JType fieldType = resolveField(env, ident);
        if (fieldType != null)
            return fieldType;
        
        // check if it is a type 
        JType type = resolveType(env, text);
        if (type != null) {
            assert type.isUnary();
            ident.setDecision(Decision.TYPE);
            return type;
        }
        
        // check if it is a constant relation
        ConstRel constRel = scene.findConstRel(text);
        if (constRel != null) {
            ident.setDecision(Decision.CONST);
            return constRel.type();
        }

        return null;     
    }
    
    /** 
     * Checks whether the node is an enum constant. 
     * Enum constants must always be preceded by the enum class name and a dot.
     */    
    private JType resolveEnum(NameSpace env, Node tree) {
        String text = asText(tree);
        try {
            Class<?> clz = env.scope().clazz();
            if (clz.isEnum()) {
                for (Object e : clz.getEnumConstants()) {
                    if (((Enum<?>)e).name().equals(text)) {
                        tree.setDecision(Decision.ENUM);
                        tree.enumConst = (Enum<?>) e;
                        return env.scope();
                    }
                }
            }
        } catch (NullPointerException e) {}
        return null;
    }
    
    /** 
     * Checks whether the node is a field (length and elems are considered fields.)
     */
    private JType resolveField(NameSpace env, Node tree) {
        String text = asText(tree);
        JField field = env.findField(text, scene);
        if (field != null) {
            tree.setDecision(Decision.GLOBAL);
            tree.field = field;

            JType value = field.type();
            if (field.isStatic())
                return value;
            else
                return field.declaringType().product(value);
        }
        return null;
    }
    
    private JType resolveType(NameSpace env, String name) {
        if (env.declarer() == null || env.declarer().arity() > 1)
            return null;
        JType.Unary parent = env.declarer().domain();
        Class<?> candidate;

        // check if it is a type name        
        // ... its own package
        candidate = getClass(name);
        // ... in the same package
        if (candidate == null && parent.clazz().getPackage() != null)
            candidate = getClass(parent.clazz().getPackage().getName() + "." + name); 
        // ... inner class
        if (candidate == null)
            candidate = getClass(parent.clazz().getName() + "$" + name);
        // ... inner class in supper class
        Class<?> sc = parent.clazz().getSuperclass();
        while (candidate == null && sc != null) {
            candidate = getClass(sc.getName() + "$" + name);
            sc = sc.getSuperclass();
        }
        // ... the same as parent
        if (candidate == null)
            if (parent.clazz().getSimpleName().equals(name))
                candidate = parent.clazz();
        // ... parallel 
        if (candidate == null)
            if (parent.clazz().getDeclaringClass() != null)
                candidate = getClass(parent.clazz().getDeclaringClass().getName() + "$" + name);
        // ... in the default import
        if (candidate == null)
            candidate = getClass("java.lang." + name);
        // ... check if exists in the java scene
        if (candidate == null) {
            JType.Unary t = scene.findJTypeForClassSimpleName(name);
            if (t != null)
                return t;            
        }
        
        if (candidate != null) {
            //scene.ensureClass(candidate); 
            return Factory.instance.newJType(candidate);
        }

        // reclassified as a package name fragment
        return null;
    }

    private Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
}
/*! @} */
