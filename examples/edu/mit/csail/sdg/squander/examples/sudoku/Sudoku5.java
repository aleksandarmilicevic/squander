/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.sudoku;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

/**
 * @author Aleksandar Milicevic
 */
@Invariant({
    "this.grid[int][int] in {0 ... this.n}" // not necessary (i.e. included in post-condition)
}) 
public class Sudoku5 {

    private int[][] grid; 
    private final int n; 
    @SuppressWarnings("unused") // used in specs!
    private final int m;
    
    public Sudoku5(int n) {
        assert Math.sqrt(n) * Math.sqrt(n) == n : "n must be a square number";
        this.n = n;
        this.m = (int) Math.sqrt(n);
        this.grid = new int[n][n];
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
        //"all x, y: int | @old(this.grid[x][y] > 0) => x -> y -> @old(this.grid[x][y]) in this.grid.elems.elts",
        "all x: {i: int | i >= 0 && i < this.n} |" +
            "this.grid[x][int] = {1 ... this.n} && this.grid[int][x] = {1 ... this.n}",
        "all i1, i2: {x: int | x >= 0 && x < this.m} |" +
            "this.grid[{i1*this.m ... (i1+1)*this.m-1}][{i2*this.m ... (i2+1)*this.m-1}] = {1 ... this.n}"
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
    
    public static Sudoku5 parse(String puzzle) {
        String[] vals = puzzle.replaceAll("\\|", " ").split("\\s+");
        assert (vals.length > 0);
        int n = Integer.parseInt(vals[0]); 
        assert vals.length == n*n + 1 : "must provide exactly " + n*n + " cell values";
        Sudoku5 sudoku = new Sudoku5(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                int val = Integer.parseInt(vals[1 + i*n + j]);
                if (val > 0)
                    sudoku.setCellValue(i, j, val);
            }
        return sudoku;
    }
    
    public static void main(String[] args) {
        SquanderGlobalOptions.INSTANCE.log_level = Level.NONE;
        Sudoku5 sudoku = Sudoku5.parse("4 | 0 1 0 0 |" +
                                         "| 0 0 3 0 |" +
                                         "| 0 3 0 0 |" +
                                         "| 0 0 4 0 |");
//        Sudoku5 sudoku = Sudoku5.parse("9 | 8 0 0 6 0 0 0 0 2 |" +
//                                         "| 0 4 0 0 5 0 0 1 0 |" +
//                                         "| 0 0 0 7 0 0 0 0 3 |" +
//                                         "| 0 9 0 0 0 4 0 0 6 |" +
//                                         "| 2 0 0 0 0 0 0 0 8 |" +
//                                         "| 7 0 0 0 1 0 0 5 0 |" +
//                                         "| 3 0 0 0 0 9 0 0 0 |" +
//                                         "| 0 1 0 0 8 0 0 9 0 |" +
//                                         "| 4 0 0 0 0 2 0 0 5 |");
        System.out.println(sudoku);
        sudoku.solve();
        System.out.println(sudoku);
    }
    
}
/*! @} */
