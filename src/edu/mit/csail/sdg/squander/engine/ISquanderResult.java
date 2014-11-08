/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.Iterator;

import edu.mit.csail.sdg.squander.absstate.ObjTupleSet;
import forge.program.ForgeExpression;

/**
 * Represents the result of executing a specification.  
 * The result must also know how to update the Java heap
 * to reflect the solution represented by that result.
 * 
 * @author Aleksandar Milicevic
 */
public interface ISquanderResult {

    /**
     * The main purpose is to provide an interface for evaluating Forge
     * expressions against a solution.
     * 
     * @author Aleksandar Milicevic
     */
    public static interface IEvaluator {
        /** Evaluates a given Forge expression and returns a set of Java object tuples */
        ObjTupleSet evaluate(ForgeExpression expr);
        /** Whether a solution was found */
        boolean hasSolution();
        /** Textual trace */
        String trace();
        /** Textual unsat core */
        String unsatCore();
        /** Textual statistics */
        String stats();
        /** Returns the next solution or null if it doesn't exist */
        IEvaluator nextSolution();
        /** Parses a string into a forge expression */
        ForgeExpression parse(String expr);
    }

    /** Weather or not a solution was found */
    public boolean hasSolution();
    /** Optional string representation of the solution. Used for debugging purposes */
    public String getTrace();
    /** Optional statistics about the solving */
    public String getStats();
    
    /**
     * Returns the Java object that is the return value of the method under
     * Squander analysis. If the method under Squander analysis has void return
     * type, <code>null</code> is returned.
     */
    public <R> R getReturnValue();

    /**
     * Restores the Java heap space from the given <code>Results</code> that
     * must contain valid solution. If the solution for this problem wasn't
     * found, a runtime exception is thrown.
     */
    public void restoreJavaHeap();
    
    /** Returns a string representation of the unsat core if a solution wasn't found (if known) */
    public String unsatCore();

    /**
     *  Finds a different solution and updates the heap if the solution is found.
     *  Returns whether a solution was found or not.  
     */
    public boolean findNext();

    /**
     * Exports the solution (if one was found) to an AlloyViz XML file
     * @param extraSkolems - a list of functions to evaluate and export as skolems; may be null
     * @param comparator - if custom ordering between atoms of a sig is desired; may be null
     */
    public void exportToAlloyVizInst(PrintStream ps, ExtraSkolems extraSkolems, 
            Comparator<? super Object> comparator);
    
    /**
     * Returns the value of the spec field with the given name as a sequence of objects. 
     * This works only for unary spec fields, so it shouldn't be used.  
     */
    @Deprecated
    public Iterator<Object> getSpecField(String specFieldName);
    

}
/*! @} */
