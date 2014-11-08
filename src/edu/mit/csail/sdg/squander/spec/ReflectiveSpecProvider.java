/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.csail.sdg.annotations.Case;
import edu.mit.csail.sdg.annotations.Effects;
import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Fresh;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.FuncField;
import edu.mit.csail.sdg.annotations.Helper;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Macro;
import edu.mit.csail.sdg.annotations.Macros;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.NonNull;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.annotations.Pure;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.annotations.Specification;
import edu.mit.csail.sdg.annotations.Throws;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import edu.mit.csail.sdg.squander.spec.Source.Rule;
import edu.mit.csail.sdg.squander.utils.ReflectionUtils;


public class ReflectiveSpecProvider implements ISpecProvider {
    
    private final String TYPE_EXCEPTIONAL = "EXCEPTIONAL";
    /*** Format strings for de-sugaring specifications */
    
    private final String CLAUSE_DEFAULT = "true";
    private final String RETURNS_FORMAT = "return = (%s)";
    private final String NONNULL_FORMAT = "(%s) != null";
           
    public MethodSpec extractMethodSpec(Method method, NameSpace ns) {
        MethodSpec result = new MethodSpec();
        extractMethodSpec(method, result, ns);
        return result;
    }
    
    private void extractMethodSpec(Method method, MethodSpec result, NameSpace ns) {
        extractMethodSpecNonRecursive(method, result, ns);
        if (!Modifier.isStatic(method.getModifiers())) {
            Class<?> methodDeclCls = method.getDeclaringClass();
            for (Class<?> cls : ReflectionUtils.getImmParents(methodDeclCls)) {
                try {
                    Method m = ReflectionUtils.getMethod(cls, method.getName(), method.getParameterTypes());
                    extractMethodSpec(m, result, ns);
                } catch (NoSuchMethodException e) {
                }
            }
        }
        return;
    }
    
    private void extractMethodSpecNonRecursive(Method method, MethodSpec result, NameSpace ns) {
        // initialize collections for the lightweight spec cases
        Set<String> requires = new LinkedHashSet<String>();
        Set<String> ensures = new LinkedHashSet<String>();
        Set<String> modifies = new LinkedHashSet<String>();
        
        // extract let bindings first
        Map<String, String> letBindings = extractLetBindings(method);
        
        // scan method annotations
        for (Annotation ann : method.getAnnotations()) {
            if (ann instanceof Requires) {
                requires.addAll(convertArray(((Requires) ann).value()));
            } else if (ann instanceof Ensures) {
                ensures.addAll(convertArray(((Ensures) ann).value()));
            } else if (ann instanceof Effects) {
                ensures.addAll(convertArray(((Effects) ann).value()));
            } else if (ann instanceof Modifies) {
                modifies.addAll(convertArray(((Modifies) ann).value()));
            } else if (ann instanceof Returns) {
                ensures.add(String.format(RETURNS_FORMAT, ((Returns) ann).value()));
            } else if (ann instanceof Pure) {
                result.makePure();
            } else if (ann instanceof Helper) {
                result.makePure();
            } else if (ann instanceof NonNull) {
                ensures.add(String.format(NONNULL_FORMAT, "return"));
            } else if (ann instanceof Throws) {
                throw new RuntimeException("Throws annotation not supported");
            } else if (ann instanceof Specification) {
                // full specification mode
                for (Case cs : ((Specification) ann).value()) {
                    // Process each case in the specification
                    final List<String> caseRequires = new ArrayList<String>();
                    final List<String> caseEnsures = new ArrayList<String>();
                    final List<String> caseModifies = new ArrayList<String>();
                    boolean caseExceptional = false;

                    caseRequires.addAll(convertArray(cs.requires()));
                    caseEnsures.addAll(convertArray(cs.ensures()));
                    caseModifies.addAll(convertArray(cs.modifies()));
                    caseExceptional = TYPE_EXCEPTIONAL.equals(cs.type().name());

                    Source pres = new Source(clause(caseRequires, letBindings, ns.declarer()), ns, Rule.CLAUSE);
                    Source posts = new Source(clause(caseEnsures, letBindings, ns.declarer()), ns, Rule.CLAUSE); 
                    Source frames = new Source(frame(caseModifies, letBindings, ns.declarer()), ns, Rule.FRAME); 
                        
                    result.addCase(pres, posts, frames, caseExceptional);
                }
            } else if (ann instanceof Fresh) {
                Fresh fresh = (Fresh) ann;
                for (FreshObjects freshObj : fresh.value()) {
                    result.addFreshObj(freshObj.cls(), freshObj.typeParams(), freshObj.num());
                }
            } else if (ann instanceof FreshObjects) {
                FreshObjects freshObj = (FreshObjects) ann;
                result.addFreshObj(freshObj.cls(), freshObj.typeParams(), freshObj.num());
            } else if (ann instanceof Options) {
                Options opt = (Options) ann;
                result.addOptions(opt);
            } 
        }

        // augment lightweight specification case
        if (requires.size() > 0 || ensures.size() > 0 || modifies.size() > 0)
            result.addCase(
                    new Source(clause(requires, letBindings, ns.declarer()), ns, Rule.CLAUSE), 
                    new Source(clause(ensures, letBindings, ns.declarer()), ns, Rule.CLAUSE), 
                    new Source(frame(modifies, letBindings, ns.declarer()), ns, Rule.FRAME), 
                    false);

    }

    protected Map<String, String> extractLetBindings(Class<?> declaringClass) {
        Map<String, String> letBindings = new LinkedHashMap<String, String>();
        while (declaringClass != null) {
            letBindings.putAll(extractLetBindings(declaringClass.getAnnotations()));
            declaringClass = declaringClass.getDeclaringClass();
        }
        return letBindings;
    }
    
    protected Map<String, String> extractLetBindings(Method m) {
        Map<String, String> letBindings = extractLetBindings(m.getAnnotations());
        letBindings.putAll(extractLetBindings(m.getDeclaringClass()));
        return letBindings;
    }
    
    protected Map<String, String> extractLetBindings(Annotation[] annotations) {
        Map<String, String> letBindings = new LinkedHashMap<String, String>();
        for (Annotation ann : annotations) {
            if (ann instanceof Macro) {
                Macro let = (Macro) ann;
                letBindings.put("$" + let.var(), let.expr());
            } else if (ann instanceof Macros) {
                Macros lets = (Macros) ann;
                for (Macro let : lets.value()) {
                    letBindings.put("$" + let.var(), let.expr());
                }
            }
        }
        return letBindings;
    }

    /* (non-Javadoc)
     * @see squander.spec.ISpecProvider#extractClassSpec(java.lang.Class)
     */
    public List<Source> extractClassSpec(JType.Unary jtype) {
        List<Source> result = new ArrayList<Source>();
        Map<String, String> letBindings = extractLetBindings(jtype.clazz());
        for (Annotation ann : jtype.clazz().getAnnotations()) {
            if (ann instanceof Invariant) {
                for (String inv : ((Invariant) ann).value())
                    result.add(new Source(clause(inv, letBindings, jtype), NameSpace.forClass(jtype), Rule.CLAUSE));
            } else if (ann instanceof SpecField) {
                for (String sf : ((SpecField) ann).value())
                    result.add(new Source(frame(sf, letBindings, jtype), NameSpace.forClass(jtype), Rule.DECLARATION, false));
            } else if (ann instanceof FuncField) {
                for (String sf : ((FuncField) ann).value())
                    result.add(new Source(frame(sf, letBindings, jtype), NameSpace.forClass(jtype), Rule.DECLARATION, true));
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see squander.spec.ISpecProvider#extractFieldSpec(java.lang.reflect.Field)
     */
    public List<Source> extractFieldSpec(Field field, JType.Unary declType) {
        Map<String, String> letBindings = extractLetBindings(field.getDeclaringClass());
        List<Source> result = new ArrayList<Source>();
        for (Annotation ann : field.getAnnotations()) {
            if (ann instanceof NonNull) {
                result.add(new Source(String.format(NONNULL_FORMAT, "this." + field.getName()), 
                        NameSpace.forClass(declType), Rule.CLAUSE));
            } else if (ann instanceof Invariant) {
                for (String inv : ((Invariant) ann).value())
                    result.add(new Source(clause(inv, letBindings, declType), NameSpace.forClass(declType), Rule.CLAUSE));
            }
        }
        return result;
    }


    /** Extracts elements of a string array annotation */
    protected List<String> convertArray(String[] strs) {
        List<String> result = new ArrayList<String>();
        for (String str : strs) {
            if ("@nothing".equals(str))
                continue;
            result.add(str);
        }
        return result;
    }
    
    protected String inlineLetVars(String expr, Map<String, String> letBindings) {
        String result = expr;
        ArrayList<Entry<String, String>> vars = new ArrayList<Map.Entry<String,String>>(letBindings.entrySet());
        Collections.reverse(vars);
        for (Entry<String, String> var : vars) {
            Matcher varMatcher = Pattern.compile("(\\W)\\Q" + var.getKey() + "\\E(\\W)").matcher(" " + result + " ");
            String replacement = letBindings.get(var.getKey()).replaceAll("\\$", "\\\\\\$");
            result = varMatcher.replaceAll("$1" + replacement + "$2").trim();
        }
        return result;
    }

    protected String clause(String clause, Map<String, String> letBindings, Unary jtype) {
        return clause(Collections.singleton(clause), letBindings, jtype);
    }
    
    protected String clause(Collection<String> clauses, Map<String, String> letBindings, Unary jtype) {
        if (clauses.size() == 0)
            return CLAUSE_DEFAULT;

        final StringBuilder sb = new StringBuilder();
        for (String s : clauses) {
            if (sb.length() > 0)
                sb.append(" && ");
            sb.append('(').append(s).append(')');
        }
        return replaceTypeParams(inlineLetVars(sb.toString(), letBindings), jtype);
    }

    protected String frame(String frame, Map<String, String> letBindings, Unary jtype) {
        return frame(Collections.singleton(frame), letBindings, jtype);
    }
    
    protected String frame(Collection<String> frames, Map<String, String> letBindings, Unary jtype) {
        if (frames.size() == 0)
            return "";
                
        final StringBuilder sb = new StringBuilder();
        for (String s : frames) {
            if (sb.length() > 0)
                sb.append(" , ");
            sb.append(s);
        }
        return replaceTypeParams(inlineLetVars(sb.toString(), letBindings), jtype);
    }

    private String replaceTypeParams(String src, Unary jtype) {
        if (jtype == null)
            return src;
        List<String> paramNames = new ArrayList<String>(jtype.clazz().getTypeParameters().length);
        for (TypeVariable<?> tp : jtype.clazz().getTypeParameters())
            paramNames.add(tp.getName());
        return SpecFileSpecProvider.replaceParamTypes(src, jtype, paramNames);
    }
}
/*! @} */
