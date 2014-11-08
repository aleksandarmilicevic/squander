package edu.mit.csail.sdg.squander.examples.puzzles;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Macro;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;

/**
 * Triangular Peg Solitaire puzzle taken from
 * 
 * http://www.mathsisfun.com/games/triangle-peg-solitaire/index.html#
 * 
 * @author Aleksandar Milicevic <aleks@csail.mit.edu>
 */
public class TriSol {

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
    
    public static class State {
        Cell[] cells = new Cell[N];
        int s, v, d;
        
        public State() {
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
        
    }
    
    @Options(ensureAllInts = false)
    @Macro(var  = "dst",
           expr = "neighborCells[via].elts.(neighborCells[src][via]) @- src")
    @Ensures({
        "return = states",
        // first position: only one is occupied
        "one c: return.first.cells.vals | !c.free",
        // last position: only one is free
        "one c: return.last.cells.vals | c.free",
        // transition constraint:
        "all i: {1 ... (return.length - 1)} | " +
           "(exists src, via: {0 ... (N-1)} | " +
               "src != via && " + 
               "neighborCells[src][via] != 0 && " +
               "one ($dst) && " +
               "return[i-1].s = src && return[i-1].v = via && return[i-1].d = $dst && " +
               "!return[i-1].cells[src].free && " +
               "return[i-1].cells[$dst].free && " +
               "return[i-1].cells[via].free && " +
               "return[i].cells[src].free && " +
               "!return[i].cells[$dst].free && " +
               "!return[i].cells[via].free && " + 
               "(all x: {0 ... (N-1)} | " +
                   "(x != src && x != via && x != $dst) => return[i-1].cells[x].free = return[i].cells[x].free))"
    })
    @Modifies("Cell.free, State.s, State.v, State.d")
    public State[] solve(State[] states) {
        return Squander.exe(this, new Object[] {states});
    }
    
    private void setNeighbors(int i, int j, int val) {
        neighborCells[i][j] = val;
        neighborCells[j][i] = val;
    }
    
    public TriSol() {
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
    
    public static void main(String[] args) {
        State[] states = new State[N-1];
        for (int i = 0; i < states.length; i++)
            states[i] = new State();
                
        TriSol ts = new TriSol();
        State[] sol = ts.solve(states);
        
        for (State s : sol) {
            System.out.println(s);
            System.out.println("------------");
        }
    }
    
}
