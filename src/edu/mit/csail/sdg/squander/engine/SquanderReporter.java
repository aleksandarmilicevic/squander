/*! \addtogroup Engine Engine 
 * This module contains the core classes responsible for executing specifications 
 * @{ 
 */
package edu.mit.csail.sdg.squander.engine;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.utils.ReflectionUtils;
import forge.solve.ForgeReporter;

/**
 * Provides various callback methods for the Squander engine to invoke at certain points.
 * It is primarily used for logging and timing purposes.
 * 
 * @author Aleksandar Milicevic
 */
public class SquanderReporter extends ForgeReporter {

    public static final String BUILDING_ANALYSIS = "buildingAnalysis";
    public static final String TRANSLATING_FORGE = "translatingForge";
    public static final String PACKAGIN_ANALYSIS = "packaginAnalysis";
    public static final String LOADING_JIMPLE = "loadingJimple";
    public static final String ANALYSIS = "analysis";
    public static final String TRANSLATING_SPECS = "translatingSpecs";
    public static final String CREATING_BOUNDS = "creatingBounds";
    public static final String CREATING_KK_BOUNDS = "creatingKKBounds";
    public static final String RESTORING_HEAP = "restoringHeap";
    public static final String RESTORING_SF = "restoringSpecFields";
    public static final String TRAVERSING_HEAP = "traversingHeap";
    public static final String CREATING_KK_UNIV = "creatingKKUniverse";
    public static final String CREATING_FRESH_OBJECTS = "creatingFreshObjects";
    public static final String CONVERTING_METHOD = "convertingMethod";
    public static final String LOADING_JAVA_SCENE = "loadingJavaScene";
    
    public static final String TRANSLATING_TO_KODKOD = "translatingToKodkod";
    public static final String TRANSLATING_TO_CNF = "translatingToCNF";
    public static final String TRANSLATING_TO_BOOLEAN = "translatingToBoolean";
    public static final String TRANSOFMING_PROCEDURE = "transofmingProcedure";
    public static final String SOLVING_CNF = "solvingCNF";
    public static final String SOLVING_ANALYSIS = "solvingAnalysis";
    public static final String SCOLEMIZING = "scolemizing";
    public static final String OPTIMIZING_BOUNDS_AND_FORMULA = "optimizingBoundsAndFormula";
    public static final String GENERATING_SBP = "generatingSBP";
    public static final String FLATTENING = "flattening";
    public static final String DETECTING_SYMMETRIES = "detectingSymmetries";
    public static final String DETECTED_SYMMETRIES = "detectedSymmetries";

    public static final SquanderReporter INSTANCE = new SquanderReporter();

    private Map<String, Long> times = new LinkedHashMap<String, Long>();
    
    private long sTime; 
    private String task;
    private boolean busy; 
    
    private void start(String task) {
        Log.log("^^^ started: " + task);
        this.sTime = System.currentTimeMillis(); 
        this.task = task;
        this.busy = true;
    }
    
    public void end() {
        if (!busy) return;
        long dt = System.currentTimeMillis() - sTime;
        times.put(task, dt);
        this.busy = false;
        Log.log("~~~    done: %s (%ss)", task, dt/1000.0);
    }

    private kodkod.engine.config.Reporter kkRep;
    public kodkod.engine.config.Reporter kkReporter() { 
        if (kkRep == null) {
            Field f = ReflectionUtils.getField(getClass(), "kkReporter");
            kkRep = (kodkod.engine.config.Reporter) ReflectionUtils.getFieldValue(this, f);
        }
        return kkRep; 
    }
    
    public void loadingJavaScene()     { end(); start(LOADING_JAVA_SCENE); }
    public void convertingMethod()     { end(); start(CONVERTING_METHOD); }
    public void creatingFreshObjects() { end(); start(CREATING_FRESH_OBJECTS); }
    public void traversingHeap()       { end(); start(TRAVERSING_HEAP); }
    public void creatingBounds()       { end(); start(CREATING_BOUNDS); }
    public void translatingSpecs()     { end(); start(TRANSLATING_SPECS); }
    public void creatingKodkodUniverse()   { end(); start(CREATING_KK_UNIV); }
    public void creatingKodkodBounds() { end(); start(CREATING_KK_BOUNDS); }
    public void restoringJavaHeap()    { end(); start(RESTORING_HEAP); }
    public void restoringSpecFields()  { end(); start(RESTORING_SF); }
    public void startedAnalysis()      { end(); start(ANALYSIS); }
    public void packagingAnalysis()    { end(); start(PACKAGIN_ANALYSIS); }
    public void translatingForge()     { end(); start(TRANSLATING_FORGE); }
    public void buildingAnalysis()     { end(); start(BUILDING_ANALYSIS); }
    public void finishedAnalysis()     { end(); }

    @Override
    public void detectedSymmetries(Set<?> parts) {
        end(); 
        start(DETECTED_SYMMETRIES);
    }

    @Override
    public void detectingSymmetries(Object bounds) {
        end(); 
        start(DETECTING_SYMMETRIES);
    }

    @Override
    public void flattening(Object circuit) {
        end(); 
        start(FLATTENING);
    }

    @Override
    public void generatingSBP() {
        end(); 
        start(GENERATING_SBP);
    }

    @Override
    public void optimizingBoundsAndFormula() {
        end(); 
        start(OPTIMIZING_BOUNDS_AND_FORMULA);
    }

    @Override
    public void skolemizing(Object decl, Object skolem, List<?> context) {
        end(); 
        start(SCOLEMIZING);
    }

    @Override
    public void solvingAnalysis() {
        end(); 
        start(SOLVING_ANALYSIS);
    }

    @Override
    public void solvingCNF(int primaryVars, int vars, int clauses) {
        end(); 
        start(SOLVING_CNF);
    }

    @Override
    public void transformingProcedure() {
        end(); 
        start(TRANSOFMING_PROCEDURE);
    }

    @Override
    public void translatingToBoolean(Object formula, Object bounds) {
        end(); 
        start(TRANSLATING_TO_BOOLEAN);
    }

    @Override
    public void translatingToCNF(Object circuit) {
        end(); 
        start(TRANSLATING_TO_CNF);
    }

    @Override
    public void translatingToKodkod() {
        end(); 
        start(TRANSLATING_TO_KODKOD);
    }

    public long getTaskTime(String task) {
        return times.get(task);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String task : times.keySet()) {
            Long t = times.get(task);
            sb.append(String.format("## task_%s: %d seconds %d milliseconds", task, t/1000, t%1000));
            sb.append("\n");
        }
        return sb.toString();
    }

    private SquanderReporter() {}

}
/*! @} */
