/*! \addtogroup Utils Utils 
 * This module contains various utility classes. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class Counter<T> {
    // using AtomicInteger for mutability more than for concurrency
    private final Map<T,AtomicInteger> map = new HashMap<T,AtomicInteger>();
    
    public Counter() {
        super();
    }

    public int getCount(final T key) {
        final AtomicInteger probe = map.get(key);
        if (probe == null) {
            return 0;
        } else {
            return probe.intValue();
        }
    }
    
    public int incrementAndGet(final T key) {
        final AtomicInteger probe = map.get(key);
        if (probe != null) {
            // common case
            final int result = probe.incrementAndGet();
            return result;
        } else {
            // initialization case
            final AtomicInteger a = new AtomicInteger(1);
            map.put(key, a);
            return a.intValue();
        }
    }
    
}
/*! @} */
