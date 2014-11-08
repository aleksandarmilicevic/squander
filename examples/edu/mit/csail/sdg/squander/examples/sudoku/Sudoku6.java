/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.sudoku;

import java.util.HashSet;
import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

/**
 * @author Aleksandar Milicevic
 */
//@Invariant({
//    "this.grid[int][int] in {x : int | x >= 0 && x <= this.n}"
//})
public class Sudoku6 {

    private int[][] grid; 
    private final int n; 
    private final int m; 
    
    private final Set<Set<Integer>> regions; // auxiliary, just to store all sub-grids

    public Sudoku6(int n) {
        assert Math.sqrt(n) * Math.sqrt(n) == n : "n must be a square number";
        this.n = n;
        this.m = (int) Math.sqrt(n);
        this.grid = new int[n][n];

        // init regions
        this.regions = new HashSet<Set<Integer>>();
        for (int i = 0; i < m; i++) {
            Set<Integer> region = new HashSet<Integer>();
            for (int j = 0; j < m; j++) {
                region.add(i*m + j);
            }
            regions.add(region);
        }
    } 
    
    @Ensures("this.grid = @old(this.grid) + row -> col -> value")
    @Modifies("this.grid[int].elts")
    public void setCellValue(int row, int col, int value) {
        grid[row][col] = value;
    }

    @Ensures("return = this.grid[row][col]")
    public int getCellValue(int row, int col) {
        return grid[row][col];
    }

    @Ensures({
        "all x: {0 ... this.n - 1} | " + 
            "this.grid[x][int] = {1 ... this.n} && " + 
            "this.grid[int][x] = {1 ... this.n}",
        "all r1, r2: this.regions.elts |" +
	        "this.grid[r1.elts][r2.elts] = {1 ... this.n}",
    })
    @Modifies("this.grid[int].elts | _<2> = 0")
    @Options(ensureAllInts=false)
    public void solve() {
        Squander.exe(this);
    }
    
    public String printSimple() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                sb.append(getCellValue(i, j)).append(" ");
        return sb.toString().trim();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int m = (int) Math.sqrt(n); 
        for (int i = 0; i < n; i++) {
            if (i % m == 0)
                printHLine(sb, n, m);
            for (int j = 0; j < n; j++) {
                if (j % m == 0)
                    sb.append("| ");
                sb.append(getCellValue(i, j)).append(" ");
            }
            sb.append("|\n");
        }
        printHLine(sb, n, m);
        return sb.toString();
    }

    private void printHLine(StringBuilder sb, int n2, int m) {
        for (int i = 0; i < n; i++) {
            if (i % m == 0)
                sb.append("+-");
            sb.append("--");
        }
        sb.append("+\n");
    }
    
    public static Sudoku6 parse(String puzzle) {
        String[] vals = puzzle.replaceAll("\\|", " ").split("\\s+");
        assert (vals.length > 0);
        int n = Integer.parseInt(vals[0]); 
        assert vals.length == n*n + 1 : "must provide exactly " + n*n + " cell values";
        Sudoku6 sudoku = new Sudoku6(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                int val = Integer.parseInt(vals[1 + i*n + j]);
                if (val > 0)
                    sudoku.setCellValue(i, j, val);
            }
        return sudoku;
    }
    
    public static void main(String[] args) {
        SquanderGlobalOptions.INSTANCE.log_level = Level.DEBUG;
//        Sudoku6 sudoku = Sudoku6.parse("4 | 0 1 0 0 |" +
//                                         "| 0 0 3 0 |" +
//                                         "| 0 3 0 0 |" +
//                                         "| 0 0 4 0 |");
        Sudoku6 sudoku = Sudoku6.parse("9 | 8 0 0 6 0 0 0 0 2 |" +
                                         "| 0 4 0 0 5 0 0 1 0 |" +
                                         "| 0 0 0 7 0 0 0 0 3 |" +
                                         "| 0 9 0 0 0 4 0 0 6 |" +
                                         "| 2 0 0 0 0 0 0 0 8 |" +
                                         "| 7 0 0 0 1 0 0 5 0 |" +
                                         "| 3 0 0 0 0 9 0 0 0 |" +
                                         "| 0 1 0 0 8 0 0 9 0 |" +
                                         "| 4 0 0 0 0 2 0 0 5 |");
        System.out.println(sudoku);
        sudoku.solve();
        System.out.println(sudoku);
    }
    
}
/*! @} */
