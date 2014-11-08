/*! \addtogroup Logging 
 * Logging module. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.log;

import java.io.PrintStream;

import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

/**
 * Simple logging facility
 * 
 * @author Aleksandar Milicevic
 */
public class Log {

    public enum Level {
        ALL, TRACE, DEBUG, LOG, WARN, ERROR, FATAL, NONE
    }
    
    private static PrintStream out = System.out;

//    static {
//        try {
//            out = new PrintStream(new java.io.File("squander.log"));
//        } catch (java.io.FileNotFoundException e) {
//        } 
//    }
    
    public static void trace(String msg) {
        trace(msg.replaceAll("%", "%%"), new Object[0]);
    }

    public static void trace(String msg, Object... args) {
        myLog(Level.TRACE, msg, args);
    }

    public static void debug(String msg) {
        debug(msg.replaceAll("%", "%%"), new Object[0]);
    }

    public static void debug(String msg, Object... args) {
        myLog(Level.DEBUG, msg, args);
    }

    public static void log(String msg) {
        log(msg.replaceAll("%", "%%"), new Object[0]);
    }

    public static void log(String msg, Object... args) {
        myLog(Level.LOG, msg, args);
    }

    public static void warn(String msg) {
        warn(msg.replaceAll("%", "%%"), new Object[0]);
    }

    public static void warn(String msg, Object... args) {
        myLog(Level.WARN, msg, args);
    }

    public static void error(String msg, Object... args) {
        myLog(Level.ERROR, msg, args);
    }

    private static void myLog(Level logLevel, String msg, Object... args) {
        if (logLevel.ordinal() < SquanderGlobalOptions.INSTANCE.log_level.ordinal())
            return;
        msg = String.format(msg, args);
        out.println(String.format("[%s] %s", logLevel, 
                msg.replaceAll("\\n", String.format("\n[%s] ", logLevel))));
    }

}
/*! @} */
