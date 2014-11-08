/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.mit.csail.sdg.squander.examples.graph.Graph.Node;

/**
 * An imperative implementation of the Hamiltonian problem 
 * taken from: 
 * 
 * http://moodle.cornellcollege.edu/0809/mod/resource/index.php?id=947
 */
public class HamiltonianMan {
    
    private static ArrayList<Integer> solution;

    public static Node[] hp(Graph g) {
        int n = g.size();
        int[][] adj = new int[n][n];
        for (Graph.Edge e : g.edges()) {
            adj[e.src.label][e.dst.label] = 1;
        }
        solution = null;
        hp(adj);
        if (solution == null)
            solution = new ArrayList<Integer>();
        Node[] nPath = new Node[solution.size()];
        int i = 0;
        for (Integer l : solution)
            nPath[i++] = new Node(l);
        return nPath;
    }
    
    public static void hp(int[][] adjacency) {
        hp(new ArrayList<Integer>(), adjacency);
    }

    public static void hp(List<Integer> pathSoFar, int[][] adjacency) {
        if (solution != null)
            return;
        int n = adjacency.length;
        if (pathSoFar.size() == n) {
            printSolution(pathSoFar);
            return;
        } else if (pathSoFar.size() == 0) {
            for (int i = 0; i < n; i++) {
                pathSoFar.add(i);
                hp(pathSoFar, adjacency);
                pathSoFar.remove(pathSoFar.size() - 1);
            }
        } else {
            int currentNode = pathSoFar.get(pathSoFar.size() - 1);
            for (int i = 0; i < n; i++) {
                if (!pathSoFar.contains(i) && adjacency[currentNode][i] != 0) {
                    pathSoFar.add(i);
                    hp(pathSoFar, adjacency);
                    pathSoFar.remove(pathSoFar.size() - 1);
                }
            }
        }
    }

    public static void printSolution(List<Integer> pathSoFar) {
        Iterator<Integer> it = pathSoFar.iterator();
        solution  = new ArrayList<Integer>();
        while (it.hasNext()) {
            Integer val = it.next();
            System.err.print(val + " ");
            solution.add(val);
        }
        System.err.println(" got it");
    }

    public static void main(String[] args) {
        int[][] adjacency = { 
            { 0, 1, 0, 0, 0 }, 
            { 1, 0, 1, 1, 0 },
            { 0, 1, 0, 1, 1 }, 
            { 0, 1, 1, 0, 0 }, 
            { 0, 0, 1, 0, 0 } };
        hp(adjacency);
        System.out.println(solution);
    }
}
/*! @} */
