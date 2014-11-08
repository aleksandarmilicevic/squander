package edu.mit.csail.sdg.squander.spec;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.csail.sdg.squander.eventbased.Final;
import edu.mit.csail.sdg.squander.eventbased.Initial;
import edu.mit.csail.sdg.squander.eventbased.StateFrame;
import edu.mit.csail.sdg.squander.eventbased.StateOptions;
import edu.mit.csail.sdg.squander.eventbased.Transition;
import edu.mit.csail.sdg.squander.spec.Source.Rule;

public class TraceSpecConverter extends ReflectiveSpecProvider {

    private final Class<?> cls;

    public TraceSpecConverter(Class<?> cls) {
        this.cls = cls;
    }

    public MethodSpec convertToMethodSpec(JMethod method) {
        MethodSpec mspec = new MethodSpec();
        
        Map<String, String> letBindings = extractLetBindings(cls);
        Set<String> modifies = new LinkedHashSet<String>();
        List<String> clauses = new LinkedList<String>();
        
        for (Annotation ann : cls.getAnnotations()) {
            if (ann instanceof Initial) {
                List<String> initialClauses = convertArray(((Initial) ann).value());
                Map<String, String> lb = new HashMap<String, String>(); 
                lb.put("this", "steps.first");
                String s = clause(initialClauses, lb, method.declaringClass());
                clauses.add(s);
            } else if (ann instanceof Final) {
                List<String> initialClauses = convertArray(((Final) ann).value());
                Map<String, String> lb = new HashMap<String, String>(); 
                lb.put("this", "steps[return-1]");
                String s = clause(initialClauses, lb, method.declaringClass());
                clauses.add(s);
            } else if (ann instanceof Transition) {
                List<String> initialClauses = convertArray(((Transition) ann).value());
                Map<String, String> lb = new LinkedHashMap<String, String>(); 
                //NOTE: order matters
                lb.put("this", "steps[i-1]");
                lb.put("this'", "steps[i]");
                String s = "all i : {1 ... (return-1)} | " + clause(initialClauses, lb, method.declaringClass());
                clauses.add(s);
            } else if (ann instanceof StateFrame) {
                modifies.addAll(convertArray(((StateFrame) ann).value()));
            } else if (ann instanceof StateOptions) {
                mspec.addOptions(((StateOptions)ann).value());
            }
        }
        
        NameSpace ns = NameSpace.forMethod(method);
        String postSpec = "(return > 0 && return <= steps.length) && " + clause(clauses, letBindings, method.declaringClass());
        Source pre = new Source("true", ns, Rule.CLAUSE);
        Source post = new Source(postSpec, ns, Rule.CLAUSE); 
        Source mod = new Source(frame(modifies, letBindings, method.declaringClass()), ns, Rule.FRAME);
        mspec.addCase(pre, post, mod, false);
        
        return mspec;
    }
    
}
