// $ANTLR 3.2 Sep 23, 2009 12:02:23 SpecFile.g 2010-07-19 14:31:29
 
package edu.mit.csail.sdg.squander.specfile.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"unused"})
public class SpecFileLexer extends Lexer {
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int T__27=27;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int SPECFILE=4;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int INVARIANT=7;
    public static final int EOF=-1;
    public static final int HexDigit=16;
    public static final int Identifier=9;
    public static final int StringLiteral=10;
    public static final int T__30=30;
    public static final int T__19=19;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int WS=17;
    public static final int T__18=18;
    public static final int SPECFIELD=5;
    public static final int UnicodeEscape=14;
    public static final int JavaIDDigit=12;
    public static final int FUNCFIELD=6;
    public static final int Letter=11;
    public static final int EscapeSequence=13;
    public static final int OctalEscape=15;
    public static final int PARAMS=8;

    // delegates
    // delegators

    public SpecFileLexer() {;} 
    public SpecFileLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public SpecFileLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "SpecFile.g"; }

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:7:7: ( '<' )
            // SpecFile.g:7:9: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:8:7: ( '>' )
            // SpecFile.g:8:9: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:9:7: ( '{' )
            // SpecFile.g:9:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:10:7: ( '}' )
            // SpecFile.g:10:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:11:7: ( 'private' )
            // SpecFile.g:11:9: 'private'
            {
            match("private"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:12:7: ( 'public' )
            // SpecFile.g:12:9: 'public'
            {
            match("public"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:13:7: ( 'protected' )
            // SpecFile.g:13:9: 'protected'
            {
            match("protected"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:14:7: ( 'class' )
            // SpecFile.g:14:9: 'class'
            {
            match("class"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:15:7: ( 'interface' )
            // SpecFile.g:15:9: 'interface'
            {
            match("interface"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:16:7: ( '@SpecField' )
            // SpecFile.g:16:9: '@SpecField'
            {
            match("@SpecField"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:17:7: ( '(' )
            // SpecFile.g:17:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:18:7: ( ')' )
            // SpecFile.g:18:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:19:7: ( ';' )
            // SpecFile.g:19:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:20:7: ( '@FuncField' )
            // SpecFile.g:20:9: '@FuncField'
            {
            match("@FuncField"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:21:7: ( '@Invariant' )
            // SpecFile.g:21:9: '@Invariant'
            {
            match("@Invariant"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:22:7: ( ',' )
            // SpecFile.g:22:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "Identifier"
    public final void mIdentifier() throws RecognitionException {
        try {
            int _type = Identifier;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:92:5: ( Letter ( Letter | JavaIDDigit )* ( '[]' )? )
            // SpecFile.g:92:9: Letter ( Letter | JavaIDDigit )* ( '[]' )?
            {
            mLetter(); 
            // SpecFile.g:92:16: ( Letter | JavaIDDigit )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='$'||(LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')||(LA1_0>='\u00C0' && LA1_0<='\u00D6')||(LA1_0>='\u00D8' && LA1_0<='\u00F6')||(LA1_0>='\u00F8' && LA1_0<='\u1FFF')||(LA1_0>='\u3040' && LA1_0<='\u318F')||(LA1_0>='\u3300' && LA1_0<='\u337F')||(LA1_0>='\u3400' && LA1_0<='\u3D2D')||(LA1_0>='\u4E00' && LA1_0<='\u9FFF')||(LA1_0>='\uF900' && LA1_0<='\uFAFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
                case 1 :
                    // SpecFile.g:
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
                    break loop1;
                }
            } while (true);

            // SpecFile.g:92:38: ( '[]' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='[') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // SpecFile.g:92:38: '[]'
                    {
                    match("[]"); 


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
    // $ANTLR end "Identifier"

    // $ANTLR start "StringLiteral"
    public final void mStringLiteral() throws RecognitionException {
        try {
            int _type = StringLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // SpecFile.g:96:5: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
            // SpecFile.g:96:8: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // SpecFile.g:96:12: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
            loop3:
            do {
                int alt3=3;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='\\') ) {
                    alt3=1;
                }
                else if ( ((LA3_0>='\u0000' && LA3_0<='!')||(LA3_0>='#' && LA3_0<='[')||(LA3_0>=']' && LA3_0<='\uFFFF')) ) {
                    alt3=2;
                }


                switch (alt3) {
                case 1 :
                    // SpecFile.g:96:14: EscapeSequence
                    {
                    mEscapeSequence(); 

                    }
                    break;
                case 2 :
                    // SpecFile.g:96:31: ~ ( '\\\\' | '\"' )
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
                    break loop3;
                }
            } while (true);

            match('\"'); 

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
            // SpecFile.g:101:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
            int alt4=3;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='\\') ) {
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
                    alt4=1;
                    }
                    break;
                case 'u':
                    {
                    alt4=2;
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
                    alt4=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // SpecFile.g:101:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
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
                    // SpecFile.g:102:9: UnicodeEscape
                    {
                    mUnicodeEscape(); 

                    }
                    break;
                case 3 :
                    // SpecFile.g:103:9: OctalEscape
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
            // SpecFile.g:108:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt5=3;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='\\') ) {
                int LA5_1 = input.LA(2);

                if ( ((LA5_1>='0' && LA5_1<='3')) ) {
                    int LA5_2 = input.LA(3);

                    if ( ((LA5_2>='0' && LA5_2<='7')) ) {
                        int LA5_4 = input.LA(4);

                        if ( ((LA5_4>='0' && LA5_4<='7')) ) {
                            alt5=1;
                        }
                        else {
                            alt5=2;}
                    }
                    else {
                        alt5=3;}
                }
                else if ( ((LA5_1>='4' && LA5_1<='7')) ) {
                    int LA5_3 = input.LA(3);

                    if ( ((LA5_3>='0' && LA5_3<='7')) ) {
                        alt5=2;
                    }
                    else {
                        alt5=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // SpecFile.g:108:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // SpecFile.g:108:14: ( '0' .. '3' )
                    // SpecFile.g:108:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // SpecFile.g:108:25: ( '0' .. '7' )
                    // SpecFile.g:108:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // SpecFile.g:108:36: ( '0' .. '7' )
                    // SpecFile.g:108:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // SpecFile.g:109:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // SpecFile.g:109:14: ( '0' .. '7' )
                    // SpecFile.g:109:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // SpecFile.g:109:25: ( '0' .. '7' )
                    // SpecFile.g:109:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // SpecFile.g:110:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // SpecFile.g:110:14: ( '0' .. '7' )
                    // SpecFile.g:110:15: '0' .. '7'
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
            // SpecFile.g:115:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // SpecFile.g:115:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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

    // $ANTLR start "HexDigit"
    public final void mHexDigit() throws RecognitionException {
        try {
            // SpecFile.g:119:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // SpecFile.g:119:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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

    // $ANTLR start "Letter"
    public final void mLetter() throws RecognitionException {
        try {
            // SpecFile.g:124:5: ( '\\u0024' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u00ff' | '\\u0100' .. '\\u1fff' | '\\u3040' .. '\\u318f' | '\\u3300' .. '\\u337f' | '\\u3400' .. '\\u3d2d' | '\\u4e00' .. '\\u9fff' | '\\uf900' .. '\\ufaff' )
            // SpecFile.g:
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
            // SpecFile.g:141:5: ( '\\u0030' .. '\\u0039' | '\\u0660' .. '\\u0669' | '\\u06f0' .. '\\u06f9' | '\\u0966' .. '\\u096f' | '\\u09e6' .. '\\u09ef' | '\\u0a66' .. '\\u0a6f' | '\\u0ae6' .. '\\u0aef' | '\\u0b66' .. '\\u0b6f' | '\\u0be7' .. '\\u0bef' | '\\u0c66' .. '\\u0c6f' | '\\u0ce6' .. '\\u0cef' | '\\u0d66' .. '\\u0d6f' | '\\u0e50' .. '\\u0e59' | '\\u0ed0' .. '\\u0ed9' | '\\u1040' .. '\\u1049' )
            // SpecFile.g:
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
            // SpecFile.g:158:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            // SpecFile.g:158:8: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
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
        // SpecFile.g:1:8: ( T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | Identifier | StringLiteral | WS )
        int alt6=19;
        alt6 = dfa6.predict(input);
        switch (alt6) {
            case 1 :
                // SpecFile.g:1:10: T__18
                {
                mT__18(); 

                }
                break;
            case 2 :
                // SpecFile.g:1:16: T__19
                {
                mT__19(); 

                }
                break;
            case 3 :
                // SpecFile.g:1:22: T__20
                {
                mT__20(); 

                }
                break;
            case 4 :
                // SpecFile.g:1:28: T__21
                {
                mT__21(); 

                }
                break;
            case 5 :
                // SpecFile.g:1:34: T__22
                {
                mT__22(); 

                }
                break;
            case 6 :
                // SpecFile.g:1:40: T__23
                {
                mT__23(); 

                }
                break;
            case 7 :
                // SpecFile.g:1:46: T__24
                {
                mT__24(); 

                }
                break;
            case 8 :
                // SpecFile.g:1:52: T__25
                {
                mT__25(); 

                }
                break;
            case 9 :
                // SpecFile.g:1:58: T__26
                {
                mT__26(); 

                }
                break;
            case 10 :
                // SpecFile.g:1:64: T__27
                {
                mT__27(); 

                }
                break;
            case 11 :
                // SpecFile.g:1:70: T__28
                {
                mT__28(); 

                }
                break;
            case 12 :
                // SpecFile.g:1:76: T__29
                {
                mT__29(); 

                }
                break;
            case 13 :
                // SpecFile.g:1:82: T__30
                {
                mT__30(); 

                }
                break;
            case 14 :
                // SpecFile.g:1:88: T__31
                {
                mT__31(); 

                }
                break;
            case 15 :
                // SpecFile.g:1:94: T__32
                {
                mT__32(); 

                }
                break;
            case 16 :
                // SpecFile.g:1:100: T__33
                {
                mT__33(); 

                }
                break;
            case 17 :
                // SpecFile.g:1:106: Identifier
                {
                mIdentifier(); 

                }
                break;
            case 18 :
                // SpecFile.g:1:117: StringLiteral
                {
                mStringLiteral(); 

                }
                break;
            case 19 :
                // SpecFile.g:1:131: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA6 dfa6 = new DFA6(this);
    static final String DFA6_eotS =
        "\5\uffff\3\15\10\uffff\4\15\3\uffff\15\15\1\51\3\15\1\55\1\uffff"+
        "\1\15\1\57\1\15\1\uffff\1\15\1\uffff\2\15\1\64\1\65\2\uffff";
    static final String DFA6_eofS =
        "\66\uffff";
    static final String DFA6_minS =
        "\1\11\4\uffff\1\162\1\154\1\156\1\106\7\uffff\1\151\1\142\1\141"+
        "\1\164\3\uffff\1\166\1\164\1\154\1\163\1\145\1\141\1\145\1\151\1"+
        "\163\1\162\1\164\2\143\1\44\1\146\1\145\1\164\1\44\1\uffff\1\141"+
        "\1\44\1\145\1\uffff\1\143\1\uffff\1\144\1\145\2\44\2\uffff";
    static final String DFA6_maxS =
        "\1\ufaff\4\uffff\1\165\1\154\1\156\1\123\7\uffff\1\157\1\142\1\141"+
        "\1\164\3\uffff\1\166\1\164\1\154\1\163\1\145\1\141\1\145\1\151\1"+
        "\163\1\162\1\164\2\143\1\ufaff\1\146\1\145\1\164\1\ufaff\1\uffff"+
        "\1\141\1\ufaff\1\145\1\uffff\1\143\1\uffff\1\144\1\145\2\ufaff\2"+
        "\uffff";
    static final String DFA6_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\4\uffff\1\13\1\14\1\15\1\20\1\21\1\22\1"+
        "\23\4\uffff\1\12\1\16\1\17\22\uffff\1\10\3\uffff\1\6\1\uffff\1\5"+
        "\4\uffff\1\7\1\11";
    static final String DFA6_specialS =
        "\66\uffff}>";
    static final String[] DFA6_transitionS = {
            "\2\17\1\uffff\2\17\22\uffff\1\17\1\uffff\1\16\1\uffff\1\15\3"+
            "\uffff\1\11\1\12\2\uffff\1\14\16\uffff\1\13\1\1\1\uffff\1\2"+
            "\1\uffff\1\10\32\15\4\uffff\1\15\1\uffff\2\15\1\6\5\15\1\7\6"+
            "\15\1\5\12\15\1\3\1\uffff\1\4\102\uffff\27\15\1\uffff\37\15"+
            "\1\uffff\u1f08\15\u1040\uffff\u0150\15\u0170\uffff\u0080\15"+
            "\u0080\uffff\u092e\15\u10d2\uffff\u5200\15\u5900\uffff\u0200"+
            "\15",
            "",
            "",
            "",
            "",
            "\1\20\2\uffff\1\21",
            "\1\22",
            "\1\23",
            "\1\25\2\uffff\1\26\11\uffff\1\24",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\27\5\uffff\1\30",
            "\1\31",
            "\1\32",
            "\1\33",
            "",
            "",
            "",
            "\1\34",
            "\1\35",
            "\1\36",
            "\1\37",
            "\1\40",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\44",
            "\1\45",
            "\1\46",
            "\1\47",
            "\1\50",
            "\1\15\13\uffff\12\15\7\uffff\33\15\3\uffff\1\15\1\uffff\32"+
            "\15\105\uffff\27\15\1\uffff\37\15\1\uffff\u1f08\15\u1040\uffff"+
            "\u0150\15\u0170\uffff\u0080\15\u0080\uffff\u092e\15\u10d2\uffff"+
            "\u5200\15\u5900\uffff\u0200\15",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\15\13\uffff\12\15\7\uffff\33\15\3\uffff\1\15\1\uffff\32"+
            "\15\105\uffff\27\15\1\uffff\37\15\1\uffff\u1f08\15\u1040\uffff"+
            "\u0150\15\u0170\uffff\u0080\15\u0080\uffff\u092e\15\u10d2\uffff"+
            "\u5200\15\u5900\uffff\u0200\15",
            "",
            "\1\56",
            "\1\15\13\uffff\12\15\7\uffff\33\15\3\uffff\1\15\1\uffff\32"+
            "\15\105\uffff\27\15\1\uffff\37\15\1\uffff\u1f08\15\u1040\uffff"+
            "\u0150\15\u0170\uffff\u0080\15\u0080\uffff\u092e\15\u10d2\uffff"+
            "\u5200\15\u5900\uffff\u0200\15",
            "\1\60",
            "",
            "\1\61",
            "",
            "\1\62",
            "\1\63",
            "\1\15\13\uffff\12\15\7\uffff\33\15\3\uffff\1\15\1\uffff\32"+
            "\15\105\uffff\27\15\1\uffff\37\15\1\uffff\u1f08\15\u1040\uffff"+
            "\u0150\15\u0170\uffff\u0080\15\u0080\uffff\u092e\15\u10d2\uffff"+
            "\u5200\15\u5900\uffff\u0200\15",
            "\1\15\13\uffff\12\15\7\uffff\33\15\3\uffff\1\15\1\uffff\32"+
            "\15\105\uffff\27\15\1\uffff\37\15\1\uffff\u1f08\15\u1040\uffff"+
            "\u0150\15\u0170\uffff\u0080\15\u0080\uffff\u092e\15\u10d2\uffff"+
            "\u5200\15\u5900\uffff\u0200\15",
            "",
            ""
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | Identifier | StringLiteral | WS );";
        }
    }
 

}