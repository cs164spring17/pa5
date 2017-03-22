grammar Faust;
@header {
    package atrai.antlr;
}

// derived from http://faust.grame.fr/images/faust-quick-reference.pdf
// there might be deviations and/or omissions

// going to translate from https://github.com/grame-cncm/faust/blob/master-dev/compiler/parser/faustparser.y

prgm: stmt*;

stmt : declaration
     | import_stmt
     | definition
     ;

declaration: 'declare' iden string ';';
import_stmt: 'import' '(' string ')' ';';
definition : iden '=' expr ';'
           | iden '(' patternlist ')' '=' expr ';' // doubles for plain functions without expressions here
           ;

patternlist: pattern (',' pattern)*;
pattern: iden | expr;

expr: expr '~' expr
    |<assoc=right> expr ',' expr
    |<assoc=right> expr ':' expr
    |<assoc=right> expr '<:' expr
    |<assoc=right> expr ':>' expr
    |<assoc=right> expr '^' expr
    | diagiteration
    | insouts
    | expr ('*' | '/') expr
    | expr ('+' | '-') expr
    | expr ('|' | '&' | 'xor' | '<<' | '>>') expr
    | expr ('=='|'!='|'>'|'<'| '>=' | '<=') expr
    | expr '@' expr
    | expr '\''
//    | time
//    | lexical
//    | foreign
//    | lambda
    ;

diagiteration : ('par' | 'seq' | 'num' | 'prod') '(' iden ',' expr ',' expr ')' ;

insouts : 'inputs' '(' expr ')'
        | 'outputs' '(' expr ')'
        ;

num: INT;
iden: ID;
string: STRING;

ID  :   ('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;
INT :   '0'..'9'+ ;
STRING : '"' ((~'"')| '\\' .)* '"';
WS  :   (' '|'\t' | '\r' | '\n')+ -> skip ;
LINE_COMMENT : '//' ~[\r\n]* -> skip ;
