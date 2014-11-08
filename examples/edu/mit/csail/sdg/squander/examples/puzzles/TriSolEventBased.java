package edu.mit.csail.sdg.squander.examples.puzzles;

import edu.mit.csail.sdg.annotations.Macro;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.eventbased.Final;
import edu.mit.csail.sdg.squander.eventbased.Initial;
import edu.mit.csail.sdg.squander.eventbased.State;
import edu.mit.csail.sdg.squander.eventbased.StateFrame;
import edu.mit.csail.sdg.squander.eventbased.StateOptions;
import edu.mit.csail.sdg.squander.eventbased.Transition;

@StateOptions(@Options(ensureAllInts = false))
@Macro(var  = "dst",
       expr = "neighborCells[via].elts.(neighborCells[src][via]) @- src")
@Initial({
    "one c: this.cells.vals | !c.free", // first position: only one is occupied
})
@Final({
    "one c: this.cells.vals | c.free"    // last position: only one is free
})
@Transition({
    "(exists src, via: {0 ... (N-1)} | " +
    "   src != via && " + 
    "   neighborCells[src][via] != 0 && " +
    "   one ($dst) && " +
    "   this.s = src && this.v = via && this.d = $dst && " +
    "   !this.cells[src].free && this.cells[$dst].free && this.cells[via].free && " +
    "   this'.cells[src].free && !this'.cells[$dst].free && !this'.cells[via].free && " + 
    "   (all x: {0 ... (N-1)} | " +
    "      (x != src && x != via && x != $dst) => this.cells[x].free = this'.cells[x].free))"
})
@StateFrame("Cell.free, TriSolEventBased.s, TriSolEventBased.v, TriSolEventBased.d")
public class TriSolEventBased extends State {
    
    public static final int n = 5;
    public static final int N = (n * (n+1)) / 2;
    
    private static final int HOR = 1, D45 = 2, D135 = 3;
    private static int[][] neighborCells; 

    public static class Cell {
        public final int id;
        boolean free;
        
        public Cell(int id, boolean free) {
            this.id = id;
            this.free = free;
        }
    }
    
    Cell[] cells = new Cell[N];
    int s, v, d;
    
    public TriSolEventBased() {
        for (int i = 0; i < N; i++) {
            cells[i] = new Cell(i, true);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int width = n * 2 - 1;
        int cnt = 0;
        for (int l = 1; l <= n; l++) {
            int padding = (width - (l*2 - 1)) / 2;
            for (int i = 0; i < padding; i++) sb.append(" ");
            for (int i = 0; i < l; i++, cnt++) 
                sb.append(cells[cnt].free ? "X" : "O").append(" ");
            sb.append("\n"); 
        }
        sb.append(String.format("%d -[%d]-> %d\n", s, v, d));
        return sb.toString();
    }
    
    private static void setNeighbors(int i, int j, int val) {
        neighborCells[i][j] = val;
        neighborCells[j][i] = val;
    }
    
    static {
        neighborCells = new int[N][N];
        for (int l = 1; l <= n; l++) {
            int offset = ((l-1) * l) / 2;
            for (int i = 0; i < l; i++) {
                // the one to the right
                if (i + 1 < l) setNeighbors(offset + i, offset + i + 1, HOR);
                // down-left and down-right
                if (l + 1 <= n) {
                    setNeighbors(offset + i, offset + i + l, D45);
                    setNeighbors(offset + i, offset + i + l + 1, D135);
                }
            }
        }
    }
    
    public static TriSolEventBased[] findTrace(int n) {
        return Squander.findTrace(TriSolEventBased.class, n);
    }
    
    public static void main(String[] args) {
        TriSolEventBased[] steps = findTrace(N-1);
        
        for (TriSolEventBased s : steps) {
            System.out.println(s);
            System.out.println("------------");
        }
    }

}
