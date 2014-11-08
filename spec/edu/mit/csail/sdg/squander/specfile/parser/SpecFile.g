grammar SpecFile;

options {
    backtrack = true;
    memoize = true;
    output=AST;
    ASTLabelType=Node;
}
    
tokens {
  SPECFILE;
  SPECFIELD;
  FUNCFIELD; 
  INVARIANT;
  PARAMS;
}
    
@lexer::header { 
package squander.specfile.parser;
}

@parser::header {
package squander.specfile.parser;
}

@members {
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
}    

//----------------------------------
// PARSER
//----------------------------------

// Top-level expression
specfile: type Identifier ('<' params '>')? '{' specfield* funcfield* invariant* '}'
    -> ^(SPECFILE type Identifier params? specfield* funcfield* invariant*);

type: mod? cls;
mod: 'private' | 'public' | 'protected';
cls: 'class' | 'interface';

specfield 
    : '@SpecField' '(' StringLiteral ')' ';'?    -> ^(SPECFIELD StringLiteral)
    | '@SpecField' '(' '{' strings '}' ')' ';'?  -> ^(SPECFIELD strings);

funcfield
    : '@FuncField' '(' StringLiteral ')' ';'?    -> ^(FUNCFIELD StringLiteral)
    | '@FuncField' '(' '{' strings '}' ')' ';'?  -> ^(FUNCFIELD strings);

invariant
    : '@Invariant' '(' StringLiteral ')' ';'?    -> ^(INVARIANT StringLiteral)
    | '@Invariant' '(' '{' strings '}' ')' ';'?  -> ^(INVARIANT strings);

params
    : Identifier (',' Identifier)*               -> ^(PARAMS Identifier*);

strings
    : StringLiteral (',' StringLiteral)*         -> StringLiteral*;

//----------------------------------
// LEXER 
//----------------------------------

Identifier 
    :   Letter (Letter|JavaIDDigit)* '[]'?
    ;

StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
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

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;


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
