/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.mit.csail.sdg.squander.serializer.impl.ArraySer;
import edu.mit.csail.sdg.squander.spec.constant.ConstRels;
import edu.mit.csail.sdg.squander.utils.Counter;
import edu.mit.csail.sdg.squander.utils.Utils;
import forge.program.ForgeDomain;
import forge.program.ForgeExpression;
import forge.program.ForgeLiteral;
import forge.program.ForgeProgram;
import forge.program.ForgeType;
import forge.program.ForgeVariable;
import forge.program.GlobalVariable;
import forge.program.InstanceDomain;
import forge.program.InstanceLiteral;
import forge.program.LocalDecls;
import forge.program.LocalVariable;
import forge.util.ExpressionUtil;

public class ForgeScene {

    public static class ArrayVars {
        public final Class<?> cls;
        public final GlobalVariable elems;
        public final GlobalVariable length;

        public ArrayVars(Class<?> cls, GlobalVariable elems, GlobalVariable length) {
            this.cls = cls;
            this.elems = elems;
            this.length = length;
        }
    }
    
    private final Counter<JType.Unary> counter = new Counter<JType.Unary>();
    private final Counter<InstanceDomain> litCnt = new Counter<InstanceDomain>();
    
    private final JavaScene javaScene; 
    private final ForgeProgram program;
    private final InstanceDomain nullType;
    private final InstanceLiteral nullLit;

    /** Maps memory addresses to Forge InstanceLiterals. */
    private final IdentityHashMap<Object, InstanceLiteral> obj2lit = new IdentityHashMap<Object, InstanceLiteral>();
    /** Maps string to Forge literals. */
    private final HashMap<String, InstanceLiteral> str2lit = new HashMap<String, InstanceLiteral>();

    /**
     * Keys are sometimes ForgeAtom and sometimes InstanceLiteral. We use
     * Strings to abstract these to a common representation.
     */
    private final Map<String, Object> lit2obj = new HashMap<String, Object>();
    
    /** Maps Java classes to Forge domains. */
    private final Map<JType.Unary, ForgeDomain> cls2dom = new HashMap<JType.Unary, ForgeDomain>();
    
    /** Maps Java fields to Forge global variables. */
    private final Map<String, GlobalVariable> globals = new HashMap<String, GlobalVariable>();
    /** Maps local variable names to local variables (e.g. thisVar, throwVar, returnVar, args) */
    private final Map<String, LocalVariable> locals = new HashMap<String, LocalVariable>(); 

    private final Map<String, GlobalVariable> consts = new HashMap<String, GlobalVariable>();
    
    private final Map<GlobalVariable, Set<JField>> var2fld = new HashMap<GlobalVariable, Set<JField>>();
    
    /** List of all integers found on the heap */
    private final Set<Integer> ints = new HashSet<Integer>(64);
    
    /** whether all integers (within a bound) should be enumerated in Kodkod */
    private boolean ensureAllInts = false;
        
    private Map<String, ForgeType.Unary> usedTypes = new HashMap<String, ForgeType.Unary>();
    
    private LocalVariable throwVar;
    private LocalVariable returnVar;
    private LocalVariable thisVar; 
    private LocalVariable[] args = new LocalVariable[0];
    
    public ForgeScene(JavaScene javaScene) {
        this.javaScene = javaScene;
        this.program = new ForgeProgram();
        this.nullType = program.newInstanceDomain("Null");
        this.nullLit = program.newInstanceLiteral("null", nullType);
        litCnt.incrementAndGet(nullType);
        lit2obj.put(nullLit.name(), null);
        cls2dom.put(JType.Factory.instance.integerType(), program.integerDomain());
        cls2dom.put(JType.Factory.instance.booleanType(), program.booleanDomain());
        addUsedType(program.integerDomain());
        addUsedType(program.booleanDomain());
        addUsedType(this.nullType);
    }
    
    public void createLocalsForMethod(JMethod m) {
        {
            // return variable
            if (m.returnType().clazz() != void.class) {
                String name = m.name() + "_ret";
                ForgeType type = typeForCls(m.returnType(), true);
                returnVar = program.newLocalVariable(name, type);
                locals.put(returnVar.name(), returnVar);
            }
        }
        {
            // this variable if (method is not static)
            if (!m.isStatic()) {
                thisVar = newThisVariable(m.declaringClass());
                locals.put(thisVar.name(), thisVar);
            }
        }
        {
            // args
            Map<String, JType.Unary> params = m.params();
            args = new LocalVariable[params.size()];
            int i = 0;
            for (Entry<String, JType.Unary> e : params.entrySet()) {
                ForgeType type = typeForCls(e.getValue(), true);
                args[i] = program.newLocalVariable(e.getKey(), type);
                locals.put(args[i].name(), args[i]);
                i++;
            }
        }
    }
    
    public LocalVariable newThisVariable(JType.Unary type) {
        String name = type.simpleName() + "_this";
        ForgeType ftype = typeForCls(type, false);
        return program.newLocalVariable(name, ftype);
    }
    
    public ForgeEnv getEnv(LocalVariable thisVar) { 
        Map<String, LocalVariable> locals = new HashMap<String, LocalVariable>();
        for (LocalVariable var : args) {
            locals.put(var.name(), var);
        }
        return new MyEnv(thisVar, locals);
    }
    
    public ForgeProgram program()              { return program; }
    public LocalVariable thisVar()             { return thisVar; }
    public LocalVariable returnVar()           { return returnVar; }
    public LocalVariable throwVar()            { return throwVar; }
    public LocalVariable[] args()              { return args; }
    public InstanceLiteral nullLit()           { return nullLit; }
    public InstanceDomain nullType()           { return nullType; }
    public Set<InstanceDomain> domains()       { return program.instanceDomains(); }
    public GlobalVariable global(String name)  { return globals.get(name); }
    public GlobalVariable global(JField fld)   { return fld == null ? null : globals.get(fld.fullName()); }
    public Collection<GlobalVariable> consts() { return consts.values(); }
    public boolean isConst(GlobalVariable var) { return consts.containsKey(var.name()); }
    public ForgeVariable findVar(String name)  {
        ForgeVariable var = global(name); 
        if (var == null)
            var = consts.get(name);
        if (var == null)
            var = locals.get(name);
        return var;
    }
    
    public Set<JField> fields(GlobalVariable var) {
        Set<JField> ret = var2fld.get(var);
        if (ret == null)
            ret = new HashSet<JField>();
        return ret;
    }
    
    public boolean isSpecField(GlobalVariable var) {
        Set<JField> fields = fields(var);
        if (fields == null || fields.isEmpty())
            return false;
        // all fields for a given variable must have the same value for isSpec(); 
        return fields.iterator().next().isSpec();
    }
    
    public boolean isOneField(GlobalVariable var) {
        // TODO: add multiplicity to JField
        Set<JField> fields = fields(var);
        if (fields.isEmpty()) 
            throw new RuntimeException("No such field: " + var.name());
             
        JField f = fields.iterator().next();
        if (!f.isSpec())
            return true;
        else {
            return f.getBound().expr.toString().startsWith("(one ("); // TODO: this is too hackish 
        }
    }
    
    public Collection<? extends GlobalVariable> nonAbstractSpecFields() {
        List<GlobalVariable> specFields = new ArrayList<GlobalVariable>();
        for (GlobalVariable g : globals.values())
            if (isSpecField(g) && !isPureAbstractSpecField(g))
                specFields.add(g);
        return specFields;
    }
    
    //TODO: remove this
    public boolean isPureAbstractSpecField(GlobalVariable var) {
        Set<JField> fields = fields(var);
        if (fields.isEmpty())
            return false;
        for (JField f : fields)  {
            if (!f.getAbsFun().expr.equals(program.trueLiteral()))
                return false;
        }
        return true;
    }
    
    public ForgeDomain findDomain(JType.Unary clz) { return cls2dom.get(clz); }
    public ForgeDomain findDomain(Class<?> clz)    { return cls2dom.get(JType.Factory.instance.newJType(clz)); }
    
    public ForgeDomain ensureDomain(JType.Unary clz) {
        ForgeDomain dom = cls2dom.get(clz);
        if (dom == null) {
            dom = program.newInstanceDomain(clz.simpleName());
            cls2dom.put(clz, dom);
            addUsedType(dom);
        }
        return dom; 
    }
    
    public JType.Unary findClassForDomain(ForgeDomain dom) {
        for (Entry<JType.Unary, ForgeDomain> e : cls2dom.entrySet()) {
            if (e.getValue().equals(dom))
                return e.getKey();
        }
        return null;
    }
    
    public void ensureInt(int i)             { ints.add(i); }
    /** returns only used ints */
    public Set<Integer> ints()               { return ints; }
    public boolean isEnsureAllInts()         { return ensureAllInts; }
    public void ensureAllInts(boolean b)     { ensureAllInts = b; }
    public Set<ForgeType.Unary> usedTypes()  { return Collections.unmodifiableSet(new HashSet<ForgeType.Unary>(usedTypes.values())); } 
    
    public GlobalVariable ensureGlobal(JField field) {
        String name = field.fullName();
        GlobalVariable global = globals.get(name);
        if (global == null) {
            global = createGlobalVar(field, name);
            globals.put(name, global);
            Set<JField> fields = new HashSet<JField>();
            fields.add(field);
            var2fld.put(global, fields);
        } else {
            Set<JField> fields = var2fld.get(global);
            assert !fields.isEmpty();
            assert field.isSpec() == fields.iterator().next().isSpec();
            fields.add(field);
        }
        return global;  
    }
    
    public GlobalVariable ensureConst(String name) {
        GlobalVariable var = consts.get(name); 
        if (var == null) {
            ForgeType type = convertToForgeType(ConstRels.findRel(name).type(), true);
            var = program.newGlobalVariable(name, type);
            consts.put(name, var);
        }
        return var;
    }
    
    public InstanceLiteral createLiteral(Object object) {
        return createLiteral(object, null);
    }
    
    public InstanceLiteral createLiteral(Object object, String suffix) {
        JType.Unary cls = javaScene.jtypeForObj(object);
        InstanceDomain instanceDomain = (InstanceDomain) ensureDomain(cls);
        int cnt = counter.incrementAndGet(cls);
        String sfx = "_" + cnt + (suffix != null && suffix.length() > 0 ? "_" + suffix : "");
        InstanceLiteral lit = program.newInstanceLiteral(cls.simpleName() + sfx, instanceDomain);
        litCnt.incrementAndGet(instanceDomain);
        if (object instanceof String) 
            str2lit.put((String)object, lit);
        else
            obj2lit.put(object, lit);
        lit2obj.put(lit.name(), object);
        return lit;
    }
    
    public int numLiteralsFor(InstanceDomain id) {
        return litCnt.getCount(id);
    }

    @SuppressWarnings("unchecked")
    public ForgeType.Unary typeForCls(JType.Unary cls, boolean includeNull) {
        if (Utils.isPrimitive(cls.clazz()))
            return ensureDomain(cls);
        else {
            ForgeType.Unary t = ensureDomain(cls); 
            for (ClassSpec sub : javaScene.subTypes(cls)) {
                ForgeDomain d = findDomain(sub.jtype());
                if (d == null && !sub.isEmpty())
                    d = ensureDomain(sub.jtype());
                if (d != null)
                    t = t.union(ensureDomain(sub.jtype()));
            }
            if (cls.clazz().isAssignableFrom(Integer.class))
                t = t.union(program.integerDomain());
            if (cls.clazz().isAssignableFrom(Boolean.class))
                t = t.union(program.booleanDomain());
            if (includeNull)
                t = t.union(nullType);
            addUsedType(t);
            return t;
        }
    }
    
    public Object strForLit(String litName) {
        return str2lit.get(litName);
    }
    
    public Object objForLit(String litName) {
        return lit2obj.get(litName);
    }
    
    public ForgeLiteral forgeLitForObj(Object obj) {
        if (obj instanceof Integer)
            return program.integerLiteral((Integer) obj);
        if (obj instanceof Boolean) {
            boolean b = (Boolean) obj;
            if (b)
                return program.trueLiteral();
            else
                return program.falseLiteral();
        }
        return instLitForObj(obj);
    }
    
    public InstanceLiteral instLitForObj(Object obj) {
        if (obj == null)
            return nullLit;
        if (obj.getClass() == String.class)
            return str2lit.get(obj);
        return obj2lit.get(obj);
    }
    
    public InstanceLiteral litForName(String name) {
        for (InstanceLiteral il : program.instanceLiterals())
            if (il.name().equals(name))
                return il;
        return null;
    }
    
    public ForgeType convertToForgeType(JType jtype, boolean includeNull) {
        ForgeType t = typeForCls(jtype.domain(), includeNull);
        for (int i = 1; i < jtype.arity(); i++) {
            t = t.product(typeForCls(jtype.projection(i), includeNull));
        }
        return t;
    }
    
    // -------------------------------------------------------------------
    // --------------------------- private ------------------------------
    // -------------------------------------------------------------------
    
    private void addUsedType(ForgeType.Unary t) {
        usedTypes.put(t.toString(), t);
    }
    
    private ForgeType getRangeForField(JField field) {
        if (field.isSpec()) {
            return convertToForgeType(field.type(), true); //TODO: is this always correct?
        } else {
            // Java field
            return typeForCls(field.type().range(), true);
        }
    }
    
    private GlobalVariable createGlobalVar(JField field, String name) {
        ForgeType domain = convertToForgeType(field.owningType(), false);
        ForgeType range = getRangeForField(field);
        ForgeType type = field.isStatic() ? range : domain.product(range);
        return program.newGlobalVariable(name, type);
    }

    // ===================================================================
    // --------------------------- ForgeEnv ------------------------------
    // ===================================================================
    
    private class MyEnv implements ForgeEnv {
    
        private final ForgeEnv.State state;
        private final Map<String, LocalVariable> locals;
        private LocalVariable myThisVar;
        
        MyEnv(LocalVariable thisVar, Map<String, LocalVariable> locals) {
            this(ForgeEnv.State.POST, thisVar, locals);
        }
        
        private MyEnv(State state, LocalVariable thisVar, Map<String, LocalVariable> locals) {
            this.state = state;
            this.myThisVar = thisVar;
            this.locals = locals;
        }

        @Override
        public ForgeExpression arg(int i) {
            return args[i];
        }
    
        @Override
        public JType.Unary classForDomain(ForgeDomain domain) {
            return findClassForDomain(domain);
        }

        @Override
        public ForgeExpression bracketElems(JType type) {
            ClassSpec clsSpec = javaScene.classSpec(type.domain());
            JField fld = clsSpec.findField(ArraySer.ELEMS);
            GlobalVariable var = ensureGlobal(fld);
            return state == State.PRE ? ExpressionUtil.bringGlobalsToPreState(var) : var;
        }
    
        @Override
        public ForgeExpression arrayLength(JType type) {
            ClassSpec clsSpec = javaScene.classSpec(type.domain());
            JField fld = clsSpec.findField(ArraySer.LENGTH);
            GlobalVariable var = ensureGlobal(fld);
            return state == State.PRE ? ExpressionUtil.bringGlobalsToPreState(var) : var;
        }

        @Override
        public ForgeExpression globalVar(GlobalVariable var) {
            if (state == ForgeEnv.State.PRE)
                return var.old();
            else 
                return var;
        }
    
        @Override
        public LocalVariable newLocalVar(String name, ForgeType type) {
            return program.newLocalVariable(name, type);
        }
    
        @Override public ForgeType integerType()        { return program.integerDomain(); }
        @Override public ForgeType nullType()           { return nullType; }
        @Override public ForgeExpression intExpr(int i) { ensureInt(i); return program.integerLiteral(i); }
        @Override public ForgeType booleanType()        { return program.booleanDomain(); }
        @Override public ForgeExpression trueExpr()     { return program.trueLiteral(); }
        @Override public ForgeExpression falseExpr()    { return program.falseLiteral(); }
        @Override public LocalDecls emptyDecls()        { return program.emptyDecls(); }
    
        @Override public ForgeExpression returnVar()    { return returnVar; }
        @Override public LocalVariable thisVar()        { return myThisVar; }
        @Override public ForgeExpression throwVar()     { return getThrowVar(); }
        
        @Override 
        public ForgeEnv setPreStateMode() {
            if (state == State.PRE)
                throw new RuntimeException("re-entered PRE mode");
            return new MyEnv(State.PRE, myThisVar, locals); 
        }
    
        @Override
        public ForgeExpression stringExpr(String text) {
            InstanceLiteral lit = str2lit.get(text);
            if (lit == null) {
                InstanceDomain stringType = (InstanceDomain) stringType();
                lit = program.newInstanceLiteral(text, stringType);
                litCnt.incrementAndGet(stringType);
                str2lit.put(text, lit);
            }
            return lit;
        }
        
        @Override
        public ForgeExpression enumConst(Enum<?> e) {
            return obj2lit.get(e);
        }

        @Override
        public ForgeType stringType() {
            return ensureDomain(JType.Factory.instance.stringType());
        }
    
        @Override
        public ForgeEnv addLocal(LocalVariable var) {
            Map<String, LocalVariable> newLocals = new HashMap<String, LocalVariable>();
            newLocals.putAll(locals);
            newLocals.put(var.name(), var);
            return new MyEnv(state, myThisVar, newLocals);
        }

        @Override
        public ForgeDomain.Unary ensureDomain(JType.Unary clz) { 
            return ForgeScene.this.ensureDomain(clz); 
        }
        
        @Override
        public ForgeDomain.Unary typeForCls(JType.Unary clz, boolean includeNull) { 
            return ForgeScene.this.typeForCls(clz, includeNull); 
        }
        
        @Override 
        public GlobalVariable ensureGlobal(JField field) { 
            return ForgeScene.this.ensureGlobal(field); 
        }

        @Override
        public GlobalVariable ensureConst(String name) {
            return ForgeScene.this.ensureConst(name);
        }

        @Override 
        public LocalVariable findLocal(String name) { 
            return locals.get(name); 
        }

        @Override
        public void ensureAllInts() {
            ensureAllInts = true;
        }

        @Override
        public void ensureInt(int i) {
            ForgeScene.this.ensureInt(i);
        }

        private ForgeExpression getThrowVar() {
            if (throwVar == null) {
                String name = "throw";
                ForgeType type = typeForCls(JType.Factory.instance.throwableType(), true);
                throwVar = program.newLocalVariable(name, type);
                locals.put(throwVar.name(), throwVar);
            }
            return throwVar;
        }
    }

}
/*! @} */
