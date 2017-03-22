grammar CLASSES;

@header {
    package atrai.antlr;
}

prog: cls*;

cls: 'class' type 'extends' type '{' field* method* '}';

field: iden ':' type ';';

method: iden '(' paramlist ')' ':' type '=' expr ';';

expr: 'new' type
    | expr '.' iden '('  arglist ')'
    | expr ('*' | '/') expr
    | expr ('+' | '-') expr
    | expr ('=='|'!='|'>'|'<'| '>=' | '<=') expr
    | iden '=' expr
    | 'let' iden ':' type '=' expr 'in' expr
    | 'if' expr 'then' expr 'else' expr
    | 'if' expr 'is' iden ':' type 'then' expr 'else' expr
    | 'while' expr 'do' expr
    | 'print' expr
    | 'isnull' expr
    | 'self'
    | num
    | iden
    | string
    | '{' exprseq '}'
    | '(' expr ')'
    ;


paramlist: param (',' param)*
    | ;
param: iden ':' type;

arglist: expr (',' expr)*
    | ;

exprseq: expr (';' expr)*;

type: ID ;
num: INT;
iden: ID;
string: STRING;

ID  :   ('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;
INT :   '0'..'9'+ ;
WS  :   (' '|'\t' | '\r' | '\n')+ -> skip ;
STRING : '"' (~'"')* '"';
