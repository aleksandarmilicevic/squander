/*! \addtogroup Utils Utils 
 * This module contains various utility classes. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.utils;

public interface Predicate<T> {

    public static class TruePred<T> implements Predicate<T> {
        @Override
        public boolean exe(T t) {
            return true;
        }
    }

    public static class FalsePred<T> implements Predicate<T> {
        @Override
        public boolean exe(T t) {
            return false;
        }
    }
    
    public boolean exe(T t);
    
}
/*! @} */
