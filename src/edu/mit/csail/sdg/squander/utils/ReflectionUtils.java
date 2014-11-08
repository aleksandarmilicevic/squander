/*! \addtogroup Utils Utils 
 * This module contains various utility classes. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kodkod.util.collections.IdentityHashSet;

/**
 * 
 * @author Aleksandar Milicevic <aca.milicevic@gmail.com>
 */
public class ReflectionUtils {

    static Map<Class<?>, Map<String, Field>> cache = new HashMap<Class<?>, Map<String, Field>>();

    public static Field getFieldWithAccess(Object obj, String fieldName) {
        return getFieldWithAccess(obj.getClass(), fieldName);
    }

    public static Field getFieldWithAccess(Class<?> cls, String fieldName) {
        Field f = getField(cls, fieldName);
        f.setAccessible(true);
        return f;
    }

    /**
     * Gets field from class cls, either declared in cls or in one
     * of its superclasses
     *
     * @param cls - declaring class of the field
     * @param fieldName - name of the field
     * @return requested field
     */
    public static Field getField(Class<?> cls, String fieldName) {
        Field f = null;
        Map<String, Field> inner = cache.get(cls);
        if (inner != null) {
            f = inner.get(fieldName);
            if (f != null)
                return f;
        } else {
            inner = new HashMap<String, Field>();
            cache.put(cls, inner);
        }

        try {
            while (cls != null) {
                try {
                    f = cls.getDeclaredField(fieldName);
                    f.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    f = null;
                }
                if (f != null)
                    break;
                cls = cls.getSuperclass();
            }

        } catch (SecurityException e) {
            throw new RuntimeException("SecurityException", e);
        }
        if (f != null) {
            inner.put(fieldName, f);
        }
        return f;
    }

    @SuppressWarnings("unchecked")
    public static <R> R getFieldValue(Object obj, Field field) {
        try {
            boolean accesible = field.isAccessible();
            field.setAccessible(true);
            Object ret = field.get(obj);
            field.setAccessible(accesible);
            return (R) ret;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static <R> R getFieldValue(Object obj, String fieldName) {
        return (R) getFieldValue(obj, getField(obj.getClass(), fieldName));
    }

    public static void setFieldValue(Object obj, String field, Object value) {
        setFieldValue(obj, getField(obj.getClass(), field), value);
    }
    
    public static void setFieldValue(Object obj, Field field, Object value) {
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(accessible);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("IllegalArgumentException", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IllegalAccessExceptione", e);
        }
    }

    private static void getAllFieldsRecursive(Class<?> clz, List<Field> fldLst) {
        if (clz == null)
            return;
        if (clz.isPrimitive())
            return;
        for (Field f : clz.getDeclaredFields()) {
            f.setAccessible(true);
            fldLst.add(f);
        }
        if (clz.getSuperclass() == Object.class)
            return;
        else
            getAllFieldsRecursive(clz.getSuperclass(), fldLst);
    }

    /**
     * Returns all fields in this class 
     * (including the ones declared in super-classes).
     */
    public static Field[] getAllFields(Class<?> clz) {
        ArrayList<Field> fieldList = new ArrayList<Field>();
        getAllFieldsRecursive(clz, fieldList);
        if (fieldList.isEmpty())
            return new Field[0];
        else
            return fieldList.toArray(new Field[0]);
    }

    /**
     * Returns all non-static (instance) fields in this class 
     * (including the ones declared in super-classes).
     */
    public static Field[] getAllNonStaticFields(Class<?> clz) {
        List<Field> fList = new ArrayList<Field>();
        getAllFieldsRecursive(clz, fList);
        List<Field> nonStaticFields = new LinkedList<Field>();
        for (Field f : fList) {
            if ((f.getModifiers() & Modifier.STATIC) == 0) {
                nonStaticFields.add(f);
            }
        }
        if (nonStaticFields.isEmpty())
            return new Field[0];
        else
            return nonStaticFields.toArray(new Field[0]);
    }

    /**
     * Returns all non-static (instance) fields declared in this class 
     * (excluding the ones declared in super-classes).
     */
    public static Field[] getDeclaredNonStaticFields(Class<?> clz) {
        List<Field> nonStaticFields = new LinkedList<Field>();
        for (Field f : clz.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) {
                f.setAccessible(true);
                nonStaticFields.add(f);
            }
        }
        if (nonStaticFields.isEmpty())
            return new Field[0];
        else
            return nonStaticFields.toArray(new Field[0]);
    }

    private static void getAllMethodsRecursive(Class<?> clz, List<Method> methLst) {
        if (clz == null)
            return;
        if (clz.isPrimitive())
            return;
        for (Method f : clz.getDeclaredMethods()) {
            f.setAccessible(true);
            methLst.add(f);
        }
        getAllMethodsRecursive(clz.getSuperclass(), methLst);
    }
    
    public static Method[] getAllMethods(Class<?> clz) {
        ArrayList<Method> methList = new ArrayList<Method>();
        getAllMethodsRecursive(clz, methList);
        if (methList.isEmpty())
            return new Method[0];
        else
            return methList.toArray(new Method[0]);
    }
    
    /**
     * Gets method from class <code>clz</code> or any of its superclasses. If no method can be found
     * <code>NoSuchMethodException</code> is raised.
     *
     * @param clz - declaring class of the method
     * @param methodName - name of the method
     * @param methodArgs - method arguments
     * @return requested method
     * @throws NoSuchMethodException
     */
    public static Method getMethod(Class<? extends Object> clz,
            String methodName, Class<?>[] methodArgs) throws NoSuchMethodException {
        if (clz == null)
            throw new NoSuchMethodException(methodName + "(" + Arrays.toString(methodArgs) + ") method does not exist ");
        try {
            return clz.getDeclaredMethod(methodName, methodArgs);
        } catch (NoSuchMethodException e) {
            return getMethod(clz.getSuperclass(), methodName, methodArgs);
        }
    }
    
    public static Method findMethod(Class<?> clz, String methodName, Class<?>[] methodParams) throws NoSuchMethodException {
        Method methodFound = null;
        l: for (Method m : clz.getDeclaredMethods()) {
            if (!m.getName().equals(methodName))
                continue;
            if (m.getParameterTypes().length != methodParams.length)
                continue;
            for (int i = 0; i < m.getParameterTypes().length; i++) 
                if (!paramsMatch(m.getParameterTypes()[i], methodParams[i]))
                    continue l;
            if (methodFound != null)
                throw new RuntimeException("Multiple possibilities");
            methodFound = m;
        }
        if (methodFound != null)
            return methodFound;
        if (clz.getSuperclass() != null)
            return findMethod(clz.getSuperclass(), methodName, methodParams);
        else 
            throw new NoSuchMethodException(methodName + " (" + Arrays.toString(methodParams) + ") not found in " + clz.getName());
    }
    
    private static void getAllConstructorsRecursive(Class<?> clz, List<Constructor<?>> constrLst) {
        if (clz == null)
            return;
        if (clz.isPrimitive())
            return;
        for (Constructor<?> c : clz.getDeclaredConstructors()) {
            c.setAccessible(true);
            constrLst.add(c);
        }
        getAllConstructorsRecursive(clz.getSuperclass(), constrLst);
    }
    
    public static Constructor<?>[] getAllConstructors(Class<?> clz) {
        ArrayList<Constructor<?>> constrList = new ArrayList<Constructor<?>>();
        getAllConstructorsRecursive(clz, constrList);
        if (constrList.isEmpty())
            return new Constructor[0];
        else
            return constrList.toArray(new Constructor[0]);
    }
    
    private static boolean paramsMatch(Class<?> declared, Class<?> actual) {
        declared = box(declared);
        actual = box(actual);
        return declared.isAssignableFrom(actual);
    }

    public static Class<?> box(Class<?> c) {
        if (c == int.class)
            return Integer.class;
        if (c == boolean.class)
            return Boolean.class;
        if (c == float.class)
            return Float.class;
        if (c == double.class)
            return Double.class;
        if (c == long.class)
            return Long.class;
        if (c == byte.class)
            return Byte.class;
        if (c == char.class)
            return Character.class;
        return c;
    }

    /**
     * <p>Invokes method of a given obj and set of parameters</p>
     *
     * @param obj - Object for which the method is going to be executed. Special
     *          if obj is of type java.lang.Class, method is executed as static method
     *          of class given as obj parameter
     * @param method - name of the object
     * @param params - actual parameters for the method
     * @return - result of the method execution
     *
     * <p>If there is any problem with method invocation, a RuntimeException is thrown</p>
     */
    public static Object invoke(Object obj, String method, Class<?>[] params, Object[] args) {
        try {
            return invoke_ex(obj, method, params, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invoke_ex(Object obj, String method, Class<?>[] params, Object[] args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> clazz = null;
        if (obj instanceof Class<?>) {
            clazz = (Class<?>) obj;
        } else {
            clazz = obj.getClass();
        }
        Method methods = clazz.getDeclaredMethod(method, params);
        methods.setAccessible(true);
        if (obj instanceof Class<?>) {
            return methods.invoke(null, args);
        } else {
            return methods.invoke(obj, args);
        }
    }

    /**
     * <p>Creates object of the class clz using default constructor</p>
     * <p>If there is any problem during object construction, a RuntimeException is
     * thrown</p>
     * 
     * @param clz -the class of object
     * @return - created object
     */
    public static Object createObjectDefaultConstructor(Class<?> clz) {
        try {
            Constructor<?> constr = clz.getDeclaredConstructor(new Class<?>[0]);
            constr.setAccessible(true);
            Object ret = constr.newInstance(new Object[0]);
            constr.setAccessible(false);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("could not create on object of class " + clz.getName() + 
                    " using default constructor. Reason: " + e.getMessage());
        }
    }
    
    public static Object createNewArray(Class<?> componentType, int len) {
        return Array.newInstance(componentType, len);
    }
    
    public static <T, K> T newInstance(Class<T> clz, Class<K> argType, K arg) {
        try {
            Constructor<T> cnstr = clz.getDeclaredConstructor(argType);
            cnstr.setAccessible(true);
            return cnstr.newInstance(arg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T newInstance(Class<T> clz, Object[] args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++)
            argTypes[i] = args.getClass();
        return newInstance(clz, argTypes, args);
    }
    
    public static <T> T newInstance(Class<T> clz, Class<?>[] argTypes, Object[] args) {
        try {
            Constructor<T> cnstr = clz.getDeclaredConstructor(argTypes);
            cnstr.setAccessible(true);
            return cnstr.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T, K, V> T newInstance(Class<T> clz, Class<K> arg1Type, Class<V> arg2Type, K arg1, V arg2) {
        try {
            Constructor<T> cnstr = clz.getDeclaredConstructor(arg1Type, arg2Type);
            cnstr.setAccessible(true);
            return cnstr.newInstance(arg1, arg2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
   /**
    * <p>
    * Finds method in class owner that contains given annotation. There must be at
    * most one annotation of the given type in the class. If there are more methods 
    * that contain same annotation, RuntimeException is thrown. In this case, it would 
    * good to use {@link #getAnnotatedMethods(Class, Class)} method
    * 
    * @param owner
    * @param annotation
    * @return
    * @see #getAnnotatedMethods(Class, Class)
    */
    public static Method getAnnotatedMethod(Class<?> owner, Class<? extends Annotation> annotation) {
        Method[] methods = owner.getDeclaredMethods();
        Method ret = null;
        for (Method m : methods) {
            if (m.getAnnotation(annotation) != null)
                if (ret == null)
                    ret = m;
                else
                    throw new RuntimeException(
                            "There are multiple methods to check");
        }
        return ret;
    }
    
    public static Method [] getAnnotatedMethods(Class<?> owner, Class<? extends Annotation> annotation) {
        Method[] methods = owner.getDeclaredMethods();
        List<Method> ret = new LinkedList<Method>();
        for (Method m : methods) {
            if (m.getAnnotation(annotation) != null)
                ret.add(m);
        }
        return ret.toArray(new Method[0]);
        
    }

    public static boolean isPrimitive(Class<?> type) {
        if (type == null) return false;
        if (type.isPrimitive()) return true;
        if (Integer.class.equals(type) || Boolean.class.equals(type) || Byte.class.equals(type) || Character.class.equals(type) ||
                Double.class.equals(type) || Float.class.equals(type) || Long.class.equals(type) || Short.class.equals(type)) 
            return true;
        return false;
    }

    /**
     * Lowest common ancestor of two classes in the type lattice.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Class lca(Class clz1, Class clz2) {
        if (clz1.isAssignableFrom(clz2))
            return clz1;
        if (clz2.isAssignableFrom(clz1))
            return clz2;
        List<Class> workList = new LinkedList<Class>();
        workList.add(clz1);
        while (!workList.isEmpty()) {
            Class clz = workList.remove(0);
            if (clz2.isAssignableFrom(clz))
                return clz2;
            Class superClz = clz.getSuperclass();
            if (superClz != null) 
                workList.add(superClz);
            for (Class intfce : clz.getInterfaces()) 
                workList.add(intfce);
        }
        return Object.class;
    }
    
    /**
     * Returns method signature as string
     */
    public static String sig(Method method) {
        StringBuilder sig = new StringBuilder();
        sig.append(method.getDeclaringClass().getName()).append("::").append(method.getName());
        sig.append("(");
        for (Class<?> cls : method.getParameterTypes()) 
            sig.append(cls.getName()).append(",");
        sig.replace(sig.length() - 1, sig.length() - 1, ")");
        return sig.toString();
    }
    
    public static Collection<Class<?>> getImmParents(Class<?> clz) {
        List<Class<?>> parents = new ArrayList<Class<?>>();
        if (clz.getSuperclass() != null)
            parents.add(clz.getSuperclass());
        for (Class<?> intr : clz.getInterfaces())
            parents.add(intr);
        return parents;
    }
    
    public static Collection<Type> getImmGenericParents(Class<?> cls) {
        List<Type> supers = new ArrayList<Type>();
        if (cls.getGenericSuperclass() != null)
            supers.add(cls.getGenericSuperclass());
        for (Type t : cls.getGenericInterfaces()) 
            supers.add(t);
        return supers;
    }

    private static final String tab = "  ";
    
    public static String toString(Object obj) {
        StringBuilder sb = new StringBuilder();
        String indent = ""; 
        printObj(obj, sb, indent, new IdentityHashSet<Object>());
        return sb.toString();
    }

    private static void printObj(Object obj, StringBuilder sb, String indent, IdentityHashSet<Object> visited) {
        if (obj == null) {
            sb.append("null");
            return;
        }
        Class<? extends Object> cls = obj.getClass();
        if (cls == String.class || Utils.isPrimitive(cls)) {
            sb.append(obj.toString());
            return;
        }
        if (!visited.add(obj)) {
//            sb.append(obj.getClass().getName() + "@" + System.identityHashCode(obj));
            sb.append(obj.getClass().getName());
            return;
        }
//      sb.append(obj.getClass().getName() + "@" + System.identityHashCode(obj));
        sb.append(obj.getClass().getName());
        sb.append("\n");
        indent = indent + tab;
        for (Field f : getAllFields(cls)) {
            sb.append(indent).append(f.getName()).append(": ");
            printObj(getFieldValue(obj, f), sb, indent, visited);
            sb.append(";\n");
        }
    }

}
/*! @} */
