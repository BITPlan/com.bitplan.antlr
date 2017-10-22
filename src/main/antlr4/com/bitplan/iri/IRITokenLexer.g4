lexer grammar IRITokenLexer;

import LexBasic;

ALPHA
   : A
   | B
   | C
   | D
   | E
   | F
   | G
   | H
   | I
   | J
   | K
   | L
   | M
   | N
   | O
   | P
   | Q
   | R
   | S
   | T
   | U
   | V
   | W
   | X
   | Y
   | Z
   ;

HEXDIG
   : DIGIT
   | (A | B | C | D | E | F)
   ;

DIGIT
   : D0
   | NON_ZERO_DIGIT
   ;

NON_ZERO_DIGIT
   : D1
   | D2
   | D3
   | D4
   | D5
   | D6
   | D7
   | D8
   | D9
   ;