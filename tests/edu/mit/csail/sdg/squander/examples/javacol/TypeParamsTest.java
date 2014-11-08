/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.javacol;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;

public class TypeParamsTest extends MySquanderTestBase {
    
    @Test
    public void test1() {
        TypeParamsEx tpe = new TypeParamsEx();
        Assert.assertEquals(5, tpe.findInt("V"));
        Assert.assertEquals(5, tpe.findInt("5"));
        Assert.assertEquals(1, tpe.findInt("I"));
        
        Set<String> set = new TypeParamsEx().common();
        Assert.assertEquals("[str1]", set.toString());
        
        tpe.common2();
        Assert.assertEquals("[str1]", tpe.sss.toString());
        
        Map<String, Integer> m  = new HashMap<String, Integer>();
        m.put("111", 1);
        m.put("222", 2);
        m.put("555", 5);
        Assert.assertEquals(1, (int)TypeParamsEx.get(m, "111"));
        Assert.assertEquals(2, (int)TypeParamsEx.get(m, "222"));
        Assert.assertEquals(5, (int)TypeParamsEx.get(m, "555"));
    }

}
/*! @} */
