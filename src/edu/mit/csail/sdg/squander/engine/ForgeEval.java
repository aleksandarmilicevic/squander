/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine;

import java.util.Iterator;

import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import edu.mit.csail.sdg.squander.engine.ISquanderResult.IEvaluator;
import forge.program.ForgeExpression;
import forge.solve.ForgeConstant;
import forge.solve.ForgeSolution;

/**
 * Evaluator based on the {@link ForgeSolution} class.
 * 
 * @author Aleksandar Milicevic (aleks@csail.mit.edu)
 */
public class ForgeEval implements ISquanderResult.IEvaluator {
    private Iterator<ForgeSolution> solutions;
    private ForgeSolution solution;
    private ForgeConverter fconv;

    public ForgeEval(Iterator<ForgeSolution> solutions, ForgeConverter fconv) {
        this.solutions = solutions;
        this.solution = solutions.next();
        this.fconv = fconv;
    }

    @Override
    public ObjTupleSet evaluate(ForgeExpression expr) {
        ForgeConstant fc = solution.trace().evaluate(expr);
        return ObjTupleSet.convertFrom(fc, fconv);
    }

    @Override
    public boolean hasSolution() {
        return solution != null && solution.trace() != null;
    }

    @Override
    public IEvaluator nextSolution() {
        if (solutions.hasNext()) 
            solution = solutions.next();
        else
            solution = null;
        return this;
    }

    @Override
    public String unsatCore() {
        return "unknown";
    }

    @Override
    public String trace() {
        return solution.trace().toString();
    }

    @Override
    public String stats() {
        return "unknown stats";
    }

    @Override
    public ForgeExpression parse(String expr) {
        return new PostExeTranslator(fconv).translate(expr);
    }
    
}
/*! @} */
