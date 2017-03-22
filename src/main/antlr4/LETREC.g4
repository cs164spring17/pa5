grammar LETREC;

@header {
    package atrai.antlr;
}

prog: expr;

expr: num
    | iden
    | '(' expr ')'
    | '(' expr expr ')'
    | expr ('*' | '/') expr
    | expr ('+' | '-') expr
    | expr ('=='|'!='|'>'|'<'| '>=' | '<=') expr
    | 'let' iden '=' expr 'in' expr
    | 'if' expr 'then' expr 'else' expr
    | 'letrec' iden '(' iden ')' '=' expr 'in' expr
    ;

num: INT;

iden: ID;

ID  :   ('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;
INT :   '0'..'9'+ ;
WS  :   (' '|'\t' | '\r' | '\n')+ -> skip ;
