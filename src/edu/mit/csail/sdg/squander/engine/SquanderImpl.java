/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.engine.ISquanderResult.IEvaluator;
import edu.mit.csail.sdg.squander.errors.ConsistencyError;
import edu.mit.csail.sdg.squander.errors.NoMoreResults;
import edu.mit.csail.sdg.squander.errors.NoSolution;
import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.spec.JMethod;
import edu.mit.csail.sdg.squander.spec.Spec;
import edu.mit.csail.sdg.squander.spec.Spec.SpecCase;
import edu.mit.csail.sdg.squander.utils.ReflectionUtils;
import edu.mit.csail.sdg.util.collections.Iterators;
import forge.cfg.ForgeCFG;
import forge.program.ForgeExpression;
import forge.program.GlobalVariable;
import forge.solve.ForgeSolver;
import forge.util.ExpressionUtil;

/**
 * An implementation of the {@link ISquander} interface that
 * uses Forge as the back-end. 
 * 
 * @author Aleksandar Milicevic (aleks@csail.mit.edu)
 */
public class SquanderImpl implements ISquander {
    
    private ISquanderResult lastResult;
    private Spec spec;
    
    protected SquanderReporter reporter;
    
    public SquanderImpl() { }
    
    // =========================================================================
    // ---------------------------- public -------------------------------------
    // =========================================================================

    /**
     * Most general form
     * 
     * @param <R>
     *            - return type
     * @param caller
     *            - the object the method is being invoked on; null if the
     *            method is static
     * @param clsName
     *            - name of the class of the method (explicitly needed if
     *            <code>caller</code> is null, otherwise must be equal to
     *            caller.getClass().getName())
     * @param methodName
     *            - name of the method
     * @param methodParams
     *            - types of method parameters
     * @param methodArgs
     *            - actual method arguments
     * @return
     */
    @SuppressWarnings("unchecked")
    public <R> R magic(Object caller, String clsName, String methodName, Class<?>[] methodParams, Object[] methodArgs) {
        return (R) magic(caller, convToJMethod(caller, clsName, methodName, methodParams), methodArgs);
    }
    
    @SuppressWarnings("unchecked")
    public <R> R magic(Object caller, Method method, Object[] methodArgs) {
        JMethod jmethod = JMethod.forJavaMethod(method);
        return (R) magic(caller, jmethod, methodArgs);
    }
    
    @SuppressWarnings("unchecked")
    public <R> R magic(Object caller, JMethod method, Object[] methodArgs) {
        if (!method.isStatic() && caller == null)
            throw new ConsistencyError(String.format("The method you are trying to execute (%s) is not static" +
            		" but the given caller object is null.", method));
        
        SquanderReporter reporter = SquanderReporter.INSTANCE;
        Iterator<? extends ISquanderResult> results = exeMethod(reporter, caller, method, methodArgs);
        
        if (!results.hasNext()) {
            throw new NoMoreResults();
        }
        ISquanderResult result = results.next();
        lastResult = result;
        
        Log.debug("===============================================");
        Log.debug(" -------------------- Trace -------------------");
        Log.debug("===============================================");
        Log.debug(result.getTrace());
        if (!result.hasSolution()) {
            throw new NoSolution();
        }
        reporter.restoringJavaHeap();
        result.restoreJavaHeap();
        reporter.finishedAnalysis();
        return (R) result.getReturnValue();
    }

//    public Iterator<Object> evalSpecField(Object caller, String specFieldName) {
//        String methodName = "__this";
//        Class<?>[] methodParamTypes = new Class<?>[] {};
//        Object[] methodArgs = new Object[] {};
//        Iterator<? extends ISquanderResult> results = exeMethod(SquanderReporter.INSTANCE,
//                caller, convToJMethod(caller, methodName, methodParamTypes), methodArgs);
//        if (!results.hasNext()) {
//            throw new RuntimeException("No results");
//        }
//        ISquanderResult result = results.next();
//        if (!result.hasSolution()) {
//            throw new RuntimeException("No solution.");
//        }
//        return result.getSpecField(specFieldName);
//    }

    public ISquanderResult getLastResult() {
        return lastResult;
    }

    // =========================================================================
    // -------------------------- protected ------------------------------------
    // =========================================================================

    public JMethod convToJMethod(Object caller, String clsName, String methodName, Class<?>[] methodParams) {
        Class<?> clz;
        try {
            if (caller != null)
                clz = caller.getClass();
            else
                clz = Class.forName(clsName);
            Method method = ReflectionUtils.findMethod(clz, methodName, methodParams);
            return JMethod.forJavaMethod(method);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("method " + methodName + " doesn't exist", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class " + clsName + " doesn't exist", e);
        }
    }
    
    /**
     * @Requires reporter != null;
     * @Requires classPath != null;
     * @Requires className != null;
     * @Requires methodName != null;
     */
    protected Iterator<? extends ISquanderResult> exeMethod(SquanderReporter reporter, 
            Object caller, JMethod m, Object[] methodArgs) {
        this.reporter = reporter;
        Object[] roots = new Object[methodArgs.length + 1];
        roots[0] = caller;
        for (int i = 0; i < methodArgs.length; i++) roots[i+1] = methodArgs[i];
        ForgeConverter fconv = new ForgeConverter(reporter, roots);
        fconv.setCallContext(caller, m, methodArgs);
        Spec spec = getSpec(fconv);
        
        // run for each spec case, quit after the first solution
        boolean predSat = false;
        IEvaluator precondEval = null;
        for (SpecCase cs : spec.cases()) {
            precondEval = checkPre(cs, fconv);
            reporter.restoringSpecFields();
            if (precondEval.hasSolution()) {
                Log.debug(precondEval.trace());
                predSat = true;
                fconv.boundSpecFields(precondEval);
                Iterator<? extends ISquanderResult> res = ensurePost(cs, fconv);
                if (res.hasNext())
                    return res;
            }
        }
        if (!predSat) {
            System.err.println("Unsat core: ");
            System.err.println(precondEval.unsatCore());
            throw new RuntimeException("pre-condition is not satisfied");
        }
        else
            throw new RuntimeException("no solution");
    }
    
    /**
     * Check pre-conditions in the specification case.
     *
     * @assumes all values of concrete fields are in bounds
     * @assumes all values of spec fields are in bounds
     * 
     * @param cs
     * @param options
     * @return non-null if the pre-condition is satisfied
     */
    protected final IEvaluator checkPre(SpecCase cs, ForgeConverter fconv) {
        ForgeExpression pre = getPreSpec(cs);
        Log.debug("===============================================");
        Log.log  (" --------- Checking pre-condition -------------");
        Log.debug("===============================================");
        Log.debug(ExpressionUtil.prettyPrint(pre));
        Log.debug("===============================================");
        Log.debug("------------------- Bounds --------------------");
        Log.debug("===============================================");
        Log.debug(fconv.printBounds());
        
        Set<GlobalVariable> modifies = Collections.<GlobalVariable>emptySet();
        return exeSpec(pre, modifies, fconv);
    }

    /**
     * Modifies the heap to ensure the post-condition of the specification
     * case.
     *
     * @assumes that only relations in modifies clause can be changed
     * @assumes that new instances should have their relations mentioned in frame
     * @assumes that specification fields are bounded to literals according to their abstraction function
     * 
     * @param cs
     * @param options
     * @return
     */
    protected final Iterator<? extends ISquanderResult> ensurePost(SpecCase cs, ForgeConverter fconv) {
        ForgeExpression post = getPostSpec(cs, fconv);

        Log.debug("===============================================");
        Log.log  ("----------- Ensuring post-condition -----------");
        Log.debug("===============================================");
        Log.debug(ExpressionUtil.prettyPrint(post));
        Log.debug("===============================================");
        Log.debug("----------------- Modifies --------------------");
        Log.debug("===============================================");
        Log.debug(cs.frame().modifiable().toString());
        Log.debug("===============================================");
        Log.debug("------------------- Bounds --------------------");
        Log.debug("===============================================");
        Log.debug(fconv.printBounds());
        
        Set<GlobalVariable> modifiable = cs.frame().modifiable();
        Set<GlobalVariable> mod = getModsForPostState(fconv, cs);
        IEvaluator ie = exeSpec(post, mod, fconv);
        return Iterators.singleton(new SquanderResult(ie, fconv, modifiable));
    }

    protected Set<GlobalVariable> getModsForPostState(ForgeConverter fconv, SpecCase sc) {
        // We have to pass all global variables to forge Spec because some specs may refer to 
        // "old" values of non-modifiable specs.  We also add additional constraints to 
        // enforce that the values of non-modifiable relations remain the same
//        return sc.frame().modifiable();
        return fconv.forgeScene().program().globalVariables();
    }
    
    protected ForgeExpression getPreSpec(SpecCase cs) {
        return cs.pre().and(cs.spec().abstractConstraint());
    }

    protected ForgeExpression getPostSpec(SpecCase cs, ForgeConverter fconv) {
        HashSet<GlobalVariable> unmod = new HashSet<GlobalVariable>();
        unmod.addAll(fconv.forgeScene().program().globalVariables());
        unmod.removeAll(cs.frame().modifiable());
        return cs.spec().abstractConstraint()
                .and(cs.spec().funcConstraint())
                .and(cs.post())
                .and(cs.frame().condition())
                .and(cs.spec().wellformedPost(unmod));
    }

    protected Spec getSpec(ForgeConverter fconv) { 
        if (spec == null) {
            spec = fconv.getSpec();
        }
        return spec;
    }
    
    /**
     * Executes the given forge expression.
     */
    protected IEvaluator exeSpec(ForgeExpression spec, Set<GlobalVariable> modifies, ForgeConverter fconv) {
        ForgeCFG.Spec fs = ForgeCFG.specification(fconv.proc(), modifies, spec);
        ForgeSolver solver = new ForgeSolver(fs, fconv.forgeOptions());
        Options opts = fconv.javaScene().methodSpec().options();
        if (opts != null && opts.solveAll()) {
            return new ForgeEval(solver.runAll(fs, fconv.forgeBounds()), fconv);
        } else {
            return new ForgeEval(Collections.singleton(solver.run(fconv.forgeBounds())).iterator(), fconv);
        }
    }

}
/*! @} */
