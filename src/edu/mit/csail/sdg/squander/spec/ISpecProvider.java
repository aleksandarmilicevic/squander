/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import edu.mit.csail.sdg.squander.spec.JType.Unary;


public interface ISpecProvider {

    public List<Source> extractClassSpec(JType.Unary jtype);

    public List<Source> extractFieldSpec(Field field, Unary declaringType);
    
    public MethodSpec extractMethodSpec(Method method, NameSpace ns);

}
/*! @} */
