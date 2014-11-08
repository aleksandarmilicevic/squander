package edu.mit.csail.sdg.squander.ast;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.FreshObjects;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.squander.MySquanderTestBase;
import edu.mit.csail.sdg.squander.Squander;

class B {
    int f1; 
    String f2; 
}

class C extends B {
    int f3; 
    String f4;
}

public class SimpleClassTest extends MySquanderTestBase {

    @Returns("cls.name")
    private static String getClassName(Class<?> cls) {
        return Squander.exe(null, cls);
    }
    
    @Returns("cls.simpleName")
    private static String getClassSimpleName(Class<?> cls) {
        return Squander.exe(null, cls);
    }
    
    @Ensures("return.elts = cls.fields.name")
    @FreshObjects(cls=Set.class, num=1, typeParams={String.class})
    private static Set<String> getFieldNames(Class<?> cls) {
        return Squander.exe(null, cls);
    }
    
    @Test
    public void testClassName() {
        Class<?> cls = getClass();
        Assert.assertEquals(cls.getName(), getClassName(cls));
        Assert.assertEquals(cls.getSimpleName(), getClassSimpleName(cls));
    }
    
    public static void main(String[] args) {
        Class<?> cls = C.class;
        System.out.println(getFieldNames(cls));
    }
    
}
