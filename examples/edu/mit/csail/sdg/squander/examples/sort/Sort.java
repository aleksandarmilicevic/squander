/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.sort;

import java.util.Arrays;
import java.util.Random;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.log.Log;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

public class Sort {

    @Invariant("this.a != null")
    private int[] a = new int[0];

    public Sort(int... array) {
        this.a = new int[array.length];
        for (int i = 0; i < a.length; i++) {
            this.a[i] = array[i];
        }
    }

    @Returns("this.a")
    public int[] getA() {
        return a;
    }

    @Ensures( { "all i: int | #{j:int | this.a[j] = i} = #{j:int | @old(this.a[j]) = i}",
                "all i: int | all j: int | 0 <= i && i < j && j < this.a.length => this.a[i] <= this.a[j]" })
    @Modifies("this.a.elts")
    @Options(ensureAllInts=false)
    public void sort() {
        Squander.exe(this);
    }


    @Override
    public String toString() {
        return Arrays.toString(a);
    }

    public static void main(String[] args) {
        SquanderGlobalOptions.INSTANCE.log_level = Log.Level.ALL;
        int n = 10;
        int[] a = new int[n];
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            a[i] = r.nextInt(n) - n / 2;
        }
        Sort sort = new Sort(a);
        long t = System.currentTimeMillis();
        sort.sort();
        long dt = System.currentTimeMillis() - t;
        System.out.println("## sorted_array: " + sort);
        System.out.println("## time: " + dt);
    }
}
/*! @} */
