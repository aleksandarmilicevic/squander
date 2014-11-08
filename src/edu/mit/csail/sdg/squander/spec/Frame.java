/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import forge.program.ForgeExpression;
import forge.program.ForgeVariable;
import forge.program.GlobalVariable;
import forge.program.LocalVariable;
import forge.transform.ExpressionReplacer;
import forge.util.ExpressionUtil;

/**
 *    Frame condition.
 *    The roots of location may be local variables. The context of these variables is not specified in this class.    
 *
 *    @specfield frame : set GlobalVariable -> ForgeExpression {
 *        (all var : GlobalVariable | one var.frame)
 *    }
 **/
public class Frame {
    private final ForgeScene forgeScene;
    private final Map<GlobalVariable, ForgeExpression> locations;
    private final Map<GlobalVariable, ForgeExpression> selectors; 
    private final Map<GlobalVariable, ForgeExpression> upperBounds;
    private final Map<GlobalVariable, ForgeExpression> filters;

    private Set<GlobalVariable> mods = null;
    
    public Frame(ForgeScene scene) {
        this.forgeScene = scene;
        this.locations = new HashMap<GlobalVariable, ForgeExpression>();
        this.selectors = new HashMap<GlobalVariable, ForgeExpression>();
        this.upperBounds = new HashMap<GlobalVariable, ForgeExpression>();
        this.filters = new HashMap<GlobalVariable, ForgeExpression>();
    }
    
    public Set<GlobalVariable> locations() {
        return locations.keySet();
    }
    
    public ForgeExpression location(GlobalVariable g)     { return locations.get(g); }
    public ForgeExpression instSelector(GlobalVariable g) { return selectors.get(g); }
    public ForgeExpression filter(GlobalVariable g)       { return filters.get(g); }
    public ForgeExpression upperBound(GlobalVariable g)   { return upperBounds.get(g); }

    /**
     * frame = no var.@old(frame) 
     *         ? @old(frame) ++ var -> heap
     *         : @old(frame) ++ var -> (var.@old(frame) + heap)
     * 
     * If var stands for a spec field, the frame of the field is automatically added.
     * @param upper 
     */
    public void add(GlobalVariable var, ForgeExpression location, ForgeExpression sel, ForgeExpression upper, ForgeExpression filter) {
        assert var != null;
        assert location != null;
        assert var.arity() > 1 : "static global variables in frame are not yet supported";
        assert (!(sel != null && filter != null)) : "can't use both instance selector and frame filter";
        
        final ForgeExpression heap = ExpressionUtil.bringGlobalsToPreState(location);
        if (locations.containsKey(var))
            locations.put(var, locations.get(var).union(heap)); // TODO: might be useful to keep them separated (in separate data structures)
        else
            locations.put(var, heap);
        
        if (sel == null)
            sel = location;
        else 
            sel = sel.intersection(location);
        
        sel = ExpressionUtil.bringGlobalsToPreState(sel);
        if (selectors.containsKey(var)) 
            selectors.put(var, selectors.get(var).union(sel));
        else
            selectors.put(var, sel);
        
        if (upper != null) {
            if (upperBounds.containsKey(var))
                throw new RuntimeException("Can't add frame for the same variable twice"); // UNION would be counter-intuitive
            upper = ExpressionUtil.bringGlobalsToPreState(upper);
            upperBounds.put(var, upper);
        }
        
        if (filter != null) {
            filter = ExpressionUtil.bringGlobalsToPreState(filter);
            ForgeExpression prevFilter = filters.get(var);
            if (prevFilter != null)
                filter = prevFilter.and(filter);
            filters.put(var, filter);
        }
        
        //--------------------
        // add field frame locations
        // -------------------
        for (final JField abs : forgeScene.fields(var)) {
            Frame frame = abs.getFrame();
            if (frame == null)
                continue;
            for (GlobalVariable dep : frame.locations.keySet()) {
                ForgeExpression depHeap = ExpressionUtil.bringGlobalsToPostState(frame.locations.get(dep));
                depHeap = depHeap.accept(new ExpressionReplacer() {
                    @Override protected ForgeExpression visit(ForgeVariable expr) {
                        ForgeExpression result = expr.equals(abs.getAbsFun().thisVar) ? heap : expr;
                        super.putCache(expr, result);
                        return result;
                    }
                });
                add(dep, depHeap, null, null, null);
            }
        }
    }

    /**
     * Requires that no additional relations are added to the scene afterwards.
     * 
     * @returns
     * (all var : scene.globals() | 
     * no var.frame => (all quant | quant.var = quant.@old(var)) &&
     * (all var : scene.globals() |
     * some var.frame => (all quant | quant.var != quant.@old(var) => quant in var.frame))
     */
    public ForgeExpression condition() {        
        ForgeExpression result = forgeScene.program().trueLiteral();
        Set<GlobalVariable> modifiable = modifiable();
        for (GlobalVariable var : forgeScene.program().globalVariables()) {
            if (var.arity() == 1) continue; // ignore static fields, not listed in modifiables
            LocalVariable quant = forgeScene.program().newLocalVariable(var.name() + "_mod", var.type().domain());                
            if (!(modifiable.contains(var))) { 
                result = result.and(quant.join(var).eq(quant.join(var.old())).forAll(quant));
            } else {
                ForgeExpression instSel = selectors.get(var);
                if (instSel != null)
                    result = result.and(modCond(var, instSel, quant));
            }
        }
        return result;
    }
    
    public ForgeExpression modCond() {
        ForgeExpression result = forgeScene.program().trueLiteral();
        for (GlobalVariable var : forgeScene.program().globalVariables()) {
            if (var.arity() == 1) continue; // ignore static fields, not listed in modifiables
            result = result.and(modCond(var));
        }
        return result;
    }
    
    public ForgeExpression modCond(GlobalVariable var) {
        ForgeExpression result = forgeScene.program().trueLiteral();
        LocalVariable quant = forgeScene.program().newLocalVariable(var.name() + "_mod", var.type().domain());               
        ForgeExpression instSel = selectors.get(var);
        if (instSel != null) 
            result = result.and(modCond(var, instSel, quant));
        return result;
    }
    
    public ForgeExpression filterCond(GlobalVariable var) {
        //TODO
        throw new NotImplementedException();
    }
    
    public ForgeExpression upperCond(GlobalVariable var) {
        ForgeExpression instSel = selectors.get(var);
        ForgeExpression upper = upperBounds.get(var);
        if (instSel == null || upper == null)
            return null;
        return var.in(instSel.product(upper));
    }
    
    private ForgeExpression modCond(GlobalVariable var, ForgeExpression instSel, LocalVariable l) {
        return l.in(instSel).not().implies(l.join(var).eq(l.join(var.old()))).forAll(l);
    }

    /**
     * Global variables for which old heap locations may be modified.
     * 
     * @returns {var : scene.globals() | some var.frame}
     */
    public Set<GlobalVariable> modifiable() {
        if (mods == null) {
            mods = new HashSet<GlobalVariable>();
            // add all variables directly specified through @Modifies
            mods.addAll(locations.keySet());
            boolean changed = true;
            while (changed) {
                changed = false;
                l: for (GlobalVariable var : forgeScene.nonAbstractSpecFields()) {
                    if (mods.contains(var)) {
                        // add its frame
//                        for (JField sf : forgeScene.fields(var)) {
//                            if (sf.getAbsFun() == null)
//                                continue;
//                            FrameInference fi = new FrameInference();
//                            sf.getAbsFun().expr.accept(fi);
//                            Set<GlobalVariable> vars = fi.globals();
//                            for (GlobalVariable v : vars) { 
//                                changed = changed || mods.add(v);
//                            }
//                        }
                    } else {
                        // see if any of its frame vars is in mods
                        // and if it is, add this var to mods 
                        for (JField sf : forgeScene.fields(var)) {
                            if (sf.getAbsFun() == null)
                                continue;
                            FrameInference fi = new FrameInference();
                            sf.getAbsFun().expr.accept(fi);
                            Set<GlobalVariable> vars = fi.globals();
                            for (GlobalVariable v : vars) { 
                                if (mods.contains(v)) {
                                    changed = true;
                                    mods.add(var);
                                    continue l;
                                }
                            }
                        }
                    }
                }
            }
        }
        return mods;
    }
    
    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("modify");
        for (GlobalVariable var : modifiable()) sb.append(' ').append(var);
        sb.append(":").append("\n");
        sb.append(ExpressionUtil.prettyPrint(condition()));        
        return sb.toString();
    }

}
/*! @} */
