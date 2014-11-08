package edu.mit.csail.sdg.squander.ast.java;

import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.ast.AClass;

@SpecField({
    "fields : set AField | this.fields = this.cls.fields",
    "funcs  : set AFunc  | this.fields = this.cls.funcs"
})
public class JClass implements AClass {
    
    private final Class<?> cls;

    public JClass(Class<?> cls) {
        this.cls = cls;
    }

    public Class<?> getCls() {
        return cls;
    }
    
}
