/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */

package edu.mit.csail.sdg.squander.spec;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.mit.csail.sdg.squander.parser.JFSLParserException;
import edu.mit.csail.sdg.squander.spec.typeerrors.ArityMismatchException;
import edu.mit.csail.sdg.squander.utils.ReflectionUtils;


public interface JType {

    /** A unary type has arity 1 */
    public boolean isUnary();

    /** Arity of a type */
    public int arity();

    public String name();
    public String simpleName();
    
    /** Domain of a type is its leftmost type in the tuple */
    public JType.Unary domain();

    /** Range of a type is its rightmost type in the tuple */
    public JType.Unary range();

    /** All atoms in the tuple */
    public List<JType.Unary> tuple();
    
    /** Projection of a type onto its columns */
    public JType projection(int... columns);
    
    /** Projection of a type onto its columns */
    public JType.Unary projection(int column);
    public JType projectionFrom(int start);
    public JType projectionFromTo(int start, int end);

    /** Transpose of a tuple type */
    public JType transpose();

    /**
     * x :- (A_1, \ldots, A_i), y :- (B_1, \ldots, B_i) 
     * => x + y :- (LCA(A_j,B_j)) (j=1,\ldots,i)
     */
    public JType union(JType type);

    /**
     * x :- (A_j), y :- (B_j) 
     * => x - y :- (A_j) 
     */
    public JType difference(JType type);

    /**
     * x :- (A_1, \ldots, A_i), y :- (B_1, \ldots, B_i) 
     * => x - y :- (A_j)
     */
    public JType intersection(JType type);

    /**
     * x :- (A_1, A_2, \ldots, A_i), y :- (B_1, B_2, \ldots, B_j) 
     * => x -> y :- (A_1, A_2, \ldots, A_i, B_1, \ldots, B_j) 
     */
    public JType product(JType that);

    /**
     * x :- (A_1, A_2, \ldots, A_i), y :- (B_1, B_2, \ldots, B_j)
     * => x.y :- (A_1, A_2, \ldots, A_{i-1}, B_2, \ldots, B_j)
     */
    public JType join(JType type);

    /**
     * Common types
     */

    public boolean isInteger();
    public boolean isBoolean();
    public boolean isSubtypeOf(JType that);

    // ======================================================================
    // -------------------------- JType.Factory -----------------------------
    // ======================================================================
    
    @SuppressWarnings("rawtypes")
    public static final class Factory {
        public static final Factory instance = new Factory(); 
        
        private Factory() {}

        public JType newJType(List<JType.Unary> tuple) {
            assert tuple.size() > 0; 
            if (tuple.size() == 1)
                return tuple.get(0);
            else
                return new JType.Tuple(tuple);
        }

        public JType newJType(Class<?> ... classes) {
            List<JType.Unary> tuple = new LinkedList<Unary>();
            for (Class<?> cls : classes) 
                tuple.add(newJType(cls));
            return newJType(tuple);
        }

        public JType.Unary newJType(Class cls) {
            Unary[] typeParams = new Unary[cls.getTypeParameters().length];
            for (int i = 0; i < typeParams.length; i++)
                typeParams[i] = JType.Factory.instance.newJType(Object.class);
            return newJType(cls, typeParams);
        }
        
        public JType.Unary newJType(Class cls, Class[] typeParams) {
            if (typeParams == null || typeParams.length == 0)
                return newJType(cls);
            JType.Unary[] jtypeParams = new JType.Unary[typeParams.length]; 
            for (int i = 0; i < typeParams.length; i++)
                jtypeParams[i] = JField.convertToJType(typeParams[i], null);
            return newJType(cls, jtypeParams);
        }
        
        public JType.Unary newJType(Class cls, JType.Unary[] typeParams) {
            if (typeParams == null || typeParams.length != cls.getTypeParameters().length)
                return newJType(cls);
            else 
                return new JType.Unary(cls, typeParams);
        }

        public JType.Unary integerType()   { return newJType(int.class); }
        public JType.Unary booleanType()   { return newJType(boolean.class); }
        public JType.Unary stringType()    { return newJType(String.class); }
        public JType.Unary throwableType() { return newJType(Throwable.class); }
        public JType.Unary objectType()    { return newJType(Object.class); }

    }
    
    // ======================================================================
    // --------------------------- AbstractJType ----------------------------
    // ======================================================================
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static abstract class AbstractJType implements JType {
        protected static final JType.Factory factory = Factory.instance;
        
        @Override public final JType difference(JType type)    { return difference(this, type); }
        @Override public final JType intersection(JType type)  { return intersection(this, type); }
        @Override public final JType join(JType type)          { return join(this, type); }
        @Override public final JType product(JType that)       { return product(this, that); }        
        @Override public final JType union(JType type)         { return union(this, type); }
        @Override public final boolean isSubtypeOf(JType that) { return isSubtypeof(this, that); }
        
        @Override
        public String name() {
            String name = "[";
            for (int i = 0; i < arity(); i++) {
                if (i > 0)
                    name += ", ";
                name += tuple().get(i).name();
            }
            name += "]";
            return name;
        }
        
        @Override
        public String simpleName() {
            String name = "[";
            for (int i = 0; i < arity(); i++) {
                if (i > 0)
                    name += ", ";
                name += tuple().get(i).simpleName();
            }
            name += "]";
            return name;
        }
        
        @Override
        public String toString() {
            return simpleName();
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JType))
                return false;
            JType that = (JType) obj;
            return this.name().equals(that.name());
        }

        @Override
        public final int hashCode() {
            return name().hashCode();
        }

        private static JType difference(JType lhs, JType rhs) {
            checkArity(lhs, rhs);
            return lhs;
        }
        
        private static JType intersection(JType lhs, JType rhs) {
            return lhs;
        }

        private static JType join(JType lhs, JType rhs) {
            if (lhs.arity() == 0 || rhs.arity() == 0) 
                throw new JFSLParserException("empty type join not allowed");
            
            List<JType.Unary> result = new ArrayList<JType.Unary>();
            for (int i = 0; i < lhs.arity() - 1; i++)
                result.add(lhs.projection(i));
            for (int i = 1; i < rhs.arity(); i++)
                result.add(rhs.projection(i));

            String msg = String.format("invalid join: %s . %s", lhs, rhs);
            Unary lhsJoinType = lhs.projection(lhs.arity() - 1);
            Unary rhsJoinType = rhs.projection(0);
            assert lhsJoinType.isSubtypeOf(rhsJoinType) || rhsJoinType.isSubtypeOf(lhsJoinType) : msg;
                
            return factory.newJType(result);
        }

        private static JType product(JType lhs, JType rhs) {
            List<JType.Unary> result = new ArrayList<Unary>(lhs.arity() + rhs.arity());
            result.addAll(lhs.tuple());
            result.addAll(rhs.tuple());
            return factory.newJType(result);
        }

        private static JType union(JType lhs, JType rhs) {
            return lca(lhs, rhs);
        }
        
        private static boolean isSubtypeof(JType lhs, JType rhs) {
            if (lhs.arity() != rhs.arity())
                return false;
            for (int i = 0; i < rhs.arity(); i++) {
                Class thisCls = lhs.projection(i).clazz();
                Class thatCls = rhs.projection(i).clazz();
                if (!thatCls.isAssignableFrom(thisCls))
                    return false;
            }
            return true;
        }
        
        private static JType lca(JType a, JType b) {
            checkArity(a, b);
            List<JType.Unary> result = new ArrayList<JType.Unary>(a.arity());
            for (int i = 0; i < a.arity(); i++) {
                Class lcaCls = ReflectionUtils.lca(a.projection(i).clazz(), b.projection(i).clazz());
                result.add(factory.newJType(lcaCls, a.projection(i).typeParams()));
            }
            return factory.newJType(result);
        }

        private static void checkArity(JType a, JType b) {
            assert a != null && b != null;
            if (a.arity() != b.arity()) 
                throw new ArityMismatchException(a.arity(), b.arity(), "arities don't match in: \"" + a + "\" and \"" + b + "\"");
        }
        
    }
    
    // ======================================================================
    // --------------------------- JType.Unary ------------------------------
    // ======================================================================
    
    @SuppressWarnings("rawtypes")
    public static final class Unary extends AbstractJType {
        private final Class<?> clz;
        private JType.Unary[] typeParams = new JType.Unary[0];
        
        private Unary(Class type, JType.Unary[] jTypes) {
            if (type == Integer.class)
                type = int.class;
            else if (type == Boolean.class)
                type = boolean.class;
            this.clz = type;
            this.typeParams = jTypes;
        }

        private Unary(Class type) {
            this(type, new JType.Unary[0]);
        }
        
        public Class clazz()                   { return clz; }
        public Unary[] typeParams()            { return Arrays.copyOf(typeParams, typeParams.length); }
        void setTypeParams(Unary[] typeParams) { this.typeParams = typeParams; }
        
        public Unary mkArray() {
            Class<?> arrCls = Array.newInstance(clz, 0).getClass();
            return JType.Factory.instance.newJType(arrCls);
        }
        
        @Override public List<Unary> tuple()  { return Collections.singletonList(this); } 
        @Override public int arity()          { return 1; }
        @Override public boolean isUnary()    { return true; }
        @Override public JType transpose()    { return this; }
        @Override public JType.Unary range()  { return this; }
        @Override public JType.Unary domain() { return this; }
        
        @Override public boolean isBoolean()  { return clz == boolean.class; }
        @Override public boolean isInteger()  { return clz == int.class; }
        
        @Override
        public JType projection(int... columns) {
            assert columns.length == 1 && columns[0] == 0;
            return this;
        }

        @Override
        public Unary projection(int column) {
            assert column == 0;
            return this;
        }
        
        @Override
        public JType projectionFrom(int start) {
            assert start == 0;
            return this;
        }

        @Override
        public JType projectionFromTo(int start, int end) {
            assert start == 0;
            assert end == 0; 
            return this;
        }

        public boolean isAssignableFrom(JType.Unary that) {
            if (that.clz == Null.class)
                return true;
            Class<?> c1 = ReflectionUtils.box(clz);
            Class<?> c2 = ReflectionUtils.box(that.clz);
            if (!c1.isAssignableFrom(c2))
                return false;
            if (typeParams.length != that.typeParams.length)
                return true;
            for (int i = 0; i < typeParams.length; i++) 
                if (!typeParams[i].isAssignableFrom(that.typeParams[i]))
                    return false;
            return true;
        }
        
        public String name() {
            String name = clz.getName();
            if (typeParams.length > 0) {
                name += "<";
                for (int i = 0; i < typeParams.length; i++) {
                    if (i > 0)
                        name += ", ";
                    name += typeParams[i].name();
                }
                name += ">";
            }
            return name;
        }
        
        public String simpleName() {
            String name = clz.getSimpleName();
            if (typeParams.length > 0) {
                name += "<";
                for (int i = 0; i < typeParams.length; i++) {
                    if (i > 0)
                        name += ", ";
                    name += typeParams[i].simpleName();
                }
                name += ">";
            }
            return name;
        }
        
        @Override
        public String toString() {
            return simpleName();
        }

    }
    
    // ======================================================================
    // --------------------------- JType.Unary ------------------------------
    // ======================================================================
    
    public static final class Tuple extends AbstractJType {

        private final JType.Unary[] tuple;
        
        private Tuple(List<JType.Unary> tuple) {
            assert tuple.size() > 1;
            this.tuple = new JType.Unary[tuple.size()];
            for (int i = 0; i < tuple.size(); i++)
                this.tuple[i] = tuple.get(i);
        }
        
        @Override public List<Unary> tuple()  { return Arrays.asList(tuple); }
        @Override public int arity()          { return tuple.length; }
        @Override public boolean isUnary()    { return false; }
        @Override public JType transpose()    { 
            List<Unary> lst = Arrays.asList(tuple);
            Collections.reverse(lst);
            return factory.newJType(lst); 
        }
        @Override public JType.Unary range()  { return tuple[arity() - 1]; }
        @Override public JType.Unary domain() { return tuple[0]; }
        
        @Override public boolean isBoolean() { return false; }
        @Override public boolean isInteger() { return false; }
        
        @Override
        public JType projection(int... columns) {
            List<Unary> result = new ArrayList<Unary>(columns.length);
            for (int col : columns) {
                result.add(projection(col));
            }
            return factory.newJType(result);
        }

        @Override
        public Unary projection(int column) {
            assert column >=0 && column < arity();
            return tuple[column];
        }

        @Override
        public JType projectionFrom(int start) {
            return projectionFromTo(start, arity() - 1);
        }

        @Override
        public JType projectionFromTo(int start, int end) {
            int[] cols = new int[end - start + 1]; 
            for (int i = start; i <= end; i++) 
                cols[i - start] = i;
            return projection(cols);
        }
        
        
        
    }

}
/*! @} */
