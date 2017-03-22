grammar Ex; 				// generates class ExParser
@header {
    package atrai.antlr;
}

stat: expr '=' expr ';'
	| expr ';'
	;
expr: expr '*' expr 			// production 1
	| expr '+' expr 			// production 2
	|<assoc=right> expr '^' expr 	// production 3
	| expr '(' expr ')' 		// f(x)
	| id
	| num
	;
id : ID;
num: INT;
INT: [0-9]+;
ID : [A-Za-z]+ ;
WS : [ \t\r\n]+ -> skip ; 		// ignore whitespace

