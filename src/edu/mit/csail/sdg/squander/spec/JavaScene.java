/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.csail.sdg.squander.spec.JType.Factory;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import edu.mit.csail.sdg.squander.spec.constant.ConstRel;
import edu.mit.csail.sdg.squander.spec.constant.ConstRels;
import edu.mit.csail.sdg.squander.utils.ReflectionUtils;

@SuppressWarnings("rawtypes")
public class JavaScene {
    
    private Map<JType.Unary, ClassSpec> classes = new HashMap<JType.Unary, ClassSpec>();
    private Map<Object, ClassSpec> obj2spec = new IdentityHashMap<Object, ClassSpec>();
    private HashSet<Class<? extends Enum<?>>> enums = new HashSet<Class<? extends Enum<?>>>();
    
    /** types whose cardinality is mentioned in the spec */
    private Map<String, JType> numTypes = new HashMap<String, JType>();
    
    private JMethod method;  
    private Object caller;
    private Object[] methodArgs = new Object[0]; 
    
    private boolean finished = false;
    private Map<String, ConstRel> constRels = new HashMap<String, ConstRel>();
    
    public boolean isFinished() { return finished; }
    
    public void addObj2spec(Object object, ClassSpec cs) { obj2spec.put(object, cs); }
    
    public ClassSpec ensureClass(Class<?> cls) {
        return ensureClass(Factory.instance.newJType(cls));
    }
    
    @SuppressWarnings("unchecked")
    public ClassSpec ensureClass(JType.Unary type) {
        ClassSpec clzSpec = classes.get(type);
        if (clzSpec == null) {
            //assert !finished : "can't add more classes after calling finish()";
            clzSpec = createClassSpec(type);
            classes.put(type, clzSpec);
            // ensure super classes and interfaces
            Class<?> clz = type.clazz();
            for (Class<?> cls : ReflectionUtils.getImmParents(clz)) {
                JType.Unary superType = Factory.instance.newJType(cls, type.typeParams());
                ClassSpec superClzSpec = ensureClass(superType);
                clzSpec.addSuper(superClzSpec);
            }
            // ensure component classes for arrays
            if (clz.isArray()) {
                JType.Unary compType = Factory.instance.newJType(clz.getComponentType());
                ensureClass(compType);
            } 
            // ensure enum constants (which are just static fields) for enums (
            if (clz.isEnum()) {
                Class<? extends Enum<?>> enumClz = (Class<? extends Enum<?>>) clz;
                enums.add(enumClz);
                for (Enum e : enumClz.getEnumConstants()) {
                    //clzSpec.ensureField(e.name());
                    addObj2spec(e, clzSpec);
                }
            }
            // typecheck
            clzSpec.typecheck();
        }
        return clzSpec;
    }

    public void finish() {
        assert !finished : "don't call finish() more than once";
        // for all classes ensure fields used in their super classes
        while (true) {
            Collection<ClassSpec> toVisit = new ArrayList<ClassSpec>(classSpecs());
            for (ClassSpec cs : toVisit) {
                for (JField jf : cs.usedFieldsAll()) {
                    if (jf.declaringType().clazz() == cs.clz())
                        continue;
                    cs.ensureField(jf.name(), false);
                }
            }
            if (toVisit.size() == classSpecs().size())
                break;
        }
        finished = true;
    }

    public void setMethod(JMethod method) {
        this.method = method;
        method.spec().typecheck(this);
    }

    public void setArgs(Object[] methodArgs) { 
        if (methodArgs == null)
            this.methodArgs = new Object[0];
        else 
            this.methodArgs = methodArgs; 
    }

    public JMethod method()                    { return method; }
    public MethodSpec methodSpec()             { return method.spec(); }
    public Collection<ClassSpec> classSpecs()  { return classes.values(); }
    public Collection<JType> numTypes()        { return numTypes.values(); }
    public Collection<ConstRel> constRels()    { return constRels.values(); }
    
    public Collection<Class<?>> classes()      { 
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (JType.Unary jtype : this.classes.keySet()) {
            classes.add(jtype.clazz());
        }
        return classes;
    }
    public Collection<Class<? extends Enum<?>>> enums()      { return enums; } 
    public List<JType.Unary> jtypes(Class<?> cls) {
        List<JType.Unary> result = new LinkedList<Unary>();
        for (JType.Unary t : classes.keySet()) {
            if (t.clazz() == cls)
                result.add(t);
        }
        return result;
    }
    //public ClassSpec classSpec(Class cls)      { return classes.get(Factory.instance.newJType(cls)); }
    public ClassSpec classSpecForObj(Object o) { return obj2spec.get(o); }
    public JType.Unary jtypeForObj(Object o)   { return obj2spec.get(o).jtype(); }
    public ClassSpec classSpec(Unary jtype)    { return classes.get(jtype); }

    public JType.Unary findJTypeForClassSimpleName(String clsName) {
        if (clsName == null || clsName.length() == 0)
            return null;
        for (Unary t : classes.keySet())
            if (t.clazz().getSimpleName().equals(clsName))
                return t;
        return null;
    }
    public void setCaller(Object caller)       { this.caller = caller; }
    public Object caller()                     { return caller; }
    public Object[] methodArgs()               { return methodArgs; }
    
    public Collection<JField> specFields() {
        List<JField> specFields = new ArrayList<JField>();
        for (ClassSpec clsSpec : classSpecs()) {
            for (JField fld : clsSpec.usedFields()) 
                if (fld.isSpec())
                    specFields.add(fld);
        }
        return specFields;
    }

    public Collection<ClassSpec> subTypes(JType.Unary jtype) {
        return subTypes(classSpec(jtype));
    }
    
    private Collection<ClassSpec> subTypes(ClassSpec clsSpec) {
        assert clsSpec != null : "clsSpec is null";
        if (((Class<?>) clsSpec.clz()).isArray())
            return new ArrayList<ClassSpec>(0);
        Collection<ClassSpec> subs = clsSpec.subs();
        if (subs == null) {
            subs = new LinkedList<ClassSpec>();
            for (ClassSpec cs : classes.values()) {
                if (cs == clsSpec)
                    continue;
                if (clsSpec.jtype().isAssignableFrom(cs.jtype()))
                    subs.add(cs);
            }
            clsSpec.setSubs(subs);
        }
        return subs;
    }
    
    public void translateSpecs(ForgeScene forgeScene) {
        assert finished : "call finish() first";
        for (ClassSpec clsSpec : classSpecs()) {
            if (clsSpec.isEmpty()) 
                continue;
            clsSpec.translateSpecs(forgeScene);
        }
        if (method != null) {
            ForgeEnv env = forgeScene.getEnv(forgeScene.thisVar());
            method.spec().translateSpecs(env, forgeScene);
        }
    }
    
    private ClassSpec createClassSpec(JType.Unary jtype) {
        //TODO (after Heap2Lit is used for initial heap traversal): 
        //assert !isFinished() : "can't create class spec after calling finish(); cls = " + jtype;
        ClassSpec clzSpec = new ClassSpec(jtype, this);
        ISpecProvider isp = new CompositeSpecProvider(
                new SpecFileSpecProvider(), new ReflectiveSpecProvider());
        List<Source> clsSpec = isp.extractClassSpec(jtype);
        for (Source src : clsSpec) {
            src.parse();
            if (src.isClause()) {
                clzSpec.addInvariant(src);
            } else if (src.isDecl()) {
                clzSpec.addSpecFieldSource(src);
            } else 
                throw new RuntimeException("invalid class spec: " + src);
        }
        return clzSpec;
    }

    public void addNumType(JType sub) {
        numTypes.put(sub.toString(), sub);
    }

    public ConstRel findConstRel(String text) {
        ConstRel rel = ConstRels.findRel(text);
        if (rel != null)
            constRels.put(rel.name(), rel);
        return rel;
    }

}
/*! @} */
