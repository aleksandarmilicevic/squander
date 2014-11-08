/*! \addtogroup Specification Specification 
 * This module contains classes for reading and maintaining specifications and converting them to Forge expressions. 
 * @{ 
 */
package edu.mit.csail.sdg.squander.spec;

import forge.program.ForgeDomain;
import forge.program.ForgeExpression;
import forge.program.ForgeType;
import forge.program.GlobalVariable;
import forge.program.LocalDecls;
import forge.program.LocalVariable;

public interface ForgeEnv {

    public static enum State { PRE, POST };
    
    public ForgeEnv setPreStateMode();
    public ForgeEnv addLocal(LocalVariable var);
    
    public ForgeType integerType();
    public ForgeType booleanType();
    public ForgeType stringType();
    public ForgeType nullType();
    
    public ForgeExpression intExpr(int i);
    public ForgeExpression stringExpr(String text);
    public ForgeExpression enumConst(Enum<?> e);
    public ForgeExpression trueExpr();
    public ForgeExpression falseExpr();
    public ForgeExpression returnVar();
    public ForgeExpression throwVar();
    public LocalVariable thisVar();
    public ForgeExpression arg(int i);
    public ForgeExpression arrayLength(JType jtype);
    public ForgeExpression bracketElems(JType jtype);
    public ForgeExpression globalVar(GlobalVariable var);
    public JType.Unary classForDomain(ForgeDomain domain);
    
    public LocalDecls emptyDecls();
    public LocalVariable newLocalVar(String name, ForgeType type);
    public LocalVariable findLocal(String name);
    public GlobalVariable ensureGlobal(JField field);
    public GlobalVariable ensureConst(String name);
    public ForgeDomain.Unary ensureDomain(JType.Unary clz);
    public ForgeDomain.Unary typeForCls(JType.Unary clz, boolean includeNull);
    
    public void ensureInt(int i);
    public void ensureAllInts();
}
/*! @} */
