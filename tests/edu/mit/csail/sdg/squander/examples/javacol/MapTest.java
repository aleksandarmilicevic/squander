/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.javacol;

import static edu.mit.csail.sdg.squander.examples.javacol.Collections.get;
import static edu.mit.csail.sdg.squander.examples.javacol.Collections.put;
import static edu.mit.csail.sdg.squander.examples.javacol.Collections.removeKey;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;

public class MapTest extends MySquanderTestBase {

    private static final String[] keys = new String[] { "str1", "str2", "str3" };
    private static final Integer[] vals = new Integer[] { 1, 2, 3 };
    
    private Map<String, Integer> map; 
    private Map<String, Integer> sqMap;
    
    @Before
    public void setUp() {
        map = new HashMap<String, Integer>();
        sqMap = new HashMap<String, Integer>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], vals[i]);
            sqMap.put(keys[i], vals[i]);
        }
    }
    
    @Test
    public void testGet() {
        for (String key : keys) {
            Integer v1 = map.get(key);
            Integer v2 = get(sqMap, key);
            Assert.assertEquals(v1, v2);
            Assert.assertEquals(map.toString(), sqMap.toString());
        }
    }
    
    @Test
    public void testPut() {
        map = new HashMap<String, Integer>();
        sqMap = new HashMap<String, Integer>();
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Integer val = vals[i];
            Integer v1 = map.put(key, val); 
            Integer v2 = put(sqMap, key, val);
            Assert.assertEquals(v1, v2);
            Assert.assertEquals(map.toString(), sqMap.toString());
        }
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Integer val = vals[i];
            Integer v1 = map.put(key, val); 
            Integer v2 = put(sqMap, key, val);
            Assert.assertEquals(v1, v2);
            Assert.assertEquals(map.toString(), sqMap.toString());
        }
    }
    
    @Test
    public void testRemove() {
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            Integer v1 = map.remove(key); 
            Integer v2 = removeKey(sqMap, key);
            Assert.assertEquals(v1, v2);
            Assert.assertEquals(map.toString(), sqMap.toString());
        }
    }
    
}
/*! @} */
