package edu.mit.csail.sdg.squander.engine;

import java.util.HashMap;
import java.util.Map;

import edu.mit.csail.sdg.squander.spec.ForgeEnv;
import edu.mit.csail.sdg.squander.spec.JType;
import edu.mit.csail.sdg.squander.spec.NameSpace;
import edu.mit.csail.sdg.squander.spec.Source;
import edu.mit.csail.sdg.squander.spec.Tr;
import edu.mit.csail.sdg.squander.spec.TypeChecker;
import forge.program.ForgeExpression;

public class PostExeTranslator {

    private final ForgeConverter fconv;

    public PostExeTranslator(ForgeConverter fconv) {
        this.fconv = fconv;
    } 
    

    private Map<String, Source> srcs = new HashMap<String, Source>();
    
    public JType typecheck(String expr) {
        NameSpace ns = NameSpace.forMethod(fconv.javaScene().method());
        Source src = srcs.get(expr);
        if (src == null) {
            src = new Source(expr, ns, Source.Rule.CLAUSE);
            srcs.put(expr, src);
        }
        if (!src.isTypechecked()) {
            TypeChecker tc = new TypeChecker(fconv.javaScene());
            src.parse();
            src.typecheck(tc);
        }
        return src.node().jtype;
    }
    
    public ForgeExpression translate(String expr) {
        Source src = srcs.get(expr);
        if (src == null) {
            typecheck(expr); 
            src = srcs.get(expr);
        }
        if (!src.isTranslated()) {
            Tr tr = new Tr();
            ForgeEnv env = fconv.forgeScene().getEnv(fconv.forgeScene().thisVar());
            src.translate(tr, env);
        }
        return src.translation();
    }
    
    
}
