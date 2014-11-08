/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine;

import java.lang.reflect.Method;

import edu.mit.csail.sdg.squander.spec.JMethod;

/**
 * Interface to the most essential Squander functions.
 * 
 * @author Aleksandar Milicevic
 */
public interface ISquander {

    /**
     * Executes the specification for the given method.
     * 
     * @param <R> - the return type
     * @param caller - the caller instance (<code>null</code> if the method is static)
     * @param clsName - name of the declaring class
     * @param methodName - method name
     * @param methodParamTypes - method parameter types
     * @param methodArgs - actual method arguments
     * @return - the result of the method's execution (or nothing if the type is <code>void</code>)
     */
    <R> R magic(Object caller, String clsName, String methodName, Class<?>[] methodParamTypes, Object[] methodArgs);
    
    /**
     * Executes the specification of the given Java method
     * 
     * @param <R> - the return type
     * @param caller - the caller instance (<code>null</code> if the method is static)
     * @param method - Java method 
     * @param methodArgs - actual method arguments 
     * @return - the result of the method's execution (or nothing if the type is <code>void</code>)
     */
    <R> R magic(Object caller, Method method, Object[] methodArgs);
    
    /**
     * Executes the specification of the given JMethod
     * 
     * @param <R> - the return type
     * @param caller - the caller instance (<code>null</code> if the method is static)
     * @param method - Squander's internal representation of a method (which includes method's spec). 
     *                 This can be used to execute specs that are not necessarily given
     *                 as annotations on a Java method
     * @param methodArgs - actual method arguments
     * @return - the result of the method's execution (or nothing if the type is <code>void</code>)
     */
    <R> R magic(Object caller, JMethod method, Object[] methodArgs);

    /**
     * Returns the result of the last execution.
     */
    ISquanderResult getLastResult();

}
/*! @} */
