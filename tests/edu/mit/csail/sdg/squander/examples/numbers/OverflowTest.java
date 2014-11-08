package edu.mit.csail.sdg.squander.examples.numbers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;
import edu.mit.csail.sdg.squander.errors.NoSolution;
import edu.mit.csail.sdg.squander.examples.overflow.OverflowWithFreeVars;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

public class OverflowTest extends MySquanderTestBase {
        
    public abstract class Tester {
        public abstract void doTest(int i, int j);
    }
    
    public abstract class GenericTester extends Tester {
        @Override
        public void doTest(int i, int j) {
            if (skip(i, j))
                return;
            int min = min();
            int max = max();
            String op = getOp();
            int res = exeJava(i, j); 
            if (res > max || res < min) {
                try {
                    int x = exeSquander(i, j);
                    Assert.fail(String.format("Overflow not detected: %s %s %s != %s", i, op, j, x));
                } catch (Exception e) {}
            } else {
                try {
                    int x = exeSquander(i, j);
                    Assert.assertEquals(String.format("Wrong result: %s %s %s != %s", i, op, j, x), res, x);
                } catch (Exception e) {
                    Assert.fail(String.format("No solution for %s %s %s", i, op, j));
                }
            }
        }
        
        protected abstract int exeJava(int i, int j);
        protected abstract int exeSquander(int i, int j);
        protected abstract String getOp();
        protected boolean skip(int i, int j) { return false; }
    }
    
    public abstract class GenericTesterWithDefaults extends Tester {
        static final int DEFAULT_RESULT = -1;
        
        @Override
        public void doTest(int i, int j) {
            int min = min();
            int max = max();
            String op = getOp();
            int res; 
            boolean of = false;
            try {
                res = exeJava(i, j);
                if (res > max || res < min) {
                    of = true;
                    res = DEFAULT_RESULT;
                }
            } catch (Exception e) {
                of = true;
                res = DEFAULT_RESULT;
            }
            try {
                int x = exeSquander(i, j, DEFAULT_RESULT);
                String msg = of
                        ? String.format("Expected the default value (%d) due to overflow (%d %s %d) but got %d", 
                                DEFAULT_RESULT, i, op, j, x)
                        : String.format("Wrong result: %s %s %s != %s", i, op, j, x);                    
                Assert.assertEquals(msg, res, x);
            } catch (Exception e) {
                String msg = String.format("Expected the default value (%d) due to overflow (%d %s %d) but got exception: %s",
                        DEFAULT_RESULT, i, op, j, e.getClass().getSimpleName() + ": " + e.getMessage());
                Assert.fail(msg);
            }
        }
        
        protected abstract int exeJava(int i, int j);
        protected abstract int exeSquander(int i, int j, int def);
        protected abstract String getOp();
    }
    
    private static final int bw = 3;
    
    private int oldBw;
    private boolean oldOf; 
    
    @Override
    @Before
    public void setUp() {
        oldBw = SquanderGlobalOptions.INSTANCE.min_bitwidth;
        oldOf = SquanderGlobalOptions.INSTANCE.noOverflow;
        SquanderGlobalOptions.INSTANCE.min_bitwidth = bw;
        SquanderGlobalOptions.INSTANCE.noOverflow = true;
    }
    
    @After
    public void tearDown() {
        SquanderGlobalOptions.INSTANCE.min_bitwidth = oldBw;
        SquanderGlobalOptions.INSTANCE.noOverflow = oldOf;
    }
    
    @Test
    public void testPlus() {
        runTestForAll(new GenericTester() {
            @Override protected int exeJava(int i, int j) { return i + j; }
            @Override protected int exeSquander(int i, int j) { return Arithmetic.plus(i, j); }
            @Override protected String getOp() { return "+"; }            
        });    
    }

    @Test
    public void testMinus() {
        runTestForAll(new GenericTester() {
            @Override protected int exeJava(int i, int j) { return i - j; }
            @Override protected int exeSquander(int i, int j) { return Arithmetic.minus(i, j); }
            @Override protected String getOp() { return "-"; }            
        });    
    }
    
    @Test
    public void testTimes() {
        runTestForAll(new GenericTester() {
            @Override protected int exeJava(int i, int j) { return i * j; }
            @Override protected int exeSquander(int i, int j) { return Arithmetic.times(i, j); }
            @Override protected String getOp() { return "*"; }            
        });    
    }
    
    @Test
    public void testDiv() {
        runTestForAll(new GenericTester() {
            @Override protected int exeJava(int i, int j) { return i / j; }
            @Override protected int exeSquander(int i, int j) { return Arithmetic.div(i, j); }
            @Override protected String getOp() { return "/"; }   
            @Override protected boolean skip(int i, int j) { return j == 0; }
        });    
    }
    
    @Test
    public void testMod() {
        runTestForAll(new GenericTester() {
            @Override protected int exeJava(int i, int j) { return i % j; }
            @Override protected int exeSquander(int i, int j) { return Arithmetic.mod(i, j); }
            @Override protected String getOp() { return "%"; }   
            @Override protected boolean skip(int i, int j) { return j == 0; }
        });    
    }
    
    @Test
    public void testPlusOrDef() {
        runTestForAll(new GenericTesterWithDefaults() {
            @Override protected int exeJava(int i, int j) { return i + j; }
            @Override protected int exeSquander(int i, int j, int d) { return Arithmetic.plusOrDef(i, j, d); }
            @Override protected String getOp() { return "+"; }            
        });    
    }

    @Test
    public void testMinusOrDef() {
        runTestForAll(new GenericTesterWithDefaults() {
            @Override protected int exeJava(int i, int j) { return i - j; }
            @Override protected int exeSquander(int i, int j, int d) { return Arithmetic.minusOrDef(i, j, d); }
            @Override protected String getOp() { return "-"; }            
        });        
    }
    
    @Test
    public void testTimesOrDef() {
        runTestForAll(new GenericTesterWithDefaults() {
            @Override protected int exeJava(int i, int j) { return i * j; }
            @Override protected int exeSquander(int i, int j, int d) { return Arithmetic.timesOrDef(i, j, d); }
            @Override protected String getOp() { return "*"; }            
        });    
    }
    
    @Test
    public void testDivOrDef() {
        runTestForAll(new GenericTesterWithDefaults() {
            @Override protected int exeJava(int i, int j) { return i / j; }
            @Override protected int exeSquander(int i, int j, int d) { return Arithmetic.divOrDef(i, j, d); }
            @Override protected String getOp() { return "/"; }            
        });    
    }
    
    @Test
    public void testModOrDef() {
        runTestForAll(new GenericTesterWithDefaults() {
            @Override protected int exeJava(int i, int j) { return i % j; }
            @Override protected int exeSquander(int i, int j, int d) { return Arithmetic.modOrDef(i, j, d); }
            @Override protected String getOp() { return "%"; }            
        });    
    }

    @Test
    public void testMin() {
        Assert.assertEquals(min(), Arithmetic.minInt());
    }
    
    @Test
    public void testMax() {
        Assert.assertEquals(max(), Arithmetic.maxInt());
    }
    
    @Test public void testChecks()       { Assert.assertTrue(Arithmetic.check1()); } 
    @Test public void testCheck1dual()   { Assert.assertTrue(Arithmetic.check1dual()); }
    @Test public void testCheck2()       { Assert.assertTrue(Arithmetic.check2()); }
    @Test public void testCheck2dual()   { Assert.assertTrue(Arithmetic.check2dual()); }
    @Test public void testCheck3()       { Assert.assertTrue(Arithmetic.check3()); }
    @Test public void testCheck4()       { Assert.assertTrue(Arithmetic.check4()); }
    @Test public void testCheck5()       { Assert.assertTrue(Arithmetic.check5()); }
    @Test public void testCheck5dual()   { Assert.assertTrue(Arithmetic.check5dual()); }
    @Test public void testCheck6()       { Assert.assertTrue(Arithmetic.check6()); }
    @Test public void testCheck6dual()   { Assert.assertTrue(Arithmetic.check6dual()); }
    @Test public void testCheck7()       { Assert.assertTrue(Arithmetic.check7()); }
    @Test public void testCheck8()       { Assert.assertTrue(Arithmetic.check8()); }
    @Test public void testCheck9()       { Assert.assertTrue(Arithmetic.check9()); }
    @Test public void testCheck9dual()   { Assert.assertTrue(Arithmetic.check9dual()); }
    @Test public void testCheck10()      { Assert.assertTrue(Arithmetic.check10()); }
    @Test public void testCheck11()      { Assert.assertTrue(Arithmetic.check11()); }
    @Test public void testCheck11dual()  { Assert.assertTrue(Arithmetic.check11dual()); }
    @Test public void testCheck11dual2() { Assert.assertTrue(Arithmetic.check11dual2()); }
    @Test public void testCheck12()      { Assert.assertTrue(Arithmetic.check12()); }
    @Test public void testCheck12dual()  { Assert.assertTrue(Arithmetic.check12dual()); }
    @Test public void testCheck12dual2() { Assert.assertTrue(Arithmetic.check12dual2()); }
    @Test public void testCheck13()      { Assert.assertTrue(Arithmetic.check13()); }
    @Test public void testCheck14()      { Assert.assertTrue(Arithmetic.check14()); }
    @Test public void testCheck15()      { Assert.assertTrue(Arithmetic.check15()); }
    @Test public void testCheck16()      { Assert.assertTrue(Arithmetic.check16()); }
    @Test public void testCheck16dual()  { Assert.assertTrue(Arithmetic.check16dual()); }
    @Test public void testCheck17()      { Assert.assertTrue(Arithmetic.check17()); }
    @Test public void testCheck18()      { Assert.assertTrue(Arithmetic.check18()); }
    @Test public void testCheck18dual()  { Assert.assertTrue(Arithmetic.check18dual()); }
    @Test public void testCheck19()      { Assert.assertTrue(Arithmetic.check19()); }
    @Test public void testCheck19dual()  { Assert.assertTrue(Arithmetic.check19dual()); }
    @Test public void testCheck20()      { Assert.assertTrue(Arithmetic.check20()); }
    @Test public void testCheck20dual()  { Assert.assertTrue(Arithmetic.check20dual()); }
    
    @Test
    public void testFreeVarsNoSolution() {
        OverflowWithFreeVars owfv = new OverflowWithFreeVars();
        try { owfv.noSol1(); Assert.fail(); } catch (NoSolution ns) {}
        try { owfv.noSol1dual(); Assert.fail(); } catch (NoSolution ns) {}
        try { owfv.noSol2(); Assert.fail(); } catch (NoSolution ns) {}
    }
    
    protected void runTestForAll(Tester t) {
        int min = min();
        int max = max();
        for (int i = min; i <= max; i++) {
            for (int j = min; j <= max; j++) {
                t.doTest(i, j);
            }
        }
    }
    
    private int min() { return -(1 << (bw - 1)); }
    private int max() { return (1 << (bw - 1)) - 1; }

}
