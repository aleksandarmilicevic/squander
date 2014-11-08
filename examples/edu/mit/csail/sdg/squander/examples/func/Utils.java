/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.func;

import edu.mit.csail.sdg.squander.spec.SqFunc;
import edu.mit.csail.sdg.squander.spec.SqFunc.Func;

public class Utils {

    public static <R, E> R fold(Iterable<E> lst, R start, SqFunc.Binary<R, R, E> f) {
        R res = start; 
        for (E e : lst) {
            res = f.exe(res, e);
        }
        return res;
    }
    
    @SuppressWarnings("unchecked")
    public static <R, E> R fold(Iterable<E> lst, R start, Func f) {
        R res = start; 
        for (E e : lst) {
            res = (R) f.exe(res, e);
        }
        return res;
    }
}
/*! @} */
