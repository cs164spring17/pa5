grammar FUN;

@header {
    package atrai.antlr;
}

prog: expr;

expr:  expr '('  ')'
    | expr '(' expr ')'
    | expr ('*' | '/') expr
    | expr ('+' | '-') expr
    | expr ('=='|'!='|'>'|'<'| '>=' | '<=') expr
    | iden '=' expr
    | 'let' decllist 'in' expr
    | 'if' expr 'then' expr 'else' expr
    | 'while' expr 'do' expr
    | 'fun' '(' iden ')' '=' expr
    | 'fun' '(' ')' '=' expr
    | 'print' expr
    | 'null'
    | num
    | iden
    | string
    | '{' exprseq '}'
    | '(' expr ')'
    ;


decllist: decl (',' decl)*;

decl: iden '=' expr;

exprseq: expr (';' expr)*;

num: INT;
iden: ID;
string: STRING;

ID  :   ('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;
INT :   '0'..'9'+ ;
WS  :   (' '|'\t' | '\r' | '\n')+ -> skip ;
STRING : '"' (~'"')* '"';
