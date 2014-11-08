/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.chess;

import static edu.mit.csail.sdg.squander.examples.chess.ChessBoardMan.dx;
import static edu.mit.csail.sdg.squander.examples.chess.ChessBoardMan.dy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import kodkod.engine.satlab.SATFactory;
import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

/**
 * Implements several chess problems/puzzles. 
 * 
 * @author Aleksandar Milicevic
 */
public class ChessBoard {

    /**
     * Wraps cell coordinates. 
     * 
     * @author Aleksandar Milicevic
     */
    public static class Cell {
        public int i, j;

        public Cell(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return String.format("(%s, %s)", i, j);
        }
    }

    // use "Kodkod" engine (TODO: "KodkodPart" doesn't seem to perform as well 
    // (it probably messes up symmetry breaking)
    /**
     * Solves the N-Queens problem.
     */
    @Ensures({
        "all disj q, r: result.elts | " +
        "   q.i       != r.i       && " +
        "   q.j       != r.j       && " +
        "   q.i - q.j != r.i - r.j && " +
        "   q.i + q.j != r.i + r.j"
    })
    @Modifies({
        "result.elts.i from {0 ... n-1}", 
        "result.elts.j from {0 ... n-1}" 
    })
    private static void nqueens2(int n, Set<Cell> result) { Squander.exe(null, n, result); }
    
    @SuppressWarnings("unused")
    @Ensures({
        "all k : {0 ... n-1} | lone (Cell@i) . k",
        "all k : {0 ... n-1} | lone (Cell@j) . k",
        "all q1 : result.elts | no q2 : result.elts - q1 | " +
        "  q1.i == q2.i || q1.i - q1.j == q2.i - q2.j || " +
        "  q1.j == q2.j || q1.i + q1.j == q2.i + q2.j"
    })
    @Modifies({
        "Cell.i [result.elts][{0 ... n-1}]", 
        "Cell.j [result.elts][{0 ... n-1}]"
    })
    private static void nqueens2_old(int n, Set<Cell> result) { Squander.exe(null, n, result); }
    
    public static Set<Cell> nqueens_int(int n) {
        Set<Cell> result = new HashSet<Cell>();
        for (int i = 0; i < n; i++)
            result.add(new Cell(i, 0));
        nqueens2(n, result);
        return result;
    }
    
    /**
     * A different encoding for the N-Queens problem. 
     */
    @Ensures({
        "all k : {0 ... n-1} | lone (Cell@i) . k",
        "all k : {0 ... n-1} | lone (Cell@j) . k",
        "all q1 : Cell | all q2 : Cell - q1 | " +
        "  q1.i != q2.i && q1.j != q2.j && " +
        "  #((^INC.(q1.i) @+ ^INC.(q2.i)) @- (^INC.(q1.i) @& ^INC.(q2.i))) !=" +
        "  #((^INC.(q1.j) @+ ^INC.(q2.j)) @- (^INC.(q1.j) @& ^INC.(q2.j)))"
    })
    @Modifies({
        "Cell.i [result.elts][{0 ... n-1}]", 
        "Cell.j [result.elts][{0 ... n-1}]"
    })
    @Options(ensureAllInts = false)
    private static void nqueens(int n, Set<Cell> result) {
        Squander.exe(null, n, result);
    }
    
    public static Set<Cell> nqueens_rel(int n) {
        Set<Cell> result = new HashSet<Cell>();
        for (int i = 0; i < n; i++)
            result.add(new Cell(i, 0));
        nqueens(n, result);
        return result;
    }
    
    public static void main(String[] args) {
        SquanderGlobalOptions.INSTANCE.sat_solver = SATFactory.MiniSat;
        SquanderGlobalOptions.INSTANCE.log_level = Level.DEBUG;
        Set<Cell> x = nqueens_int(36);
        printNQueens(x);
    }
    
    /**
     * Solves the Knight's Tour problem. 
     */
    @Ensures({
        "result.elems[int] = @old(result[int])",
        "all k : {0 ... n * m - 2} | " +
        "  ((result[k+1].i = result[k].i + 2) && (result[k+1].j = result[k].j + 1)) || " +
        "  ((result[k+1].i = result[k].i + 1) && (result[k+1].j = result[k].j + 2)) || " +
        "  ((result[k+1].i = result[k].i - 1) && (result[k+1].j = result[k].j + 2)) || " +
        "  ((result[k+1].i = result[k].i - 2) && (result[k+1].j = result[k].j + 1)) || " +
        "  ((result[k+1].i = result[k].i - 2) && (result[k+1].j = result[k].j - 1)) || " +
        "  ((result[k+1].i = result[k].i - 1) && (result[k+1].j = result[k].j - 2)) || " +
        "  ((result[k+1].i = result[k].i + 1) && (result[k+1].j = result[k].j - 2)) || " +
        "  ((result[k+1].i = result[k].i + 2) && (result[k+1].j = result[k].j - 1))"
    })
    @Modifies("result.elts")
    public static void knightsTour(int n, int m, Cell[] result) {
        Squander.exe(null, n, m, result);
    }

    /**
     * A different (potentially more efficient) encoding for the Knight's Tour problem.
     */
    @Ensures({
        "result.elems[int] = @old(result[int])",
        "all k : {0 ... n * m - 2} | " +
        "  result[k+1] in next[result[k].i * m + result[k].j].elts"
    })
    @Modifies("result.elts")
    @Options(ensureAllInts=false)
    public static void knightsTour2(int n, int m, Cell[] result, Set<Cell>[] next) {
        Squander.exe(null, n, m, result, next);
    }
    
    public static Cell[] knightsTour(int n, int m) {
        Cell[] walk = new Cell[n*m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                walk[i*m + j] = new Cell(i, j);
        knightsTour(n, m, walk);
        return walk;
    }
    
    @SuppressWarnings("unchecked")
    public static Cell[] knightsTour2(int n, int m) {
        Cell[] walk = new Cell[n*m];
        Set<Cell>[] next = new Set[n*m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                next[i*m + j] = new HashSet<Cell>();
                walk[i*m + j] = new Cell(i, j);
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0;j < m; j++) {
                for (int k = 0; k < dx.length; k++) {
                    int ii = i + dx[k];
                    int jj = j + dy[k];
                    if (ii >= 0 && ii < n && jj >=0 && jj < m)
                        next[i*m + j].add(walk[ii*m + jj]);
                }
            }
        }
        knightsTour2(n, m, walk, next);
        return walk;
    }
    
    public static void printNQueens(Collection<Cell> queens) {
        int n = queens.size();
        boolean[][] board = new boolean[n][n];
        for (Cell c : queens) {
            board[c.i][c.j] = true;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j])
                    System.out.print(" Q");
                else
                    System.out.print(" .");
            }
            System.out.println();
        }
    }
    
    public static void main_kt(String[] args) {
        int n = 5; 
        int m = 5;
        if (args.length > 1) {
            n = Integer.parseInt(args[0]);
            m = Integer.parseInt(args[1]);
        }
        Cell[] tour = knightsTour(n, m);
        int[][] board = new int[n][m];
        assert tour.length == n*m;
        int idx = 1;
        for (Cell c : tour)
            board[c.i][c.j] = idx++;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
    
}
/*! @} */
