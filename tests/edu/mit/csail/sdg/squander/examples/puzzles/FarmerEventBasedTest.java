package edu.mit.csail.sdg.squander.examples.puzzles;

import org.junit.Assert;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;
import edu.mit.csail.sdg.squander.errors.NoSolution;

import static edu.mit.csail.sdg.squander.examples.puzzles.FarmerEventBased.*;

public class FarmerEventBasedTest extends MySquanderTestBase {
    @Test
    public void testFarmerNoSol() {
        Assert.assertNull("Didn't expect to find a solution within 7 steps", exeFarmer(7));        
    }
    
    @Test
    public void testFarmer() {
        FarmerEventBased[] steps = exeFarmer(8);
        int n = steps.length;
        for (int i = 0; i < 4; i++) Assert.assertTrue("Not everyone is at the near side at the beginning", steps[0].objs[i]);
        for (int i = 0; i < 4; i++) Assert.assertFalse("Not everyone is at the far side at the end", steps[n-1].objs[i]);
        for (int si = 1; si < n; si++) {
            Assert.assertNotSame("Farmer didn't cross at step " + si, steps[si-1].objs[FARMER], steps[si].objs[FARMER]);
            int cnt = 0;
            for (int i = 1; i < 4; i++) {
                if (steps[si-1].objs[i] != steps[si].objs[i]) 
                    cnt++;
            }
            Assert.assertTrue("More than one object (beside the farmer) crossed at step " + si, cnt <= 1);
        }
    }

    private FarmerEventBased[] exeFarmer(int maxSteps) {        
        try {
            FarmerEventBased[] x = FarmerEventBased.findTrace(maxSteps);
            return x;
        } catch (NoSolution e) {
            return null;
        }
    }
}
