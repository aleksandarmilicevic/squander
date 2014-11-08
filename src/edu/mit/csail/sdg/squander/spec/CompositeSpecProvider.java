/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class CompositeSpecProvider implements ISpecProvider {

    private final ISpecProvider[] providers;
    
    public CompositeSpecProvider(ISpecProvider... providers) {
        this.providers = providers;
    }

    @Override
    public List<Source> extractClassSpec(JType.Unary jtype) {
        List<Source> result = new LinkedList<Source>();
        for (ISpecProvider isp : providers) 
            result.addAll(isp.extractClassSpec(jtype));
        return result;
    }

    @Override
    public List<Source> extractFieldSpec(Field field, JType.Unary declaringType) {
        List<Source> result = new LinkedList<Source>();
        for (ISpecProvider isp : providers) 
            result.addAll(isp.extractFieldSpec(field, declaringType));
        return result;
    }

    @Override
    public MethodSpec extractMethodSpec(Method method, NameSpace ns) {
        MethodSpec result = null;
        for (ISpecProvider isp : providers) {
            MethodSpec ms = isp.extractMethodSpec(method, ns);
            if (ms == null || ms.cases().size() == 0)
                continue;
            if (result != null)
                throw new RuntimeException("Multiple specs for method found: " + method);
            result = ms;
        }
        if (result == null)
            result = new MethodSpec();
        return result;
    }

}
/*! @} */
