/*!
 * \mainpage Squander
 */

/*! \addtogroup API
 * 
 * Public API.
 *  
 * @{
 */
package edu.mit.csail.sdg.squander;

import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.mit.csail.sdg.squander.engine.ISquander;
import edu.mit.csail.sdg.squander.engine.ISquanderResult;
import edu.mit.csail.sdg.squander.errors.ConsistencyError;
import edu.mit.csail.sdg.squander.errors.NoSolution;
import edu.mit.csail.sdg.squander.eventbased.State;
import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;
import edu.mit.csail.sdg.squander.parser.JFSLParserException;
import edu.mit.csail.sdg.squander.spec.JMethod;
import edu.mit.csail.sdg.squander.spec.TraceSpecConverter;
import edu.mit.csail.sdg.squander.spec.JType.Factory;
import edu.mit.csail.sdg.squander.spec.JType.Unary;
import edu.mit.csail.sdg.squander.spec.typeerrors.TypeCheckException;

/**
 * Provides a public API to Squander.
 * 
 * @author Aleksandar Milicevic (aleks@csail.mit.edu)
 */
public final class Squander {
    
    private Squander() {
        throw new UnsupportedOperationException("instantiation forbidden");
    }
    
    private static ISquander lastSqImpl; 

    // =========================================================================
    // --------------------------- public API ----------------------------------
    // =========================================================================
    
    /**
     * Executes the caller method's specification.  Method parameter types
     * are inferred from <code>methodArgs</code>.  If there's any ambiguity 
     * about that, use {@link Squander#exe(Object, Class[], Object[])}.
     * 
     * <p>
     * <b><i> Use this method only to invoke Squander from within the method
     * which you want to squander an implementation for.</i></b>
     * </p>
     * 
     * @param <R> - the return type
     * @param caller - the caller instance (<code>null</code> if static method)
     * @param methodArgs - invocation arguments
     * @return - the return value of the invocation
     */
    @SuppressWarnings("unchecked")
    public static <R> R exe(Object caller, Object ... methodArgs) {
        Class<?>[] methodParamTypes = new Class<?>[methodArgs.length];
        for (int i = 0; i < methodArgs.length; i++)
            if (methodArgs[i] == null)
                methodParamTypes[i] = Object.class;
            else
                methodParamTypes[i] = methodArgs[i].getClass();
        return (R) exe(caller, methodParamTypes, methodArgs);
    }

    /**
     * Executes the caller method's specification.
     * 
     * <p>
     * <b><i> Use this method only to invoke Squander from within the method
     * which you want to squander an implementation for.</i></b>
     * </p>
     * 
     * @param <R> - the return type
     * @param caller - the caller instance (<code>null</code> if static method)
     * @param methodParamTypes - method parameter types
     * @param methodArgs - invocation arguments
     * @return - the return value of the invocation
     */
    @SuppressWarnings("unchecked")
    public static <R> R exe(Object caller, Class<?>[] methodParamTypes, Object[] methodArgs) {
        Throwable t = new Throwable();
        String clsName = getCallerClassName(t);
        String methodName = getCallerMethod(t);
        return (R) exe(caller, clsName, methodName, methodParamTypes, methodArgs);
    }

    /**
     * Executes the given method's specification.
     * <b><i>Assumes that the method doesn't have any parameters.</b></i>
     * 
     * @param <R> - the return type
     * @param caller - the caller instance (<code>null</code> if static method)
     * @param clsName - declaring class full name
     * @param methodName - method name
     * @return - the return value of the invocation
     */
    @SuppressWarnings("unchecked")
    public static <R> R exe(Object caller, String clsName, String methodName) {
        return (R) exe(caller, clsName, methodName, new Class<?>[0], new Object[0]);
    }

    /**
     * The most general form
     * 
     * @param <R> - the return type
     * @param caller - the caller instance (<code>null</code> if static method)
     * @param clsName - declaring class full name
     * @param methodName - method name
     * @param methodParamTypes - method parameter types
     * @param methodArgs - invocation arguments
     * @return - the return value of the invocation
     */
    @SuppressWarnings("unchecked")
    public static <R> R exe(Object caller, String clsName, String methodName, 
            Class<?>[] methodParamTypes, Object[] methodArgs) {
        Log.log("Using %s engine", SquanderGlobalOptions.INSTANCE.engine);
        ISquander sq = SquanderGlobalOptions.INSTANCE.getSquanderImpl();
        lastSqImpl = sq;
        try {
            return (R) sq.magic(caller, clsName, methodName, methodParamTypes, methodArgs);
        } catch (ConsistencyError e) {
            Log.error("*** CONSISTENCY ERROR ***");
            Log.error(e.getMessage());
            throw e;
        } catch (JFSLParserException e) {
            if (e instanceof TypeCheckException)
                Log.error("*** TYPECHECK ERROR ***");
            else
                Log.error("*** PARSE ERROR ***");
            Log.error(e.getMessage()); 
            if (e.source() != null) {             
                String src = e.source();
                int col = e.column();
//                int start = e.startIdx();
//                int stop = e.stopIdx();
                StringBuilder srcSnippet = new StringBuilder();
                StringBuilder marker = new StringBuilder();
                srcSnippet.append(" [...] ");
                marker.append("       ");
                int w = 35;
                for (int i = col - w; i < col + w; i++) {
                    if (i < 0 || i >= src.length())
                        continue;
                    srcSnippet.append(src.charAt(i));
                    marker.append(i == col ? "^" : "-");
                }
                srcSnippet.append(" [...] ");
                marker.append("       ");
                Log.error(srcSnippet.toString());
                Log.error(marker.toString());
                
//                Log.error(e.source());
//                StringBuilder msg = new StringBuilder();
//                for (int i = 0; i < e.source().length(); i++) 
//                    msg.append(i == col ? "^" : "-");
//                Log.error(msg.toString());
            }
            throw e;
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static <R> R[] findTrace(Class<? extends State> cls, int maxSteps) {
        try {
            R[] states = (R[]) Array.newInstance(cls, maxSteps);
            for (int i = 0; i < maxSteps; i++) {
                states[i] = (R) cls.newInstance();
            }
            ISquander sq = SquanderGlobalOptions.INSTANCE.getSquanderImpl();
            lastSqImpl = sq;
            JMethod mthd = constructTraceSolveMethod(cls);
            
            int n = sq.magic(null, mthd, new Object[] { states });
            R[] result = (R[]) Array.newInstance(cls, n);
            System.arraycopy(states, 0, result, 0, n);
            return result;
        } catch (NoSolution e) {
            return null;
        } catch (JFSLParserException e) {
            throw new RuntimeException("Error while parsing: \n" + e.source(), e);  
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static JMethod constructTraceSolveMethod(Class<?> cls) {
        Map<String, Unary> params = new LinkedHashMap<String, Unary>();
        Class<?> stepsCls = Array.newInstance(cls, 0).getClass();
        params.put("steps", Factory.instance.newJType(stepsCls));
        JMethod jm = new JMethod("solveTrace", Factory.instance.newJType(cls), params, Factory.instance.integerType(), true);
        jm.setSpec(new TraceSpecConverter(cls).convertToMethodSpec(jm));        
        return jm;
    }
    
    /**
     * Returns the result of the last Squander execution.
     */
    public static ISquanderResult getLastResult() {
        if (lastSqImpl != null)
            return lastSqImpl.getLastResult();
        return null;
    }

    private static String getCallerMethod(Throwable t) {
        return getRightStackTraceElem(t).getMethodName();
    }

    private static String getCallerClassName(Throwable t) {
        return getRightStackTraceElem(t).getClassName();
    }

    private static StackTraceElement getRightStackTraceElem(Throwable t) {
        StackTraceElement[] trace = t.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            if (!trace[i].getClassName().equals(Squander.class.getName()))
                return trace[i];
        }
        return null;
    }
}

/*! @} */