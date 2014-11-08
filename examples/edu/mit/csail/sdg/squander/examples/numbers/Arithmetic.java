/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.numbers;

import edu.mit.csail.sdg.annotations.Case;
import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Options;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.Specification;
import edu.mit.csail.sdg.squander.Squander;
import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

/**
 * Examples of arithmetic algorithms.
 * @author kuat
 */
public class Arithmetic {

    @Requires("m > 0 && n > 0")
    @Ensures({"no x : int | x > return && m % x = 0 && n % x = 0",
        "return > 0",
        "m % return = 0",
        "n % return = 0"})
    public static int gcd(final int m, final int n) {
        return Squander.exe(null, new Class<?>[] {int.class, int.class}, new Object[] {m, n});
    }

    @Requires("m > 0 && n > 0")
    @Ensures({"no x : int | x > return && m % x = 0 && n % x = 0",
        "return > 0",
        "m % return = 0",
        "n % return = 0"})
    public static int gcd_impl(int m, int n) {
        if (m < n) {
            int t = m;
            m = n;
            n = t;
        }

        int r = m % n;

        if (r == 0) {
            return n;
        } else {
            return gcd(n, r);
        }
    }

//    @Ensures( { "all i: int | #{j:int | return[j] = i} = #{j:int | a[j] = i}",
//                "all i: int | all j: int | 0 <= i && i < j && j < return.length => return[i] <= return[j]" })
//  @Fresh({@FreshObjects(cls=int[].class, num=1)})
//    public static int[] sort2(int[] a) {
//        return new Squander().magic(null, new Class[] {int[].class}, new Object[] {a});
//    }
    
    @Ensures( { "all i: int | #{j:int | a[j] = i} = #{j:int | @old(a[j]) = i}",
                "all i: int | all j: int | 0 <= i && i < j && j < a.length => a[i] <= a[j]" })
    @Modifies("a.elts")
    @Returns("a")
    public static int[] sort(int[] a) {
        return Squander.exe(null, new Class[] { int[].class }, new Object[] { a });
    }
    
    @Specification({
            @Case(
                    requires = "s = 0",
                    ensures = "return = 0"                        
            ),
            @Case(
                    requires = "s > 0",
                    ensures = "return > 0 && return <= s / return && s / (return + 1) < (return + 1)"
            )
    })                    
    public static int square_root(int s) {
        return Squander.exe(null, new Class<?>[] {int.class}, new Object[]{s});
    }

    
    // Carroll Morgan's square root development
    // return : [return^2 <= s < (return + 1)^2]
    @Requires("s >= 0")
    @Ensures({"return >= 0",
        "return <= s / return", 
        "s / (return + 1) < (return + 1)"})
    public static int square_root_finite(int s) {
        return Squander.exe(null, new Class<?>[] {int.class}, new Object[]{s});
    }
    
    public static int square_root_mixed(final int s) {
        int r = 1;
        int q = s; 
        while (r+1 < q) {
            int p = findP(r, q);
            if (s < p*p) {
                q = findQ(p, q, r, s);
            } else {
                r = findR(p, q, r, s);
            }
        }
        return r; 
    }

    @Requires("r + 1 < q")
    @Ensures("return > r && return < q")
    @Options(ensureAllInts = true)
    private static int findP(final int r, final int q) {
        return Squander.exe(null, new Class[] {int.class, int.class}, new Object[] {r, q});
    }
    
    @Requires({"r <= s/r && s/q < q", "s/p < p", "p < q"})
    @Ensures("return < q && return > 0 && r <= s/r && s/return < return")
    private static int findQ(final int p, final int q, final int r, final int s) {
        return Squander.exe(null, new Class[] {int.class, int.class, int.class, int.class}, 
                new Object[] {p, q, r, s});
    }
    
    @Requires({"r <= s/r && s/q < q", "s/p >= p", "r < p"})
    @Ensures("return > r && return  <= s/return && s/q < q")
    private static int findR(final int p, final int q, final int r, final int s) {
        return Squander.exe(null, new Class[] {int.class, int.class, int.class, int.class}, 
                new Object[] {p, q, r, s});
    }
    
    public static int _square_root_mixed(final int s) {
        int r = 1;
        int q = s; 
        while (r+1 < q) {
            int p = findP(r, q);
            if (s < p*p) {
                q = _findQ(p, q, r, s);
            } else {
                r = _findR(p, q, r, s);
            }
        }
        return r; 
    }

    @Requires({"r*r <= s && s < q*q", "s < p*p", "p < q"})
    @Ensures("return < q && return > 0 && r*r <= s && s < return * return")
    private static int _findQ(final int p, final int q, final int r, final int s) {
        return Squander.exe(null, new Class[] {int.class, int.class, int.class, int.class}, 
                new Object[] {p, q, r, s});
    }
    
    @Requires({"r <= s/r && s/q < q", "s >= p*p", "r < p"})
    @Ensures("return > r && return * return <= s && s < q*q")
    private static int _findR(final int p, final int q, final int r, final int s) {
        return Squander.exe(null, new Class[] {int.class, int.class, int.class, int.class}, 
                new Object[] {p, q, r, s});
    }
    
    /* 
     * the following methods perform simple arithmetic operations 
     * and return no solution in case of an overflow 
     */
    
    @Returns("a + b")
    public static int plus(int a, int b)  { return Squander.exe(null, a, b); }
    
    @Returns("a - b")
    public static int minus(int a, int b) { return Squander.exe(null, a, b); }
    
    @Returns("a * b")
    public static int times(int a, int b) { return Squander.exe(null, a, b); }
    
    @Returns("a / b")
    public static int div(int a, int b)   { return Squander.exe(null, a, b); }
    
    @Returns("a % b")
    public static int mod(int a, int b)   { return Squander.exe(null, a, b); }
    
    /* 
     * the following methods perform simple arithmetic operations 
     * and return a default value in case of an overflow 
     */
    
    @Returns("some (a + b) ? a + b : def")
    public static int plusOrDef(int a, int b, int def)  { return Squander.exe(null, a, b, def); }
    
    @Returns("some (a - b) ? a - b : def")
    public static int minusOrDef(int a, int b, int def) { return Squander.exe(null, a, b, def); }
    
    @Returns("some (a * b) ? a * b : def")
    public static int timesOrDef(int a, int b, int def) { return Squander.exe(null, a, b, def); }
    
    @Returns("some (a / b) ? a / b : def")
    public static int divOrDef(int a, int b, int def)   { return Squander.exe(null, a, b, def); }
    
    @Returns("some (a % b) ? a % b : def")
    public static int modOrDef(int a, int b, int def)   { return Squander.exe(null, a, b, def); }
    
    /*
     * the following methods are used to check certain predicates
     */
    
    @Returns("all i: int | (!(i > 0) || i + i > i) && (i < 0 => i + i < i)")
    @Options(ensureAllInts = true)
    public static boolean check1()     { return Squander.exe(null); }
    
    @Returns("!(some i: int | !((i > 0 => i + i > i) && (i < 0 => i + i < i)))")
    @Options(ensureAllInts = true)
    public static boolean check1dual() { return Squander.exe(null); }
    
    @Returns("all i: int | i * i >= 0")
    @Options(ensureAllInts = true)
    public static boolean check2()     { return Squander.exe(null); }
    
    @Returns("!(some i: int | !(i * i >= 0))")
    @Options(ensureAllInts = true)
    public static boolean check2dual() { return Squander.exe(null); }
    
    @Returns("#{i: int | i + i > 0} = 3")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check3()     { return Squander.exe(null); }
    
    @Returns("#{i: int | i + i < 0} = 4")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check4()     { return Squander.exe(null); }
    
    @Returns("!(all i: int | i + i != i)")
    @Options(ensureAllInts = true)
    public static boolean check5()     { return Squander.exe(null); }
    
    @Returns("some i: int | i + i = i")
    @Options(ensureAllInts = true)
    public static boolean check5dual() { return Squander.exe(null); }
    
    // law of the excluded middle
    @Returns("all a, b, c: int | a + b > c || !(a + b > c)")
    @Options(ensureAllInts = true)
    public static boolean check6()     { return Squander.exe(null); }
    
    // law of the excluded middle
    @Returns("!(some a, b, c: int | !(a + b > c) && !(a + b <= c))")
    @Options(ensureAllInts = true)
    public static boolean check6dual() { return Squander.exe(null); }
    
    @Returns("(4 + 5 = 6 + 3) ? false : true")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check7()     { return Squander.exe(null); }      
    
    @Returns("(4 + 5 != 6 + 3) ? false : true")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check8()     { return Squander.exe(null); }
    
    // law of the excluded middle
    @Returns("all a: int | a * 2 < 0 || !(a * 2 < 0)")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check9()     { return Squander.exe(null); }
    
    // law of the excluded middle
    @Returns("!(some a: int | a * 2 < 0 && !(a * 2 < 0))")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check9dual()     { return Squander.exe(null); }
    
    // law of the excluded middle
    @Returns("no a: int | a * 2 < 0 && !(a * 2 < 0)")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check9dual2()    { return Squander.exe(null); }
    
    @Returns("#{a: int | a * 3 < 0 || !(a * 3 < 0)} = 5")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check10()     { return Squander.exe(null); }
    
    // quantifier semantics
    @Returns("!(all a: int | a != 0 ? a/a = 1 : a > 0)")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check11()     { return Squander.exe(null); }
    
    @Returns("some a: int | !(a != 0 ? a/a = 1 : a > 0)")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check11dual()     { return Squander.exe(null); }
    
    @Returns("(all a: int | a != 0 ? a/a = 1 : a > 0) ? false : true")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check11dual2()     { return Squander.exe(null); }
    
    // contrapositive
    @Returns("all a, b: int | (a+b > a => a+b > b) = (!(a+b > b) => !(a+b > a))")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check12()    { return Squander.exe(null); }
    
    // contrapositive
    @Returns("no a, b: int | (a+b > a => a+b > b) != (!(a+b > b) => !(a+b > a))")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check12dual()    { return Squander.exe(null); }
    
    // contrapositive
    @Returns("!(some a, b: int | (a+b > a => a+b > b) != (!(a+b > b) => !(a+b > a)))")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check12dual2()    { return Squander.exe(null); }
    
    // nested
    @Returns("some a: int | all b: int | some(a + b)")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check13()    { return Squander.exe(null); }
    
    // nested
    @Returns("some a: int | a > 0 => (all b: int | some(a + b))")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check14()    { return Squander.exe(null); }
    
    // nested
    @Returns("all b: int | some a: int | some(a + b)")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check15()    { return Squander.exe(null); }
    
    // nested
    @Returns("(all b: int | some a: int | a > 0 && some(a + b)) ? false : true")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check16()    { return Squander.exe(null); }
    
    @Returns("(some b: int | all a: int | a <= 0 || a + b != a + b)")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check16dual()   { return Squander.exe(null); }

    // nested
    @Returns("all a: int | all b: int | a + 1 > a")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check17()    { return Squander.exe(null); }
    
    @Returns("!(all a: int | some b: int | a + 1 < b + 1)")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check18()    { return Squander.exe(null); }
    
    @Returns("some a: int | all b: int | !(a + 1 < b + 1)")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check18dual()    { return Squander.exe(null); }
    
    @Returns("all a: int | some b: int | a + 1 <= b + 1")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check19()    { return Squander.exe(null); }
    
    @Returns("!(some a: int | all b: int | !(a + 1 <= b + 1))")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check19dual()    { return Squander.exe(null); }
    
    @Returns("all a: int | some b: int | (a + 1 = b + 1)")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check20()    { return Squander.exe(null); }
    
    @Returns("all a: int | some b: int | !(a + 1 != b + 1)")
    @Options(ensureAllInts = true, bitwidth = 4)
    public static boolean check20dual()    { return Squander.exe(null); }    
        
    /* --- */
    
    @Ensures("all x: int | return <= x")
    @Options(ensureAllInts = true)
    public static int minInt()            { return Squander.exe(null); }
    
    @Ensures("all x: int | return >= x")
    @Options(ensureAllInts = true)
    public static int maxInt()            { return Squander.exe(null); }
        
    public static void main(String[] args) {
        SquanderGlobalOptions.INSTANCE.log_level = Level.ALL;
        SquanderGlobalOptions.INSTANCE.noOverflow = true;
        System.out.println(check20());        
    }
    
}
/*! @} */
