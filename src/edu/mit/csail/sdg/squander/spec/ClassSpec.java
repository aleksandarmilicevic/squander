/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.spec.JType.Factory;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import edu.mit.csail.sdg.squander.utils.ReflectionUtils;
import forge.program.ForgeExpression;
import forge.program.LocalVariable;
import forge.util.ExpressionUtil;

public class ClassSpec {

    public static class Invariant {
        public final ForgeExpression expr; 
        public final LocalVariable thisVar;

        public Invariant(ForgeExpression expr, LocalVariable thisVar) {
            this.expr = expr;
            this.thisVar = thisVar;
        }

        public ForgeExpression replaceThis(LocalVariable replacement) {
            return ExpressionUtil.replaceVariable(expr, thisVar, replacement);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public static LinkedHashMap<TypeVariable, Unary> getTypeVarBindings(Class<?> cls, Unary[] actualTypes) {
        assert actualTypes == null || actualTypes.length == cls.getTypeParameters().length;
        LinkedHashMap<TypeVariable, Unary> bindings = new LinkedHashMap<TypeVariable, JType.Unary>();        
        for (int i = 0; i < cls.getTypeParameters().length; i++) {
            bindings.put(cls.getTypeParameters()[i], 
                    actualTypes != null ? actualTypes[i] : JType.Factory.instance.objectType());
        }
        return bindings;
    }
    
    // ====================================================================
    // ---------------------------- Members -------------------------------
    // ====================================================================
    
    private final JType.Unary jtype;
    private final JavaScene javaScene;
    
    private Collection<ClassSpec> subs; 
    private List<ClassSpec> supers = new LinkedList<ClassSpec>();
    
    private Map<String, Source> specFieldSources = new HashMap<String, Source>();
    private List<Source> invariantSources = new ArrayList<Source>();
    private List<Invariant> invariants = new ArrayList<Invariant>();
    
    private Map<String, JField> usedFields = new HashMap<String, JField>();
    
    public ClassSpec(JType.Unary jtype, JavaScene javaScene) {
        this.jtype = jtype;
        this.javaScene = javaScene;
    }
    
    public ClassSpec(Class<?> clz, JType.Unary[] typeParams, JavaScene javaScene) {
        this(Factory.instance.newJType(clz, typeParams), javaScene);
    }
    
    public boolean isEmpty() {
        return invariantSources.isEmpty() && specFieldSources.isEmpty();
    }

    public JType.Unary jtype()                      { return jtype; }
    public Class<?> clz()                           { return jtype.clazz(); }
    public JType.Unary[] typeParams()               { return jtype.typeParams(); }
    
    public Collection<ClassSpec> subs()             { return subs; }
    public void setSubs(Collection<ClassSpec> subs) { this.subs = subs; }
    
    public void addSuper(ClassSpec superClsSpec)    { supers.add(superClsSpec);    }

    public JField findField(String name) {
        JField jf = usedFields.get(name);
        if (jf != null)
            return jf;
        for (ClassSpec scs : supers) {
            jf = scs.findField(name);
            if (jf != null) {
                return jf;
            }
        }
        return null;
    }
    
    public JField ensureField(String name) {
        return ensureField(name, true);
    }
    
    // TODO: problem when spec field has the same name as an existing Java field
    public JField ensureField(String name, boolean recurse) { 
        // first try already used fields
        JField jf = usedFields.get(name);
        if (jf != null)
            return jf;

        if (javaScene.isFinished()) {
            Log.trace("returning null for field '" + name + "' since called after finish");
            return null;
        }
        
        // then, search through spec fields
        Source specSource = specFieldSources.get(name);
        if (specSource != null) {
            TypeChecker checker = new TypeChecker(javaScene);
            checker.setClsSpecForSpecField(this);
            specSource.typecheck(checker); // this will add a JField to this ClassSpec (by calling <code>addSpecField</code>)
            JField f = usedFields.get(name);
            assert f != null;
            if (specSource.isFuncField())
                f.setFuncFlag(true);
            return f;
        }
        
        // finally, try Java fields
        Field f = ReflectionUtils.getField(jtype.clazz(), name);
        if (f != null) {
            jf = JField.newJavaField(f, getTypeVarBindings(clz(), typeParams())); //TODO
            addUsedField(jf);
            // extract field invariants, if any
            ISpecProvider rsp = new ReflectiveSpecProvider();
            TypeChecker checker = new TypeChecker(javaScene);
            for (Source src : rsp.extractFieldSpec(f, jtype)) {
                addInvariant(src);
                src.typecheck(checker);
            }
            return jf;
        }
        
        // couldn't find field here, look in super classes
        if (recurse) {
            for (ClassSpec scs : supers) {
                jf = scs.ensureField(name);
                if (jf != null) {
                    return jf;
                }
            }
        }
        return null; 
    }
    
    public Collection<Invariant> invariants() { return invariants; }
    public Collection<Source> specFields()    { return specFieldSources.values(); }
    public boolean hasSpecField(String name)  { return specFieldSources.containsKey(name); }
    public Collection<JField> usedFields()    { return usedFields.values(); }
    public Collection<JField> usedFieldsAll() {
        Map<String, JField> result = new HashMap<String, JField>();
        result.putAll(usedFields);
        for (ClassSpec cs : supers) {
            for (JField f : cs.usedFieldsAll())
                if (!result.containsKey(f.name()))
                    result.put(f.name(), f);
        }
        return result.values();
    }

    public void addInvariant(Source src) { 
        assert !javaScene.isFinished();
        invariantSources.add(src); 
    }

    public void addSpecFieldSource(Source src) {
        assert !javaScene.isFinished() : "can't add spec field after calling finish(): " + src;
        specFieldSources.put(extractSpecFieldName(src), src);
    }
    
    public void addSpecField(JField field) {
        assert !javaScene.isFinished();
        assert field.isSpec();
        addUsedField(field);
    }

    /**
     * Type-checks only invariants
     */
    public void typecheck() {
        TypeChecker checker = new TypeChecker(javaScene);
        // this is because new invariants maybe added during typechecking 
        ArrayList<Source> invSrcs = new ArrayList<Source>(invariantSources);
        for (Source s : invSrcs) 
            s.typecheck(checker);
    }

    public void translateSpecs(ForgeScene forgeScene) {
        LocalVariable thisVar = forgeScene.newThisVariable(jtype());
        ForgeEnv env = forgeScene.getEnv(thisVar);
        Tr tr = new Tr();
        Tr.SpecFieldTranslator funTr = new Tr.SpecFieldTranslator();
        for (Entry<String, JField> e : usedFields.entrySet()) {
            if (!e.getValue().isSpec())
                continue;
            Source s = specFieldSources.get(e.getKey());
            JField field = e.getValue();
            Tr.SpecFieldBoundTranslator boundTr = new Tr.SpecFieldBoundTranslator(field);
            field.setDomain(s.translate(tr, env));
            field.setAbsFun(new Invariant(s.translate(funTr, env), env.thisVar()));
            field.setBound(new Invariant(s.translate(boundTr, env), env.thisVar()));
            Frame frame = new Frame(forgeScene);
            Tr.FrameConstructor frameTr = new Tr.FrameConstructor(frame, forgeScene);
            s.translate(frameTr, env);
            if (frame.locations().size() > 0)
                field.setFrame(frame);
        }
        for (Source s : invariantSources) {
            ForgeExpression expr = s.translate(tr, env);
            Invariant inv = new Invariant(expr, env.thisVar());
            invariants.add(inv);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(jtype);
        for (JField jf : usedFields.values())
            sb.append("\n  ").append(jf.name());
        return sb.toString();
    }
    
    private void addUsedField(JField field)   { 
        usedFields.put(field.name(), field); 
    }
    
    private String extractSpecFieldName(Source src) {
        // TODO: does this cover all possible cases
        return src.source.substring(0, src.source.indexOf(":")).trim();
    }

}
/*! @} */
