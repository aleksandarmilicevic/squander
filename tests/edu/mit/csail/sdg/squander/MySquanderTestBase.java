package edu.mit.csail.sdg.squander;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import kodkod.engine.satlab.SATFactory;

import org.junit.Before;

import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions.Engine;

public class MySquanderTestBase {

    protected ByteArrayOutputStream outBaos;
    protected ByteArrayOutputStream errBaos;
    
    public MySquanderTestBase() {
        SquanderGlobalOptions.INSTANCE.log_level = Log.Level.NONE;
        SquanderGlobalOptions.INSTANCE.sat_solver = SATFactory.DefaultSAT4J;
        SquanderGlobalOptions.INSTANCE.noOverflow = true;
        SquanderGlobalOptions.INSTANCE.unsat_core = false;
        // TODO: make configurable, e.g. read from env
        SquanderGlobalOptions.INSTANCE.engine = Engine.Kodkod; 
     }        
    
    @Before
    public void setUp() {
        outBaos = new ByteArrayOutputStream();
        errBaos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBaos));
        System.setErr(new PrintStream(errBaos));
    }
    
    protected String getOut() { return new String(outBaos.toByteArray()); }
    protected String getErr() { return new String(errBaos.toByteArray()); }
}
