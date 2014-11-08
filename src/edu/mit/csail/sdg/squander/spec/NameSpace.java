/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mit.csail.sdg.squander.spec.JType.Unary;

public class NameSpace {
    
    private final Map<String, JType> locals;
    private final List<JType.Unary> scopes; 
    private final JMethod method; 
    
    // ===========================================================
    // ------------------------ static ---------------------------
    // ===========================================================
    
    public static NameSpace forMethod(JMethod method) {
        List<JType.Unary> scopes = new ArrayList<JType.Unary>(1);
        scopes.add(method.declaringClass());
        Map<String, JType> locals = new HashMap<String, JType>(method.params().size());
        for (Entry<String, Unary> e : method.params().entrySet()) {
            locals.put(e.getKey(), e.getValue());
        }
        return new NameSpace(method, locals, scopes);
    }
    
    public static NameSpace forClass(Unary type) {
        List<Unary> scopes = new ArrayList<Unary>(1);
        scopes.add(type);
        Map<String, JType> locals = new HashMap<String, JType>(0);
        return new NameSpace(null, locals, scopes);
    }

    // ===========================================================
    // ------------------------ member ---------------------------
    // ===========================================================

    private NameSpace(JMethod method, Map<String, JType> locals, List<Unary> scopes) {
        this.method = method;
        this.locals = locals;
        this.scopes = scopes;
    }

    public NameSpace addLocal(String name, JType type) {
        HashMap<String, JType> newLocals = new HashMap<String, JType>(locals.size() + 1);
        newLocals.putAll(locals);
        newLocals.put(name, type);
        return new NameSpace(method, newLocals, scopes);
    }

    public NameSpace addScope(JType.Unary clz) {
        List<JType.Unary> newScopes = new ArrayList<JType.Unary>(scopes.size() + 1);
        newScopes.addAll(scopes);
        newScopes.add(clz);
        return new NameSpace(method, locals, newScopes);
    }

    public JField findField(String name, JavaScene scene) { // TODO: delete javascene
        for (int i = scopes.size() - 1; i >= 0; i--) {
            JType.Unary declarer = scopes.get(i);
            ClassSpec clzSpec = scene.ensureClass(declarer);
            JField fld = clzSpec.ensureField(name);
            if (fld != null)
                return fld;
        }
        return null;
    }

    public JType findLocal(String name) {
        return locals.get(name);
    }

    public boolean inArray() {
        JType.Unary clz = scope(); 
        if (clz == null)
            return false;
        return clz.clazz().isArray();
    }
    
    public boolean inList() {
        JType.Unary clz = scope(); 
        if (clz == null)
            return false;
        return List.class.isAssignableFrom(clz.clazz());
    }

    public boolean inMap() {
        JType.Unary clz = scope(); 
        if (clz == null)
            return false;
        return Map.class.isAssignableFrom(clz.clazz());
    }
    
    public JMethod method() {
        return method;
    }

    public JType.Unary scope() {
        if (scopes.isEmpty())
            return null;
        return scopes.get(scopes.size() - 1);
    }

    public JType.Unary declarer() {
        if (scopes.isEmpty())
            return null;
        return scopes.get(0);
    }

}
/*! @} */
