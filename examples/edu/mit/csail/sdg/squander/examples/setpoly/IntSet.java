/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.setpoly;

import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.Squander;


@SpecField("elts: set int")
public class IntSet {
    
    public IntSet() { init(); }
    
    @Ensures("no this.elts")
    private void init()            { Squander.exe(this);  }

    @Ensures("return = e !in @old(this.elems) && this.elems = @old(this.elts) @+ e")
    @Modifies("this.elts")
    public boolean add(int e)      { return Squander.exe(this, e); }
    
    @Ensures("return = e in this.elts")
    public boolean contains(int e) { return Squander.exe(this, e); }
    
    @Ensures("return = s2.elems in this.elts")
    public boolean containsAll(IntSet s2) { return Squander.exe(this, s2); }
    
    @Ensures("return = e in @old(this.elems) && this.elems = @old(this.elts) @- e")
    @Modifies("this.elts")
    public boolean remove(int e)   { return Squander.exe(this, e); }
    
    @Ensures("return.elts = this.elts")
    @FreshObjects(cls=Set.class, typeParams={Integer.class}, num=1)
    @Modifies("return.elts")
    public Set<Integer> nodes()    { return Squander.exe(this); }
    
    public static void main(String[] args) {
        IntSet s1 = new SetIntSet(); 
        IntSet s2 = new SetIntSet();
        s1.add(2);
        s1.add(3);
        s1.add(4);
        s2.add(3);
        s2.add(2);
        System.out.println(s1.containsAll(s2));
        s1.remove(2);
        System.out.println(s1.containsAll(s2));
    }
    
    public static void main2(String[] args) {
        IntSet ss = 
//            new IntSet();
            new SetIntSet();
//            new BstIntSet();
        ss.add(2);
        ss.remove(3);
        ss.add(3);
//        ss.remove(3);
        ss.add(2);
        ss.add(5);
        ss.add(7);
//        ss.remove(3);
        System.out.println(ss.nodes());
        System.out.println(ss);
    }
}
/*! @} */
