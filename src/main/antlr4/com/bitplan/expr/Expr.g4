/**
 * see https://github.com/antlr/antlr4/issues/994
 * 
 */
grammar Expr;

expr: ID
    | 'not' expr
    | expr 'and' expr
    | expr 'or' expr
    | expr 'between' expr 'and' expr
    ;

ID: [a-zA-Z_][a-zA-Z_0-9]*;
WS: [ \t\n\r\f]+ -> skip;
ERROR: .;