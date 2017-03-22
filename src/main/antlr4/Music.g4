grammar Music;
@header {
    package atrai.antlr;
}

prgm: stmt*;

stmt: iden '=' expr ';'
    ;

expr : expr ':+:' expr
     | expr ':=:' expr
     | note
     | iden
     ;

note : pitch dur;
dur : DUR;
pitch : PITCH num;

num: INT;
iden: ID;
string: STRING;

DUR : ('wn' | 'hn' | 'qn' | 'en' | 'sn' | 'tn') ;

PITCH : ('a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g'
        | 'as' | 'bs' | 'cs' | 'ds' | 'es' | 'fs' | 'gs'
        | 'af' | 'bf' | 'cf' | 'df' | 'ef' | 'ff' | 'gf');

ID  :   ('A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;
INT :   '0'..'9'+ ;
STRING : '"' ((~'"')| '\\' .)* '"';
WS  :   (' '|'\t' | '\r' | '\n')+ -> skip ;
LINE_COMMENT : '//' ~[\r\n]* -> skip ;
