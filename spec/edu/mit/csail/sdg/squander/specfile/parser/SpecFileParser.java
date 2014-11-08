// $ANTLR 3.2 Sep 23, 2009 12:02:23 SpecFile.g 2010-07-19 14:31:29

package edu.mit.csail.sdg.squander.specfile.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

@SuppressWarnings({"unused"})
public class SpecFileParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SPECFILE", "SPECFIELD", "FUNCFIELD", "INVARIANT", "PARAMS", "Identifier", "StringLiteral", "Letter", "JavaIDDigit", "EscapeSequence", "UnicodeEscape", "OctalEscape", "HexDigit", "WS", "'<'", "'>'", "'{'", "'}'", "'private'", "'public'", "'protected'", "'class'", "'interface'", "'@SpecField'", "'('", "')'", "';'", "'@FuncField'", "'@Invariant'", "','"
    };
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
    public static final int T__19=19;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int WS=17;
    public static final int T__33=33;
    public static final int T__18=18;
    public static final int SPECFIELD=5;
    public static final int UnicodeEscape=14;
    public static final int JavaIDDigit=12;
    public static final int OctalEscape=15;
    public static final int EscapeSequence=13;
    public static final int Letter=11;
    public static final int FUNCFIELD=6;
    public static final int PARAMS=8;

    // delegates
    // delegators


        public SpecFileParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public SpecFileParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[28+1];
             
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return SpecFileParser.tokenNames; }
    public String getGrammarFileName() { return "SpecFile.g"; }


    // Base type of nodes in the AST 
    public static final class Node extends CommonTree {
        public Node(Token t) {
          super(t);
        }
        
        public Node(Node node) {
            super(node);
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


    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
        throw new SpecFileParserException(e);
    }


    public static class specfile_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "specfile"
    // SpecFile.g:62:1: specfile : type Identifier ( '<' params '>' )? '{' ( specfield )* ( funcfield )* ( invariant )* '}' -> ^( SPECFILE type Identifier ( params )? ( specfield )* ( funcfield )* ( invariant )* ) ;
    public final SpecFileParser.specfile_return specfile() throws RecognitionException {
        SpecFileParser.specfile_return retval = new SpecFileParser.specfile_return();
        retval.start = input.LT(1);
        int specfile_StartIndex = input.index();
        Node root_0 = null;

        Token Identifier2=null;
        Token char_literal3=null;
        Token char_literal5=null;
        Token char_literal6=null;
        Token char_literal10=null;
        SpecFileParser.type_return type1 = null;

        SpecFileParser.params_return params4 = null;

        SpecFileParser.specfield_return specfield7 = null;

        SpecFileParser.funcfield_return funcfield8 = null;

        SpecFileParser.invariant_return invariant9 = null;


        Node Identifier2_tree=null;
        Node char_literal3_tree=null;
        Node char_literal5_tree=null;
        Node char_literal6_tree=null;
        Node char_literal10_tree=null;
        RewriteRuleTokenStream stream_21=new RewriteRuleTokenStream(adaptor,"token 21");
        RewriteRuleTokenStream stream_20=new RewriteRuleTokenStream(adaptor,"token 20");
        RewriteRuleTokenStream stream_19=new RewriteRuleTokenStream(adaptor,"token 19");
        RewriteRuleTokenStream stream_18=new RewriteRuleTokenStream(adaptor,"token 18");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");
        RewriteRuleSubtreeStream stream_funcfield=new RewriteRuleSubtreeStream(adaptor,"rule funcfield");
        RewriteRuleSubtreeStream stream_invariant=new RewriteRuleSubtreeStream(adaptor,"rule invariant");
        RewriteRuleSubtreeStream stream_params=new RewriteRuleSubtreeStream(adaptor,"rule params");
        RewriteRuleSubtreeStream stream_type=new RewriteRuleSubtreeStream(adaptor,"rule type");
        RewriteRuleSubtreeStream stream_specfield=new RewriteRuleSubtreeStream(adaptor,"rule specfield");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }
            // SpecFile.g:62:9: ( type Identifier ( '<' params '>' )? '{' ( specfield )* ( funcfield )* ( invariant )* '}' -> ^( SPECFILE type Identifier ( params )? ( specfield )* ( funcfield )* ( invariant )* ) )
            // SpecFile.g:62:11: type Identifier ( '<' params '>' )? '{' ( specfield )* ( funcfield )* ( invariant )* '}'
            {
            pushFollow(FOLLOW_type_in_specfile105);
            type1=type();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_type.add(type1.getTree());
            Identifier2=(Token)match(input,Identifier,FOLLOW_Identifier_in_specfile107); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Identifier.add(Identifier2);

            // SpecFile.g:62:27: ( '<' params '>' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==18) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // SpecFile.g:62:28: '<' params '>'
                    {
                    char_literal3=(Token)match(input,18,FOLLOW_18_in_specfile110); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_18.add(char_literal3);

                    pushFollow(FOLLOW_params_in_specfile112);
                    params4=params();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_params.add(params4.getTree());
                    char_literal5=(Token)match(input,19,FOLLOW_19_in_specfile114); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_19.add(char_literal5);


                    }
                    break;

            }

            char_literal6=(Token)match(input,20,FOLLOW_20_in_specfile118); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_20.add(char_literal6);

            // SpecFile.g:62:49: ( specfield )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==27) ) {
                    alt2=1;
                }


                switch (alt2) {
                case 1 :
                    // SpecFile.g:0:0: specfield
                    {
                    pushFollow(FOLLOW_specfield_in_specfile120);
                    specfield7=specfield();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_specfield.add(specfield7.getTree());

                    }
                    break;

                default :
                    break loop2;
                }
            } while (true);

            // SpecFile.g:62:60: ( funcfield )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==31) ) {
                    alt3=1;
                }


                switch (alt3) {
                case 1 :
                    // SpecFile.g:0:0: funcfield
                    {
                    pushFollow(FOLLOW_funcfield_in_specfile123);
                    funcfield8=funcfield();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_funcfield.add(funcfield8.getTree());

                    }
                    break;

                default :
                    break loop3;
                }
            } while (true);

            // SpecFile.g:62:71: ( invariant )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==32) ) {
                    alt4=1;
                }


                switch (alt4) {
                case 1 :
                    // SpecFile.g:0:0: invariant
                    {
                    pushFollow(FOLLOW_invariant_in_specfile126);
                    invariant9=invariant();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_invariant.add(invariant9.getTree());

                    }
                    break;

                default :
                    break loop4;
                }
            } while (true);

            char_literal10=(Token)match(input,21,FOLLOW_21_in_specfile129); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_21.add(char_literal10);



            // AST REWRITE
            // elements: funcfield, params, type, invariant, specfield, Identifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 63:5: -> ^( SPECFILE type Identifier ( params )? ( specfield )* ( funcfield )* ( invariant )* )
            {
                // SpecFile.g:63:8: ^( SPECFILE type Identifier ( params )? ( specfield )* ( funcfield )* ( invariant )* )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(SPECFILE, "SPECFILE"), root_1);

                adaptor.addChild(root_1, stream_type.nextTree());
                adaptor.addChild(root_1, stream_Identifier.nextNode());
                // SpecFile.g:63:35: ( params )?
                if ( stream_params.hasNext() ) {
                    adaptor.addChild(root_1, stream_params.nextTree());

                }
                stream_params.reset();
                // SpecFile.g:63:43: ( specfield )*
                while ( stream_specfield.hasNext() ) {
                    adaptor.addChild(root_1, stream_specfield.nextTree());

                }
                stream_specfield.reset();
                // SpecFile.g:63:54: ( funcfield )*
                while ( stream_funcfield.hasNext() ) {
                    adaptor.addChild(root_1, stream_funcfield.nextTree());

                }
                stream_funcfield.reset();
                // SpecFile.g:63:65: ( invariant )*
                while ( stream_invariant.hasNext() ) {
                    adaptor.addChild(root_1, stream_invariant.nextTree());

                }
                stream_invariant.reset();

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
            if ( state.backtracking>0 ) { memoize(input, 1, specfile_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "specfile"

    public static class type_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "type"
    // SpecFile.g:65:1: type : ( mod )? cls ;
    public final SpecFileParser.type_return type() throws RecognitionException {
        SpecFileParser.type_return retval = new SpecFileParser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        Node root_0 = null;

        SpecFileParser.mod_return mod11 = null;

        SpecFileParser.cls_return cls12 = null;



        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }
            // SpecFile.g:65:5: ( ( mod )? cls )
            // SpecFile.g:65:7: ( mod )? cls
            {
            root_0 = (Node)adaptor.nil();

            // SpecFile.g:65:7: ( mod )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( ((LA5_0>=22 && LA5_0<=24)) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // SpecFile.g:0:0: mod
                    {
                    pushFollow(FOLLOW_mod_in_type162);
                    mod11=mod();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, mod11.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_cls_in_type165);
            cls12=cls();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, cls12.getTree());

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
            if ( state.backtracking>0 ) { memoize(input, 2, type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "type"

    public static class mod_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mod"
    // SpecFile.g:66:1: mod : ( 'private' | 'public' | 'protected' );
    public final SpecFileParser.mod_return mod() throws RecognitionException {
        SpecFileParser.mod_return retval = new SpecFileParser.mod_return();
        retval.start = input.LT(1);
        int mod_StartIndex = input.index();
        Node root_0 = null;

        Token set13=null;

        Node set13_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }
            // SpecFile.g:66:4: ( 'private' | 'public' | 'protected' )
            // SpecFile.g:
            {
            root_0 = (Node)adaptor.nil();

            set13=(Token)input.LT(1);
            if ( (input.LA(1)>=22 && input.LA(1)<=24) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Node)adaptor.create(set13));
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
            if ( state.backtracking>0 ) { memoize(input, 3, mod_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "mod"

    public static class cls_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "cls"
    // SpecFile.g:67:1: cls : ( 'class' | 'interface' );
    public final SpecFileParser.cls_return cls() throws RecognitionException {
        SpecFileParser.cls_return retval = new SpecFileParser.cls_return();
        retval.start = input.LT(1);
        int cls_StartIndex = input.index();
        Node root_0 = null;

        Token set14=null;

        Node set14_tree=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }
            // SpecFile.g:67:4: ( 'class' | 'interface' )
            // SpecFile.g:
            {
            root_0 = (Node)adaptor.nil();

            set14=(Token)input.LT(1);
            if ( (input.LA(1)>=25 && input.LA(1)<=26) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Node)adaptor.create(set14));
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
            if ( state.backtracking>0 ) { memoize(input, 4, cls_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "cls"

    public static class specfield_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "specfield"
    // SpecFile.g:69:1: specfield : ( '@SpecField' '(' StringLiteral ')' ( ';' )? -> ^( SPECFIELD StringLiteral ) | '@SpecField' '(' '{' strings '}' ')' ( ';' )? -> ^( SPECFIELD strings ) );
    public final SpecFileParser.specfield_return specfield() throws RecognitionException {
        SpecFileParser.specfield_return retval = new SpecFileParser.specfield_return();
        retval.start = input.LT(1);
        int specfield_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal15=null;
        Token char_literal16=null;
        Token StringLiteral17=null;
        Token char_literal18=null;
        Token char_literal19=null;
        Token string_literal20=null;
        Token char_literal21=null;
        Token char_literal22=null;
        Token char_literal24=null;
        Token char_literal25=null;
        Token char_literal26=null;
        SpecFileParser.strings_return strings23 = null;


        Node string_literal15_tree=null;
        Node char_literal16_tree=null;
        Node StringLiteral17_tree=null;
        Node char_literal18_tree=null;
        Node char_literal19_tree=null;
        Node string_literal20_tree=null;
        Node char_literal21_tree=null;
        Node char_literal22_tree=null;
        Node char_literal24_tree=null;
        Node char_literal25_tree=null;
        Node char_literal26_tree=null;
        RewriteRuleTokenStream stream_21=new RewriteRuleTokenStream(adaptor,"token 21");
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_20=new RewriteRuleTokenStream(adaptor,"token 20");
        RewriteRuleTokenStream stream_30=new RewriteRuleTokenStream(adaptor,"token 30");
        RewriteRuleTokenStream stream_27=new RewriteRuleTokenStream(adaptor,"token 27");
        RewriteRuleTokenStream stream_28=new RewriteRuleTokenStream(adaptor,"token 28");
        RewriteRuleTokenStream stream_29=new RewriteRuleTokenStream(adaptor,"token 29");
        RewriteRuleSubtreeStream stream_strings=new RewriteRuleSubtreeStream(adaptor,"rule strings");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // SpecFile.g:70:5: ( '@SpecField' '(' StringLiteral ')' ( ';' )? -> ^( SPECFIELD StringLiteral ) | '@SpecField' '(' '{' strings '}' ')' ( ';' )? -> ^( SPECFIELD strings ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==27) ) {
                int LA8_1 = input.LA(2);

                if ( (LA8_1==28) ) {
                    int LA8_2 = input.LA(3);

                    if ( (LA8_2==StringLiteral) ) {
                        alt8=1;
                    }
                    else if ( (LA8_2==20) ) {
                        alt8=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 8, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // SpecFile.g:70:7: '@SpecField' '(' StringLiteral ')' ( ';' )?
                    {
                    string_literal15=(Token)match(input,27,FOLLOW_27_in_specfield202); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_27.add(string_literal15);

                    char_literal16=(Token)match(input,28,FOLLOW_28_in_specfield204); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_28.add(char_literal16);

                    StringLiteral17=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_specfield206); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_StringLiteral.add(StringLiteral17);

                    char_literal18=(Token)match(input,29,FOLLOW_29_in_specfield208); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_29.add(char_literal18);

                    // SpecFile.g:70:42: ( ';' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==30) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // SpecFile.g:0:0: ';'
                            {
                            char_literal19=(Token)match(input,30,FOLLOW_30_in_specfield210); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_30.add(char_literal19);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: StringLiteral
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 70:50: -> ^( SPECFIELD StringLiteral )
                    {
                        // SpecFile.g:70:53: ^( SPECFIELD StringLiteral )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(SPECFIELD, "SPECFIELD"), root_1);

                        adaptor.addChild(root_1, stream_StringLiteral.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // SpecFile.g:71:7: '@SpecField' '(' '{' strings '}' ')' ( ';' )?
                    {
                    string_literal20=(Token)match(input,27,FOLLOW_27_in_specfield230); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_27.add(string_literal20);

                    char_literal21=(Token)match(input,28,FOLLOW_28_in_specfield232); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_28.add(char_literal21);

                    char_literal22=(Token)match(input,20,FOLLOW_20_in_specfield234); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_20.add(char_literal22);

                    pushFollow(FOLLOW_strings_in_specfield236);
                    strings23=strings();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_strings.add(strings23.getTree());
                    char_literal24=(Token)match(input,21,FOLLOW_21_in_specfield238); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_21.add(char_literal24);

                    char_literal25=(Token)match(input,29,FOLLOW_29_in_specfield240); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_29.add(char_literal25);

                    // SpecFile.g:71:44: ( ';' )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==30) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // SpecFile.g:0:0: ';'
                            {
                            char_literal26=(Token)match(input,30,FOLLOW_30_in_specfield242); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_30.add(char_literal26);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: strings
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 71:50: -> ^( SPECFIELD strings )
                    {
                        // SpecFile.g:71:53: ^( SPECFIELD strings )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(SPECFIELD, "SPECFIELD"), root_1);

                        adaptor.addChild(root_1, stream_strings.nextTree());

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
            if ( state.backtracking>0 ) { memoize(input, 5, specfield_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "specfield"

    public static class funcfield_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "funcfield"
    // SpecFile.g:73:1: funcfield : ( '@FuncField' '(' StringLiteral ')' ( ';' )? -> ^( FUNCFIELD StringLiteral ) | '@FuncField' '(' '{' strings '}' ')' ( ';' )? -> ^( FUNCFIELD strings ) );
    public final SpecFileParser.funcfield_return funcfield() throws RecognitionException {
        SpecFileParser.funcfield_return retval = new SpecFileParser.funcfield_return();
        retval.start = input.LT(1);
        int funcfield_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal27=null;
        Token char_literal28=null;
        Token StringLiteral29=null;
        Token char_literal30=null;
        Token char_literal31=null;
        Token string_literal32=null;
        Token char_literal33=null;
        Token char_literal34=null;
        Token char_literal36=null;
        Token char_literal37=null;
        Token char_literal38=null;
        SpecFileParser.strings_return strings35 = null;


        Node string_literal27_tree=null;
        Node char_literal28_tree=null;
        Node StringLiteral29_tree=null;
        Node char_literal30_tree=null;
        Node char_literal31_tree=null;
        Node string_literal32_tree=null;
        Node char_literal33_tree=null;
        Node char_literal34_tree=null;
        Node char_literal36_tree=null;
        Node char_literal37_tree=null;
        Node char_literal38_tree=null;
        RewriteRuleTokenStream stream_21=new RewriteRuleTokenStream(adaptor,"token 21");
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_20=new RewriteRuleTokenStream(adaptor,"token 20");
        RewriteRuleTokenStream stream_30=new RewriteRuleTokenStream(adaptor,"token 30");
        RewriteRuleTokenStream stream_31=new RewriteRuleTokenStream(adaptor,"token 31");
        RewriteRuleTokenStream stream_28=new RewriteRuleTokenStream(adaptor,"token 28");
        RewriteRuleTokenStream stream_29=new RewriteRuleTokenStream(adaptor,"token 29");
        RewriteRuleSubtreeStream stream_strings=new RewriteRuleSubtreeStream(adaptor,"rule strings");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // SpecFile.g:74:5: ( '@FuncField' '(' StringLiteral ')' ( ';' )? -> ^( FUNCFIELD StringLiteral ) | '@FuncField' '(' '{' strings '}' ')' ( ';' )? -> ^( FUNCFIELD strings ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==31) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==28) ) {
                    int LA11_2 = input.LA(3);

                    if ( (LA11_2==StringLiteral) ) {
                        alt11=1;
                    }
                    else if ( (LA11_2==20) ) {
                        alt11=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 11, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // SpecFile.g:74:7: '@FuncField' '(' StringLiteral ')' ( ';' )?
                    {
                    string_literal27=(Token)match(input,31,FOLLOW_31_in_funcfield264); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_31.add(string_literal27);

                    char_literal28=(Token)match(input,28,FOLLOW_28_in_funcfield266); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_28.add(char_literal28);

                    StringLiteral29=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_funcfield268); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_StringLiteral.add(StringLiteral29);

                    char_literal30=(Token)match(input,29,FOLLOW_29_in_funcfield270); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_29.add(char_literal30);

                    // SpecFile.g:74:42: ( ';' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==30) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // SpecFile.g:0:0: ';'
                            {
                            char_literal31=(Token)match(input,30,FOLLOW_30_in_funcfield272); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_30.add(char_literal31);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: StringLiteral
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 74:50: -> ^( FUNCFIELD StringLiteral )
                    {
                        // SpecFile.g:74:53: ^( FUNCFIELD StringLiteral )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(FUNCFIELD, "FUNCFIELD"), root_1);

                        adaptor.addChild(root_1, stream_StringLiteral.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // SpecFile.g:75:7: '@FuncField' '(' '{' strings '}' ')' ( ';' )?
                    {
                    string_literal32=(Token)match(input,31,FOLLOW_31_in_funcfield292); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_31.add(string_literal32);

                    char_literal33=(Token)match(input,28,FOLLOW_28_in_funcfield294); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_28.add(char_literal33);

                    char_literal34=(Token)match(input,20,FOLLOW_20_in_funcfield296); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_20.add(char_literal34);

                    pushFollow(FOLLOW_strings_in_funcfield298);
                    strings35=strings();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_strings.add(strings35.getTree());
                    char_literal36=(Token)match(input,21,FOLLOW_21_in_funcfield300); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_21.add(char_literal36);

                    char_literal37=(Token)match(input,29,FOLLOW_29_in_funcfield302); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_29.add(char_literal37);

                    // SpecFile.g:75:44: ( ';' )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==30) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // SpecFile.g:0:0: ';'
                            {
                            char_literal38=(Token)match(input,30,FOLLOW_30_in_funcfield304); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_30.add(char_literal38);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: strings
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 75:50: -> ^( FUNCFIELD strings )
                    {
                        // SpecFile.g:75:53: ^( FUNCFIELD strings )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(FUNCFIELD, "FUNCFIELD"), root_1);

                        adaptor.addChild(root_1, stream_strings.nextTree());

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
            if ( state.backtracking>0 ) { memoize(input, 6, funcfield_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "funcfield"

    public static class invariant_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "invariant"
    // SpecFile.g:77:1: invariant : ( '@Invariant' '(' StringLiteral ')' ( ';' )? -> ^( INVARIANT StringLiteral ) | '@Invariant' '(' '{' strings '}' ')' ( ';' )? -> ^( INVARIANT strings ) );
    public final SpecFileParser.invariant_return invariant() throws RecognitionException {
        SpecFileParser.invariant_return retval = new SpecFileParser.invariant_return();
        retval.start = input.LT(1);
        int invariant_StartIndex = input.index();
        Node root_0 = null;

        Token string_literal39=null;
        Token char_literal40=null;
        Token StringLiteral41=null;
        Token char_literal42=null;
        Token char_literal43=null;
        Token string_literal44=null;
        Token char_literal45=null;
        Token char_literal46=null;
        Token char_literal48=null;
        Token char_literal49=null;
        Token char_literal50=null;
        SpecFileParser.strings_return strings47 = null;


        Node string_literal39_tree=null;
        Node char_literal40_tree=null;
        Node StringLiteral41_tree=null;
        Node char_literal42_tree=null;
        Node char_literal43_tree=null;
        Node string_literal44_tree=null;
        Node char_literal45_tree=null;
        Node char_literal46_tree=null;
        Node char_literal48_tree=null;
        Node char_literal49_tree=null;
        Node char_literal50_tree=null;
        RewriteRuleTokenStream stream_21=new RewriteRuleTokenStream(adaptor,"token 21");
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_20=new RewriteRuleTokenStream(adaptor,"token 20");
        RewriteRuleTokenStream stream_30=new RewriteRuleTokenStream(adaptor,"token 30");
        RewriteRuleTokenStream stream_32=new RewriteRuleTokenStream(adaptor,"token 32");
        RewriteRuleTokenStream stream_28=new RewriteRuleTokenStream(adaptor,"token 28");
        RewriteRuleTokenStream stream_29=new RewriteRuleTokenStream(adaptor,"token 29");
        RewriteRuleSubtreeStream stream_strings=new RewriteRuleSubtreeStream(adaptor,"rule strings");
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // SpecFile.g:78:5: ( '@Invariant' '(' StringLiteral ')' ( ';' )? -> ^( INVARIANT StringLiteral ) | '@Invariant' '(' '{' strings '}' ')' ( ';' )? -> ^( INVARIANT strings ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==32) ) {
                int LA14_1 = input.LA(2);

                if ( (LA14_1==28) ) {
                    int LA14_2 = input.LA(3);

                    if ( (LA14_2==StringLiteral) ) {
                        alt14=1;
                    }
                    else if ( (LA14_2==20) ) {
                        alt14=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 14, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // SpecFile.g:78:7: '@Invariant' '(' StringLiteral ')' ( ';' )?
                    {
                    string_literal39=(Token)match(input,32,FOLLOW_32_in_invariant326); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_32.add(string_literal39);

                    char_literal40=(Token)match(input,28,FOLLOW_28_in_invariant328); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_28.add(char_literal40);

                    StringLiteral41=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_invariant330); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_StringLiteral.add(StringLiteral41);

                    char_literal42=(Token)match(input,29,FOLLOW_29_in_invariant332); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_29.add(char_literal42);

                    // SpecFile.g:78:42: ( ';' )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==30) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // SpecFile.g:0:0: ';'
                            {
                            char_literal43=(Token)match(input,30,FOLLOW_30_in_invariant334); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_30.add(char_literal43);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: StringLiteral
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 78:50: -> ^( INVARIANT StringLiteral )
                    {
                        // SpecFile.g:78:53: ^( INVARIANT StringLiteral )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(INVARIANT, "INVARIANT"), root_1);

                        adaptor.addChild(root_1, stream_StringLiteral.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // SpecFile.g:79:7: '@Invariant' '(' '{' strings '}' ')' ( ';' )?
                    {
                    string_literal44=(Token)match(input,32,FOLLOW_32_in_invariant354); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_32.add(string_literal44);

                    char_literal45=(Token)match(input,28,FOLLOW_28_in_invariant356); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_28.add(char_literal45);

                    char_literal46=(Token)match(input,20,FOLLOW_20_in_invariant358); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_20.add(char_literal46);

                    pushFollow(FOLLOW_strings_in_invariant360);
                    strings47=strings();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_strings.add(strings47.getTree());
                    char_literal48=(Token)match(input,21,FOLLOW_21_in_invariant362); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_21.add(char_literal48);

                    char_literal49=(Token)match(input,29,FOLLOW_29_in_invariant364); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_29.add(char_literal49);

                    // SpecFile.g:79:44: ( ';' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==30) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // SpecFile.g:0:0: ';'
                            {
                            char_literal50=(Token)match(input,30,FOLLOW_30_in_invariant366); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_30.add(char_literal50);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: strings
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Node)adaptor.nil();
                    // 79:50: -> ^( INVARIANT strings )
                    {
                        // SpecFile.g:79:53: ^( INVARIANT strings )
                        {
                        Node root_1 = (Node)adaptor.nil();
                        root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(INVARIANT, "INVARIANT"), root_1);

                        adaptor.addChild(root_1, stream_strings.nextTree());

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
            if ( state.backtracking>0 ) { memoize(input, 7, invariant_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "invariant"

    public static class params_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "params"
    // SpecFile.g:81:1: params : Identifier ( ',' Identifier )* -> ^( PARAMS ( Identifier )* ) ;
    public final SpecFileParser.params_return params() throws RecognitionException {
        SpecFileParser.params_return retval = new SpecFileParser.params_return();
        retval.start = input.LT(1);
        int params_StartIndex = input.index();
        Node root_0 = null;

        Token Identifier51=null;
        Token char_literal52=null;
        Token Identifier53=null;

        Node Identifier51_tree=null;
        Node char_literal52_tree=null;
        Node Identifier53_tree=null;
        RewriteRuleTokenStream stream_33=new RewriteRuleTokenStream(adaptor,"token 33");
        RewriteRuleTokenStream stream_Identifier=new RewriteRuleTokenStream(adaptor,"token Identifier");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // SpecFile.g:82:5: ( Identifier ( ',' Identifier )* -> ^( PARAMS ( Identifier )* ) )
            // SpecFile.g:82:7: Identifier ( ',' Identifier )*
            {
            Identifier51=(Token)match(input,Identifier,FOLLOW_Identifier_in_params388); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_Identifier.add(Identifier51);

            // SpecFile.g:82:18: ( ',' Identifier )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==33) ) {
                    alt15=1;
                }


                switch (alt15) {
                case 1 :
                    // SpecFile.g:82:19: ',' Identifier
                    {
                    char_literal52=(Token)match(input,33,FOLLOW_33_in_params391); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_33.add(char_literal52);

                    Identifier53=(Token)match(input,Identifier,FOLLOW_Identifier_in_params393); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_Identifier.add(Identifier53);


                    }
                    break;

                default :
                    break loop15;
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
            // 82:50: -> ^( PARAMS ( Identifier )* )
            {
                // SpecFile.g:82:53: ^( PARAMS ( Identifier )* )
                {
                Node root_1 = (Node)adaptor.nil();
                root_1 = (Node)adaptor.becomeRoot((Node)adaptor.create(PARAMS, "PARAMS"), root_1);

                // SpecFile.g:82:62: ( Identifier )*
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
            if ( state.backtracking>0 ) { memoize(input, 8, params_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "params"

    public static class strings_return extends ParserRuleReturnScope {
        Node tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "strings"
    // SpecFile.g:84:1: strings : StringLiteral ( ',' StringLiteral )* -> ( StringLiteral )* ;
    public final SpecFileParser.strings_return strings() throws RecognitionException {
        SpecFileParser.strings_return retval = new SpecFileParser.strings_return();
        retval.start = input.LT(1);
        int strings_StartIndex = input.index();
        Node root_0 = null;

        Token StringLiteral54=null;
        Token char_literal55=null;
        Token StringLiteral56=null;

        Node StringLiteral54_tree=null;
        Node char_literal55_tree=null;
        Node StringLiteral56_tree=null;
        RewriteRuleTokenStream stream_StringLiteral=new RewriteRuleTokenStream(adaptor,"token StringLiteral");
        RewriteRuleTokenStream stream_33=new RewriteRuleTokenStream(adaptor,"token 33");

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // SpecFile.g:85:5: ( StringLiteral ( ',' StringLiteral )* -> ( StringLiteral )* )
            // SpecFile.g:85:7: StringLiteral ( ',' StringLiteral )*
            {
            StringLiteral54=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_strings430); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_StringLiteral.add(StringLiteral54);

            // SpecFile.g:85:21: ( ',' StringLiteral )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==33) ) {
                    alt16=1;
                }


                switch (alt16) {
                case 1 :
                    // SpecFile.g:85:22: ',' StringLiteral
                    {
                    char_literal55=(Token)match(input,33,FOLLOW_33_in_strings433); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_33.add(char_literal55);

                    StringLiteral56=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_strings435); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_StringLiteral.add(StringLiteral56);


                    }
                    break;

                default :
                    break loop16;
                }
            } while (true);



            // AST REWRITE
            // elements: StringLiteral
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Node)adaptor.nil();
            // 85:50: -> ( StringLiteral )*
            {
                // SpecFile.g:85:53: ( StringLiteral )*
                while ( stream_StringLiteral.hasNext() ) {
                    adaptor.addChild(root_0, stream_StringLiteral.nextNode());

                }
                stream_StringLiteral.reset();

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
            if ( state.backtracking>0 ) { memoize(input, 9, strings_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "strings"

    // Delegated rules


 

    public static final BitSet FOLLOW_type_in_specfile105 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_Identifier_in_specfile107 = new BitSet(new long[]{0x0000000000140000L});
    public static final BitSet FOLLOW_18_in_specfile110 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_params_in_specfile112 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_specfile114 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_specfile118 = new BitSet(new long[]{0x0000000188200000L});
    public static final BitSet FOLLOW_specfield_in_specfile120 = new BitSet(new long[]{0x0000000188200000L});
    public static final BitSet FOLLOW_funcfield_in_specfile123 = new BitSet(new long[]{0x0000000180200000L});
    public static final BitSet FOLLOW_invariant_in_specfile126 = new BitSet(new long[]{0x0000000100200000L});
    public static final BitSet FOLLOW_21_in_specfile129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mod_in_type162 = new BitSet(new long[]{0x0000000006000000L});
    public static final BitSet FOLLOW_cls_in_type165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_mod0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_cls0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_specfield202 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_specfield204 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_StringLiteral_in_specfield206 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_specfield208 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_30_in_specfield210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_specfield230 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_specfield232 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_specfield234 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_strings_in_specfield236 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_specfield238 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_specfield240 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_30_in_specfield242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_funcfield264 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_funcfield266 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_StringLiteral_in_funcfield268 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_funcfield270 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_30_in_funcfield272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_funcfield292 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_funcfield294 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_funcfield296 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_strings_in_funcfield298 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_funcfield300 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_funcfield302 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_30_in_funcfield304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_invariant326 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_invariant328 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_StringLiteral_in_invariant330 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_invariant332 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_30_in_invariant334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_invariant354 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_invariant356 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_invariant358 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_strings_in_invariant360 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_invariant362 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_invariant364 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_30_in_invariant366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_params388 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_33_in_params391 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_Identifier_in_params393 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_StringLiteral_in_strings430 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_33_in_strings433 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_StringLiteral_in_strings435 = new BitSet(new long[]{0x0000000200000002L});

}