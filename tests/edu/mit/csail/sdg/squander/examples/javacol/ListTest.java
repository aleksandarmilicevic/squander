/*! \addtogroup Tests Tests 
 * This module contains many JUnit tests. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.javacol;

import static edu.mit.csail.sdg.squander.examples.javacol.Collections.add;
import static edu.mit.csail.sdg.squander.examples.javacol.Collections.get;
import static edu.mit.csail.sdg.squander.examples.javacol.Collections.next;
import static edu.mit.csail.sdg.squander.examples.javacol.Collections.prev;
import static edu.mit.csail.sdg.squander.examples.javacol.Collections.remove;
import static edu.mit.csail.sdg.squander.examples.javacol.Collections.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.mit.csail.sdg.squander.MySquanderTestBase;

public class ListTest extends MySquanderTestBase {

    private final String[] elems = new String[] { "str1", "str2", "str1", "str3", "str2" };
    
    private List<String> lst; 
    private List<String> sqLst;
    
    @Before
    public void setUp() {
        lst = new ArrayList<String>(elems.length);
        sqLst = new ArrayList<String>(elems.length);
        for (String str : elems) {
            lst.add(str); 
            sqLst.add(str);
        }
    }
    
    @Test
    public void testGet() {
        for (int i = 0; i < lst.size(); i++) {
            Assert.assertEquals(lst.get(i), get(sqLst, i));
        }
    }
    
    @Test
    public void testSet() {
        for (int i = 0; i < elems.length; i++) {
            lst.set(i, "asdf");
            set(sqLst, i, "asdf");
            Assert.assertEquals(lst, sqLst);
        }
    }
    
    @Test
    public void testAdd() {
        lst = new ArrayList<String>(elems.length);
        sqLst = new ArrayList<String>(elems.length);
        for (String s : elems) {
            lst.add(s);
            add(sqLst, s);
            Assert.assertEquals(lst, sqLst);
        }
    }
    
    @Test
    public void testPrevNext() {
        lst = Arrays.asList(new String[] { "sadf", "dfas", "oer", "23" });
        for (int i = 0; i < lst.size(); i++) {
            String e = lst.get(i);
            String prev = i > 0 ? lst.get(i - 1) : null;
            String sqPrev = prev(lst, e);
            Assert.assertEquals(prev, sqPrev);
        }
        for (int i = 0; i < lst.size(); i++) {
            String e = lst.get(i);
            String next = i < lst.size() - 1 ? lst.get(i + 1) : null;
            String sqPrev = next(lst, e);
            Assert.assertEquals(next, sqPrev);
        }
    }
    
    @Test
    public void testRemove() {
        for (int i = 0; i < elems.length; i++) {
            String s1 = lst.remove(0);
            String s2 = remove(sqLst, 0);
            Assert.assertEquals(s1, s2);
            Assert.assertEquals(lst, sqLst);
        }
    }
    
}
/*! @} */
