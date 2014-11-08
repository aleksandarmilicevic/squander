/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.util.LinkedHashMap;

import edu.mit.csail.sdg.squander.engine.ISquander;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;
import edu.mit.csail.sdg.squander.spec.Source.Rule;




public class SqFunc {

    public static interface Binary<R, A1, A2> {
        public R exe(A1 arg1, A2 arg2);
    }

    public static class Func {
        private final String requires, ensures, modifies;

        public Func(String requires, String ensures, String modifies) {
            this.requires = requires;
            this.ensures = ensures;
            this.modifies = modifies;
        }
        
        public Object exe(Object... args) {
            LinkedHashMap<String, Class<?>> params = new LinkedHashMap<String, Class<?>>();
            for (int i = 0; i < args.length; i++)
                params.put("@arg(" + i + ")", args[i].getClass());
            MethodSpec ms = new MethodSpec();
            JMethod jm = new JMethod("<anonymous>", Null.class, params, Object.class, true);
            NameSpace ns = NameSpace.forMethod(jm);
            ms.addCase(new Source(requires, ns, Rule.CLAUSE), 
                       new Source(ensures, ns, Rule.CLAUSE), 
                       new Source(modifies, ns, Rule.FRAME), false);
            
            jm.setSpec(ms);
            ISquander sq = SquanderGlobalOptions.INSTANCE.getSquanderImpl();
            return sq.magic(null, jm, args);
        }
    }
    
    public static Func mkFunc(String ensures) {
        return mkFunc("true", ensures, "");
    }
    
    public static Func mkFunc(String requires, String ensures, String modifies) {
        return new Func(requires, ensures, modifies); 
    }
}
/*! @} */
