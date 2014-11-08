package edu.mit.csail.sdg.squander.ast.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import edu.mit.csail.sdg.annotations.SpecField;
import edu.mit.csail.sdg.squander.ast.AFunc;

@SpecField({
    "name          : one String | this.name = this.meth.name + this.constr.name",
    "isConstructor : one String | this.isConstructor = this.constr != null",
})
public class JFunc implements AFunc {

    private final Method meth; 
    private final Constructor<?> constr;
    
    public JFunc(Method meth) {
        this.meth = meth;
        this.constr = null;
    }
    
    public JFunc(Constructor<?> constr) {
        this.meth = null;
        this.constr = constr;
    }

    public Method getMeth() {
        return meth;
    }

    public Constructor<?> getConstr() {
        return constr;
    }
    
}
