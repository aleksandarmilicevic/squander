/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.regressions;

import junit.framework.Assert;

import org.junit.Test;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Fresh;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.squander.Squander;

public class Tests {

    @Ensures("return.length = 2 && null !in return[0] + return[1]")
    @Modifies({"return.elts", "return.length"})
    @Fresh({@FreshObjects(cls = Tests[].class, num = 1), @FreshObjects(cls = Tests.class, num = 2)})
    Tests[] make() {
        return Squander.exe(this);
    }
    
    @Test
    public void testMake() {
        Object[] m = make();
        Assert.assertNotNull(m);
        Assert.assertEquals(2, m.length);
        Assert.assertNotNull(m[0]);
        Assert.assertNotNull(m[1]);
    }
    
}
/*! @} */
