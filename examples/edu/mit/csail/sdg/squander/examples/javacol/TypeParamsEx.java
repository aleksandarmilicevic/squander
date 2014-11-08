/*! \addtogroup Examples Examples 
 * This module contains many examples of usage. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.examples.javacol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.squander.Squander;

class Str {
    final String msg;
    public Str(String msg) { this.msg = msg; } 
}

public class TypeParamsEx {

    Map<Str, Integer> map = new HashMap<Str, Integer>();
    List<String> list = new LinkedList<String>();
    Set<String> sss = new HashSet<String>();
    Map<Integer, Set<String>> ms = new HashMap<Integer, Set<String>>();
    
    public TypeParamsEx() {
        init();
    }

    private void init() {
        map.put(new Str("str1"), 1);
        map.put(new Str("str2"), 2);
        map.put(new Str("str3"), 3);
        
        list.add("str1");
        list.add("str4");
        
        Set<String> s1 = new HashSet<String>();
        s1.add("1");
        s1.add("I");
        ms.put(1, s1);
        Set<String> s2 = new HashSet<String>();
        s2.add("2");
        s2.add("II");
        ms.put(2, s2);
        Set<String> s5 = new HashSet<String>();
        s5.add("5");
        s5.add("V");
        ms.put(5, s5);
    }
    
    @Ensures("key in this.ms.elts[return].elts")
    public int findInt(String key) {
        return Squander.exe(this, key);
    }
    
    @Ensures("return.elts = (this.map.keys.msg & this.list[int])")
    @Modifies("return.elts")
    @FreshObjects(cls=Set.class, typeParams={String.class}, num=1)
    public Set<String> common() {
        return Squander.exe(this);
    }
    
    @Ensures("this.sss.elts = (this.map.keys.msg & this.list[int])")
    @Modifies("this.sss.elts")
    public void common2() {
        Squander.exe(this);
    }
    
    @Ensures("return = m[s]")
    public static Integer get(Map<String, Integer> m, String s) {
        return Squander.exe(null, m, s);
    }
    
    public static void main1(String[] args) {
        Map<String, Integer> m  = new HashMap<String, Integer>();
        m.put("111", 1);
        m.put("222", 2);
        m.put("555", 5);
        System.out.println(get(m, "111"));
    }

    public static void main(String[] args) {
        Set<String> set = new TypeParamsEx().common();
        System.out.println(set);
    }

    public static void main3(String[] args) {
        TypeParamsEx tp = new TypeParamsEx();
        tp.common2();
        System.out.println(tp.sss);
    }
    
    public static void main4(String[] args) {
//        GlobalOptions.INSTANCE.log_level = Level.LOG.NONE;
        System.out.println(new TypeParamsEx().findInt("V"));
    }
}
/*! @} */
