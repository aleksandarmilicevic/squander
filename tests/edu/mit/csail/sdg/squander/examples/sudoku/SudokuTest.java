/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.sudoku;

import org.junit.Assert;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;

public class SudokuTest extends MySquanderTestBase {

    @Test
    public void testSudoku1Small() {
        Sudoku1 sudoku = Sudoku1.parse("4 | 0 1 0 0 | 0 0 3 0 | 0 3 0 0 | 0 0 4 0");
        sudoku.solve();
        Assert.assertEquals("3 1 2 4 2 4 3 1 4 3 1 2 1 2 4 3", sudoku.printSimple());
    }
    
    @Test
    public void testSudoku2Small() {
        Sudoku2 sudoku = Sudoku2.parse("4 | 0 1 0 0 | 0 0 3 0 | 0 3 0 0 | 0 0 4 0");
        sudoku.solve();
        Assert.assertEquals("3 1 2 4 2 4 3 1 4 3 1 2 1 2 4 3", sudoku.printSimple());
    }
    
    @Test 
    public void testSudoku3Small() {
        Sudoku3 sudoku = Sudoku3.parse("4 | 0 1 0 0 | 0 0 3 0 | 0 3 0 0 | 0 0 4 0");
        sudoku.solve();
        Assert.assertEquals("3 1 2 4 2 4 3 1 4 3 1 2 1 2 4 3", sudoku.printSimple());
    }
    
    @Test 
    public void testSudoku5Small() {
        Sudoku5 sudoku = Sudoku5.parse("4 | 0 1 0 0 | 0 0 3 0 | 0 3 0 0 | 0 0 4 0");
        sudoku.solve();
        Assert.assertEquals("3 1 2 4 2 4 3 1 4 3 1 2 1 2 4 3", sudoku.printSimple());
    }
    
    @Test 
    public void testSudoku6Small() {
        Sudoku6 sudoku = Sudoku6.parse("4 | 0 1 0 0 | 0 0 3 0 | 0 3 0 0 | 0 0 4 0");
        sudoku.solve();
        Assert.assertEquals("3 1 2 4 2 4 3 1 4 3 1 2 1 2 4 3", sudoku.printSimple());
    }
    
    @Test 
    public void testSudoku7Small() {
        Sudoku7 sudoku = Sudoku7.parse("4 | 0 1 0 0 | 0 0 3 0 | 0 3 0 0 | 0 0 4 0");
        sudoku.solve();
        Assert.assertEquals("3 1 2 4 2 4 3 1 4 3 1 2 1 2 4 3", sudoku.printSimple());
    }
    
    @Test 
    public void testSudoku9Small() {
        Sudoku9 sudoku = Sudoku9.parse("4 | 0 1 0 0 | 0 0 3 0 | 0 3 0 0 | 0 0 4 0");
        sudoku.solve();
        Assert.assertEquals("3 1 2 4 2 4 3 1 4 3 1 2 1 2 4 3", sudoku.printSimple());
    }
    
//    @Test 
//    public void testSudoku8Small() {
//        Sudoku8 sudoku = Sudoku8.parse("4 | 0 1 0 0 | 0 0 3 0 | 0 3 0 0 | 0 0 4 0");
//        sudoku.solve();
//        Assert.assertEquals("3 1 2 4 2 4 3 1 4 3 1 2 1 2 4 3", sudoku.printSimple());
//    }
}
/*! @} */
