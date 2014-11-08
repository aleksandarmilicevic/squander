/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.chess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.mit.csail.sdg.squander.examples.chess.ChessBoard.Cell;

/**
 * Manual (imperative) implementations of several chess problems/puzzles
 * 
 * @author Aleksandar Milicevic
 */
public class ChessBoardMan {

    // ========================================================
    // -------------------- Knight's Tour ---------------------
    // ========================================================
    
    public static int[] dx = new int[] {2, 1, -1, -2, -2, -1, 1, 2};
    public static int[] dy = new int[] {1, 2, 2, 1, -1, -2, -2, -1};

    private static long t1, t2;

    /**
     * An imperative implementation of the Knight's Tour problem
     */
    public static Cell[] knightsTourMan(int n, int m) {
        int[][] pot = new int[n][m];
        int si = 0;
        int sj = 2;
        pot[si][sj] = 1; 
        t1 = System.currentTimeMillis();
        search(pot, n, m , si, sj, 2);
        System.out.println("not found");
        t2 = System.currentTimeMillis();
        System.out.println("time: " + (t2 - t1)/1000.0);
        return null; //TODO
    }
    
    
    private static void search(int[][] pot, int n, int m, int currI, int currJ, int cnt) {
        for (int k = 0; k < 8; k++) {
            int nextI = currI + dx[k];
            int nextJ = currJ + dy[k];
            if (nextI < 0 || nextI >= n) continue;
            if (nextJ < 0 || nextJ >= m) continue;
            if (pot[nextI][nextJ] != 0) continue;
            pot[nextI][nextJ] = cnt;
            if (cnt == n*m) {
                t2 = System.currentTimeMillis();
                System.out.println("found!");
                print(pot);
                System.out.println("time: " + (t2 - t1)/1000.0);
                System.exit(32);
            } else {
                search(pot, n, m, nextI, nextJ, cnt+1);
            }
            pot[nextI][nextJ] = 0;
        }
    }

    private static void print(int[][] pot) {
        for (int i = 0; i < pot.length; i++)
            System.out.println(Arrays.toString(pot[i]));
        System.out.println();
    }
    
    public static void main(String[] args) {
        knightsTourMan(6, 7);
    }

    // ========================================================
    // ---------------------- N Queens ------------------------
    // ========================================================
    
    /**
     * An imperative implementation of the N-Queens problem
     */
    public static Set<Cell> nqueens(int n) {
        int[] colAssignments = new int[n];
        boolean[] rowTaken = new boolean[n];
        boolean[] d45Taken = new boolean[2*n - 1];
        boolean[] d135Taken = new boolean[2*n - 1];
        boolean b = solveNQueens(n, 0, colAssignments, rowTaken, d45Taken, d135Taken);
        if (!b)
            return null;
        Set<Cell> result = new HashSet<Cell>();
        for (int col = 0; col < n; col++) {
            result.add(new Cell(colAssignments[col], col));
        }
        return result;
    }

    private static boolean solveNQueens(int n, int col, int[] colAssignments, boolean[] rowTaken, 
            boolean[] d45Taken, boolean[] d135Taken) {
        if (col >= n)
            return true;
        for (int row = 0; row < n; row++) {
            if (rowTaken[row]) continue;
            if (d45Taken[row + col]) continue;
            if (d135Taken[col - row + n - 1]) continue;
            colAssignments[col] = row;
            rowTaken[row] = true;
            d45Taken[row + col] = true;
            d135Taken[col - row + n - 1] = true;
            if (solveNQueens(n, col + 1, colAssignments, rowTaken, d45Taken, d135Taken)) 
                return true;
            rowTaken[row] = false;
            d45Taken[row + col] = false;
            d135Taken[col - row + n - 1] = false;
        }
        return false;
    }
    
}
/*! @} */
