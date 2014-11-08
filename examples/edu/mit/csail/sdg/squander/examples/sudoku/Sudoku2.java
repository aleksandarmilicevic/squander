/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.sudoku;

import java.util.Arrays;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.squander.Squander;

//@SpecField("m : one int | this.m * this.m = this.n")
@Invariant({
    "all i, j : {0 ... this.n - 1} | " +
    "    this.rows[i].cells[j] = this.cols[j].cells[i] &&" +
    "    this.rows[i].cells[j] = this.grids[(i/this.m)*this.m + j/this.m].cells[(i%this.m)*this.m + j%this.m]"
})
public class Sudoku2 {
    
    @Invariant("all v : {1 ... this.cells.length} | lone this.cells.elts.v")
    static class CellGroup {
        int[] cells;

        public CellGroup(int n) {
            this.cells = new int[n];
        }

        @Override
        public String toString() {
            return Arrays.toString(cells);
        }
        
    }
    
    private final int n; 
    @SuppressWarnings("unused") // used in specs!
    private final int m;
    
    private CellGroup[] rows;
    private CellGroup[] cols; 
    private CellGroup[] grids;
    
    public Sudoku2(int n) {
        assert Math.sqrt(n) * Math.sqrt(n) == n : "n must be a square number";
        this.n = n;
        this.m = (int) Math.sqrt(n);
        init();
    }
    
    @Ensures("all i, j: int | (i = row & j = col) ? this.rows[i].cells[col] = val : this.rows[i].cells[col] = @old(this.rows[i].cells[col])")
    public void setCellValue(int row, int col, int val) {
        if (val < 1 || val > n)
            val = 0; // make the cell empty
        int m = (int) Math.sqrt(n);
        rows[row].cells[col] = val;
        cols[col].cells[row] = val;
        grids[(row/m)*m + col/m].cells[(row%m)*m + col%m] = val;
        assert repOK();
    }

    @Returns("this.rows[row].cells[col]")
    public int getCellValue(int row, int col) {
        return rows[row].cells[col];
    }
    
    @Ensures({
        //"all g : CellGroup | all v : int | v in g.cells[int] => v > 0 && v <= this.n",
        "all g: CellGroup | all i: {0 ... this.n-1} | g.cells[i] > 0 && g.cells[i] <= this.n",
        //"all g: CellGroup | all i: int | i >= 0 && i < this.n && @old(g.cells[i] != 0) => g.cells[i] = @old(g.cells[i])"
    })
    @Modifies("CellGroup.cells.elts | _<2> = 0")
    @Options(ensureAllInts=false)
    public void solve() {
        Squander.exe(this);
    }
    
    public boolean repOK() {
        int m = (int) Math.sqrt(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (rows[i].cells[j] != cols[j].cells[i])
                    return false;
                if (grids[(i/m)*m + j/m].cells[(i%m)*m + j%m] != rows[i].cells[j])
                    return false;
            }
        }
        return true;
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

    private void init() {
        rows = new CellGroup[n];
        cols = new CellGroup[n];
        grids = new CellGroup[n];
        for (int i = 0; i < n; i++) {
            rows[i] = new CellGroup(n);
            cols[i] = new CellGroup(n);
            grids[i] = new CellGroup(n);
        }
    }
    
    public static Sudoku2 parse(String puzzle) {
        String[] vals = puzzle.replaceAll("\\|", " ").split("\\s+");
        assert (vals.length > 0);
        int n = Integer.parseInt(vals[0]); 
        assert vals.length == n*n + 1 : "must provide exactly " + n*n + " cell values";
        Sudoku2 sudoku = new Sudoku2(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                sudoku.setCellValue(i, j, Integer.parseInt(vals[1 + i*n + j]));
        return sudoku;
    }
    
    public static void main(String[] args) {
//        Sudoku2 sudoku = Sudoku2.parse("4 | 0 1 0 0 | 0 0 3 0 | 0 3 0 0 | 0 0 4 0");
        Sudoku2 sudoku = Sudoku2.parse("9 | 8 0 0 6 0 0 0 0 2 |" +
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
        assert sudoku.repOK();
        System.out.println(sudoku);
    }

}
/*! @} */
