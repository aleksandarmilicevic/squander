/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.sudoku;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

/**
 * <b>"grid" is indexed from "1" not from "0"</b>
 *  
 * @author Aleksandar Milicevic
 */
@Invariant({
    "this.grid[int][int] in {x : int | x >= 0 && x <= this.n}", 
    "all i, j: int | i @+ j in {1 ... this.n} ? lone this.grid[i][j] : no this.grid[i][j]"
})
@SpecField("grid : int -> int -> int")
public class Sudoku3 {

    private final int n; 
    @SuppressWarnings("unused") // used in specs!
    private final int m;

    public Sudoku3(int n) {
        assert Math.sqrt(n) * Math.sqrt(n) == n : "n must be a square number";
        this.n = n;
        this.m = (int) Math.sqrt(n);
        initGrid();
    } 
    
    @Ensures("no this.grid")
    private void initGrid() { Squander.exe(this); }

    @Ensures("this.grid = @old(this.grid) + row -> col -> value")
    @Modifies("this.grid")
    public void setCellValue(int row, int col, int value) {
        Squander.exe(this, row, col, value);
    }

    @Ensures("some this.grid[row][col] " +
                "? return = this.grid[row][col]" +
                ": return = 0")
    public int getCellValue(int row, int col) {
        return Squander.exe(this, row, col);
    }

    @Ensures({
        //"@old(this.grid) in this.grid",
        "all x: {1 ... this.n} |" +
            "this.grid[x][int] = {1 ... this.n} && " +
            "this.grid[int][x] = {1 ... this.n}",
        "all r, c: {0 ... this.m - 1} |" +
            "this.grid[{r*this.m + 1 ... (r+1)*this.m}][{c*this.m + 1 ... (c+1)*this.m}] = {1 ... this.n}"
    })
    @Modifies("this.grid | false") // means keep all tuples from the pre-state in the post-state
    @Options(ensureAllInts=false)
    public void solve() {
        Squander.exe(this);
    }
    
    public String printSimple() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                sb.append(getCellValue(i+1, j+1)).append(" ");
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
                sb.append(getCellValue(i+1, j+1)).append(" ");
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
    
    public static Sudoku3 parse(String puzzle) {
        String[] vals = puzzle.replaceAll("\\|", " ").split("\\s+");
        assert (vals.length > 0);
        int n = Integer.parseInt(vals[0]); 
        assert vals.length == n*n + 1 : "must provide exactly " + n*n + " cell values";
        Sudoku3 sudoku = new Sudoku3(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                int val = Integer.parseInt(vals[1 + i*n + j]);
                if (val > 0)
                    sudoku.setCellValue(i+1, j+1, val);
            }
        return sudoku;
    }
    
    public static void main(String[] args) {
        SquanderGlobalOptions.INSTANCE.log_level = Level.NONE;
//        Sudoku3 sudoku = Sudoku3.parse("4 | 0 1 0 0 |" +
//                                         "| 0 0 3 0 |" +
//                                         "| 0 3 0 0 |" +
//                                         "| 0 0 4 0 |");
        Sudoku3 sudoku = Sudoku3.parse("9 | 8 0 0 6 0 0 0 0 2 |" +
                                         "| 0 4 0 0 5 0 0 1 0 |" +
                                         "| 0 0 0 7 0 0 0 0 3 |" +
                                         "| 0 9 0 0 0 4 0 0 6 |" +
                                         "| 2 0 0 0 0 0 0 0 8 |" +
                                         "| 7 0 0 0 1 0 0 5 0 |" +
                                         "| 3 0 0 0 0 9 0 0 0 |" +
                                         "| 0 1 0 0 8 0 0 9 0 |" +
                                         "| 4 0 0 0 0 2 0 0 5 |");
        sudoku.solve();
        System.out.println(sudoku);
    }
    
}
/*! @} */
