// $ANTLR 3.2 Sep 23, 2009 12:02:23 JFSL.g 2012-09-25 11:21:24
 
package edu.mit.csail.sdg.squander.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all"})
public class JFSLLexer extends Lexer {
    public static final int PACKAGE=108;
    public static final int T__159=159;
    public static final int OP_SET_ALL=85;
    public static final int T__158=158;
    public static final int OP_INSTANCEOF=64;
    public static final int TYPE_INT=99;
    public static final int OP_GEQ=59;
    public static final int OP_SET_LONE=82;
    public static final int OP_SET_NO=81;
    public static final int T__160=160;
    public static final int OP_INTERSECTION=52;
    public static final int T__167=167;
    public static final int LIT_FALSE=93;
    public static final int T__168=168;
    public static final int EOF=-1;
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
    public static final int FILE=105;
    public static final int T__142=142;
    public static final int LAMBDA_VAR=41;
    public static final int T__140=140;
    public static final int T__145=145;
    public static final int T__146=146;
    public static final int T__143=143;
    public static final int T__144=144;
    public static final int OP_SHL=61;
    public static final int METHOD_PARAMETERS=113;
    public static final int TYPE_REF=103;
    public static final int WS=138;
    public static final int OP_SHR=62;
    public static final int SPECFIELD=107;
    public static final int TYPE_CHAR=96;
    public static final int QUANTIFY_ENUM=17;
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
    public static final int DECLARATION=4;
    public static final int HexDigit=129;
    public static final int T__202=202;
    public static final int T__203=203;
    public static final int ENSURES=117;
    public static final int T__204=204;
    public static final int OP_USHR=63;
    public static final int T__205=205;
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
    public static final int OP_SET_NUM=83;
    public static final int T__174=174;
    public static final int T__173=173;
    public static final int TYPE_ARRAY=104;
    public static final int T__172=172;
    public static final int T__179=179;
    public static final int OP_SET_ONE=79;
    public static final int T__178=178;
    public static final int REQUIRES=116;
    public static final int T__177=177;
    public static final int T__176=176;
    public static final int FRAME_FIELD=26;
    public static final int OP_NEQ=45;
    public static final int UnicodeEscape=134;
    public static final int T__171=171;
    public static final int T__170=170;
    public static final int OP_TIMES=73;
    public static final int OP_LEQ=60;
    public static final int FRAME=5;
    public static final int FRAME_ALL=13;
    public static final int BINARY=14;
    public static final int OP_UNION=66;
    public static final int T__169=169;
    public static final int METHOD=112;
    public static final int RETURN_VAR=39;
    public static final int OP_IMPLIES=78;

    // delegates
    // delegators

    public JFSLLexer() {;} 
    public JFSLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public JFSLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "JFSL.g"; }

    // $ANTLR start "T__139"
    public final void mT__139() throws RecognitionException {
        try {
            int _type = T__139;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:7:8: ( 'package' )
            // JFSL.g:7:10: 'package'
            {
            match("package"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__139"

    // $ANTLR start "T__140"
    public final void mT__140() throws RecognitionException {
        try {
            int _type = T__140;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:8:8: ( ';' )
            // JFSL.g:8:10: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__140"

    // $ANTLR start "T__141"
    public final void mT__141() throws RecognitionException {
        try {
            int _type = T__141;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:9:8: ( '.' )
            // JFSL.g:9:10: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__141"

    // $ANTLR start "T__142"
    public final void mT__142() throws RecognitionException {
        try {
            int _type = T__142;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:10:8: ( 'import' )
            // JFSL.g:10:10: 'import'
            {
            match("import"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__142"

    // $ANTLR start "T__143"
    public final void mT__143() throws RecognitionException {
        try {
            int _type = T__143;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:11:8: ( 'class' )
            // JFSL.g:11:10: 'class'
            {
            match("class"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__143"

    // $ANTLR start "T__144"
    public final void mT__144() throws RecognitionException {
        try {
            int _type = T__144;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:12:8: ( 'interface' )
            // JFSL.g:12:10: 'interface'
            {
            match("interface"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__144"

    // $ANTLR start "T__145"
    public final void mT__145() throws RecognitionException {
        try {
            int _type = T__145;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:13:8: ( '{' )
            // JFSL.g:13:10: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__145"

    // $ANTLR start "T__146"
    public final void mT__146() throws RecognitionException {
        try {
            int _type = T__146;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:14:8: ( '}' )
            // JFSL.g:14:10: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__146"

    // $ANTLR start "T__147"
    public final void mT__147() throws RecognitionException {
        try {
            int _type = T__147;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:15:8: ( '<' )
            // JFSL.g:15:10: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__147"

    // $ANTLR start "T__148"
    public final void mT__148() throws RecognitionException {
        try {
            int _type = T__148;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:16:8: ( ',' )
            // JFSL.g:16:10: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__148"

    // $ANTLR start "T__149"
    public final void mT__149() throws RecognitionException {
        try {
            int _type = T__149;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:17:8: ( '>' )
            // JFSL.g:17:10: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__149"

    // $ANTLR start "T__150"
    public final void mT__150() throws RecognitionException {
        try {
            int _type = T__150;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:18:8: ( '@Invariant' )
            // JFSL.g:18:10: '@Invariant'
            {
            match("@Invariant"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__150"

    // $ANTLR start "T__151"
    public final void mT__151() throws RecognitionException {
        try {
            int _type = T__151;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:19:8: ( '(' )
            // JFSL.g:19:10: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__151"

    // $ANTLR start "T__152"
    public final void mT__152() throws RecognitionException {
        try {
            int _type = T__152;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:20:8: ( ')' )
            // JFSL.g:20:10: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__152"

    // $ANTLR start "T__153"
    public final void mT__153() throws RecognitionException {
        try {
            int _type = T__153;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:21:8: ( '@SpecField' )
            // JFSL.g:21:10: '@SpecField'
            {
            match("@SpecField"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__153"

    // $ANTLR start "T__154"
    public final void mT__154() throws RecognitionException {
        try {
            int _type = T__154;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:22:8: ( '@Requires' )
            // JFSL.g:22:10: '@Requires'
            {
            match("@Requires"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__154"

    // $ANTLR start "T__155"
    public final void mT__155() throws RecognitionException {
        try {
            int _type = T__155;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:23:8: ( '@Ensures' )
            // JFSL.g:23:10: '@Ensures'
            {
            match("@Ensures"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__155"

    // $ANTLR start "T__156"
    public final void mT__156() throws RecognitionException {
        try {
            int _type = T__156;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:24:8: ( '@Throws' )
            // JFSL.g:24:10: '@Throws'
            {
            match("@Throws"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__156"

    // $ANTLR start "T__157"
    public final void mT__157() throws RecognitionException {
        try {
            int _type = T__157;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:25:8: ( '@Modifies' )
            // JFSL.g:25:10: '@Modifies'
            {
            match("@Modifies"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__157"

    // $ANTLR start "T__158"
    public final void mT__158() throws RecognitionException {
        try {
            int _type = T__158;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:26:8: ( '@Helper' )
            // JFSL.g:26:10: '@Helper'
            {
            match("@Helper"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__158"

    // $ANTLR start "T__159"
    public final void mT__159() throws RecognitionException {
        try {
            int _type = T__159;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:27:8: ( '@Pure' )
            // JFSL.g:27:10: '@Pure'
            {
            match("@Pure"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__159"

    // $ANTLR start "T__160"
    public final void mT__160() throws RecognitionException {
        try {
            int _type = T__160;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:28:8: ( 'public' )
            // JFSL.g:28:10: 'public'
            {
            match("public"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__160"

    // $ANTLR start "T__161"
    public final void mT__161() throws RecognitionException {
        try {
            int _type = T__161;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:29:8: ( 'private' )
            // JFSL.g:29:10: 'private'
            {
            match("private"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__161"

    // $ANTLR start "T__162"
    public final void mT__162() throws RecognitionException {
        try {
            int _type = T__162;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:30:8: ( ':' )
            // JFSL.g:30:10: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__162"

    // $ANTLR start "T__163"
    public final void mT__163() throws RecognitionException {
        try {
            int _type = T__163;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:31:8: ( 'from' )
            // JFSL.g:31:10: 'from'
            {
            match("from"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__163"

    // $ANTLR start "T__164"
    public final void mT__164() throws RecognitionException {
        try {
            int _type = T__164;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:32:8: ( '|' )
            // JFSL.g:32:10: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__164"

    // $ANTLR start "T__165"
    public final void mT__165() throws RecognitionException {
        try {
            int _type = T__165;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:33:8: ( 'disj' )
            // JFSL.g:33:10: 'disj'
            {
            match("disj"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__165"

    // $ANTLR start "T__166"
    public final void mT__166() throws RecognitionException {
        try {
            int _type = T__166;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:34:8: ( 'set' )
            // JFSL.g:34:10: 'set'
            {
            match("set"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__166"

    // $ANTLR start "T__167"
    public final void mT__167() throws RecognitionException {
        try {
            int _type = T__167;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:35:8: ( 'seq' )
            // JFSL.g:35:10: 'seq'
            {
            match("seq"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__167"

    // $ANTLR start "T__168"
    public final void mT__168() throws RecognitionException {
        try {
            int _type = T__168;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:36:8: ( '*' )
            // JFSL.g:36:10: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__168"

    // $ANTLR start "T__169"
    public final void mT__169() throws RecognitionException {
        try {
            int _type = T__169;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:37:8: ( 'true' )
            // JFSL.g:37:10: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__169"

    // $ANTLR start "T__170"
    public final void mT__170() throws RecognitionException {
        try {
            int _type = T__170;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:38:8: ( 'false' )
            // JFSL.g:38:10: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__170"

    // $ANTLR start "T__171"
    public final void mT__171() throws RecognitionException {
        try {
            int _type = T__171;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:39:8: ( 'null' )
            // JFSL.g:39:10: 'null'
            {
            match("null"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__171"

    // $ANTLR start "T__172"
    public final void mT__172() throws RecognitionException {
        try {
            int _type = T__172;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:40:8: ( 'boolean' )
            // JFSL.g:40:10: 'boolean'
            {
            match("boolean"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__172"

    // $ANTLR start "T__173"
    public final void mT__173() throws RecognitionException {
        try {
            int _type = T__173;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:41:8: ( 'char' )
            // JFSL.g:41:10: 'char'
            {
            match("char"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__173"

    // $ANTLR start "T__174"
    public final void mT__174() throws RecognitionException {
        try {
            int _type = T__174;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:42:8: ( 'byte' )
            // JFSL.g:42:10: 'byte'
            {
            match("byte"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__174"

    // $ANTLR start "T__175"
    public final void mT__175() throws RecognitionException {
        try {
            int _type = T__175;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:43:8: ( 'short' )
            // JFSL.g:43:10: 'short'
            {
            match("short"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__175"

    // $ANTLR start "T__176"
    public final void mT__176() throws RecognitionException {
        try {
            int _type = T__176;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:44:8: ( 'int' )
            // JFSL.g:44:10: 'int'
            {
            match("int"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__176"

    // $ANTLR start "T__177"
    public final void mT__177() throws RecognitionException {
        try {
            int _type = T__177;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:45:8: ( 'long' )
            // JFSL.g:45:10: 'long'
            {
            match("long"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__177"

    // $ANTLR start "T__178"
    public final void mT__178() throws RecognitionException {
        try {
            int _type = T__178;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:46:8: ( 'float' )
            // JFSL.g:46:10: 'float'
            {
            match("float"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__178"

    // $ANTLR start "T__179"
    public final void mT__179() throws RecognitionException {
        try {
            int _type = T__179;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:47:8: ( 'double' )
            // JFSL.g:47:10: 'double'
            {
            match("double"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__179"

    // $ANTLR start "T__180"
    public final void mT__180() throws RecognitionException {
        try {
            int _type = T__180;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:48:8: ( '[' )
            // JFSL.g:48:10: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__180"

    // $ANTLR start "T__181"
    public final void mT__181() throws RecognitionException {
        try {
            int _type = T__181;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:49:8: ( ']' )
            // JFSL.g:49:10: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__181"

    // $ANTLR start "T__182"
    public final void mT__182() throws RecognitionException {
        try {
            int _type = T__182;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:50:8: ( '?' )
            // JFSL.g:50:10: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__182"

    // $ANTLR start "T__183"
    public final void mT__183() throws RecognitionException {
        try {
            int _type = T__183;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:51:8: ( '=' )
            // JFSL.g:51:10: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__183"

    // $ANTLR start "T__184"
    public final void mT__184() throws RecognitionException {
        try {
            int _type = T__184;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:52:8: ( '!' )
            // JFSL.g:52:10: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__184"

    // $ANTLR start "T__185"
    public final void mT__185() throws RecognitionException {
        try {
            int _type = T__185;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:53:8: ( 'implies' )
            // JFSL.g:53:10: 'implies'
            {
            match("implies"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__185"

    // $ANTLR start "T__186"
    public final void mT__186() throws RecognitionException {
        try {
            int _type = T__186;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:54:8: ( 'iff' )
            // JFSL.g:54:10: 'iff'
            {
            match("iff"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__186"

    // $ANTLR start "T__187"
    public final void mT__187() throws RecognitionException {
        try {
            int _type = T__187;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:55:8: ( '^^' )
            // JFSL.g:55:10: '^^'
            {
            match("^^"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__187"

    // $ANTLR start "T__188"
    public final void mT__188() throws RecognitionException {
        try {
            int _type = T__188;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:56:8: ( '||' )
            // JFSL.g:56:10: '||'
            {
            match("||"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__188"

    // $ANTLR start "T__189"
    public final void mT__189() throws RecognitionException {
        try {
            int _type = T__189;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:57:8: ( '&&' )
            // JFSL.g:57:10: '&&'
            {
            match("&&"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__189"

    // $ANTLR start "T__190"
    public final void mT__190() throws RecognitionException {
        try {
            int _type = T__190;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:58:8: ( '^' )
            // JFSL.g:58:10: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__190"

    // $ANTLR start "T__191"
    public final void mT__191() throws RecognitionException {
        try {
            int _type = T__191;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:59:8: ( '&' )
            // JFSL.g:59:10: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__191"

    // $ANTLR start "T__192"
    public final void mT__192() throws RecognitionException {
        try {
            int _type = T__192;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:60:8: ( 'instanceof' )
            // JFSL.g:60:10: 'instanceof'
            {
            match("instanceof"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__192"

    // $ANTLR start "T__193"
    public final void mT__193() throws RecognitionException {
        try {
            int _type = T__193;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:61:8: ( 'in' )
            // JFSL.g:61:10: 'in'
            {
            match("in"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__193"

    // $ANTLR start "T__194"
    public final void mT__194() throws RecognitionException {
        try {
            int _type = T__194;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:62:8: ( '...' )
            // JFSL.g:62:10: '...'
            {
            match("..."); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__194"

    // $ANTLR start "T__195"
    public final void mT__195() throws RecognitionException {
        try {
            int _type = T__195;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:63:8: ( '+' )
            // JFSL.g:63:10: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__195"

    // $ANTLR start "T__196"
    public final void mT__196() throws RecognitionException {
        try {
            int _type = T__196;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:64:8: ( '-' )
            // JFSL.g:64:10: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__196"

    // $ANTLR start "T__197"
    public final void mT__197() throws RecognitionException {
        try {
            int _type = T__197;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:65:8: ( '#' )
            // JFSL.g:65:10: '#'
            {
            match('#'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__197"

    // $ANTLR start "T__198"
    public final void mT__198() throws RecognitionException {
        try {
            int _type = T__198;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:66:8: ( '/' )
            // JFSL.g:66:10: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__198"

    // $ANTLR start "T__199"
    public final void mT__199() throws RecognitionException {
        try {
            int _type = T__199;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:67:8: ( '%' )
            // JFSL.g:67:10: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__199"

    // $ANTLR start "T__200"
    public final void mT__200() throws RecognitionException {
        try {
            int _type = T__200;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:68:8: ( '@' )
            // JFSL.g:68:10: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__200"

    // $ANTLR start "T__201"
    public final void mT__201() throws RecognitionException {
        try {
            int _type = T__201;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:69:8: ( '++' )
            // JFSL.g:69:10: '++'
            {
            match("++"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__201"

    // $ANTLR start "T__202"
    public final void mT__202() throws RecognitionException {
        try {
            int _type = T__202;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:70:8: ( '->' )
            // JFSL.g:70:10: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__202"

    // $ANTLR start "T__203"
    public final void mT__203() throws RecognitionException {
        try {
            int _type = T__203;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:71:8: ( '~' )
            // JFSL.g:71:10: '~'
            {
            match('~'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__203"

    // $ANTLR start "T__204"
    public final void mT__204() throws RecognitionException {
        try {
            int _type = T__204;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:72:8: ( 'return' )
            // JFSL.g:72:10: 'return'
            {
            match("return"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__204"

    // $ANTLR start "T__205"
    public final void mT__205() throws RecognitionException {
        try {
            int _type = T__205;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:73:8: ( 'throw' )
            // JFSL.g:73:10: 'throw'
            {
            match("throw"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__205"

    // $ANTLR start "T__206"
    public final void mT__206() throws RecognitionException {
        try {
            int _type = T__206;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:74:8: ( 'this' )
            // JFSL.g:74:10: 'this'
            {
            match("this"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__206"

    // $ANTLR start "T__207"
    public final void mT__207() throws RecognitionException {
        try {
            int _type = T__207;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:75:8: ( 'super' )
            // JFSL.g:75:10: 'super'
            {
            match("super"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__207"

    // $ANTLR start "T__208"
    public final void mT__208() throws RecognitionException {
        try {
            int _type = T__208;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:76:8: ( '_' )
            // JFSL.g:76:10: '_'
            {
            match('_'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__208"

    // $ANTLR start "T__209"
    public final void mT__209() throws RecognitionException {
        try {
            int _type = T__209;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:77:8: ( '@old' )
            // JFSL.g:77:10: '@old'
            {
            match("@old"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__209"

    // $ANTLR start "T__210"
    public final void mT__210() throws RecognitionException {
        try {
            int _type = T__210;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:78:8: ( '@arg' )
            // JFSL.g:78:10: '@arg'
            {
            match("@arg"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__210"

    // $ANTLR start "T__211"
    public final void mT__211() throws RecognitionException {
        try {
            int _type = T__211;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:79:8: ( 'one' )
            // JFSL.g:79:10: 'one'
            {
            match("one"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__211"

    // $ANTLR start "T__212"
    public final void mT__212() throws RecognitionException {
        try {
            int _type = T__212;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:80:8: ( 'some' )
            // JFSL.g:80:10: 'some'
            {
            match("some"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__212"

    // $ANTLR start "T__213"
    public final void mT__213() throws RecognitionException {
        try {
            int _type = T__213;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:81:8: ( 'lone' )
            // JFSL.g:81:10: 'lone'
            {
            match("lone"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__213"

    // $ANTLR start "T__214"
    public final void mT__214() throws RecognitionException {
        try {
            int _type = T__214;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:82:8: ( 'no' )
            // JFSL.g:82:10: 'no'
            {
            match("no"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__214"

    // $ANTLR start "T__215"
    public final void mT__215() throws RecognitionException {
        try {
            int _type = T__215;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:83:8: ( 'sum' )
            // JFSL.g:83:10: 'sum'
            {
            match("sum"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__215"

    // $ANTLR start "T__216"
    public final void mT__216() throws RecognitionException {
        try {
            int _type = T__216;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:84:8: ( 'all' )
            // JFSL.g:84:10: 'all'
            {
            match("all"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__216"

    // $ANTLR start "T__217"
    public final void mT__217() throws RecognitionException {
        try {
            int _type = T__217;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:85:8: ( 'exists' )
            // JFSL.g:85:10: 'exists'
            {
            match("exists"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__217"

    // $ANTLR start "HexLiteral"
    public final void mHexLiteral() throws RecognitionException {
        try {
            int _type = HexLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:862:12: ( '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )? )
            // JFSL.g:862:14: '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )?
            {
            match('0'); 
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // JFSL.g:862:28: ( HexDigit )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='F')||(LA1_0>='a' && LA1_0<='f')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // JFSL.g:862:28: HexDigit
            	    {
            	    mHexDigit(); 

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

            // JFSL.g:862:38: ( IntegerTypeSuffix )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='L'||LA2_0=='l') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // JFSL.g:862:38: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "HexLiteral"

    // $ANTLR start "DecimalLiteral"
    public final void mDecimalLiteral() throws RecognitionException {
        try {
            int _type = DecimalLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:864:16: ( ( '0' | '1' .. '9' ( '0' .. '9' )* ) ( IntegerTypeSuffix )? )
            // JFSL.g:864:18: ( '0' | '1' .. '9' ( '0' .. '9' )* ) ( IntegerTypeSuffix )?
            {
            // JFSL.g:864:18: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='0') ) {
                alt4=1;
            }
            else if ( ((LA4_0>='1' && LA4_0<='9')) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // JFSL.g:864:19: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // JFSL.g:864:25: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // JFSL.g:864:34: ( '0' .. '9' )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // JFSL.g:864:34: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    }
                    break;

            }

            // JFSL.g:864:45: ( IntegerTypeSuffix )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='L'||LA5_0=='l') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // JFSL.g:864:45: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DecimalLiteral"

    // $ANTLR start "OctalLiteral"
    public final void mOctalLiteral() throws RecognitionException {
        try {
            int _type = OctalLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:866:14: ( '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )? )
            // JFSL.g:866:16: '0' ( '0' .. '7' )+ ( IntegerTypeSuffix )?
            {
            match('0'); 
            // JFSL.g:866:20: ( '0' .. '7' )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0>='0' && LA6_0<='7')) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // JFSL.g:866:21: '0' .. '7'
            	    {
            	    matchRange('0','7'); 

            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
            } while (true);

            // JFSL.g:866:32: ( IntegerTypeSuffix )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='L'||LA7_0=='l') ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // JFSL.g:866:32: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OctalLiteral"

    // $ANTLR start "HexDigit"
    public final void mHexDigit() throws RecognitionException {
        try {
            // JFSL.g:869:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // JFSL.g:869:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HexDigit"

    // $ANTLR start "IntegerTypeSuffix"
    public final void mIntegerTypeSuffix() throws RecognitionException {
        try {
            // JFSL.g:872:19: ( ( 'l' | 'L' ) )
            // JFSL.g:872:21: ( 'l' | 'L' )
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "IntegerTypeSuffix"

    // $ANTLR start "FloatingPointLiteral"
    public final void mFloatingPointLiteral() throws RecognitionException {
        try {
            int _type = FloatingPointLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:875:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )? | ( '0' .. '9' )+ FloatTypeSuffix )
            int alt18=4;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // JFSL.g:875:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )?
                    {
                    // JFSL.g:875:9: ( '0' .. '9' )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // JFSL.g:875:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);

                    match('.'); 
                    // JFSL.g:875:25: ( '0' .. '9' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // JFSL.g:875:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    // JFSL.g:875:37: ( Exponent )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='E'||LA10_0=='e') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // JFSL.g:875:37: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }

                    // JFSL.g:875:47: ( FloatTypeSuffix )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0=='D'||LA11_0=='F'||LA11_0=='d'||LA11_0=='f') ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // JFSL.g:875:47: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // JFSL.g:876:9: '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )?
                    {
                    match('.'); 
                    // JFSL.g:876:13: ( '0' .. '9' )+
                    int cnt12=0;
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // JFSL.g:876:14: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt12 >= 1 ) break loop12;
                                EarlyExitException eee =
                                    new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
                    } while (true);

                    // JFSL.g:876:25: ( Exponent )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0=='E'||LA13_0=='e') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // JFSL.g:876:25: Exponent
                            {
                            mExponent(); 

                            }
                            break;

                    }

                    // JFSL.g:876:35: ( FloatTypeSuffix )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0=='D'||LA14_0=='F'||LA14_0=='d'||LA14_0=='f') ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // JFSL.g:876:35: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // JFSL.g:877:9: ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )?
                    {
                    // JFSL.g:877:9: ( '0' .. '9' )+
                    int cnt15=0;
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( ((LA15_0>='0' && LA15_0<='9')) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // JFSL.g:877:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt15 >= 1 ) break loop15;
                                EarlyExitException eee =
                                    new EarlyExitException(15, input);
                                throw eee;
                        }
                        cnt15++;
                    } while (true);

                    mExponent(); 
                    // JFSL.g:877:30: ( FloatTypeSuffix )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0=='D'||LA16_0=='F'||LA16_0=='d'||LA16_0=='f') ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // JFSL.g:877:30: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); 

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // JFSL.g:878:9: ( '0' .. '9' )+ FloatTypeSuffix
                    {
                    // JFSL.g:878:9: ( '0' .. '9' )+
                    int cnt17=0;
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( ((LA17_0>='0' && LA17_0<='9')) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // JFSL.g:878:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt17 >= 1 ) break loop17;
                                EarlyExitException eee =
                                    new EarlyExitException(17, input);
                                throw eee;
                        }
                        cnt17++;
                    } while (true);

                    mFloatTypeSuffix(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FloatingPointLiteral"

    // $ANTLR start "Exponent"
    public final void mExponent() throws RecognitionException {
        try {
            // JFSL.g:882:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // JFSL.g:882:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // JFSL.g:882:22: ( '+' | '-' )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0=='+'||LA19_0=='-') ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // JFSL.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // JFSL.g:882:33: ( '0' .. '9' )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( ((LA20_0>='0' && LA20_0<='9')) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // JFSL.g:882:34: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "Exponent"

    // $ANTLR start "FloatTypeSuffix"
    public final void mFloatTypeSuffix() throws RecognitionException {
        try {
            // JFSL.g:885:17: ( ( 'f' | 'F' | 'd' | 'D' ) )
            // JFSL.g:885:19: ( 'f' | 'F' | 'd' | 'D' )
            {
            if ( input.LA(1)=='D'||input.LA(1)=='F'||input.LA(1)=='d'||input.LA(1)=='f' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "FloatTypeSuffix"

    // $ANTLR start "CharacterLiteral"
    public final void mCharacterLiteral() throws RecognitionException {
        try {
            int _type = CharacterLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:888:5: ( '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) ) '\\'' )
            // JFSL.g:888:9: '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) ) '\\''
            {
            match('\''); 
            // JFSL.g:888:14: ( EscapeSequence | ~ ( '\\'' | '\\\\' ) )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0=='\\') ) {
                alt21=1;
            }
            else if ( ((LA21_0>='\u0000' && LA21_0<='&')||(LA21_0>='(' && LA21_0<='[')||(LA21_0>=']' && LA21_0<='\uFFFF')) ) {
                alt21=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // JFSL.g:888:16: EscapeSequence
                    {
                    mEscapeSequence(); 

                    }
                    break;
                case 2 :
                    // JFSL.g:888:33: ~ ( '\\'' | '\\\\' )
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CharacterLiteral"

    // $ANTLR start "StringLiteral"
    public final void mStringLiteral() throws RecognitionException {
        try {
            int _type = StringLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:892:5: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' | '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) )* '\\'' )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0=='\"') ) {
                alt24=1;
            }
            else if ( (LA24_0=='\'') ) {
                alt24=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // JFSL.g:892:8: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); 
                    // JFSL.g:892:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
                    loop22:
                    do {
                        int alt22=3;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0=='\\') ) {
                            alt22=1;
                        }
                        else if ( ((LA22_0>='\u0000' && LA22_0<='!')||(LA22_0>='#' && LA22_0<='[')||(LA22_0>=']' && LA22_0<='\uFFFF')) ) {
                            alt22=2;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // JFSL.g:892:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // JFSL.g:892:32: ~ ( '\\\\' | '\"' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop22;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 2 :
                    // JFSL.g:893:8: '\\'' ( EscapeSequence | ~ ( '\\'' | '\\\\' ) )* '\\''
                    {
                    match('\''); 
                    // JFSL.g:893:13: ( EscapeSequence | ~ ( '\\'' | '\\\\' ) )*
                    loop23:
                    do {
                        int alt23=3;
                        int LA23_0 = input.LA(1);

                        if ( (LA23_0=='\\') ) {
                            alt23=1;
                        }
                        else if ( ((LA23_0>='\u0000' && LA23_0<='&')||(LA23_0>='(' && LA23_0<='[')||(LA23_0>=']' && LA23_0<='\uFFFF')) ) {
                            alt23=2;
                        }


                        switch (alt23) {
                    	case 1 :
                    	    // JFSL.g:893:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // JFSL.g:893:32: ~ ( '\\'' | '\\\\' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop23;
                        }
                    } while (true);

                    match('\''); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "StringLiteral"

    // $ANTLR start "EscapeSequence"
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // JFSL.g:898:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
            int alt25=3;
            int LA25_0 = input.LA(1);

            if ( (LA25_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt25=1;
                    }
                    break;
                case 'u':
                    {
                    alt25=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt25=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 25, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // JFSL.g:898:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;
                case 2 :
                    // JFSL.g:899:9: UnicodeEscape
                    {
                    mUnicodeEscape(); 

                    }
                    break;
                case 3 :
                    // JFSL.g:900:9: OctalEscape
                    {
                    mOctalEscape(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "EscapeSequence"

    // $ANTLR start "OctalEscape"
    public final void mOctalEscape() throws RecognitionException {
        try {
            // JFSL.g:905:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt26=3;
            int LA26_0 = input.LA(1);

            if ( (LA26_0=='\\') ) {
                int LA26_1 = input.LA(2);

                if ( ((LA26_1>='0' && LA26_1<='3')) ) {
                    int LA26_2 = input.LA(3);

                    if ( ((LA26_2>='0' && LA26_2<='7')) ) {
                        int LA26_4 = input.LA(4);

                        if ( ((LA26_4>='0' && LA26_4<='7')) ) {
                            alt26=1;
                        }
                        else {
                            alt26=2;}
                    }
                    else {
                        alt26=3;}
                }
                else if ( ((LA26_1>='4' && LA26_1<='7')) ) {
                    int LA26_3 = input.LA(3);

                    if ( ((LA26_3>='0' && LA26_3<='7')) ) {
                        alt26=2;
                    }
                    else {
                        alt26=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // JFSL.g:905:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // JFSL.g:905:14: ( '0' .. '3' )
                    // JFSL.g:905:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // JFSL.g:905:25: ( '0' .. '7' )
                    // JFSL.g:905:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // JFSL.g:905:36: ( '0' .. '7' )
                    // JFSL.g:905:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // JFSL.g:906:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // JFSL.g:906:14: ( '0' .. '7' )
                    // JFSL.g:906:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // JFSL.g:906:25: ( '0' .. '7' )
                    // JFSL.g:906:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // JFSL.g:907:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // JFSL.g:907:14: ( '0' .. '7' )
                    // JFSL.g:907:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "OctalEscape"

    // $ANTLR start "UnicodeEscape"
    public final void mUnicodeEscape() throws RecognitionException {
        try {
            // JFSL.g:912:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // JFSL.g:912:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
            {
            match('\\'); 
            match('u'); 
            mHexDigit(); 
            mHexDigit(); 
            mHexDigit(); 
            mHexDigit(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "UnicodeEscape"

    // $ANTLR start "Identifier"
    public final void mIdentifier() throws RecognitionException {
        try {
            int _type = Identifier;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:916:5: ( Letter ( Letter | JavaIDDigit )* )
            // JFSL.g:916:9: Letter ( Letter | JavaIDDigit )*
            {
            mLetter(); 
            // JFSL.g:916:16: ( Letter | JavaIDDigit )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0=='$'||(LA27_0>='0' && LA27_0<='9')||(LA27_0>='A' && LA27_0<='Z')||LA27_0=='_'||(LA27_0>='a' && LA27_0<='z')||(LA27_0>='\u00C0' && LA27_0<='\u00D6')||(LA27_0>='\u00D8' && LA27_0<='\u00F6')||(LA27_0>='\u00F8' && LA27_0<='\u1FFF')||(LA27_0>='\u3040' && LA27_0<='\u318F')||(LA27_0>='\u3300' && LA27_0<='\u337F')||(LA27_0>='\u3400' && LA27_0<='\u3D2D')||(LA27_0>='\u4E00' && LA27_0<='\u9FFF')||(LA27_0>='\uF900' && LA27_0<='\uFAFF')) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // JFSL.g:
            	    {
            	    if ( input.LA(1)=='$'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u3040' && input.LA(1)<='\u318F')||(input.LA(1)>='\u3300' && input.LA(1)<='\u337F')||(input.LA(1)>='\u3400' && input.LA(1)<='\u3D2D')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FFF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFAFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "Identifier"

    // $ANTLR start "Letter"
    public final void mLetter() throws RecognitionException {
        try {
            // JFSL.g:921:5: ( '\\u0024' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u00ff' | '\\u0100' .. '\\u1fff' | '\\u3040' .. '\\u318f' | '\\u3300' .. '\\u337f' | '\\u3400' .. '\\u3d2d' | '\\u4e00' .. '\\u9fff' | '\\uf900' .. '\\ufaff' )
            // JFSL.g:
            {
            if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u1FFF')||(input.LA(1)>='\u3040' && input.LA(1)<='\u318F')||(input.LA(1)>='\u3300' && input.LA(1)<='\u337F')||(input.LA(1)>='\u3400' && input.LA(1)<='\u3D2D')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FFF')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFAFF') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "Letter"

    // $ANTLR start "JavaIDDigit"
    public final void mJavaIDDigit() throws RecognitionException {
        try {
            // JFSL.g:938:5: ( '\\u0030' .. '\\u0039' | '\\u0660' .. '\\u0669' | '\\u06f0' .. '\\u06f9' | '\\u0966' .. '\\u096f' | '\\u09e6' .. '\\u09ef' | '\\u0a66' .. '\\u0a6f' | '\\u0ae6' .. '\\u0aef' | '\\u0b66' .. '\\u0b6f' | '\\u0be7' .. '\\u0bef' | '\\u0c66' .. '\\u0c6f' | '\\u0ce6' .. '\\u0cef' | '\\u0d66' .. '\\u0d6f' | '\\u0e50' .. '\\u0e59' | '\\u0ed0' .. '\\u0ed9' | '\\u1040' .. '\\u1049' )
            // JFSL.g:
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='\u0660' && input.LA(1)<='\u0669')||(input.LA(1)>='\u06F0' && input.LA(1)<='\u06F9')||(input.LA(1)>='\u0966' && input.LA(1)<='\u096F')||(input.LA(1)>='\u09E6' && input.LA(1)<='\u09EF')||(input.LA(1)>='\u0A66' && input.LA(1)<='\u0A6F')||(input.LA(1)>='\u0AE6' && input.LA(1)<='\u0AEF')||(input.LA(1)>='\u0B66' && input.LA(1)<='\u0B6F')||(input.LA(1)>='\u0BE7' && input.LA(1)<='\u0BEF')||(input.LA(1)>='\u0C66' && input.LA(1)<='\u0C6F')||(input.LA(1)>='\u0CE6' && input.LA(1)<='\u0CEF')||(input.LA(1)>='\u0D66' && input.LA(1)<='\u0D6F')||(input.LA(1)>='\u0E50' && input.LA(1)<='\u0E59')||(input.LA(1)>='\u0ED0' && input.LA(1)<='\u0ED9')||(input.LA(1)>='\u1040' && input.LA(1)<='\u1049') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "JavaIDDigit"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JFSL.g:955:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            // JFSL.g:955:8: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // JFSL.g:1:8: ( T__139 | T__140 | T__141 | T__142 | T__143 | T__144 | T__145 | T__146 | T__147 | T__148 | T__149 | T__150 | T__151 | T__152 | T__153 | T__154 | T__155 | T__156 | T__157 | T__158 | T__159 | T__160 | T__161 | T__162 | T__163 | T__164 | T__165 | T__166 | T__167 | T__168 | T__169 | T__170 | T__171 | T__172 | T__173 | T__174 | T__175 | T__176 | T__177 | T__178 | T__179 | T__180 | T__181 | T__182 | T__183 | T__184 | T__185 | T__186 | T__187 | T__188 | T__189 | T__190 | T__191 | T__192 | T__193 | T__194 | T__195 | T__196 | T__197 | T__198 | T__199 | T__200 | T__201 | T__202 | T__203 | T__204 | T__205 | T__206 | T__207 | T__208 | T__209 | T__210 | T__211 | T__212 | T__213 | T__214 | T__215 | T__216 | T__217 | HexLiteral | DecimalLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | Identifier | WS )
        int alt28=87;
        alt28 = dfa28.predict(input);
        switch (alt28) {
            case 1 :
                // JFSL.g:1:10: T__139
                {
                mT__139(); 

                }
                break;
            case 2 :
                // JFSL.g:1:17: T__140
                {
                mT__140(); 

                }
                break;
            case 3 :
                // JFSL.g:1:24: T__141
                {
                mT__141(); 

                }
                break;
            case 4 :
                // JFSL.g:1:31: T__142
                {
                mT__142(); 

                }
                break;
            case 5 :
                // JFSL.g:1:38: T__143
                {
                mT__143(); 

                }
                break;
            case 6 :
                // JFSL.g:1:45: T__144
                {
                mT__144(); 

                }
                break;
            case 7 :
                // JFSL.g:1:52: T__145
                {
                mT__145(); 

                }
                break;
            case 8 :
                // JFSL.g:1:59: T__146
                {
                mT__146(); 

                }
                break;
            case 9 :
                // JFSL.g:1:66: T__147
                {
                mT__147(); 

                }
                break;
            case 10 :
                // JFSL.g:1:73: T__148
                {
                mT__148(); 

                }
                break;
            case 11 :
                // JFSL.g:1:80: T__149
                {
                mT__149(); 

                }
                break;
            case 12 :
                // JFSL.g:1:87: T__150
                {
                mT__150(); 

                }
                break;
            case 13 :
                // JFSL.g:1:94: T__151
                {
                mT__151(); 

                }
                break;
            case 14 :
                // JFSL.g:1:101: T__152
                {
                mT__152(); 

                }
                break;
            case 15 :
                // JFSL.g:1:108: T__153
                {
                mT__153(); 

                }
                break;
            case 16 :
                // JFSL.g:1:115: T__154
                {
                mT__154(); 

                }
                break;
            case 17 :
                // JFSL.g:1:122: T__155
                {
                mT__155(); 

                }
                break;
            case 18 :
                // JFSL.g:1:129: T__156
                {
                mT__156(); 

                }
                break;
            case 19 :
                // JFSL.g:1:136: T__157
                {
                mT__157(); 

                }
                break;
            case 20 :
                // JFSL.g:1:143: T__158
                {
                mT__158(); 

                }
                break;
            case 21 :
                // JFSL.g:1:150: T__159
                {
                mT__159(); 

                }
                break;
            case 22 :
                // JFSL.g:1:157: T__160
                {
                mT__160(); 

                }
                break;
            case 23 :
                // JFSL.g:1:164: T__161
                {
                mT__161(); 

                }
                break;
            case 24 :
                // JFSL.g:1:171: T__162
                {
                mT__162(); 

                }
                break;
            case 25 :
                // JFSL.g:1:178: T__163
                {
                mT__163(); 

                }
                break;
            case 26 :
                // JFSL.g:1:185: T__164
                {
                mT__164(); 

                }
                break;
            case 27 :
                // JFSL.g:1:192: T__165
                {
                mT__165(); 

                }
                break;
            case 28 :
                // JFSL.g:1:199: T__166
                {
                mT__166(); 

                }
                break;
            case 29 :
                // JFSL.g:1:206: T__167
                {
                mT__167(); 

                }
                break;
            case 30 :
                // JFSL.g:1:213: T__168
                {
                mT__168(); 

                }
                break;
            case 31 :
                // JFSL.g:1:220: T__169
                {
                mT__169(); 

                }
                break;
            case 32 :
                // JFSL.g:1:227: T__170
                {
                mT__170(); 

                }
                break;
            case 33 :
                // JFSL.g:1:234: T__171
                {
                mT__171(); 

                }
                break;
            case 34 :
                // JFSL.g:1:241: T__172
                {
                mT__172(); 

                }
                break;
            case 35 :
                // JFSL.g:1:248: T__173
                {
                mT__173(); 

                }
                break;
            case 36 :
                // JFSL.g:1:255: T__174
                {
                mT__174(); 

                }
                break;
            case 37 :
                // JFSL.g:1:262: T__175
                {
                mT__175(); 

                }
                break;
            case 38 :
                // JFSL.g:1:269: T__176
                {
                mT__176(); 

                }
                break;
            case 39 :
                // JFSL.g:1:276: T__177
                {
                mT__177(); 

                }
                break;
            case 40 :
                // JFSL.g:1:283: T__178
                {
                mT__178(); 

                }
                break;
            case 41 :
                // JFSL.g:1:290: T__179
                {
                mT__179(); 

                }
                break;
            case 42 :
                // JFSL.g:1:297: T__180
                {
                mT__180(); 

                }
                break;
            case 43 :
                // JFSL.g:1:304: T__181
                {
                mT__181(); 

                }
                break;
            case 44 :
                // JFSL.g:1:311: T__182
                {
                mT__182(); 

                }
                break;
            case 45 :
                // JFSL.g:1:318: T__183
                {
                mT__183(); 

                }
                break;
            case 46 :
                // JFSL.g:1:325: T__184
                {
                mT__184(); 

                }
                break;
            case 47 :
                // JFSL.g:1:332: T__185
                {
                mT__185(); 

                }
                break;
            case 48 :
                // JFSL.g:1:339: T__186
                {
                mT__186(); 

                }
                break;
            case 49 :
                // JFSL.g:1:346: T__187
                {
                mT__187(); 

                }
                break;
            case 50 :
                // JFSL.g:1:353: T__188
                {
                mT__188(); 

                }
                break;
            case 51 :
                // JFSL.g:1:360: T__189
                {
                mT__189(); 

                }
                break;
            case 52 :
                // JFSL.g:1:367: T__190
                {
                mT__190(); 

                }
                break;
            case 53 :
                // JFSL.g:1:374: T__191
                {
                mT__191(); 

                }
                break;
            case 54 :
                // JFSL.g:1:381: T__192
                {
                mT__192(); 

                }
                break;
            case 55 :
                // JFSL.g:1:388: T__193
                {
                mT__193(); 

                }
                break;
            case 56 :
                // JFSL.g:1:395: T__194
                {
                mT__194(); 

                }
                break;
            case 57 :
                // JFSL.g:1:402: T__195
                {
                mT__195(); 

                }
                break;
            case 58 :
                // JFSL.g:1:409: T__196
                {
                mT__196(); 

                }
                break;
            case 59 :
                // JFSL.g:1:416: T__197
                {
                mT__197(); 

                }
                break;
            case 60 :
                // JFSL.g:1:423: T__198
                {
                mT__198(); 

                }
                break;
            case 61 :
                // JFSL.g:1:430: T__199
                {
                mT__199(); 

                }
                break;
            case 62 :
                // JFSL.g:1:437: T__200
                {
                mT__200(); 

                }
                break;
            case 63 :
                // JFSL.g:1:444: T__201
                {
                mT__201(); 

                }
                break;
            case 64 :
                // JFSL.g:1:451: T__202
                {
                mT__202(); 

                }
                break;
            case 65 :
                // JFSL.g:1:458: T__203
                {
                mT__203(); 

                }
                break;
            case 66 :
                // JFSL.g:1:465: T__204
                {
                mT__204(); 

                }
                break;
            case 67 :
                // JFSL.g:1:472: T__205
                {
                mT__205(); 

                }
                break;
            case 68 :
                // JFSL.g:1:479: T__206
                {
                mT__206(); 

                }
                break;
            case 69 :
                // JFSL.g:1:486: T__207
                {
                mT__207(); 

                }
                break;
            case 70 :
                // JFSL.g:1:493: T__208
                {
                mT__208(); 

                }
                break;
            case 71 :
                // JFSL.g:1:500: T__209
                {
                mT__209(); 

                }
                break;
            case 72 :
                // JFSL.g:1:507: T__210
                {
                mT__210(); 

                }
                break;
            case 73 :
                // JFSL.g:1:514: T__211
                {
                mT__211(); 

                }
                break;
            case 74 :
                // JFSL.g:1:521: T__212
                {
                mT__212(); 

                }
                break;
            case 75 :
                // JFSL.g:1:528: T__213
                {
                mT__213(); 

                }
                break;
            case 76 :
                // JFSL.g:1:535: T__214
                {
                mT__214(); 

                }
                break;
            case 77 :
                // JFSL.g:1:542: T__215
                {
                mT__215(); 

                }
                break;
            case 78 :
                // JFSL.g:1:549: T__216
                {
                mT__216(); 

                }
                break;
            case 79 :
                // JFSL.g:1:556: T__217
                {
                mT__217(); 

                }
                break;
            case 80 :
                // JFSL.g:1:563: HexLiteral
                {
                mHexLiteral(); 

                }
                break;
            case 81 :
                // JFSL.g:1:574: DecimalLiteral
                {
                mDecimalLiteral(); 

                }
                break;
            case 82 :
                // JFSL.g:1:589: OctalLiteral
                {
                mOctalLiteral(); 

                }
                break;
            case 83 :
                // JFSL.g:1:602: FloatingPointLiteral
                {
                mFloatingPointLiteral(); 

                }
                break;
            case 84 :
                // JFSL.g:1:623: CharacterLiteral
                {
                mCharacterLiteral(); 

                }
                break;
            case 85 :
                // JFSL.g:1:640: StringLiteral
                {
                mStringLiteral(); 

                }
                break;
            case 86 :
                // JFSL.g:1:654: Identifier
                {
                mIdentifier(); 

                }
                break;
            case 87 :
                // JFSL.g:1:665: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA18 dfa18 = new DFA18(this);
    protected DFA28 dfa28 = new DFA28(this);
    static final String DFA18_eotS =
        "\6\uffff";
    static final String DFA18_eofS =
        "\6\uffff";
    static final String DFA18_minS =
        "\2\56\4\uffff";
    static final String DFA18_maxS =
        "\1\71\1\146\4\uffff";
    static final String DFA18_acceptS =
        "\2\uffff\1\2\1\4\1\3\1\1";
    static final String DFA18_specialS =
        "\6\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\5\1\uffff\12\1\12\uffff\1\3\1\4\1\3\35\uffff\1\3\1\4\1\3",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "874:1: FloatingPointLiteral : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )? | ( '0' .. '9' )+ FloatTypeSuffix );";
        }
    }
    static final String DFA28_eotS =
        "\1\uffff\1\56\1\uffff\1\65\2\56\5\uffff\1\105\3\uffff\1\56\1\112"+
        "\2\56\1\uffff\4\56\5\uffff\1\131\1\133\1\135\1\137\4\uffff\1\56"+
        "\1\141\3\56\2\147\4\uffff\3\56\3\uffff\1\56\1\161\3\56\13\uffff"+
        "\3\56\2\uffff\11\56\1\u0084\3\56\10\uffff\1\56\1\uffff\3\56\1\uffff"+
        "\1\u008c\1\uffff\1\147\2\uffff\4\56\1\u0098\1\56\1\uffff\1\u009a"+
        "\7\56\1\u00a2\1\u00a3\2\56\1\u00a6\5\56\1\uffff\4\56\1\u00b1\1\u00b2"+
        "\1\56\6\uffff\6\56\1\uffff\1\56\1\uffff\1\56\1\u00c0\1\u00c1\2\56"+
        "\1\u00c4\1\56\2\uffff\2\56\1\uffff\1\u00c8\1\u00c9\1\56\1\u00cb"+
        "\1\u00cc\1\56\1\u00ce\1\u00cf\1\u00d0\1\56\2\uffff\1\56\4\uffff"+
        "\7\56\1\u00dc\2\uffff\1\u00dd\1\u00de\1\uffff\1\56\1\u00e0\1\u00e1"+
        "\2\uffff\1\u00e2\2\uffff\1\56\3\uffff\2\56\2\uffff\1\56\1\u00e8"+
        "\1\56\1\u00ea\3\56\3\uffff\1\u00ee\3\uffff\1\56\1\u00f0\1\u00f1"+
        "\1\uffff\1\u00f3\1\uffff\1\u00f4\1\uffff\1\u00f5\2\56\1\uffff\1"+
        "\u00f8\6\uffff\2\56\1\uffff\1\u00fb\1\56\1\uffff\1\u00fd\1\uffff";
    static final String DFA28_eofS =
        "\u00fe\uffff";
    static final String DFA28_minS =
        "\1\11\1\141\1\uffff\1\56\1\146\1\150\5\uffff\1\105\3\uffff\1\141"+
        "\1\174\1\151\1\145\1\uffff\1\150\3\157\5\uffff\1\136\1\46\1\53\1"+
        "\76\4\uffff\1\145\1\44\1\156\1\154\1\170\2\56\1\0\3\uffff\1\143"+
        "\1\142\1\151\3\uffff\1\160\1\44\1\146\2\141\13\uffff\1\157\1\154"+
        "\1\157\2\uffff\1\163\1\165\1\161\1\157\2\155\1\165\1\151\1\154\1"+
        "\44\1\157\1\164\1\156\10\uffff\1\164\1\uffff\1\145\1\154\1\151\1"+
        "\uffff\1\56\1\uffff\1\56\1\42\1\0\1\153\1\154\1\166\1\154\1\44\1"+
        "\164\1\uffff\1\44\1\163\1\162\1\155\1\163\1\141\1\152\1\142\2\44"+
        "\1\162\1\145\1\44\2\145\1\157\1\163\1\154\1\uffff\1\154\2\145\1"+
        "\165\2\44\1\163\1\uffff\1\0\1\60\2\0\1\uffff\1\141\1\151\1\141\1"+
        "\162\1\151\1\162\1\uffff\1\141\1\uffff\1\163\2\44\1\145\1\164\1"+
        "\44\1\154\2\uffff\1\164\1\162\1\uffff\2\44\1\167\2\44\1\145\3\44"+
        "\1\162\2\uffff\1\164\1\60\2\0\1\uffff\1\147\1\143\2\164\1\145\1"+
        "\146\1\156\1\44\2\uffff\2\44\1\uffff\1\145\2\44\2\uffff\1\44\2\uffff"+
        "\1\141\3\uffff\1\156\1\163\1\60\1\0\1\145\1\44\1\145\1\44\1\163"+
        "\1\141\1\143\3\uffff\1\44\3\uffff\1\156\2\44\1\60\1\44\1\uffff\1"+
        "\44\1\uffff\1\44\1\143\1\145\1\uffff\1\44\2\uffff\1\0\3\uffff\1"+
        "\145\1\157\1\uffff\1\44\1\146\1\uffff\1\44\1\uffff";
    static final String DFA28_maxS =
        "\1\ufaff\1\165\1\uffff\1\71\1\156\1\154\5\uffff\1\157\3\uffff\1"+
        "\162\1\174\1\157\1\165\1\uffff\1\162\1\165\1\171\1\157\5\uffff\1"+
        "\136\1\46\1\53\1\76\4\uffff\1\145\1\ufaff\1\156\1\154\2\170\1\146"+
        "\1\uffff\3\uffff\1\143\1\142\1\151\3\uffff\1\160\1\ufaff\1\146\2"+
        "\141\13\uffff\1\157\1\154\1\157\2\uffff\1\163\1\165\1\164\1\157"+
        "\1\160\1\155\1\165\1\162\1\154\1\ufaff\1\157\1\164\1\156\10\uffff"+
        "\1\164\1\uffff\1\145\1\154\1\151\1\uffff\1\146\1\uffff\1\146\1\165"+
        "\1\uffff\1\153\1\154\1\166\1\157\1\ufaff\1\164\1\uffff\1\ufaff\1"+
        "\163\1\162\1\155\1\163\1\141\1\152\1\142\2\ufaff\1\162\1\145\1\ufaff"+
        "\2\145\1\157\1\163\1\154\1\uffff\1\154\1\145\1\147\1\165\2\ufaff"+
        "\1\163\1\uffff\1\uffff\1\146\2\uffff\1\uffff\1\141\1\151\1\141\1"+
        "\162\1\151\1\162\1\uffff\1\141\1\uffff\1\163\2\ufaff\1\145\1\164"+
        "\1\ufaff\1\154\2\uffff\1\164\1\162\1\uffff\2\ufaff\1\167\2\ufaff"+
        "\1\145\3\ufaff\1\162\2\uffff\1\164\1\146\2\uffff\1\uffff\1\147\1"+
        "\143\2\164\1\145\1\146\1\156\1\ufaff\2\uffff\2\ufaff\1\uffff\1\145"+
        "\2\ufaff\2\uffff\1\ufaff\2\uffff\1\141\3\uffff\1\156\1\163\1\146"+
        "\1\uffff\1\145\1\ufaff\1\145\1\ufaff\1\163\1\141\1\143\3\uffff\1"+
        "\ufaff\3\uffff\1\156\2\ufaff\1\146\1\ufaff\1\uffff\1\ufaff\1\uffff"+
        "\1\ufaff\1\143\1\145\1\uffff\1\ufaff\2\uffff\1\uffff\3\uffff\1\145"+
        "\1\157\1\uffff\1\ufaff\1\146\1\uffff\1\ufaff\1\uffff";
    static final String DFA28_acceptS =
        "\2\uffff\1\2\3\uffff\1\7\1\10\1\11\1\12\1\13\1\uffff\1\15\1\16\1"+
        "\30\4\uffff\1\36\4\uffff\1\52\1\53\1\54\1\55\1\56\4\uffff\1\73\1"+
        "\74\1\75\1\101\10\uffff\1\125\1\126\1\127\3\uffff\1\70\1\123\1\3"+
        "\5\uffff\1\14\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\107\1\110\1\76"+
        "\3\uffff\1\62\1\32\15\uffff\1\61\1\64\1\63\1\65\1\77\1\71\1\100"+
        "\1\72\1\uffff\1\106\3\uffff\1\120\1\uffff\1\121\11\uffff\1\67\22"+
        "\uffff\1\114\7\uffff\1\122\4\uffff\1\124\6\uffff\1\46\1\uffff\1"+
        "\60\7\uffff\1\34\1\35\2\uffff\1\115\12\uffff\1\111\1\116\4\uffff"+
        "\1\124\10\uffff\1\43\1\31\2\uffff\1\33\3\uffff\1\112\1\37\1\uffff"+
        "\1\104\1\41\1\uffff\1\44\1\47\1\113\13\uffff\1\5\1\40\1\50\1\uffff"+
        "\1\45\1\105\1\103\5\uffff\1\26\1\uffff\1\4\3\uffff\1\51\1\uffff"+
        "\1\102\1\117\1\uffff\1\1\1\27\1\57\2\uffff\1\42\2\uffff\1\6\1\uffff"+
        "\1\66";
    static final String DFA28_specialS =
        "\54\uffff\1\2\75\uffff\1\5\42\uffff\1\10\1\uffff\1\4\1\7\44\uffff"+
        "\1\3\1\1\35\uffff\1\0\35\uffff\1\6\13\uffff}>";
    static final String[] DFA28_transitionS = {
            "\2\57\1\uffff\2\57\22\uffff\1\57\1\34\1\55\1\41\1\56\1\43\1"+
            "\36\1\54\1\14\1\15\1\23\1\37\1\11\1\40\1\3\1\42\1\52\11\53\1"+
            "\16\1\2\1\10\1\33\1\12\1\32\1\13\32\56\1\30\1\uffff\1\31\1\35"+
            "\1\46\1\uffff\1\50\1\26\1\5\1\21\1\51\1\17\2\56\1\4\2\56\1\27"+
            "\1\56\1\25\1\47\1\1\1\56\1\45\1\22\1\24\6\56\1\6\1\20\1\7\1"+
            "\44\101\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\60\20\uffff\1\62\2\uffff\1\61",
            "",
            "\1\63\1\uffff\12\64",
            "\1\70\6\uffff\1\66\1\67",
            "\1\72\3\uffff\1\71",
            "",
            "",
            "",
            "",
            "",
            "\1\76\2\uffff\1\101\1\73\3\uffff\1\100\2\uffff\1\102\1\uffff"+
            "\1\75\1\74\1\77\14\uffff\1\104\15\uffff\1\103",
            "",
            "",
            "",
            "\1\107\12\uffff\1\110\5\uffff\1\106",
            "\1\111",
            "\1\113\5\uffff\1\114",
            "\1\115\2\uffff\1\116\6\uffff\1\120\5\uffff\1\117",
            "",
            "\1\122\11\uffff\1\121",
            "\1\124\5\uffff\1\123",
            "\1\125\11\uffff\1\126",
            "\1\127",
            "",
            "",
            "",
            "",
            "",
            "\1\130",
            "\1\132",
            "\1\134",
            "\1\136",
            "",
            "",
            "",
            "",
            "\1\140",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\64\1\uffff\10\146\2\64\12\uffff\3\64\21\uffff\1\145\13\uffff"+
            "\3\64\21\uffff\1\145",
            "\1\64\1\uffff\12\150\12\uffff\3\64\35\uffff\3\64",
            "\47\152\1\55\64\152\1\151\uffa3\152",
            "",
            "",
            "",
            "\1\153",
            "\1\154",
            "\1\155",
            "",
            "",
            "",
            "\1\156",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\22"+
            "\56\1\160\1\157\6\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08"+
            "\56\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "\1\162",
            "\1\163",
            "\1\164",
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
            "\1\165",
            "\1\166",
            "\1\167",
            "",
            "",
            "\1\170",
            "\1\171",
            "\1\173\2\uffff\1\172",
            "\1\174",
            "\1\176\2\uffff\1\175",
            "\1\177",
            "\1\u0080",
            "\1\u0082\10\uffff\1\u0081",
            "\1\u0083",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u0085",
            "\1\u0086",
            "\1\u0087",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u0088",
            "",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "",
            "\1\64\1\uffff\10\146\2\64\12\uffff\3\64\35\uffff\3\64",
            "",
            "\1\64\1\uffff\12\150\12\uffff\3\64\35\uffff\3\64",
            "\1\u008d\4\uffff\1\u008d\10\uffff\4\u008f\4\u0090\44\uffff"+
            "\1\u008d\5\uffff\1\u008d\3\uffff\1\u008d\7\uffff\1\u008d\3\uffff"+
            "\1\u008d\1\uffff\1\u008d\1\u008e",
            "\47\55\1\u0091\uffd8\55",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094",
            "\1\u0096\2\uffff\1\u0095",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\4\56"+
            "\1\u0097\25\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56"+
            "\u1040\uffff\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e"+
            "\56\u10d2\uffff\u5200\56\u5900\uffff\u0200\56",
            "\1\u0099",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u009b",
            "\1\u009c",
            "\1\u009d",
            "\1\u009e",
            "\1\u009f",
            "\1\u00a0",
            "\1\u00a1",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00a4",
            "\1\u00a5",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\1\u00aa",
            "\1\u00ab",
            "",
            "\1\u00ac",
            "\1\u00ad",
            "\1\u00af\1\uffff\1\u00ae",
            "\1\u00b0",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00b3",
            "",
            "\47\55\1\u0091\uffd8\55",
            "\12\u00b4\7\uffff\6\u00b4\32\uffff\6\u00b4",
            "\47\55\1\u0091\10\55\10\u00b5\uffc8\55",
            "\47\55\1\u0091\10\55\10\u00b6\uffc8\55",
            "",
            "\1\u00b8",
            "\1\u00b9",
            "\1\u00ba",
            "\1\u00bb",
            "\1\u00bc",
            "\1\u00bd",
            "",
            "\1\u00be",
            "",
            "\1\u00bf",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00c2",
            "\1\u00c3",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00c5",
            "",
            "",
            "\1\u00c6",
            "\1\u00c7",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00ca",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00cd",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00d1",
            "",
            "",
            "\1\u00d2",
            "\12\u00d3\7\uffff\6\u00d3\32\uffff\6\u00d3",
            "\47\55\1\u0091\10\55\10\u00d4\uffc8\55",
            "\47\55\1\u0091\uffd8\55",
            "",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\1\u00d9",
            "\1\u00da",
            "\1\u00db",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\u00df",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "\1\u00e3",
            "",
            "",
            "",
            "\1\u00e4",
            "\1\u00e5",
            "\12\u00e6\7\uffff\6\u00e6\32\uffff\6\u00e6",
            "\47\55\1\u0091\uffd8\55",
            "\1\u00e7",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00e9",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00eb",
            "\1\u00ec",
            "\1\u00ed",
            "",
            "",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "",
            "\1\u00ef",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\12\u00f2\7\uffff\6\u00f2\32\uffff\6\u00f2",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00f6",
            "\1\u00f7",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "",
            "",
            "\47\55\1\u0091\uffd8\55",
            "",
            "",
            "",
            "\1\u00f9",
            "\1\u00fa",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            "\1\u00fc",
            "",
            "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32"+
            "\56\105\uffff\27\56\1\uffff\37\56\1\uffff\u1f08\56\u1040\uffff"+
            "\u0150\56\u0170\uffff\u0080\56\u0080\uffff\u092e\56\u10d2\uffff"+
            "\u5200\56\u5900\uffff\u0200\56",
            ""
    };

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__139 | T__140 | T__141 | T__142 | T__143 | T__144 | T__145 | T__146 | T__147 | T__148 | T__149 | T__150 | T__151 | T__152 | T__153 | T__154 | T__155 | T__156 | T__157 | T__158 | T__159 | T__160 | T__161 | T__162 | T__163 | T__164 | T__165 | T__166 | T__167 | T__168 | T__169 | T__170 | T__171 | T__172 | T__173 | T__174 | T__175 | T__176 | T__177 | T__178 | T__179 | T__180 | T__181 | T__182 | T__183 | T__184 | T__185 | T__186 | T__187 | T__188 | T__189 | T__190 | T__191 | T__192 | T__193 | T__194 | T__195 | T__196 | T__197 | T__198 | T__199 | T__200 | T__201 | T__202 | T__203 | T__204 | T__205 | T__206 | T__207 | T__208 | T__209 | T__210 | T__211 | T__212 | T__213 | T__214 | T__215 | T__216 | T__217 | HexLiteral | DecimalLiteral | OctalLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | Identifier | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA28_212 = input.LA(1);

                        s = -1;
                        if ( (LA28_212=='\'') ) {s = 145;}

                        else if ( ((LA28_212>='\u0000' && LA28_212<='&')||(LA28_212>='(' && LA28_212<='\uFFFF')) ) {s = 45;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA28_182 = input.LA(1);

                        s = -1;
                        if ( (LA28_182=='\'') ) {s = 145;}

                        else if ( ((LA28_182>='\u0000' && LA28_182<='&')||(LA28_182>='(' && LA28_182<='\uFFFF')) ) {s = 45;}

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA28_44 = input.LA(1);

                        s = -1;
                        if ( (LA28_44=='\\') ) {s = 105;}

                        else if ( ((LA28_44>='\u0000' && LA28_44<='&')||(LA28_44>='(' && LA28_44<='[')||(LA28_44>=']' && LA28_44<='\uFFFF')) ) {s = 106;}

                        else if ( (LA28_44=='\'') ) {s = 45;}

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA28_181 = input.LA(1);

                        s = -1;
                        if ( ((LA28_181>='0' && LA28_181<='7')) ) {s = 212;}

                        else if ( (LA28_181=='\'') ) {s = 145;}

                        else if ( ((LA28_181>='\u0000' && LA28_181<='&')||(LA28_181>='(' && LA28_181<='/')||(LA28_181>='8' && LA28_181<='\uFFFF')) ) {s = 45;}

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA28_143 = input.LA(1);

                        s = -1;
                        if ( ((LA28_143>='0' && LA28_143<='7')) ) {s = 181;}

                        else if ( (LA28_143=='\'') ) {s = 145;}

                        else if ( ((LA28_143>='\u0000' && LA28_143<='&')||(LA28_143>='(' && LA28_143<='/')||(LA28_143>='8' && LA28_143<='\uFFFF')) ) {s = 45;}

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA28_106 = input.LA(1);

                        s = -1;
                        if ( (LA28_106=='\'') ) {s = 145;}

                        else if ( ((LA28_106>='\u0000' && LA28_106<='&')||(LA28_106>='(' && LA28_106<='\uFFFF')) ) {s = 45;}

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA28_242 = input.LA(1);

                        s = -1;
                        if ( (LA28_242=='\'') ) {s = 145;}

                        else if ( ((LA28_242>='\u0000' && LA28_242<='&')||(LA28_242>='(' && LA28_242<='\uFFFF')) ) {s = 45;}

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA28_144 = input.LA(1);

                        s = -1;
                        if ( (LA28_144=='\'') ) {s = 145;}

                        else if ( ((LA28_144>='\u0000' && LA28_144<='&')||(LA28_144>='(' && LA28_144<='/')||(LA28_144>='8' && LA28_144<='\uFFFF')) ) {s = 45;}

                        else if ( ((LA28_144>='0' && LA28_144<='7')) ) {s = 182;}

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA28_141 = input.LA(1);

                        s = -1;
                        if ( (LA28_141=='\'') ) {s = 145;}

                        else if ( ((LA28_141>='\u0000' && LA28_141<='&')||(LA28_141>='(' && LA28_141<='\uFFFF')) ) {s = 45;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 28, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}