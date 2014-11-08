package edu.mit.csail.sdg.squander.ast.java;

import java.lang.reflect.Field;

import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.ast.AField;

@SpecField({
    "name: one String | this.name = this.fld.name"
})
public class JField implements AField {

    private final Field fld;

    public JField(Field fld) {
        this.fld = fld;
    }

    public Field getFld() {
        return fld;
    }
    
}
