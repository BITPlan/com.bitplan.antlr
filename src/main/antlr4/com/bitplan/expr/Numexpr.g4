/**
 * see https://github.com/antlr/antlr4/issues/994
 
 */
grammar Numexpr;

numexpr: if_statement EOF;

if_statement:
  if_part ( else_part ) ? 'endif';
 
if_part:
  'if' expr statement_list|
  'if' expr;

else_part:
  'else' statement_list |
  'else';

statement_list:
  statement +;  
  
statement: if_statement; 

expr: ID
    | VALUE
    | 'not' expr
    | expr '=' expr
    | expr 'and' expr
    | expr 'or' expr
    | expr 'between' expr 'and' expr
    ;

VALUE: [0-9]+;
ID: [a-zA-Z_][a-zA-Z_0-9]*;
WS: [ \t\n\r\f]+ -> skip;
ERROR: .;