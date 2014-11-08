// $ANTLR 3.2 Sep 23, 2009 12:02:23 JFSL.g 2012-09-25 11:21:23

package edu.mit.csail.sdg.squander.parser;

import edu.mit.csail.sdg.squander.spec.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

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
@SuppressWarnings({"all"})
public class JFSLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "DECLARATION", "FRAME", "NULL", "MOD_DISJ", "MOD_NONE", "DECL_SET", "DECL_SEQ", "DECL_NONE", "FRAME_LOCATION", "FRAME_ALL", "BINARY", "UNARY", "QUANTIFY", "QUANTIFY_ENUM", "CONDITIONAL", "ARGUMENTS", "CHAIN", "PROJECTION", "JOIN", "JOIN_REFLEXIVE", "BRACKET", "CALL", "FRAME_FIELD", "FRAME_FILTER", "FRAME_DOMAIN", "OLD", "ARGUMENT", "AMBIGUOUS", "FIELD", "CAST", "CLASS_DESIGNATOR", "THIS_VAR", "SUPER_VAR", "IDENTIFIER", "VOID", "RETURN_VAR", "THROW_VAR", "LAMBDA_VAR", "DECLS", "MDECLS", "OP_EQ", "OP_NEQ", "OP_OR", "OP_XOR", "OP_AND", "OP_NOT", "OP_BIT_OR", "OP_BIT_XOR", "OP_INTERSECTION", "OP_BIT_AND_OR_INTERSECTION", "OP_TRANSPOSE", "OP_BIT_NOT_OR_TRANSPOSE", "OP_CLOSURE", "OP_GT", "OP_LT", "OP_GEQ", "OP_LEQ", "OP_SHL", "OP_SHR", "OP_USHR", "OP_INSTANCEOF", "OP_PLUS", "OP_UNION", "OP_PLUS_OR_UNION", "OP_MINUS", "OP_DIFFERENCE", "OP_MINUS_OR_DIFFERENCE", "OP_RELATIONAL_OVERRIDE", "OP_RELATIONAL_COMPOSE", "OP_TIMES", "OP_DIVIDE", "OP_MOD", "OP_EQUIV", "OP_NEQUIV", "OP_IMPLIES", "OP_SET_ONE", "OP_SET_SOME", "OP_SET_NO", "OP_SET_LONE", "OP_SET_NUM", "OP_SET_SUM", "OP_SET_ALL", "OP_SET_EXISTS", "OP_SET_COMPREHENSION", "OP_SET_COMPREHENSION_ENUM", "OP_RANGE", "OP_SET_SUBSET", "OP_NSET_SUBSET", "LIT_TRUE", "LIT_FALSE", "LIT_NULL", "TYPE_BOOLEAN", "TYPE_CHAR", "TYPE_BYTE", "TYPE_SHORT", "TYPE_INT", "TYPE_LONG", "TYPE_FLOAT", "TYPE_DOUBLE", "TYPE_REF", "TYPE_ARRAY", "FILE", "INVARIANT", "SPECFIELD", "PACKAGE", "TYPE_DECLARATION", "TYPE_PARAMETERS", "IMPORT", "METHOD", "METHOD_PARAMETERS", "SPECCASE", "SPECIFICATION", "REQUIRES", "ENSURES", "MODIFIES", "THROWS", "HELPER", "PURE", "Identifier", "FloatingPointLiteral", "CharacterLiteral", "StringLiteral", "HexLiteral", "OctalLiteral", "DecimalLiteral", "HexDigit", "IntegerTypeSuffix", "Exponent", "FloatTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "Letter", "JavaIDDigit", "WS", "'package'", "';'", "'.'", "'import'", "'class'", "'interface'", "'{'", "'}'", "'<'", "','", "'>'", "'@Invariant'", "'('", "')'", "'@SpecField'", "'@Requires'", "'@Ensures'", "'@Throws'", "'@Modifies'", "'@Helper'", "'@Pure'", "'public'", "'private'", "':'", "'from'", "'|'", "'disj'", "'set'", "'seq'", "'*'", "'true'", "'false'", "'null'", "'boolean'", "'char'", "'byte'", "'short'", "'int'", "'long'", "'float'", "'double'", "'['", "']'", "'?'", "'='", "'!'", "'implies'", "'iff'", "'^^'", "'||'", "'&&'", "'^'", "'&'", "'instanceof'", "'in'", "'...'", "'+'", "'-'", "'#'", "'/'", "'%'", "'@'", "'++'", "'->'", "'~'", "'return'", "'throw'", "'this'", "'super'", "'_'", "'@old'", "'@arg'", "'one'", "'some'", "'lone'", "'no'", "'sum'", "'all'", "'exists'"
    };
    public static final int PACKAGE=108;
    public static final int T__159=159;
    public static final int T__158=158;
    public static final int OP_INSTANCEOF=64;
    public static final int OP_SET_ALL=85;
    public static final int OP_GEQ=59;
    public static final int TYPE_INT=99;
    public static final int OP_SET_LONE=82;
    public static final int OP_SET_NO=81;
    public static final int T__160=160;
    public static final int T__167=167;
    public static final int OP_INTERSECTION=52;
    public static final int EOF=-1;
    public static final int T__168=168;
    public static final int LIT_FALSE=93;
    public static final int T__165=165;
    public static final int T__166=166;
    public static final int T__163=163;
    public static final int T__164=164;
    public static final int T__161=161;
    public static final int T__162=162;
    public static final int OP_MOD=75;
    public static final int OP_EQ=44;
    public static final int IMPORT=111;
    public static final int T__148=148;
    public static final int T__147=147;
    public static final int T__149=149;
    public static final int TYPE_BYTE=97;
    public static final int DECL_SET=9;
    public static final int TYPE_PARAMETERS=110;
    public static final int DECL_SEQ=10;
    public static final int T__154=154;
    public static final int T__155=155;
    public static final int T__156=156;
    public static final int T__157=157;
    public static final int T__150=150;
    public static final int T__151=151;
    public static final int T__152=152;
    public static final int MOD_NONE=8;
    public static final int T__153=153;
    public static final int T__139=139;
    public static final int THIS_VAR=35;
    public static final int IntegerTypeSuffix=130;
    public static final int OP_DIFFERENCE=69;
    public static final int T__141=141;
    public static final int T__142=142;
    public static final int FILE=105;
    public static final int T__140=140;
    public static final int LAMBDA_VAR=41;
    public static final int T__145=145;
    public static final int T__146=146;
    public static final int T__143=143;
    public static final int T__144=144;
    public static final int METHOD_PARAMETERS=113;
    public static final int OP_SHL=61;
    public static final int TYPE_REF=103;
    public static final int WS=138;
    public static final int OP_SHR=62;
    public static final int SPECFIELD=107;
    public static final int QUANTIFY_ENUM=17;
    public static final int TYPE_CHAR=96;
    public static final int FloatingPointLiteral=123;
    public static final int JavaIDDigit=137;
    public static final int CALL=25;
    public static final int SPECIFICATION=115;
    public static final int CLASS_DESIGNATOR=34;
    public static final int CHAIN=20;
    public static final int T__215=215;
    public static final int T__216=216;
    public static final int T__213=213;
    public static final int T__214=214;
    public static final int T__217=217;
    public static final int OP_OR=46;
    public static final int AMBIGUOUS=31;
    public static final int TYPE_LONG=100;
    public static final int HexDigit=129;
    public static final int DECLARATION=4;
    public static final int T__202=202;
    public static final int ENSURES=117;
    public static final int T__203=203;
    public static final int T__204=204;
    public static final int T__205=205;
    public static final int OP_USHR=63;
    public static final int T__206=206;
    public static final int T__207=207;
    public static final int T__208=208;
    public static final int T__209=209;
    public static final int OP_RANGE=89;
    public static final int ARGUMENT=30;
    public static final int OP_NOT=49;
    public static final int OP_EQUIV=76;
    public static final int OP_SET_SOME=80;
    public static final int UNARY=15;
    public static final int OP_NEQUIV=77;
    public static final int T__210=210;
    public static final int T__212=212;
    public static final int T__211=211;
    public static final int HexLiteral=126;
    public static final int MDECLS=43;
    public static final int OP_DIVIDE=74;
    public static final int THROW_VAR=40;
    public static final int StringLiteral=125;
    public static final int OP_SET_COMPREHENSION=87;
    public static final int OP_XOR=47;
    public static final int OP_MINUS_OR_DIFFERENCE=70;
    public static final int PROJECTION=21;
    public static final int OctalEscape=135;
    public static final int CAST=33;
    public static final int OP_BIT_AND_OR_INTERSECTION=53;
    public static final int JOIN_REFLEXIVE=23;
    public static final int OP_AND=48;
    public static final int FloatTypeSuffix=132;
    public static final int OctalLiteral=127;
    public static final int BRACKET=24;
    public static final int OP_RELATIONAL_COMPOSE=72;
    public static final int OP_BIT_XOR=51;
    public static final int LIT_NULL=94;
    public static final int MODIFIES=118;
    public static final int Identifier=122;
    public static final int TYPE_DOUBLE=102;
    public static final int QUANTIFY=16;
    public static final int OP_MINUS=68;
    public static final int VOID=38;
    public static final int OP_LT=58;
    public static final int OP_CLOSURE=56;
    public static final int DECL_NONE=11;
    public static final int OP_SET_SUM=84;
    public static final int ARGUMENTS=19;
    public static final int NULL=6;
    public static final int CONDITIONAL=18;
    public static final int INVARIANT=106;
    public static final int OLD=29;
    public static final int THROWS=119;
    public static final int FRAME_LOCATION=12;
    public static final int T__200=200;
    public static final int T__201=201;
    public static final int FIELD=32;
    public static final int OP_RELATIONAL_OVERRIDE=71;
    public static final int OP_PLUS_OR_UNION=67;
    public static final int Letter=136;
    public static final int EscapeSequence=133;
    public static final int TYPE_BOOLEAN=95;
    public static final int OP_TRANSPOSE=54;
    public static final int CharacterLiteral=124;
    public static final int SUPER_VAR=36;
    public static final int OP_GT=57;
    public static final int Exponent=131;
    public static final int OP_SET_COMPREHENSION_ENUM=88;
    public static final int LIT_TRUE=92;
    public static final int OP_BIT_NOT_OR_TRANSPOSE=55;
    public static final int TYPE_DECLARATION=109;
    public static final int T__199=199;
    public static final int PURE=121;
    public static final int T__198=198;
    public static final int T__197=197;
    public static final int T__196=196;
    public static final int T__195=195;
    public static final int T__194=194;
    public static final int FRAME_DOMAIN=28;
    public static final int T__193=193;
    public static final int T__192=192;
    public static final int T__191=191;
    public static final int T__190=190;
    public static final int IDENTIFIER=37;
    public static final int OP_BIT_OR=50;
    public static final int OP_SET_SUBSET=90;
    public static final int MOD_DISJ=7;
    public static final int SPECCASE=114;
    public static final int T__184=184;
    public static final int T__183=183;
    public static final int T__186=186;
    public static final int T__185=185;
    public static final int T__188=188;
    public static final int T__187=187;
    public static final int TYPE_SHORT=98;
    public static final int T__189=189;
    public static final int OP_PLUS=65;
    public static final int HELPER=120;
    public static final int T__180=180;
    public static final int TYPE_FLOAT=101;
    public static final int FRAME_FILTER=27;
    public static final int OP_SET_EXISTS=86;
    public static final int T__182=182;
    public static final int OP_NSET_SUBSET=91;
    public static final int T__181=181;
    public static final int DECLS=42;
    public static final int DecimalLiteral=128;
    public static final int JOIN=22;
    public static final int T__175=175;
    public static final int T__174=174;
    public static final int OP_SET_NUM=83;
    public static final int T__173=173;
    public static final int T__172=172;
    public static final int TYPE_ARRAY=104;
    public static final int T__179=179;
    public static final int REQUIRES=116;
    public static final int T__178=178;
    public static final int OP_SET_ONE=79;
    public static final int T__177=177;
    public static final int T__176=176;
    public static final int FRAME_FIELD=26;
    public static final int UnicodeEscape=134;
    public static final int OP_NEQ=45;
    public static final int T__171=171;
    public static final int T__170=170;
    public static final int FRAME=5;
    public static final int OP_LEQ=60;
    public static final int OP_TIMES=73;
    public static final int FRAME_ALL=13;
    public static final int BINARY=14;
    public static final int T__169=169;
    public static final int OP_UNION=66;
    public static final int METHOD=112;
    public static final int RETURN_VAR=39;
    public static final int OP_IMPLIES=78;

    // delegates
    // delegators


        public JFSLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public JFSLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[232+1];
             
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return JFSLParser.tokenNames; }
    public String getGrammarFileName() { return "JFSL.g"; }


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


    public static class clause_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "clause"
    // JFSL.g:398:1: clause : expression EOF ;
    public final JFSLParser.clause_return clause() throws RecognitionException {
        JFSLParser.clause_return retval = new JFSLParser.clause_return();
        retval.start = input.LT(1);
        int clause_StartIndex = input.index();
        Node root_0 = null;

        Token EOF2=null;
        JFSLParser.expression_return expression1 = null;


        Node EOF2_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }
            // JFSL.g:398:7: ( expression EOF )
            // JFSL.g:398:9: expression EOF
            {
            root_0 = (Node)adaptor.nil();

            pushFollow(FOLLOW_expression_in_clause793);
            expression1=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression1.getTree());
            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_clause795); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 1, clause_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "clause"

    public static class specField_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "specField"
    // JFSL.g:399:1: specField : declaration EOF ;
    public final JFSLParser.specField_return specField() throws RecognitionException {
        JFSLParser.specField_return retval = new JFSLParser.specField_return();
        retval.start = input.LT(1);
        int specField_StartIndex = input.index();
        Node root_0 = null;

        Token EOF4=null;
        JFSLParser.declaration_return declaration3 = null;


        Node EOF4_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }
            // JFSL.g:399:10: ( declaration EOF )
            // JFSL.g:399:12: declaration EOF
            {
            root_0 = (Node)adaptor.nil();

            pushFollow(FOLLOW_declaration_in_specField802);
            declaration3=declaration();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, declaration3.getTree());
            EOF4=(Token)match(input,EOF,FOLLOW_EOF_in_specField804); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 2, specField_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "specField"

    public static class modifies_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "modifies"
    // JFSL.g:400:1: modifies : frame EOF ;
    public final JFSLParser.modifies_return modifies() throws RecognitionException {
        JFSLParser.modifies_return retval = new JFSLParser.modifies_return();
        retval.start = input.LT(1);
        int modifies_StartIndex = input.index();
        Node root_0 = null;

        Token EOF6=null;
        JFSLParser.frame_return frame5 = null;


        Node EOF6_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }
            // JFSL.g:400:9: ( frame EOF )
            // JFSL.g:400:11: frame EOF
            {
            root_0 = (Node)adaptor.nil();

            pushFollow(FOLLOW_frame_in_modifies811);
            frame5=frame();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, frame5.getTree());
            EOF6=(Token)match(input,EOF,FOLLOW_EOF_in_modifies813); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 3, modifies_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "modifies"

    public static class compilationUnit_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "compilationUnit"
    // JFSL.g:405:1: compilationUnit : 'package' packageName ';' importDeclaration typeDeclaration ( ';' )* EOF -> ^( FILE packageName importDeclaration typeDeclaration ) ;
    public final JFSLParser.compilationUnit_return compilationUnit() throws RecognitionException {
        JFSLParser.compilationUnit_return retval = new JFSLParser.compilationUnit_return();
        retval.start = input.LT(1);
        int compilationUnit_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal7=null;
        Token char_literal9=null;
        Token char_literal12=null;
        Token EOF13=null;
        JFSLParser.packageName_return packageName8 = null;

        JFSLParser.importDeclaration_return importDeclaration10 = null;

        JFSLParser.typeDeclaration_return typeDeclaration11 = null;


        Node string_literal7_tree=null;
        Node char_literal9_tree=null;
        Node char_literal12_tree=null;
        Node EOF13_tree=null;
        RewriteRuleTokenStream stream_139=new RewriteRuleTokenStream(adaptor,"token 139");
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleTokenStream stream_140=new RewriteRuleTokenStream(adaptor,"token 140");
        RewriteRuleSubtreeStream stream_packageName=new RewriteRuleSubtreeStream(adaptor,"rule packageName");
        RewriteRuleSubtreeStream stream_typeDeclaration=new RewriteRuleSubtreeStream(adaptor,"rule typeDeclaration");
        RewriteRuleSubtreeStream stream_importDeclaration=new RewriteRuleSubtreeStream(adaptor,"rule importDeclaration");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }
            // JFSL.g:406:3: ( 'package' packageName ';' importDeclaration typeDeclaration ( ';' )* EOF -> ^( FILE packageName importDeclaration typeDeclaration ) )
            // JFSL.g:406:5: 'package' packageName ';' importDeclaration typeDeclaration ( ';' )* EOF
            {
            string_literal7=(Token)match(input,139,FOLLOW_139_in_compilationUnit833); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_139.add(string_literal7);

            pushFollow(FOLLOW_packageName_in_compilationUnit835);
            packageName8=packageName();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_packageName.add(packageName8.getTree());
            char_literal9=(Token)match(input,140,FOLLOW_140_in_compilationUnit837); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_140.add(char_literal9);

            pushFollow(FOLLOW_importDeclaration_in_compilationUnit843);
            importDeclaration10=importDeclaration();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_importDeclaration.add(importDeclaration10.getTree());
            pushFollow(FOLLOW_typeDeclaration_in_compilationUnit849);
            typeDeclaration11=typeDeclaration();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_typeDeclaration.add(typeDeclaration11.getTree());
            // JFSL.g:408:21: ( ';' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==140) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // JFSL.g:0:0: ';'
            	    {
            	    char_literal12=(Token)match(input,140,FOLLOW_140_in_compilationUnit851); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_140.add(char_literal12);


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            EOF13=(Token)match(input,EOF,FOLLOW_EOF_in_compilationUnit858); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF13);



            // AST REWRITE
            // elements: packageName, importDeclaration, typeDeclaration
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 409:9: -> ^( FILE packageName importDeclaration typeDeclaration )
            {
                // JFSL.g:409:12: ^( FILE packageName importDeclaration typeDeclaration )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(FILE, "FILE"), root_1);

                adaptor.addChild(root_1, stream_packageName.nextTree());
                adaptor.addChild(root_1, stream_importDeclaration.nextTree());
                adaptor.addChild(root_1, stream_typeDeclaration.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 4, compilationUnit_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "compilationUnit"

    public static class packageName_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "packageName"
    // JFSL.g:412:1: packageName : Identifier ( '.' Identifier )* -> ^( PACKAGE ( Identifier )+ ) ;
    public final JFSLParser.packageName_return packageName() throws RecognitionException {
        JFSLParser.packageName_return retval = new JFSLParser.packageName_return();
        retval.start = input.LT(1);
        int packageName_StartIndex = input.index();
        Node root_0 = null;

        Token Identifier14=null;
        Token char_literal15=null;
        Token Identifier16=null;

        Node Identifier14_tree=null;
        Node char_literal15_tree=null;
        Node Identifier16_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_141=new RewriteRuleTokenStream(adaptor,"token 141");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // JFSL.g:413:3: ( Identifier ( '.' Identifier )* -> ^( PACKAGE ( Identifier )+ ) )
            // JFSL.g:413:5: Identifier ( '.' Identifier )*
            {
            Identifier14=(Token)match(input,Identifier,FOLLOW_Identifier_in_packageName883); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Identifier.add(Identifier14);

            // JFSL.g:413:16: ( '.' Identifier )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==141) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // JFSL.g:413:17: '.' Identifier
            	    {
            	    char_literal15=(Token)match(input,141,FOLLOW_141_in_packageName886); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_141.add(char_literal15);

            	    Identifier16=(Token)match(input,Identifier,FOLLOW_Identifier_in_packageName888); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Identifier.add(Identifier16);


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);



            // AST REWRITE
            // elements: Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 413:34: -> ^( PACKAGE ( Identifier )+ )
            {
                // JFSL.g:413:37: ^( PACKAGE ( Identifier )+ )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(PACKAGE, "PACKAGE"), root_1);

                if ( !(stream_Identifier.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_Identifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_Identifier.nextNode());

                }
                stream_Identifier.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 5, packageName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "packageName"

    public static class importDeclaration_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "importDeclaration"
    // JFSL.g:416:1: importDeclaration : ( 'import' packageName ';' )* -> ^( IMPORT ( packageName )* ) ;
    public final JFSLParser.importDeclaration_return importDeclaration() throws RecognitionException {
        JFSLParser.importDeclaration_return retval = new JFSLParser.importDeclaration_return();
        retval.start = input.LT(1);
        int importDeclaration_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal17=null;
        Token char_literal19=null;
        JFSLParser.packageName_return packageName18 = null;


        Node string_literal17_tree=null;
        Node char_literal19_tree=null;
        RewriteRuleTokenStream stream_140=new RewriteRuleTokenStream(adaptor,"token 140");
        RewriteRuleTokenStream stream_142=new RewriteRuleTokenStream(adaptor,"token 142");
        RewriteRuleSubtreeStream stream_packageName=new RewriteRuleSubtreeStream(adaptor,"rule packageName");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // JFSL.g:417:3: ( ( 'import' packageName ';' )* -> ^( IMPORT ( packageName )* ) )
            // JFSL.g:417:5: ( 'import' packageName ';' )*
            {
            // JFSL.g:417:5: ( 'import' packageName ';' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==142) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // JFSL.g:417:6: 'import' packageName ';'
            	    {
            	    string_literal17=(Token)match(input,142,FOLLOW_142_in_importDeclaration916); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_142.add(string_literal17);

            	    pushFollow(FOLLOW_packageName_in_importDeclaration918);
            	    packageName18=packageName();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_packageName.add(packageName18.getTree());
            	    char_literal19=(Token)match(input,140,FOLLOW_140_in_importDeclaration920); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_140.add(char_literal19);


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);



            // AST REWRITE
            // elements: packageName
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 417:33: -> ^( IMPORT ( packageName )* )
            {
                // JFSL.g:417:36: ^( IMPORT ( packageName )* )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(IMPORT, "IMPORT"), root_1);

                // JFSL.g:417:45: ( packageName )*
                while ( stream_packageName.hasNext() ) {
                    adaptor.addChild(root_1, stream_packageName.nextTree());

                }
                stream_packageName.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 6, importDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "importDeclaration"

    public static class typeDeclaration_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeDeclaration"
    // JFSL.g:420:1: typeDeclaration : ( 'class' | 'interface' ) Identifier ( typeParameters )? '{' ( typeBodyDeclaration )* '}' -> ^( TYPE_DECLARATION Identifier ^( TYPE_PARAMETERS ( typeParameters )? ) ( typeBodyDeclaration )* ) ;
    public final JFSLParser.typeDeclaration_return typeDeclaration() throws RecognitionException {
        JFSLParser.typeDeclaration_return retval = new JFSLParser.typeDeclaration_return();
        retval.start = input.LT(1);
        int typeDeclaration_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal20=null;
        Token string_literal21=null;
        Token Identifier22=null;
        Token char_literal24=null;
        Token char_literal26=null;
        JFSLParser.typeParameters_return typeParameters23 = null;

        JFSLParser.typeBodyDeclaration_return typeBodyDeclaration25 = null;


        Node string_literal20_tree=null;
        Node string_literal21_tree=null;
        Node Identifier22_tree=null;
        Node char_literal24_tree=null;
        Node char_literal26_tree=null;
        RewriteRuleTokenStream stream_143=new RewriteRuleTokenStream(adaptor,"token 143");
        RewriteRuleTokenStream stream_144=new RewriteRuleTokenStream(adaptor,"token 144");
        RewriteRuleTokenStream stream_145=new RewriteRuleTokenStream(adaptor,"token 145");
        RewriteRuleTokenStream stream_146=new RewriteRuleTokenStream(adaptor,"token 146");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_typeParameters=new RewriteRuleSubtreeStream(adaptor,"rule typeParameters");
        RewriteRuleSubtreeStream stream_typeBodyDeclaration=new RewriteRuleSubtreeStream(adaptor,"rule typeBodyDeclaration");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // JFSL.g:421:3: ( ( 'class' | 'interface' ) Identifier ( typeParameters )? '{' ( typeBodyDeclaration )* '}' -> ^( TYPE_DECLARATION Identifier ^( TYPE_PARAMETERS ( typeParameters )? ) ( typeBodyDeclaration )* ) )
            // JFSL.g:421:5: ( 'class' | 'interface' ) Identifier ( typeParameters )? '{' ( typeBodyDeclaration )* '}'
            {
            // JFSL.g:421:5: ( 'class' | 'interface' )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==143) ) {
                alt4=1;
            }
            else if ( (LA4_0==144) ) {
                alt4=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // JFSL.g:421:6: 'class'
                    {
                    string_literal20=(Token)match(input,143,FOLLOW_143_in_typeDeclaration947); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_143.add(string_literal20);


                    }
                    break;
                case 2 :
                    // JFSL.g:421:16: 'interface'
                    {
                    string_literal21=(Token)match(input,144,FOLLOW_144_in_typeDeclaration951); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_144.add(string_literal21);


                    }
                    break;

            }

            Identifier22=(Token)match(input,Identifier,FOLLOW_Identifier_in_typeDeclaration954); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Identifier.add(Identifier22);

            // JFSL.g:421:40: ( typeParameters )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==147) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // JFSL.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_typeDeclaration956);
                    typeParameters23=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_typeParameters.add(typeParameters23.getTree());

                    }
                    break;

            }

            char_literal24=(Token)match(input,145,FOLLOW_145_in_typeDeclaration963); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_145.add(char_literal24);

            // JFSL.g:422:9: ( typeBodyDeclaration )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==Identifier||LA6_0==140||(LA6_0>=143 && LA6_0<=144)||LA6_0==150||LA6_0==153) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // JFSL.g:0:0: typeBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_typeBodyDeclaration_in_typeDeclaration965);
            	    typeBodyDeclaration25=typeBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_typeBodyDeclaration.add(typeBodyDeclaration25.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            char_literal26=(Token)match(input,146,FOLLOW_146_in_typeDeclaration968); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_146.add(char_literal26);



            // AST REWRITE
            // elements: Identifier, typeParameters, typeBodyDeclaration
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 422:34: -> ^( TYPE_DECLARATION Identifier ^( TYPE_PARAMETERS ( typeParameters )? ) ( typeBodyDeclaration )* )
            {
                // JFSL.g:422:37: ^( TYPE_DECLARATION Identifier ^( TYPE_PARAMETERS ( typeParameters )? ) ( typeBodyDeclaration )* )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_DECLARATION, "TYPE_DECLARATION"), root_1);

                adaptor.addChild(root_1, stream_Identifier.nextNode());
                // JFSL.g:422:67: ^( TYPE_PARAMETERS ( typeParameters )? )
                {
                Node root_2 = (Node)adaptor.nil();
                root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_PARAMETERS, "TYPE_PARAMETERS"), root_2);

                // JFSL.g:422:85: ( typeParameters )?
                if ( stream_typeParameters.hasNext() ) {
                    adaptor.addChild(root_2, stream_typeParameters.nextTree());

                }
                stream_typeParameters.reset();

                adaptor.addChild(root_1, root_2);
                }
                // JFSL.g:422:102: ( typeBodyDeclaration )*
                while ( stream_typeBodyDeclaration.hasNext() ) {
                    adaptor.addChild(root_1, stream_typeBodyDeclaration.nextTree());

                }
                stream_typeBodyDeclaration.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 7, typeDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeDeclaration"

    public static class typeParameters_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeParameters"
    // JFSL.g:425:1: typeParameters : '<' Identifier ( ',' Identifier )* '>' -> ( ^( IDENTIFIER Identifier ) )+ ;
    public final JFSLParser.typeParameters_return typeParameters() throws RecognitionException {
        JFSLParser.typeParameters_return retval = new JFSLParser.typeParameters_return();
        retval.start = input.LT(1);
        int typeParameters_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal27=null;
        Token Identifier28=null;
        Token char_literal29=null;
        Token Identifier30=null;
        Token char_literal31=null;

        Node char_literal27_tree=null;
        Node Identifier28_tree=null;
        Node char_literal29_tree=null;
        Node Identifier30_tree=null;
        Node char_literal31_tree=null;
        RewriteRuleTokenStream stream_147=new RewriteRuleTokenStream(adaptor,"token 147");
        RewriteRuleTokenStream stream_148=new RewriteRuleTokenStream(adaptor,"token 148");
        RewriteRuleTokenStream stream_149=new RewriteRuleTokenStream(adaptor,"token 149");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // JFSL.g:426:3: ( '<' Identifier ( ',' Identifier )* '>' -> ( ^( IDENTIFIER Identifier ) )+ )
            // JFSL.g:426:5: '<' Identifier ( ',' Identifier )* '>'
            {
            char_literal27=(Token)match(input,147,FOLLOW_147_in_typeParameters999); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_147.add(char_literal27);

            Identifier28=(Token)match(input,Identifier,FOLLOW_Identifier_in_typeParameters1001); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Identifier.add(Identifier28);

            // JFSL.g:426:20: ( ',' Identifier )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==148) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // JFSL.g:426:21: ',' Identifier
            	    {
            	    char_literal29=(Token)match(input,148,FOLLOW_148_in_typeParameters1004); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_148.add(char_literal29);

            	    Identifier30=(Token)match(input,Identifier,FOLLOW_Identifier_in_typeParameters1006); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Identifier.add(Identifier30);


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            char_literal31=(Token)match(input,149,FOLLOW_149_in_typeParameters1010); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_149.add(char_literal31);



            // AST REWRITE
            // elements: Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 426:42: -> ( ^( IDENTIFIER Identifier ) )+
            {
                if ( !(stream_Identifier.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_Identifier.hasNext() ) {
                    // JFSL.g:426:45: ^( IDENTIFIER Identifier )
                    {
                    Node root_1 = (Node)adaptor.nil();
                    root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(IDENTIFIER, "IDENTIFIER"), root_1);

                    adaptor.addChild(root_1, stream_Identifier.nextNode());

                    adaptor.addChild(root_0, root_1);
                    }

                }
                stream_Identifier.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 8, typeParameters_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeParameters"

    public static class typeParameters2_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeParameters2"
    // JFSL.g:429:1: typeParameters2 : '<' primaryTypeIdentifier ( ',' primaryTypeIdentifier )* '>' -> ( primaryTypeIdentifier )+ ;
    public final JFSLParser.typeParameters2_return typeParameters2() throws RecognitionException {
        JFSLParser.typeParameters2_return retval = new JFSLParser.typeParameters2_return();
        retval.start = input.LT(1);
        int typeParameters2_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal32=null;
        Token char_literal34=null;
        Token char_literal36=null;
        JFSLParser.primaryTypeIdentifier_return primaryTypeIdentifier33 = null;

        JFSLParser.primaryTypeIdentifier_return primaryTypeIdentifier35 = null;


        Node char_literal32_tree=null;
        Node char_literal34_tree=null;
        Node char_literal36_tree=null;
        RewriteRuleTokenStream stream_147=new RewriteRuleTokenStream(adaptor,"token 147");
        RewriteRuleTokenStream stream_148=new RewriteRuleTokenStream(adaptor,"token 148");
        RewriteRuleTokenStream stream_149=new RewriteRuleTokenStream(adaptor,"token 149");
        RewriteRuleSubtreeStream stream_primaryTypeIdentifier=new RewriteRuleSubtreeStream(adaptor,"rule primaryTypeIdentifier");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // JFSL.g:430:3: ( '<' primaryTypeIdentifier ( ',' primaryTypeIdentifier )* '>' -> ( primaryTypeIdentifier )+ )
            // JFSL.g:430:5: '<' primaryTypeIdentifier ( ',' primaryTypeIdentifier )* '>'
            {
            char_literal32=(Token)match(input,147,FOLLOW_147_in_typeParameters21036); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_147.add(char_literal32);

            pushFollow(FOLLOW_primaryTypeIdentifier_in_typeParameters21038);
            primaryTypeIdentifier33=primaryTypeIdentifier();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primaryTypeIdentifier.add(primaryTypeIdentifier33.getTree());
            // JFSL.g:430:31: ( ',' primaryTypeIdentifier )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==148) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // JFSL.g:430:32: ',' primaryTypeIdentifier
            	    {
            	    char_literal34=(Token)match(input,148,FOLLOW_148_in_typeParameters21041); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_148.add(char_literal34);

            	    pushFollow(FOLLOW_primaryTypeIdentifier_in_typeParameters21043);
            	    primaryTypeIdentifier35=primaryTypeIdentifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_primaryTypeIdentifier.add(primaryTypeIdentifier35.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            char_literal36=(Token)match(input,149,FOLLOW_149_in_typeParameters21047); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_149.add(char_literal36);



            // AST REWRITE
            // elements: primaryTypeIdentifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 430:64: -> ( primaryTypeIdentifier )+
            {
                if ( !(stream_primaryTypeIdentifier.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_primaryTypeIdentifier.hasNext() ) {
                    adaptor.addChild(root_0, stream_primaryTypeIdentifier.nextTree());

                }
                stream_primaryTypeIdentifier.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 9, typeParameters2_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeParameters2"

    public static class typeBodyDeclaration_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeBodyDeclaration"
    // JFSL.g:433:1: typeBodyDeclaration : ( ';' | typeDeclaration | '@Invariant' '(' expression ')' -> ^( INVARIANT expression ) | '@SpecField' '(' declaration ')' -> ^( SPECFIELD declaration ) | Identifier '(' methodParameters ')' '{' specCase ( ';' specCase )* ( ';' )* '}' -> ^( METHOD Identifier methodParameters ^( SPECIFICATION ( specCase )+ ) ) );
    public final JFSLParser.typeBodyDeclaration_return typeBodyDeclaration() throws RecognitionException {
        JFSLParser.typeBodyDeclaration_return retval = new JFSLParser.typeBodyDeclaration_return();
        retval.start = input.LT(1);
        int typeBodyDeclaration_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal37=null;
        Token string_literal39=null;
        Token char_literal40=null;
        Token char_literal42=null;
        Token string_literal43=null;
        Token char_literal44=null;
        Token char_literal46=null;
        Token Identifier47=null;
        Token char_literal48=null;
        Token char_literal50=null;
        Token char_literal51=null;
        Token char_literal53=null;
        Token char_literal55=null;
        Token char_literal56=null;
        JFSLParser.typeDeclaration_return typeDeclaration38 = null;

        JFSLParser.expression_return expression41 = null;

        JFSLParser.declaration_return declaration45 = null;

        JFSLParser.methodParameters_return methodParameters49 = null;

        JFSLParser.specCase_return specCase52 = null;

        JFSLParser.specCase_return specCase54 = null;


        Node char_literal37_tree=null;
        Node string_literal39_tree=null;
        Node char_literal40_tree=null;
        Node char_literal42_tree=null;
        Node string_literal43_tree=null;
        Node char_literal44_tree=null;
        Node char_literal46_tree=null;
        Node Identifier47_tree=null;
        Node char_literal48_tree=null;
        Node char_literal50_tree=null;
        Node char_literal51_tree=null;
        Node char_literal53_tree=null;
        Node char_literal55_tree=null;
        Node char_literal56_tree=null;
        RewriteRuleTokenStream stream_152=new RewriteRuleTokenStream(adaptor,"token 152");
        RewriteRuleTokenStream stream_153=new RewriteRuleTokenStream(adaptor,"token 153");
        RewriteRuleTokenStream stream_145=new RewriteRuleTokenStream(adaptor,"token 145");
        RewriteRuleTokenStream stream_150=new RewriteRuleTokenStream(adaptor,"token 150");
        RewriteRuleTokenStream stream_146=new RewriteRuleTokenStream(adaptor,"token 146");
        RewriteRuleTokenStream stream_151=new RewriteRuleTokenStream(adaptor,"token 151");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_140=new RewriteRuleTokenStream(adaptor,"token 140");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_methodParameters=new RewriteRuleSubtreeStream(adaptor,"rule methodParameters");
        RewriteRuleSubtreeStream stream_specCase=new RewriteRuleSubtreeStream(adaptor,"rule specCase");
        RewriteRuleSubtreeStream stream_declaration=new RewriteRuleSubtreeStream(adaptor,"rule declaration");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // JFSL.g:434:3: ( ';' | typeDeclaration | '@Invariant' '(' expression ')' -> ^( INVARIANT expression ) | '@SpecField' '(' declaration ')' -> ^( SPECFIELD declaration ) | Identifier '(' methodParameters ')' '{' specCase ( ';' specCase )* ( ';' )* '}' -> ^( METHOD Identifier methodParameters ^( SPECIFICATION ( specCase )+ ) ) )
            int alt11=5;
            switch ( input.LA(1) ) {
            case 140:
                {
                alt11=1;
                }
                break;
            case 143:
            case 144:
                {
                alt11=2;
                }
                break;
            case 150:
                {
                alt11=3;
                }
                break;
            case 153:
                {
                alt11=4;
                }
                break;
            case Identifier:
                {
                alt11=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // JFSL.g:434:5: ';'
                    {
                    root_0 = (Node)adaptor.nil();

                    char_literal37=(Token)match(input,140,FOLLOW_140_in_typeBodyDeclaration1065); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JFSL.g:435:5: typeDeclaration
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_typeDeclaration_in_typeBodyDeclaration1072);
                    typeDeclaration38=typeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeDeclaration38.getTree());

                    }
                    break;
                case 3 :
                    // JFSL.g:436:5: '@Invariant' '(' expression ')'
                    {
                    string_literal39=(Token)match(input,150,FOLLOW_150_in_typeBodyDeclaration1079); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_150.add(string_literal39);

                    char_literal40=(Token)match(input,151,FOLLOW_151_in_typeBodyDeclaration1081); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_151.add(char_literal40);

                    pushFollow(FOLLOW_expression_in_typeBodyDeclaration1083);
                    expression41=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression41.getTree());
                    char_literal42=(Token)match(input,152,FOLLOW_152_in_typeBodyDeclaration1085); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_152.add(char_literal42);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 436:37: -> ^( INVARIANT expression )
                    {
                        // JFSL.g:436:40: ^( INVARIANT expression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(INVARIANT, "INVARIANT"), root_1);

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // JFSL.g:437:5: '@SpecField' '(' declaration ')'
                    {
                    string_literal43=(Token)match(input,153,FOLLOW_153_in_typeBodyDeclaration1099); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_153.add(string_literal43);

                    char_literal44=(Token)match(input,151,FOLLOW_151_in_typeBodyDeclaration1101); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_151.add(char_literal44);

                    pushFollow(FOLLOW_declaration_in_typeBodyDeclaration1103);
                    declaration45=declaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_declaration.add(declaration45.getTree());
                    char_literal46=(Token)match(input,152,FOLLOW_152_in_typeBodyDeclaration1105); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_152.add(char_literal46);



                    // AST REWRITE
                    // elements: declaration
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 437:38: -> ^( SPECFIELD declaration )
                    {
                        // JFSL.g:437:41: ^( SPECFIELD declaration )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(SPECFIELD, "SPECFIELD"), root_1);

                        adaptor.addChild(root_1, stream_declaration.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // JFSL.g:438:5: Identifier '(' methodParameters ')' '{' specCase ( ';' specCase )* ( ';' )* '}'
                    {
                    Identifier47=(Token)match(input,Identifier,FOLLOW_Identifier_in_typeBodyDeclaration1119); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier47);

                    char_literal48=(Token)match(input,151,FOLLOW_151_in_typeBodyDeclaration1121); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_151.add(char_literal48);

                    pushFollow(FOLLOW_methodParameters_in_typeBodyDeclaration1123);
                    methodParameters49=methodParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_methodParameters.add(methodParameters49.getTree());
                    char_literal50=(Token)match(input,152,FOLLOW_152_in_typeBodyDeclaration1125); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_152.add(char_literal50);

                    char_literal51=(Token)match(input,145,FOLLOW_145_in_typeBodyDeclaration1127); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_145.add(char_literal51);

                    pushFollow(FOLLOW_specCase_in_typeBodyDeclaration1129);
                    specCase52=specCase();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_specCase.add(specCase52.getTree());
                    // JFSL.g:438:54: ( ';' specCase )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==140) ) {
                            int LA9_1 = input.LA(2);

                            if ( (synpred13_JFSL()) ) {
                                alt9=1;
                            }


                        }


                        switch (alt9) {
                    	case 1 :
                    	    // JFSL.g:438:55: ';' specCase
                    	    {
                    	    char_literal53=(Token)match(input,140,FOLLOW_140_in_typeBodyDeclaration1132); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_140.add(char_literal53);

                    	    pushFollow(FOLLOW_specCase_in_typeBodyDeclaration1134);
                    	    specCase54=specCase();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_specCase.add(specCase54.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    // JFSL.g:438:70: ( ';' )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==140) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // JFSL.g:438:71: ';'
                    	    {
                    	    char_literal55=(Token)match(input,140,FOLLOW_140_in_typeBodyDeclaration1139); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_140.add(char_literal55);


                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);

                    char_literal56=(Token)match(input,146,FOLLOW_146_in_typeBodyDeclaration1143); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_146.add(char_literal56);



                    // AST REWRITE
                    // elements: specCase, methodParameters, Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 439:5: -> ^( METHOD Identifier methodParameters ^( SPECIFICATION ( specCase )+ ) )
                    {
                        // JFSL.g:439:8: ^( METHOD Identifier methodParameters ^( SPECIFICATION ( specCase )+ ) )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(METHOD, "METHOD"), root_1);

                        adaptor.addChild(root_1, stream_Identifier.nextNode());
                        adaptor.addChild(root_1, stream_methodParameters.nextTree());
                        // JFSL.g:439:45: ^( SPECIFICATION ( specCase )+ )
                        {
                        Node root_2 = (Node)adaptor.nil();
                        root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(SPECIFICATION, "SPECIFICATION"), root_2);

                        if ( !(stream_specCase.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_specCase.hasNext() ) {
                            adaptor.addChild(root_2, stream_specCase.nextTree());

                        }
                        stream_specCase.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 10, typeBodyDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeBodyDeclaration"

    public static class methodParameters_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "methodParameters"
    // JFSL.g:442:1: methodParameters : ( type Identifier ( ',' type Identifier )* )? -> ^( METHOD_PARAMETERS ( type )* ( Identifier )* ) ;
    public final JFSLParser.methodParameters_return methodParameters() throws RecognitionException {
        JFSLParser.methodParameters_return retval = new JFSLParser.methodParameters_return();
        retval.start = input.LT(1);
        int methodParameters_StartIndex = input.index();
        Node root_0 = null;

        Token Identifier58=null;
        Token char_literal59=null;
        Token Identifier61=null;
        JFSLParser.type_return type57 = null;

        JFSLParser.type_return type60 = null;


        Node Identifier58_tree=null;
        Node char_literal59_tree=null;
        Node Identifier61_tree=null;
        RewriteRuleTokenStream stream_148=new RewriteRuleTokenStream(adaptor,"token 148");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }
            // JFSL.g:443:3: ( ( type Identifier ( ',' type Identifier )* )? -> ^( METHOD_PARAMETERS ( type )* ( Identifier )* ) )
            // JFSL.g:443:5: ( type Identifier ( ',' type Identifier )* )?
            {
            // JFSL.g:443:5: ( type Identifier ( ',' type Identifier )* )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==Identifier||(LA13_0>=172 && LA13_0<=179)) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // JFSL.g:443:6: type Identifier ( ',' type Identifier )*
                    {
                    pushFollow(FOLLOW_type_in_methodParameters1181);
                    type57=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_type.add(type57.getTree());
                    Identifier58=(Token)match(input,Identifier,FOLLOW_Identifier_in_methodParameters1183); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier58);

                    // JFSL.g:443:22: ( ',' type Identifier )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==148) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // JFSL.g:443:23: ',' type Identifier
                    	    {
                    	    char_literal59=(Token)match(input,148,FOLLOW_148_in_methodParameters1186); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_148.add(char_literal59);

                    	    pushFollow(FOLLOW_type_in_methodParameters1188);
                    	    type60=type();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_type.add(type60.getTree());
                    	    Identifier61=(Token)match(input,Identifier,FOLLOW_Identifier_in_methodParameters1190); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_Identifier.add(Identifier61);


                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);


                    }
                    break;

            }



            // AST REWRITE
            // elements: Identifier, type
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 443:47: -> ^( METHOD_PARAMETERS ( type )* ( Identifier )* )
            {
                // JFSL.g:443:50: ^( METHOD_PARAMETERS ( type )* ( Identifier )* )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(METHOD_PARAMETERS, "METHOD_PARAMETERS"), root_1);

                // JFSL.g:443:70: ( type )*
                while ( stream_type.hasNext() ) {
                    adaptor.addChild(root_1, stream_type.nextTree());

                }
                stream_type.reset();
                // JFSL.g:443:76: ( Identifier )*
                while ( stream_Identifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_Identifier.nextNode());

                }
                stream_Identifier.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 11, methodParameters_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "methodParameters"

    public static class specCase_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "specCase"
    // JFSL.g:446:1: specCase : ( ( '@Requires' '(' requires= expression ')' )? ( '@Ensures' '(' ensures= expression ')' | ( '@Throws' '(' except= expression ')' ) )? ( '@Modifies' '(' changes= frame ')' )? -> ^( SPECCASE ^( REQUIRES ( $requires)? ) ^( ENSURES ( $ensures)? ) ^( THROWS ( $except)? ) ^( MODIFIES ( $changes)? ) ) | '@Helper' -> ^( HELPER ) | '@Pure' -> ^( PURE ) );
    public final JFSLParser.specCase_return specCase() throws RecognitionException {
        JFSLParser.specCase_return retval = new JFSLParser.specCase_return();
        retval.start = input.LT(1);
        int specCase_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal62=null;
        Token char_literal63=null;
        Token char_literal64=null;
        Token string_literal65=null;
        Token char_literal66=null;
        Token char_literal67=null;
        Token string_literal68=null;
        Token char_literal69=null;
        Token char_literal70=null;
        Token string_literal71=null;
        Token char_literal72=null;
        Token char_literal73=null;
        Token string_literal74=null;
        Token string_literal75=null;
        JFSLParser.expression_return requires = null;

        JFSLParser.expression_return ensures = null;

        JFSLParser.expression_return except = null;

        JFSLParser.frame_return changes = null;


        Node string_literal62_tree=null;
        Node char_literal63_tree=null;
        Node char_literal64_tree=null;
        Node string_literal65_tree=null;
        Node char_literal66_tree=null;
        Node char_literal67_tree=null;
        Node string_literal68_tree=null;
        Node char_literal69_tree=null;
        Node char_literal70_tree=null;
        Node string_literal71_tree=null;
        Node char_literal72_tree=null;
        Node char_literal73_tree=null;
        Node string_literal74_tree=null;
        Node string_literal75_tree=null;
        RewriteRuleTokenStream stream_152=new RewriteRuleTokenStream(adaptor,"token 152");
        RewriteRuleTokenStream stream_151=new RewriteRuleTokenStream(adaptor,"token 151");
        RewriteRuleTokenStream stream_159=new RewriteRuleTokenStream(adaptor,"token 159");
        RewriteRuleTokenStream stream_158=new RewriteRuleTokenStream(adaptor,"token 158");
        RewriteRuleTokenStream stream_157=new RewriteRuleTokenStream(adaptor,"token 157");
        RewriteRuleTokenStream stream_156=new RewriteRuleTokenStream(adaptor,"token 156");
        RewriteRuleTokenStream stream_155=new RewriteRuleTokenStream(adaptor,"token 155");
        RewriteRuleTokenStream stream_154=new RewriteRuleTokenStream(adaptor,"token 154");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_frame=new RewriteRuleSubtreeStream(adaptor,"rule frame");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }
            // JFSL.g:447:3: ( ( '@Requires' '(' requires= expression ')' )? ( '@Ensures' '(' ensures= expression ')' | ( '@Throws' '(' except= expression ')' ) )? ( '@Modifies' '(' changes= frame ')' )? -> ^( SPECCASE ^( REQUIRES ( $requires)? ) ^( ENSURES ( $ensures)? ) ^( THROWS ( $except)? ) ^( MODIFIES ( $changes)? ) ) | '@Helper' -> ^( HELPER ) | '@Pure' -> ^( PURE ) )
            int alt17=3;
            switch ( input.LA(1) ) {
            case EOF:
            case 140:
            case 146:
            case 154:
            case 155:
            case 156:
            case 157:
                {
                alt17=1;
                }
                break;
            case 158:
                {
                alt17=2;
                }
                break;
            case 159:
                {
                alt17=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // JFSL.g:447:5: ( '@Requires' '(' requires= expression ')' )? ( '@Ensures' '(' ensures= expression ')' | ( '@Throws' '(' except= expression ')' ) )? ( '@Modifies' '(' changes= frame ')' )?
                    {
                    // JFSL.g:447:5: ( '@Requires' '(' requires= expression ')' )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==154) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // JFSL.g:447:6: '@Requires' '(' requires= expression ')'
                            {
                            string_literal62=(Token)match(input,154,FOLLOW_154_in_specCase1226); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_154.add(string_literal62);

                            char_literal63=(Token)match(input,151,FOLLOW_151_in_specCase1228); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_151.add(char_literal63);

                            pushFollow(FOLLOW_expression_in_specCase1234);
                            requires=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_expression.add(requires.getTree());
                            char_literal64=(Token)match(input,152,FOLLOW_152_in_specCase1236); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_152.add(char_literal64);


                            }
                            break;

                    }

                    // JFSL.g:448:5: ( '@Ensures' '(' ensures= expression ')' | ( '@Throws' '(' except= expression ')' ) )?
                    int alt15=3;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==155) ) {
                        alt15=1;
                    }
                    else if ( (LA15_0==156) ) {
                        alt15=2;
                    }
                    switch (alt15) {
                        case 1 :
                            // JFSL.g:448:6: '@Ensures' '(' ensures= expression ')'
                            {
                            string_literal65=(Token)match(input,155,FOLLOW_155_in_specCase1247); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_155.add(string_literal65);

                            char_literal66=(Token)match(input,151,FOLLOW_151_in_specCase1249); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_151.add(char_literal66);

                            pushFollow(FOLLOW_expression_in_specCase1255);
                            ensures=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_expression.add(ensures.getTree());
                            char_literal67=(Token)match(input,152,FOLLOW_152_in_specCase1257); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_152.add(char_literal67);


                            }
                            break;
                        case 2 :
                            // JFSL.g:448:48: ( '@Throws' '(' except= expression ')' )
                            {
                            // JFSL.g:448:48: ( '@Throws' '(' except= expression ')' )
                            // JFSL.g:448:49: '@Throws' '(' except= expression ')'
                            {
                            string_literal68=(Token)match(input,156,FOLLOW_156_in_specCase1262); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_156.add(string_literal68);

                            char_literal69=(Token)match(input,151,FOLLOW_151_in_specCase1264); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_151.add(char_literal69);

                            pushFollow(FOLLOW_expression_in_specCase1270);
                            except=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_expression.add(except.getTree());
                            char_literal70=(Token)match(input,152,FOLLOW_152_in_specCase1272); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_152.add(char_literal70);


                            }


                            }
                            break;

                    }

                    // JFSL.g:449:5: ( '@Modifies' '(' changes= frame ')' )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==157) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // JFSL.g:449:6: '@Modifies' '(' changes= frame ')'
                            {
                            string_literal71=(Token)match(input,157,FOLLOW_157_in_specCase1283); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_157.add(string_literal71);

                            char_literal72=(Token)match(input,151,FOLLOW_151_in_specCase1285); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_151.add(char_literal72);

                            pushFollow(FOLLOW_frame_in_specCase1291);
                            changes=frame();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_frame.add(changes.getTree());
                            char_literal73=(Token)match(input,152,FOLLOW_152_in_specCase1293); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_152.add(char_literal73);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: requires, ensures, changes, except
                    // token labels: 
                    // rule labels: ensures, retval, except, requires, changes
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_ensures=new RewriteRuleSubtreeStream(adaptor,"rule ensures",ensures!=null?ensures.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_except=new RewriteRuleSubtreeStream(adaptor,"rule except",except!=null?except.tree:null);
                    RewriteRuleSubtreeStream stream_requires=new RewriteRuleSubtreeStream(adaptor,"rule requires",requires!=null?requires.tree:null);
                    RewriteRuleSubtreeStream stream_changes=new RewriteRuleSubtreeStream(adaptor,"rule changes",changes!=null?changes.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 450:5: -> ^( SPECCASE ^( REQUIRES ( $requires)? ) ^( ENSURES ( $ensures)? ) ^( THROWS ( $except)? ) ^( MODIFIES ( $changes)? ) )
                    {
                        // JFSL.g:450:8: ^( SPECCASE ^( REQUIRES ( $requires)? ) ^( ENSURES ( $ensures)? ) ^( THROWS ( $except)? ) ^( MODIFIES ( $changes)? ) )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(SPECCASE, "SPECCASE"), root_1);

                        // JFSL.g:450:19: ^( REQUIRES ( $requires)? )
                        {
                        Node root_2 = (Node)adaptor.nil();
                        root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(REQUIRES, "REQUIRES"), root_2);

                        // JFSL.g:450:30: ( $requires)?
                        if ( stream_requires.hasNext() ) {
                            adaptor.addChild(root_2, stream_requires.nextTree());

                        }
                        stream_requires.reset();

                        adaptor.addChild(root_1, root_2);
                        }
                        // JFSL.g:450:42: ^( ENSURES ( $ensures)? )
                        {
                        Node root_2 = (Node)adaptor.nil();
                        root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(ENSURES, "ENSURES"), root_2);

                        // JFSL.g:450:52: ( $ensures)?
                        if ( stream_ensures.hasNext() ) {
                            adaptor.addChild(root_2, stream_ensures.nextTree());

                        }
                        stream_ensures.reset();

                        adaptor.addChild(root_1, root_2);
                        }
                        // JFSL.g:450:63: ^( THROWS ( $except)? )
                        {
                        Node root_2 = (Node)adaptor.nil();
                        root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(THROWS, "THROWS"), root_2);

                        // JFSL.g:450:72: ( $except)?
                        if ( stream_except.hasNext() ) {
                            adaptor.addChild(root_2, stream_except.nextTree());

                        }
                        stream_except.reset();

                        adaptor.addChild(root_1, root_2);
                        }
                        // JFSL.g:450:82: ^( MODIFIES ( $changes)? )
                        {
                        Node root_2 = (Node)adaptor.nil();
                        root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(MODIFIES, "MODIFIES"), root_2);

                        // JFSL.g:450:93: ( $changes)?
                        if ( stream_changes.hasNext() ) {
                            adaptor.addChild(root_2, stream_changes.nextTree());

                        }
                        stream_changes.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:451:5: '@Helper'
                    {
                    string_literal74=(Token)match(input,158,FOLLOW_158_in_specCase1344); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_158.add(string_literal74);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 451:15: -> ^( HELPER )
                    {
                        // JFSL.g:451:18: ^( HELPER )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(HELPER, "HELPER"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:452:5: '@Pure'
                    {
                    string_literal75=(Token)match(input,159,FOLLOW_159_in_specCase1359); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_159.add(string_literal75);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 452:13: -> ^( PURE )
                    {
                        // JFSL.g:452:16: ^( PURE )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(PURE, "PURE"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 12, specCase_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "specCase"

    public static class declaration_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "declaration"
    // JFSL.g:457:1: declaration : ( ( 'public' | 'private' )? Identifier ':' declarationMult additiveExpression 'from' frame ( '|' expression )? -> ^( DECLARATION ^( IDENTIFIER Identifier ) declarationMult additiveExpression frame ( expression )? ) | ( 'public' | 'private' )? Identifier ':' declarationMult additiveExpression ( '|' expression )? -> ^( DECLARATION ^( IDENTIFIER Identifier ) declarationMult additiveExpression NULL ( expression )? ) );
    public final JFSLParser.declaration_return declaration() throws RecognitionException {
        JFSLParser.declaration_return retval = new JFSLParser.declaration_return();
        retval.start = input.LT(1);
        int declaration_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal76=null;
        Token string_literal77=null;
        Token Identifier78=null;
        Token char_literal79=null;
        Token string_literal82=null;
        Token char_literal84=null;
        Token string_literal86=null;
        Token string_literal87=null;
        Token Identifier88=null;
        Token char_literal89=null;
        Token char_literal92=null;
        JFSLParser.declarationMult_return declarationMult80 = null;

        JFSLParser.additiveExpression_return additiveExpression81 = null;

        JFSLParser.frame_return frame83 = null;

        JFSLParser.expression_return expression85 = null;

        JFSLParser.declarationMult_return declarationMult90 = null;

        JFSLParser.additiveExpression_return additiveExpression91 = null;

        JFSLParser.expression_return expression93 = null;


        Node string_literal76_tree=null;
        Node string_literal77_tree=null;
        Node Identifier78_tree=null;
        Node char_literal79_tree=null;
        Node string_literal82_tree=null;
        Node char_literal84_tree=null;
        Node string_literal86_tree=null;
        Node string_literal87_tree=null;
        Node Identifier88_tree=null;
        Node char_literal89_tree=null;
        Node char_literal92_tree=null;
        RewriteRuleTokenStream stream_161=new RewriteRuleTokenStream(adaptor,"token 161");
        RewriteRuleTokenStream stream_162=new RewriteRuleTokenStream(adaptor,"token 162");
        RewriteRuleTokenStream stream_163=new RewriteRuleTokenStream(adaptor,"token 163");
        RewriteRuleTokenStream stream_164=new RewriteRuleTokenStream(adaptor,"token 164");
        RewriteRuleTokenStream stream_160=new RewriteRuleTokenStream(adaptor,"token 160");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_frame=new RewriteRuleSubtreeStream(adaptor,"rule frame");
        RewriteRuleSubtreeStream stream_additiveExpression=new RewriteRuleSubtreeStream(adaptor,"rule additiveExpression");
        RewriteRuleSubtreeStream stream_declarationMult=new RewriteRuleSubtreeStream(adaptor,"rule declarationMult");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }
            // JFSL.g:458:3: ( ( 'public' | 'private' )? Identifier ':' declarationMult additiveExpression 'from' frame ( '|' expression )? -> ^( DECLARATION ^( IDENTIFIER Identifier ) declarationMult additiveExpression frame ( expression )? ) | ( 'public' | 'private' )? Identifier ':' declarationMult additiveExpression ( '|' expression )? -> ^( DECLARATION ^( IDENTIFIER Identifier ) declarationMult additiveExpression NULL ( expression )? ) )
            int alt22=2;
            switch ( input.LA(1) ) {
            case 160:
                {
                int LA22_1 = input.LA(2);

                if ( (synpred26_JFSL()) ) {
                    alt22=1;
                }
                else if ( (true) ) {
                    alt22=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 1, input);

                    throw nvae;
                }
                }
                break;
            case 161:
                {
                int LA22_2 = input.LA(2);

                if ( (synpred26_JFSL()) ) {
                    alt22=1;
                }
                else if ( (true) ) {
                    alt22=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 2, input);

                    throw nvae;
                }
                }
                break;
            case Identifier:
                {
                int LA22_3 = input.LA(2);

                if ( (synpred26_JFSL()) ) {
                    alt22=1;
                }
                else if ( (true) ) {
                    alt22=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // JFSL.g:458:5: ( 'public' | 'private' )? Identifier ':' declarationMult additiveExpression 'from' frame ( '|' expression )?
                    {
                    // JFSL.g:458:5: ( 'public' | 'private' )?
                    int alt18=3;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0==160) ) {
                        alt18=1;
                    }
                    else if ( (LA18_0==161) ) {
                        alt18=2;
                    }
                    switch (alt18) {
                        case 1 :
                            // JFSL.g:458:6: 'public'
                            {
                            string_literal76=(Token)match(input,160,FOLLOW_160_in_declaration1383); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_160.add(string_literal76);


                            }
                            break;
                        case 2 :
                            // JFSL.g:458:17: 'private'
                            {
                            string_literal77=(Token)match(input,161,FOLLOW_161_in_declaration1387); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_161.add(string_literal77);


                            }
                            break;

                    }

                    Identifier78=(Token)match(input,Identifier,FOLLOW_Identifier_in_declaration1391); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier78);

                    char_literal79=(Token)match(input,162,FOLLOW_162_in_declaration1393); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_162.add(char_literal79);

                    pushFollow(FOLLOW_declarationMult_in_declaration1395);
                    declarationMult80=declarationMult();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_declarationMult.add(declarationMult80.getTree());
                    pushFollow(FOLLOW_additiveExpression_in_declaration1397);
                    additiveExpression81=additiveExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_additiveExpression.add(additiveExpression81.getTree());
                    string_literal82=(Token)match(input,163,FOLLOW_163_in_declaration1399); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_163.add(string_literal82);

                    pushFollow(FOLLOW_frame_in_declaration1401);
                    frame83=frame();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_frame.add(frame83.getTree());
                    // JFSL.g:458:92: ( '|' expression )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0==164) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // JFSL.g:458:93: '|' expression
                            {
                            char_literal84=(Token)match(input,164,FOLLOW_164_in_declaration1404); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_164.add(char_literal84);

                            pushFollow(FOLLOW_expression_in_declaration1406);
                            expression85=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_expression.add(expression85.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: expression, frame, additiveExpression, declarationMult, Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 459:3: -> ^( DECLARATION ^( IDENTIFIER Identifier ) declarationMult additiveExpression frame ( expression )? )
                    {
                        // JFSL.g:459:6: ^( DECLARATION ^( IDENTIFIER Identifier ) declarationMult additiveExpression frame ( expression )? )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(DECLARATION, "DECLARATION"), root_1);

                        // JFSL.g:459:20: ^( IDENTIFIER Identifier )
                        {
                        Node root_2 = (Node)adaptor.nil();
                        root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(IDENTIFIER, "IDENTIFIER"), root_2);

                        adaptor.addChild(root_2, stream_Identifier.nextNode());

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_declarationMult.nextTree());
                        adaptor.addChild(root_1, stream_additiveExpression.nextTree());
                        adaptor.addChild(root_1, stream_frame.nextTree());
                        // JFSL.g:459:86: ( expression )?
                        if ( stream_expression.hasNext() ) {
                            adaptor.addChild(root_1, stream_expression.nextTree());

                        }
                        stream_expression.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:460:5: ( 'public' | 'private' )? Identifier ':' declarationMult additiveExpression ( '|' expression )?
                    {
                    // JFSL.g:460:5: ( 'public' | 'private' )?
                    int alt20=3;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==160) ) {
                        alt20=1;
                    }
                    else if ( (LA20_0==161) ) {
                        alt20=2;
                    }
                    switch (alt20) {
                        case 1 :
                            // JFSL.g:460:6: 'public'
                            {
                            string_literal86=(Token)match(input,160,FOLLOW_160_in_declaration1438); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_160.add(string_literal86);


                            }
                            break;
                        case 2 :
                            // JFSL.g:460:17: 'private'
                            {
                            string_literal87=(Token)match(input,161,FOLLOW_161_in_declaration1442); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_161.add(string_literal87);


                            }
                            break;

                    }

                    Identifier88=(Token)match(input,Identifier,FOLLOW_Identifier_in_declaration1446); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier88);

                    char_literal89=(Token)match(input,162,FOLLOW_162_in_declaration1448); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_162.add(char_literal89);

                    pushFollow(FOLLOW_declarationMult_in_declaration1450);
                    declarationMult90=declarationMult();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_declarationMult.add(declarationMult90.getTree());
                    pushFollow(FOLLOW_additiveExpression_in_declaration1452);
                    additiveExpression91=additiveExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_additiveExpression.add(additiveExpression91.getTree());
                    // JFSL.g:460:79: ( '|' expression )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==164) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // JFSL.g:460:80: '|' expression
                            {
                            char_literal92=(Token)match(input,164,FOLLOW_164_in_declaration1455); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_164.add(char_literal92);

                            pushFollow(FOLLOW_expression_in_declaration1457);
                            expression93=expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_expression.add(expression93.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: declarationMult, Identifier, expression, additiveExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 461:3: -> ^( DECLARATION ^( IDENTIFIER Identifier ) declarationMult additiveExpression NULL ( expression )? )
                    {
                        // JFSL.g:461:6: ^( DECLARATION ^( IDENTIFIER Identifier ) declarationMult additiveExpression NULL ( expression )? )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(DECLARATION, "DECLARATION"), root_1);

                        // JFSL.g:461:20: ^( IDENTIFIER Identifier )
                        {
                        Node root_2 = (Node)adaptor.nil();
                        root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(IDENTIFIER, "IDENTIFIER"), root_2);

                        adaptor.addChild(root_2, stream_Identifier.nextNode());

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_declarationMult.nextTree());
                        adaptor.addChild(root_1, stream_additiveExpression.nextTree());
                        adaptor.addChild(root_1, (Node)adaptor.create(NULL, "NULL"));
                        // JFSL.g:461:85: ( expression )?
                        if ( stream_expression.hasNext() ) {
                            adaptor.addChild(root_1, stream_expression.nextTree());

                        }
                        stream_expression.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 13, declaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "declaration"

    public static class quantIdMod_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "quantIdMod"
    // JFSL.g:464:1: quantIdMod : (x= 'disj' -> ^( MOD_DISJ $x) | -> MOD_NONE );
    public final JFSLParser.quantIdMod_return quantIdMod() throws RecognitionException {
        JFSLParser.quantIdMod_return retval = new JFSLParser.quantIdMod_return();
        retval.start = input.LT(1);
        int quantIdMod_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;

        Node x_tree=null;
        RewriteRuleTokenStream stream_165=new RewriteRuleTokenStream(adaptor,"token 165");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }
            // JFSL.g:465:3: (x= 'disj' -> ^( MOD_DISJ $x) | -> MOD_NONE )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==165) ) {
                alt23=1;
            }
            else if ( (LA23_0==Identifier) ) {
                alt23=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // JFSL.g:465:5: x= 'disj'
                    {
                    x=(Token)match(input,165,FOLLOW_165_in_quantIdMod1499); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_165.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 465:14: -> ^( MOD_DISJ $x)
                    {
                        // JFSL.g:465:17: ^( MOD_DISJ $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(MOD_DISJ, "MOD_DISJ"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:466:5: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 466:5: -> MOD_NONE
                    {
                        adaptor.addChild(root_0, (Node)adaptor.create(MOD_NONE, "MOD_NONE"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 14, quantIdMod_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "quantIdMod"

    public static class declarationMult_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "declarationMult"
    // JFSL.g:469:1: declarationMult : ( setDeclOp | x= 'set' -> ^( DECL_SET $x) | x= 'seq' -> ^( DECL_SEQ $x) | -> DECL_NONE );
    public final JFSLParser.declarationMult_return declarationMult() throws RecognitionException {
        JFSLParser.declarationMult_return retval = new JFSLParser.declarationMult_return();
        retval.start = input.LT(1);
        int declarationMult_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;
        JFSLParser.setDeclOp_return setDeclOp94 = null;


        Node x_tree=null;
        RewriteRuleTokenStream stream_166=new RewriteRuleTokenStream(adaptor,"token 166");
        RewriteRuleTokenStream stream_167=new RewriteRuleTokenStream(adaptor,"token 167");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }
            // JFSL.g:470:3: ( setDeclOp | x= 'set' -> ^( DECL_SET $x) | x= 'seq' -> ^( DECL_SEQ $x) | -> DECL_NONE )
            int alt24=4;
            switch ( input.LA(1) ) {
            case 211:
            case 212:
            case 213:
                {
                alt24=1;
                }
                break;
            case 166:
                {
                alt24=2;
                }
                break;
            case 167:
                {
                alt24=3;
                }
                break;
            case Identifier:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 145:
            case 151:
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 184:
            case 190:
            case 195:
            case 196:
            case 197:
            case 200:
            case 203:
            case 204:
            case 205:
            case 206:
            case 207:
            case 208:
            case 209:
            case 210:
                {
                alt24=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // JFSL.g:470:5: setDeclOp
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_setDeclOp_in_declarationMult1531);
                    setDeclOp94=setDeclOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, setDeclOp94.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:471:5: x= 'set'
                    {
                    x=(Token)match(input,166,FOLLOW_166_in_declarationMult1539); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_166.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 471:13: -> ^( DECL_SET $x)
                    {
                        // JFSL.g:471:16: ^( DECL_SET $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(DECL_SET, "DECL_SET"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:472:5: x= 'seq'
                    {
                    x=(Token)match(input,167,FOLLOW_167_in_declarationMult1559); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_167.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 472:13: -> ^( DECL_SEQ $x)
                    {
                        // JFSL.g:472:16: ^( DECL_SEQ $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(DECL_SEQ, "DECL_SEQ"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // JFSL.g:473:5: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 473:5: -> DECL_NONE
                    {
                        adaptor.addChild(root_0, (Node)adaptor.create(DECL_NONE, "DECL_NONE"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 15, declarationMult_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "declarationMult"

    public static class frame_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "frame"
    // JFSL.g:476:1: frame : ( | storeRef ( ',' storeRef )* ) -> ^( FRAME ( storeRef )* ) ;
    public final JFSLParser.frame_return frame() throws RecognitionException {
        JFSLParser.frame_return retval = new JFSLParser.frame_return();
        retval.start = input.LT(1);
        int frame_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal96=null;
        JFSLParser.storeRef_return storeRef95 = null;

        JFSLParser.storeRef_return storeRef97 = null;


        Node char_literal96_tree=null;
        RewriteRuleTokenStream stream_148=new RewriteRuleTokenStream(adaptor,"token 148");
        RewriteRuleSubtreeStream stream_storeRef=new RewriteRuleSubtreeStream(adaptor,"rule storeRef");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }
            // JFSL.g:477:3: ( ( | storeRef ( ',' storeRef )* ) -> ^( FRAME ( storeRef )* ) )
            // JFSL.g:477:5: ( | storeRef ( ',' storeRef )* )
            {
            // JFSL.g:477:5: ( | storeRef ( ',' storeRef )* )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==EOF||LA26_0==152||LA26_0==164) ) {
                alt26=1;
            }
            else if ( (LA26_0==Identifier||LA26_0==145||LA26_0==151||LA26_0==190||LA26_0==200||(LA26_0>=203 && LA26_0<=210)) ) {
                alt26=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // JFSL.g:477:7: 
                    {
                    }
                    break;
                case 2 :
                    // JFSL.g:477:9: storeRef ( ',' storeRef )*
                    {
                    pushFollow(FOLLOW_storeRef_in_frame1595);
                    storeRef95=storeRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_storeRef.add(storeRef95.getTree());
                    // JFSL.g:477:18: ( ',' storeRef )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0==148) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // JFSL.g:477:19: ',' storeRef
                    	    {
                    	    char_literal96=(Token)match(input,148,FOLLOW_148_in_frame1598); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_148.add(char_literal96);

                    	    pushFollow(FOLLOW_storeRef_in_frame1600);
                    	    storeRef97=storeRef();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_storeRef.add(storeRef97.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);


                    }
                    break;

            }



            // AST REWRITE
            // elements: storeRef
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 477:35: -> ^( FRAME ( storeRef )* )
            {
                // JFSL.g:477:38: ^( FRAME ( storeRef )* )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(FRAME, "FRAME"), root_1);

                // JFSL.g:477:46: ( storeRef )*
                while ( stream_storeRef.hasNext() ) {
                    adaptor.addChild(root_1, stream_storeRef.nextTree());

                }
                stream_storeRef.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 16, frame_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "frame"

    public static class storeRef_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "storeRef"
    // JFSL.g:480:1: storeRef : storePrimary storeSelectors ( '|' ff= expression )? ( 'from' fd= expression )? -> ^( FRAME_LOCATION ^( FRAME_FIELD storePrimary storeSelectors ) ( ^( FRAME_FILTER $ff) )? ( ^( FRAME_DOMAIN $fd) )? ) ;
    public final JFSLParser.storeRef_return storeRef() throws RecognitionException {
        JFSLParser.storeRef_return retval = new JFSLParser.storeRef_return();
        retval.start = input.LT(1);
        int storeRef_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal100=null;
        Token string_literal101=null;
        JFSLParser.expression_return ff = null;

        JFSLParser.expression_return fd = null;

        JFSLParser.storePrimary_return storePrimary98 = null;

        JFSLParser.storeSelectors_return storeSelectors99 = null;


        Node char_literal100_tree=null;
        Node string_literal101_tree=null;
        RewriteRuleTokenStream stream_163=new RewriteRuleTokenStream(adaptor,"token 163");
        RewriteRuleTokenStream stream_164=new RewriteRuleTokenStream(adaptor,"token 164");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_storeSelectors=new RewriteRuleSubtreeStream(adaptor,"rule storeSelectors");
        RewriteRuleSubtreeStream stream_storePrimary=new RewriteRuleSubtreeStream(adaptor,"rule storePrimary");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }
            // JFSL.g:481:3: ( storePrimary storeSelectors ( '|' ff= expression )? ( 'from' fd= expression )? -> ^( FRAME_LOCATION ^( FRAME_FIELD storePrimary storeSelectors ) ( ^( FRAME_FILTER $ff) )? ( ^( FRAME_DOMAIN $fd) )? ) )
            // JFSL.g:481:5: storePrimary storeSelectors ( '|' ff= expression )? ( 'from' fd= expression )?
            {
            pushFollow(FOLLOW_storePrimary_in_storeRef1625);
            storePrimary98=storePrimary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_storePrimary.add(storePrimary98.getTree());
            pushFollow(FOLLOW_storeSelectors_in_storeRef1627);
            storeSelectors99=storeSelectors();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_storeSelectors.add(storeSelectors99.getTree());
            // JFSL.g:481:33: ( '|' ff= expression )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==164) ) {
                int LA27_1 = input.LA(2);

                if ( (synpred36_JFSL()) ) {
                    alt27=1;
                }
            }
            switch (alt27) {
                case 1 :
                    // JFSL.g:481:34: '|' ff= expression
                    {
                    char_literal100=(Token)match(input,164,FOLLOW_164_in_storeRef1630); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_164.add(char_literal100);

                    pushFollow(FOLLOW_expression_in_storeRef1634);
                    ff=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(ff.getTree());

                    }
                    break;

            }

            // JFSL.g:481:54: ( 'from' fd= expression )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==163) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // JFSL.g:481:55: 'from' fd= expression
                    {
                    string_literal101=(Token)match(input,163,FOLLOW_163_in_storeRef1639); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_163.add(string_literal101);

                    pushFollow(FOLLOW_expression_in_storeRef1643);
                    fd=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(fd.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: fd, storePrimary, storeSelectors, ff
            // token labels: 
            // rule labels: retval, ff, fd
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_ff=new RewriteRuleSubtreeStream(adaptor,"rule ff",ff!=null?ff.tree:null);
            RewriteRuleSubtreeStream stream_fd=new RewriteRuleSubtreeStream(adaptor,"rule fd",fd!=null?fd.tree:null);

            root_0 = (Node)adaptor.nil();
            // 482:6: -> ^( FRAME_LOCATION ^( FRAME_FIELD storePrimary storeSelectors ) ( ^( FRAME_FILTER $ff) )? ( ^( FRAME_DOMAIN $fd) )? )
            {
                // JFSL.g:482:9: ^( FRAME_LOCATION ^( FRAME_FIELD storePrimary storeSelectors ) ( ^( FRAME_FILTER $ff) )? ( ^( FRAME_DOMAIN $fd) )? )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(FRAME_LOCATION, "FRAME_LOCATION"), root_1);

                // JFSL.g:482:26: ^( FRAME_FIELD storePrimary storeSelectors )
                {
                Node root_2 = (Node)adaptor.nil();
                root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(FRAME_FIELD, "FRAME_FIELD"), root_2);

                adaptor.addChild(root_2, stream_storePrimary.nextTree());
                adaptor.addChild(root_2, stream_storeSelectors.nextTree());

                adaptor.addChild(root_1, root_2);
                }
                // JFSL.g:482:69: ( ^( FRAME_FILTER $ff) )?
                if ( stream_ff.hasNext() ) {
                    // JFSL.g:482:69: ^( FRAME_FILTER $ff)
                    {
                    Node root_2 = (Node)adaptor.nil();
                    root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(FRAME_FILTER, "FRAME_FILTER"), root_2);

                    adaptor.addChild(root_2, stream_ff.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_ff.reset();
                // JFSL.g:482:90: ( ^( FRAME_DOMAIN $fd) )?
                if ( stream_fd.hasNext() ) {
                    // JFSL.g:482:90: ^( FRAME_DOMAIN $fd)
                    {
                    Node root_2 = (Node)adaptor.nil();
                    root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(FRAME_DOMAIN, "FRAME_DOMAIN"), root_2);

                    adaptor.addChild(root_2, stream_fd.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_fd.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 17, storeRef_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "storeRef"

    public static class storePrimary_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "storePrimary"
    // JFSL.g:485:1: storePrimary : ( Identifier -> ^( IDENTIFIER Identifier ) | common );
    public final JFSLParser.storePrimary_return storePrimary() throws RecognitionException {
        JFSLParser.storePrimary_return retval = new JFSLParser.storePrimary_return();
        retval.start = input.LT(1);
        int storePrimary_StartIndex = input.index();
        Node root_0 = null;

        Token Identifier102=null;
        JFSLParser.common_return common103 = null;


        Node Identifier102_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }
            // JFSL.g:486:3: ( Identifier -> ^( IDENTIFIER Identifier ) | common )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==Identifier) ) {
                alt29=1;
            }
            else if ( (LA29_0==145||LA29_0==151||LA29_0==190||LA29_0==200||(LA29_0>=203 && LA29_0<=210)) ) {
                alt29=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // JFSL.g:486:5: Identifier
                    {
                    Identifier102=(Token)match(input,Identifier,FOLLOW_Identifier_in_storePrimary1696); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier102);



                    // AST REWRITE
                    // elements: Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 486:16: -> ^( IDENTIFIER Identifier )
                    {
                        // JFSL.g:486:19: ^( IDENTIFIER Identifier )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(IDENTIFIER, "IDENTIFIER"), root_1);

                        adaptor.addChild(root_1, stream_Identifier.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:487:5: common
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_common_in_storePrimary1711);
                    common103=common();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, common103.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 18, storePrimary_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "storePrimary"

    public static class storeSelectors_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "storeSelectors"
    // JFSL.g:490:1: storeSelectors : ( ( selector )* storeWildCard | ( selector )+ );
    public final JFSLParser.storeSelectors_return storeSelectors() throws RecognitionException {
        JFSLParser.storeSelectors_return retval = new JFSLParser.storeSelectors_return();
        retval.start = input.LT(1);
        int storeSelectors_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.selector_return selector104 = null;

        JFSLParser.storeWildCard_return storeWildCard105 = null;

        JFSLParser.selector_return selector106 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }
            // JFSL.g:491:3: ( ( selector )* storeWildCard | ( selector )+ )
            int alt32=2;
            switch ( input.LA(1) ) {
            case 141:
                {
                int LA32_1 = input.LA(2);

                if ( (synpred40_JFSL()) ) {
                    alt32=1;
                }
                else if ( (true) ) {
                    alt32=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 1, input);

                    throw nvae;
                }
                }
                break;
            case 180:
                {
                int LA32_2 = input.LA(2);

                if ( (synpred40_JFSL()) ) {
                    alt32=1;
                }
                else if ( (true) ) {
                    alt32=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 2, input);

                    throw nvae;
                }
                }
                break;
            case 147:
                {
                int LA32_3 = input.LA(2);

                if ( (synpred40_JFSL()) ) {
                    alt32=1;
                }
                else if ( (true) ) {
                    alt32=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // JFSL.g:491:5: ( selector )* storeWildCard
                    {
                    root_0 = (Node)adaptor.nil();

                    // JFSL.g:491:5: ( selector )*
                    loop30:
                    do {
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( (LA30_0==141) ) {
                            int LA30_1 = input.LA(2);

                            if ( (LA30_1==168) ) {
                                int LA30_3 = input.LA(3);

                                if ( (LA30_3==Identifier||LA30_3==145||LA30_3==151||LA30_3==190||LA30_3==200||(LA30_3>=203 && LA30_3<=210)) ) {
                                    alt30=1;
                                }


                            }
                            else if ( (LA30_1==Identifier||LA30_1==145||LA30_1==151||(LA30_1>=172 && LA30_1<=179)||LA30_1==190||LA30_1==200||(LA30_1>=203 && LA30_1<=210)) ) {
                                alt30=1;
                            }


                        }
                        else if ( (LA30_0==147||LA30_0==180) ) {
                            alt30=1;
                        }


                        switch (alt30) {
                    	case 1 :
                    	    // JFSL.g:0:0: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_storeSelectors1726);
                    	    selector104=selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, selector104.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop30;
                        }
                    } while (true);

                    pushFollow(FOLLOW_storeWildCard_in_storeSelectors1729);
                    storeWildCard105=storeWildCard();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, storeWildCard105.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:492:5: ( selector )+
                    {
                    root_0 = (Node)adaptor.nil();

                    // JFSL.g:492:5: ( selector )+
                    int cnt31=0;
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==141||LA31_0==147||LA31_0==180) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // JFSL.g:0:0: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_storeSelectors1736);
                    	    selector106=selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, selector106.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt31 >= 1 ) break loop31;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(31, input);
                                throw eee;
                        }
                        cnt31++;
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 19, storeSelectors_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "storeSelectors"

    public static class storeWildCard_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "storeWildCard"
    // JFSL.g:495:1: storeWildCard : ( '.' '*' ) -> FRAME_ALL ;
    public final JFSLParser.storeWildCard_return storeWildCard() throws RecognitionException {
        JFSLParser.storeWildCard_return retval = new JFSLParser.storeWildCard_return();
        retval.start = input.LT(1);
        int storeWildCard_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal107=null;
        Token char_literal108=null;

        Node char_literal107_tree=null;
        Node char_literal108_tree=null;
        RewriteRuleTokenStream stream_168=new RewriteRuleTokenStream(adaptor,"token 168");
        RewriteRuleTokenStream stream_141=new RewriteRuleTokenStream(adaptor,"token 141");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }
            // JFSL.g:496:4: ( ( '.' '*' ) -> FRAME_ALL )
            // JFSL.g:496:6: ( '.' '*' )
            {
            // JFSL.g:496:6: ( '.' '*' )
            // JFSL.g:496:7: '.' '*'
            {
            char_literal107=(Token)match(input,141,FOLLOW_141_in_storeWildCard1757); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_141.add(char_literal107);

            char_literal108=(Token)match(input,168,FOLLOW_168_in_storeWildCard1759); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_168.add(char_literal108);


            }



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 496:16: -> FRAME_ALL
            {
                adaptor.addChild(root_0, (Node)adaptor.create(FRAME_ALL, "FRAME_ALL"));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 20, storeWildCard_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "storeWildCard"

    public static class keywordLiteral_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "keywordLiteral"
    // JFSL.g:499:1: keywordLiteral : ( 'true' -> LIT_TRUE | 'false' -> LIT_FALSE | 'null' -> LIT_NULL );
    public final JFSLParser.keywordLiteral_return keywordLiteral() throws RecognitionException {
        JFSLParser.keywordLiteral_return retval = new JFSLParser.keywordLiteral_return();
        retval.start = input.LT(1);
        int keywordLiteral_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal109=null;
        Token string_literal110=null;
        Token string_literal111=null;

        Node string_literal109_tree=null;
        Node string_literal110_tree=null;
        Node string_literal111_tree=null;
        RewriteRuleTokenStream stream_170=new RewriteRuleTokenStream(adaptor,"token 170");
        RewriteRuleTokenStream stream_171=new RewriteRuleTokenStream(adaptor,"token 171");
        RewriteRuleTokenStream stream_169=new RewriteRuleTokenStream(adaptor,"token 169");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }
            // JFSL.g:500:3: ( 'true' -> LIT_TRUE | 'false' -> LIT_FALSE | 'null' -> LIT_NULL )
            int alt33=3;
            switch ( input.LA(1) ) {
            case 169:
                {
                alt33=1;
                }
                break;
            case 170:
                {
                alt33=2;
                }
                break;
            case 171:
                {
                alt33=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // JFSL.g:500:5: 'true'
                    {
                    string_literal109=(Token)match(input,169,FOLLOW_169_in_keywordLiteral1780); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_169.add(string_literal109);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 500:12: -> LIT_TRUE
                    {
                        adaptor.addChild(root_0, (Node)adaptor.create(LIT_TRUE, "LIT_TRUE"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:501:5: 'false'
                    {
                    string_literal110=(Token)match(input,170,FOLLOW_170_in_keywordLiteral1792); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_170.add(string_literal110);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 501:13: -> LIT_FALSE
                    {
                        adaptor.addChild(root_0, (Node)adaptor.create(LIT_FALSE, "LIT_FALSE"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:502:5: 'null'
                    {
                    string_literal111=(Token)match(input,171,FOLLOW_171_in_keywordLiteral1803); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_171.add(string_literal111);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 502:12: -> LIT_NULL
                    {
                        adaptor.addChild(root_0, (Node)adaptor.create(LIT_NULL, "LIT_NULL"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 21, keywordLiteral_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "keywordLiteral"

    public static class literal_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // JFSL.g:505:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | keywordLiteral );
    public final JFSLParser.literal_return literal() throws RecognitionException {
        JFSLParser.literal_return retval = new JFSLParser.literal_return();
        retval.start = input.LT(1);
        int literal_StartIndex = input.index();
        Node root_0 = null;

        Token FloatingPointLiteral113=null;
        Token CharacterLiteral114=null;
        Token StringLiteral115=null;
        JFSLParser.integerLiteral_return integerLiteral112 = null;

        JFSLParser.keywordLiteral_return keywordLiteral116 = null;


        Node FloatingPointLiteral113_tree=null;
        Node CharacterLiteral114_tree=null;
        Node StringLiteral115_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }
            // JFSL.g:506:3: ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | keywordLiteral )
            int alt34=5;
            switch ( input.LA(1) ) {
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
                {
                alt34=1;
                }
                break;
            case FloatingPointLiteral:
                {
                alt34=2;
                }
                break;
            case CharacterLiteral:
                {
                alt34=3;
                }
                break;
            case StringLiteral:
                {
                alt34=4;
                }
                break;
            case 169:
            case 170:
            case 171:
                {
                alt34=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // JFSL.g:506:5: integerLiteral
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_integerLiteral_in_literal1827);
                    integerLiteral112=integerLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, integerLiteral112.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:507:5: FloatingPointLiteral
                    {
                    root_0 = (Node)adaptor.nil();

                    FloatingPointLiteral113=(Token)match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_literal1833); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FloatingPointLiteral113_tree = (Node)adaptor.create(FloatingPointLiteral113);
                    adaptor.addChild(root_0, FloatingPointLiteral113_tree);
                    }

                    }
                    break;
                case 3 :
                    // JFSL.g:508:5: CharacterLiteral
                    {
                    root_0 = (Node)adaptor.nil();

                    CharacterLiteral114=(Token)match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal1839); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CharacterLiteral114_tree = (Node)adaptor.create(CharacterLiteral114);
                    adaptor.addChild(root_0, CharacterLiteral114_tree);
                    }

                    }
                    break;
                case 4 :
                    // JFSL.g:509:5: StringLiteral
                    {
                    root_0 = (Node)adaptor.nil();

                    StringLiteral115=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_literal1845); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    StringLiteral115_tree = (Node)adaptor.create(StringLiteral115);
                    adaptor.addChild(root_0, StringLiteral115_tree);
                    }

                    }
                    break;
                case 5 :
                    // JFSL.g:510:5: keywordLiteral
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_keywordLiteral_in_literal1851);
                    keywordLiteral116=keywordLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, keywordLiteral116.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 22, literal_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "literal"

    public static class integerLiteral_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "integerLiteral"
    // JFSL.g:513:1: integerLiteral : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final JFSLParser.integerLiteral_return integerLiteral() throws RecognitionException {
        JFSLParser.integerLiteral_return retval = new JFSLParser.integerLiteral_return();
        retval.start = input.LT(1);
        int integerLiteral_StartIndex = input.index();
        Node root_0 = null;

        Token set117=null;

        Node set117_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }
            // JFSL.g:514:3: ( HexLiteral | OctalLiteral | DecimalLiteral )
            // JFSL.g:
            {
            root_0 = (Node)adaptor.nil();

            set117=(Token)input.LT(1);
            if ( (input.LA(1)>=HexLiteral && input.LA(1)<=DecimalLiteral) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Node)adaptor.create(set117));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 23, integerLiteral_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "integerLiteral"

    public static class primitiveType_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primitiveType"
    // JFSL.g:519:1: primitiveType : (x= 'boolean' -> ^( TYPE_BOOLEAN $x) | x= 'char' -> ^( TYPE_CHAR $x) | x= 'byte' -> ^( TYPE_BYTE $x) | x= 'short' -> ^( TYPE_SHORT $x) | x= 'int' -> ^( TYPE_INT $x) | x= 'long' -> ^( TYPE_LONG $x) | x= 'float' -> ^( TYPE_FLOAT $x) | x= 'double' -> ^( TYPE_DOUBLE $x) );
    public final JFSLParser.primitiveType_return primitiveType() throws RecognitionException {
        JFSLParser.primitiveType_return retval = new JFSLParser.primitiveType_return();
        retval.start = input.LT(1);
        int primitiveType_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;

        Node x_tree=null;
        RewriteRuleTokenStream stream_174=new RewriteRuleTokenStream(adaptor,"token 174");
        RewriteRuleTokenStream stream_175=new RewriteRuleTokenStream(adaptor,"token 175");
        RewriteRuleTokenStream stream_172=new RewriteRuleTokenStream(adaptor,"token 172");
        RewriteRuleTokenStream stream_173=new RewriteRuleTokenStream(adaptor,"token 173");
        RewriteRuleTokenStream stream_179=new RewriteRuleTokenStream(adaptor,"token 179");
        RewriteRuleTokenStream stream_178=new RewriteRuleTokenStream(adaptor,"token 178");
        RewriteRuleTokenStream stream_177=new RewriteRuleTokenStream(adaptor,"token 177");
        RewriteRuleTokenStream stream_176=new RewriteRuleTokenStream(adaptor,"token 176");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }
            // JFSL.g:520:3: (x= 'boolean' -> ^( TYPE_BOOLEAN $x) | x= 'char' -> ^( TYPE_CHAR $x) | x= 'byte' -> ^( TYPE_BYTE $x) | x= 'short' -> ^( TYPE_SHORT $x) | x= 'int' -> ^( TYPE_INT $x) | x= 'long' -> ^( TYPE_LONG $x) | x= 'float' -> ^( TYPE_FLOAT $x) | x= 'double' -> ^( TYPE_DOUBLE $x) )
            int alt35=8;
            switch ( input.LA(1) ) {
            case 172:
                {
                alt35=1;
                }
                break;
            case 173:
                {
                alt35=2;
                }
                break;
            case 174:
                {
                alt35=3;
                }
                break;
            case 175:
                {
                alt35=4;
                }
                break;
            case 176:
                {
                alt35=5;
                }
                break;
            case 177:
                {
                alt35=6;
                }
                break;
            case 178:
                {
                alt35=7;
                }
                break;
            case 179:
                {
                alt35=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // JFSL.g:520:5: x= 'boolean'
                    {
                    x=(Token)match(input,172,FOLLOW_172_in_primitiveType1907); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_172.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 520:16: -> ^( TYPE_BOOLEAN $x)
                    {
                        // JFSL.g:520:19: ^( TYPE_BOOLEAN $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_BOOLEAN, "TYPE_BOOLEAN"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:521:5: x= 'char'
                    {
                    x=(Token)match(input,173,FOLLOW_173_in_primitiveType1923); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_173.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 521:16: -> ^( TYPE_CHAR $x)
                    {
                        // JFSL.g:521:19: ^( TYPE_CHAR $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_CHAR, "TYPE_CHAR"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:522:5: x= 'byte'
                    {
                    x=(Token)match(input,174,FOLLOW_174_in_primitiveType1942); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_174.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 522:16: -> ^( TYPE_BYTE $x)
                    {
                        // JFSL.g:522:19: ^( TYPE_BYTE $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_BYTE, "TYPE_BYTE"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // JFSL.g:523:5: x= 'short'
                    {
                    x=(Token)match(input,175,FOLLOW_175_in_primitiveType1961); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_175.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 523:16: -> ^( TYPE_SHORT $x)
                    {
                        // JFSL.g:523:19: ^( TYPE_SHORT $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_SHORT, "TYPE_SHORT"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // JFSL.g:524:5: x= 'int'
                    {
                    x=(Token)match(input,176,FOLLOW_176_in_primitiveType1979); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_176.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 524:16: -> ^( TYPE_INT $x)
                    {
                        // JFSL.g:524:19: ^( TYPE_INT $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_INT, "TYPE_INT"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // JFSL.g:525:5: x= 'long'
                    {
                    x=(Token)match(input,177,FOLLOW_177_in_primitiveType1999); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_177.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 525:16: -> ^( TYPE_LONG $x)
                    {
                        // JFSL.g:525:19: ^( TYPE_LONG $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_LONG, "TYPE_LONG"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 7 :
                    // JFSL.g:526:5: x= 'float'
                    {
                    x=(Token)match(input,178,FOLLOW_178_in_primitiveType2018); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_178.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 526:16: -> ^( TYPE_FLOAT $x)
                    {
                        // JFSL.g:526:19: ^( TYPE_FLOAT $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_FLOAT, "TYPE_FLOAT"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 8 :
                    // JFSL.g:527:5: x= 'double'
                    {
                    x=(Token)match(input,179,FOLLOW_179_in_primitiveType2036); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_179.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 527:16: -> ^( TYPE_DOUBLE $x)
                    {
                        // JFSL.g:527:19: ^( TYPE_DOUBLE $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_DOUBLE, "TYPE_DOUBLE"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 24, primitiveType_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primitiveType"

    public static class typeName_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeName"
    // JFSL.g:530:1: typeName : Identifier ( '.' Identifier )* ( typeParameters )? -> ^( TYPE_REF ( ^( IDENTIFIER Identifier ) )+ ( ^( TYPE_PARAMETERS typeParameters ) )? ) ;
    public final JFSLParser.typeName_return typeName() throws RecognitionException {
        JFSLParser.typeName_return retval = new JFSLParser.typeName_return();
        retval.start = input.LT(1);
        int typeName_StartIndex = input.index();
        Node root_0 = null;

        Token Identifier118=null;
        Token char_literal119=null;
        Token Identifier120=null;
        JFSLParser.typeParameters_return typeParameters121 = null;


        Node Identifier118_tree=null;
        Node char_literal119_tree=null;
        Node Identifier120_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_141=new RewriteRuleTokenStream(adaptor,"token 141");
        RewriteRuleSubtreeStream stream_typeParameters=new RewriteRuleSubtreeStream(adaptor,"rule typeParameters");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return retval; }
            // JFSL.g:531:3: ( Identifier ( '.' Identifier )* ( typeParameters )? -> ^( TYPE_REF ( ^( IDENTIFIER Identifier ) )+ ( ^( TYPE_PARAMETERS typeParameters ) )? ) )
            // JFSL.g:531:5: Identifier ( '.' Identifier )* ( typeParameters )?
            {
            Identifier118=(Token)match(input,Identifier,FOLLOW_Identifier_in_typeName2058); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Identifier.add(Identifier118);

            // JFSL.g:531:16: ( '.' Identifier )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==141) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // JFSL.g:531:17: '.' Identifier
            	    {
            	    char_literal119=(Token)match(input,141,FOLLOW_141_in_typeName2061); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_141.add(char_literal119);

            	    Identifier120=(Token)match(input,Identifier,FOLLOW_Identifier_in_typeName2063); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Identifier.add(Identifier120);


            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

            // JFSL.g:531:34: ( typeParameters )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==147) ) {
                int LA37_1 = input.LA(2);

                if ( (LA37_1==Identifier) ) {
                    alt37=1;
                }
            }
            switch (alt37) {
                case 1 :
                    // JFSL.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_typeName2067);
                    typeParameters121=typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_typeParameters.add(typeParameters121.getTree());

                    }
                    break;

            }



            // AST REWRITE
            // elements: typeParameters, Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 531:50: -> ^( TYPE_REF ( ^( IDENTIFIER Identifier ) )+ ( ^( TYPE_PARAMETERS typeParameters ) )? )
            {
                // JFSL.g:531:53: ^( TYPE_REF ( ^( IDENTIFIER Identifier ) )+ ( ^( TYPE_PARAMETERS typeParameters ) )? )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_REF, "TYPE_REF"), root_1);

                if ( !(stream_Identifier.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_Identifier.hasNext() ) {
                    // JFSL.g:531:64: ^( IDENTIFIER Identifier )
                    {
                    Node root_2 = (Node)adaptor.nil();
                    root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(IDENTIFIER, "IDENTIFIER"), root_2);

                    adaptor.addChild(root_2, stream_Identifier.nextNode());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_Identifier.reset();
                // JFSL.g:531:90: ( ^( TYPE_PARAMETERS typeParameters ) )?
                if ( stream_typeParameters.hasNext() ) {
                    // JFSL.g:531:90: ^( TYPE_PARAMETERS typeParameters )
                    {
                    Node root_2 = (Node)adaptor.nil();
                    root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_PARAMETERS, "TYPE_PARAMETERS"), root_2);

                    adaptor.addChild(root_2, stream_typeParameters.nextTree());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_typeParameters.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 25, typeName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeName"

    public static class typeDisambiguous_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "typeDisambiguous"
    // JFSL.g:534:1: typeDisambiguous : ( ( primitiveType -> primitiveType ) ( ( '[' ']' ) -> ^( TYPE_ARRAY $typeDisambiguous) )* | ( typeName -> typeName ) ( ( '[' ']' ) -> ^( TYPE_ARRAY $typeDisambiguous) )+ );
    public final JFSLParser.typeDisambiguous_return typeDisambiguous() throws RecognitionException {
        JFSLParser.typeDisambiguous_return retval = new JFSLParser.typeDisambiguous_return();
        retval.start = input.LT(1);
        int typeDisambiguous_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal123=null;
        Token char_literal124=null;
        Token char_literal126=null;
        Token char_literal127=null;
        JFSLParser.primitiveType_return primitiveType122 = null;

        JFSLParser.typeName_return typeName125 = null;


        Node char_literal123_tree=null;
        Node char_literal124_tree=null;
        Node char_literal126_tree=null;
        Node char_literal127_tree=null;
        RewriteRuleTokenStream stream_180=new RewriteRuleTokenStream(adaptor,"token 180");
        RewriteRuleTokenStream stream_181=new RewriteRuleTokenStream(adaptor,"token 181");
        RewriteRuleSubtreeStream stream_typeName=new RewriteRuleSubtreeStream(adaptor,"rule typeName");
        RewriteRuleSubtreeStream stream_primitiveType=new RewriteRuleSubtreeStream(adaptor,"rule primitiveType");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }
            // JFSL.g:535:3: ( ( primitiveType -> primitiveType ) ( ( '[' ']' ) -> ^( TYPE_ARRAY $typeDisambiguous) )* | ( typeName -> typeName ) ( ( '[' ']' ) -> ^( TYPE_ARRAY $typeDisambiguous) )+ )
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( ((LA40_0>=172 && LA40_0<=179)) ) {
                alt40=1;
            }
            else if ( (LA40_0==Identifier) ) {
                alt40=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;
            }
            switch (alt40) {
                case 1 :
                    // JFSL.g:535:5: ( primitiveType -> primitiveType ) ( ( '[' ']' ) -> ^( TYPE_ARRAY $typeDisambiguous) )*
                    {
                    // JFSL.g:535:5: ( primitiveType -> primitiveType )
                    // JFSL.g:535:6: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_typeDisambiguous2104);
                    primitiveType122=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_primitiveType.add(primitiveType122.getTree());


                    // AST REWRITE
                    // elements: primitiveType
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 535:20: -> primitiveType
                    {
                        adaptor.addChild(root_0, stream_primitiveType.nextTree());

                    }

                    retval.tree = root_0;}
                    }

                    // JFSL.g:535:38: ( ( '[' ']' ) -> ^( TYPE_ARRAY $typeDisambiguous) )*
                    loop38:
                    do {
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( (LA38_0==180) ) {
                            int LA38_2 = input.LA(2);

                            if ( (LA38_2==181) ) {
                                int LA38_3 = input.LA(3);

                                if ( (synpred59_JFSL()) ) {
                                    alt38=1;
                                }


                            }


                        }


                        switch (alt38) {
                    	case 1 :
                    	    // JFSL.g:535:39: ( '[' ']' )
                    	    {
                    	    // JFSL.g:535:39: ( '[' ']' )
                    	    // JFSL.g:535:40: '[' ']'
                    	    {
                    	    char_literal123=(Token)match(input,180,FOLLOW_180_in_typeDisambiguous2113); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_180.add(char_literal123);

                    	    char_literal124=(Token)match(input,181,FOLLOW_181_in_typeDisambiguous2115); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_181.add(char_literal124);


                    	    }



                    	    // AST REWRITE
                    	    // elements: typeDisambiguous
                    	    // token labels: 
                    	    // rule labels: retval
                    	    // token list labels: 
                    	    // rule list labels: 
                    	    // wildcard labels: 
                    	    if ( state.backtracking==0 ) {
                    	    retval.tree = root_0;
                    	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    	    root_0 = (Node)adaptor.nil();
                    	    // 535:49: -> ^( TYPE_ARRAY $typeDisambiguous)
                    	    {
                    	        // JFSL.g:535:52: ^( TYPE_ARRAY $typeDisambiguous)
                    	        {
                    	        Node root_1 = (Node)adaptor.nil();
                    	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_ARRAY, "TYPE_ARRAY"), root_1);

                    	        adaptor.addChild(root_1, stream_retval.nextTree());

                    	        adaptor.addChild(root_0, root_1);
                    	        }

                    	    }

                    	    retval.tree = root_0;}
                    	    }
                    	    break;

                    	default :
                    	    break loop38;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // JFSL.g:536:5: ( typeName -> typeName ) ( ( '[' ']' ) -> ^( TYPE_ARRAY $typeDisambiguous) )+
                    {
                    // JFSL.g:536:5: ( typeName -> typeName )
                    // JFSL.g:536:6: typeName
                    {
                    pushFollow(FOLLOW_typeName_in_typeDisambiguous2134);
                    typeName125=typeName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_typeName.add(typeName125.getTree());


                    // AST REWRITE
                    // elements: typeName
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 536:15: -> typeName
                    {
                        adaptor.addChild(root_0, stream_typeName.nextTree());

                    }

                    retval.tree = root_0;}
                    }

                    // JFSL.g:536:28: ( ( '[' ']' ) -> ^( TYPE_ARRAY $typeDisambiguous) )+
                    int cnt39=0;
                    loop39:
                    do {
                        int alt39=2;
                        int LA39_0 = input.LA(1);

                        if ( (LA39_0==180) ) {
                            int LA39_2 = input.LA(2);

                            if ( (LA39_2==181) ) {
                                int LA39_3 = input.LA(3);

                                if ( (synpred61_JFSL()) ) {
                                    alt39=1;
                                }


                            }


                        }


                        switch (alt39) {
                    	case 1 :
                    	    // JFSL.g:536:29: ( '[' ']' )
                    	    {
                    	    // JFSL.g:536:29: ( '[' ']' )
                    	    // JFSL.g:536:30: '[' ']'
                    	    {
                    	    char_literal126=(Token)match(input,180,FOLLOW_180_in_typeDisambiguous2143); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_180.add(char_literal126);

                    	    char_literal127=(Token)match(input,181,FOLLOW_181_in_typeDisambiguous2145); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_181.add(char_literal127);


                    	    }



                    	    // AST REWRITE
                    	    // elements: typeDisambiguous
                    	    // token labels: 
                    	    // rule labels: retval
                    	    // token list labels: 
                    	    // rule list labels: 
                    	    // wildcard labels: 
                    	    if ( state.backtracking==0 ) {
                    	    retval.tree = root_0;
                    	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    	    root_0 = (Node)adaptor.nil();
                    	    // 536:39: -> ^( TYPE_ARRAY $typeDisambiguous)
                    	    {
                    	        // JFSL.g:536:42: ^( TYPE_ARRAY $typeDisambiguous)
                    	        {
                    	        Node root_1 = (Node)adaptor.nil();
                    	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_ARRAY, "TYPE_ARRAY"), root_1);

                    	        adaptor.addChild(root_1, stream_retval.nextTree());

                    	        adaptor.addChild(root_0, root_1);
                    	        }

                    	    }

                    	    retval.tree = root_0;}
                    	    }
                    	    break;

                    	default :
                    	    if ( cnt39 >= 1 ) break loop39;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(39, input);
                                throw eee;
                        }
                        cnt39++;
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 26, typeDisambiguous_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeDisambiguous"

    public static class type_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type"
    // JFSL.g:539:1: type : ( typeDisambiguous | typeName );
    public final JFSLParser.type_return type() throws RecognitionException {
        JFSLParser.type_return retval = new JFSLParser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.typeDisambiguous_return typeDisambiguous128 = null;

        JFSLParser.typeName_return typeName129 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return retval; }
            // JFSL.g:540:3: ( typeDisambiguous | typeName )
            int alt41=2;
            alt41 = dfa41.predict(input);
            switch (alt41) {
                case 1 :
                    // JFSL.g:540:5: typeDisambiguous
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_typeDisambiguous_in_type2170);
                    typeDisambiguous128=typeDisambiguous();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeDisambiguous128.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:541:5: typeName
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_typeName_in_type2176);
                    typeName129=typeName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeName129.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 27, type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "type"

    public static class parExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "parExpression"
    // JFSL.g:546:1: parExpression : x= '(' expression ')' ;
    public final JFSLParser.parExpression_return parExpression() throws RecognitionException {
        JFSLParser.parExpression_return retval = new JFSLParser.parExpression_return();
        retval.start = input.LT(1);
        int parExpression_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;
        Token char_literal131=null;
        JFSLParser.expression_return expression130 = null;


        Node x_tree=null;
        Node char_literal131_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return retval; }
            // JFSL.g:547:3: (x= '(' expression ')' )
            // JFSL.g:547:5: x= '(' expression ')'
            {
            root_0 = (Node)adaptor.nil();

            x=(Token)match(input,151,FOLLOW_151_in_parExpression2212); if (state.failed) return retval;
            pushFollow(FOLLOW_expression_in_parExpression2215);
            expression130=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression130.getTree());
            char_literal131=(Token)match(input,152,FOLLOW_152_in_parExpression2217); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 28, parExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "parExpression"

    public static class expressionList_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expressionList"
    // JFSL.g:550:1: expressionList : expression ( ',' expression )* ;
    public final JFSLParser.expressionList_return expressionList() throws RecognitionException {
        JFSLParser.expressionList_return retval = new JFSLParser.expressionList_return();
        retval.start = input.LT(1);
        int expressionList_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal133=null;
        JFSLParser.expression_return expression132 = null;

        JFSLParser.expression_return expression134 = null;


        Node char_literal133_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return retval; }
            // JFSL.g:551:3: ( expression ( ',' expression )* )
            // JFSL.g:551:7: expression ( ',' expression )*
            {
            root_0 = (Node)adaptor.nil();

            pushFollow(FOLLOW_expression_in_expressionList2246);
            expression132=expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, expression132.getTree());
            // JFSL.g:551:18: ( ',' expression )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==148) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // JFSL.g:551:19: ',' expression
            	    {
            	    char_literal133=(Token)match(input,148,FOLLOW_148_in_expressionList2249); if (state.failed) return retval;
            	    pushFollow(FOLLOW_expression_in_expressionList2252);
            	    expression134=expression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, expression134.getTree());

            	    }
            	    break;

            	default :
            	    break loop42;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 29, expressionList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expressionList"

    public static class arguments_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "arguments"
    // JFSL.g:554:1: arguments : '(' ( expressionList )? ')' -> ^( ARGUMENTS ( expressionList )? ) ;
    public final JFSLParser.arguments_return arguments() throws RecognitionException {
        JFSLParser.arguments_return retval = new JFSLParser.arguments_return();
        retval.start = input.LT(1);
        int arguments_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal135=null;
        Token char_literal137=null;
        JFSLParser.expressionList_return expressionList136 = null;


        Node char_literal135_tree=null;
        Node char_literal137_tree=null;
        RewriteRuleTokenStream stream_152=new RewriteRuleTokenStream(adaptor,"token 152");
        RewriteRuleTokenStream stream_151=new RewriteRuleTokenStream(adaptor,"token 151");
        RewriteRuleSubtreeStream stream_expressionList=new RewriteRuleSubtreeStream(adaptor,"rule expressionList");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return retval; }
            // JFSL.g:555:3: ( '(' ( expressionList )? ')' -> ^( ARGUMENTS ( expressionList )? ) )
            // JFSL.g:555:5: '(' ( expressionList )? ')'
            {
            char_literal135=(Token)match(input,151,FOLLOW_151_in_arguments2273); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_151.add(char_literal135);

            // JFSL.g:555:9: ( expressionList )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( ((LA43_0>=Identifier && LA43_0<=DecimalLiteral)||LA43_0==145||LA43_0==151||(LA43_0>=169 && LA43_0<=179)||LA43_0==184||LA43_0==190||(LA43_0>=195 && LA43_0<=197)||LA43_0==200||(LA43_0>=203 && LA43_0<=217)) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // JFSL.g:0:0: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments2275);
                    expressionList136=expressionList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expressionList.add(expressionList136.getTree());

                    }
                    break;

            }

            char_literal137=(Token)match(input,152,FOLLOW_152_in_arguments2278); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_152.add(char_literal137);



            // AST REWRITE
            // elements: expressionList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 555:29: -> ^( ARGUMENTS ( expressionList )? )
            {
                // JFSL.g:555:32: ^( ARGUMENTS ( expressionList )? )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(ARGUMENTS, "ARGUMENTS"), root_1);

                // JFSL.g:555:44: ( expressionList )?
                if ( stream_expressionList.hasNext() ) {
                    adaptor.addChild(root_1, stream_expressionList.nextTree());

                }
                stream_expressionList.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 30, arguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arguments"

    public static class expression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "expression"
    // JFSL.g:560:1: expression : conditionalExpression ;
    public final JFSLParser.expression_return expression() throws RecognitionException {
        JFSLParser.expression_return retval = new JFSLParser.expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.conditionalExpression_return conditionalExpression138 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return retval; }
            // JFSL.g:561:3: ( conditionalExpression )
            // JFSL.g:561:5: conditionalExpression
            {
            root_0 = (Node)adaptor.nil();

            pushFollow(FOLLOW_conditionalExpression_in_expression2302);
            conditionalExpression138=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, conditionalExpression138.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 31, expression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class conditionalExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalExpression"
    // JFSL.g:564:1: conditionalExpression : ( quantifiedExpression -> quantifiedExpression ) ( '?' expression ':' expression -> ^( CONDITIONAL $conditionalExpression expression expression ) )? ;
    public final JFSLParser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        JFSLParser.conditionalExpression_return retval = new JFSLParser.conditionalExpression_return();
        retval.start = input.LT(1);
        int conditionalExpression_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal140=null;
        Token char_literal142=null;
        JFSLParser.quantifiedExpression_return quantifiedExpression139 = null;

        JFSLParser.expression_return expression141 = null;

        JFSLParser.expression_return expression143 = null;


        Node char_literal140_tree=null;
        Node char_literal142_tree=null;
        RewriteRuleTokenStream stream_162=new RewriteRuleTokenStream(adaptor,"token 162");
        RewriteRuleTokenStream stream_182=new RewriteRuleTokenStream(adaptor,"token 182");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_quantifiedExpression=new RewriteRuleSubtreeStream(adaptor,"rule quantifiedExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return retval; }
            // JFSL.g:565:3: ( ( quantifiedExpression -> quantifiedExpression ) ( '?' expression ':' expression -> ^( CONDITIONAL $conditionalExpression expression expression ) )? )
            // JFSL.g:565:5: ( quantifiedExpression -> quantifiedExpression ) ( '?' expression ':' expression -> ^( CONDITIONAL $conditionalExpression expression expression ) )?
            {
            // JFSL.g:565:5: ( quantifiedExpression -> quantifiedExpression )
            // JFSL.g:565:7: quantifiedExpression
            {
            pushFollow(FOLLOW_quantifiedExpression_in_conditionalExpression2319);
            quantifiedExpression139=quantifiedExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_quantifiedExpression.add(quantifiedExpression139.getTree());


            // AST REWRITE
            // elements: quantifiedExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 565:28: -> quantifiedExpression
            {
                adaptor.addChild(root_0, stream_quantifiedExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:566:5: ( '?' expression ':' expression -> ^( CONDITIONAL $conditionalExpression expression expression ) )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==182) ) {
                int LA44_1 = input.LA(2);

                if ( (synpred65_JFSL()) ) {
                    alt44=1;
                }
            }
            switch (alt44) {
                case 1 :
                    // JFSL.g:566:7: '?' expression ':' expression
                    {
                    char_literal140=(Token)match(input,182,FOLLOW_182_in_conditionalExpression2332); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_182.add(char_literal140);

                    pushFollow(FOLLOW_expression_in_conditionalExpression2334);
                    expression141=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression141.getTree());
                    char_literal142=(Token)match(input,162,FOLLOW_162_in_conditionalExpression2336); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_162.add(char_literal142);

                    pushFollow(FOLLOW_expression_in_conditionalExpression2338);
                    expression143=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression143.getTree());


                    // AST REWRITE
                    // elements: conditionalExpression, expression, expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 566:37: -> ^( CONDITIONAL $conditionalExpression expression expression )
                    {
                        // JFSL.g:566:40: ^( CONDITIONAL $conditionalExpression expression expression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(CONDITIONAL, "CONDITIONAL"), root_1);

                        adaptor.addChild(root_1, stream_retval.nextTree());
                        adaptor.addChild(root_1, stream_expression.nextTree());
                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 32, conditionalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalExpression"

    public static class quantifiedExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "quantifiedExpression"
    // JFSL.g:570:1: quantifiedExpression : ( setQuantOp decls '|' expression -> ^( QUANTIFY setQuantOp decls expression ) | setQuantOp mdecls '|' expression -> ^( QUANTIFY setQuantOp mdecls expression ) | logicalExpression );
    public final JFSLParser.quantifiedExpression_return quantifiedExpression() throws RecognitionException {
        JFSLParser.quantifiedExpression_return retval = new JFSLParser.quantifiedExpression_return();
        retval.start = input.LT(1);
        int quantifiedExpression_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal146=null;
        Token char_literal150=null;
        JFSLParser.setQuantOp_return setQuantOp144 = null;

        JFSLParser.decls_return decls145 = null;

        JFSLParser.expression_return expression147 = null;

        JFSLParser.setQuantOp_return setQuantOp148 = null;

        JFSLParser.mdecls_return mdecls149 = null;

        JFSLParser.expression_return expression151 = null;

        JFSLParser.logicalExpression_return logicalExpression152 = null;


        Node char_literal146_tree=null;
        Node char_literal150_tree=null;
        RewriteRuleTokenStream stream_164=new RewriteRuleTokenStream(adaptor,"token 164");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_setQuantOp=new RewriteRuleSubtreeStream(adaptor,"rule setQuantOp");
        RewriteRuleSubtreeStream stream_mdecls=new RewriteRuleSubtreeStream(adaptor,"rule mdecls");
        RewriteRuleSubtreeStream stream_decls=new RewriteRuleSubtreeStream(adaptor,"rule decls");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return retval; }
            // JFSL.g:571:3: ( setQuantOp decls '|' expression -> ^( QUANTIFY setQuantOp decls expression ) | setQuantOp mdecls '|' expression -> ^( QUANTIFY setQuantOp mdecls expression ) | logicalExpression )
            int alt45=3;
            alt45 = dfa45.predict(input);
            switch (alt45) {
                case 1 :
                    // JFSL.g:571:5: setQuantOp decls '|' expression
                    {
                    pushFollow(FOLLOW_setQuantOp_in_quantifiedExpression2374);
                    setQuantOp144=setQuantOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_setQuantOp.add(setQuantOp144.getTree());
                    pushFollow(FOLLOW_decls_in_quantifiedExpression2376);
                    decls145=decls();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_decls.add(decls145.getTree());
                    char_literal146=(Token)match(input,164,FOLLOW_164_in_quantifiedExpression2379); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_164.add(char_literal146);

                    pushFollow(FOLLOW_expression_in_quantifiedExpression2381);
                    expression147=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression147.getTree());


                    // AST REWRITE
                    // elements: expression, decls, setQuantOp
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 571:38: -> ^( QUANTIFY setQuantOp decls expression )
                    {
                        // JFSL.g:571:41: ^( QUANTIFY setQuantOp decls expression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(QUANTIFY, "QUANTIFY"), root_1);

                        adaptor.addChild(root_1, stream_setQuantOp.nextTree());
                        adaptor.addChild(root_1, stream_decls.nextTree());
                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:572:5: setQuantOp mdecls '|' expression
                    {
                    pushFollow(FOLLOW_setQuantOp_in_quantifiedExpression2399);
                    setQuantOp148=setQuantOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_setQuantOp.add(setQuantOp148.getTree());
                    pushFollow(FOLLOW_mdecls_in_quantifiedExpression2401);
                    mdecls149=mdecls();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_mdecls.add(mdecls149.getTree());
                    char_literal150=(Token)match(input,164,FOLLOW_164_in_quantifiedExpression2403); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_164.add(char_literal150);

                    pushFollow(FOLLOW_expression_in_quantifiedExpression2405);
                    expression151=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression151.getTree());


                    // AST REWRITE
                    // elements: expression, mdecls, setQuantOp
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 572:38: -> ^( QUANTIFY setQuantOp mdecls expression )
                    {
                        // JFSL.g:572:41: ^( QUANTIFY setQuantOp mdecls expression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(QUANTIFY, "QUANTIFY"), root_1);

                        adaptor.addChild(root_1, stream_setQuantOp.nextTree());
                        adaptor.addChild(root_1, stream_mdecls.nextTree());
                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:573:5: logicalExpression
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_logicalExpression_in_quantifiedExpression2423);
                    logicalExpression152=logicalExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, logicalExpression152.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 33, quantifiedExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "quantifiedExpression"

    public static class logicalExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "logicalExpression"
    // JFSL.g:576:1: logicalExpression : ( conditionalXorExpression -> conditionalXorExpression ) ( logicalOp conditionalXorExpression -> ^( BINARY logicalOp $logicalExpression conditionalXorExpression ) )? ;
    public final JFSLParser.logicalExpression_return logicalExpression() throws RecognitionException {
        JFSLParser.logicalExpression_return retval = new JFSLParser.logicalExpression_return();
        retval.start = input.LT(1);
        int logicalExpression_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.conditionalXorExpression_return conditionalXorExpression153 = null;

        JFSLParser.logicalOp_return logicalOp154 = null;

        JFSLParser.conditionalXorExpression_return conditionalXorExpression155 = null;


        RewriteRuleSubtreeStream stream_conditionalXorExpression=new RewriteRuleSubtreeStream(adaptor,"rule conditionalXorExpression");
        RewriteRuleSubtreeStream stream_logicalOp=new RewriteRuleSubtreeStream(adaptor,"rule logicalOp");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return retval; }
            // JFSL.g:577:3: ( ( conditionalXorExpression -> conditionalXorExpression ) ( logicalOp conditionalXorExpression -> ^( BINARY logicalOp $logicalExpression conditionalXorExpression ) )? )
            // JFSL.g:577:5: ( conditionalXorExpression -> conditionalXorExpression ) ( logicalOp conditionalXorExpression -> ^( BINARY logicalOp $logicalExpression conditionalXorExpression ) )?
            {
            // JFSL.g:577:5: ( conditionalXorExpression -> conditionalXorExpression )
            // JFSL.g:577:6: conditionalXorExpression
            {
            pushFollow(FOLLOW_conditionalXorExpression_in_logicalExpression2439);
            conditionalXorExpression153=conditionalXorExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_conditionalXorExpression.add(conditionalXorExpression153.getTree());


            // AST REWRITE
            // elements: conditionalXorExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 577:31: -> conditionalXorExpression
            {
                adaptor.addChild(root_0, stream_conditionalXorExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:578:5: ( logicalOp conditionalXorExpression -> ^( BINARY logicalOp $logicalExpression conditionalXorExpression ) )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==147||LA46_0==183||(LA46_0>=185 && LA46_0<=186)) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // JFSL.g:578:7: logicalOp conditionalXorExpression
                    {
                    pushFollow(FOLLOW_logicalOp_in_logicalExpression2452);
                    logicalOp154=logicalOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_logicalOp.add(logicalOp154.getTree());
                    pushFollow(FOLLOW_conditionalXorExpression_in_logicalExpression2454);
                    conditionalXorExpression155=conditionalXorExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_conditionalXorExpression.add(conditionalXorExpression155.getTree());


                    // AST REWRITE
                    // elements: conditionalXorExpression, logicalOp, logicalExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 578:42: -> ^( BINARY logicalOp $logicalExpression conditionalXorExpression )
                    {
                        // JFSL.g:578:45: ^( BINARY logicalOp $logicalExpression conditionalXorExpression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

                        adaptor.addChild(root_1, stream_logicalOp.nextTree());
                        adaptor.addChild(root_1, stream_retval.nextTree());
                        adaptor.addChild(root_1, stream_conditionalXorExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 34, logicalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "logicalExpression"

    public static class logicalOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "logicalOp"
    // JFSL.g:582:1: logicalOp : ( '<' '=' '>' -> OP_EQUIV | '<' '!' '>' -> OP_NEQUIV | '=' '>' -> OP_IMPLIES | 'implies' -> OP_IMPLIES | 'iff' -> OP_EQUIV );
    public final JFSLParser.logicalOp_return logicalOp() throws RecognitionException {
        JFSLParser.logicalOp_return retval = new JFSLParser.logicalOp_return();
        retval.start = input.LT(1);
        int logicalOp_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal156=null;
        Token char_literal157=null;
        Token char_literal158=null;
        Token char_literal159=null;
        Token char_literal160=null;
        Token char_literal161=null;
        Token char_literal162=null;
        Token char_literal163=null;
        Token string_literal164=null;
        Token string_literal165=null;

        Node char_literal156_tree=null;
        Node char_literal157_tree=null;
        Node char_literal158_tree=null;
        Node char_literal159_tree=null;
        Node char_literal160_tree=null;
        Node char_literal161_tree=null;
        Node char_literal162_tree=null;
        Node char_literal163_tree=null;
        Node string_literal164_tree=null;
        Node string_literal165_tree=null;
        RewriteRuleTokenStream stream_147=new RewriteRuleTokenStream(adaptor,"token 147");
        RewriteRuleTokenStream stream_183=new RewriteRuleTokenStream(adaptor,"token 183");
        RewriteRuleTokenStream stream_184=new RewriteRuleTokenStream(adaptor,"token 184");
        RewriteRuleTokenStream stream_185=new RewriteRuleTokenStream(adaptor,"token 185");
        RewriteRuleTokenStream stream_149=new RewriteRuleTokenStream(adaptor,"token 149");
        RewriteRuleTokenStream stream_186=new RewriteRuleTokenStream(adaptor,"token 186");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return retval; }
            // JFSL.g:583:3: ( '<' '=' '>' -> OP_EQUIV | '<' '!' '>' -> OP_NEQUIV | '=' '>' -> OP_IMPLIES | 'implies' -> OP_IMPLIES | 'iff' -> OP_EQUIV )
            int alt47=5;
            switch ( input.LA(1) ) {
            case 147:
                {
                int LA47_1 = input.LA(2);

                if ( (LA47_1==183) ) {
                    alt47=1;
                }
                else if ( (LA47_1==184) ) {
                    alt47=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 1, input);

                    throw nvae;
                }
                }
                break;
            case 183:
                {
                alt47=3;
                }
                break;
            case 185:
                {
                alt47=4;
                }
                break;
            case 186:
                {
                alt47=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
                case 1 :
                    // JFSL.g:583:7: '<' '=' '>'
                    {
                    char_literal156=(Token)match(input,147,FOLLOW_147_in_logicalOp2491); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_147.add(char_literal156);

                    char_literal157=(Token)match(input,183,FOLLOW_183_in_logicalOp2493); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_183.add(char_literal157);

                    char_literal158=(Token)match(input,149,FOLLOW_149_in_logicalOp2495); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_149.add(char_literal158);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 583:19: -> OP_EQUIV
                    {
                        adaptor.addChild(root_0, (Node)adaptor.create(OP_EQUIV, "OP_EQUIV"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:584:7: '<' '!' '>'
                    {
                    char_literal159=(Token)match(input,147,FOLLOW_147_in_logicalOp2508); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_147.add(char_literal159);

                    char_literal160=(Token)match(input,184,FOLLOW_184_in_logicalOp2510); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_184.add(char_literal160);

                    char_literal161=(Token)match(input,149,FOLLOW_149_in_logicalOp2512); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_149.add(char_literal161);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 584:19: -> OP_NEQUIV
                    {
                        adaptor.addChild(root_0, (Node)adaptor.create(OP_NEQUIV, "OP_NEQUIV"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:585:7: '=' '>'
                    {
                    char_literal162=(Token)match(input,183,FOLLOW_183_in_logicalOp2525); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_183.add(char_literal162);

                    char_literal163=(Token)match(input,149,FOLLOW_149_in_logicalOp2527); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_149.add(char_literal163);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 585:19: -> OP_IMPLIES
                    {
                        adaptor.addChild(root_0, (Node)adaptor.create(OP_IMPLIES, "OP_IMPLIES"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // JFSL.g:586:7: 'implies'
                    {
                    string_literal164=(Token)match(input,185,FOLLOW_185_in_logicalOp2543); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_185.add(string_literal164);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 586:19: -> OP_IMPLIES
                    {
                        adaptor.addChild(root_0, (Node)adaptor.create(OP_IMPLIES, "OP_IMPLIES"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // JFSL.g:587:7: 'iff'
                    {
                    string_literal165=(Token)match(input,186,FOLLOW_186_in_logicalOp2557); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_186.add(string_literal165);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 587:19: -> OP_EQUIV
                    {
                        adaptor.addChild(root_0, (Node)adaptor.create(OP_EQUIV, "OP_EQUIV"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 35, logicalOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "logicalOp"

    public static class conditionalXorExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalXorExpression"
    // JFSL.g:590:1: conditionalXorExpression : ( conditionalOrExpression -> conditionalOrExpression ) ( '^^' a= conditionalOrExpression -> ^( BINARY OP_XOR $conditionalXorExpression $a) )* ;
    public final JFSLParser.conditionalXorExpression_return conditionalXorExpression() throws RecognitionException {
        JFSLParser.conditionalXorExpression_return retval = new JFSLParser.conditionalXorExpression_return();
        retval.start = input.LT(1);
        int conditionalXorExpression_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal167=null;
        JFSLParser.conditionalOrExpression_return a = null;

        JFSLParser.conditionalOrExpression_return conditionalOrExpression166 = null;


        Node string_literal167_tree=null;
        RewriteRuleTokenStream stream_187=new RewriteRuleTokenStream(adaptor,"token 187");
        RewriteRuleSubtreeStream stream_conditionalOrExpression=new RewriteRuleSubtreeStream(adaptor,"rule conditionalOrExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return retval; }
            // JFSL.g:591:3: ( ( conditionalOrExpression -> conditionalOrExpression ) ( '^^' a= conditionalOrExpression -> ^( BINARY OP_XOR $conditionalXorExpression $a) )* )
            // JFSL.g:591:5: ( conditionalOrExpression -> conditionalOrExpression ) ( '^^' a= conditionalOrExpression -> ^( BINARY OP_XOR $conditionalXorExpression $a) )*
            {
            // JFSL.g:591:5: ( conditionalOrExpression -> conditionalOrExpression )
            // JFSL.g:591:6: conditionalOrExpression
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalXorExpression2583);
            conditionalOrExpression166=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_conditionalOrExpression.add(conditionalOrExpression166.getTree());


            // AST REWRITE
            // elements: conditionalOrExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 591:30: -> conditionalOrExpression
            {
                adaptor.addChild(root_0, stream_conditionalOrExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:592:7: ( '^^' a= conditionalOrExpression -> ^( BINARY OP_XOR $conditionalXorExpression $a) )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==187) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // JFSL.g:592:9: '^^' a= conditionalOrExpression
            	    {
            	    string_literal167=(Token)match(input,187,FOLLOW_187_in_conditionalXorExpression2598); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_187.add(string_literal167);

            	    pushFollow(FOLLOW_conditionalOrExpression_in_conditionalXorExpression2602);
            	    a=conditionalOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_conditionalOrExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: conditionalXorExpression, a
            	    // token labels: 
            	    // rule labels: retval, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 592:40: -> ^( BINARY OP_XOR $conditionalXorExpression $a)
            	    {
            	        // JFSL.g:592:43: ^( BINARY OP_XOR $conditionalXorExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, (Node)adaptor.create(OP_XOR, "OP_XOR"));
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 36, conditionalXorExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalXorExpression"

    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalOrExpression"
    // JFSL.g:596:1: conditionalOrExpression : ( conditionalAndExpression -> conditionalAndExpression ) ( '||' a= conditionalAndExpression -> ^( BINARY OP_OR $conditionalOrExpression $a) )* ;
    public final JFSLParser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        JFSLParser.conditionalOrExpression_return retval = new JFSLParser.conditionalOrExpression_return();
        retval.start = input.LT(1);
        int conditionalOrExpression_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal169=null;
        JFSLParser.conditionalAndExpression_return a = null;

        JFSLParser.conditionalAndExpression_return conditionalAndExpression168 = null;


        Node string_literal169_tree=null;
        RewriteRuleTokenStream stream_188=new RewriteRuleTokenStream(adaptor,"token 188");
        RewriteRuleSubtreeStream stream_conditionalAndExpression=new RewriteRuleSubtreeStream(adaptor,"rule conditionalAndExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return retval; }
            // JFSL.g:597:3: ( ( conditionalAndExpression -> conditionalAndExpression ) ( '||' a= conditionalAndExpression -> ^( BINARY OP_OR $conditionalOrExpression $a) )* )
            // JFSL.g:597:5: ( conditionalAndExpression -> conditionalAndExpression ) ( '||' a= conditionalAndExpression -> ^( BINARY OP_OR $conditionalOrExpression $a) )*
            {
            // JFSL.g:597:5: ( conditionalAndExpression -> conditionalAndExpression )
            // JFSL.g:597:6: conditionalAndExpression
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression2643);
            conditionalAndExpression168=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_conditionalAndExpression.add(conditionalAndExpression168.getTree());


            // AST REWRITE
            // elements: conditionalAndExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 597:31: -> conditionalAndExpression
            {
                adaptor.addChild(root_0, stream_conditionalAndExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:598:7: ( '||' a= conditionalAndExpression -> ^( BINARY OP_OR $conditionalOrExpression $a) )*
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==188) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // JFSL.g:598:9: '||' a= conditionalAndExpression
            	    {
            	    string_literal169=(Token)match(input,188,FOLLOW_188_in_conditionalOrExpression2658); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_188.add(string_literal169);

            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression2662);
            	    a=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_conditionalAndExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: a, conditionalOrExpression
            	    // token labels: 
            	    // rule labels: retval, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 598:41: -> ^( BINARY OP_OR $conditionalOrExpression $a)
            	    {
            	        // JFSL.g:598:44: ^( BINARY OP_OR $conditionalOrExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, (Node)adaptor.create(OP_OR, "OP_OR"));
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop49;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 37, conditionalOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalOrExpression"

    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "conditionalAndExpression"
    // JFSL.g:602:1: conditionalAndExpression : ( inclusiveOrExpression -> inclusiveOrExpression ) ( '&&' a= inclusiveOrExpression -> ^( BINARY OP_AND $conditionalAndExpression $a) )* ;
    public final JFSLParser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        JFSLParser.conditionalAndExpression_return retval = new JFSLParser.conditionalAndExpression_return();
        retval.start = input.LT(1);
        int conditionalAndExpression_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal171=null;
        JFSLParser.inclusiveOrExpression_return a = null;

        JFSLParser.inclusiveOrExpression_return inclusiveOrExpression170 = null;


        Node string_literal171_tree=null;
        RewriteRuleTokenStream stream_189=new RewriteRuleTokenStream(adaptor,"token 189");
        RewriteRuleSubtreeStream stream_inclusiveOrExpression=new RewriteRuleSubtreeStream(adaptor,"rule inclusiveOrExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return retval; }
            // JFSL.g:603:3: ( ( inclusiveOrExpression -> inclusiveOrExpression ) ( '&&' a= inclusiveOrExpression -> ^( BINARY OP_AND $conditionalAndExpression $a) )* )
            // JFSL.g:603:5: ( inclusiveOrExpression -> inclusiveOrExpression ) ( '&&' a= inclusiveOrExpression -> ^( BINARY OP_AND $conditionalAndExpression $a) )*
            {
            // JFSL.g:603:5: ( inclusiveOrExpression -> inclusiveOrExpression )
            // JFSL.g:603:6: inclusiveOrExpression
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression2699);
            inclusiveOrExpression170=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_inclusiveOrExpression.add(inclusiveOrExpression170.getTree());


            // AST REWRITE
            // elements: inclusiveOrExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 603:28: -> inclusiveOrExpression
            {
                adaptor.addChild(root_0, stream_inclusiveOrExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:604:7: ( '&&' a= inclusiveOrExpression -> ^( BINARY OP_AND $conditionalAndExpression $a) )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( (LA50_0==189) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // JFSL.g:604:9: '&&' a= inclusiveOrExpression
            	    {
            	    string_literal171=(Token)match(input,189,FOLLOW_189_in_conditionalAndExpression2714); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_189.add(string_literal171);

            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression2718);
            	    a=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_inclusiveOrExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: a, conditionalAndExpression
            	    // token labels: 
            	    // rule labels: retval, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 604:38: -> ^( BINARY OP_AND $conditionalAndExpression $a)
            	    {
            	        // JFSL.g:604:41: ^( BINARY OP_AND $conditionalAndExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, (Node)adaptor.create(OP_AND, "OP_AND"));
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop50;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 38, conditionalAndExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalAndExpression"

    public static class inclusiveOrExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "inclusiveOrExpression"
    // JFSL.g:608:1: inclusiveOrExpression : ( exclusiveOrExpression -> exclusiveOrExpression ) ( '|' a= exclusiveOrExpression -> ^( BINARY OP_BIT_OR $inclusiveOrExpression $a) )* ;
    public final JFSLParser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        JFSLParser.inclusiveOrExpression_return retval = new JFSLParser.inclusiveOrExpression_return();
        retval.start = input.LT(1);
        int inclusiveOrExpression_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal173=null;
        JFSLParser.exclusiveOrExpression_return a = null;

        JFSLParser.exclusiveOrExpression_return exclusiveOrExpression172 = null;


        Node char_literal173_tree=null;
        RewriteRuleTokenStream stream_164=new RewriteRuleTokenStream(adaptor,"token 164");
        RewriteRuleSubtreeStream stream_exclusiveOrExpression=new RewriteRuleSubtreeStream(adaptor,"rule exclusiveOrExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return retval; }
            // JFSL.g:609:3: ( ( exclusiveOrExpression -> exclusiveOrExpression ) ( '|' a= exclusiveOrExpression -> ^( BINARY OP_BIT_OR $inclusiveOrExpression $a) )* )
            // JFSL.g:609:5: ( exclusiveOrExpression -> exclusiveOrExpression ) ( '|' a= exclusiveOrExpression -> ^( BINARY OP_BIT_OR $inclusiveOrExpression $a) )*
            {
            // JFSL.g:609:5: ( exclusiveOrExpression -> exclusiveOrExpression )
            // JFSL.g:609:6: exclusiveOrExpression
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression2760);
            exclusiveOrExpression172=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_exclusiveOrExpression.add(exclusiveOrExpression172.getTree());


            // AST REWRITE
            // elements: exclusiveOrExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 609:28: -> exclusiveOrExpression
            {
                adaptor.addChild(root_0, stream_exclusiveOrExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:610:7: ( '|' a= exclusiveOrExpression -> ^( BINARY OP_BIT_OR $inclusiveOrExpression $a) )*
            loop51:
            do {
                int alt51=2;
                alt51 = dfa51.predict(input);
                switch (alt51) {
            	case 1 :
            	    // JFSL.g:610:9: '|' a= exclusiveOrExpression
            	    {
            	    char_literal173=(Token)match(input,164,FOLLOW_164_in_inclusiveOrExpression2775); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_164.add(char_literal173);

            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression2779);
            	    a=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_exclusiveOrExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: a, inclusiveOrExpression
            	    // token labels: 
            	    // rule labels: retval, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 610:37: -> ^( BINARY OP_BIT_OR $inclusiveOrExpression $a)
            	    {
            	        // JFSL.g:610:40: ^( BINARY OP_BIT_OR $inclusiveOrExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, (Node)adaptor.create(OP_BIT_OR, "OP_BIT_OR"));
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 39, inclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "inclusiveOrExpression"

    public static class exclusiveOrExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "exclusiveOrExpression"
    // JFSL.g:614:1: exclusiveOrExpression : ( andExpression -> andExpression ) ( '^' a= andExpression -> ^( BINARY OP_BIT_XOR $exclusiveOrExpression $a) )* ;
    public final JFSLParser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        JFSLParser.exclusiveOrExpression_return retval = new JFSLParser.exclusiveOrExpression_return();
        retval.start = input.LT(1);
        int exclusiveOrExpression_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal175=null;
        JFSLParser.andExpression_return a = null;

        JFSLParser.andExpression_return andExpression174 = null;


        Node char_literal175_tree=null;
        RewriteRuleTokenStream stream_190=new RewriteRuleTokenStream(adaptor,"token 190");
        RewriteRuleSubtreeStream stream_andExpression=new RewriteRuleSubtreeStream(adaptor,"rule andExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }
            // JFSL.g:615:3: ( ( andExpression -> andExpression ) ( '^' a= andExpression -> ^( BINARY OP_BIT_XOR $exclusiveOrExpression $a) )* )
            // JFSL.g:615:5: ( andExpression -> andExpression ) ( '^' a= andExpression -> ^( BINARY OP_BIT_XOR $exclusiveOrExpression $a) )*
            {
            // JFSL.g:615:5: ( andExpression -> andExpression )
            // JFSL.g:615:6: andExpression
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression2816);
            andExpression174=andExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_andExpression.add(andExpression174.getTree());


            // AST REWRITE
            // elements: andExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 615:20: -> andExpression
            {
                adaptor.addChild(root_0, stream_andExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:616:7: ( '^' a= andExpression -> ^( BINARY OP_BIT_XOR $exclusiveOrExpression $a) )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==190) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // JFSL.g:616:9: '^' a= andExpression
            	    {
            	    char_literal175=(Token)match(input,190,FOLLOW_190_in_exclusiveOrExpression2831); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_190.add(char_literal175);

            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression2835);
            	    a=andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_andExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: a, exclusiveOrExpression
            	    // token labels: 
            	    // rule labels: retval, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 616:29: -> ^( BINARY OP_BIT_XOR $exclusiveOrExpression $a)
            	    {
            	        // JFSL.g:616:32: ^( BINARY OP_BIT_XOR $exclusiveOrExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, (Node)adaptor.create(OP_BIT_XOR, "OP_BIT_XOR"));
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop52;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 40, exclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "exclusiveOrExpression"

    public static class andExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "andExpression"
    // JFSL.g:620:1: andExpression : ( equalityExpression -> equalityExpression ) ( bitAndOp a= equalityExpression -> ^( BINARY bitAndOp $andExpression $a) )* ;
    public final JFSLParser.andExpression_return andExpression() throws RecognitionException {
        JFSLParser.andExpression_return retval = new JFSLParser.andExpression_return();
        retval.start = input.LT(1);
        int andExpression_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.equalityExpression_return a = null;

        JFSLParser.equalityExpression_return equalityExpression176 = null;

        JFSLParser.bitAndOp_return bitAndOp177 = null;


        RewriteRuleSubtreeStream stream_equalityExpression=new RewriteRuleSubtreeStream(adaptor,"rule equalityExpression");
        RewriteRuleSubtreeStream stream_bitAndOp=new RewriteRuleSubtreeStream(adaptor,"rule bitAndOp");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return retval; }
            // JFSL.g:621:3: ( ( equalityExpression -> equalityExpression ) ( bitAndOp a= equalityExpression -> ^( BINARY bitAndOp $andExpression $a) )* )
            // JFSL.g:621:7: ( equalityExpression -> equalityExpression ) ( bitAndOp a= equalityExpression -> ^( BINARY bitAndOp $andExpression $a) )*
            {
            // JFSL.g:621:7: ( equalityExpression -> equalityExpression )
            // JFSL.g:621:8: equalityExpression
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression2874);
            equalityExpression176=equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_equalityExpression.add(equalityExpression176.getTree());


            // AST REWRITE
            // elements: equalityExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 621:27: -> equalityExpression
            {
                adaptor.addChild(root_0, stream_equalityExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:622:7: ( bitAndOp a= equalityExpression -> ^( BINARY bitAndOp $andExpression $a) )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);

                if ( (LA53_0==191) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // JFSL.g:622:9: bitAndOp a= equalityExpression
            	    {
            	    pushFollow(FOLLOW_bitAndOp_in_andExpression2889);
            	    bitAndOp177=bitAndOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_bitAndOp.add(bitAndOp177.getTree());
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression2893);
            	    a=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_equalityExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: andExpression, a, bitAndOp
            	    // token labels: 
            	    // rule labels: retval, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 622:39: -> ^( BINARY bitAndOp $andExpression $a)
            	    {
            	        // JFSL.g:622:42: ^( BINARY bitAndOp $andExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, stream_bitAndOp.nextTree());
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop53;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 41, andExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "andExpression"

    public static class bitAndOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bitAndOp"
    // JFSL.g:626:1: bitAndOp : x= '&' -> ^( OP_BIT_AND_OR_INTERSECTION $x) ;
    public final JFSLParser.bitAndOp_return bitAndOp() throws RecognitionException {
        JFSLParser.bitAndOp_return retval = new JFSLParser.bitAndOp_return();
        retval.start = input.LT(1);
        int bitAndOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;

        Node x_tree=null;
        RewriteRuleTokenStream stream_191=new RewriteRuleTokenStream(adaptor,"token 191");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return retval; }
            // JFSL.g:627:3: (x= '&' -> ^( OP_BIT_AND_OR_INTERSECTION $x) )
            // JFSL.g:627:5: x= '&'
            {
            x=(Token)match(input,191,FOLLOW_191_in_bitAndOp2932); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_191.add(x);



            // AST REWRITE
            // elements: x
            // token labels: x
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 627:11: -> ^( OP_BIT_AND_OR_INTERSECTION $x)
            {
                // JFSL.g:627:14: ^( OP_BIT_AND_OR_INTERSECTION $x)
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_BIT_AND_OR_INTERSECTION, "OP_BIT_AND_OR_INTERSECTION"), root_1);

                adaptor.addChild(root_1, stream_x.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 42, bitAndOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "bitAndOp"

    public static class equalityExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "equalityExpression"
    // JFSL.g:631:1: equalityExpression : ( instanceOfExpression -> instanceOfExpression ) (op= equalityOp a= instanceOfExpression -> ^( BINARY $op $equalityExpression $a) )* ;
    public final JFSLParser.equalityExpression_return equalityExpression() throws RecognitionException {
        JFSLParser.equalityExpression_return retval = new JFSLParser.equalityExpression_return();
        retval.start = input.LT(1);
        int equalityExpression_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.equalityOp_return op = null;

        JFSLParser.instanceOfExpression_return a = null;

        JFSLParser.instanceOfExpression_return instanceOfExpression178 = null;


        RewriteRuleSubtreeStream stream_instanceOfExpression=new RewriteRuleSubtreeStream(adaptor,"rule instanceOfExpression");
        RewriteRuleSubtreeStream stream_equalityOp=new RewriteRuleSubtreeStream(adaptor,"rule equalityOp");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return retval; }
            // JFSL.g:632:3: ( ( instanceOfExpression -> instanceOfExpression ) (op= equalityOp a= instanceOfExpression -> ^( BINARY $op $equalityExpression $a) )* )
            // JFSL.g:632:7: ( instanceOfExpression -> instanceOfExpression ) (op= equalityOp a= instanceOfExpression -> ^( BINARY $op $equalityExpression $a) )*
            {
            // JFSL.g:632:7: ( instanceOfExpression -> instanceOfExpression )
            // JFSL.g:632:8: instanceOfExpression
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression2959);
            instanceOfExpression178=instanceOfExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_instanceOfExpression.add(instanceOfExpression178.getTree());


            // AST REWRITE
            // elements: instanceOfExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 632:29: -> instanceOfExpression
            {
                adaptor.addChild(root_0, stream_instanceOfExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:633:7: (op= equalityOp a= instanceOfExpression -> ^( BINARY $op $equalityExpression $a) )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==183) ) {
                    int LA54_2 = input.LA(2);

                    if ( ((LA54_2>=Identifier && LA54_2<=DecimalLiteral)||LA54_2==145||LA54_2==151||(LA54_2>=169 && LA54_2<=179)||(LA54_2>=183 && LA54_2<=184)||LA54_2==190||(LA54_2>=195 && LA54_2<=197)||LA54_2==200||(LA54_2>=203 && LA54_2<=215)) ) {
                        alt54=1;
                    }


                }
                else if ( (LA54_0==184) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // JFSL.g:633:9: op= equalityOp a= instanceOfExpression
            	    {
            	    pushFollow(FOLLOW_equalityOp_in_equalityExpression2976);
            	    op=equalityOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_equalityOp.add(op.getTree());
            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression2980);
            	    a=instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_instanceOfExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: op, equalityExpression, a
            	    // token labels: 
            	    // rule labels: retval, op, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_op=new RewriteRuleSubtreeStream(adaptor,"rule op",op!=null?op.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 633:46: -> ^( BINARY $op $equalityExpression $a)
            	    {
            	        // JFSL.g:633:49: ^( BINARY $op $equalityExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, stream_op.nextTree());
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop54;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 43, equalityExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "equalityExpression"

    public static class equalityOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "equalityOp"
    // JFSL.g:637:1: equalityOp : ( eqOp | neqOp );
    public final JFSLParser.equalityOp_return equalityOp() throws RecognitionException {
        JFSLParser.equalityOp_return retval = new JFSLParser.equalityOp_return();
        retval.start = input.LT(1);
        int equalityOp_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.eqOp_return eqOp179 = null;

        JFSLParser.neqOp_return neqOp180 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return retval; }
            // JFSL.g:638:3: ( eqOp | neqOp )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==183) ) {
                alt55=1;
            }
            else if ( (LA55_0==184) ) {
                alt55=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // JFSL.g:638:5: eqOp
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_eqOp_in_equalityOp3020);
                    eqOp179=eqOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, eqOp179.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:639:5: neqOp
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_neqOp_in_equalityOp3026);
                    neqOp180=neqOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, neqOp180.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 44, equalityOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "equalityOp"

    public static class eqOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "eqOp"
    // JFSL.g:642:1: eqOp : (x= '=' '=' -> ^( OP_EQ $x) | x= '=' -> ^( OP_EQ $x) );
    public final JFSLParser.eqOp_return eqOp() throws RecognitionException {
        JFSLParser.eqOp_return retval = new JFSLParser.eqOp_return();
        retval.start = input.LT(1);
        int eqOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;
        Token char_literal181=null;

        Node x_tree=null;
        Node char_literal181_tree=null;
        RewriteRuleTokenStream stream_183=new RewriteRuleTokenStream(adaptor,"token 183");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return retval; }
            // JFSL.g:643:3: (x= '=' '=' -> ^( OP_EQ $x) | x= '=' -> ^( OP_EQ $x) )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==183) ) {
                int LA56_1 = input.LA(2);

                if ( (LA56_1==183) ) {
                    alt56=1;
                }
                else if ( (LA56_1==EOF||(LA56_1>=Identifier && LA56_1<=DecimalLiteral)||LA56_1==145||LA56_1==151||(LA56_1>=169 && LA56_1<=179)||LA56_1==184||LA56_1==190||(LA56_1>=195 && LA56_1<=197)||LA56_1==200||(LA56_1>=203 && LA56_1<=215)) ) {
                    alt56=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 56, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // JFSL.g:643:5: x= '=' '='
                    {
                    x=(Token)match(input,183,FOLLOW_183_in_eqOp3045); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_183.add(x);

                    char_literal181=(Token)match(input,183,FOLLOW_183_in_eqOp3047); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_183.add(char_literal181);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 643:15: -> ^( OP_EQ $x)
                    {
                        // JFSL.g:643:18: ^( OP_EQ $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_EQ, "OP_EQ"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:644:5: x= '='
                    {
                    x=(Token)match(input,183,FOLLOW_183_in_eqOp3065); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_183.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 644:15: -> ^( OP_EQ $x)
                    {
                        // JFSL.g:644:18: ^( OP_EQ $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_EQ, "OP_EQ"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 45, eqOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "eqOp"

    public static class neqOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "neqOp"
    // JFSL.g:647:1: neqOp : x= '!' '=' -> ^( OP_NEQ $x) ;
    public final JFSLParser.neqOp_return neqOp() throws RecognitionException {
        JFSLParser.neqOp_return retval = new JFSLParser.neqOp_return();
        retval.start = input.LT(1);
        int neqOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;
        Token char_literal182=null;

        Node x_tree=null;
        Node char_literal182_tree=null;
        RewriteRuleTokenStream stream_183=new RewriteRuleTokenStream(adaptor,"token 183");
        RewriteRuleTokenStream stream_184=new RewriteRuleTokenStream(adaptor,"token 184");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return retval; }
            // JFSL.g:648:3: (x= '!' '=' -> ^( OP_NEQ $x) )
            // JFSL.g:648:5: x= '!' '='
            {
            x=(Token)match(input,184,FOLLOW_184_in_neqOp3093); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_184.add(x);

            char_literal182=(Token)match(input,183,FOLLOW_183_in_neqOp3095); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_183.add(char_literal182);



            // AST REWRITE
            // elements: x
            // token labels: x
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 648:15: -> ^( OP_NEQ $x)
            {
                // JFSL.g:648:18: ^( OP_NEQ $x)
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_NEQ, "OP_NEQ"), root_1);

                adaptor.addChild(root_1, stream_x.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 46, neqOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "neqOp"

    public static class instanceOfExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "instanceOfExpression"
    // JFSL.g:651:1: instanceOfExpression : ( relationalExpression -> relationalExpression ) ( 'instanceof' a= type -> ^( BINARY OP_INSTANCEOF $instanceOfExpression $a) )? ;
    public final JFSLParser.instanceOfExpression_return instanceOfExpression() throws RecognitionException {
        JFSLParser.instanceOfExpression_return retval = new JFSLParser.instanceOfExpression_return();
        retval.start = input.LT(1);
        int instanceOfExpression_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal184=null;
        JFSLParser.type_return a = null;

        JFSLParser.relationalExpression_return relationalExpression183 = null;


        Node string_literal184_tree=null;
        RewriteRuleTokenStream stream_192=new RewriteRuleTokenStream(adaptor,"token 192");
        RewriteRuleSubtreeStream stream_relationalExpression=new RewriteRuleSubtreeStream(adaptor,"rule relationalExpression");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return retval; }
            // JFSL.g:652:3: ( ( relationalExpression -> relationalExpression ) ( 'instanceof' a= type -> ^( BINARY OP_INSTANCEOF $instanceOfExpression $a) )? )
            // JFSL.g:652:7: ( relationalExpression -> relationalExpression ) ( 'instanceof' a= type -> ^( BINARY OP_INSTANCEOF $instanceOfExpression $a) )?
            {
            // JFSL.g:652:7: ( relationalExpression -> relationalExpression )
            // JFSL.g:652:8: relationalExpression
            {
            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression3124);
            relationalExpression183=relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_relationalExpression.add(relationalExpression183.getTree());


            // AST REWRITE
            // elements: relationalExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 652:29: -> relationalExpression
            {
                adaptor.addChild(root_0, stream_relationalExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:653:7: ( 'instanceof' a= type -> ^( BINARY OP_INSTANCEOF $instanceOfExpression $a) )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==192) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // JFSL.g:653:9: 'instanceof' a= type
                    {
                    string_literal184=(Token)match(input,192,FOLLOW_192_in_instanceOfExpression3139); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_192.add(string_literal184);

                    pushFollow(FOLLOW_type_in_instanceOfExpression3143);
                    a=type();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_type.add(a.getTree());


                    // AST REWRITE
                    // elements: instanceOfExpression, a
                    // token labels: 
                    // rule labels: retval, a
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 653:29: -> ^( BINARY OP_INSTANCEOF $instanceOfExpression $a)
                    {
                        // JFSL.g:653:32: ^( BINARY OP_INSTANCEOF $instanceOfExpression $a)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

                        adaptor.addChild(root_1, (Node)adaptor.create(OP_INSTANCEOF, "OP_INSTANCEOF"));
                        adaptor.addChild(root_1, stream_retval.nextTree());
                        adaptor.addChild(root_1, stream_a.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 47, instanceOfExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "instanceOfExpression"

    public static class relationalExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalExpression"
    // JFSL.g:657:1: relationalExpression : ( setUnaryExpression -> setUnaryExpression ) (op= relationalOp a= setUnaryExpression -> ^( BINARY $op $relationalExpression $a) )* ;
    public final JFSLParser.relationalExpression_return relationalExpression() throws RecognitionException {
        JFSLParser.relationalExpression_return retval = new JFSLParser.relationalExpression_return();
        retval.start = input.LT(1);
        int relationalExpression_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.relationalOp_return op = null;

        JFSLParser.setUnaryExpression_return a = null;

        JFSLParser.setUnaryExpression_return setUnaryExpression185 = null;


        RewriteRuleSubtreeStream stream_relationalOp=new RewriteRuleSubtreeStream(adaptor,"rule relationalOp");
        RewriteRuleSubtreeStream stream_setUnaryExpression=new RewriteRuleSubtreeStream(adaptor,"rule setUnaryExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return retval; }
            // JFSL.g:658:3: ( ( setUnaryExpression -> setUnaryExpression ) (op= relationalOp a= setUnaryExpression -> ^( BINARY $op $relationalExpression $a) )* )
            // JFSL.g:658:7: ( setUnaryExpression -> setUnaryExpression ) (op= relationalOp a= setUnaryExpression -> ^( BINARY $op $relationalExpression $a) )*
            {
            // JFSL.g:658:7: ( setUnaryExpression -> setUnaryExpression )
            // JFSL.g:658:8: setUnaryExpression
            {
            pushFollow(FOLLOW_setUnaryExpression_in_relationalExpression3182);
            setUnaryExpression185=setUnaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_setUnaryExpression.add(setUnaryExpression185.getTree());


            // AST REWRITE
            // elements: setUnaryExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 658:27: -> setUnaryExpression
            {
                adaptor.addChild(root_0, stream_setUnaryExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:659:7: (op= relationalOp a= setUnaryExpression -> ^( BINARY $op $relationalExpression $a) )*
            loop58:
            do {
                int alt58=2;
                switch ( input.LA(1) ) {
                case 184:
                    {
                    int LA58_2 = input.LA(2);

                    if ( (LA58_2==193) ) {
                        alt58=1;
                    }


                    }
                    break;
                case 147:
                    {
                    switch ( input.LA(2) ) {
                    case 183:
                        {
                        int LA58_5 = input.LA(3);

                        if ( ((LA58_5>=Identifier && LA58_5<=DecimalLiteral)||LA58_5==145||LA58_5==151||(LA58_5>=169 && LA58_5<=179)||LA58_5==184||LA58_5==190||(LA58_5>=195 && LA58_5<=197)||LA58_5==200||(LA58_5>=203 && LA58_5<=215)) ) {
                            alt58=1;
                        }


                        }
                        break;
                    case 184:
                        {
                        int LA58_6 = input.LA(3);

                        if ( ((LA58_6>=Identifier && LA58_6<=DecimalLiteral)||LA58_6==145||LA58_6==151||(LA58_6>=169 && LA58_6<=179)||LA58_6==184||LA58_6==190||(LA58_6>=195 && LA58_6<=196)||LA58_6==200||(LA58_6>=203 && LA58_6<=210)) ) {
                            alt58=1;
                        }


                        }
                        break;
                    case Identifier:
                    case FloatingPointLiteral:
                    case CharacterLiteral:
                    case StringLiteral:
                    case HexLiteral:
                    case OctalLiteral:
                    case DecimalLiteral:
                    case 145:
                    case 151:
                    case 169:
                    case 170:
                    case 171:
                    case 172:
                    case 173:
                    case 174:
                    case 175:
                    case 176:
                    case 177:
                    case 178:
                    case 179:
                    case 190:
                    case 195:
                    case 196:
                    case 197:
                    case 200:
                    case 203:
                    case 204:
                    case 205:
                    case 206:
                    case 207:
                    case 208:
                    case 209:
                    case 210:
                    case 211:
                    case 212:
                    case 213:
                    case 214:
                    case 215:
                        {
                        alt58=1;
                        }
                        break;

                    }

                    }
                    break;
                case 149:
                case 193:
                    {
                    alt58=1;
                    }
                    break;

                }

                switch (alt58) {
            	case 1 :
            	    // JFSL.g:659:9: op= relationalOp a= setUnaryExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression3199);
            	    op=relationalOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_relationalOp.add(op.getTree());
            	    pushFollow(FOLLOW_setUnaryExpression_in_relationalExpression3203);
            	    a=setUnaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_setUnaryExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: op, relationalExpression, a
            	    // token labels: 
            	    // rule labels: retval, op, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_op=new RewriteRuleSubtreeStream(adaptor,"rule op",op!=null?op.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 659:46: -> ^( BINARY $op $relationalExpression $a)
            	    {
            	        // JFSL.g:659:49: ^( BINARY $op $relationalExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, stream_op.nextTree());
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 48, relationalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalExpression"

    public static class inOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "inOp"
    // JFSL.g:663:1: inOp : x= 'in' -> ^( OP_SET_SUBSET $x) ;
    public final JFSLParser.inOp_return inOp() throws RecognitionException {
        JFSLParser.inOp_return retval = new JFSLParser.inOp_return();
        retval.start = input.LT(1);
        int inOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;

        Node x_tree=null;
        RewriteRuleTokenStream stream_193=new RewriteRuleTokenStream(adaptor,"token 193");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return retval; }
            // JFSL.g:664:3: (x= 'in' -> ^( OP_SET_SUBSET $x) )
            // JFSL.g:664:5: x= 'in'
            {
            x=(Token)match(input,193,FOLLOW_193_in_inOp3244); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_193.add(x);



            // AST REWRITE
            // elements: x
            // token labels: x
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 664:12: -> ^( OP_SET_SUBSET $x)
            {
                // JFSL.g:664:15: ^( OP_SET_SUBSET $x)
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SET_SUBSET, "OP_SET_SUBSET"), root_1);

                adaptor.addChild(root_1, stream_x.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 49, inOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "inOp"

    public static class relationalOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalOp"
    // JFSL.g:667:1: relationalOp : (x= '<' '=' -> ^( OP_LEQ $x) | x= '>' '=' -> ^( OP_GEQ $x) | x= '<' -> ^( OP_LT $x) | x= '>' -> ^( OP_GT $x) | inOp | x= '!' 'in' -> ^( OP_NSET_SUBSET $x) );
    public final JFSLParser.relationalOp_return relationalOp() throws RecognitionException {
        JFSLParser.relationalOp_return retval = new JFSLParser.relationalOp_return();
        retval.start = input.LT(1);
        int relationalOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;
        Token char_literal186=null;
        Token char_literal187=null;
        Token string_literal189=null;
        JFSLParser.inOp_return inOp188 = null;


        Node x_tree=null;
        Node char_literal186_tree=null;
        Node char_literal187_tree=null;
        Node string_literal189_tree=null;
        RewriteRuleTokenStream stream_147=new RewriteRuleTokenStream(adaptor,"token 147");
        RewriteRuleTokenStream stream_183=new RewriteRuleTokenStream(adaptor,"token 183");
        RewriteRuleTokenStream stream_184=new RewriteRuleTokenStream(adaptor,"token 184");
        RewriteRuleTokenStream stream_149=new RewriteRuleTokenStream(adaptor,"token 149");
        RewriteRuleTokenStream stream_193=new RewriteRuleTokenStream(adaptor,"token 193");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }
            // JFSL.g:668:3: (x= '<' '=' -> ^( OP_LEQ $x) | x= '>' '=' -> ^( OP_GEQ $x) | x= '<' -> ^( OP_LT $x) | x= '>' -> ^( OP_GT $x) | inOp | x= '!' 'in' -> ^( OP_NSET_SUBSET $x) )
            int alt59=6;
            switch ( input.LA(1) ) {
            case 147:
                {
                int LA59_1 = input.LA(2);

                if ( (LA59_1==183) ) {
                    alt59=1;
                }
                else if ( ((LA59_1>=Identifier && LA59_1<=DecimalLiteral)||LA59_1==145||LA59_1==151||(LA59_1>=169 && LA59_1<=179)||LA59_1==184||LA59_1==190||(LA59_1>=195 && LA59_1<=197)||LA59_1==200||(LA59_1>=203 && LA59_1<=215)) ) {
                    alt59=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 59, 1, input);

                    throw nvae;
                }
                }
                break;
            case 149:
                {
                int LA59_2 = input.LA(2);

                if ( (LA59_2==183) ) {
                    alt59=2;
                }
                else if ( ((LA59_2>=Identifier && LA59_2<=DecimalLiteral)||LA59_2==145||LA59_2==151||(LA59_2>=169 && LA59_2<=179)||LA59_2==184||LA59_2==190||(LA59_2>=195 && LA59_2<=197)||LA59_2==200||(LA59_2>=203 && LA59_2<=215)) ) {
                    alt59=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 59, 2, input);

                    throw nvae;
                }
                }
                break;
            case 193:
                {
                alt59=5;
                }
                break;
            case 184:
                {
                alt59=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }

            switch (alt59) {
                case 1 :
                    // JFSL.g:668:5: x= '<' '='
                    {
                    x=(Token)match(input,147,FOLLOW_147_in_relationalOp3270); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_147.add(x);

                    char_literal186=(Token)match(input,183,FOLLOW_183_in_relationalOp3272); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_183.add(char_literal186);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 668:16: -> ^( OP_LEQ $x)
                    {
                        // JFSL.g:668:19: ^( OP_LEQ $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_LEQ, "OP_LEQ"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:669:5: x= '>' '='
                    {
                    x=(Token)match(input,149,FOLLOW_149_in_relationalOp3291); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_149.add(x);

                    char_literal187=(Token)match(input,183,FOLLOW_183_in_relationalOp3293); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_183.add(char_literal187);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 669:16: -> ^( OP_GEQ $x)
                    {
                        // JFSL.g:669:19: ^( OP_GEQ $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_GEQ, "OP_GEQ"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:670:5: x= '<'
                    {
                    x=(Token)match(input,147,FOLLOW_147_in_relationalOp3312); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_147.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 670:16: -> ^( OP_LT $x)
                    {
                        // JFSL.g:670:19: ^( OP_LT $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_LT, "OP_LT"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // JFSL.g:671:5: x= '>'
                    {
                    x=(Token)match(input,149,FOLLOW_149_in_relationalOp3335); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_149.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 671:16: -> ^( OP_GT $x)
                    {
                        // JFSL.g:671:19: ^( OP_GT $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_GT, "OP_GT"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // JFSL.g:672:5: inOp
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_inOp_in_relationalOp3356);
                    inOp188=inOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, inOp188.getTree());

                    }
                    break;
                case 6 :
                    // JFSL.g:673:5: x= '!' 'in'
                    {
                    x=(Token)match(input,184,FOLLOW_184_in_relationalOp3365); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_184.add(x);

                    string_literal189=(Token)match(input,193,FOLLOW_193_in_relationalOp3367); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_193.add(string_literal189);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 673:16: -> ^( OP_NSET_SUBSET $x)
                    {
                        // JFSL.g:673:19: ^( OP_NSET_SUBSET $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_NSET_SUBSET, "OP_NSET_SUBSET"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 50, relationalOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalOp"

    public static class setUnaryExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "setUnaryExpression"
    // JFSL.g:676:1: setUnaryExpression : ( setUnaryOp joinExpression -> ^( UNARY setUnaryOp joinExpression ) | shiftExpression );
    public final JFSLParser.setUnaryExpression_return setUnaryExpression() throws RecognitionException {
        JFSLParser.setUnaryExpression_return retval = new JFSLParser.setUnaryExpression_return();
        retval.start = input.LT(1);
        int setUnaryExpression_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.setUnaryOp_return setUnaryOp190 = null;

        JFSLParser.joinExpression_return joinExpression191 = null;

        JFSLParser.shiftExpression_return shiftExpression192 = null;


        RewriteRuleSubtreeStream stream_setUnaryOp=new RewriteRuleSubtreeStream(adaptor,"rule setUnaryOp");
        RewriteRuleSubtreeStream stream_joinExpression=new RewriteRuleSubtreeStream(adaptor,"rule joinExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return retval; }
            // JFSL.g:677:3: ( setUnaryOp joinExpression -> ^( UNARY setUnaryOp joinExpression ) | shiftExpression )
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( ((LA60_0>=211 && LA60_0<=215)) ) {
                alt60=1;
            }
            else if ( ((LA60_0>=Identifier && LA60_0<=DecimalLiteral)||LA60_0==145||LA60_0==151||(LA60_0>=169 && LA60_0<=179)||LA60_0==184||LA60_0==190||(LA60_0>=195 && LA60_0<=197)||LA60_0==200||(LA60_0>=203 && LA60_0<=210)) ) {
                alt60=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 60, 0, input);

                throw nvae;
            }
            switch (alt60) {
                case 1 :
                    // JFSL.g:677:5: setUnaryOp joinExpression
                    {
                    pushFollow(FOLLOW_setUnaryOp_in_setUnaryExpression3391);
                    setUnaryOp190=setUnaryOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_setUnaryOp.add(setUnaryOp190.getTree());
                    pushFollow(FOLLOW_joinExpression_in_setUnaryExpression3393);
                    joinExpression191=joinExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_joinExpression.add(joinExpression191.getTree());


                    // AST REWRITE
                    // elements: setUnaryOp, joinExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 677:31: -> ^( UNARY setUnaryOp joinExpression )
                    {
                        // JFSL.g:677:34: ^( UNARY setUnaryOp joinExpression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(UNARY, "UNARY"), root_1);

                        adaptor.addChild(root_1, stream_setUnaryOp.nextTree());
                        adaptor.addChild(root_1, stream_joinExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:678:5: shiftExpression
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_shiftExpression_in_setUnaryExpression3409);
                    shiftExpression192=shiftExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, shiftExpression192.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 51, setUnaryExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "setUnaryExpression"

    public static class shiftExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftExpression"
    // JFSL.g:681:1: shiftExpression : ( rangeExpression -> rangeExpression ) (op= shiftOp a= rangeExpression -> ^( BINARY $op $shiftExpression $a) )* ;
    public final JFSLParser.shiftExpression_return shiftExpression() throws RecognitionException {
        JFSLParser.shiftExpression_return retval = new JFSLParser.shiftExpression_return();
        retval.start = input.LT(1);
        int shiftExpression_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.shiftOp_return op = null;

        JFSLParser.rangeExpression_return a = null;

        JFSLParser.rangeExpression_return rangeExpression193 = null;


        RewriteRuleSubtreeStream stream_rangeExpression=new RewriteRuleSubtreeStream(adaptor,"rule rangeExpression");
        RewriteRuleSubtreeStream stream_shiftOp=new RewriteRuleSubtreeStream(adaptor,"rule shiftOp");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }
            // JFSL.g:682:3: ( ( rangeExpression -> rangeExpression ) (op= shiftOp a= rangeExpression -> ^( BINARY $op $shiftExpression $a) )* )
            // JFSL.g:682:7: ( rangeExpression -> rangeExpression ) (op= shiftOp a= rangeExpression -> ^( BINARY $op $shiftExpression $a) )*
            {
            // JFSL.g:682:7: ( rangeExpression -> rangeExpression )
            // JFSL.g:682:8: rangeExpression
            {
            pushFollow(FOLLOW_rangeExpression_in_shiftExpression3427);
            rangeExpression193=rangeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rangeExpression.add(rangeExpression193.getTree());


            // AST REWRITE
            // elements: rangeExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 682:24: -> rangeExpression
            {
                adaptor.addChild(root_0, stream_rangeExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:683:7: (op= shiftOp a= rangeExpression -> ^( BINARY $op $shiftExpression $a) )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==147) ) {
                    int LA61_1 = input.LA(2);

                    if ( (LA61_1==147) ) {
                        alt61=1;
                    }


                }
                else if ( (LA61_0==149) ) {
                    int LA61_2 = input.LA(2);

                    if ( (LA61_2==149) ) {
                        alt61=1;
                    }


                }


                switch (alt61) {
            	case 1 :
            	    // JFSL.g:683:9: op= shiftOp a= rangeExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression3444);
            	    op=shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_shiftOp.add(op.getTree());
            	    pushFollow(FOLLOW_rangeExpression_in_shiftExpression3448);
            	    a=rangeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rangeExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: op, shiftExpression, a
            	    // token labels: 
            	    // rule labels: retval, op, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_op=new RewriteRuleSubtreeStream(adaptor,"rule op",op!=null?op.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 683:38: -> ^( BINARY $op $shiftExpression $a)
            	    {
            	        // JFSL.g:683:41: ^( BINARY $op $shiftExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, stream_op.nextTree());
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop61;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 52, shiftExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "shiftExpression"

    public static class shiftOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "shiftOp"
    // JFSL.g:687:1: shiftOp : (x= '<' '<' -> ^( OP_SHL $x) | x= '>' '>' '>' -> ^( OP_USHR $x) | x= '>' '>' -> ^( OP_SHR $x) );
    public final JFSLParser.shiftOp_return shiftOp() throws RecognitionException {
        JFSLParser.shiftOp_return retval = new JFSLParser.shiftOp_return();
        retval.start = input.LT(1);
        int shiftOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;
        Token char_literal194=null;
        Token char_literal195=null;
        Token char_literal196=null;
        Token char_literal197=null;

        Node x_tree=null;
        Node char_literal194_tree=null;
        Node char_literal195_tree=null;
        Node char_literal196_tree=null;
        Node char_literal197_tree=null;
        RewriteRuleTokenStream stream_147=new RewriteRuleTokenStream(adaptor,"token 147");
        RewriteRuleTokenStream stream_149=new RewriteRuleTokenStream(adaptor,"token 149");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return retval; }
            // JFSL.g:688:3: (x= '<' '<' -> ^( OP_SHL $x) | x= '>' '>' '>' -> ^( OP_USHR $x) | x= '>' '>' -> ^( OP_SHR $x) )
            int alt62=3;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==147) ) {
                alt62=1;
            }
            else if ( (LA62_0==149) ) {
                int LA62_2 = input.LA(2);

                if ( (LA62_2==149) ) {
                    int LA62_3 = input.LA(3);

                    if ( (LA62_3==149) ) {
                        alt62=2;
                    }
                    else if ( ((LA62_3>=Identifier && LA62_3<=DecimalLiteral)||LA62_3==145||LA62_3==151||(LA62_3>=169 && LA62_3<=179)||LA62_3==184||LA62_3==190||(LA62_3>=195 && LA62_3<=197)||LA62_3==200||(LA62_3>=203 && LA62_3<=210)) ) {
                        alt62=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 62, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 62, 0, input);

                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // JFSL.g:688:5: x= '<' '<'
                    {
                    x=(Token)match(input,147,FOLLOW_147_in_shiftOp3487); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_147.add(x);

                    char_literal194=(Token)match(input,147,FOLLOW_147_in_shiftOp3489); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_147.add(char_literal194);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 688:19: -> ^( OP_SHL $x)
                    {
                        // JFSL.g:688:22: ^( OP_SHL $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SHL, "OP_SHL"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:689:5: x= '>' '>' '>'
                    {
                    x=(Token)match(input,149,FOLLOW_149_in_shiftOp3511); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_149.add(x);

                    char_literal195=(Token)match(input,149,FOLLOW_149_in_shiftOp3513); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_149.add(char_literal195);

                    char_literal196=(Token)match(input,149,FOLLOW_149_in_shiftOp3515); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_149.add(char_literal196);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 689:19: -> ^( OP_USHR $x)
                    {
                        // JFSL.g:689:22: ^( OP_USHR $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_USHR, "OP_USHR"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:690:5: x= '>' '>'
                    {
                    x=(Token)match(input,149,FOLLOW_149_in_shiftOp3533); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_149.add(x);

                    char_literal197=(Token)match(input,149,FOLLOW_149_in_shiftOp3535); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_149.add(char_literal197);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 690:19: -> ^( OP_SHR $x)
                    {
                        // JFSL.g:690:22: ^( OP_SHR $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SHR, "OP_SHR"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 53, shiftOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "shiftOp"

    public static class rangeExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rangeExpression"
    // JFSL.g:693:1: rangeExpression : ( additiveExpression -> additiveExpression ) ( '...' a= additiveExpression -> ^( BINARY OP_RANGE $rangeExpression $a) )* ;
    public final JFSLParser.rangeExpression_return rangeExpression() throws RecognitionException {
        JFSLParser.rangeExpression_return retval = new JFSLParser.rangeExpression_return();
        retval.start = input.LT(1);
        int rangeExpression_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal199=null;
        JFSLParser.additiveExpression_return a = null;

        JFSLParser.additiveExpression_return additiveExpression198 = null;


        Node string_literal199_tree=null;
        RewriteRuleTokenStream stream_194=new RewriteRuleTokenStream(adaptor,"token 194");
        RewriteRuleSubtreeStream stream_additiveExpression=new RewriteRuleSubtreeStream(adaptor,"rule additiveExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return retval; }
            // JFSL.g:694:3: ( ( additiveExpression -> additiveExpression ) ( '...' a= additiveExpression -> ^( BINARY OP_RANGE $rangeExpression $a) )* )
            // JFSL.g:694:5: ( additiveExpression -> additiveExpression ) ( '...' a= additiveExpression -> ^( BINARY OP_RANGE $rangeExpression $a) )*
            {
            // JFSL.g:694:5: ( additiveExpression -> additiveExpression )
            // JFSL.g:694:6: additiveExpression
            {
            pushFollow(FOLLOW_additiveExpression_in_rangeExpression3564);
            additiveExpression198=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_additiveExpression.add(additiveExpression198.getTree());


            // AST REWRITE
            // elements: additiveExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 694:25: -> additiveExpression
            {
                adaptor.addChild(root_0, stream_additiveExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:695:5: ( '...' a= additiveExpression -> ^( BINARY OP_RANGE $rangeExpression $a) )*
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==194) ) {
                    alt63=1;
                }


                switch (alt63) {
            	case 1 :
            	    // JFSL.g:695:6: '...' a= additiveExpression
            	    {
            	    string_literal199=(Token)match(input,194,FOLLOW_194_in_rangeExpression3576); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_194.add(string_literal199);

            	    pushFollow(FOLLOW_additiveExpression_in_rangeExpression3580);
            	    a=additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_additiveExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: a, rangeExpression
            	    // token labels: 
            	    // rule labels: retval, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 695:33: -> ^( BINARY OP_RANGE $rangeExpression $a)
            	    {
            	        // JFSL.g:695:36: ^( BINARY OP_RANGE $rangeExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, (Node)adaptor.create(OP_RANGE, "OP_RANGE"));
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop63;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 54, rangeExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "rangeExpression"

    public static class additiveExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "additiveExpression"
    // JFSL.g:699:1: additiveExpression : ( sizeExpression -> sizeExpression ) (op= additiveOp a= sizeExpression -> ^( BINARY $op $additiveExpression $a) )* ;
    public final JFSLParser.additiveExpression_return additiveExpression() throws RecognitionException {
        JFSLParser.additiveExpression_return retval = new JFSLParser.additiveExpression_return();
        retval.start = input.LT(1);
        int additiveExpression_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.additiveOp_return op = null;

        JFSLParser.sizeExpression_return a = null;

        JFSLParser.sizeExpression_return sizeExpression200 = null;


        RewriteRuleSubtreeStream stream_sizeExpression=new RewriteRuleSubtreeStream(adaptor,"rule sizeExpression");
        RewriteRuleSubtreeStream stream_additiveOp=new RewriteRuleSubtreeStream(adaptor,"rule additiveOp");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return retval; }
            // JFSL.g:700:3: ( ( sizeExpression -> sizeExpression ) (op= additiveOp a= sizeExpression -> ^( BINARY $op $additiveExpression $a) )* )
            // JFSL.g:700:7: ( sizeExpression -> sizeExpression ) (op= additiveOp a= sizeExpression -> ^( BINARY $op $additiveExpression $a) )*
            {
            // JFSL.g:700:7: ( sizeExpression -> sizeExpression )
            // JFSL.g:700:8: sizeExpression
            {
            pushFollow(FOLLOW_sizeExpression_in_additiveExpression3618);
            sizeExpression200=sizeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sizeExpression.add(sizeExpression200.getTree());


            // AST REWRITE
            // elements: sizeExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 700:23: -> sizeExpression
            {
                adaptor.addChild(root_0, stream_sizeExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:701:7: (op= additiveOp a= sizeExpression -> ^( BINARY $op $additiveExpression $a) )*
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( ((LA64_0>=195 && LA64_0<=196)) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // JFSL.g:701:9: op= additiveOp a= sizeExpression
            	    {
            	    pushFollow(FOLLOW_additiveOp_in_additiveExpression3636);
            	    op=additiveOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_additiveOp.add(op.getTree());
            	    pushFollow(FOLLOW_sizeExpression_in_additiveExpression3640);
            	    a=sizeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_sizeExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: op, a, additiveExpression
            	    // token labels: 
            	    // rule labels: retval, op, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_op=new RewriteRuleSubtreeStream(adaptor,"rule op",op!=null?op.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 701:40: -> ^( BINARY $op $additiveExpression $a)
            	    {
            	        // JFSL.g:701:43: ^( BINARY $op $additiveExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, stream_op.nextTree());
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop64;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 55, additiveExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "additiveExpression"

    public static class additiveOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "additiveOp"
    // JFSL.g:705:1: additiveOp : (x= '+' -> ^( OP_PLUS_OR_UNION $x) | x= '-' -> ^( OP_MINUS_OR_DIFFERENCE $x) );
    public final JFSLParser.additiveOp_return additiveOp() throws RecognitionException {
        JFSLParser.additiveOp_return retval = new JFSLParser.additiveOp_return();
        retval.start = input.LT(1);
        int additiveOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;

        Node x_tree=null;
        RewriteRuleTokenStream stream_195=new RewriteRuleTokenStream(adaptor,"token 195");
        RewriteRuleTokenStream stream_196=new RewriteRuleTokenStream(adaptor,"token 196");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return retval; }
            // JFSL.g:706:3: (x= '+' -> ^( OP_PLUS_OR_UNION $x) | x= '-' -> ^( OP_MINUS_OR_DIFFERENCE $x) )
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==195) ) {
                alt65=1;
            }
            else if ( (LA65_0==196) ) {
                alt65=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 65, 0, input);

                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // JFSL.g:706:5: x= '+'
                    {
                    x=(Token)match(input,195,FOLLOW_195_in_additiveOp3683); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_195.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 706:13: -> ^( OP_PLUS_OR_UNION $x)
                    {
                        // JFSL.g:706:16: ^( OP_PLUS_OR_UNION $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_PLUS_OR_UNION, "OP_PLUS_OR_UNION"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:707:5: x= '-'
                    {
                    x=(Token)match(input,196,FOLLOW_196_in_additiveOp3703); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_196.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 707:13: -> ^( OP_MINUS_OR_DIFFERENCE $x)
                    {
                        // JFSL.g:707:16: ^( OP_MINUS_OR_DIFFERENCE $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_MINUS_OR_DIFFERENCE, "OP_MINUS_OR_DIFFERENCE"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 56, additiveOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "additiveOp"

    public static class sizeExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sizeExpression"
    // JFSL.g:710:1: sizeExpression : ( '#' joinExpression -> ^( UNARY OP_SET_NUM joinExpression ) | multiplicativeExpression );
    public final JFSLParser.sizeExpression_return sizeExpression() throws RecognitionException {
        JFSLParser.sizeExpression_return retval = new JFSLParser.sizeExpression_return();
        retval.start = input.LT(1);
        int sizeExpression_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal201=null;
        JFSLParser.joinExpression_return joinExpression202 = null;

        JFSLParser.multiplicativeExpression_return multiplicativeExpression203 = null;


        Node char_literal201_tree=null;
        RewriteRuleTokenStream stream_197=new RewriteRuleTokenStream(adaptor,"token 197");
        RewriteRuleSubtreeStream stream_joinExpression=new RewriteRuleSubtreeStream(adaptor,"rule joinExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return retval; }
            // JFSL.g:711:3: ( '#' joinExpression -> ^( UNARY OP_SET_NUM joinExpression ) | multiplicativeExpression )
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==197) ) {
                alt66=1;
            }
            else if ( ((LA66_0>=Identifier && LA66_0<=DecimalLiteral)||LA66_0==145||LA66_0==151||(LA66_0>=169 && LA66_0<=179)||LA66_0==184||LA66_0==190||(LA66_0>=195 && LA66_0<=196)||LA66_0==200||(LA66_0>=203 && LA66_0<=210)) ) {
                alt66=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // JFSL.g:711:5: '#' joinExpression
                    {
                    char_literal201=(Token)match(input,197,FOLLOW_197_in_sizeExpression3727); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_197.add(char_literal201);

                    pushFollow(FOLLOW_joinExpression_in_sizeExpression3729);
                    joinExpression202=joinExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_joinExpression.add(joinExpression202.getTree());


                    // AST REWRITE
                    // elements: joinExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 711:24: -> ^( UNARY OP_SET_NUM joinExpression )
                    {
                        // JFSL.g:711:27: ^( UNARY OP_SET_NUM joinExpression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(UNARY, "UNARY"), root_1);

                        adaptor.addChild(root_1, (Node)adaptor.create(OP_SET_NUM, "OP_SET_NUM"));
                        adaptor.addChild(root_1, stream_joinExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:712:5: multiplicativeExpression
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_multiplicativeExpression_in_sizeExpression3745);
                    multiplicativeExpression203=multiplicativeExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, multiplicativeExpression203.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 57, sizeExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "sizeExpression"

    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multiplicativeExpression"
    // JFSL.g:715:1: multiplicativeExpression : ( setAdditiveExpression -> setAdditiveExpression ) (op= multOp a= setAdditiveExpression -> ^( BINARY $op $multiplicativeExpression $a) )* ;
    public final JFSLParser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        JFSLParser.multiplicativeExpression_return retval = new JFSLParser.multiplicativeExpression_return();
        retval.start = input.LT(1);
        int multiplicativeExpression_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.multOp_return op = null;

        JFSLParser.setAdditiveExpression_return a = null;

        JFSLParser.setAdditiveExpression_return setAdditiveExpression204 = null;


        RewriteRuleSubtreeStream stream_multOp=new RewriteRuleSubtreeStream(adaptor,"rule multOp");
        RewriteRuleSubtreeStream stream_setAdditiveExpression=new RewriteRuleSubtreeStream(adaptor,"rule setAdditiveExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return retval; }
            // JFSL.g:716:3: ( ( setAdditiveExpression -> setAdditiveExpression ) (op= multOp a= setAdditiveExpression -> ^( BINARY $op $multiplicativeExpression $a) )* )
            // JFSL.g:716:7: ( setAdditiveExpression -> setAdditiveExpression ) (op= multOp a= setAdditiveExpression -> ^( BINARY $op $multiplicativeExpression $a) )*
            {
            // JFSL.g:716:7: ( setAdditiveExpression -> setAdditiveExpression )
            // JFSL.g:716:8: setAdditiveExpression
            {
            pushFollow(FOLLOW_setAdditiveExpression_in_multiplicativeExpression3763);
            setAdditiveExpression204=setAdditiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_setAdditiveExpression.add(setAdditiveExpression204.getTree());


            // AST REWRITE
            // elements: setAdditiveExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 716:30: -> setAdditiveExpression
            {
                adaptor.addChild(root_0, stream_setAdditiveExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:717:7: (op= multOp a= setAdditiveExpression -> ^( BINARY $op $multiplicativeExpression $a) )*
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( (LA67_0==168||(LA67_0>=198 && LA67_0<=199)) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // JFSL.g:717:9: op= multOp a= setAdditiveExpression
            	    {
            	    pushFollow(FOLLOW_multOp_in_multiplicativeExpression3780);
            	    op=multOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_multOp.add(op.getTree());
            	    pushFollow(FOLLOW_setAdditiveExpression_in_multiplicativeExpression3784);
            	    a=setAdditiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_setAdditiveExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: multiplicativeExpression, op, a
            	    // token labels: 
            	    // rule labels: retval, op, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_op=new RewriteRuleSubtreeStream(adaptor,"rule op",op!=null?op.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 717:43: -> ^( BINARY $op $multiplicativeExpression $a)
            	    {
            	        // JFSL.g:717:46: ^( BINARY $op $multiplicativeExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, stream_op.nextTree());
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop67;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 58, multiplicativeExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "multiplicativeExpression"

    public static class multOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "multOp"
    // JFSL.g:720:1: multOp : (x= '*' -> ^( OP_TIMES $x) | x= '/' -> ^( OP_DIVIDE $x) | x= '%' -> ^( OP_MOD $x) );
    public final JFSLParser.multOp_return multOp() throws RecognitionException {
        JFSLParser.multOp_return retval = new JFSLParser.multOp_return();
        retval.start = input.LT(1);
        int multOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;

        Node x_tree=null;
        RewriteRuleTokenStream stream_198=new RewriteRuleTokenStream(adaptor,"token 198");
        RewriteRuleTokenStream stream_199=new RewriteRuleTokenStream(adaptor,"token 199");
        RewriteRuleTokenStream stream_168=new RewriteRuleTokenStream(adaptor,"token 168");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return retval; }
            // JFSL.g:721:3: (x= '*' -> ^( OP_TIMES $x) | x= '/' -> ^( OP_DIVIDE $x) | x= '%' -> ^( OP_MOD $x) )
            int alt68=3;
            switch ( input.LA(1) ) {
            case 168:
                {
                alt68=1;
                }
                break;
            case 198:
                {
                alt68=2;
                }
                break;
            case 199:
                {
                alt68=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 68, 0, input);

                throw nvae;
            }

            switch (alt68) {
                case 1 :
                    // JFSL.g:721:5: x= '*'
                    {
                    x=(Token)match(input,168,FOLLOW_168_in_multOp3816); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_168.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 721:13: -> ^( OP_TIMES $x)
                    {
                        // JFSL.g:721:16: ^( OP_TIMES $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_TIMES, "OP_TIMES"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:722:5: x= '/'
                    {
                    x=(Token)match(input,198,FOLLOW_198_in_multOp3836); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_198.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 722:13: -> ^( OP_DIVIDE $x)
                    {
                        // JFSL.g:722:16: ^( OP_DIVIDE $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_DIVIDE, "OP_DIVIDE"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:723:5: x= '%'
                    {
                    x=(Token)match(input,199,FOLLOW_199_in_multOp3856); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_199.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 723:13: -> ^( OP_MOD $x)
                    {
                        // JFSL.g:723:16: ^( OP_MOD $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_MOD, "OP_MOD"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 59, multOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "multOp"

    public static class setAdditiveExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "setAdditiveExpression"
    // JFSL.g:726:1: setAdditiveExpression : ( overrideExpression -> overrideExpression ) (op= setAdditiveOp a= overrideExpression -> ^( BINARY $op $setAdditiveExpression $a) )* ;
    public final JFSLParser.setAdditiveExpression_return setAdditiveExpression() throws RecognitionException {
        JFSLParser.setAdditiveExpression_return retval = new JFSLParser.setAdditiveExpression_return();
        retval.start = input.LT(1);
        int setAdditiveExpression_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.setAdditiveOp_return op = null;

        JFSLParser.overrideExpression_return a = null;

        JFSLParser.overrideExpression_return overrideExpression205 = null;


        RewriteRuleSubtreeStream stream_overrideExpression=new RewriteRuleSubtreeStream(adaptor,"rule overrideExpression");
        RewriteRuleSubtreeStream stream_setAdditiveOp=new RewriteRuleSubtreeStream(adaptor,"rule setAdditiveOp");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return retval; }
            // JFSL.g:727:3: ( ( overrideExpression -> overrideExpression ) (op= setAdditiveOp a= overrideExpression -> ^( BINARY $op $setAdditiveExpression $a) )* )
            // JFSL.g:727:6: ( overrideExpression -> overrideExpression ) (op= setAdditiveOp a= overrideExpression -> ^( BINARY $op $setAdditiveExpression $a) )*
            {
            // JFSL.g:727:6: ( overrideExpression -> overrideExpression )
            // JFSL.g:727:7: overrideExpression
            {
            pushFollow(FOLLOW_overrideExpression_in_setAdditiveExpression3884);
            overrideExpression205=overrideExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_overrideExpression.add(overrideExpression205.getTree());


            // AST REWRITE
            // elements: overrideExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 727:26: -> overrideExpression
            {
                adaptor.addChild(root_0, stream_overrideExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:728:9: (op= setAdditiveOp a= overrideExpression -> ^( BINARY $op $setAdditiveExpression $a) )*
            loop69:
            do {
                int alt69=2;
                int LA69_0 = input.LA(1);

                if ( (LA69_0==200) ) {
                    alt69=1;
                }


                switch (alt69) {
            	case 1 :
            	    // JFSL.g:728:11: op= setAdditiveOp a= overrideExpression
            	    {
            	    pushFollow(FOLLOW_setAdditiveOp_in_setAdditiveExpression3904);
            	    op=setAdditiveOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_setAdditiveOp.add(op.getTree());
            	    pushFollow(FOLLOW_overrideExpression_in_setAdditiveExpression3908);
            	    a=overrideExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_overrideExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: a, setAdditiveExpression, op
            	    // token labels: 
            	    // rule labels: retval, op, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_op=new RewriteRuleSubtreeStream(adaptor,"rule op",op!=null?op.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 728:49: -> ^( BINARY $op $setAdditiveExpression $a)
            	    {
            	        // JFSL.g:728:52: ^( BINARY $op $setAdditiveExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, stream_op.nextTree());
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop69;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 60, setAdditiveExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "setAdditiveExpression"

    public static class setAdditiveOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "setAdditiveOp"
    // JFSL.g:732:1: setAdditiveOp : (x= '@' '+' -> ^( OP_UNION $x) | x= '@' '-' -> ^( OP_DIFFERENCE $x) );
    public final JFSLParser.setAdditiveOp_return setAdditiveOp() throws RecognitionException {
        JFSLParser.setAdditiveOp_return retval = new JFSLParser.setAdditiveOp_return();
        retval.start = input.LT(1);
        int setAdditiveOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;
        Token char_literal206=null;
        Token char_literal207=null;

        Node x_tree=null;
        Node char_literal206_tree=null;
        Node char_literal207_tree=null;
        RewriteRuleTokenStream stream_200=new RewriteRuleTokenStream(adaptor,"token 200");
        RewriteRuleTokenStream stream_195=new RewriteRuleTokenStream(adaptor,"token 195");
        RewriteRuleTokenStream stream_196=new RewriteRuleTokenStream(adaptor,"token 196");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return retval; }
            // JFSL.g:733:3: (x= '@' '+' -> ^( OP_UNION $x) | x= '@' '-' -> ^( OP_DIFFERENCE $x) )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==200) ) {
                int LA70_1 = input.LA(2);

                if ( (LA70_1==195) ) {
                    alt70=1;
                }
                else if ( (LA70_1==196) ) {
                    alt70=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 70, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // JFSL.g:733:5: x= '@' '+'
                    {
                    x=(Token)match(input,200,FOLLOW_200_in_setAdditiveOp3952); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_200.add(x);

                    char_literal206=(Token)match(input,195,FOLLOW_195_in_setAdditiveOp3954); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_195.add(char_literal206);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 733:15: -> ^( OP_UNION $x)
                    {
                        // JFSL.g:733:18: ^( OP_UNION $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_UNION, "OP_UNION"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:734:5: x= '@' '-'
                    {
                    x=(Token)match(input,200,FOLLOW_200_in_setAdditiveOp3972); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_200.add(x);

                    char_literal207=(Token)match(input,196,FOLLOW_196_in_setAdditiveOp3974); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_196.add(char_literal207);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 734:15: -> ^( OP_DIFFERENCE $x)
                    {
                        // JFSL.g:734:18: ^( OP_DIFFERENCE $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_DIFFERENCE, "OP_DIFFERENCE"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 61, setAdditiveOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "setAdditiveOp"

    public static class overrideExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "overrideExpression"
    // JFSL.g:737:1: overrideExpression : ( intersectionExpression -> intersectionExpression ) ( '++' a= intersectionExpression -> ^( BINARY OP_RELATIONAL_OVERRIDE $overrideExpression $a) )* ;
    public final JFSLParser.overrideExpression_return overrideExpression() throws RecognitionException {
        JFSLParser.overrideExpression_return retval = new JFSLParser.overrideExpression_return();
        retval.start = input.LT(1);
        int overrideExpression_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal209=null;
        JFSLParser.intersectionExpression_return a = null;

        JFSLParser.intersectionExpression_return intersectionExpression208 = null;


        Node string_literal209_tree=null;
        RewriteRuleTokenStream stream_201=new RewriteRuleTokenStream(adaptor,"token 201");
        RewriteRuleSubtreeStream stream_intersectionExpression=new RewriteRuleSubtreeStream(adaptor,"rule intersectionExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return retval; }
            // JFSL.g:738:3: ( ( intersectionExpression -> intersectionExpression ) ( '++' a= intersectionExpression -> ^( BINARY OP_RELATIONAL_OVERRIDE $overrideExpression $a) )* )
            // JFSL.g:738:6: ( intersectionExpression -> intersectionExpression ) ( '++' a= intersectionExpression -> ^( BINARY OP_RELATIONAL_OVERRIDE $overrideExpression $a) )*
            {
            // JFSL.g:738:6: ( intersectionExpression -> intersectionExpression )
            // JFSL.g:738:7: intersectionExpression
            {
            pushFollow(FOLLOW_intersectionExpression_in_overrideExpression4002);
            intersectionExpression208=intersectionExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_intersectionExpression.add(intersectionExpression208.getTree());


            // AST REWRITE
            // elements: intersectionExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 738:30: -> intersectionExpression
            {
                adaptor.addChild(root_0, stream_intersectionExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:739:7: ( '++' a= intersectionExpression -> ^( BINARY OP_RELATIONAL_OVERRIDE $overrideExpression $a) )*
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);

                if ( (LA71_0==201) ) {
                    alt71=1;
                }


                switch (alt71) {
            	case 1 :
            	    // JFSL.g:739:9: '++' a= intersectionExpression
            	    {
            	    string_literal209=(Token)match(input,201,FOLLOW_201_in_overrideExpression4017); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_201.add(string_literal209);

            	    pushFollow(FOLLOW_intersectionExpression_in_overrideExpression4021);
            	    a=intersectionExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_intersectionExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: overrideExpression, a
            	    // token labels: 
            	    // rule labels: retval, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 739:39: -> ^( BINARY OP_RELATIONAL_OVERRIDE $overrideExpression $a)
            	    {
            	        // JFSL.g:739:42: ^( BINARY OP_RELATIONAL_OVERRIDE $overrideExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, (Node)adaptor.create(OP_RELATIONAL_OVERRIDE, "OP_RELATIONAL_OVERRIDE"));
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop71;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 62, overrideExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "overrideExpression"

    public static class intersectionExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "intersectionExpression"
    // JFSL.g:743:1: intersectionExpression : ( composeExpression -> composeExpression ) ( '@' '&' a= composeExpression -> ^( BINARY OP_INTERSECTION $intersectionExpression $a) )* ;
    public final JFSLParser.intersectionExpression_return intersectionExpression() throws RecognitionException {
        JFSLParser.intersectionExpression_return retval = new JFSLParser.intersectionExpression_return();
        retval.start = input.LT(1);
        int intersectionExpression_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal211=null;
        Token char_literal212=null;
        JFSLParser.composeExpression_return a = null;

        JFSLParser.composeExpression_return composeExpression210 = null;


        Node char_literal211_tree=null;
        Node char_literal212_tree=null;
        RewriteRuleTokenStream stream_200=new RewriteRuleTokenStream(adaptor,"token 200");
        RewriteRuleTokenStream stream_191=new RewriteRuleTokenStream(adaptor,"token 191");
        RewriteRuleSubtreeStream stream_composeExpression=new RewriteRuleSubtreeStream(adaptor,"rule composeExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return retval; }
            // JFSL.g:744:3: ( ( composeExpression -> composeExpression ) ( '@' '&' a= composeExpression -> ^( BINARY OP_INTERSECTION $intersectionExpression $a) )* )
            // JFSL.g:744:6: ( composeExpression -> composeExpression ) ( '@' '&' a= composeExpression -> ^( BINARY OP_INTERSECTION $intersectionExpression $a) )*
            {
            // JFSL.g:744:6: ( composeExpression -> composeExpression )
            // JFSL.g:744:7: composeExpression
            {
            pushFollow(FOLLOW_composeExpression_in_intersectionExpression4065);
            composeExpression210=composeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_composeExpression.add(composeExpression210.getTree());


            // AST REWRITE
            // elements: composeExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 744:25: -> composeExpression
            {
                adaptor.addChild(root_0, stream_composeExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:745:7: ( '@' '&' a= composeExpression -> ^( BINARY OP_INTERSECTION $intersectionExpression $a) )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==200) ) {
                    int LA72_2 = input.LA(2);

                    if ( (LA72_2==191) ) {
                        alt72=1;
                    }


                }


                switch (alt72) {
            	case 1 :
            	    // JFSL.g:745:9: '@' '&' a= composeExpression
            	    {
            	    char_literal211=(Token)match(input,200,FOLLOW_200_in_intersectionExpression4080); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_200.add(char_literal211);

            	    char_literal212=(Token)match(input,191,FOLLOW_191_in_intersectionExpression4082); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_191.add(char_literal212);

            	    pushFollow(FOLLOW_composeExpression_in_intersectionExpression4086);
            	    a=composeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_composeExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: a, intersectionExpression
            	    // token labels: 
            	    // rule labels: retval, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 745:37: -> ^( BINARY OP_INTERSECTION $intersectionExpression $a)
            	    {
            	        // JFSL.g:745:40: ^( BINARY OP_INTERSECTION $intersectionExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, (Node)adaptor.create(OP_INTERSECTION, "OP_INTERSECTION"));
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop72;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 63, intersectionExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "intersectionExpression"

    public static class composeExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "composeExpression"
    // JFSL.g:749:1: composeExpression : ( unaryExpression -> unaryExpression ) ( '->' a= unaryExpression -> ^( BINARY OP_RELATIONAL_COMPOSE $composeExpression $a) )* ;
    public final JFSLParser.composeExpression_return composeExpression() throws RecognitionException {
        JFSLParser.composeExpression_return retval = new JFSLParser.composeExpression_return();
        retval.start = input.LT(1);
        int composeExpression_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal214=null;
        JFSLParser.unaryExpression_return a = null;

        JFSLParser.unaryExpression_return unaryExpression213 = null;


        Node string_literal214_tree=null;
        RewriteRuleTokenStream stream_202=new RewriteRuleTokenStream(adaptor,"token 202");
        RewriteRuleSubtreeStream stream_unaryExpression=new RewriteRuleSubtreeStream(adaptor,"rule unaryExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return retval; }
            // JFSL.g:750:3: ( ( unaryExpression -> unaryExpression ) ( '->' a= unaryExpression -> ^( BINARY OP_RELATIONAL_COMPOSE $composeExpression $a) )* )
            // JFSL.g:750:6: ( unaryExpression -> unaryExpression ) ( '->' a= unaryExpression -> ^( BINARY OP_RELATIONAL_COMPOSE $composeExpression $a) )*
            {
            // JFSL.g:750:6: ( unaryExpression -> unaryExpression )
            // JFSL.g:750:7: unaryExpression
            {
            pushFollow(FOLLOW_unaryExpression_in_composeExpression4126);
            unaryExpression213=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_unaryExpression.add(unaryExpression213.getTree());


            // AST REWRITE
            // elements: unaryExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 750:23: -> unaryExpression
            {
                adaptor.addChild(root_0, stream_unaryExpression.nextTree());

            }

            retval.tree = root_0;}
            }

            // JFSL.g:751:7: ( '->' a= unaryExpression -> ^( BINARY OP_RELATIONAL_COMPOSE $composeExpression $a) )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);

                if ( (LA73_0==202) ) {
                    alt73=1;
                }


                switch (alt73) {
            	case 1 :
            	    // JFSL.g:751:9: '->' a= unaryExpression
            	    {
            	    string_literal214=(Token)match(input,202,FOLLOW_202_in_composeExpression4141); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_202.add(string_literal214);

            	    pushFollow(FOLLOW_unaryExpression_in_composeExpression4145);
            	    a=unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_unaryExpression.add(a.getTree());


            	    // AST REWRITE
            	    // elements: a, composeExpression
            	    // token labels: 
            	    // rule labels: retval, a
            	    // token list labels: 
            	    // rule list labels: 
            	    // wildcard labels: 
            	    if ( state.backtracking==0 ) {
            	    retval.tree = root_0;
            	    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            	    RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"rule a",a!=null?a.tree:null);

            	    root_0 = (Node)adaptor.nil();
            	    // 751:32: -> ^( BINARY OP_RELATIONAL_COMPOSE $composeExpression $a)
            	    {
            	        // JFSL.g:751:35: ^( BINARY OP_RELATIONAL_COMPOSE $composeExpression $a)
            	        {
            	        Node root_1 = (Node)adaptor.nil();
            	        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BINARY, "BINARY"), root_1);

            	        adaptor.addChild(root_1, (Node)adaptor.create(OP_RELATIONAL_COMPOSE, "OP_RELATIONAL_COMPOSE"));
            	        adaptor.addChild(root_1, stream_retval.nextTree());
            	        adaptor.addChild(root_1, stream_a.nextTree());

            	        adaptor.addChild(root_0, root_1);
            	        }

            	    }

            	    retval.tree = root_0;}
            	    }
            	    break;

            	default :
            	    break loop73;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 64, composeExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "composeExpression"

    public static class unaryExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpression"
    // JFSL.g:755:1: unaryExpression : ( '+' unaryExpression -> ^( UNARY OP_PLUS unaryExpression ) | '-' unaryExpression -> ^( UNARY OP_MINUS unaryExpression ) | unaryExpressionNotPlusMinus );
    public final JFSLParser.unaryExpression_return unaryExpression() throws RecognitionException {
        JFSLParser.unaryExpression_return retval = new JFSLParser.unaryExpression_return();
        retval.start = input.LT(1);
        int unaryExpression_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal215=null;
        Token char_literal217=null;
        JFSLParser.unaryExpression_return unaryExpression216 = null;

        JFSLParser.unaryExpression_return unaryExpression218 = null;

        JFSLParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus219 = null;


        Node char_literal215_tree=null;
        Node char_literal217_tree=null;
        RewriteRuleTokenStream stream_195=new RewriteRuleTokenStream(adaptor,"token 195");
        RewriteRuleTokenStream stream_196=new RewriteRuleTokenStream(adaptor,"token 196");
        RewriteRuleSubtreeStream stream_unaryExpression=new RewriteRuleSubtreeStream(adaptor,"rule unaryExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return retval; }
            // JFSL.g:756:3: ( '+' unaryExpression -> ^( UNARY OP_PLUS unaryExpression ) | '-' unaryExpression -> ^( UNARY OP_MINUS unaryExpression ) | unaryExpressionNotPlusMinus )
            int alt74=3;
            switch ( input.LA(1) ) {
            case 195:
                {
                alt74=1;
                }
                break;
            case 196:
                {
                alt74=2;
                }
                break;
            case Identifier:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case HexLiteral:
            case OctalLiteral:
            case DecimalLiteral:
            case 145:
            case 151:
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 184:
            case 190:
            case 200:
            case 203:
            case 204:
            case 205:
            case 206:
            case 207:
            case 208:
            case 209:
            case 210:
                {
                alt74=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 74, 0, input);

                throw nvae;
            }

            switch (alt74) {
                case 1 :
                    // JFSL.g:756:5: '+' unaryExpression
                    {
                    char_literal215=(Token)match(input,195,FOLLOW_195_in_unaryExpression4188); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_195.add(char_literal215);

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression4190);
                    unaryExpression216=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_unaryExpression.add(unaryExpression216.getTree());


                    // AST REWRITE
                    // elements: unaryExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 756:25: -> ^( UNARY OP_PLUS unaryExpression )
                    {
                        // JFSL.g:756:28: ^( UNARY OP_PLUS unaryExpression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(UNARY, "UNARY"), root_1);

                        adaptor.addChild(root_1, (Node)adaptor.create(OP_PLUS, "OP_PLUS"));
                        adaptor.addChild(root_1, stream_unaryExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:757:5: '-' unaryExpression
                    {
                    char_literal217=(Token)match(input,196,FOLLOW_196_in_unaryExpression4206); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_196.add(char_literal217);

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression4208);
                    unaryExpression218=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_unaryExpression.add(unaryExpression218.getTree());


                    // AST REWRITE
                    // elements: unaryExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 757:25: -> ^( UNARY OP_MINUS unaryExpression )
                    {
                        // JFSL.g:757:28: ^( UNARY OP_MINUS unaryExpression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(UNARY, "UNARY"), root_1);

                        adaptor.addChild(root_1, (Node)adaptor.create(OP_MINUS, "OP_MINUS"));
                        adaptor.addChild(root_1, stream_unaryExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:758:5: unaryExpressionNotPlusMinus
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression4224);
                    unaryExpressionNotPlusMinus219=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, unaryExpressionNotPlusMinus219.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 65, unaryExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "unaryExpression"

    public static class unaryExpressionNotPlusMinus_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "unaryExpressionNotPlusMinus"
    // JFSL.g:761:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression -> ^( UNARY OP_BIT_NOT_OR_TRANSPOSE unaryExpression ) | '!' unaryExpression -> ^( UNARY OP_NOT unaryExpression ) | castExpression | joinExpression );
    public final JFSLParser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        JFSLParser.unaryExpressionNotPlusMinus_return retval = new JFSLParser.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal220=null;
        Token char_literal222=null;
        JFSLParser.unaryExpression_return unaryExpression221 = null;

        JFSLParser.unaryExpression_return unaryExpression223 = null;

        JFSLParser.castExpression_return castExpression224 = null;

        JFSLParser.joinExpression_return joinExpression225 = null;


        Node char_literal220_tree=null;
        Node char_literal222_tree=null;
        RewriteRuleTokenStream stream_203=new RewriteRuleTokenStream(adaptor,"token 203");
        RewriteRuleTokenStream stream_184=new RewriteRuleTokenStream(adaptor,"token 184");
        RewriteRuleSubtreeStream stream_unaryExpression=new RewriteRuleSubtreeStream(adaptor,"rule unaryExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return retval; }
            // JFSL.g:762:3: ( '~' unaryExpression -> ^( UNARY OP_BIT_NOT_OR_TRANSPOSE unaryExpression ) | '!' unaryExpression -> ^( UNARY OP_NOT unaryExpression ) | castExpression | joinExpression )
            int alt75=4;
            alt75 = dfa75.predict(input);
            switch (alt75) {
                case 1 :
                    // JFSL.g:762:5: '~' unaryExpression
                    {
                    char_literal220=(Token)match(input,203,FOLLOW_203_in_unaryExpressionNotPlusMinus4237); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_203.add(char_literal220);

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus4239);
                    unaryExpression221=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_unaryExpression.add(unaryExpression221.getTree());


                    // AST REWRITE
                    // elements: unaryExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 762:25: -> ^( UNARY OP_BIT_NOT_OR_TRANSPOSE unaryExpression )
                    {
                        // JFSL.g:762:28: ^( UNARY OP_BIT_NOT_OR_TRANSPOSE unaryExpression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(UNARY, "UNARY"), root_1);

                        adaptor.addChild(root_1, (Node)adaptor.create(OP_BIT_NOT_OR_TRANSPOSE, "OP_BIT_NOT_OR_TRANSPOSE"));
                        adaptor.addChild(root_1, stream_unaryExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:763:5: '!' unaryExpression
                    {
                    char_literal222=(Token)match(input,184,FOLLOW_184_in_unaryExpressionNotPlusMinus4255); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_184.add(char_literal222);

                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus4257);
                    unaryExpression223=unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_unaryExpression.add(unaryExpression223.getTree());


                    // AST REWRITE
                    // elements: unaryExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 763:25: -> ^( UNARY OP_NOT unaryExpression )
                    {
                        // JFSL.g:763:28: ^( UNARY OP_NOT unaryExpression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(UNARY, "UNARY"), root_1);

                        adaptor.addChild(root_1, (Node)adaptor.create(OP_NOT, "OP_NOT"));
                        adaptor.addChild(root_1, stream_unaryExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:764:5: castExpression
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus4277);
                    castExpression224=castExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, castExpression224.getTree());

                    }
                    break;
                case 4 :
                    // JFSL.g:765:5: joinExpression
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_joinExpression_in_unaryExpressionNotPlusMinus4283);
                    joinExpression225=joinExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, joinExpression225.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 66, unaryExpressionNotPlusMinus_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "unaryExpressionNotPlusMinus"

    public static class castExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "castExpression"
    // JFSL.g:768:1: castExpression : '(' primitiveType ')' unaryExpression -> ^( CAST primitiveType unaryExpression ) ;
    public final JFSLParser.castExpression_return castExpression() throws RecognitionException {
        JFSLParser.castExpression_return retval = new JFSLParser.castExpression_return();
        retval.start = input.LT(1);
        int castExpression_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal226=null;
        Token char_literal228=null;
        JFSLParser.primitiveType_return primitiveType227 = null;

        JFSLParser.unaryExpression_return unaryExpression229 = null;


        Node char_literal226_tree=null;
        Node char_literal228_tree=null;
        RewriteRuleTokenStream stream_152=new RewriteRuleTokenStream(adaptor,"token 152");
        RewriteRuleTokenStream stream_151=new RewriteRuleTokenStream(adaptor,"token 151");
        RewriteRuleSubtreeStream stream_primitiveType=new RewriteRuleSubtreeStream(adaptor,"rule primitiveType");
        RewriteRuleSubtreeStream stream_unaryExpression=new RewriteRuleSubtreeStream(adaptor,"rule unaryExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return retval; }
            // JFSL.g:769:3: ( '(' primitiveType ')' unaryExpression -> ^( CAST primitiveType unaryExpression ) )
            // JFSL.g:769:5: '(' primitiveType ')' unaryExpression
            {
            char_literal226=(Token)match(input,151,FOLLOW_151_in_castExpression4303); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_151.add(char_literal226);

            pushFollow(FOLLOW_primitiveType_in_castExpression4305);
            primitiveType227=primitiveType();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primitiveType.add(primitiveType227.getTree());
            char_literal228=(Token)match(input,152,FOLLOW_152_in_castExpression4307); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_152.add(char_literal228);

            pushFollow(FOLLOW_unaryExpression_in_castExpression4309);
            unaryExpression229=unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_unaryExpression.add(unaryExpression229.getTree());


            // AST REWRITE
            // elements: unaryExpression, primitiveType
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 769:43: -> ^( CAST primitiveType unaryExpression )
            {
                // JFSL.g:769:46: ^( CAST primitiveType unaryExpression )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(CAST, "CAST"), root_1);

                adaptor.addChild(root_1, stream_primitiveType.nextTree());
                adaptor.addChild(root_1, stream_unaryExpression.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 67, castExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "castExpression"

    public static class decls_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "decls"
    // JFSL.g:773:1: decls : ( Identifier ( inOp | ':' ) declarationMult rangeExpression ) ( ',' ( Identifier ( inOp | ':' ) declarationMult rangeExpression ) )* -> ^( DECLS ( Identifier declarationMult rangeExpression )+ ) ;
    public final JFSLParser.decls_return decls() throws RecognitionException {
        JFSLParser.decls_return retval = new JFSLParser.decls_return();
        retval.start = input.LT(1);
        int decls_StartIndex = input.index();
        Node root_0 = null;

        Token Identifier230=null;
        Token char_literal232=null;
        Token char_literal235=null;
        Token Identifier236=null;
        Token char_literal238=null;
        JFSLParser.inOp_return inOp231 = null;

        JFSLParser.declarationMult_return declarationMult233 = null;

        JFSLParser.rangeExpression_return rangeExpression234 = null;

        JFSLParser.inOp_return inOp237 = null;

        JFSLParser.declarationMult_return declarationMult239 = null;

        JFSLParser.rangeExpression_return rangeExpression240 = null;


        Node Identifier230_tree=null;
        Node char_literal232_tree=null;
        Node char_literal235_tree=null;
        Node Identifier236_tree=null;
        Node char_literal238_tree=null;
        RewriteRuleTokenStream stream_162=new RewriteRuleTokenStream(adaptor,"token 162");
        RewriteRuleTokenStream stream_148=new RewriteRuleTokenStream(adaptor,"token 148");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_rangeExpression=new RewriteRuleSubtreeStream(adaptor,"rule rangeExpression");
        RewriteRuleSubtreeStream stream_inOp=new RewriteRuleSubtreeStream(adaptor,"rule inOp");
        RewriteRuleSubtreeStream stream_declarationMult=new RewriteRuleSubtreeStream(adaptor,"rule declarationMult");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return retval; }
            // JFSL.g:774:3: ( ( Identifier ( inOp | ':' ) declarationMult rangeExpression ) ( ',' ( Identifier ( inOp | ':' ) declarationMult rangeExpression ) )* -> ^( DECLS ( Identifier declarationMult rangeExpression )+ ) )
            // JFSL.g:774:5: ( Identifier ( inOp | ':' ) declarationMult rangeExpression ) ( ',' ( Identifier ( inOp | ':' ) declarationMult rangeExpression ) )*
            {
            // JFSL.g:774:5: ( Identifier ( inOp | ':' ) declarationMult rangeExpression )
            // JFSL.g:774:6: Identifier ( inOp | ':' ) declarationMult rangeExpression
            {
            Identifier230=(Token)match(input,Identifier,FOLLOW_Identifier_in_decls4335); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Identifier.add(Identifier230);

            // JFSL.g:774:17: ( inOp | ':' )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==193) ) {
                alt76=1;
            }
            else if ( (LA76_0==162) ) {
                alt76=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // JFSL.g:774:18: inOp
                    {
                    pushFollow(FOLLOW_inOp_in_decls4338);
                    inOp231=inOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_inOp.add(inOp231.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:774:25: ':'
                    {
                    char_literal232=(Token)match(input,162,FOLLOW_162_in_decls4342); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_162.add(char_literal232);


                    }
                    break;

            }

            pushFollow(FOLLOW_declarationMult_in_decls4345);
            declarationMult233=declarationMult();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_declarationMult.add(declarationMult233.getTree());
            pushFollow(FOLLOW_rangeExpression_in_decls4347);
            rangeExpression234=rangeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rangeExpression.add(rangeExpression234.getTree());

            }

            // JFSL.g:774:63: ( ',' ( Identifier ( inOp | ':' ) declarationMult rangeExpression ) )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==148) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // JFSL.g:774:64: ',' ( Identifier ( inOp | ':' ) declarationMult rangeExpression )
            	    {
            	    char_literal235=(Token)match(input,148,FOLLOW_148_in_decls4351); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_148.add(char_literal235);

            	    // JFSL.g:774:68: ( Identifier ( inOp | ':' ) declarationMult rangeExpression )
            	    // JFSL.g:774:69: Identifier ( inOp | ':' ) declarationMult rangeExpression
            	    {
            	    Identifier236=(Token)match(input,Identifier,FOLLOW_Identifier_in_decls4354); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Identifier.add(Identifier236);

            	    // JFSL.g:774:80: ( inOp | ':' )
            	    int alt77=2;
            	    int LA77_0 = input.LA(1);

            	    if ( (LA77_0==193) ) {
            	        alt77=1;
            	    }
            	    else if ( (LA77_0==162) ) {
            	        alt77=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 77, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt77) {
            	        case 1 :
            	            // JFSL.g:774:81: inOp
            	            {
            	            pushFollow(FOLLOW_inOp_in_decls4357);
            	            inOp237=inOp();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) stream_inOp.add(inOp237.getTree());

            	            }
            	            break;
            	        case 2 :
            	            // JFSL.g:774:88: ':'
            	            {
            	            char_literal238=(Token)match(input,162,FOLLOW_162_in_decls4361); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_162.add(char_literal238);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_declarationMult_in_decls4364);
            	    declarationMult239=declarationMult();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_declarationMult.add(declarationMult239.getTree());
            	    pushFollow(FOLLOW_rangeExpression_in_decls4366);
            	    rangeExpression240=rangeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rangeExpression.add(rangeExpression240.getTree());

            	    }


            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);



            // AST REWRITE
            // elements: declarationMult, Identifier, rangeExpression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 775:10: -> ^( DECLS ( Identifier declarationMult rangeExpression )+ )
            {
                // JFSL.g:775:13: ^( DECLS ( Identifier declarationMult rangeExpression )+ )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(DECLS, "DECLS"), root_1);

                if ( !(stream_declarationMult.hasNext()||stream_Identifier.hasNext()||stream_rangeExpression.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_declarationMult.hasNext()||stream_Identifier.hasNext()||stream_rangeExpression.hasNext() ) {
                    adaptor.addChild(root_1, stream_Identifier.nextNode());
                    adaptor.addChild(root_1, stream_declarationMult.nextTree());
                    adaptor.addChild(root_1, stream_rangeExpression.nextTree());

                }
                stream_declarationMult.reset();
                stream_Identifier.reset();
                stream_rangeExpression.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 68, decls_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "decls"

    public static class mdecls_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mdecls"
    // JFSL.g:778:1: mdecls : ( quantIdMod Identifier ( ',' Identifier )* ( inOp | ':' ) declarationMult rangeExpression ) -> ^( MDECLS quantIdMod ( Identifier )+ declarationMult rangeExpression ) ;
    public final JFSLParser.mdecls_return mdecls() throws RecognitionException {
        JFSLParser.mdecls_return retval = new JFSLParser.mdecls_return();
        retval.start = input.LT(1);
        int mdecls_StartIndex = input.index();
        Node root_0 = null;

        Token Identifier242=null;
        Token char_literal243=null;
        Token Identifier244=null;
        Token char_literal246=null;
        JFSLParser.quantIdMod_return quantIdMod241 = null;

        JFSLParser.inOp_return inOp245 = null;

        JFSLParser.declarationMult_return declarationMult247 = null;

        JFSLParser.rangeExpression_return rangeExpression248 = null;


        Node Identifier242_tree=null;
        Node char_literal243_tree=null;
        Node Identifier244_tree=null;
        Node char_literal246_tree=null;
        RewriteRuleTokenStream stream_162=new RewriteRuleTokenStream(adaptor,"token 162");
        RewriteRuleTokenStream stream_148=new RewriteRuleTokenStream(adaptor,"token 148");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_rangeExpression=new RewriteRuleSubtreeStream(adaptor,"rule rangeExpression");
        RewriteRuleSubtreeStream stream_inOp=new RewriteRuleSubtreeStream(adaptor,"rule inOp");
        RewriteRuleSubtreeStream stream_quantIdMod=new RewriteRuleSubtreeStream(adaptor,"rule quantIdMod");
        RewriteRuleSubtreeStream stream_declarationMult=new RewriteRuleSubtreeStream(adaptor,"rule declarationMult");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return retval; }
            // JFSL.g:779:3: ( ( quantIdMod Identifier ( ',' Identifier )* ( inOp | ':' ) declarationMult rangeExpression ) -> ^( MDECLS quantIdMod ( Identifier )+ declarationMult rangeExpression ) )
            // JFSL.g:779:5: ( quantIdMod Identifier ( ',' Identifier )* ( inOp | ':' ) declarationMult rangeExpression )
            {
            // JFSL.g:779:5: ( quantIdMod Identifier ( ',' Identifier )* ( inOp | ':' ) declarationMult rangeExpression )
            // JFSL.g:779:6: quantIdMod Identifier ( ',' Identifier )* ( inOp | ':' ) declarationMult rangeExpression
            {
            pushFollow(FOLLOW_quantIdMod_in_mdecls4409);
            quantIdMod241=quantIdMod();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_quantIdMod.add(quantIdMod241.getTree());
            Identifier242=(Token)match(input,Identifier,FOLLOW_Identifier_in_mdecls4411); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Identifier.add(Identifier242);

            // JFSL.g:779:28: ( ',' Identifier )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==148) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // JFSL.g:779:29: ',' Identifier
            	    {
            	    char_literal243=(Token)match(input,148,FOLLOW_148_in_mdecls4414); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_148.add(char_literal243);

            	    Identifier244=(Token)match(input,Identifier,FOLLOW_Identifier_in_mdecls4416); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_Identifier.add(Identifier244);


            	    }
            	    break;

            	default :
            	    break loop79;
                }
            } while (true);

            // JFSL.g:779:46: ( inOp | ':' )
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==193) ) {
                alt80=1;
            }
            else if ( (LA80_0==162) ) {
                alt80=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);

                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // JFSL.g:779:47: inOp
                    {
                    pushFollow(FOLLOW_inOp_in_mdecls4421);
                    inOp245=inOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_inOp.add(inOp245.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:779:54: ':'
                    {
                    char_literal246=(Token)match(input,162,FOLLOW_162_in_mdecls4425); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_162.add(char_literal246);


                    }
                    break;

            }

            pushFollow(FOLLOW_declarationMult_in_mdecls4428);
            declarationMult247=declarationMult();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_declarationMult.add(declarationMult247.getTree());
            pushFollow(FOLLOW_rangeExpression_in_mdecls4430);
            rangeExpression248=rangeExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rangeExpression.add(rangeExpression248.getTree());

            }



            // AST REWRITE
            // elements: rangeExpression, declarationMult, quantIdMod, Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 780:10: -> ^( MDECLS quantIdMod ( Identifier )+ declarationMult rangeExpression )
            {
                // JFSL.g:780:13: ^( MDECLS quantIdMod ( Identifier )+ declarationMult rangeExpression )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(MDECLS, "MDECLS"), root_1);

                adaptor.addChild(root_1, stream_quantIdMod.nextTree());
                if ( !(stream_Identifier.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_Identifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_Identifier.nextNode());

                }
                stream_Identifier.reset();
                adaptor.addChild(root_1, stream_declarationMult.nextTree());
                adaptor.addChild(root_1, stream_rangeExpression.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 69, mdecls_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "mdecls"

    public static class relationalUnaryExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "relationalUnaryExpression"
    // JFSL.g:783:1: relationalUnaryExpression : ( specUnaryOp parExpression -> ^( UNARY specUnaryOp parExpression ) | specUnaryOp Identifier -> ^( UNARY specUnaryOp ^( IDENTIFIER Identifier ) ) );
    public final JFSLParser.relationalUnaryExpression_return relationalUnaryExpression() throws RecognitionException {
        JFSLParser.relationalUnaryExpression_return retval = new JFSLParser.relationalUnaryExpression_return();
        retval.start = input.LT(1);
        int relationalUnaryExpression_StartIndex = input.index();
        Node root_0 = null;

        Token Identifier252=null;
        JFSLParser.specUnaryOp_return specUnaryOp249 = null;

        JFSLParser.parExpression_return parExpression250 = null;

        JFSLParser.specUnaryOp_return specUnaryOp251 = null;


        Node Identifier252_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_specUnaryOp=new RewriteRuleSubtreeStream(adaptor,"rule specUnaryOp");
        RewriteRuleSubtreeStream stream_parExpression=new RewriteRuleSubtreeStream(adaptor,"rule parExpression");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return retval; }
            // JFSL.g:784:3: ( specUnaryOp parExpression -> ^( UNARY specUnaryOp parExpression ) | specUnaryOp Identifier -> ^( UNARY specUnaryOp ^( IDENTIFIER Identifier ) ) )
            int alt81=2;
            switch ( input.LA(1) ) {
            case 200:
                {
                int LA81_1 = input.LA(2);

                if ( (LA81_1==190) ) {
                    int LA81_4 = input.LA(3);

                    if ( (LA81_4==151) ) {
                        alt81=1;
                    }
                    else if ( (LA81_4==Identifier) ) {
                        alt81=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 81, 4, input);

                        throw nvae;
                    }
                }
                else if ( (LA81_1==203) ) {
                    int LA81_5 = input.LA(3);

                    if ( (LA81_5==151) ) {
                        alt81=1;
                    }
                    else if ( (LA81_5==Identifier) ) {
                        alt81=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 81, 5, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 81, 1, input);

                    throw nvae;
                }
                }
                break;
            case 190:
                {
                int LA81_2 = input.LA(2);

                if ( (LA81_2==Identifier) ) {
                    alt81=2;
                }
                else if ( (LA81_2==151) ) {
                    alt81=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 81, 2, input);

                    throw nvae;
                }
                }
                break;
            case 203:
                {
                int LA81_3 = input.LA(2);

                if ( (LA81_3==151) ) {
                    alt81=1;
                }
                else if ( (LA81_3==Identifier) ) {
                    alt81=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 81, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);

                throw nvae;
            }

            switch (alt81) {
                case 1 :
                    // JFSL.g:784:5: specUnaryOp parExpression
                    {
                    pushFollow(FOLLOW_specUnaryOp_in_relationalUnaryExpression4469);
                    specUnaryOp249=specUnaryOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_specUnaryOp.add(specUnaryOp249.getTree());
                    pushFollow(FOLLOW_parExpression_in_relationalUnaryExpression4471);
                    parExpression250=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_parExpression.add(parExpression250.getTree());


                    // AST REWRITE
                    // elements: parExpression, specUnaryOp
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 784:31: -> ^( UNARY specUnaryOp parExpression )
                    {
                        // JFSL.g:784:34: ^( UNARY specUnaryOp parExpression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(UNARY, "UNARY"), root_1);

                        adaptor.addChild(root_1, stream_specUnaryOp.nextTree());
                        adaptor.addChild(root_1, stream_parExpression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:785:5: specUnaryOp Identifier
                    {
                    pushFollow(FOLLOW_specUnaryOp_in_relationalUnaryExpression4487);
                    specUnaryOp251=specUnaryOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_specUnaryOp.add(specUnaryOp251.getTree());
                    Identifier252=(Token)match(input,Identifier,FOLLOW_Identifier_in_relationalUnaryExpression4489); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier252);



                    // AST REWRITE
                    // elements: specUnaryOp, Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 785:28: -> ^( UNARY specUnaryOp ^( IDENTIFIER Identifier ) )
                    {
                        // JFSL.g:785:31: ^( UNARY specUnaryOp ^( IDENTIFIER Identifier ) )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(UNARY, "UNARY"), root_1);

                        adaptor.addChild(root_1, stream_specUnaryOp.nextTree());
                        // JFSL.g:785:51: ^( IDENTIFIER Identifier )
                        {
                        Node root_2 = (Node)adaptor.nil();
                        root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(IDENTIFIER, "IDENTIFIER"), root_2);

                        adaptor.addChild(root_2, stream_Identifier.nextNode());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 70, relationalUnaryExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalUnaryExpression"

    public static class joinExpression_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "joinExpression"
    // JFSL.g:788:1: joinExpression : primary ( selector )* -> ^( CHAIN primary ( selector )* ) ;
    public final JFSLParser.joinExpression_return joinExpression() throws RecognitionException {
        JFSLParser.joinExpression_return retval = new JFSLParser.joinExpression_return();
        retval.start = input.LT(1);
        int joinExpression_StartIndex = input.index();
        Node root_0 = null;

        JFSLParser.primary_return primary253 = null;

        JFSLParser.selector_return selector254 = null;


        RewriteRuleSubtreeStream stream_selector=new RewriteRuleSubtreeStream(adaptor,"rule selector");
        RewriteRuleSubtreeStream stream_primary=new RewriteRuleSubtreeStream(adaptor,"rule primary");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return retval; }
            // JFSL.g:789:3: ( primary ( selector )* -> ^( CHAIN primary ( selector )* ) )
            // JFSL.g:789:7: primary ( selector )*
            {
            pushFollow(FOLLOW_primary_in_joinExpression4522);
            primary253=primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_primary.add(primary253.getTree());
            // JFSL.g:789:15: ( selector )*
            loop82:
            do {
                int alt82=2;
                alt82 = dfa82.predict(input);
                switch (alt82) {
            	case 1 :
            	    // JFSL.g:0:0: selector
            	    {
            	    pushFollow(FOLLOW_selector_in_joinExpression4524);
            	    selector254=selector();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_selector.add(selector254.getTree());

            	    }
            	    break;

            	default :
            	    break loop82;
                }
            } while (true);



            // AST REWRITE
            // elements: primary, selector
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 789:24: -> ^( CHAIN primary ( selector )* )
            {
                // JFSL.g:789:27: ^( CHAIN primary ( selector )* )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(CHAIN, "CHAIN"), root_1);

                adaptor.addChild(root_1, stream_primary.nextTree());
                // JFSL.g:789:43: ( selector )*
                while ( stream_selector.hasNext() ) {
                    adaptor.addChild(root_1, stream_selector.nextTree());

                }
                stream_selector.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 71, joinExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "joinExpression"

    public static class common_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "common"
    // JFSL.g:792:1: common : ( parExpression | x= 'return' -> ^( RETURN_VAR $x) | x= 'throw' -> ^( THROW_VAR $x) | x= 'this' -> ^( THIS_VAR $x) | x= 'super' -> ^( SUPER_VAR $x) | x= '_' -> ^( LAMBDA_VAR $x) | x= '@old' '(' expression ')' -> ^( OLD expression ) | x= '@arg' '(' integerLiteral ')' -> ^( ARGUMENT integerLiteral ) | relationalUnaryExpression | '{' decls '|' expression '}' -> ^( QUANTIFY OP_SET_COMPREHENSION decls expression ) | '{' rangeExpression ( ',' rangeExpression )* '}' -> ^( OP_SET_COMPREHENSION_ENUM ( rangeExpression )+ ) );
    public final JFSLParser.common_return common() throws RecognitionException {
        JFSLParser.common_return retval = new JFSLParser.common_return();
        retval.start = input.LT(1);
        int common_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;
        Token char_literal256=null;
        Token char_literal258=null;
        Token char_literal259=null;
        Token char_literal261=null;
        Token char_literal263=null;
        Token char_literal265=null;
        Token char_literal267=null;
        Token char_literal268=null;
        Token char_literal270=null;
        Token char_literal272=null;
        JFSLParser.parExpression_return parExpression255 = null;

        JFSLParser.expression_return expression257 = null;

        JFSLParser.integerLiteral_return integerLiteral260 = null;

        JFSLParser.relationalUnaryExpression_return relationalUnaryExpression262 = null;

        JFSLParser.decls_return decls264 = null;

        JFSLParser.expression_return expression266 = null;

        JFSLParser.rangeExpression_return rangeExpression269 = null;

        JFSLParser.rangeExpression_return rangeExpression271 = null;


        Node x_tree=null;
        Node char_literal256_tree=null;
        Node char_literal258_tree=null;
        Node char_literal259_tree=null;
        Node char_literal261_tree=null;
        Node char_literal263_tree=null;
        Node char_literal265_tree=null;
        Node char_literal267_tree=null;
        Node char_literal268_tree=null;
        Node char_literal270_tree=null;
        Node char_literal272_tree=null;
        RewriteRuleTokenStream stream_204=new RewriteRuleTokenStream(adaptor,"token 204");
        RewriteRuleTokenStream stream_145=new RewriteRuleTokenStream(adaptor,"token 145");
        RewriteRuleTokenStream stream_205=new RewriteRuleTokenStream(adaptor,"token 205");
        RewriteRuleTokenStream stream_210=new RewriteRuleTokenStream(adaptor,"token 210");
        RewriteRuleTokenStream stream_146=new RewriteRuleTokenStream(adaptor,"token 146");
        RewriteRuleTokenStream stream_148=new RewriteRuleTokenStream(adaptor,"token 148");
        RewriteRuleTokenStream stream_206=new RewriteRuleTokenStream(adaptor,"token 206");
        RewriteRuleTokenStream stream_207=new RewriteRuleTokenStream(adaptor,"token 207");
        RewriteRuleTokenStream stream_208=new RewriteRuleTokenStream(adaptor,"token 208");
        RewriteRuleTokenStream stream_209=new RewriteRuleTokenStream(adaptor,"token 209");
        RewriteRuleTokenStream stream_152=new RewriteRuleTokenStream(adaptor,"token 152");
        RewriteRuleTokenStream stream_151=new RewriteRuleTokenStream(adaptor,"token 151");
        RewriteRuleTokenStream stream_164=new RewriteRuleTokenStream(adaptor,"token 164");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_rangeExpression=new RewriteRuleSubtreeStream(adaptor,"rule rangeExpression");
        RewriteRuleSubtreeStream stream_decls=new RewriteRuleSubtreeStream(adaptor,"rule decls");
        RewriteRuleSubtreeStream stream_integerLiteral=new RewriteRuleSubtreeStream(adaptor,"rule integerLiteral");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return retval; }
            // JFSL.g:793:3: ( parExpression | x= 'return' -> ^( RETURN_VAR $x) | x= 'throw' -> ^( THROW_VAR $x) | x= 'this' -> ^( THIS_VAR $x) | x= 'super' -> ^( SUPER_VAR $x) | x= '_' -> ^( LAMBDA_VAR $x) | x= '@old' '(' expression ')' -> ^( OLD expression ) | x= '@arg' '(' integerLiteral ')' -> ^( ARGUMENT integerLiteral ) | relationalUnaryExpression | '{' decls '|' expression '}' -> ^( QUANTIFY OP_SET_COMPREHENSION decls expression ) | '{' rangeExpression ( ',' rangeExpression )* '}' -> ^( OP_SET_COMPREHENSION_ENUM ( rangeExpression )+ ) )
            int alt84=11;
            alt84 = dfa84.predict(input);
            switch (alt84) {
                case 1 :
                    // JFSL.g:793:5: parExpression
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_parExpression_in_common4553);
                    parExpression255=parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, parExpression255.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:794:5: x= 'return'
                    {
                    x=(Token)match(input,204,FOLLOW_204_in_common4561); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_204.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 794:16: -> ^( RETURN_VAR $x)
                    {
                        // JFSL.g:794:19: ^( RETURN_VAR $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(RETURN_VAR, "RETURN_VAR"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:795:5: x= 'throw'
                    {
                    x=(Token)match(input,205,FOLLOW_205_in_common4578); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_205.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 795:16: -> ^( THROW_VAR $x)
                    {
                        // JFSL.g:795:19: ^( THROW_VAR $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(THROW_VAR, "THROW_VAR"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // JFSL.g:796:5: x= 'this'
                    {
                    x=(Token)match(input,206,FOLLOW_206_in_common4596); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_206.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 796:16: -> ^( THIS_VAR $x)
                    {
                        // JFSL.g:796:19: ^( THIS_VAR $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(THIS_VAR, "THIS_VAR"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // JFSL.g:797:5: x= 'super'
                    {
                    x=(Token)match(input,207,FOLLOW_207_in_common4615); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_207.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 797:16: -> ^( SUPER_VAR $x)
                    {
                        // JFSL.g:797:19: ^( SUPER_VAR $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(SUPER_VAR, "SUPER_VAR"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // JFSL.g:798:5: x= '_'
                    {
                    x=(Token)match(input,208,FOLLOW_208_in_common4633); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_208.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 798:16: -> ^( LAMBDA_VAR $x)
                    {
                        // JFSL.g:798:19: ^( LAMBDA_VAR $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(LAMBDA_VAR, "LAMBDA_VAR"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 7 :
                    // JFSL.g:799:5: x= '@old' '(' expression ')'
                    {
                    x=(Token)match(input,209,FOLLOW_209_in_common4655); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_209.add(x);

                    char_literal256=(Token)match(input,151,FOLLOW_151_in_common4657); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_151.add(char_literal256);

                    pushFollow(FOLLOW_expression_in_common4659);
                    expression257=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression257.getTree());
                    char_literal258=(Token)match(input,152,FOLLOW_152_in_common4661); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_152.add(char_literal258);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 799:33: -> ^( OLD expression )
                    {
                        // JFSL.g:799:36: ^( OLD expression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OLD, "OLD"), root_1);

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 8 :
                    // JFSL.g:800:5: x= '@arg' '(' integerLiteral ')'
                    {
                    x=(Token)match(input,210,FOLLOW_210_in_common4680); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_210.add(x);

                    char_literal259=(Token)match(input,151,FOLLOW_151_in_common4682); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_151.add(char_literal259);

                    pushFollow(FOLLOW_integerLiteral_in_common4684);
                    integerLiteral260=integerLiteral();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_integerLiteral.add(integerLiteral260.getTree());
                    char_literal261=(Token)match(input,152,FOLLOW_152_in_common4686); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_152.add(char_literal261);



                    // AST REWRITE
                    // elements: integerLiteral
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 800:37: -> ^( ARGUMENT integerLiteral )
                    {
                        // JFSL.g:800:40: ^( ARGUMENT integerLiteral )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(ARGUMENT, "ARGUMENT"), root_1);

                        adaptor.addChild(root_1, stream_integerLiteral.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 9 :
                    // JFSL.g:801:5: relationalUnaryExpression
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_relationalUnaryExpression_in_common4700);
                    relationalUnaryExpression262=relationalUnaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, relationalUnaryExpression262.getTree());

                    }
                    break;
                case 10 :
                    // JFSL.g:802:5: '{' decls '|' expression '}'
                    {
                    char_literal263=(Token)match(input,145,FOLLOW_145_in_common4707); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_145.add(char_literal263);

                    pushFollow(FOLLOW_decls_in_common4709);
                    decls264=decls();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_decls.add(decls264.getTree());
                    char_literal265=(Token)match(input,164,FOLLOW_164_in_common4711); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_164.add(char_literal265);

                    pushFollow(FOLLOW_expression_in_common4713);
                    expression266=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression266.getTree());
                    char_literal267=(Token)match(input,146,FOLLOW_146_in_common4715); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_146.add(char_literal267);



                    // AST REWRITE
                    // elements: decls, expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 802:34: -> ^( QUANTIFY OP_SET_COMPREHENSION decls expression )
                    {
                        // JFSL.g:802:37: ^( QUANTIFY OP_SET_COMPREHENSION decls expression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(QUANTIFY, "QUANTIFY"), root_1);

                        adaptor.addChild(root_1, (Node)adaptor.create(OP_SET_COMPREHENSION, "OP_SET_COMPREHENSION"));
                        adaptor.addChild(root_1, stream_decls.nextTree());
                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 11 :
                    // JFSL.g:803:5: '{' rangeExpression ( ',' rangeExpression )* '}'
                    {
                    char_literal268=(Token)match(input,145,FOLLOW_145_in_common4733); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_145.add(char_literal268);

                    pushFollow(FOLLOW_rangeExpression_in_common4735);
                    rangeExpression269=rangeExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rangeExpression.add(rangeExpression269.getTree());
                    // JFSL.g:803:25: ( ',' rangeExpression )*
                    loop83:
                    do {
                        int alt83=2;
                        int LA83_0 = input.LA(1);

                        if ( (LA83_0==148) ) {
                            alt83=1;
                        }


                        switch (alt83) {
                    	case 1 :
                    	    // JFSL.g:803:26: ',' rangeExpression
                    	    {
                    	    char_literal270=(Token)match(input,148,FOLLOW_148_in_common4738); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_148.add(char_literal270);

                    	    pushFollow(FOLLOW_rangeExpression_in_common4740);
                    	    rangeExpression271=rangeExpression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_rangeExpression.add(rangeExpression271.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop83;
                        }
                    } while (true);

                    char_literal272=(Token)match(input,146,FOLLOW_146_in_common4744); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_146.add(char_literal272);



                    // AST REWRITE
                    // elements: rangeExpression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 803:52: -> ^( OP_SET_COMPREHENSION_ENUM ( rangeExpression )+ )
                    {
                        // JFSL.g:803:55: ^( OP_SET_COMPREHENSION_ENUM ( rangeExpression )+ )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SET_COMPREHENSION_ENUM, "OP_SET_COMPREHENSION_ENUM"), root_1);

                        if ( !(stream_rangeExpression.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_rangeExpression.hasNext() ) {
                            adaptor.addChild(root_1, stream_rangeExpression.nextTree());

                        }
                        stream_rangeExpression.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 72, common_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "common"

    public static class primary_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primary"
    // JFSL.g:806:1: primary : ( common | literal | primaryTypeIdentifier | typeName '@' Identifier -> ^( FIELD typeName ^( IDENTIFIER Identifier ) ) | Identifier arguments -> ^( CALL Identifier arguments ) );
    public final JFSLParser.primary_return primary() throws RecognitionException {
        JFSLParser.primary_return retval = new JFSLParser.primary_return();
        retval.start = input.LT(1);
        int primary_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal277=null;
        Token Identifier278=null;
        Token Identifier279=null;
        JFSLParser.common_return common273 = null;

        JFSLParser.literal_return literal274 = null;

        JFSLParser.primaryTypeIdentifier_return primaryTypeIdentifier275 = null;

        JFSLParser.typeName_return typeName276 = null;

        JFSLParser.arguments_return arguments280 = null;


        Node char_literal277_tree=null;
        Node Identifier278_tree=null;
        Node Identifier279_tree=null;
        RewriteRuleTokenStream stream_200=new RewriteRuleTokenStream(adaptor,"token 200");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_typeName=new RewriteRuleSubtreeStream(adaptor,"rule typeName");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return retval; }
            // JFSL.g:807:3: ( common | literal | primaryTypeIdentifier | typeName '@' Identifier -> ^( FIELD typeName ^( IDENTIFIER Identifier ) ) | Identifier arguments -> ^( CALL Identifier arguments ) )
            int alt85=5;
            alt85 = dfa85.predict(input);
            switch (alt85) {
                case 1 :
                    // JFSL.g:807:5: common
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_common_in_primary4768);
                    common273=common();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, common273.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:808:5: literal
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_primary4774);
                    literal274=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal274.getTree());

                    }
                    break;
                case 3 :
                    // JFSL.g:809:5: primaryTypeIdentifier
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_primaryTypeIdentifier_in_primary4780);
                    primaryTypeIdentifier275=primaryTypeIdentifier();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, primaryTypeIdentifier275.getTree());

                    }
                    break;
                case 4 :
                    // JFSL.g:810:5: typeName '@' Identifier
                    {
                    pushFollow(FOLLOW_typeName_in_primary4786);
                    typeName276=typeName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_typeName.add(typeName276.getTree());
                    char_literal277=(Token)match(input,200,FOLLOW_200_in_primary4788); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_200.add(char_literal277);

                    Identifier278=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary4790); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier278);



                    // AST REWRITE
                    // elements: typeName, Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 810:29: -> ^( FIELD typeName ^( IDENTIFIER Identifier ) )
                    {
                        // JFSL.g:810:32: ^( FIELD typeName ^( IDENTIFIER Identifier ) )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(FIELD, "FIELD"), root_1);

                        adaptor.addChild(root_1, stream_typeName.nextTree());
                        // JFSL.g:810:49: ^( IDENTIFIER Identifier )
                        {
                        Node root_2 = (Node)adaptor.nil();
                        root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(IDENTIFIER, "IDENTIFIER"), root_2);

                        adaptor.addChild(root_2, stream_Identifier.nextNode());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // JFSL.g:811:5: Identifier arguments
                    {
                    Identifier279=(Token)match(input,Identifier,FOLLOW_Identifier_in_primary4810); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier279);

                    pushFollow(FOLLOW_arguments_in_primary4812);
                    arguments280=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arguments.add(arguments280.getTree());


                    // AST REWRITE
                    // elements: Identifier, arguments
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 811:26: -> ^( CALL Identifier arguments )
                    {
                        // JFSL.g:811:29: ^( CALL Identifier arguments )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(CALL, "CALL"), root_1);

                        adaptor.addChild(root_1, stream_Identifier.nextNode());
                        adaptor.addChild(root_1, stream_arguments.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 73, primary_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primary"

    public static class primaryTypeIdentifier_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "primaryTypeIdentifier"
    // JFSL.g:814:1: primaryTypeIdentifier : ( typeDisambiguous | Identifier ( '.' Identifier )* ( typeParameters2 )? -> ^( AMBIGUOUS ( ^( IDENTIFIER Identifier ) )+ ( ^( TYPE_PARAMETERS typeParameters2 ) )? ) );
    public final JFSLParser.primaryTypeIdentifier_return primaryTypeIdentifier() throws RecognitionException {
        JFSLParser.primaryTypeIdentifier_return retval = new JFSLParser.primaryTypeIdentifier_return();
        retval.start = input.LT(1);
        int primaryTypeIdentifier_StartIndex = input.index();
        Node root_0 = null;

        Token Identifier282=null;
        Token char_literal283=null;
        Token Identifier284=null;
        JFSLParser.typeDisambiguous_return typeDisambiguous281 = null;

        JFSLParser.typeParameters2_return typeParameters2285 = null;


        Node Identifier282_tree=null;
        Node char_literal283_tree=null;
        Node Identifier284_tree=null;
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_141=new RewriteRuleTokenStream(adaptor,"token 141");
        RewriteRuleSubtreeStream stream_typeParameters2=new RewriteRuleSubtreeStream(adaptor,"rule typeParameters2");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return retval; }
            // JFSL.g:815:3: ( typeDisambiguous | Identifier ( '.' Identifier )* ( typeParameters2 )? -> ^( AMBIGUOUS ( ^( IDENTIFIER Identifier ) )+ ( ^( TYPE_PARAMETERS typeParameters2 ) )? ) )
            int alt88=2;
            alt88 = dfa88.predict(input);
            switch (alt88) {
                case 1 :
                    // JFSL.g:815:5: typeDisambiguous
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_typeDisambiguous_in_primaryTypeIdentifier4837);
                    typeDisambiguous281=typeDisambiguous();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, typeDisambiguous281.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:816:5: Identifier ( '.' Identifier )* ( typeParameters2 )?
                    {
                    Identifier282=(Token)match(input,Identifier,FOLLOW_Identifier_in_primaryTypeIdentifier4843); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier282);

                    // JFSL.g:816:16: ( '.' Identifier )*
                    loop86:
                    do {
                        int alt86=2;
                        int LA86_0 = input.LA(1);

                        if ( (LA86_0==141) ) {
                            int LA86_2 = input.LA(2);

                            if ( (LA86_2==Identifier) ) {
                                int LA86_3 = input.LA(3);

                                if ( (synpred133_JFSL()) ) {
                                    alt86=1;
                                }


                            }


                        }


                        switch (alt86) {
                    	case 1 :
                    	    // JFSL.g:816:17: '.' Identifier
                    	    {
                    	    char_literal283=(Token)match(input,141,FOLLOW_141_in_primaryTypeIdentifier4846); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_141.add(char_literal283);

                    	    Identifier284=(Token)match(input,Identifier,FOLLOW_Identifier_in_primaryTypeIdentifier4848); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_Identifier.add(Identifier284);


                    	    }
                    	    break;

                    	default :
                    	    break loop86;
                        }
                    } while (true);

                    // JFSL.g:816:34: ( typeParameters2 )?
                    int alt87=2;
                    alt87 = dfa87.predict(input);
                    switch (alt87) {
                        case 1 :
                            // JFSL.g:0:0: typeParameters2
                            {
                            pushFollow(FOLLOW_typeParameters2_in_primaryTypeIdentifier4852);
                            typeParameters2285=typeParameters2();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_typeParameters2.add(typeParameters2285.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: typeParameters2, Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 816:51: -> ^( AMBIGUOUS ( ^( IDENTIFIER Identifier ) )+ ( ^( TYPE_PARAMETERS typeParameters2 ) )? )
                    {
                        // JFSL.g:816:54: ^( AMBIGUOUS ( ^( IDENTIFIER Identifier ) )+ ( ^( TYPE_PARAMETERS typeParameters2 ) )? )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(AMBIGUOUS, "AMBIGUOUS"), root_1);

                        if ( !(stream_Identifier.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_Identifier.hasNext() ) {
                            // JFSL.g:816:66: ^( IDENTIFIER Identifier )
                            {
                            Node root_2 = (Node)adaptor.nil();
                            root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(IDENTIFIER, "IDENTIFIER"), root_2);

                            adaptor.addChild(root_2, stream_Identifier.nextNode());

                            adaptor.addChild(root_1, root_2);
                            }

                        }
                        stream_Identifier.reset();
                        // JFSL.g:816:92: ( ^( TYPE_PARAMETERS typeParameters2 ) )?
                        if ( stream_typeParameters2.hasNext() ) {
                            // JFSL.g:816:92: ^( TYPE_PARAMETERS typeParameters2 )
                            {
                            Node root_2 = (Node)adaptor.nil();
                            root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(TYPE_PARAMETERS, "TYPE_PARAMETERS"), root_2);

                            adaptor.addChild(root_2, stream_typeParameters2.nextTree());

                            adaptor.addChild(root_1, root_2);
                            }

                        }
                        stream_typeParameters2.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 74, primaryTypeIdentifier_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primaryTypeIdentifier"

    public static class selector_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "selector"
    // JFSL.g:820:1: selector : ( '.' Identifier arguments -> ^( CALL Identifier arguments ) | '.' Identifier -> ^( JOIN ^( IDENTIFIER Identifier ) ) | '.' primitiveType -> ^( JOIN primitiveType ) | '.' common -> ^( JOIN common ) | '.' '*' Identifier -> ^( JOIN_REFLEXIVE ^( IDENTIFIER Identifier ) ) | '.' '*' common -> ^( JOIN_REFLEXIVE common ) | '[' expression ']' -> ^( BRACKET expression ) | '[' ']' -> ^( BRACKET ) | '<' DecimalLiteral ( ',' DecimalLiteral )* '>' -> ^( PROJECTION ( DecimalLiteral )+ ) );
    public final JFSLParser.selector_return selector() throws RecognitionException {
        JFSLParser.selector_return retval = new JFSLParser.selector_return();
        retval.start = input.LT(1);
        int selector_StartIndex = input.index();
        Node root_0 = null;

        Token char_literal286=null;
        Token Identifier287=null;
        Token char_literal289=null;
        Token Identifier290=null;
        Token char_literal291=null;
        Token char_literal293=null;
        Token char_literal295=null;
        Token char_literal296=null;
        Token Identifier297=null;
        Token char_literal298=null;
        Token char_literal299=null;
        Token char_literal301=null;
        Token char_literal303=null;
        Token char_literal304=null;
        Token char_literal305=null;
        Token char_literal306=null;
        Token DecimalLiteral307=null;
        Token char_literal308=null;
        Token DecimalLiteral309=null;
        Token char_literal310=null;
        JFSLParser.arguments_return arguments288 = null;

        JFSLParser.primitiveType_return primitiveType292 = null;

        JFSLParser.common_return common294 = null;

        JFSLParser.common_return common300 = null;

        JFSLParser.expression_return expression302 = null;


        Node char_literal286_tree=null;
        Node Identifier287_tree=null;
        Node char_literal289_tree=null;
        Node Identifier290_tree=null;
        Node char_literal291_tree=null;
        Node char_literal293_tree=null;
        Node char_literal295_tree=null;
        Node char_literal296_tree=null;
        Node Identifier297_tree=null;
        Node char_literal298_tree=null;
        Node char_literal299_tree=null;
        Node char_literal301_tree=null;
        Node char_literal303_tree=null;
        Node char_literal304_tree=null;
        Node char_literal305_tree=null;
        Node char_literal306_tree=null;
        Node DecimalLiteral307_tree=null;
        Node char_literal308_tree=null;
        Node DecimalLiteral309_tree=null;
        Node char_literal310_tree=null;
        RewriteRuleTokenStream stream_180=new RewriteRuleTokenStream(adaptor,"token 180");
        RewriteRuleTokenStream stream_181=new RewriteRuleTokenStream(adaptor,"token 181");
        RewriteRuleTokenStream stream_147=new RewriteRuleTokenStream(adaptor,"token 147");
        RewriteRuleTokenStream stream_148=new RewriteRuleTokenStream(adaptor,"token 148");
        RewriteRuleTokenStream stream_149=new RewriteRuleTokenStream(adaptor,"token 149");
        RewriteRuleTokenStream stream_DecimalLiteral=new RewriteRuleTokenStream(adaptor,"token DecimalLiteral");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleTokenStream stream_168=new RewriteRuleTokenStream(adaptor,"token 168");
        RewriteRuleTokenStream stream_141=new RewriteRuleTokenStream(adaptor,"token 141");
        RewriteRuleSubtreeStream stream_expression=new RewriteRuleSubtreeStream(adaptor,"rule expression");
        RewriteRuleSubtreeStream stream_arguments=new RewriteRuleSubtreeStream(adaptor,"rule arguments");
        RewriteRuleSubtreeStream stream_common=new RewriteRuleSubtreeStream(adaptor,"rule common");
        RewriteRuleSubtreeStream stream_primitiveType=new RewriteRuleSubtreeStream(adaptor,"rule primitiveType");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return retval; }
            // JFSL.g:821:3: ( '.' Identifier arguments -> ^( CALL Identifier arguments ) | '.' Identifier -> ^( JOIN ^( IDENTIFIER Identifier ) ) | '.' primitiveType -> ^( JOIN primitiveType ) | '.' common -> ^( JOIN common ) | '.' '*' Identifier -> ^( JOIN_REFLEXIVE ^( IDENTIFIER Identifier ) ) | '.' '*' common -> ^( JOIN_REFLEXIVE common ) | '[' expression ']' -> ^( BRACKET expression ) | '[' ']' -> ^( BRACKET ) | '<' DecimalLiteral ( ',' DecimalLiteral )* '>' -> ^( PROJECTION ( DecimalLiteral )+ ) )
            int alt90=9;
            alt90 = dfa90.predict(input);
            switch (alt90) {
                case 1 :
                    // JFSL.g:821:5: '.' Identifier arguments
                    {
                    char_literal286=(Token)match(input,141,FOLLOW_141_in_selector4889); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_141.add(char_literal286);

                    Identifier287=(Token)match(input,Identifier,FOLLOW_Identifier_in_selector4891); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier287);

                    pushFollow(FOLLOW_arguments_in_selector4893);
                    arguments288=arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_arguments.add(arguments288.getTree());


                    // AST REWRITE
                    // elements: Identifier, arguments
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 821:30: -> ^( CALL Identifier arguments )
                    {
                        // JFSL.g:821:33: ^( CALL Identifier arguments )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(CALL, "CALL"), root_1);

                        adaptor.addChild(root_1, stream_Identifier.nextNode());
                        adaptor.addChild(root_1, stream_arguments.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:822:5: '.' Identifier
                    {
                    char_literal289=(Token)match(input,141,FOLLOW_141_in_selector4909); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_141.add(char_literal289);

                    Identifier290=(Token)match(input,Identifier,FOLLOW_Identifier_in_selector4911); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier290);



                    // AST REWRITE
                    // elements: Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 822:20: -> ^( JOIN ^( IDENTIFIER Identifier ) )
                    {
                        // JFSL.g:822:23: ^( JOIN ^( IDENTIFIER Identifier ) )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(JOIN, "JOIN"), root_1);

                        // JFSL.g:822:30: ^( IDENTIFIER Identifier )
                        {
                        Node root_2 = (Node)adaptor.nil();
                        root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(IDENTIFIER, "IDENTIFIER"), root_2);

                        adaptor.addChild(root_2, stream_Identifier.nextNode());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:823:5: '.' primitiveType
                    {
                    char_literal291=(Token)match(input,141,FOLLOW_141_in_selector4929); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_141.add(char_literal291);

                    pushFollow(FOLLOW_primitiveType_in_selector4931);
                    primitiveType292=primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_primitiveType.add(primitiveType292.getTree());


                    // AST REWRITE
                    // elements: primitiveType
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 823:23: -> ^( JOIN primitiveType )
                    {
                        // JFSL.g:823:26: ^( JOIN primitiveType )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(JOIN, "JOIN"), root_1);

                        adaptor.addChild(root_1, stream_primitiveType.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // JFSL.g:824:5: '.' common
                    {
                    char_literal293=(Token)match(input,141,FOLLOW_141_in_selector4945); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_141.add(char_literal293);

                    pushFollow(FOLLOW_common_in_selector4947);
                    common294=common();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_common.add(common294.getTree());


                    // AST REWRITE
                    // elements: common
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 824:16: -> ^( JOIN common )
                    {
                        // JFSL.g:824:19: ^( JOIN common )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(JOIN, "JOIN"), root_1);

                        adaptor.addChild(root_1, stream_common.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // JFSL.g:825:5: '.' '*' Identifier
                    {
                    char_literal295=(Token)match(input,141,FOLLOW_141_in_selector4961); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_141.add(char_literal295);

                    char_literal296=(Token)match(input,168,FOLLOW_168_in_selector4963); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_168.add(char_literal296);

                    Identifier297=(Token)match(input,Identifier,FOLLOW_Identifier_in_selector4965); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier297);



                    // AST REWRITE
                    // elements: Identifier
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 825:24: -> ^( JOIN_REFLEXIVE ^( IDENTIFIER Identifier ) )
                    {
                        // JFSL.g:825:27: ^( JOIN_REFLEXIVE ^( IDENTIFIER Identifier ) )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(JOIN_REFLEXIVE, "JOIN_REFLEXIVE"), root_1);

                        // JFSL.g:825:44: ^( IDENTIFIER Identifier )
                        {
                        Node root_2 = (Node)adaptor.nil();
                        root_2 = (Node)adaptor.becomeRoot((Node)adaptor.create(IDENTIFIER, "IDENTIFIER"), root_2);

                        adaptor.addChild(root_2, stream_Identifier.nextNode());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // JFSL.g:826:5: '.' '*' common
                    {
                    char_literal298=(Token)match(input,141,FOLLOW_141_in_selector4983); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_141.add(char_literal298);

                    char_literal299=(Token)match(input,168,FOLLOW_168_in_selector4985); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_168.add(char_literal299);

                    pushFollow(FOLLOW_common_in_selector4987);
                    common300=common();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_common.add(common300.getTree());


                    // AST REWRITE
                    // elements: common
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 826:20: -> ^( JOIN_REFLEXIVE common )
                    {
                        // JFSL.g:826:23: ^( JOIN_REFLEXIVE common )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(JOIN_REFLEXIVE, "JOIN_REFLEXIVE"), root_1);

                        adaptor.addChild(root_1, stream_common.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 7 :
                    // JFSL.g:827:5: '[' expression ']'
                    {
                    char_literal301=(Token)match(input,180,FOLLOW_180_in_selector5001); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_180.add(char_literal301);

                    pushFollow(FOLLOW_expression_in_selector5003);
                    expression302=expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_expression.add(expression302.getTree());
                    char_literal303=(Token)match(input,181,FOLLOW_181_in_selector5005); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_181.add(char_literal303);



                    // AST REWRITE
                    // elements: expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 827:24: -> ^( BRACKET expression )
                    {
                        // JFSL.g:827:27: ^( BRACKET expression )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BRACKET, "BRACKET"), root_1);

                        adaptor.addChild(root_1, stream_expression.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 8 :
                    // JFSL.g:828:5: '[' ']'
                    {
                    char_literal304=(Token)match(input,180,FOLLOW_180_in_selector5021); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_180.add(char_literal304);

                    char_literal305=(Token)match(input,181,FOLLOW_181_in_selector5023); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_181.add(char_literal305);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 828:13: -> ^( BRACKET )
                    {
                        // JFSL.g:828:16: ^( BRACKET )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(BRACKET, "BRACKET"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 9 :
                    // JFSL.g:829:5: '<' DecimalLiteral ( ',' DecimalLiteral )* '>'
                    {
                    char_literal306=(Token)match(input,147,FOLLOW_147_in_selector5036); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_147.add(char_literal306);

                    DecimalLiteral307=(Token)match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_selector5038); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DecimalLiteral.add(DecimalLiteral307);

                    // JFSL.g:829:24: ( ',' DecimalLiteral )*
                    loop89:
                    do {
                        int alt89=2;
                        int LA89_0 = input.LA(1);

                        if ( (LA89_0==148) ) {
                            alt89=1;
                        }


                        switch (alt89) {
                    	case 1 :
                    	    // JFSL.g:829:25: ',' DecimalLiteral
                    	    {
                    	    char_literal308=(Token)match(input,148,FOLLOW_148_in_selector5041); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_148.add(char_literal308);

                    	    DecimalLiteral309=(Token)match(input,DecimalLiteral,FOLLOW_DecimalLiteral_in_selector5043); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_DecimalLiteral.add(DecimalLiteral309);


                    	    }
                    	    break;

                    	default :
                    	    break loop89;
                        }
                    } while (true);

                    char_literal310=(Token)match(input,149,FOLLOW_149_in_selector5047); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_149.add(char_literal310);



                    // AST REWRITE
                    // elements: DecimalLiteral
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 829:50: -> ^( PROJECTION ( DecimalLiteral )+ )
                    {
                        // JFSL.g:829:53: ^( PROJECTION ( DecimalLiteral )+ )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(PROJECTION, "PROJECTION"), root_1);

                        if ( !(stream_DecimalLiteral.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_DecimalLiteral.hasNext() ) {
                            adaptor.addChild(root_1, stream_DecimalLiteral.nextNode());

                        }
                        stream_DecimalLiteral.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 75, selector_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "selector"

    public static class specUnaryOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "specUnaryOp"
    // JFSL.g:833:1: specUnaryOp : (x= '@' '^' -> ^( OP_CLOSURE $x) | x= '^' -> ^( OP_CLOSURE $x) | x= '@' '~' -> ^( OP_TRANSPOSE $x) | x= '~' -> ^( OP_TRANSPOSE $x) );
    public final JFSLParser.specUnaryOp_return specUnaryOp() throws RecognitionException {
        JFSLParser.specUnaryOp_return retval = new JFSLParser.specUnaryOp_return();
        retval.start = input.LT(1);
        int specUnaryOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;
        Token char_literal311=null;
        Token char_literal312=null;

        Node x_tree=null;
        Node char_literal311_tree=null;
        Node char_literal312_tree=null;
        RewriteRuleTokenStream stream_203=new RewriteRuleTokenStream(adaptor,"token 203");
        RewriteRuleTokenStream stream_200=new RewriteRuleTokenStream(adaptor,"token 200");
        RewriteRuleTokenStream stream_190=new RewriteRuleTokenStream(adaptor,"token 190");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return retval; }
            // JFSL.g:834:3: (x= '@' '^' -> ^( OP_CLOSURE $x) | x= '^' -> ^( OP_CLOSURE $x) | x= '@' '~' -> ^( OP_TRANSPOSE $x) | x= '~' -> ^( OP_TRANSPOSE $x) )
            int alt91=4;
            switch ( input.LA(1) ) {
            case 200:
                {
                int LA91_1 = input.LA(2);

                if ( (LA91_1==190) ) {
                    alt91=1;
                }
                else if ( (LA91_1==203) ) {
                    alt91=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 91, 1, input);

                    throw nvae;
                }
                }
                break;
            case 190:
                {
                alt91=2;
                }
                break;
            case 203:
                {
                alt91=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 91, 0, input);

                throw nvae;
            }

            switch (alt91) {
                case 1 :
                    // JFSL.g:834:5: x= '@' '^'
                    {
                    x=(Token)match(input,200,FOLLOW_200_in_specUnaryOp5076); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_200.add(x);

                    char_literal311=(Token)match(input,190,FOLLOW_190_in_specUnaryOp5078); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_190.add(char_literal311);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 834:15: -> ^( OP_CLOSURE $x)
                    {
                        // JFSL.g:834:18: ^( OP_CLOSURE $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_CLOSURE, "OP_CLOSURE"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:835:5: x= '^'
                    {
                    x=(Token)match(input,190,FOLLOW_190_in_specUnaryOp5095); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_190.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 835:15: -> ^( OP_CLOSURE $x)
                    {
                        // JFSL.g:835:18: ^( OP_CLOSURE $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_CLOSURE, "OP_CLOSURE"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:836:5: x= '@' '~'
                    {
                    x=(Token)match(input,200,FOLLOW_200_in_specUnaryOp5116); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_200.add(x);

                    char_literal312=(Token)match(input,203,FOLLOW_203_in_specUnaryOp5118); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_203.add(char_literal312);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 836:15: -> ^( OP_TRANSPOSE $x)
                    {
                        // JFSL.g:836:18: ^( OP_TRANSPOSE $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_TRANSPOSE, "OP_TRANSPOSE"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // JFSL.g:837:5: x= '~'
                    {
                    x=(Token)match(input,203,FOLLOW_203_in_specUnaryOp5135); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_203.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 837:15: -> ^( OP_TRANSPOSE $x)
                    {
                        // JFSL.g:837:18: ^( OP_TRANSPOSE $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_TRANSPOSE, "OP_TRANSPOSE"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 76, specUnaryOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "specUnaryOp"

    public static class setDeclOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "setDeclOp"
    // JFSL.g:840:1: setDeclOp : (x= 'one' -> ^( OP_SET_ONE $x) | x= 'some' -> ^( OP_SET_SOME $x) | x= 'lone' -> ^( OP_SET_LONE $x) );
    public final JFSLParser.setDeclOp_return setDeclOp() throws RecognitionException {
        JFSLParser.setDeclOp_return retval = new JFSLParser.setDeclOp_return();
        retval.start = input.LT(1);
        int setDeclOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;

        Node x_tree=null;
        RewriteRuleTokenStream stream_211=new RewriteRuleTokenStream(adaptor,"token 211");
        RewriteRuleTokenStream stream_212=new RewriteRuleTokenStream(adaptor,"token 212");
        RewriteRuleTokenStream stream_213=new RewriteRuleTokenStream(adaptor,"token 213");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return retval; }
            // JFSL.g:841:3: (x= 'one' -> ^( OP_SET_ONE $x) | x= 'some' -> ^( OP_SET_SOME $x) | x= 'lone' -> ^( OP_SET_LONE $x) )
            int alt92=3;
            switch ( input.LA(1) ) {
            case 211:
                {
                alt92=1;
                }
                break;
            case 212:
                {
                alt92=2;
                }
                break;
            case 213:
                {
                alt92=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 92, 0, input);

                throw nvae;
            }

            switch (alt92) {
                case 1 :
                    // JFSL.g:841:5: x= 'one'
                    {
                    x=(Token)match(input,211,FOLLOW_211_in_setDeclOp5170); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_211.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 841:15: -> ^( OP_SET_ONE $x)
                    {
                        // JFSL.g:841:18: ^( OP_SET_ONE $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SET_ONE, "OP_SET_ONE"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // JFSL.g:842:5: x= 'some'
                    {
                    x=(Token)match(input,212,FOLLOW_212_in_setDeclOp5189); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_212.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 842:15: -> ^( OP_SET_SOME $x)
                    {
                        // JFSL.g:842:18: ^( OP_SET_SOME $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SET_SOME, "OP_SET_SOME"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:843:5: x= 'lone'
                    {
                    x=(Token)match(input,213,FOLLOW_213_in_setDeclOp5208); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_213.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 843:15: -> ^( OP_SET_LONE $x)
                    {
                        // JFSL.g:843:18: ^( OP_SET_LONE $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SET_LONE, "OP_SET_LONE"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 77, setDeclOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "setDeclOp"

    public static class setUnaryOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "setUnaryOp"
    // JFSL.g:845:1: setUnaryOp : ( setDeclOp | x= 'no' -> ^( OP_SET_NO $x) | x= 'sum' -> ^( OP_SET_SUM $x) );
    public final JFSLParser.setUnaryOp_return setUnaryOp() throws RecognitionException {
        JFSLParser.setUnaryOp_return retval = new JFSLParser.setUnaryOp_return();
        retval.start = input.LT(1);
        int setUnaryOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;
        JFSLParser.setDeclOp_return setDeclOp313 = null;


        Node x_tree=null;
        RewriteRuleTokenStream stream_215=new RewriteRuleTokenStream(adaptor,"token 215");
        RewriteRuleTokenStream stream_214=new RewriteRuleTokenStream(adaptor,"token 214");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return retval; }
            // JFSL.g:846:3: ( setDeclOp | x= 'no' -> ^( OP_SET_NO $x) | x= 'sum' -> ^( OP_SET_SUM $x) )
            int alt93=3;
            switch ( input.LA(1) ) {
            case 211:
            case 212:
            case 213:
                {
                alt93=1;
                }
                break;
            case 214:
                {
                alt93=2;
                }
                break;
            case 215:
                {
                alt93=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 93, 0, input);

                throw nvae;
            }

            switch (alt93) {
                case 1 :
                    // JFSL.g:846:5: setDeclOp
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_setDeclOp_in_setUnaryOp5236);
                    setDeclOp313=setDeclOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, setDeclOp313.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:847:5: x= 'no'
                    {
                    x=(Token)match(input,214,FOLLOW_214_in_setUnaryOp5244); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_214.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 847:13: -> ^( OP_SET_NO $x)
                    {
                        // JFSL.g:847:16: ^( OP_SET_NO $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SET_NO, "OP_SET_NO"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:848:5: x= 'sum'
                    {
                    x=(Token)match(input,215,FOLLOW_215_in_setUnaryOp5262); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_215.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 848:15: -> ^( OP_SET_SUM $x)
                    {
                        // JFSL.g:848:18: ^( OP_SET_SUM $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SET_SUM, "OP_SET_SUM"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 78, setUnaryOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "setUnaryOp"

    public static class setQuantOp_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "setQuantOp"
    // JFSL.g:851:1: setQuantOp : ( setUnaryOp | x= '#' -> ^( OP_SET_NUM $x) | x= 'all' -> ^( OP_SET_ALL $x) | x= 'exists' -> ^( OP_SET_EXISTS $x) );
    public final JFSLParser.setQuantOp_return setQuantOp() throws RecognitionException {
        JFSLParser.setQuantOp_return retval = new JFSLParser.setQuantOp_return();
        retval.start = input.LT(1);
        int setQuantOp_StartIndex = input.index();
        Node root_0 = null;

        Token x=null;
        JFSLParser.setUnaryOp_return setUnaryOp314 = null;


        Node x_tree=null;
        RewriteRuleTokenStream stream_216=new RewriteRuleTokenStream(adaptor,"token 216");
        RewriteRuleTokenStream stream_217=new RewriteRuleTokenStream(adaptor,"token 217");
        RewriteRuleTokenStream stream_197=new RewriteRuleTokenStream(adaptor,"token 197");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return retval; }
            // JFSL.g:852:3: ( setUnaryOp | x= '#' -> ^( OP_SET_NUM $x) | x= 'all' -> ^( OP_SET_ALL $x) | x= 'exists' -> ^( OP_SET_EXISTS $x) )
            int alt94=4;
            switch ( input.LA(1) ) {
            case 211:
            case 212:
            case 213:
            case 214:
            case 215:
                {
                alt94=1;
                }
                break;
            case 197:
                {
                alt94=2;
                }
                break;
            case 216:
                {
                alt94=3;
                }
                break;
            case 217:
                {
                alt94=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 94, 0, input);

                throw nvae;
            }

            switch (alt94) {
                case 1 :
                    // JFSL.g:852:5: setUnaryOp
                    {
                    root_0 = (Node)adaptor.nil();

                    pushFollow(FOLLOW_setUnaryOp_in_setQuantOp5289);
                    setUnaryOp314=setUnaryOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, setUnaryOp314.getTree());

                    }
                    break;
                case 2 :
                    // JFSL.g:853:5: x= '#'
                    {
                    x=(Token)match(input,197,FOLLOW_197_in_setQuantOp5297); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_197.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 853:12: -> ^( OP_SET_NUM $x)
                    {
                        // JFSL.g:853:15: ^( OP_SET_NUM $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SET_NUM, "OP_SET_NUM"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // JFSL.g:854:5: x= 'all'
                    {
                    x=(Token)match(input,216,FOLLOW_216_in_setQuantOp5315); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_216.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 854:15: -> ^( OP_SET_ALL $x)
                    {
                        // JFSL.g:854:18: ^( OP_SET_ALL $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SET_ALL, "OP_SET_ALL"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // JFSL.g:855:5: x= 'exists'
                    {
                    x=(Token)match(input,217,FOLLOW_217_in_setQuantOp5335); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_217.add(x);



                    // AST REWRITE
                    // elements: x
                    // token labels: x
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_x=new RewriteRuleTokenStream(adaptor,"token x",x);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 855:16: -> ^( OP_SET_EXISTS $x)
                    {
                        // JFSL.g:855:19: ^( OP_SET_EXISTS $x)
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(OP_SET_EXISTS, "OP_SET_EXISTS"), root_1);

                        adaptor.addChild(root_1, stream_x.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Node)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Node)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 79, setQuantOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "setQuantOp"

    // $ANTLR start synpred13_JFSL
    public final void synpred13_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:438:55: ( ';' specCase )
        // JFSL.g:438:55: ';' specCase
        {
        match(input,140,FOLLOW_140_in_synpred13_JFSL1132); if (state.failed) return ;
        pushFollow(FOLLOW_specCase_in_synpred13_JFSL1134);
        specCase();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_JFSL

    // $ANTLR start synpred26_JFSL
    public final void synpred26_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:458:5: ( ( 'public' | 'private' )? Identifier ':' declarationMult additiveExpression 'from' frame ( '|' expression )? )
        // JFSL.g:458:5: ( 'public' | 'private' )? Identifier ':' declarationMult additiveExpression 'from' frame ( '|' expression )?
        {
        // JFSL.g:458:5: ( 'public' | 'private' )?
        int alt99=2;
        int LA99_0 = input.LA(1);

        if ( ((LA99_0>=160 && LA99_0<=161)) ) {
            alt99=1;
        }
        switch (alt99) {
            case 1 :
                // JFSL.g:
                {
                if ( (input.LA(1)>=160 && input.LA(1)<=161) ) {
                    input.consume();
                    state.errorRecovery=false;state.failed=false;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    MismatchedSetException mse = new MismatchedSetException(null,input);
                    throw mse;
                }


                }
                break;

        }

        match(input,Identifier,FOLLOW_Identifier_in_synpred26_JFSL1391); if (state.failed) return ;
        match(input,162,FOLLOW_162_in_synpred26_JFSL1393); if (state.failed) return ;
        pushFollow(FOLLOW_declarationMult_in_synpred26_JFSL1395);
        declarationMult();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_additiveExpression_in_synpred26_JFSL1397);
        additiveExpression();

        state._fsp--;
        if (state.failed) return ;
        match(input,163,FOLLOW_163_in_synpred26_JFSL1399); if (state.failed) return ;
        pushFollow(FOLLOW_frame_in_synpred26_JFSL1401);
        frame();

        state._fsp--;
        if (state.failed) return ;
        // JFSL.g:458:92: ( '|' expression )?
        int alt100=2;
        int LA100_0 = input.LA(1);

        if ( (LA100_0==164) ) {
            alt100=1;
        }
        switch (alt100) {
            case 1 :
                // JFSL.g:458:93: '|' expression
                {
                match(input,164,FOLLOW_164_in_synpred26_JFSL1404); if (state.failed) return ;
                pushFollow(FOLLOW_expression_in_synpred26_JFSL1406);
                expression();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }


        }
    }
    // $ANTLR end synpred26_JFSL

    // $ANTLR start synpred36_JFSL
    public final void synpred36_JFSL_fragment() throws RecognitionException {   
        JFSLParser.expression_return ff = null;


        // JFSL.g:481:34: ( '|' ff= expression )
        // JFSL.g:481:34: '|' ff= expression
        {
        match(input,164,FOLLOW_164_in_synpred36_JFSL1630); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred36_JFSL1634);
        ff=expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred36_JFSL

    // $ANTLR start synpred40_JFSL
    public final void synpred40_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:491:5: ( ( selector )* storeWildCard )
        // JFSL.g:491:5: ( selector )* storeWildCard
        {
        // JFSL.g:491:5: ( selector )*
        loop101:
        do {
            int alt101=2;
            int LA101_0 = input.LA(1);

            if ( (LA101_0==141) ) {
                int LA101_1 = input.LA(2);

                if ( (LA101_1==168) ) {
                    int LA101_3 = input.LA(3);

                    if ( (LA101_3==Identifier||LA101_3==145||LA101_3==151||LA101_3==190||LA101_3==200||(LA101_3>=203 && LA101_3<=210)) ) {
                        alt101=1;
                    }


                }
                else if ( (LA101_1==Identifier||LA101_1==145||LA101_1==151||(LA101_1>=172 && LA101_1<=179)||LA101_1==190||LA101_1==200||(LA101_1>=203 && LA101_1<=210)) ) {
                    alt101=1;
                }


            }
            else if ( (LA101_0==147||LA101_0==180) ) {
                alt101=1;
            }


            switch (alt101) {
        	case 1 :
        	    // JFSL.g:0:0: selector
        	    {
        	    pushFollow(FOLLOW_selector_in_synpred40_JFSL1726);
        	    selector();

        	    state._fsp--;
        	    if (state.failed) return ;

        	    }
        	    break;

        	default :
        	    break loop101;
            }
        } while (true);

        pushFollow(FOLLOW_storeWildCard_in_synpred40_JFSL1729);
        storeWildCard();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred40_JFSL

    // $ANTLR start synpred59_JFSL
    public final void synpred59_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:535:39: ( ( '[' ']' ) )
        // JFSL.g:535:39: ( '[' ']' )
        {
        // JFSL.g:535:39: ( '[' ']' )
        // JFSL.g:535:40: '[' ']'
        {
        match(input,180,FOLLOW_180_in_synpred59_JFSL2113); if (state.failed) return ;
        match(input,181,FOLLOW_181_in_synpred59_JFSL2115); if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred59_JFSL

    // $ANTLR start synpred61_JFSL
    public final void synpred61_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:536:29: ( ( '[' ']' ) )
        // JFSL.g:536:29: ( '[' ']' )
        {
        // JFSL.g:536:29: ( '[' ']' )
        // JFSL.g:536:30: '[' ']'
        {
        match(input,180,FOLLOW_180_in_synpred61_JFSL2143); if (state.failed) return ;
        match(input,181,FOLLOW_181_in_synpred61_JFSL2145); if (state.failed) return ;

        }


        }
    }
    // $ANTLR end synpred61_JFSL

    // $ANTLR start synpred65_JFSL
    public final void synpred65_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:566:7: ( '?' expression ':' expression )
        // JFSL.g:566:7: '?' expression ':' expression
        {
        match(input,182,FOLLOW_182_in_synpred65_JFSL2332); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred65_JFSL2334);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,162,FOLLOW_162_in_synpred65_JFSL2336); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred65_JFSL2338);
        expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred65_JFSL

    // $ANTLR start synpred66_JFSL
    public final void synpred66_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:571:5: ( setQuantOp decls '|' expression )
        // JFSL.g:571:5: setQuantOp decls '|' expression
        {
        pushFollow(FOLLOW_setQuantOp_in_synpred66_JFSL2374);
        setQuantOp();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_decls_in_synpred66_JFSL2376);
        decls();

        state._fsp--;
        if (state.failed) return ;
        match(input,164,FOLLOW_164_in_synpred66_JFSL2379); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred66_JFSL2381);
        expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred66_JFSL

    // $ANTLR start synpred67_JFSL
    public final void synpred67_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:572:5: ( setQuantOp mdecls '|' expression )
        // JFSL.g:572:5: setQuantOp mdecls '|' expression
        {
        pushFollow(FOLLOW_setQuantOp_in_synpred67_JFSL2399);
        setQuantOp();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_mdecls_in_synpred67_JFSL2401);
        mdecls();

        state._fsp--;
        if (state.failed) return ;
        match(input,164,FOLLOW_164_in_synpred67_JFSL2403); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred67_JFSL2405);
        expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred67_JFSL

    // $ANTLR start synpred76_JFSL
    public final void synpred76_JFSL_fragment() throws RecognitionException {   
        JFSLParser.exclusiveOrExpression_return a = null;


        // JFSL.g:610:9: ( '|' a= exclusiveOrExpression )
        // JFSL.g:610:9: '|' a= exclusiveOrExpression
        {
        match(input,164,FOLLOW_164_in_synpred76_JFSL2775); if (state.failed) return ;
        pushFollow(FOLLOW_exclusiveOrExpression_in_synpred76_JFSL2779);
        a=exclusiveOrExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred76_JFSL

    // $ANTLR start synpred107_JFSL
    public final void synpred107_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:762:5: ( '~' unaryExpression )
        // JFSL.g:762:5: '~' unaryExpression
        {
        match(input,203,FOLLOW_203_in_synpred107_JFSL4237); if (state.failed) return ;
        pushFollow(FOLLOW_unaryExpression_in_synpred107_JFSL4239);
        unaryExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred107_JFSL

    // $ANTLR start synpred109_JFSL
    public final void synpred109_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:764:5: ( castExpression )
        // JFSL.g:764:5: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred109_JFSL4277);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred109_JFSL

    // $ANTLR start synpred116_JFSL
    public final void synpred116_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:789:15: ( selector )
        // JFSL.g:789:15: selector
        {
        pushFollow(FOLLOW_selector_in_synpred116_JFSL4524);
        selector();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred116_JFSL

    // $ANTLR start synpred132_JFSL
    public final void synpred132_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:815:5: ( typeDisambiguous )
        // JFSL.g:815:5: typeDisambiguous
        {
        pushFollow(FOLLOW_typeDisambiguous_in_synpred132_JFSL4837);
        typeDisambiguous();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred132_JFSL

    // $ANTLR start synpred133_JFSL
    public final void synpred133_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:816:17: ( '.' Identifier )
        // JFSL.g:816:17: '.' Identifier
        {
        match(input,141,FOLLOW_141_in_synpred133_JFSL4846); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred133_JFSL4848); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred133_JFSL

    // $ANTLR start synpred134_JFSL
    public final void synpred134_JFSL_fragment() throws RecognitionException {   
        // JFSL.g:816:34: ( typeParameters2 )
        // JFSL.g:816:34: typeParameters2
        {
        pushFollow(FOLLOW_typeParameters2_in_synpred134_JFSL4852);
        typeParameters2();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred134_JFSL

    // Delegated rules

    public final boolean synpred59_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred59_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred109_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred109_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred66_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred66_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred76_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred76_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred65_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred65_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred116_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred116_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred132_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred132_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred40_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred40_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred13_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred13_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred26_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred26_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred61_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred61_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred107_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred107_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred67_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred67_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred36_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred36_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred134_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred134_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred133_JFSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred133_JFSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA41 dfa41 = new DFA41(this);
    protected DFA45 dfa45 = new DFA45(this);
    protected DFA51 dfa51 = new DFA51(this);
    protected DFA75 dfa75 = new DFA75(this);
    protected DFA82 dfa82 = new DFA82(this);
    protected DFA84 dfa84 = new DFA84(this);
    protected DFA85 dfa85 = new DFA85(this);
    protected DFA88 dfa88 = new DFA88(this);
    protected DFA87 dfa87 = new DFA87(this);
    protected DFA90 dfa90 = new DFA90(this);
    static final String DFA41_eotS =
        "\13\uffff";
    static final String DFA41_eofS =
        "\2\uffff\1\5\3\uffff\1\5\2\uffff\1\5\1\uffff";
    static final String DFA41_minS =
        "\1\172\1\uffff\3\172\1\uffff\1\172\1\u0094\2\172\1\u0094";
    static final String DFA41_maxS =
        "\1\u00b3\1\uffff\1\u00bf\1\172\1\u00b8\1\uffff\1\u00bf\1\u0095\1"+
        "\172\1\u00bf\1\u0095";
    static final String DFA41_acceptS =
        "\1\uffff\1\1\3\uffff\1\2\5\uffff";
    static final String DFA41_specialS =
        "\13\uffff}>";
    static final String[] DFA41_transitionS = {
            "\1\2\61\uffff\10\1",
            "",
            "\1\5\22\uffff\1\3\4\uffff\1\5\1\4\1\5\3\uffff\1\5\11\uffff"+
            "\3\5\17\uffff\1\1\13\5",
            "\1\6",
            "\1\7\74\uffff\2\5",
            "",
            "\1\5\22\uffff\1\3\4\uffff\1\5\1\4\1\5\3\uffff\1\5\11\uffff"+
            "\3\5\17\uffff\1\1\13\5",
            "\1\10\1\11",
            "\1\12",
            "\1\5\27\uffff\3\5\3\uffff\1\5\11\uffff\3\5\17\uffff\1\1\13"+
            "\5",
            "\1\10\1\11"
    };

    static final short[] DFA41_eot = DFA.unpackEncodedString(DFA41_eotS);
    static final short[] DFA41_eof = DFA.unpackEncodedString(DFA41_eofS);
    static final char[] DFA41_min = DFA.unpackEncodedStringToUnsignedChars(DFA41_minS);
    static final char[] DFA41_max = DFA.unpackEncodedStringToUnsignedChars(DFA41_maxS);
    static final short[] DFA41_accept = DFA.unpackEncodedString(DFA41_acceptS);
    static final short[] DFA41_special = DFA.unpackEncodedString(DFA41_specialS);
    static final short[][] DFA41_transition;

    static {
        int numStates = DFA41_transitionS.length;
        DFA41_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA41_transition[i] = DFA.unpackEncodedString(DFA41_transitionS[i]);
        }
    }

    class DFA41 extends DFA {

        public DFA41(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 41;
            this.eot = DFA41_eot;
            this.eof = DFA41_eof;
            this.min = DFA41_min;
            this.max = DFA41_max;
            this.accept = DFA41_accept;
            this.special = DFA41_special;
            this.transition = DFA41_transition;
        }
        public String getDescription() {
            return "539:1: type : ( typeDisambiguous | typeName );";
        }
    }
    static final String DFA45_eotS =
        "\52\uffff";
    static final String DFA45_eofS =
        "\52\uffff";
    static final String DFA45_minS =
        "\1\172\10\0\41\uffff";
    static final String DFA45_maxS =
        "\1\u00d9\10\0\41\uffff";
    static final String DFA45_acceptS =
        "\11\uffff\1\3\36\uffff\1\1\1\2";
    static final String DFA45_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\41\uffff}>";
    static final String[] DFA45_transitionS = {
            "\7\11\20\uffff\1\11\5\uffff\1\11\21\uffff\13\11\4\uffff\1\11"+
            "\5\uffff\1\11\4\uffff\2\11\1\6\2\uffff\1\11\2\uffff\10\11\1"+
            "\1\1\2\1\3\1\4\1\5\1\7\1\10",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA45_eot = DFA.unpackEncodedString(DFA45_eotS);
    static final short[] DFA45_eof = DFA.unpackEncodedString(DFA45_eofS);
    static final char[] DFA45_min = DFA.unpackEncodedStringToUnsignedChars(DFA45_minS);
    static final char[] DFA45_max = DFA.unpackEncodedStringToUnsignedChars(DFA45_maxS);
    static final short[] DFA45_accept = DFA.unpackEncodedString(DFA45_acceptS);
    static final short[] DFA45_special = DFA.unpackEncodedString(DFA45_specialS);
    static final short[][] DFA45_transition;

    static {
        int numStates = DFA45_transitionS.length;
        DFA45_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA45_transition[i] = DFA.unpackEncodedString(DFA45_transitionS[i]);
        }
    }

    class DFA45 extends DFA {

        public DFA45(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 45;
            this.eot = DFA45_eot;
            this.eof = DFA45_eof;
            this.min = DFA45_min;
            this.max = DFA45_max;
            this.accept = DFA45_accept;
            this.special = DFA45_special;
            this.transition = DFA45_transition;
        }
        public String getDescription() {
            return "570:1: quantifiedExpression : ( setQuantOp decls '|' expression -> ^( QUANTIFY setQuantOp decls expression ) | setQuantOp mdecls '|' expression -> ^( QUANTIFY setQuantOp mdecls expression ) | logicalExpression );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA45_1 = input.LA(1);

                         
                        int index45_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred66_JFSL()) ) {s = 40;}

                        else if ( (synpred67_JFSL()) ) {s = 41;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index45_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA45_2 = input.LA(1);

                         
                        int index45_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred66_JFSL()) ) {s = 40;}

                        else if ( (synpred67_JFSL()) ) {s = 41;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index45_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA45_3 = input.LA(1);

                         
                        int index45_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred66_JFSL()) ) {s = 40;}

                        else if ( (synpred67_JFSL()) ) {s = 41;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index45_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA45_4 = input.LA(1);

                         
                        int index45_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred66_JFSL()) ) {s = 40;}

                        else if ( (synpred67_JFSL()) ) {s = 41;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index45_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA45_5 = input.LA(1);

                         
                        int index45_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred66_JFSL()) ) {s = 40;}

                        else if ( (synpred67_JFSL()) ) {s = 41;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index45_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA45_6 = input.LA(1);

                         
                        int index45_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred66_JFSL()) ) {s = 40;}

                        else if ( (synpred67_JFSL()) ) {s = 41;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index45_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA45_7 = input.LA(1);

                         
                        int index45_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred66_JFSL()) ) {s = 40;}

                        else if ( (synpred67_JFSL()) ) {s = 41;}

                         
                        input.seek(index45_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA45_8 = input.LA(1);

                         
                        int index45_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred66_JFSL()) ) {s = 40;}

                        else if ( (synpred67_JFSL()) ) {s = 41;}

                         
                        input.seek(index45_8);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 45, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA51_eotS =
        "\22\uffff";
    static final String DFA51_eofS =
        "\1\1\21\uffff";
    static final String DFA51_minS =
        "\1\u0092\14\uffff\1\0\4\uffff";
    static final String DFA51_maxS =
        "\1\u00bd\14\uffff\1\0\4\uffff";
    static final String DFA51_acceptS =
        "\1\uffff\1\2\17\uffff\1\1";
    static final String DFA51_specialS =
        "\15\uffff\1\0\4\uffff}>";
    static final String[] DFA51_transitionS = {
            "\3\1\3\uffff\1\1\11\uffff\2\1\1\15\20\uffff\3\1\1\uffff\5\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA51_eot = DFA.unpackEncodedString(DFA51_eotS);
    static final short[] DFA51_eof = DFA.unpackEncodedString(DFA51_eofS);
    static final char[] DFA51_min = DFA.unpackEncodedStringToUnsignedChars(DFA51_minS);
    static final char[] DFA51_max = DFA.unpackEncodedStringToUnsignedChars(DFA51_maxS);
    static final short[] DFA51_accept = DFA.unpackEncodedString(DFA51_acceptS);
    static final short[] DFA51_special = DFA.unpackEncodedString(DFA51_specialS);
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA51_transition[i] = DFA.unpackEncodedString(DFA51_transitionS[i]);
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }
        public String getDescription() {
            return "()* loopback of 610:7: ( '|' a= exclusiveOrExpression -> ^( BINARY OP_BIT_OR $inclusiveOrExpression $a) )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA51_13 = input.LA(1);

                         
                        int index51_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred76_JFSL()) ) {s = 17;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index51_13);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 51, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA75_eotS =
        "\40\uffff";
    static final String DFA75_eofS =
        "\40\uffff";
    static final String DFA75_minS =
        "\1\172\1\0\1\uffff\1\0\34\uffff";
    static final String DFA75_maxS =
        "\1\u00d2\1\0\1\uffff\1\0\34\uffff";
    static final String DFA75_acceptS =
        "\2\uffff\1\2\1\uffff\1\4\31\uffff\1\1\1\3";
    static final String DFA75_specialS =
        "\1\uffff\1\0\1\uffff\1\1\34\uffff}>";
    static final String[] DFA75_transitionS = {
            "\7\4\20\uffff\1\4\5\uffff\1\3\21\uffff\13\4\4\uffff\1\2\5\uffff"+
            "\1\4\11\uffff\1\4\2\uffff\1\1\7\4",
            "\1\uffff",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA75_eot = DFA.unpackEncodedString(DFA75_eotS);
    static final short[] DFA75_eof = DFA.unpackEncodedString(DFA75_eofS);
    static final char[] DFA75_min = DFA.unpackEncodedStringToUnsignedChars(DFA75_minS);
    static final char[] DFA75_max = DFA.unpackEncodedStringToUnsignedChars(DFA75_maxS);
    static final short[] DFA75_accept = DFA.unpackEncodedString(DFA75_acceptS);
    static final short[] DFA75_special = DFA.unpackEncodedString(DFA75_specialS);
    static final short[][] DFA75_transition;

    static {
        int numStates = DFA75_transitionS.length;
        DFA75_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA75_transition[i] = DFA.unpackEncodedString(DFA75_transitionS[i]);
        }
    }

    class DFA75 extends DFA {

        public DFA75(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 75;
            this.eot = DFA75_eot;
            this.eof = DFA75_eof;
            this.min = DFA75_min;
            this.max = DFA75_max;
            this.accept = DFA75_accept;
            this.special = DFA75_special;
            this.transition = DFA75_transition;
        }
        public String getDescription() {
            return "761:1: unaryExpressionNotPlusMinus : ( '~' unaryExpression -> ^( UNARY OP_BIT_NOT_OR_TRANSPOSE unaryExpression ) | '!' unaryExpression -> ^( UNARY OP_NOT unaryExpression ) | castExpression | joinExpression );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA75_1 = input.LA(1);

                         
                        int index75_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_JFSL()) ) {s = 30;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index75_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA75_3 = input.LA(1);

                         
                        int index75_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred109_JFSL()) ) {s = 31;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index75_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 75, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA82_eotS =
        "\42\uffff";
    static final String DFA82_eofS =
        "\1\2\41\uffff";
    static final String DFA82_minS =
        "\1\u008d\1\0\40\uffff";
    static final String DFA82_maxS =
        "\1\u00ca\1\0\40\uffff";
    static final String DFA82_acceptS =
        "\2\uffff\1\2\35\uffff\1\1\1\uffff";
    static final String DFA82_specialS =
        "\1\uffff\1\0\40\uffff}>";
    static final String[] DFA82_transitionS = {
            "\1\40\4\uffff\1\2\1\1\2\2\2\uffff\1\2\11\uffff\3\2\3\uffff\1"+
            "\2\13\uffff\1\40\20\2\1\uffff\5\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA82_eot = DFA.unpackEncodedString(DFA82_eotS);
    static final short[] DFA82_eof = DFA.unpackEncodedString(DFA82_eofS);
    static final char[] DFA82_min = DFA.unpackEncodedStringToUnsignedChars(DFA82_minS);
    static final char[] DFA82_max = DFA.unpackEncodedStringToUnsignedChars(DFA82_maxS);
    static final short[] DFA82_accept = DFA.unpackEncodedString(DFA82_acceptS);
    static final short[] DFA82_special = DFA.unpackEncodedString(DFA82_specialS);
    static final short[][] DFA82_transition;

    static {
        int numStates = DFA82_transitionS.length;
        DFA82_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA82_transition[i] = DFA.unpackEncodedString(DFA82_transitionS[i]);
        }
    }

    class DFA82 extends DFA {

        public DFA82(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 82;
            this.eot = DFA82_eot;
            this.eof = DFA82_eof;
            this.min = DFA82_min;
            this.max = DFA82_max;
            this.accept = DFA82_accept;
            this.special = DFA82_special;
            this.transition = DFA82_transition;
        }
        public String getDescription() {
            return "()* loopback of 789:15: ( selector )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA82_1 = input.LA(1);

                         
                        int index82_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred116_JFSL()) ) {s = 32;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index82_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 82, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA84_eotS =
        "\16\uffff";
    static final String DFA84_eofS =
        "\16\uffff";
    static final String DFA84_minS =
        "\1\u0091\11\uffff\1\172\1\u008d\2\uffff";
    static final String DFA84_maxS =
        "\1\u00d2\11\uffff\1\u00d2\1\u00ca\2\uffff";
    static final String DFA84_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\2\uffff\1\13\1\12";
    static final String DFA84_specialS =
        "\16\uffff}>";
    static final String[] DFA84_transitionS = {
            "\1\12\5\uffff\1\1\46\uffff\1\11\11\uffff\1\11\2\uffff\1\11\1"+
            "\2\1\3\1\4\1\5\1\6\1\7\1\10",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\13\6\14\20\uffff\1\14\5\uffff\1\14\21\uffff\13\14\4\uffff"+
            "\1\14\5\uffff\1\14\4\uffff\3\14\2\uffff\1\14\2\uffff\10\14",
            "\1\14\4\uffff\3\14\2\uffff\1\14\12\uffff\1\15\5\uffff\1\14"+
            "\13\uffff\1\14\14\uffff\1\15\3\14\1\uffff\5\14",
            "",
            ""
    };

    static final short[] DFA84_eot = DFA.unpackEncodedString(DFA84_eotS);
    static final short[] DFA84_eof = DFA.unpackEncodedString(DFA84_eofS);
    static final char[] DFA84_min = DFA.unpackEncodedStringToUnsignedChars(DFA84_minS);
    static final char[] DFA84_max = DFA.unpackEncodedStringToUnsignedChars(DFA84_maxS);
    static final short[] DFA84_accept = DFA.unpackEncodedString(DFA84_acceptS);
    static final short[] DFA84_special = DFA.unpackEncodedString(DFA84_specialS);
    static final short[][] DFA84_transition;

    static {
        int numStates = DFA84_transitionS.length;
        DFA84_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA84_transition[i] = DFA.unpackEncodedString(DFA84_transitionS[i]);
        }
    }

    class DFA84 extends DFA {

        public DFA84(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 84;
            this.eot = DFA84_eot;
            this.eof = DFA84_eof;
            this.min = DFA84_min;
            this.max = DFA84_max;
            this.accept = DFA84_accept;
            this.special = DFA84_special;
            this.transition = DFA84_transition;
        }
        public String getDescription() {
            return "792:1: common : ( parExpression | x= 'return' -> ^( RETURN_VAR $x) | x= 'throw' -> ^( THROW_VAR $x) | x= 'this' -> ^( THIS_VAR $x) | x= 'super' -> ^( SUPER_VAR $x) | x= '_' -> ^( LAMBDA_VAR $x) | x= '@old' '(' expression ')' -> ^( OLD expression ) | x= '@arg' '(' integerLiteral ')' -> ^( ARGUMENT integerLiteral ) | relationalUnaryExpression | '{' decls '|' expression '}' -> ^( QUANTIFY OP_SET_COMPREHENSION decls expression ) | '{' rangeExpression ( ',' rangeExpression )* '}' -> ^( OP_SET_COMPREHENSION_ENUM ( rangeExpression )+ ) );";
        }
    }
    static final String DFA85_eotS =
        "\24\uffff";
    static final String DFA85_eofS =
        "\4\uffff\1\3\4\uffff\2\3\1\uffff\1\3\2\uffff\2\3\2\uffff\1\3";
    static final String DFA85_minS =
        "\1\172\3\uffff\1\u008d\3\172\1\uffff\2\u008d\1\uffff\3\172\1\u008d"+
        "\3\172\1\u008d";
    static final String DFA85_maxS =
        "\1\u00d2\3\uffff\1\u00ca\1\u00d2\1\u00d7\1\u00c4\1\uffff\2\u00ca"+
        "\1\uffff\1\u00d7\1\u00d9\1\u00cb\1\u00ca\1\u00d7\1\u00d9\1\u00cb"+
        "\1\u00ca";
    static final String DFA85_acceptS =
        "\1\uffff\1\1\1\2\1\3\4\uffff\1\5\2\uffff\1\4\10\uffff";
    static final String DFA85_specialS =
        "\24\uffff}>";
    static final String[] DFA85_transitionS = {
            "\1\4\6\2\20\uffff\1\1\5\uffff\1\1\21\uffff\3\2\10\3\12\uffff"+
            "\1\1\11\uffff\1\1\2\uffff\10\1",
            "",
            "",
            "",
            "\1\5\4\uffff\1\3\1\6\2\3\1\uffff\1\10\1\3\11\uffff\3\3\3\uffff"+
            "\1\3\13\uffff\21\3\1\uffff\2\3\1\7\2\3",
            "\1\11\26\uffff\1\3\5\uffff\1\3\20\uffff\1\3\3\uffff\10\3\12"+
            "\uffff\1\3\11\uffff\1\3\2\uffff\10\3",
            "\1\12\6\3\20\uffff\1\3\1\uffff\1\3\3\uffff\1\3\21\uffff\13"+
            "\3\3\uffff\2\3\5\uffff\1\3\4\uffff\3\3\2\uffff\1\3\2\uffff\15"+
            "\3",
            "\1\13\104\uffff\1\3\3\uffff\2\3",
            "",
            "\1\5\4\uffff\1\3\1\6\2\3\1\uffff\2\3\11\uffff\3\3\3\uffff\1"+
            "\3\13\uffff\21\3\1\uffff\2\3\1\7\2\3",
            "\1\3\4\uffff\2\3\1\15\1\14\1\uffff\2\3\11\uffff\3\3\3\uffff"+
            "\1\3\13\uffff\21\3\1\uffff\5\3",
            "",
            "\7\3\14\uffff\1\3\3\uffff\5\3\1\uffff\2\3\11\uffff\3\3\3\uffff"+
            "\40\3\1\16\17\3",
            "\1\17\6\3\20\uffff\1\3\5\uffff\1\3\21\uffff\13\3\4\uffff\1"+
            "\3\5\uffff\1\3\4\uffff\3\3\2\uffff\1\3\2\uffff\17\3",
            "\1\13\103\uffff\2\3\3\uffff\2\3\6\uffff\1\3",
            "\1\3\5\uffff\1\3\1\21\1\20\1\uffff\2\3\13\uffff\1\3\3\uffff"+
            "\1\3\13\uffff\1\3\1\uffff\17\3\1\uffff\5\3",
            "\7\3\14\uffff\1\3\3\uffff\5\3\1\uffff\2\3\11\uffff\3\3\3\uffff"+
            "\40\3\1\22\17\3",
            "\1\23\6\3\20\uffff\1\3\5\uffff\1\3\21\uffff\13\3\4\uffff\1"+
            "\3\5\uffff\1\3\4\uffff\3\3\2\uffff\1\3\2\uffff\17\3",
            "\1\13\103\uffff\2\3\3\uffff\2\3\6\uffff\1\3",
            "\1\3\5\uffff\1\3\1\21\1\20\1\uffff\2\3\13\uffff\1\3\3\uffff"+
            "\1\3\13\uffff\1\3\1\uffff\17\3\1\uffff\5\3"
    };

    static final short[] DFA85_eot = DFA.unpackEncodedString(DFA85_eotS);
    static final short[] DFA85_eof = DFA.unpackEncodedString(DFA85_eofS);
    static final char[] DFA85_min = DFA.unpackEncodedStringToUnsignedChars(DFA85_minS);
    static final char[] DFA85_max = DFA.unpackEncodedStringToUnsignedChars(DFA85_maxS);
    static final short[] DFA85_accept = DFA.unpackEncodedString(DFA85_acceptS);
    static final short[] DFA85_special = DFA.unpackEncodedString(DFA85_specialS);
    static final short[][] DFA85_transition;

    static {
        int numStates = DFA85_transitionS.length;
        DFA85_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA85_transition[i] = DFA.unpackEncodedString(DFA85_transitionS[i]);
        }
    }

    class DFA85 extends DFA {

        public DFA85(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 85;
            this.eot = DFA85_eot;
            this.eof = DFA85_eof;
            this.min = DFA85_min;
            this.max = DFA85_max;
            this.accept = DFA85_accept;
            this.special = DFA85_special;
            this.transition = DFA85_transition;
        }
        public String getDescription() {
            return "806:1: primary : ( common | literal | primaryTypeIdentifier | typeName '@' Identifier -> ^( FIELD typeName ^( IDENTIFIER Identifier ) ) | Identifier arguments -> ^( CALL Identifier arguments ) );";
        }
    }
    static final String DFA88_eotS =
        "\20\uffff";
    static final String DFA88_eofS =
        "\2\uffff\1\5\4\uffff\2\5\2\uffff\3\5\1\uffff\1\5";
    static final String DFA88_minS =
        "\1\172\1\uffff\1\u008d\2\172\1\uffff\1\172\2\u008d\1\0\2\172\1\u008d"+
        "\2\172\1\u008d";
    static final String DFA88_maxS =
        "\1\u00b3\1\uffff\1\u00ca\1\u00d2\1\u00d7\1\uffff\1\u00d9\2\u00ca"+
        "\1\0\1\u00d9\1\u00d7\1\u00ca\1\u00d7\1\u00d9\1\u00ca";
    static final String DFA88_acceptS =
        "\1\uffff\1\1\3\uffff\1\2\12\uffff";
    static final String DFA88_specialS =
        "\11\uffff\1\0\6\uffff}>";
    static final String[] DFA88_transitionS = {
            "\1\2\61\uffff\10\1",
            "",
            "\1\3\4\uffff\1\5\1\4\2\5\2\uffff\1\5\11\uffff\3\5\3\uffff\1"+
            "\5\13\uffff\1\6\20\5\1\uffff\5\5",
            "\1\7\26\uffff\1\5\5\uffff\1\5\20\uffff\1\5\3\uffff\10\5\12"+
            "\uffff\1\5\11\uffff\1\5\2\uffff\10\5",
            "\1\10\6\5\20\uffff\1\5\1\uffff\1\5\3\uffff\1\5\21\uffff\13"+
            "\5\3\uffff\2\5\5\uffff\1\5\4\uffff\3\5\2\uffff\1\5\2\uffff\15"+
            "\5",
            "",
            "\7\5\20\uffff\1\5\5\uffff\1\5\21\uffff\13\5\1\uffff\1\11\2"+
            "\uffff\1\5\5\uffff\1\5\4\uffff\3\5\2\uffff\1\5\2\uffff\17\5",
            "\1\3\4\uffff\1\5\1\4\2\5\1\uffff\2\5\11\uffff\3\5\3\uffff\1"+
            "\5\13\uffff\1\6\20\5\1\uffff\5\5",
            "\1\5\4\uffff\2\5\1\12\1\13\1\uffff\2\5\11\uffff\3\5\3\uffff"+
            "\1\5\13\uffff\21\5\1\uffff\5\5",
            "\1\uffff",
            "\1\14\6\5\20\uffff\1\5\5\uffff\1\5\21\uffff\13\5\4\uffff\1"+
            "\5\5\uffff\1\5\4\uffff\3\5\2\uffff\1\5\2\uffff\17\5",
            "\7\5\14\uffff\1\5\3\uffff\5\5\1\uffff\2\5\11\uffff\3\5\3\uffff"+
            "\14\5\1\6\43\5",
            "\1\5\5\uffff\1\5\1\16\1\15\1\uffff\2\5\13\uffff\1\5\3\uffff"+
            "\1\5\13\uffff\1\5\1\uffff\17\5\1\uffff\5\5",
            "\7\5\14\uffff\1\5\3\uffff\5\5\1\uffff\2\5\11\uffff\3\5\3\uffff"+
            "\14\5\1\6\43\5",
            "\1\17\6\5\20\uffff\1\5\5\uffff\1\5\21\uffff\13\5\4\uffff\1"+
            "\5\5\uffff\1\5\4\uffff\3\5\2\uffff\1\5\2\uffff\17\5",
            "\1\5\5\uffff\1\5\1\16\1\15\1\uffff\2\5\13\uffff\1\5\3\uffff"+
            "\1\5\13\uffff\1\5\1\uffff\17\5\1\uffff\5\5"
    };

    static final short[] DFA88_eot = DFA.unpackEncodedString(DFA88_eotS);
    static final short[] DFA88_eof = DFA.unpackEncodedString(DFA88_eofS);
    static final char[] DFA88_min = DFA.unpackEncodedStringToUnsignedChars(DFA88_minS);
    static final char[] DFA88_max = DFA.unpackEncodedStringToUnsignedChars(DFA88_maxS);
    static final short[] DFA88_accept = DFA.unpackEncodedString(DFA88_acceptS);
    static final short[] DFA88_special = DFA.unpackEncodedString(DFA88_specialS);
    static final short[][] DFA88_transition;

    static {
        int numStates = DFA88_transitionS.length;
        DFA88_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA88_transition[i] = DFA.unpackEncodedString(DFA88_transitionS[i]);
        }
    }

    class DFA88 extends DFA {

        public DFA88(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 88;
            this.eot = DFA88_eot;
            this.eof = DFA88_eof;
            this.min = DFA88_min;
            this.max = DFA88_max;
            this.accept = DFA88_accept;
            this.special = DFA88_special;
            this.transition = DFA88_transition;
        }
        public String getDescription() {
            return "814:1: primaryTypeIdentifier : ( typeDisambiguous | Identifier ( '.' Identifier )* ( typeParameters2 )? -> ^( AMBIGUOUS ( ^( IDENTIFIER Identifier ) )+ ( ^( TYPE_PARAMETERS typeParameters2 ) )? ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA88_9 = input.LA(1);

                         
                        int index88_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred132_JFSL()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index88_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 88, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA87_eotS =
        "\43\uffff";
    static final String DFA87_eofS =
        "\1\2\42\uffff";
    static final String DFA87_minS =
        "\1\u008d\1\0\41\uffff";
    static final String DFA87_maxS =
        "\1\u00ca\1\0\41\uffff";
    static final String DFA87_acceptS =
        "\2\uffff\1\2\37\uffff\1\1";
    static final String DFA87_specialS =
        "\1\uffff\1\0\41\uffff}>";
    static final String[] DFA87_transitionS = {
            "\1\2\4\uffff\1\2\1\1\2\2\2\uffff\1\2\11\uffff\3\2\3\uffff\1"+
            "\2\13\uffff\21\2\1\uffff\5\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA87_eot = DFA.unpackEncodedString(DFA87_eotS);
    static final short[] DFA87_eof = DFA.unpackEncodedString(DFA87_eofS);
    static final char[] DFA87_min = DFA.unpackEncodedStringToUnsignedChars(DFA87_minS);
    static final char[] DFA87_max = DFA.unpackEncodedStringToUnsignedChars(DFA87_maxS);
    static final short[] DFA87_accept = DFA.unpackEncodedString(DFA87_acceptS);
    static final short[] DFA87_special = DFA.unpackEncodedString(DFA87_specialS);
    static final short[][] DFA87_transition;

    static {
        int numStates = DFA87_transitionS.length;
        DFA87_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA87_transition[i] = DFA.unpackEncodedString(DFA87_transitionS[i]);
        }
    }

    class DFA87 extends DFA {

        public DFA87(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 87;
            this.eot = DFA87_eot;
            this.eof = DFA87_eof;
            this.min = DFA87_min;
            this.max = DFA87_max;
            this.accept = DFA87_accept;
            this.special = DFA87_special;
            this.transition = DFA87_transition;
        }
        public String getDescription() {
            return "816:34: ( typeParameters2 )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA87_1 = input.LA(1);

                         
                        int index87_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred134_JFSL()) ) {s = 34;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index87_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 87, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA90_eotS =
        "\16\uffff";
    static final String DFA90_eofS =
        "\4\uffff\1\13\11\uffff";
    static final String DFA90_minS =
        "\1\u008d\2\172\1\uffff\1\u008d\1\172\10\uffff";
    static final String DFA90_maxS =
        "\1\u00b4\1\u00d2\1\u00d9\1\uffff\1\u00ca\1\u00d2\10\uffff";
    static final String DFA90_acceptS =
        "\3\uffff\1\11\2\uffff\1\3\1\4\1\10\1\7\1\1\1\2\1\5\1\6";
    static final String DFA90_specialS =
        "\16\uffff}>";
    static final String[] DFA90_transitionS = {
            "\1\1\5\uffff\1\3\40\uffff\1\2",
            "\1\4\26\uffff\1\7\5\uffff\1\7\20\uffff\1\5\3\uffff\10\6\12"+
            "\uffff\1\7\11\uffff\1\7\2\uffff\10\7",
            "\7\11\20\uffff\1\11\5\uffff\1\11\21\uffff\13\11\1\uffff\1\10"+
            "\2\uffff\1\11\5\uffff\1\11\4\uffff\3\11\2\uffff\1\11\2\uffff"+
            "\17\11",
            "",
            "\1\13\4\uffff\4\13\1\uffff\1\12\1\13\11\uffff\3\13\3\uffff"+
            "\1\13\13\uffff\21\13\1\uffff\5\13",
            "\1\14\26\uffff\1\15\5\uffff\1\15\46\uffff\1\15\11\uffff\1\15"+
            "\2\uffff\10\15",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA90_eot = DFA.unpackEncodedString(DFA90_eotS);
    static final short[] DFA90_eof = DFA.unpackEncodedString(DFA90_eofS);
    static final char[] DFA90_min = DFA.unpackEncodedStringToUnsignedChars(DFA90_minS);
    static final char[] DFA90_max = DFA.unpackEncodedStringToUnsignedChars(DFA90_maxS);
    static final short[] DFA90_accept = DFA.unpackEncodedString(DFA90_acceptS);
    static final short[] DFA90_special = DFA.unpackEncodedString(DFA90_specialS);
    static final short[][] DFA90_transition;

    static {
        int numStates = DFA90_transitionS.length;
        DFA90_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA90_transition[i] = DFA.unpackEncodedString(DFA90_transitionS[i]);
        }
    }

    class DFA90 extends DFA {

        public DFA90(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 90;
            this.eot = DFA90_eot;
            this.eof = DFA90_eof;
            this.min = DFA90_min;
            this.max = DFA90_max;
            this.accept = DFA90_accept;
            this.special = DFA90_special;
            this.transition = DFA90_transition;
        }
        public String getDescription() {
            return "820:1: selector : ( '.' Identifier arguments -> ^( CALL Identifier arguments ) | '.' Identifier -> ^( JOIN ^( IDENTIFIER Identifier ) ) | '.' primitiveType -> ^( JOIN primitiveType ) | '.' common -> ^( JOIN common ) | '.' '*' Identifier -> ^( JOIN_REFLEXIVE ^( IDENTIFIER Identifier ) ) | '.' '*' common -> ^( JOIN_REFLEXIVE common ) | '[' expression ']' -> ^( BRACKET expression ) | '[' ']' -> ^( BRACKET ) | '<' DecimalLiteral ( ',' DecimalLiteral )* '>' -> ^( PROJECTION ( DecimalLiteral )+ ) );";
        }
    }
 

    public static final BitSet FOLLOW_expression_in_clause793 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_clause795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_declaration_in_specField802 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_specField804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_frame_in_modifies811 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_modifies813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_139_in_compilationUnit833 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_packageName_in_compilationUnit835 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_140_in_compilationUnit837 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x000000000001C000L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit843 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x000000000001C000L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit849 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_140_in_compilationUnit851 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_EOF_in_compilationUnit858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_packageName883 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_141_in_packageName886 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_packageName888 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_142_in_importDeclaration916 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_packageName_in_importDeclaration918 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_140_in_importDeclaration920 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_143_in_typeDeclaration947 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_144_in_typeDeclaration951 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_typeDeclaration954 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000000000A0000L});
    public static final BitSet FOLLOW_typeParameters_in_typeDeclaration956 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_145_in_typeDeclaration963 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x000000000245D000L});
    public static final BitSet FOLLOW_typeBodyDeclaration_in_typeDeclaration965 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x000000000245D000L});
    public static final BitSet FOLLOW_146_in_typeDeclaration968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_147_in_typeParameters999 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_typeParameters1001 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000300000L});
    public static final BitSet FOLLOW_148_in_typeParameters1004 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_typeParameters1006 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000300000L});
    public static final BitSet FOLLOW_149_in_typeParameters1010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_147_in_typeParameters21036 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x000FF00000000000L});
    public static final BitSet FOLLOW_primaryTypeIdentifier_in_typeParameters21038 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000300000L});
    public static final BitSet FOLLOW_148_in_typeParameters21041 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x000FF00000000000L});
    public static final BitSet FOLLOW_primaryTypeIdentifier_in_typeParameters21043 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000300000L});
    public static final BitSet FOLLOW_149_in_typeParameters21047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_140_in_typeBodyDeclaration1065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDeclaration_in_typeBodyDeclaration1072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_150_in_typeBodyDeclaration1079 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_151_in_typeBodyDeclaration1081 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_typeBodyDeclaration1083 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_typeBodyDeclaration1085 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_153_in_typeBodyDeclaration1099 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_151_in_typeBodyDeclaration1101 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x0000000300000000L});
    public static final BitSet FOLLOW_declaration_in_typeBodyDeclaration1103 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_typeBodyDeclaration1105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeBodyDeclaration1119 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_151_in_typeBodyDeclaration1121 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x000FF00001000000L});
    public static final BitSet FOLLOW_methodParameters_in_typeBodyDeclaration1123 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_typeBodyDeclaration1125 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_145_in_typeBodyDeclaration1127 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000000FC041000L});
    public static final BitSet FOLLOW_specCase_in_typeBodyDeclaration1129 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000041000L});
    public static final BitSet FOLLOW_140_in_typeBodyDeclaration1132 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000000FC041000L});
    public static final BitSet FOLLOW_specCase_in_typeBodyDeclaration1134 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000041000L});
    public static final BitSet FOLLOW_140_in_typeBodyDeclaration1139 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000041000L});
    public static final BitSet FOLLOW_146_in_typeBodyDeclaration1143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_methodParameters1181 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_methodParameters1183 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_148_in_methodParameters1186 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x000FF00000000000L});
    public static final BitSet FOLLOW_type_in_methodParameters1188 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_methodParameters1190 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_154_in_specCase1226 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_151_in_specCase1228 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_specCase1234 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_specCase1236 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000038000000L});
    public static final BitSet FOLLOW_155_in_specCase1247 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_151_in_specCase1249 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_specCase1255 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_specCase1257 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_156_in_specCase1262 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_151_in_specCase1264 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_specCase1270 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_specCase1272 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_157_in_specCase1283 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_151_in_specCase1285 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x4000000001820000L,0x000000000007F900L});
    public static final BitSet FOLLOW_frame_in_specCase1291 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_specCase1293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_158_in_specCase1344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_159_in_specCase1359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_160_in_declaration1383 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_161_in_declaration1387 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_declaration1391 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_162_in_declaration1393 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFEC000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_declarationMult_in_declaration1395 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_additiveExpression_in_declaration1397 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_163_in_declaration1399 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x4000001000820000L,0x000000000007F900L});
    public static final BitSet FOLLOW_frame_in_declaration1401 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_164_in_declaration1404 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_declaration1406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_160_in_declaration1438 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_161_in_declaration1442 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_declaration1446 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_162_in_declaration1448 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFEC000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_declarationMult_in_declaration1450 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_additiveExpression_in_declaration1452 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_164_in_declaration1455 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_declaration1457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_165_in_quantIdMod1499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setDeclOp_in_declarationMult1531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_166_in_declarationMult1539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_167_in_declarationMult1559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_storeRef_in_frame1595 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_148_in_frame1598 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x4000000000920000L,0x000000000007F900L});
    public static final BitSet FOLLOW_storeRef_in_frame1600 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_storePrimary_in_storeRef1625 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0010000000082000L});
    public static final BitSet FOLLOW_storeSelectors_in_storeRef1627 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000001800000000L});
    public static final BitSet FOLLOW_164_in_storeRef1630 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_storeRef1634 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_163_in_storeRef1639 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_storeRef1643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_storePrimary1696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_common_in_storePrimary1711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_storeSelectors1726 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0010000000082000L});
    public static final BitSet FOLLOW_storeWildCard_in_storeSelectors1729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_storeSelectors1736 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0010000000082000L});
    public static final BitSet FOLLOW_141_in_storeWildCard1757 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_168_in_storeWildCard1759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_169_in_keywordLiteral1780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_170_in_keywordLiteral1792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_171_in_keywordLiteral1803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_integerLiteral_in_literal1827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_literal1833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal1839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal1845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_keywordLiteral_in_literal1851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_172_in_primitiveType1907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_173_in_primitiveType1923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_174_in_primitiveType1942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_175_in_primitiveType1961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_176_in_primitiveType1979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_177_in_primitiveType1999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_178_in_primitiveType2018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_179_in_primitiveType2036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeName2058 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000082000L});
    public static final BitSet FOLLOW_141_in_typeName2061 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_typeName2063 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000082000L});
    public static final BitSet FOLLOW_typeParameters_in_typeName2067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_typeDisambiguous2104 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_180_in_typeDisambiguous2113 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_181_in_typeDisambiguous2115 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_typeName_in_typeDisambiguous2134 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_180_in_typeDisambiguous2143 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_181_in_typeDisambiguous2145 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0010000000000000L});
    public static final BitSet FOLLOW_typeDisambiguous_in_type2170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeName_in_type2176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_151_in_parExpression2212 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_parExpression2215 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_parExpression2217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList2246 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_148_in_expressionList2249 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_expressionList2252 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_151_in_arguments2273 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0001820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expressionList_in_arguments2275 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_arguments2278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression2302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_quantifiedExpression_in_conditionalExpression2319 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0040000000000000L});
    public static final BitSet FOLLOW_182_in_conditionalExpression2332 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression2334 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_162_in_conditionalExpression2336 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression2338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setQuantOp_in_quantifiedExpression2374 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_decls_in_quantifiedExpression2376 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_164_in_quantifiedExpression2379 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_quantifiedExpression2381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setQuantOp_in_quantifiedExpression2399 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_mdecls_in_quantifiedExpression2401 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_164_in_quantifiedExpression2403 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_quantifiedExpression2405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logicalExpression_in_quantifiedExpression2423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalXorExpression_in_logicalExpression2439 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0680000000080000L});
    public static final BitSet FOLLOW_logicalOp_in_logicalExpression2452 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_conditionalXorExpression_in_logicalExpression2454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_147_in_logicalOp2491 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_183_in_logicalOp2493 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_149_in_logicalOp2495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_147_in_logicalOp2508 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_184_in_logicalOp2510 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_149_in_logicalOp2512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_183_in_logicalOp2525 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_149_in_logicalOp2527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_185_in_logicalOp2543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_186_in_logicalOp2557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalXorExpression2583 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_187_in_conditionalXorExpression2598 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalXorExpression2602 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0800000000000000L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression2643 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x1000000000000000L});
    public static final BitSet FOLLOW_188_in_conditionalOrExpression2658 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression2662 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x1000000000000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression2699 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_189_in_conditionalAndExpression2714 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression2718 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x2000000000000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression2760 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_164_in_inclusiveOrExpression2775 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression2779 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression2816 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_190_in_exclusiveOrExpression2831 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression2835 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression2874 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x8000000000000000L});
    public static final BitSet FOLLOW_bitAndOp_in_andExpression2889 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression2893 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x8000000000000000L});
    public static final BitSet FOLLOW_191_in_bitAndOp2932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression2959 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0180000000000000L});
    public static final BitSet FOLLOW_equalityOp_in_equalityExpression2976 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression2980 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0180000000000000L});
    public static final BitSet FOLLOW_eqOp_in_equalityOp3020 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_neqOp_in_equalityOp3026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_183_in_eqOp3045 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_183_in_eqOp3047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_183_in_eqOp3065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_184_in_neqOp3093 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_183_in_neqOp3095 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression3124 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_192_in_instanceOfExpression3139 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x000FF00000000000L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression3143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setUnaryExpression_in_relationalExpression3182 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0100000000280000L,0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression3199 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_setUnaryExpression_in_relationalExpression3203 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0100000000280000L,0x0000000000000002L});
    public static final BitSet FOLLOW_193_in_inOp3244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_147_in_relationalOp3270 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_183_in_relationalOp3272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_149_in_relationalOp3291 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0080000000000000L});
    public static final BitSet FOLLOW_183_in_relationalOp3293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_147_in_relationalOp3312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_149_in_relationalOp3335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inOp_in_relationalOp3356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_184_in_relationalOp3365 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_193_in_relationalOp3367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setUnaryOp_in_setUnaryExpression3391 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_joinExpression_in_setUnaryExpression3393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_setUnaryExpression3409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rangeExpression_in_shiftExpression3427 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000280000L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression3444 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_rangeExpression_in_shiftExpression3448 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000280000L});
    public static final BitSet FOLLOW_147_in_shiftOp3487 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_147_in_shiftOp3489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_149_in_shiftOp3511 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_149_in_shiftOp3513 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_149_in_shiftOp3515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_149_in_shiftOp3533 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000200000L});
    public static final BitSet FOLLOW_149_in_shiftOp3535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_rangeExpression3564 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_194_in_rangeExpression3576 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_additiveExpression_in_rangeExpression3580 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_sizeExpression_in_additiveExpression3618 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000018L});
    public static final BitSet FOLLOW_additiveOp_in_additiveExpression3636 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_sizeExpression_in_additiveExpression3640 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000018L});
    public static final BitSet FOLLOW_195_in_additiveOp3683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_196_in_additiveOp3703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_197_in_sizeExpression3727 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_joinExpression_in_sizeExpression3729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_sizeExpression3745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setAdditiveExpression_in_multiplicativeExpression3763 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000010000000000L,0x00000000000000C0L});
    public static final BitSet FOLLOW_multOp_in_multiplicativeExpression3780 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_setAdditiveExpression_in_multiplicativeExpression3784 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000010000000000L,0x00000000000000C0L});
    public static final BitSet FOLLOW_168_in_multOp3816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_198_in_multOp3836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_199_in_multOp3856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_overrideExpression_in_setAdditiveExpression3884 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_setAdditiveOp_in_setAdditiveExpression3904 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_overrideExpression_in_setAdditiveExpression3908 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_200_in_setAdditiveOp3952 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_195_in_setAdditiveOp3954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_200_in_setAdditiveOp3972 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_196_in_setAdditiveOp3974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_intersectionExpression_in_overrideExpression4002 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_201_in_overrideExpression4017 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_intersectionExpression_in_overrideExpression4021 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_composeExpression_in_intersectionExpression4065 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_200_in_intersectionExpression4080 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x8000000000000000L});
    public static final BitSet FOLLOW_191_in_intersectionExpression4082 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_composeExpression_in_intersectionExpression4086 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_unaryExpression_in_composeExpression4126 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_202_in_composeExpression4141 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_unaryExpression_in_composeExpression4145 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_195_in_unaryExpression4188 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression4190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_196_in_unaryExpression4206 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression4208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression4224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_203_in_unaryExpressionNotPlusMinus4237 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus4239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_184_in_unaryExpressionNotPlusMinus4255 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus4257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus4277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_joinExpression_in_unaryExpressionNotPlusMinus4283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_151_in_castExpression4303 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x000FF00000000000L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression4305 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_castExpression4307 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression4309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_decls4335 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000400000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_inOp_in_decls4338 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFEC000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_162_in_decls4342 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFEC000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_declarationMult_in_decls4345 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_rangeExpression_in_decls4347 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_148_in_decls4351 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_decls4354 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000400000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_inOp_in_decls4357 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFEC000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_162_in_decls4361 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFEC000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_declarationMult_in_decls4364 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_rangeExpression_in_decls4366 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_quantIdMod_in_mdecls4409 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_mdecls4411 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000400100000L,0x0000000000000002L});
    public static final BitSet FOLLOW_148_in_mdecls4414 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_mdecls4416 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000400100000L,0x0000000000000002L});
    public static final BitSet FOLLOW_inOp_in_mdecls4421 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFEC000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_162_in_mdecls4425 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFEC000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_declarationMult_in_mdecls4428 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_rangeExpression_in_mdecls4430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_specUnaryOp_in_relationalUnaryExpression4469 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_parExpression_in_relationalUnaryExpression4471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_specUnaryOp_in_relationalUnaryExpression4487 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_relationalUnaryExpression4489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_joinExpression4522 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0010000000082000L});
    public static final BitSet FOLLOW_selector_in_joinExpression4524 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0010000000082000L});
    public static final BitSet FOLLOW_parExpression_in_common4553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_204_in_common4561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_205_in_common4578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_206_in_common4596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_207_in_common4615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_208_in_common4633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_209_in_common4655 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_151_in_common4657 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_common4659 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_common4661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_210_in_common4680 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_151_in_common4682 = new BitSet(new long[]{0x0000000000000000L,0xC000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_integerLiteral_in_common4684 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_152_in_common4686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalUnaryExpression_in_common4700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_145_in_common4707 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_decls_in_common4709 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_164_in_common4711 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_common4713 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_146_in_common4715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_145_in_common4733 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_rangeExpression_in_common4735 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000140000L});
    public static final BitSet FOLLOW_148_in_common4738 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_rangeExpression_in_common4740 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000140000L});
    public static final BitSet FOLLOW_146_in_common4744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_common_in_primary4768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary4774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primaryTypeIdentifier_in_primary4780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeName_in_primary4786 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_200_in_primary4788 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_primary4790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary4810 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_arguments_in_primary4812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDisambiguous_in_primaryTypeIdentifier4837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primaryTypeIdentifier4843 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000082000L});
    public static final BitSet FOLLOW_141_in_primaryTypeIdentifier4846 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_primaryTypeIdentifier4848 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000000000082000L});
    public static final BitSet FOLLOW_typeParameters2_in_primaryTypeIdentifier4852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_141_in_selector4889 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_selector4891 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_arguments_in_selector4893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_141_in_selector4909 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_selector4911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_141_in_selector4929 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x000FF00000000000L});
    public static final BitSet FOLLOW_primitiveType_in_selector4931 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_141_in_selector4945 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x4000000000820000L,0x000000000007F900L});
    public static final BitSet FOLLOW_common_in_selector4947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_141_in_selector4961 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_168_in_selector4963 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_selector4965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_141_in_selector4983 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_168_in_selector4985 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x4000000000820000L,0x000000000007F900L});
    public static final BitSet FOLLOW_common_in_selector4987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_180_in_selector5001 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_selector5003 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_181_in_selector5005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_180_in_selector5021 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_181_in_selector5023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_147_in_selector5036 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_DecimalLiteral_in_selector5038 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000300000L});
    public static final BitSet FOLLOW_148_in_selector5041 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_DecimalLiteral_in_selector5043 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000300000L});
    public static final BitSet FOLLOW_149_in_selector5047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_200_in_specUnaryOp5076 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x4000000000000000L});
    public static final BitSet FOLLOW_190_in_specUnaryOp5078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_190_in_specUnaryOp5095 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_200_in_specUnaryOp5116 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_203_in_specUnaryOp5118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_203_in_specUnaryOp5135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_211_in_setDeclOp5170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_212_in_setDeclOp5189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_213_in_setDeclOp5208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setDeclOp_in_setUnaryOp5236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_214_in_setUnaryOp5244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_215_in_setUnaryOp5262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setUnaryOp_in_setQuantOp5289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_197_in_setQuantOp5297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_216_in_setQuantOp5315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_217_in_setQuantOp5335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_140_in_synpred13_JFSL1132 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x00000000FC000000L});
    public static final BitSet FOLLOW_specCase_in_synpred13_JFSL1134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred26_JFSL1382 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_synpred26_JFSL1391 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_162_in_synpred26_JFSL1393 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFEC000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_declarationMult_in_synpred26_JFSL1395 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_additiveExpression_in_synpred26_JFSL1397 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_163_in_synpred26_JFSL1399 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x4000001000820000L,0x000000000007F900L});
    public static final BitSet FOLLOW_frame_in_synpred26_JFSL1401 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_164_in_synpred26_JFSL1404 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_synpred26_JFSL1406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_164_in_synpred36_JFSL1630 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_synpred36_JFSL1634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_synpred40_JFSL1726 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0010000000082000L});
    public static final BitSet FOLLOW_storeWildCard_in_synpred40_JFSL1729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_180_in_synpred59_JFSL2113 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_181_in_synpred59_JFSL2115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_180_in_synpred61_JFSL2143 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0020000000000000L});
    public static final BitSet FOLLOW_181_in_synpred61_JFSL2145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_182_in_synpred65_JFSL2332 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_synpred65_JFSL2334 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000000400000000L});
    public static final BitSet FOLLOW_162_in_synpred65_JFSL2336 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_synpred65_JFSL2338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setQuantOp_in_synpred66_JFSL2374 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_decls_in_synpred66_JFSL2376 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_164_in_synpred66_JFSL2379 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_synpred66_JFSL2381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setQuantOp_in_synpred67_JFSL2399 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L,0x0000002000000000L});
    public static final BitSet FOLLOW_mdecls_in_synpred67_JFSL2401 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000000L,0x0000001000000000L});
    public static final BitSet FOLLOW_164_in_synpred67_JFSL2403 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_expression_in_synpred67_JFSL2405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_164_in_synpred76_JFSL2775 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_synpred76_JFSL2779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_203_in_synpred107_JFSL4237 = new BitSet(new long[]{0x0000000000000000L,0xFC00000000000000L,0x410FFE0000820001L,0x0000000003FFF938L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred107_JFSL4239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred109_JFSL4277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_synpred116_JFSL4524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDisambiguous_in_synpred132_JFSL4837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_141_in_synpred133_JFSL4846 = new BitSet(new long[]{0x0000000000000000L,0x0400000000000000L});
    public static final BitSet FOLLOW_Identifier_in_synpred133_JFSL4848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters2_in_synpred134_JFSL4852 = new BitSet(new long[]{0x0000000000000002L});

}