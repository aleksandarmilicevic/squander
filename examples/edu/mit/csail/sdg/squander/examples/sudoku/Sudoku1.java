/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.sudoku;

import java.util.Arrays;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;

@SpecField("empty : one boolean | this.empty <=> this.value == 0")
class Cell {
    int value = 0; // 0 means empty 

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}

@Invariant({
    "null !in this.rows + this.cols + this.grids" // needed to make rows, cols and grids part of the scene
})
public class Sudoku1 {
    
    // this one causes much slower translation from Kodkod to SAT (because it has to iterate through all cells instead of all integers)
    //   @Invariant("all c1 : Cell | all c2 : Cell | (c1 != c2 && !c1.empty && !c2.empty && (c1 + c2) in this.cells[int]) => c1.value != c2.value")
    // this one is not as bad
    //   @Invariant("all i1, i2: {x: int | x >= 0 & x < this.cells.length} | i1 != i2 => this.cells[i1].value != this.cells[i2].value")
    // this one is the best
    //@Invariant("all v1 : int - 0 | lone {c : this.cells.vals | c.value = v1}")
	@Invariant("all v1 : int @- 0 | lone this.cells.elts.value.v1")
    static class CellGroup {
        Cell[] cells;

        public CellGroup(int n) {
            this.cells = new Cell[n];
        }

        @Override
        public String toString() {
            return Arrays.toString(cells);
        }
        
    }
    
    private final int n; 
    
    private CellGroup[] rows;
    private CellGroup[] cols; 
    private CellGroup[] grids;
    
    public Sudoku1(int n) {
        assert Math.sqrt(n) * Math.sqrt(n) == n : "n must be a square number";
        this.n = n;
        init();
    }
    
    @Ensures("val > 0 && val <= this.n ? this.rows[row].cells[col].value = val " +
                                      ": this.rows[row].cells[col].value = 0")
    @Modifies("Cell.value [this.rows[row].cells[col]]")
    public void setCellValue(int row, int col, int val) {
//        Squander.magic(this, row, col, val);
        if (val < 1 || val > n)
            val = 0; // make the cell empty
        rows[row].cells[col].value = val;
    }

    @Returns("this.rows[row].cells[col].value")
    public int getCellValue(int row, int col) {
//        return Squander.magic(this, row, col);
        return rows[row].cells[col].value;
    }
    
    @Ensures("all c : Cell | c.value > 0 && c.value <= this.n")
    @Modifies("Cell.value | _<0>.empty")
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

    private void init() {
        rows = new CellGroup[n];
        cols = new CellGroup[n];
        grids = new CellGroup[n];
        for (int i = 0; i < n; i++) {
            rows[i] = new CellGroup(n);
            cols[i] = new CellGroup(n);
            grids[i] = new CellGroup(n);
        }
        int m = (int) Math.sqrt(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Cell c = new Cell();
                rows[i].cells[j] = c;
                cols[j].cells[i] = c;
                int gridI = i / m;
                int gridJ = j / m;
                int gridIdx = gridI * m + gridJ;
                int gridCellI = i % m; 
                int gridCellJ = j % m;
                int gridCellIdx = gridCellI * m + gridCellJ;
                grids[gridIdx].cells[gridCellIdx] = c;
            }
        }
    }
    
    public static Sudoku1 parse(String puzzle) {
        String[] vals = puzzle.replaceAll("\\|", " ").split("\\s+");
        assert (vals.length > 0);
        int n = Integer.parseInt(vals[0]); 
        assert vals.length == n*n + 1 : "must provide exactly " + n*n + " cell values";
        Sudoku1 sudoku = new Sudoku1(n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                sudoku.setCellValue(i, j, Integer.parseInt(vals[1 + i*n + j]));
        return sudoku;
    }
    
    public static void main(String[] args) {
//        Sudoku1 sudoku = Sudoku1.parse("4 | 0 1 0 0 | 0 0 3 0 | 0 3 0 0 | 0 0 4 0");
//        Sudoku1 sudoku = Sudoku1.parse("9 | 8 0 0 6 0 0 0 0 2 |" +
//                                         "| 0 4 0 0 5 0 0 1 0 |" +
//                                         "| 0 0 0 7 0 0 0 0 3 |" +
//                                         "| 0 9 0 0 0 4 0 0 6 |" +
//                                         "| 2 0 0 0 0 0 0 0 8 |" +
//                                         "| 7 0 0 0 1 0 0 5 0 |" +
//                                         "| 3 0 0 0 0 9 0 0 0 |" +
//                                         "| 0 1 0 0 8 0 0 9 0 |" +
//                                         "| 4 0 0 0 0 2 0 0 5 |");
        Sudoku1 sudoku = Sudoku1.parse(
              "9 | 0 0 0 1 0 0 0 9 0 |" +
                "| 0 6 7 9 2 0 0 4 5 |" +
                "| 0 0 0 0 7 3 2 0 0 |" +
                "| 0 1 0 0 0 0 4 8 9 |" +
                "| 0 7 0 0 0 0 0 5 0 |" +
                "| 4 3 6 0 0 0 0 2 0 |" +
                "| 0 0 1 7 9 0 0 0 0 |" +
                "| 7 4 0 0 3 2 9 1 0 |" +
                "| 0 9 0 0 0 1 0 0 0 |");
        System.out.println(sudoku);
        sudoku.solve();
        System.out.println(sudoku);
    }
}
/*! @} */
