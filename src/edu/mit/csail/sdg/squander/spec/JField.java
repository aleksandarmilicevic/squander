/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.mit.csail.sdg.squander.spec.ClassSpec.Invariant;
import edu.mit.csail.sdg.squander.spec.JType.Factory;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import forge.program.ForgeExpression;


/** 
 * @author kuat
 * @author Aleksandar Milicevic
 */
public final class JField {
    
    private static final Factory factory = JType.Factory.instance;

    private final JType type;
    private final JType.Unary owningType;
    private final JType.Unary declaringType;
    private final String name;
    private final Field javaField;
    
    // these are set only for spec fields 
    
    private ForgeExpression domain; 
    private Invariant absFun;
    private Invariant bound;
    private Frame frame;
    private boolean funcFlag = false; 
    
    public static JField newSpecField(String name, Class<?> owner, Class<?> declarer, JType type) {
        Unary declaringType = factory.newJType(declarer);
        Unary owningType = factory.newJType(owner);
        return newSpecField(name, owningType, declaringType, type);
    }
    
    public static JField newSpecField(String name, Unary owningType, Unary declaringType, JType type) {
        return new JField(name, type, owningType, declaringType, null);
    }

    @SuppressWarnings("rawtypes")
    public static JField newJavaField(Field f) {
        LinkedHashMap<TypeVariable, Unary> bindings = new LinkedHashMap<TypeVariable, JType.Unary>();
        for (TypeVariable v : f.getDeclaringClass().getTypeParameters()) {
            bindings.put(v, JType.Factory.instance.newJType(Object.class));
        }
        return newJavaField(f, bindings );
    }
    
    @SuppressWarnings("rawtypes")
    public static JField newJavaField(Field f, LinkedHashMap<TypeVariable, Unary> typeVarBindings) {
        String name = f.getName(); 
//        Unary[] typeParams = getTypeParams(f.getGenericType(), typeVarBindings);
//        Unary type = factory.newJType(f.getType(), typeParams);
        Unary type = convertToJType(f.getGenericType(), typeVarBindings);
        Unary declaringType = factory.newJType(f.getDeclaringClass(), typeVarBindings.values().toArray(new Unary[0]));
        return new JField(name, type, declaringType, declaringType, f);
    }
    
    JField(String name, JType type, Unary owningType, Unary declaringType, Field javaField) {
        this.name = name;
        this.type = type;
        this.owningType = owningType;
        this.declaringType = declaringType;
        this.javaField = javaField;
    }
            
    public String name()               { return name; }
    public JType type()                { return type; }
    public JType.Unary owningType()    { return owningType; }
    public JType.Unary declaringType() { return declaringType; }
    public Field getJavaField()        { return javaField; }
    public boolean isStatic()          { return javaField != null && (javaField.getModifiers() & Modifier.STATIC) != 0; }
    public boolean isSpec()            { return javaField == null; }
    public boolean isFunc()            { return isSpec() && funcFlag; }
    public boolean isPureAbstract()    { return absFun.expr.equals(absFun.expr.program().trueLiteral()); }
    
    public ForgeExpression getDomain()         { return domain; }
    public Invariant getAbsFun()               { return absFun; }
    public Invariant getBound()                { return bound; }
    public Frame getFrame()                    { return frame; }
    
    public void setDomain(ForgeExpression dom) { this.domain = dom; }
    public void setAbsFun(Invariant absFun)    { this.absFun = absFun; }
    public void setBound(Invariant bound)      { this.bound = bound; }
    public void setFrame(Frame frame)          { this.frame = frame; }
    public void setFuncFlag(boolean flag)      { this.funcFlag = flag; }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof JField))
            return false;
        return this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public String fullName() {
        return owningType.simpleName() + "__" + name;
    }
    
    public Unary[][] getTypeParams() {
//        if (javaField != null) {
//            Unary[] tp = getTypeParams(javaField.getGenericType());
//            return new Unary[][] { tp };
//        } else {
            Unary[][] tp = new Unary[type.arity()][];
            for (int i = 0; i < type.arity(); i++) {
                tp[i] = type.projection(i).typeParams();
            }
            return tp;
//        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isSpec()) sb.append("spec ");
        if (isStatic()) sb.append("static ");
        sb.append(declaringType).append("::");
        sb.append(name).append(" ");
        sb.append(type);
        return sb.toString();
    }
    
    @SuppressWarnings("rawtypes")
    public static Unary[] getTypeParams(Type genericType) {
        return getTypeParams(genericType, new LinkedHashMap<TypeVariable, JType.Unary>());
    }
    
    @SuppressWarnings("rawtypes")
    public static Unary[] getTypeParams(Type t, Map<TypeVariable, Unary> typeVarBindings) {
        return convertToJType(t, typeVarBindings).typeParams();
    }
    
    @SuppressWarnings("rawtypes")
    public static Unary convertToJType(Type t, Map<TypeVariable, Unary> typeVarBindings) {
        JType.Unary jtype = null;
        if (t instanceof Class<?>) { 
            Class<?> cls = (Class<?>) t;
            jtype = JType.Factory.instance.newJType(cls);
            return jtype;
        } else if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            Type[] actualTypeArgs = pt.getActualTypeArguments();
            Unary[] typeParams = new Unary[actualTypeArgs.length];
            int idx = 0;
            for (Type actType : actualTypeArgs) {
                typeParams[idx++] = convertToJType(actType, typeVarBindings);
            }
            Class<?> rawType = (Class<?>) ((ParameterizedType) t).getRawType();
            jtype = JType.Factory.instance.newJType(rawType, typeParams);
            return jtype;
        } else if (t instanceof TypeVariable<?>) {
            if (typeVarBindings != null) {
                Unary result = typeVarBindings.get(t);
                if (result != null)
                    return result;
            }
            return JType.Factory.instance.newJType(Object.class);
        } else if (t instanceof WildcardType) {
            return JType.Factory.instance.newJType(Object.class);
        } else if (t instanceof GenericArrayType) {
            Type compType = ((GenericArrayType) t).getGenericComponentType();
            Unary jCompType = convertToJType(compType, typeVarBindings);
            Class arrClass = Array.newInstance(jCompType.clazz(), 0).getClass();
            return JType.Factory.instance.newJType(arrClass, new Unary[] { jCompType });
        } else {
            throw new RuntimeException("don't know how to convert " + t.getClass().getName() + " to JType");
        }
    }
    
//    @SuppressWarnings("rawtypes")
//    public static Unary[] getTypeParams(Type genericType, Map<TypeVariable, Unary> typeVarBindings) {
//        if (genericType instanceof ParameterizedType) {
//            ParameterizedType pt = (ParameterizedType) genericType;
//            Type[] actualTypeArgs = pt.getActualTypeArguments();
//            Unary[] typeParams = new Unary[actualTypeArgs.length];
//            int idx = 0;
//            for (Type t : actualTypeArgs) {
//                typeParams[idx++] = convertToJType(t, typeVarBindings);
//            }
//            return typeParams;
//        }
//        return null;
//    }
//    
//    @SuppressWarnings("rawtypes")
//    public static Unary convertToJType(Type t, Map<TypeVariable, Unary> typeVarBindings) {
//        JType.Unary jtype = cache.get(t);
//        if (jtype != null)
//            return jtype;
//        if (t instanceof Class<?>) { 
//            Class<?> cls = (Class<?>) t;
//            jtype = JType.Factory.instance.newJType(cls);
//            cache.put(t, jtype);
//            return jtype;
//        } else if (t instanceof ParameterizedType) {
//            Unary[] typeParams = getTypeParams(t, typeVarBindings);
//            Class<?> rawType = (Class<?>) ((ParameterizedType) t).getRawType();
//            jtype = JType.Factory.instance.newJType(rawType, typeParams);
//            cache.put(t, jtype);
//            return jtype;
//        } else if (t instanceof TypeVariable<?>) {
//            if (typeVarBindings != null) {
//                Unary result = typeVarBindings.get(t);
//                if (result != null)
//                    return result;
//            }
//            return JType.Factory.instance.newJType(Object.class);
//        } else if (t instanceof WildcardType) {
//            return JType.Factory.instance.newJType(Object.class);
//        } else {
//            throw new RuntimeException("don't know how to convert " + t.getClass().getName() + " to JType");
//        }
//    }

}
/*! @} */
