/*
 [The "BSD licence"]
 Copyright (c) 2007-2008 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g 
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created 
 *          elementValuePair and elementValuePairs rules, then used them in the 
 *          annotation rule.  Allows it to recognize annotation references with 
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which 
 *          has the Identifier portion in it, the parser would fail on constants in 
 *          annotation definitions because it expected two identifiers.  
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to 
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing 
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *          Again, JLS doesn't seem to allow this, but java.lang.Class has an example of
 *          of this construct.
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 *      
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *  Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *  and forVarControl to use variableModifier* not 'final'? (annotation)?
 *
 *  Version 1.0.5 -- Terence, June 21, 2007
 *  --a[i].foo didn't work. Fixed unaryExpression
 *
 *  Version 1.0.6 -- John Ridgway, March 17, 2008
 *      Made "assert" a switchable keyword like "enum".
 *      Fixed compilationUnit to disallow "annotation importDeclaration ...".
 *      Changed "Identifier ('.' Identifier)*" to "qualifiedName" in more 
 *          places.
 *      Changed modifier* and/or variableModifier* to classOrInterfaceModifiers,
 *          modifiers or variableModifiers, as appropriate.
 *      Renamed "bound" to "typeBound" to better match language in the JLS.
 *      Added "memberDeclaration" which rewrites to methodDeclaration or 
 *      fieldDeclaration and pulled type into memberDeclaration.  So we parse 
 *          type and then move on to decide whether we're dealing with a field
 *          or a method.
 *      Modified "constructorDeclaration" to use "constructorBody" instead of
 *          "methodBody".  constructorBody starts with explicitConstructorInvocation,
 *          then goes on to blockStatement*.  Pulling explicitConstructorInvocation
 *          out of expressions allowed me to simplify "primary".
 *      Changed variableDeclarator to simplify it.
 *      Changed type to use classOrInterfaceType, thus simplifying it; of course
 *          I then had to add classOrInterfaceType, but it is used in several 
 *          places.
 *      Fixed annotations, old version allowed "@X(y,z)", which is illegal.
 *      Added optional comma to end of "elementValueArrayInitializer"; as per JLS.
 *      Changed annotationTypeElementRest to use normalClassDeclaration and 
 *          normalInterfaceDeclaration rather than classDeclaration and 
 *          interfaceDeclaration, thus getting rid of a couple of grammar ambiguities.
 *      Split localVariableDeclaration into localVariableDeclarationStatement
 *          (includes the terminating semi-colon) and localVariableDeclaration.  
 *          This allowed me to use localVariableDeclaration in "forInit" clauses,
 *           simplifying them.
 *      Changed switchBlockStatementGroup to use multiple labels.  This adds an
 *          ambiguity, but if one uses appropriately greedy parsing it yields the
 *           parse that is closest to the meaning of the switch statement.
 *      Renamed "forVarControl" to "enhancedForControl" -- JLS language.
 *      Added semantic predicates to test for shift operations rather than other
 *          things.  Thus, for instance, the string "< <" will never be treated
 *          as a left-shift operator.
 *      In "creator" we rule out "nonWildcardTypeArguments" on arrayCreation, 
 *          which are illegal.
 *      Moved "nonWildcardTypeArguments into innerCreator.
 *      Removed 'super' superSuffix from explicitGenericInvocation, since that
 *          is only used in explicitConstructorInvocation at the beginning of a
 *           constructorBody.  (This is part of the simplification of expressions
 *           mentioned earlier.)
 *      Simplified primary (got rid of those things that are only used in
 *          explicitConstructorInvocation).
 *      Lexer -- removed "Exponent?" from FloatingPointLiteral choice 4, since it
 *          led to an ambiguity.
 *
 *      This grammar successfully parses every .java file in the JDK 1.5 source 
 *          tree (excluding those whose file names include '-', which are not
 *          valid Java compilation units).
 *
 *  Known remaining problems:
 *      "Letter" and "JavaIDDigit" are wrong.  The actual specification of
 *      "Letter" should be "a character for which the method
 *      Character.isJavaIdentifierStart(int) returns true."  A "Java 
 *      letter-or-digit is a character for which the method 
 *      Character.isJavaIdentifierPart(int) returns true."
 */

grammar JFSL;

options {
    backtrack = true;
    memoize = true;
    output=AST;
    ASTLabelType=Node;
}
    
tokens {
  DECLARATION;
  FRAME;

  NULL;

  MOD_DISJ; 
  MOD_NONE; 
  
  DECL_SET;
  DECL_SEQ;
  DECL_NONE;
  FRAME_LOCATION;
  FRAME_ALL;
  
  BINARY; 
  UNARY;
  QUANTIFY;
  QUANTIFY_ENUM;
  CONDITIONAL;
  ARGUMENTS;
  
  CHAIN;
  PROJECTION;
  
  JOIN;
  JOIN_REFLEXIVE;
  BRACKET;
  CALL;
  
  FRAME_FIELD;
  FRAME_FILTER;
  FRAME_DOMAIN;
  
  OLD;
  ARGUMENT;
  AMBIGUOUS;
  FIELD;
  
  CAST;
  CLASS_DESIGNATOR; 

  THIS_VAR;
  SUPER_VAR;
  IDENTIFIER;
  VOID;
  RETURN_VAR;
  THROW_VAR;
  LAMBDA_VAR;

  DECLS;
  MDECLS;    
    
  OP_EQ;
  OP_NEQ;
  OP_OR;
  OP_XOR;   
  OP_AND;
  OP_NOT; 
  
  OP_BIT_OR;
  OP_BIT_XOR;

  OP_INTERSECTION;  
  OP_BIT_AND_OR_INTERSECTION;

  OP_TRANSPOSE;
  OP_BIT_NOT_OR_TRANSPOSE;
  
  OP_CLOSURE;
  
  OP_GT;
  OP_LT;
  OP_GEQ;
  OP_LEQ;
  
  OP_SHL;
  OP_SHR;
  OP_USHR;
  
  OP_INSTANCEOF;
  
  OP_PLUS;
  OP_UNION;
  OP_PLUS_OR_UNION;
  
  OP_MINUS;
  OP_DIFFERENCE;
  OP_MINUS_OR_DIFFERENCE;

  OP_RELATIONAL_OVERRIDE;
  OP_RELATIONAL_COMPOSE;
  
  OP_TIMES;
  OP_DIVIDE;
  OP_MOD;
      
  OP_EQUIV;
  OP_NEQUIV;
  OP_IMPLIES;

  OP_SET_ONE;
  OP_SET_SOME;
  OP_SET_NO;
  OP_SET_LONE;
  OP_SET_NUM;
  OP_SET_SUM; 
  OP_SET_ALL;
  OP_SET_EXISTS;
  OP_SET_COMPREHENSION;
  OP_SET_COMPREHENSION_ENUM;
  OP_RANGE;
  
  OP_SET_SUBSET;
  OP_NSET_SUBSET;
  
  LIT_TRUE;
  LIT_FALSE;
  LIT_NULL;
  
  TYPE_BOOLEAN;
  TYPE_CHAR;
  TYPE_BYTE;
  TYPE_SHORT;
  TYPE_INT;
  TYPE_LONG;
  TYPE_FLOAT;
  TYPE_DOUBLE;  
  
  TYPE_REF;
  TYPE_ARRAY;

  // Compilation unit token
  FILE;
  INVARIANT;
  SPECFIELD;
  PACKAGE;
  TYPE_DECLARATION;
  TYPE_PARAMETERS;
  IMPORT;
  METHOD;
  METHOD_PARAMETERS;
  SPECCASE;
  SPECIFICATION;
  REQUIRES;
  ENSURES;
  MODIFIES; 
  THROWS;
  HELPER;
  PURE;
}
    
@lexer::header { 
package edu.mit.csail.sdg.squander.parser;
}

@parser::header {
package edu.mit.csail.sdg.squander.parser;

import edu.mit.csail.sdg.squander.spec.*;
}

@members {
// Base type of nodes in the AST 
public static final class Node extends CommonTree {
    // Used for refining ambiguous operator 
    private boolean flag;
    
    // Used for determining decision 
    private Decision decision;
    
    // Decision attributes 
    public JType jtype;
    public JField field;
    public Enum enumConst; 
    
    public Node(Token t) {
      super(t);
    }
    
    public Node(Node node) {
        super(node);
        this.flag = false;
        this.decision = null;
    }
    
    public void setFlag(boolean flag) { this.flag = flag; }    
    public boolean flag()             { return flag; }
    public void setJType(JType jtype) { this.jtype = jtype; }
    public Decision decision()        { return decision; }
            
    public void setDecision(Decision d) {
        //assert this.decision == null || this.decision == d : "change of decision from " + this.decision + " to " + d;
        this.decision = d;
    }
    
    @Override
    public Tree dupNode() {
      return new Node(this);
    }       
};

// factory 
public static final class NodeAdaptor extends CommonTreeAdaptor {
    @Override
    public Object create(Token payload) {
        return new Node(payload);
    }
};

// decision 
public static enum Decision {LOCAL, GLOBAL, TYPE, FRAGMENT, CONST, ENUM};

public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    throw new JFSLParserException(e);
}
}    

// PARSER


// Top-level expressions
clause: expression EOF!;
specField: declaration EOF!;
modifies: frame EOF!;
      
// File structure 
// This is based on Java grammar but omits many of the
// redundant details
compilationUnit
  : 'package' packageName ';'
    importDeclaration
    typeDeclaration ';'*
    EOF -> ^(FILE packageName importDeclaration typeDeclaration)
  ;

packageName
  : Identifier ('.' Identifier)* -> ^(PACKAGE Identifier+) 
  ;
  
importDeclaration
  : ('import' packageName ';')* -> ^(IMPORT packageName*)
  ;
  
typeDeclaration
  : ('class' | 'interface') Identifier typeParameters?
    '{' typeBodyDeclaration* '}' -> ^(TYPE_DECLARATION Identifier ^(TYPE_PARAMETERS typeParameters?) typeBodyDeclaration*)
  ;

typeParameters
  : '<' Identifier (',' Identifier)* '>' -> ^(IDENTIFIER Identifier)+
  ;
    
typeParameters2
  : '<' primaryTypeIdentifier (',' primaryTypeIdentifier)* '>' -> primaryTypeIdentifier+
  ;

typeBodyDeclaration
  : ';'!
  | typeDeclaration 
  | '@Invariant' '(' expression ')' -> ^(INVARIANT expression)
  | '@SpecField' '(' declaration ')' -> ^(SPECFIELD declaration)
  | Identifier '(' methodParameters ')' '{' specCase (';' specCase)* (';')* '}' 
    -> ^(METHOD Identifier methodParameters ^(SPECIFICATION specCase+))
  ;
  
methodParameters
  : (type Identifier (',' type Identifier)*)? -> ^(METHOD_PARAMETERS type* Identifier*)
  ;
    
specCase  
  : ('@Requires' '(' requires = expression ')')?  
    ('@Ensures' '(' ensures = expression ')' | ('@Throws' '(' except = expression ')'))? 
    ('@Modifies' '(' changes = frame ')')? 
    -> ^(SPECCASE ^(REQUIRES $requires?) ^(ENSURES $ensures?) ^(THROWS $except?) ^(MODIFIES $changes?))
  | '@Helper' -> ^(HELPER)   
  | '@Pure' -> ^(PURE)
  ;
  
// Expressions

declaration
  : ('public' | 'private')? Identifier ':' declarationMult additiveExpression 'from' frame ('|' expression)?
  -> ^(DECLARATION ^(IDENTIFIER Identifier) declarationMult additiveExpression frame expression?)
  | ('public' | 'private')? Identifier ':' declarationMult additiveExpression ('|' expression)?
  -> ^(DECLARATION ^(IDENTIFIER Identifier) declarationMult additiveExpression NULL expression?)
  ;
  
quantIdMod
  : x='disj' -> ^(MOD_DISJ $x)
  | -> MOD_NONE
  ;
  
declarationMult
  : setDeclOp
  | x='set' -> ^(DECL_SET $x)   
  | x='seq' -> ^(DECL_SEQ $x)
  | -> DECL_NONE
  ;
  
frame
  : ( | storeRef (',' storeRef)*) -> ^(FRAME storeRef*)
  ;

storeRef
  : storePrimary storeSelectors ('|' ff=expression)? ('from' fd=expression)?
     -> ^(FRAME_LOCATION ^(FRAME_FIELD storePrimary storeSelectors) ^(FRAME_FILTER $ff)? ^(FRAME_DOMAIN $fd)?) 
  ;
  
storePrimary
  : Identifier -> ^(IDENTIFIER Identifier) 
  | common
  ;
  
storeSelectors
  : selector* storeWildCard 
  | selector+
  ;   
  
storeWildCard
   : ('.' '*') -> FRAME_ALL 
  ;
  
keywordLiteral
  : 'true' ->   LIT_TRUE
  | 'false' ->  LIT_FALSE
  | 'null' ->   LIT_NULL
  ;
    
literal 
  : integerLiteral
  | FloatingPointLiteral
  | CharacterLiteral
  | StringLiteral
  | keywordLiteral
  ;

integerLiteral
  : HexLiteral
  | OctalLiteral
  | DecimalLiteral
  ;
                
primitiveType
  : x='boolean'-> ^(TYPE_BOOLEAN $x)
  | x='char'   -> ^(TYPE_CHAR $x)
  | x='byte'   -> ^(TYPE_BYTE $x)
  | x='short'  -> ^(TYPE_SHORT $x)
  | x='int'    -> ^(TYPE_INT $x)
  | x='long'   -> ^(TYPE_LONG $x)
  | x='float'  -> ^(TYPE_FLOAT $x)
  | x='double' -> ^(TYPE_DOUBLE $x)
  ;

typeName
  : Identifier ('.' Identifier)* typeParameters? -> ^(TYPE_REF ^(IDENTIFIER Identifier)+ ^(TYPE_PARAMETERS typeParameters)?)
  ;
  
typeDisambiguous
  : (primitiveType -> primitiveType) (('[' ']') -> ^(TYPE_ARRAY $typeDisambiguous))*
  | (typeName -> typeName) (('[' ']') -> ^(TYPE_ARRAY $typeDisambiguous))+
  ;

type
  : typeDisambiguous
  | typeName    
  ;   
            
// JAVA EXPRESSIONS

parExpression
  : x='('! expression ')'! 
  ;
            
expressionList
  :   expression (','! expression)*
  ;
      
arguments
  : '(' expressionList? ')' -> ^(ARGUMENTS expressionList?)
  ;

// MULTIPLE ARITY EXPRESSIONS

expression
  : conditionalExpression
  ;
  
conditionalExpression
  : ( quantifiedExpression -> quantifiedExpression)
    ( '?' expression ':' expression -> ^(CONDITIONAL $conditionalExpression expression expression)
    )?
  ;
  
quantifiedExpression 
  : setQuantOp decls  '|' expression -> ^(QUANTIFY setQuantOp decls expression)
  | setQuantOp mdecls '|' expression -> ^(QUANTIFY setQuantOp mdecls expression)
  | logicalExpression
  ;
  
logicalExpression
  : (conditionalXorExpression -> conditionalXorExpression)
    ( logicalOp conditionalXorExpression -> ^(BINARY logicalOp $logicalExpression conditionalXorExpression)
    )?
  ;
  
logicalOp
  :   '<' '=' '>' -> OP_EQUIV 
  |   '<' '!' '>' -> OP_NEQUIV 
  |   '=' '>'     -> OP_IMPLIES
  |   'implies'   -> OP_IMPLIES
  |   'iff'       -> OP_EQUIV
  ;
  
conditionalXorExpression
  : (conditionalOrExpression -> conditionalOrExpression)
      ( '^^' a=conditionalOrExpression -> ^(BINARY OP_XOR $conditionalXorExpression $a)
      )*
  ;  
  
conditionalOrExpression
  : (conditionalAndExpression -> conditionalAndExpression)
      ( '||' a=conditionalAndExpression -> ^(BINARY OP_OR $conditionalOrExpression $a)
      )*
  ;

conditionalAndExpression
  : (inclusiveOrExpression -> inclusiveOrExpression)
      ( '&&' a=inclusiveOrExpression -> ^(BINARY OP_AND $conditionalAndExpression $a)     
      )*
  ;

inclusiveOrExpression
  : (exclusiveOrExpression -> exclusiveOrExpression)
      ( '|' a=exclusiveOrExpression -> ^(BINARY OP_BIT_OR $inclusiveOrExpression $a)
      )*
  ;

exclusiveOrExpression
  : (andExpression -> andExpression)
      ( '^' a=andExpression -> ^(BINARY OP_BIT_XOR $exclusiveOrExpression $a)
      )*
  ;

andExpression
  :   (equalityExpression -> equalityExpression)
      ( bitAndOp a=equalityExpression -> ^(BINARY bitAndOp $andExpression $a)
      )*
  ;

bitAndOp 
  : x='&' -> ^(OP_BIT_AND_OR_INTERSECTION $x)
  ;
 

equalityExpression
  :   (instanceOfExpression -> instanceOfExpression)
      ( op=equalityOp a=instanceOfExpression -> ^(BINARY $op $equalityExpression $a)
      )*
  ;
  
equalityOp 
  : eqOp
  | neqOp
  ;
    
eqOp
  : x='=' '=' -> ^(OP_EQ $x) 
  | x='='     -> ^(OP_EQ $x)
  ;

neqOp
  : x='!' '=' -> ^(OP_NEQ $x)
  ;
    
instanceOfExpression
  :   (relationalExpression -> relationalExpression)
      ( 'instanceof' a=type -> ^(BINARY OP_INSTANCEOF $instanceOfExpression $a)
      )?
  ;

relationalExpression
  :   (setUnaryExpression -> setUnaryExpression)
      ( op=relationalOp a=setUnaryExpression -> ^(BINARY $op $relationalExpression $a)
      )*
  ;
  
inOp
  : x='in' -> ^(OP_SET_SUBSET $x)
  ;
  
relationalOp
  : x='<' '='  -> ^(OP_LEQ $x) 
  | x='>' '='  -> ^(OP_GEQ $x) 
  | x='<'      -> ^(OP_LT $x) 
  | x='>'      -> ^(OP_GT $x) 
  | inOp 
  | x='!' 'in' -> ^(OP_NSET_SUBSET $x)
  ;
  
setUnaryExpression
  : setUnaryOp joinExpression -> ^(UNARY setUnaryOp joinExpression)
  | shiftExpression
  ;
  
shiftExpression
  :   (rangeExpression -> rangeExpression)
      ( op=shiftOp a=rangeExpression -> ^(BINARY $op $shiftExpression $a)
      )*
  ;

shiftOp
  : x='<' '<'     -> ^(OP_SHL $x) 
  | x='>' '>' '>' -> ^(OP_USHR $x) 
  | x='>' '>'     -> ^(OP_SHR $x)
  ;
  
rangeExpression
  : (additiveExpression -> additiveExpression)
    ('...' a=additiveExpression -> ^(BINARY OP_RANGE $rangeExpression $a)
    )* 
  ;

additiveExpression
  :   (sizeExpression -> sizeExpression) 
      ( op=additiveOp a=sizeExpression -> ^(BINARY $op $additiveExpression $a)
      )*
  ;
  
additiveOp  
  : x='+'   -> ^(OP_PLUS_OR_UNION $x) 
  | x='-'   -> ^(OP_MINUS_OR_DIFFERENCE $x)
  ;

sizeExpression
  : '#' joinExpression -> ^(UNARY OP_SET_NUM joinExpression)
  | multiplicativeExpression
  ;
  
multiplicativeExpression
  :   (setAdditiveExpression -> setAdditiveExpression)
      ( op=multOp a=setAdditiveExpression -> ^(BINARY $op $multiplicativeExpression $a))*
  ;

multOp
  : x='*'   -> ^(OP_TIMES $x) 
  | x='/'   -> ^(OP_DIVIDE $x) 
  | x='%'   -> ^(OP_MOD $x)
  ;
  
setAdditiveExpression
  :  (overrideExpression -> overrideExpression) 
        ( op=setAdditiveOp a=overrideExpression -> ^(BINARY $op $setAdditiveExpression $a)
        )*
  ; 
  
setAdditiveOp
  : x='@' '+' -> ^(OP_UNION $x) 
  | x='@' '-' -> ^(OP_DIFFERENCE $x)
  ;
    
overrideExpression
  :  (intersectionExpression -> intersectionExpression)
      ( '++' a=intersectionExpression -> ^(BINARY OP_RELATIONAL_OVERRIDE $overrideExpression $a) // associative but not commutative
      )*
  ;   
  
intersectionExpression
  :  (composeExpression -> composeExpression)
      ( '@' '&' a=composeExpression -> ^(BINARY OP_INTERSECTION $intersectionExpression $a)
      )*
  ;
  
composeExpression
  :  (unaryExpression -> unaryExpression)
      ( '->' a=unaryExpression -> ^(BINARY OP_RELATIONAL_COMPOSE $composeExpression $a)
      )*
  ;   
    
unaryExpression
  : '+' unaryExpression -> ^(UNARY OP_PLUS unaryExpression)
  | '-' unaryExpression -> ^(UNARY OP_MINUS unaryExpression)
  | unaryExpressionNotPlusMinus
  ;

unaryExpressionNotPlusMinus
  : '~' unaryExpression -> ^(UNARY OP_BIT_NOT_OR_TRANSPOSE unaryExpression)
  | '!' unaryExpression -> ^(UNARY OP_NOT unaryExpression)    
  | castExpression
  | joinExpression  
  ;     

castExpression
  : '(' primitiveType ')' unaryExpression -> ^(CAST primitiveType unaryExpression)
//  | '(' (type | expression) ')' unaryExpressionNotPlusMinus -> ^(CAST type? expression? unaryExpressionNotPlusMinus)
  ;

decls 
  : (Identifier (inOp | ':') declarationMult rangeExpression) (',' (Identifier (inOp | ':') declarationMult rangeExpression))* 
         -> ^(DECLS (Identifier declarationMult rangeExpression)+)
  ;

mdecls 
  : (quantIdMod Identifier (',' Identifier)* (inOp | ':') declarationMult rangeExpression) 
         -> ^(MDECLS quantIdMod Identifier+ declarationMult rangeExpression)
  ;

relationalUnaryExpression
  : specUnaryOp parExpression -> ^(UNARY specUnaryOp parExpression)
  | specUnaryOp Identifier -> ^(UNARY specUnaryOp ^(IDENTIFIER Identifier))
  ;
    
joinExpression
  :   primary selector*-> ^(CHAIN primary selector*) 
  ;
    
common
  : parExpression
  | x='return' -> ^(RETURN_VAR $x)
  | x='throw'  -> ^(THROW_VAR $x)
  | x='this'   -> ^(THIS_VAR $x)
  | x='super'  -> ^(SUPER_VAR $x)
  | x='_'      -> ^(LAMBDA_VAR $x)
  | x='@old' '(' expression ')' -> ^(OLD expression)   
  | x='@arg' '(' integerLiteral ')' -> ^(ARGUMENT integerLiteral)
  | relationalUnaryExpression 
  | '{' decls '|' expression '}' -> ^(QUANTIFY OP_SET_COMPREHENSION decls expression)
  | '{' rangeExpression (',' rangeExpression)* '}' -> ^(OP_SET_COMPREHENSION_ENUM rangeExpression+)
  ;
  
primary
  : common
  | literal
  | primaryTypeIdentifier
  | typeName '@' Identifier -> ^(FIELD typeName ^(IDENTIFIER Identifier))
  | Identifier arguments -> ^(CALL Identifier arguments)
  ;
  
primaryTypeIdentifier
  : typeDisambiguous
  | Identifier ('.' Identifier)* typeParameters2? -> ^(AMBIGUOUS ^(IDENTIFIER Identifier)+ ^(TYPE_PARAMETERS typeParameters2)?)
  ;
  
//  Ignores inner this and super selectors, inner creators
selector
  : '.' Identifier arguments -> ^(CALL Identifier arguments)
  | '.' Identifier -> ^(JOIN ^(IDENTIFIER Identifier))
  | '.' primitiveType -> ^(JOIN primitiveType)
  | '.' common -> ^(JOIN common)
  | '.' '*' Identifier -> ^(JOIN_REFLEXIVE ^(IDENTIFIER Identifier))
  | '.' '*' common -> ^(JOIN_REFLEXIVE common)
  | '[' expression ']' -> ^(BRACKET expression)  
  | '[' ']' -> ^(BRACKET) 
  | '<' DecimalLiteral (',' DecimalLiteral)* '>' -> ^(PROJECTION DecimalLiteral+)
  ;
    
// specification-only expressions       
specUnaryOp
  : x='@' '^' -> ^(OP_CLOSURE $x)
  | x='^'     -> ^(OP_CLOSURE $x)
  | x='@' '~' -> ^(OP_TRANSPOSE $x)
  | x='~'     -> ^(OP_TRANSPOSE $x)
  ;
      
setDeclOp 
  : x='one'   -> ^(OP_SET_ONE $x)
  | x='some'  -> ^(OP_SET_SOME $x) 
  | x='lone'  -> ^(OP_SET_LONE $x) 
  ;     
setUnaryOp
  : setDeclOp
  | x='no'  -> ^(OP_SET_NO $x)
  | x='sum'   -> ^(OP_SET_SUM $x) 
  ;
  
setQuantOp
  : setUnaryOp
  | x='#'  -> ^(OP_SET_NUM $x)
  | x='all'   -> ^(OP_SET_ALL $x) 
  | x='exists' -> ^(OP_SET_EXISTS $x)
  ;

// -----------------------------------
// ------------ LEXER ----------------
// -----------------------------------

HexLiteral : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;

DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix? ;

OctalLiteral : '0' ('0'..'7')+ IntegerTypeSuffix? ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
IntegerTypeSuffix : ('l'|'L') ;

FloatingPointLiteral
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ FloatTypeSuffix
    ;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;

CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;

StringLiteral
    :  '"'  ( EscapeSequence | ~('\\'|'"') )* '"'
    |  '\'' ( EscapeSequence | ~('\''|'\\'))* '\''
    ;

fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;
  
Identifier
    :   Letter (Letter|JavaIDDigit)*
    ;

fragment
Letter
    :  '\u0024' |
       '\u0041'..'\u005a' |
       '\u005f' |
       '\u0061'..'\u007a' |
       '\u00c0'..'\u00d6' |
       '\u00d8'..'\u00f6' |
       '\u00f8'..'\u00ff' |
       '\u0100'..'\u1fff' |
       '\u3040'..'\u318f' |
       '\u3300'..'\u337f' |
       '\u3400'..'\u3d2d' |
       '\u4e00'..'\u9fff' |
       '\uf900'..'\ufaff'
    ;

fragment
JavaIDDigit
    :  '\u0030'..'\u0039' |
       '\u0660'..'\u0669' |
       '\u06f0'..'\u06f9' |
       '\u0966'..'\u096f' |
       '\u09e6'..'\u09ef' |
       '\u0a66'..'\u0a6f' |
       '\u0ae6'..'\u0aef' |
       '\u0b66'..'\u0b6f' |
       '\u0be7'..'\u0bef' |
       '\u0c66'..'\u0c6f' |
       '\u0ce6'..'\u0cef' |
       '\u0d66'..'\u0d6f' |
       '\u0e50'..'\u0e59' |
       '\u0ed0'..'\u0ed9' |
       '\u1040'..'\u1049'
   ;

WS  :  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;}
    ;
