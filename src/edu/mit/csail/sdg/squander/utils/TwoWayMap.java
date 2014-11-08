/*! \addtogroup Utils Utils 
 * This module contains various utility classes. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TwoWayMap<K, V> implements Map<K, V> {
    private Map<K, V> map = new LinkedHashMap<K, V>();
    private Map<V, K> inv = new LinkedHashMap<V, K>();
    
    public void clear() {
        map.clear();
        inv.clear();
    }
    
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }
    
    public boolean containsValue(Object value) {
        return inv.containsKey(value);
    }
    
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }
    
    public boolean equals(Object o) {
        return map.equals(o);
    }
    
    public V get(Object key) {
        return map.get(key);
    }
    
    public K getKeyForValue(V value) {
        return inv.get(value);
    }
    
    public int hashCode() {
        return map.hashCode();
    }
    
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    public Set<K> keySet() {
        return map.keySet();
    }
    
    public V put(K key, V value) {
        V v = map.put(key, value);
        inv.put(value, key);
        return v;
    }
    
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }
    
    public V remove(Object key) {
        V v = map.remove(key);
        if (v != null)
            inv.remove(v);
        return v;
    }
    
    public int size() {
        return map.size();
    }
    
    public Collection<V> values() {
        return map.values();
    }
    
}
/*! @} */
