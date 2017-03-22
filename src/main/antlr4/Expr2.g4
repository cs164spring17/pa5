grammar Expr2;

@header {
package atrai.antlr;
import java.util.HashMap;
}


prog:   stat+ ;

stat:   expr nl
    |   iden '=' expr nl
    |   nl
    ;

expr:   expr '*' expr
    |   expr ('+'|'-') expr
    |   atom
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
