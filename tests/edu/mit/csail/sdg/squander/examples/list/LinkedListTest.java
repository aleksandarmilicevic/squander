/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.list;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;
import edu.mit.csail.sdg.squander.examples.func.Utils;
import edu.mit.csail.sdg.squander.examples.list.LinkedList.Node;
import edu.mit.csail.sdg.squander.spec.SqFunc;


public class LinkedListTest extends MySquanderTestBase {
    
    private LinkedList lst;

    @Before
    public void setUp() {
        lst = new LinkedList();
        lst.add(new Node(1));
        Node n2 = new Node(2);
        n2.setNext(new Node(3));
        lst.add(n2);
        lst.add(new Node(4));
    }
    
    @Test
    public void testFold() {
        int expected = 0; 
        for (Node n : lst.nodes())
            expected += n.getValue();
        int sum = Utils.fold(lst, 0, SqFunc.mkFunc("return = @arg(0) + @arg(1).value"));
        Assert.assertEquals(expected, sum);
    }

}
/*! @} */
