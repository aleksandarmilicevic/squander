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
    "null !in this.grid[Num][Num]",
    "this.nums != null" // just to bring Nums into Java/Forge scene
})
@SpecField("grid : Num -> Num -> Num")
public class Sudoku4 {

    class Num {
        final int val;

        public Num(int val) {
            this.val = val;
        } 
    }
    
    private final int n; 
    @SuppressWarnings("unused") // used in specs!
    private final int m;
    private final Num[] nums;

    public Sudoku4(int n) {
        assert Math.sqrt(n) * Math.sqrt(n) == n : "n must be a square number";
        this.n = n;
        this.m = (int) Math.sqrt(n);
        this.nums = new Num[n];
        for (int i = 0; i < n; i++)
            nums[i] = new Num(i+1);
        initGrid();
    } 
    
    @Ensures("no this.grid")
    private void initGrid() { Squander.exe(this); }

    @Ensures("this.grid = @old(this.grid) + {nn: Num | nn.val==row} -> {nn: Num | nn.val==col} -> {nn: Num | nn.val==value}")
    @Modifies("this.grid")
    public void setCellValue(int row, int col, int value) {
        long t = System.currentTimeMillis();
        Squander.exe(this, new Class[] {int.class, int.class, int.class}, new Object[] {row, col, value});
        System.out.println(String.format("set[%s][%s]=%s: ", row, col, value) + (System.currentTimeMillis() - t)/1000.0);
    }

    @Ensures("some this.grid[{nn: Num | nn.val == row}][{nn: Num | nn.val == col}] " +
                "? return = this.grid[{nn: Num | nn.val == row}][{nn: Num | nn.val == col}].val" +
                ": return = 0")
    public int getCellValue(int row, int col) {
        long t = System.currentTimeMillis();
        int ret = Squander.exe(this, new Class[] {int.class, int.class}, new Object[] {row, col});
        System.out.println("get: " + (System.currentTimeMillis() - t)/1000.0);
        return ret;
    }

    @Ensures({
        "@old(this.grid) in this.grid",
        "all x : Num | all y : Num | x != null && y != null" +
            "=> one this.grid[x][y]" +
                "&& this.grid[x][y] !in this.grid[x][Num - y]" +
                "&& this.grid[x][y] !in this.grid[Num - x][y]",
        "all i1 : int | all i2 : int | i1 >= 0 && i1 < this.m && i2 >= 0 && i2 < this.m " +
            "=> Num in (null + this.grid[{nn : Num | nn.val > i1*this.m && nn.val <= (i1+1)*this.m}]" +
                                       "[{nn : Num | nn.val > i2*this.m && nn.val <= (i2+1)*this.m}])"
    })
    @Modifies("this.grid")
    @Options(ensureAllInts=false)
    public void solve() {
        SquanderGlobalOptions.INSTANCE.log_level = Level.DEBUG;
        long t = System.currentTimeMillis();
        Squander.exe(this);
        System.out.println("solve: " + (System.currentTimeMillis() - t)/1000.0);
        SquanderGlobalOptions.INSTANCE.log_level = Level.NONE;
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
    
    public static Sudoku4 parse(String puzzle) {
        String[] vals = puzzle.replaceAll("\\|", " ").split("\\s+");
        assert (vals.length > 0);
        int n = Integer.parseInt(vals[0]); 
        assert vals.length == n*n + 1 : "must provide exactly " + n*n + " cell values";
        Sudoku4 sudoku = new Sudoku4(n);
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
//        Sudoku4 sudoku = Sudoku4.parse("4 | 0 1 0 0 |" +
//                                         "| 0 0 3 0 |" +
//                                         "| 0 3 0 0 |" +
//                                         "| 0 0 4 0 |");
        Sudoku4 sudoku = Sudoku4.parse("9 | 8 0 0 6 0 0 0 0 2 |" +
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
