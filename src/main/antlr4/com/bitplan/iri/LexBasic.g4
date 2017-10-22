lexer grammar LexBasic;

/*
 * Lexer rules
 *//// ucschar        = %xA0-D7FF / %xF900-FDCF / %xFDF0-FFEF///                / %x10000-1FFFD / %x20000-2FFFD / %x30000-3FFFD///                / %x40000-4FFFD / %x50000-5FFFD / %x60000-6FFFD///                / %x70000-7FFFD / %x80000-8FFFD / %x90000-9FFFD///                / %xA0000-AFFFD / %xB0000-BFFFD / %xC0000-CFFFD///                / %xD0000-DFFFD / %xE1000-EFFFD

UCSCHAR
   : '\u00A0' .. '\uD7FF'
   | '\uF900' .. '\uFDCF'
   | '\uFDF0' .. '\uFFEF'
   ;

/// iprivate       = %xE000-F8FF / %xF0000-FFFFD / %x100000-10FFFD

IPRIVATE
   : '\uE000' .. '\uF8FF'
   ;


D0
   : '0'
   ;


D1
   : '1'
   ;


D2
   : '2'
   ;


D3
   : '3'
   ;


D4
   : '4'
   ;


D5
   : '5'
   ;


D6
   : '6'
   ;


D7
   : '7'
   ;


D8
   : '8'
   ;


D9
   : '9'
   ;


A
   : [aA]
   ;


B
   : [bB]
   ;


C
   : [cC]
   ;


D
   : [dD]
   ;


E
   : [eE]
   ;


F
   : [fF]
   ;


G
   : [gG]
   ;


H
   : [hH]
   ;


I
   : [iI]
   ;


J
   : [jJ]
   ;


K
   : [kK]
   ;


L
   : [lL]
   ;


M
   : [mM]
   ;


N
   : [nN]
   ;


O
   : [oO]
   ;


P
   : [pP]
   ;


Q
   : [qQ]
   ;


R
   : [rR]
   ;


S
   : [sS]
   ;


T
   : [tT]
   ;


U
   : [uU]
   ;


V
   : [vV]
   ;


W
   : [wW]
   ;


X
   : [xX]
   ;


Y
   : [yY]
   ;


Z
   : [zZ]
   ;


COL2
   : '::'
   ;


COL
   : ':'
   ;


DOT
   : '.'
   ;


PERCENT
   : '%'
   ;


HYPHEN
   : '-'
   ;


TILDE
   : '~'
   ;


USCORE
   : '_'
   ;


EXCL
   : '!'
   ;


DOLLAR
   : '$'
   ;


AMP
   : '&'
   ;


SQUOTE
   : '\''
   ;


OPAREN
   : '('
   ;


CPAREN
   : ')'
   ;


STAR
   : '*'
   ;


PLUS
   : '+'
   ;


COMMA
   : ','
   ;


SCOL
   : ';'
   ;


EQUALS
   : '='
   ;


FSLASH2
   : '//'
   ;


FSLASH
   : '/'
   ;


QMARK
   : '?'
   ;


HASH
   : '#'
   ;


OBRACK
   : '['
   ;


CBRACK
   : ']'
   ;


AT
   : '@'
   ;
