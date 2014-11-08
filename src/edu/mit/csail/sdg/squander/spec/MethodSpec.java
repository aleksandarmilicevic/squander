/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import forge.program.ForgeExpression;

/**
 * Method specification source. Abstract data type that holds method
 * specifications.

 * Empty specification source should be the identity with respect to the union
 * operation.
 * 
 * @author kuat
 * @author Aleksandar Milicevic
 */
public class MethodSpec {

    /** Specification case source */
    public class CaseSource {
        private Source preSrc;
        private Source pstSrc;
        private Source frmSrc;
        private boolean exceptional;
        
        private ForgeExpression pre; 
        private ForgeExpression pst; 
        private Frame frm;

        public Source preSrc()       { return preSrc; }
        public Source postSrc()      { return pstSrc; }
        public Source frameSrc()     { return frmSrc; }
        public boolean exceptional() { return exceptional; }
        
        public ForgeExpression pre()   { return pre; }
        public ForgeExpression post()  { return pst; }
        public Frame frame()           { return frm; }
        
        CaseSource(Source requires, Source ensures, Source modifies, boolean exceptional) {
            this.preSrc = requires;
            this.pstSrc = ensures;
            this.frmSrc = modifies;
            this.exceptional = exceptional;
        }
    }

    private final List<CaseSource> cases;
    private final Map<String, CaseSource> pre2cs = new HashMap<String, CaseSource>();
    private final Map<JType.Unary, Integer> freshObj = new HashMap<JType.Unary, Integer>();
    private final Map<String, String> letBindings = new HashMap<String, String>();
    private Options options;
    
    private boolean helper;
    private boolean pure;
    
    public Options options()        { return options; }
    public boolean isHelper()       { return helper; }
    public boolean isPure()         { return pure; }
    public List<CaseSource> cases() { return Collections.unmodifiableList(cases); }
    public boolean isTrivial()      { return !pure && cases.size() == 0; }
    
    public Map<JType.Unary, Integer> freshObjects() { return freshObj; }
    
    MethodSpec() {
        this.cases = new ArrayList<CaseSource>();
        this.helper = false;
        this.pure = false;
    }

    public void typecheck(JavaScene javaScene) {
        TypeChecker checker = new TypeChecker(javaScene);
        for (CaseSource cs : cases) {
            cs.preSrc().typecheck(checker);
            cs.postSrc().typecheck(checker);
            cs.frameSrc().typecheck(checker);
        }
    }
    
    public void translateSpecs(ForgeEnv env, ForgeScene forgeScene) {
        Tr tr = new Tr();
        for (CaseSource cs : cases) {
            cs.pre = cs.preSrc().translate(tr, env);
            cs.pst = cs.postSrc().translate(tr, env);
            cs.frm = new Frame(forgeScene);
            Tr.FrameConstructor fc = new Tr.FrameConstructor(cs.frm, forgeScene);
            cs.frameSrc().translate(fc, env);
        }
    }
    
    public void addOptions(Options opt) {
        this.options = opt;
    }
    
    public void addLetBinding(String var, String expr) {
        this.letBindings.put(var, expr);
    }
    
    void addFreshObj(Class<?> cls, Class<?>[] typeParams, int num) {
        Unary newJType;
        if (typeParams == null || typeParams.length == 0) {
            newJType = JType.Factory.instance.newJType(cls);
        } else {
            newJType = JType.Factory.instance.newJType(cls, typeParams);
        }
        freshObj.put(newJType, num);
        
    }

    void addCase(Source requires, Source ensures, Source modifies, boolean exceptional) {
        // see if a source with the same precondition already exists
        CaseSource cs = pre2cs.get(requires.source); 
        if (cs == null) {
            cs = new CaseSource(requires, ensures, modifies, exceptional);
            cases.add(cs);
            pre2cs.put(requires.source, cs);
        } else {
            cases.remove(cs);
            String post = String.format("(%s) && (%s)", cs.postSrc().source, ensures.source);
            Source newEnsures = new Source(post, ensures.ns, ensures.rule);
            String mod;
            if (cs.frmSrc.source.length() == 0)
                mod = ensures.source;
            else if (ensures.source.length() == 0)
                mod = cs.frmSrc.source;
            else
                mod = String.format("%s, %s", cs.frmSrc.source, modifies.source);
            Source newModifies = new Source(mod, modifies.ns, modifies.rule);
            boolean newExceptional = cs.exceptional || exceptional;
            CaseSource newCs = new CaseSource(requires, newEnsures, newModifies, newExceptional); 
            cases.add(newCs);
            pre2cs.put(requires.source, newCs);
        }
    }
    
    void mergeCasesWith(MethodSpec ms) {
        for (CaseSource c : ms.cases)
            cases.add(c);
    }
    
    void makeHelper() {
        helper = true;
    }
    
    void makePure() {
        pure = true;
    }

}
/*! @} */
