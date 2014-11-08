/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.lang.reflect.Method;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mit.csail.sdg.squander.spec.JType.Factory;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import edu.mit.csail.sdg.squander.utils.Utils;


import static edu.mit.csail.sdg.squander.spec.JField.getTypeParams;

public class JMethod {

    private final String methodName; 
    private final JType.Unary declaringClass;
    private final Map<String, JType.Unary> params;
    private final JType.Unary returnType;
    private final boolean isStatic;
    
    private MethodSpec spec;

    public JMethod(String methodName, Class<?> declaringClass, Map<String, Class<?>> params, 
            Class<?> returnType, boolean isStatic) {
        this(methodName, Factory.instance.newJType(declaringClass), convertMap(params), 
                Factory.instance.newJType(returnType), isStatic);
    }
    
    private static Map<String, Unary> convertMap(Map<String, Class<?>> map) {
        Map<String, JType.Unary> result = new LinkedHashMap<String, Unary>();
        for (Entry<String, Class<?>> e : map.entrySet()) {
            result.put(e.getKey(), Factory.instance.newJType(e.getValue()));
        }
        return result;
    }

    public JMethod(String methodName, JType.Unary declaringClass, Map<String, JType.Unary> params, 
            JType.Unary returnType, boolean isStatic) {
        this.methodName = methodName;
        this.declaringClass = declaringClass;
        this.params = params;
        this.returnType = returnType;
        this.isStatic = isStatic;
    }

    public String name()                   { return methodName; }
    public Unary declaringClass()          { return declaringClass; }
    public Map<String, Unary> params()     { return params; }
    public List<Unary> paramTypes()        { return new ArrayList<Unary>(params.values()); }
    public Unary returnType()              { return returnType; }
    public boolean isStatic()              { return isStatic; }
    public MethodSpec spec()               { return spec; }
    
    public void setSpec(MethodSpec spec)   { this.spec = spec; }
    
    public static JMethod forJavaMethod(Method m) {
        Factory factory = JType.Factory.instance;
        Map<String, Unary> locals = new LinkedHashMap<String, Unary>();
        Class<?>[] params = m.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            String name = Utils.getMethodParamName(m, i); 
            locals.put(name, factory.newJType(params[i], getTypeParams(m.getGenericParameterTypes()[i]))); 
        }
        boolean isStatic = Modifier.isStatic(m.getModifiers());
        Unary declClass = factory.newJType(m.getDeclaringClass()); //TODO: discover type params
        Unary retType = factory.newJType(m.getReturnType(), getTypeParams(m.getGenericReturnType()));
        JMethod jm = new JMethod(m.getName(), declClass, locals, retType, isStatic);
        MethodSpec spec = new ReflectiveSpecProvider().extractMethodSpec(m, NameSpace.forMethod(jm));
        jm.spec = spec;
        return jm;
    }

    public String signature() {
        StringBuilder paramsStr = new StringBuilder();
        for (String paramName : params.keySet()) {
            if (paramsStr.length() > 0) 
                paramsStr.append(", ");
            paramsStr.append(params.get(paramName)).append(" ").append(paramName);
        }
            
        return String.format("%s %s.%s(%s)", returnType, declaringClass, methodName, paramsStr);
    }

    @Override
    public String toString() {
        return signature();
    }

}
/*! @} */
