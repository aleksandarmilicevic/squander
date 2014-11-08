/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.csail.sdg.squander.spec.ClassSpec.Invariant;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import forge.program.ForgeExpression;
import forge.program.ForgeProgram;
import forge.program.ForgeType;
import forge.program.ForgeVariable;
import forge.program.GlobalVariable;
import forge.program.LocalVariable;
import forge.util.ExpressionUtil;

/**
 * Specification.
 * 
 * @specfield proc: Procedure
 * @specfield cases: set SpecCase
 * 
 * @author Kuat Yessenov (kuat@mit.edu)
 * @author Aleksandar Milicevic
 */
public final class Spec {

    /**
     * Specification case. Remark that the pre-condition is <b>not</b> in the
     * pre-state.
     * 
     * @author Kuat Yessenov (kuat@mit.edu)
     * @author Aleksandar Milicevic
     */
    public final class SpecCase {
        private final ForgeExpression pre;
        private final ForgeExpression post;
        private final Frame frame;
        private final boolean helper;

        private SpecCase(ForgeExpression requires, ForgeExpression ensures, Frame frame, boolean helper) {
            this.pre = ExpressionUtil.bringGlobalsToPostState(requires);
            this.post = ensures;
            this.frame = frame;
            this.helper = helper;
        }

        public ForgeExpression preOnly()  { return pre; }
        public ForgeExpression postOnly() { return post; }
        public ForgeExpression pre()      { return pre; } //helper ? pre : pre.and(invariant()); }
        public ForgeExpression post()     { return helper ? post : post.and(invariant()); }
        
        public Frame frame() { return frame; }
        public Spec spec()   { return Spec.this; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (helper) sb.append("@Helper").append("\n");
            sb.append("@Requires:").append("\n").append(ExpressionUtil.prettyPrint(pre()));
            sb.append("@Ensures:").append("\n").append(ExpressionUtil.prettyPrint(post()));
            sb.append("@Modifies:").append("\n").append(frame);
            return sb.toString();
        }
    }

    @SuppressWarnings("unused")
    private static int fieldCounter = 0;
    
    private final JavaScene javaScene;
    private final ForgeScene forgeScene;
    private final List<SpecCase> cases;
    
    // -------------------------------------------------------------------
    // ----------------------- Constructors ------------------------------
    // -------------------------------------------------------------------
    
    public Spec(JavaScene javaScene, ForgeScene forgeScene) {
        this.javaScene = javaScene;
        this.forgeScene = forgeScene;
        this.cases = new ArrayList<SpecCase>();
    }
     
    
    // -------------------------------------------------------------------
    // ------------------------- Accessors -------------------------------
    // -------------------------------------------------------------------
    
    public JavaScene javaScene()        { return javaScene; }
    public ForgeScene forgeScene()      { return forgeScene; }
    public Collection<SpecCase> cases() { return cases; }
    public boolean isEmpty()            { return cases.isEmpty(); }
    
    // -------------------------------------------------------------------
    // --------------------------- Setters -------------------------------
    // -------------------------------------------------------------------
    
    public void addCase(ForgeExpression requires, ForgeExpression ensures, Frame frame, boolean helper) {
        assert requires != null && ensures != null && frame != null;
        cases.add(new SpecCase(requires, ensures, frame, helper));
    }
    
    /**
     * Semantic meaning of a specification.
     */
    
//    /**
//     * OR of old(pre)
//     */
//    private ForgeExpression pre() {
//        ForgeExpression result = forgeScene.program().falseLiteral();
//        for (SpecCase cs : cases)
//            result = result.or(ExpressionUtil.bringGlobalsToPreState(cs.pre()));
//        return result;
//    }
//
//    /**
//     * AND of old(pre) => post /\ frame
//     */
//    private ForgeExpression post() {
//        ForgeExpression result = forgeScene.program().trueLiteral();
//        for (SpecCase cs : cases) {
//            ForgeExpression pre = ExpressionUtil.bringGlobalsToPreState(cs.pre());
//            result = result.and(pre.implies(cs.post().and(cs.frame().condition())));
//        }
//        return result;
//    }
//    
//    /**
//     * All modifiable global variables across frames.
//     */
//    private Set<GlobalVariable> modifiable() {
//        Set<GlobalVariable> result = new HashSet<GlobalVariable>();
//        for (SpecCase cs : cases)
//            result.addAll(cs.frame().modifiable());
//        return result;
//    }

    /**
     * Expression denoting the valid state semantics of invariants: For all
     * relevant classes, for any instance in the class all invariants must hold. 
     * The only exception is for constructors to avoid invariant condition on the of "this" 
     * (the exact type, not super-types.)
     */
    public ForgeExpression invariant() {
        ForgeProgram program = forgeScene.program();
        ForgeExpression result = program.trueLiteral();
        for (ClassSpec clsSpec : javaScene.classSpecs()) {
            if (clsSpec.isEmpty())
                continue;
            Class<?> clazz = clsSpec.clz();
            ForgeExpression invariant = program.trueLiteral();
            String varName = clazz.getSimpleName() + "0";
            ForgeType domain = forgeScene.typeForCls(clsSpec.jtype(), false);
            LocalVariable quant = program.newLocalVariable(varName, domain);            
            for (Invariant inv : clsSpec.invariants()) {
                invariant = invariant.and(inv.replaceThis(quant));
            }
            // TODO: what about constructors
            result = result.and(invariant.forAll(quant));
        }
        return result;
    }
    
    /** Java language constraints for the pre-state 
     * @param modifiable */
    public ForgeExpression wellformedPre(Set<GlobalVariable> modifiable) {
        // inputs are singletons
        ForgeExpression contract = forgeScene.program().trueLiteral();

        Set<ForgeVariable> singletons = new HashSet<ForgeVariable>();
        if (forgeScene.thisVar() != null) singletons.add(forgeScene.thisVar());
        for (LocalVariable arg : forgeScene.args())
            singletons.add(arg);
        for (ForgeVariable var : singletons) 
            contract = contract.and(var.one());
            
        // java constraint
        contract = contract.and(concreteConstraint(modifiable));
                
        return contract;
    }


    /** Java language constraints for the post-state */
    /** Language constraints for post-state 
     * @param modifiable */
    public ForgeExpression wellformedPost(Set<GlobalVariable> modifiable) {
        // outputs are singletons
        ForgeExpression contract = forgeScene.program().trueLiteral();
        
        if (forgeScene.returnVar() != null) contract = contract.and(forgeScene.returnVar().one());
        if (forgeScene.throwVar() != null) contract = contract.and(forgeScene.throwVar().one());

        // concrete constraint in post-state
        contract = contract.and(concreteConstraint(modifiable));
        return contract;
    }
    
    /**
     * Skips over the unmodifiable vars
     */
    public ForgeExpression concreteConstraint(Set<GlobalVariable> unmodifiable) {
        ForgeProgram program = forgeScene.program();
        ForgeExpression result = program.trueLiteral();
        
        // concrete fields are functions (or singletons)
        for (ClassSpec clsSpec : javaScene.classSpecs()) {
            for (JField field : clsSpec.usedFields()) {
                if (field.isSpec())
                    continue;
                if (unmodifiable.contains(forgeScene.global(field)))
                    continue;
                result = result.and(fieldConstraint(field));
            }
        }
        
        return result;
    }
    
    public ForgeExpression abstractConstraint() {
        ForgeExpression result = forgeScene.program().trueLiteral();
        for (ClassSpec clsSpec : javaScene.classSpecs()) {
            for (JField field : clsSpec.usedFields()) {
                if (!field.isSpec() || field.isFunc()) 
                    continue;
                result = result.and(fieldConstraint(field));
            }
        }
        return result;
    }
    
    public ForgeExpression funcConstraint() {
        ForgeExpression result = forgeScene.program().trueLiteral();
        for (ClassSpec clsSpec : javaScene.classSpecs()) {
            for (JField field : clsSpec.usedFields()) {
                if (!field.isFunc()) 
                    continue;
                result = result.and(fieldConstraint(field));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (SpecCase cs : cases)
            sb.append("Case #" + ++i).append('\n').append(cs);

        sb.append("\n");
        sb.append("Abstraction function:");
        sb.append("\n");
        sb.append(ExpressionUtil.prettyPrint(abstractConstraint()));

        sb.append("\n");
        sb.append("Func function:");
        sb.append("\n");
        sb.append(ExpressionUtil.prettyPrint(funcConstraint()));
        
        sb.append("\n");
        sb.append("Concrete constraint:");
        sb.append("\n");
        sb.append(ExpressionUtil.prettyPrint(concreteConstraint(new HashSet<GlobalVariable>())));

        return sb.toString();
    }
    
    private ForgeExpression fieldConstraint(JField field) {
        fieldCounter++;
        GlobalVariable var = forgeScene.global(field);
        if (var == null) return forgeScene.program().trueLiteral();
        ForgeProgram program = forgeScene.program();
        Unary ownerClz = field.owningType();
        Unary domClz = field.declaringType();
        if (field.isSpec()) { // spec field
            ForgeExpression condition = forgeScene.program().trueLiteral();
            {
                String name = "l_" + domClz.clazz().getSimpleName();
                LocalVariable x = program.newLocalVariable(name, forgeScene.typeForCls(domClz, false));
                condition = condition.and(field.getAbsFun().replaceThis(x).forAll(x));
            }
            {
                if (ownerClz == domClz) {
                    String name = "l_" + ownerClz.clazz().getSimpleName();
                    LocalVariable x = program.newLocalVariable(name, forgeScene.typeForCls(ownerClz, false));
                    condition = condition.and(field.getBound().replaceThis(x).forAll(x));
                } else {
                    // the owner class is going to add the "bound" constraint
                }
            }
            return condition;
        } else { // java field
            if (field.isStatic()) 
                return var.one();
            else {
                String name = "l_" + domClz.clazz().getSimpleName();
                ForgeType domain = forgeScene.typeForCls(domClz, false);
                LocalVariable quant = forgeScene.program().newLocalVariable(name , domain);
                return quant.join(var).one().forAll(quant);
            }
        }
    }

}
/*! @} */
