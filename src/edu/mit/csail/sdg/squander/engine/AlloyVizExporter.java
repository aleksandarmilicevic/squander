package edu.mit.csail.sdg.squander.engine;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.mit.csail.sdg.squander.absstate.ObjTuple;
import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.engine.ISquanderResult.IEvaluator;
import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.spec.JField;
import edu.mit.csail.sdg.squander.spec.JType;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import edu.mit.csail.sdg.squander.spec.constant.ConstRel2KKConst;
import edu.mit.csail.sdg.squander.utils.AutoId.AutoIdCounter;
import forge.program.ForgeExpression;
import forge.program.GlobalVariable;
import forge.program.InstanceDomain;

public class AlloyVizExporter {

    private final IEvaluator eval;
    private final ForgeConverter fconv;
    
    private IdentityHashMap<Object, Integer> ids = new IdentityHashMap<Object, Integer>();
    private IdentityHashMap<Object, Integer> atomIds = new IdentityHashMap<Object, Integer>();
    private AutoIdCounter atomIdCnt;

    private final String univSigId = getId(Object.class);
    private final String intSigId = getId(int.class);
    private final String boolSigId = getId(boolean.class);
    
    public AlloyVizExporter(ForgeConverter fconv, IEvaluator eval) {
        this.fconv = fconv;
        this.eval = eval;
    }

    public void export(PrintStream ps, ExtraSkolems skolems, Comparator<? super Object> comparator) {
        atomIdCnt = new AutoIdCounter();
        writeAlloyElement(ps, skolems, comparator);
    }
        
    private void writeAlloyElement(PrintStream ps, ExtraSkolems skolems, Comparator<? super Object> comparator) {
        ps.println("<alloy>");
        writeInstanceElement(ps, skolems, comparator);        
        ps.println("</alloy>");
    }

    private void writeInstanceElement(PrintStream ps, ExtraSkolems skolems, Comparator<? super Object> comparator) {
        String methodName = fconv.javaScene().method().signature();
        writeElementStart(ps, "", "instance", "bitwidth", fconv.bw(), "maxseq", 0, "command", 
                "squander: " + methodName, "filename", "");
        writeUnivSigElement(ps);
        
        // sigs
        writeBuiltinSigs(ps);
        for (InstanceDomain dom : fconv.forgeScene().domains()) {
            writeSigElement(ps, dom, comparator);
        }
        
        // fields
        f: for (GlobalVariable var : fconv.forgeScene().program().globalVariables()) {
            // skip Kodkod const relations
            if (new ConstRel2KKConst().convert(var.name()) != null)
                continue;
            // skip arrays and collections (since those relations will be expanded)
            for (JField fld : fconv.forgeScene().fields(var)) {
                if (isCollectionClass(fld.owningType().clazz()))
                    continue f;
                break;
            }
            writeFieldElement(ps, var);
        }

        writeExtraScolems(ps, skolems);
        writeElementEnd(ps, "", "instance");
    }
    
    private void writeBuiltinSigs(PrintStream ps) {
        writeElementStart(ps, "", "sig", "label", "Int", "ID", intSigId, "parentID", univSigId, "builtin", "yes");
        writeElementEnd(ps, "", "sig");
        writeElementStart(ps, "", "sig", "label", "Bool", "ID", boolSigId, "parentID", univSigId, "builtin", "no");
        writeAtomElement(ps, true);
        writeAtomElement(ps, false);
        writeElementEnd(ps, "", "sig");
    }
    

    @SuppressWarnings("unchecked")
    private void writeSigElement(PrintStream ps, InstanceDomain dom, Comparator<? super Object> comparator) {        
        Unary cls = fconv.forgeScene().findClassForDomain(dom);
        if (cls == null || cls.clazz() == Object.class)
            return;
        // don't show maps/arrays/collections as sigs
        if (isCollectionClass(cls.clazz()))
            return;
        // handle enums separately
        if (cls.clazz().isEnum()) {
            writeEnumElement(ps, cls.clazz());
            return;
        }
            
        String parentId = univSigId;
        Class<?> superCls = cls.clazz().getSuperclass();
        if (superCls != null) {
            if (fconv.forgeScene().findDomain(superCls) != null)
                parentId = getId(superCls);
        }
        writeElementStart(ps, "", "sig", "label", dom.name(), "ID", getId(cls.clazz()), "parentID", parentId, 
                "abstract", yesno((cls.clazz().getModifiers() & Modifier.ABSTRACT) != 0));
        Object[] objs = fconv.heap2Lit().objectsForClass(cls.clazz()).toArray();
        if (comparator != null) {
            Arrays.sort(objs, comparator);
        }
        for (Object obj : objs) {
            writeAtomElement(ps, obj);
        }
        writeElementEnd(ps, "", "sig");
    }

    private void writeFieldElement(PrintStream ps, GlobalVariable var) {
        JField fld;
        try {
            fld = fconv.forgeScene().fields(var).iterator().next();
        } catch (Exception e) {
            Log.warn("Could not get field for variable '" + var.name() + "'");
            return;
        }
        JType type = expandType(fld.owningType().product(fld.type()), 0);        
        writeElementStart(ps, "", "field", "label", var.name(), "ID", getId(var), "parentID", getId(type.domain().clazz()));
        ObjTupleSet val = eval.evaluate(var);
        for (ObjTuple t : val.tuples()) {
            writeTupleElement(ps, t);
        }        
        writeTypes(ps, type);
        writeElementEnd(ps, "", "field");
    }
    
    private void writeExtraScolems(PrintStream ps, ExtraSkolems skolems) {
        if (skolems == null) 
            return;
        for (String expr : skolems.skolemFunctions) {
            int idx = expr.indexOf(":"); 
            if (idx == -1) {
                Log.warn("Invalid skolem function format: " + expr); 
                continue;
            }
            String name = expr.substring(0, idx).trim();
            String func = expr.substring(idx+1).trim();
            PostExeTranslator tr = new PostExeTranslator(fconv);
            JType jtype = tr.typecheck(func);
            ForgeExpression fe = tr.translate(func);
            ObjTupleSet ots = eval.evaluate(fe);
            writeScolemElement(ps, name, jtype, ots);
        }
    }

    private void writeScolemElement(PrintStream ps, String name, JType jtype, ObjTupleSet ots) {
        writeElementStart(ps, "", "skolem", "label", name, "ID", getId(getScolemIdObj(name)));
        for (ObjTuple t : ots) {
            writeTupleElement(ps, t);
        }
        writeTypes(ps, jtype);
        writeElementEnd(ps, "", "skolem");
    }
    
    private void writeTupleElement(PrintStream ps, ObjTuple t) {
        ObjTupleSet ots = expandTuples(t.atoms(), 0);
        if (ots == null) return;
        for (ObjTuple t2 : ots.tuples()) {
            ps.print("    <tuple>");
            for (Object a : t2) {                
                inlineAtomElement(ps, a);
            }
            ps.println("</tuple>");
        }
    }
    
    private void writeEnumElement(PrintStream ps, Class<? extends Enum<?>> enumCls) {
        writeElementStart(ps, "", "sig", "label", enumCls.getSimpleName(), "ID", getId(enumCls), 
                "parentID", univSigId);
        for (Enum<?> e : enumCls.getEnumConstants()) {
            writeAtomElement(ps, e);
        }
        writeElementEnd(ps, "", "sig");
    }

    private void inlineAtomElement(PrintStream ps, Object obj) {
        inlineElementStartEnd(ps, "atom", "label", atomNameForObj(obj));
    }
    private void writeAtomElement(PrintStream ps, Object obj) {
        ps.print("    "); inlineAtomElement(ps, obj); ps.println();
    }
    
    private void writeTypes(PrintStream ps, JType type) {
        ps.print("    ");
        ps.print("<types>");
        for (JType.Unary un : type.tuple()) {
            inlineElementStartEnd(ps, "type", "ID", getId(un.clazz()));
        }
        ps.println("</types>");
    }

    private JType expandType(JType type, int startIdx) {
        JType.Unary fst = type.projection(startIdx);
        if (startIdx == type.arity() - 1) {
             return expandUnary(fst);
        } else {
            JType first = expandUnary(fst);
            JType rest = expandType(type, startIdx + 1); 
            return first.product(rest);
        }
    }

    private JType expandUnary(Unary t) {
        assert t.clazz() != null;
        if (isCollectionClass(t.clazz())) {
            if (t.clazz().isArray()) {
                JType compType = JType.Factory.instance.newJType(t.clazz().getComponentType());
                return JType.Factory.instance.integerType().product(compType);
            } else if (List.class.isAssignableFrom(t.clazz())) {
                return JType.Factory.instance.integerType().product(t.typeParams()[0]);
            } else {
                JType ret = null;
                for (Unary u : t.typeParams()) {
                    if (ret == null) ret = u; else ret = ret.product(u);
                }
                return ret;
            }
        } else {
            return t;
        }
    }

    private ObjTupleSet expandTuples(Object[] atoms, int startIdx) {
        Object atom = atoms[startIdx];
        if (startIdx == atoms.length - 1) {
             return toOts(atom);
        } else {
            ObjTupleSet first = toOts(atom);
            ObjTupleSet rest = expandTuples(atoms, startIdx + 1); 
            return myProduct(first, rest);
        }
    }

    @SuppressWarnings("unchecked")
    private ObjTupleSet toOts(Object atom) {
        if (isCollectionClass(atom.getClass())) {
            if (atom.getClass().isArray() || atom instanceof List) {
                Iterable<Object> col;
                if (atom instanceof List)
                    col = (Iterable<Object>) atom;
                else
                    col = Arrays.asList((Object[])atom);
                ObjTupleSet ret = null; 
                int idx = 0; 
                for (Object elem : col) {
                    ObjTupleSet fst = new ObjTupleSet(1);
                    fst.add(new ObjTuple(idx++));
                    ObjTupleSet prod = myProduct(fst, toOts(elem));
                    if (ret == null) ret = prod; else ret = myUnion(ret, prod);
                }                
                return ret;
            } else if (atom instanceof Set) {
                ObjTupleSet ret = null;
                for (Object elem : (Set<?>) atom) {
                    if (ret == null) ret = toOts(elem); else ret = myUnion(ret, toOts(elem));
                }                        
                return ret;
            } else if (atom instanceof Map) {
                ObjTupleSet ret = null;
                for (Entry<?, ?> e : ((Map<?, ?>) atom).entrySet()) {
                    ObjTupleSet ots = myProduct(toOts(e.getKey()), toOts(e.getValue()));
                    if (ret == null) ret = ots; else ret = myUnion(ret, ots);
                }
                return ret;
            }
            throw new RuntimeException("unknown collection kind: " + atom.getClass().getName());
        } else {
            ObjTupleSet ots = new ObjTupleSet(1);
            if (ots != null) ots.add(new ObjTuple(atom));
            return ots;
        }
    }

    private ObjTupleSet myProduct(ObjTupleSet ots1, ObjTupleSet ots2) {
        if (ots1 == null || ots2 == null) return null;
        return ots1.product(ots2);
    }

    private ObjTupleSet myUnion(ObjTupleSet ots1, ObjTupleSet ots2) {
        if (ots1 == null || ots2 == null) return null;
        return ots1.union(ots2);
    }
    
    private void writeUnivSigElement(PrintStream ps) {
        writeElementStartEnd(ps, "", "sig", "label", "univ", "ID", univSigId, "builtin", "yes");
    }
    
    private String atomNameForObj(Object obj) {
        if (obj == null)
            return "null";
        if (obj instanceof Boolean)
            return Boolean.toString((Boolean) obj);
        if (obj instanceof Integer)
            return Integer.toString((Integer) obj);
        Integer id = atomIds.get(obj); 
        if (id == null) {
            id = atomIdCnt.getAndInc(obj.getClass());
            atomIds.put(obj, id); 
        }
        String clsName = obj.getClass().getSimpleName();
        if (obj instanceof Enum)
            clsName += "." + ((Enum<?>)obj).name();
        return String.format("%s$%04d", clsName, id);
    }

    private String yesno(boolean b) {
        return b ? "yes" : "no";
    }

    private String concatAttrs(Object... attrs) {
        assert attrs.length % 2 == 0;
        String attrStr = "";
        for (int i = 0; i < attrs.length/2; i++) {
            attrStr += String.format(" %s=\"%s\"", escape(attrs[2*i].toString()), escape(attrs[2*i+1].toString()));
        }
        return attrStr.trim();
    }
    
    private String escape(String str) {
        return str.replaceAll("<[^>]*>", "").replaceAll("\"", "'");
    }

    private void inlineElementStartEnd(PrintStream ps, String name, Object... attrs) {
        String attrStr = concatAttrs(attrs);
        ps.print(String.format("<%s %s/>", name, attrStr));
    }
    
    private void writeElementStartEnd(PrintStream ps, String ident, String name, Object... attrs) {
        ps.print(ident); inlineElementStartEnd(ps, name, attrs); ps.println();
    }

    private void writeElementStart(PrintStream ps, String ident, String name, Object... attrs) {
        String attrStr = concatAttrs(attrs);
        ps.println(String.format("%s<%s %s>", ident, name, attrStr));        
    } 
    
    private void writeElementEnd(PrintStream ps, String ident, String name) {
        ps.println(String.format("%s</%s>", ident, name));
    }    

    private Object getScolemIdObj(String name) {
        return "skolem/" + name;
    }
    
    private String getId(Object obj) {
        Integer id = ids.get(obj);
        if (id == null) {
            id = ids.size();
            ids.put(obj, id);
        }
        return String.format("%04d", id);
    }

    private boolean isCollectionClass(Class<?> clazz) {
        return clazz != null &&
               (clazz.isArray() || Collection.class.isAssignableFrom(clazz) 
                                || Map.class.isAssignableFrom(clazz));
    }
}
