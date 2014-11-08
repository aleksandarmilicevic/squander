package edu.mit.csail.sdg.squander.utils;

import java.util.HashMap;
import java.util.Map;

public abstract class AutoId {
    
    public static class AutoIdCounter {
        private Map<Class<?>, Integer> counters = new HashMap<Class<?>, Integer>();
        
        public int getAndInc(Class<?> cls) {
            Integer cnt = counters.get(cls);
            if (cnt == null) {
                cnt = new Integer(0);
            }
            counters.put(cls, cnt+1);
            return cnt;
        }

        public void clear() {
            counters.clear();
        }
    }
    
    private static final AutoIdCounter cnt = new AutoIdCounter();
    public final int _id = cnt.getAndInc(getClass());
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _id + ")";
    }        
}
