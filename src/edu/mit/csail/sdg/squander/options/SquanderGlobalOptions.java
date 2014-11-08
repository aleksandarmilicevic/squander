/*! \addtogroup Configuration Configuration 
 * This module is responsible for keeping configuration parameters. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.options;

import kodkod.engine.satlab.SATFactory;
import edu.mit.csail.sdg.squander.engine.ISquander;
import edu.mit.csail.sdg.squander.engine.SquanderImpl;
import edu.mit.csail.sdg.squander.engine.SquanderReporter;
import edu.mit.csail.sdg.squander.engine.kk.SquanderKodkodImpl;
import edu.mit.csail.sdg.squander.engine.kk.SquanderKodkodPartImpl;
import edu.mit.csail.sdg.squander.log.Log.Level;

/**
 * Various general options, not related to particular specification executions
 * 
 * @author Aleksandar Milicevic
 */
public class SquanderGlobalOptions {
    
    public static enum Engine {
        Forge, Kodkod, KodkodPart //, Kodkod2
    }
    
    public static SquanderGlobalOptions INSTANCE = new SquanderGlobalOptions();
    
    public Level log_level = Level.DEBUG;
    public Engine engine = Engine.Kodkod;
    public SATFactory sat_solver = SATFactory.MiniSat; //SATFactory.DefaultSAT4J; //SATFactory.MiniSat;  //SATFactory.MiniSatExternal;

    /** forbid overflow */
    public boolean noOverflow = true;
    /** minimum bitwidth to use */
    public int min_bitwidth = 5;
    /** should the unsat core be computed in case of no solution (it potentially degrades performance) */
    public boolean unsat_core = false;
    /** whether the quantifiers whose domains are known statically should be unrolled */
    public boolean pre_eval = false;
    /** no need to change this */
    public boolean desugar_quants = true;

    public SquanderReporter reporter = SquanderReporter.INSTANCE;

    public ISquander getSquanderImpl() {
        switch (engine) {
        case Forge:
            return new SquanderImpl();
        case Kodkod:
            return new SquanderKodkodImpl();
//        case Kodkod2:
//            return new SquanderKodkod2Impl();
        case KodkodPart: 
            return new SquanderKodkodPartImpl();
        default:
            return new SquanderKodkodImpl();
        }
    }

}
/*! @} */
