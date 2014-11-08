/*! \addtogroup Utils Utils 
 * This module contains various utility classes. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;

import forge.program.ForgeDomain;
import forge.program.ForgeProgram;
import forge.program.ForgeType;
import forge.program.ForgeVariable;
import forge.program.GlobalVariable;
import forge.program.InstanceDomain;
import forge.program.InstanceLiteral;
import forge.solve.BooleanAtom;
import forge.solve.ForgeConstant;
import forge.solve.IntegerAtom;

/**
 * Various static utility methods
 * 
 * @author Aleksandar Milicevic (aleks@csail.mit.edu)
 */
public class Utils {

    /**
     * Returns true if <code>cls.isPrimitive()</code> is true, or
     * if cls is equal to either Integer.class or Boolean.class
     */
    public static boolean isPrimitive(Class<?> cls) {
        if (cls.isPrimitive())
            return true;
        if (Number.class.isAssignableFrom(cls))
            return true;
        if (Boolean.class == cls)
            return true;
        return false;
    }

    /**
     * Linearly searches through all instance domain in order to find one
     * with the given name.
     * 
     * NOTE: there should probably exist a better (and more efficient) way to do this.
     */
    public static InstanceDomain findDomain(final Class<?> cls, final ForgeProgram program) {
        if (cls == null)
            return null;
        String clsName = cls.getName();
        if (cls.isArray())
            clsName = cls.getComponentType().getName() + "[]";
        for (final InstanceDomain d : program.instanceDomains()) {
            if (clsName.equals(d.name()))
                return d;
        }
        for (Class<?> s : ReflectionUtils.getImmParents(cls)) {
            InstanceDomain idom = findDomain(s, program);
            if (idom != null)
                return idom;
        }
        return null;
    }

    /**
     * Linearly searches through all global variables in order to find one
     * with the given name.
     * 
     * NOTE: there should probably exist a better (and more efficient) way to do this.
     */
    public static GlobalVariable findGlobalVar(String varName, ForgeProgram program) {
        if (varName == null)
            return null;
        for (final GlobalVariable v : program.globalVariables()) {
            if (varName.equals(v.name()))
                return v;
        }
        return null;
    }
    
    /**
     * Linearly searches through all instance literals in order to find one
     * with the given name.
     * 
     * NOTE: there should probably exist a better (and more efficient) way to do this.
     */
    public static InstanceLiteral findLiteral(final String litName, final ForgeProgram program) {
        if (litName == null)
            return null;
        for (final InstanceLiteral l : program.instanceLiterals()) {
            if (litName.equals(l.name()))
                return l;
        }
        return null;
    }

    /**
     * Checks whether the given global variable is array and if it is
     * returns its array domain, otherwise returns null.
     */
    public static ForgeDomain getArrayDomain(ForgeVariable g) {
        ForgeType type = g.type();
        Set<? extends forge.program.ForgeType.Tuple> tupleTypes = type.tupleTypes();
        for (forge.program.ForgeType.Tuple t : tupleTypes) {
            ForgeDomain d;
            if (t.domains().size() == 1) {
                d = t.domains().get(0);
            } else { 
                d = t.domains().get(1);
            }
            if (!d.name().endsWith("[]"))
                continue;
            return d;
        }
        return null;
    }
    
    public static String getArrayType(final ForgeVariable var) {
        forge.program.ForgeType.Tuple t = var.type().tupleTypes().iterator().next();
        if (var.arity() == 1) {
            t = t.domains().get(0);
        } else  if (var.arity() >= 2) {
            t = t.domains().get(1);
        }
        InstanceDomain instanceDomain = (InstanceDomain) t;
        String clsName = instanceDomain.name();
        if (!clsName.endsWith("[]"))
            throw new RuntimeException("Return type is not an array type: " + clsName);
        return clsName.substring(0, clsName.length() - 2);
    }

    
    public static Class<?> classForName(final String clsName) throws ClassNotFoundException {
        if ("int".equals(clsName))
            return int.class;
        if ("boolean".equals(clsName))
            return boolean.class;
        return Class.forName(clsName);
    }

//    /**
//     * Finds the method in the given class with the given name and parameter types
//     */
//    public static SootMethod findSootMethod(final SootClass clazz,
//            final String methodName, final Class<?>[] parameterTypes) {
//        SootClass c = clazz;
//        while (c != null) {
//            for (final SootMethod method : c.getMethods()) {
//                if (method.getName().equals(methodName)) {
//                    // check whether the parameters match
//                    boolean ok = true;
//                    for (int index = 0; index < parameterTypes.length; index++) {
//                        Class<?> paramType = parameterTypes[index];
//                        String paramName;
//                        if (paramType.isArray())
//                            paramName = paramType.getCanonicalName();
//                        else 
//                            paramName = paramType.getName();
//                        if (index >= method.getParameterCount() 
//                                || !method.getParameterType(index).toString().equals(paramName)) {
//                            ok = false;
//                            break;
//                        }
//                    }
//                    if (ok) 
//                        return method;
//                }
//            }
//            c = c.getSuperclass();
//        }
//        return null;
//    }

    /**
     * Returns all annotations on the given method
     */
    // TODO: Squander should be fetching annotations through the central mechanism that also examines auxiliary files
    public static Annotation[] getMethodAnnotations(String className, String methodName, Class<?>[] parameterTypes) {
        try  {
            Class<?> cls = Class.forName(className);
            if ("<init>".equals(methodName)) {
                Constructor<?> c = cls.getDeclaredConstructor(parameterTypes);
                c.setAccessible(true);
                return c.getAnnotations();
            } else {
                try {
                    // look for method declared exactly in this class
                    final Method m = cls.getDeclaredMethod(methodName, parameterTypes);
                    m.setAccessible(true);
                    return m.getAnnotations();
                } catch (final NoSuchMethodException e) {
                    // look for a public method declared in some super-class
                    final Method m = cls.getMethod(methodName, parameterTypes);
                    m.setAccessible(true);
                    return m.getAnnotations();
                }
            }
        } catch (NoSuchMethodException e) {
            String msg = String.format("couldn't find a method named %s.%s to extract annotations from", className, methodName);
            throw new RuntimeException(msg, e);
        } catch (Exception e) { 
            throw new RuntimeException("error getting annotations", e);
        }
    }

    public static boolean boolValue(ForgeConstant fConst) {
        if (fConst.tuples().size() == 0)
            return false;
        assert fConst instanceof BooleanAtom : "must be a BooleanAtom in order to extract a boolean value from it; fc = " + fConst;
        return ((BooleanAtom) fConst).value();
    }

    public static int intValue(ForgeConstant fConst) {
        assert fConst instanceof IntegerAtom : "must be an IntegerAtom in order to extract an int value from it; fc = " + fConst;
        return ((IntegerAtom) fConst).value();
    }

    public static String getMethodParamName(Method method, int i) {
        Paranamer pn = new BytecodeReadingParanamer();
        method.setAccessible(true);
        try { 
            String[] paramNames = pn.lookupParameterNames(method, true);
            return paramNames[i];
        } catch (Exception e) {
            return "@arg(" + i + ")";
        }
    }

    public static void writeToFile(String text, String fileName) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
            bos.write(text.getBytes());
            bos.flush();
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } 
    }

}
/*! @} */
