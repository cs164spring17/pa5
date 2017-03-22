grammar Expr;

@header {
package atrai.antlr;
}


prog:   stat+ ;

stat:   expr nl
    |   iden '=' expr nl
    |   nl
    ;

expr:   multExpr
        (   '+' multExpr
        |   '-' multExpr
        )*
    ;

multExpr:   atom ('*' atom )*
    ;

atom:   num
    |   iden
    |   '(' expr ')'
    ;

num: INT;

iden: ID;

nl: NEWLINE;

ID  :   ('a'..'z'|'A'..'Z')+ ;
INT :   '0'..'9'+ ;
NEWLINE:'\r'? '\n' ;
WS  :   (' '|'\t')+ {skip();} ;
