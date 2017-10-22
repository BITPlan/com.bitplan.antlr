// based on https://github.com/antlr/grammars-v4/blob/master/iri/IRI.g4
// but split into three parts
parser grammar IRIParser;

options {
    // use separate Lexer
 	tokenVocab = IRITokenLexer;
 }
 
/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2014 by Bart Kiers
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * Project      : An IRI ANTLR 4 grammar and parser.
 * Developed by : Bart Kiers, bart@big-o.nl
 * Also see     : https://github.com/bkiers/iri-parser
 */
//
// An IRI grammar based on: http://www.ietf.org/rfc/rfc3987.txt
//
// The inline comments starting with "///" in this grammar are direct 
// copy-pastes from the ABNF syntax from the reference linked above.
//

/*
 * Parser rules
 */
parse
   : iri NEWLINE* EOF
   ;

/// IRI            = scheme ":" ihier-part [ "?" iquery ] [ "#" ifragment ]
iri
   : scheme ':' ihier_part ('?' iquery)? ('#' ifragment)?
   ;

/// ihier-part     = "//" iauthority ipath-abempty///                / ipath-absolute///                / ipath-rootless///                / ipath-empty
ihier_part
   : '//' iauthority ipath_abempty
   | ipath_absolute
   | ipath_rootless
   | ipath_empty
   ;

/// IRI-reference  = IRI / irelative-ref
iri_reference
   : iri
   | irelative_ref
   ;

/// absolute-IRI   = scheme ":" ihier-part [ "?" iquery ]
absolute_iri
   : scheme ':' ihier_part ('?' iquery)?
   ;

/// irelative-ref  = irelative-part [ "?" iquery ] [ "#" ifragment ]
irelative_ref
   : irelative_part ('?' iquery)? ('#' ifragment)?
   ;

/// irelative-part = "//" iauthority ipath-abempty///                     / ipath-absolute///                     / ipath-noscheme///                     / ipath-empty
irelative_part
   : '//' iauthority ipath_abempty
   | ipath_absolute
   | ipath_noscheme
   | ipath_empty
   ;

/// iauthority     = [ iuserinfo "@" ] ihost [ ":" port ]
iauthority
   : (iuserinfo '@')? ihost (':' port)?
   ;

/// iuserinfo      = *( iunreserved / pct-encoded / sub-delims / ":" )
iuserinfo
   : (iunreserved | pct_encoded | sub_delims | ':')*
   ;

/// ihost          = IP-literal / IPv4address / ireg-name
ihost
   : ip_literal
   | ip_v4_address
   | ireg_name
   ;

/// ireg-name      = *( iunreserved / pct-encoded / sub-delims )
ireg_name
   : (iunreserved | pct_encoded | sub_delims)*
   ;

/// ipath          = ipath-abempty   ; begins with "/" or is empty///                / ipath-absolute  ; begins with "/" but not "//"///                / ipath-noscheme  ; begins with a non-colon segment///                / ipath-rootless  ; begins with a segment///                / ipath-empty     ; zero characters
ipath
   : ipath_abempty
   | ipath_absolute
   | ipath_noscheme
   | ipath_rootless
   | ipath_empty
   ;

/// ipath-abempty  = *( "/" isegment )
ipath_abempty
   : ('/' isegment)*
   ;

/// ipath-absolute = "/" [ isegment-nz *( "/" isegment ) ]
ipath_absolute
   : '/' (isegment_nz ('/' isegment)*)?
   ;

/// ipath-noscheme = isegment-nz-nc *( "/" isegment )
ipath_noscheme
   : isegment_nz_nc ('/' isegment)*
   ;

/// ipath-rootless = isegment-nz *( "/" isegment )
ipath_rootless
   : isegment_nz ('/' isegment)*
   ;

/// ipath-empty    = 0<ipchar>
ipath_empty
   :/* nothing */
   ;

/// isegment       = *ipchar
isegment
   : ipchar*
   ;

/// isegment-nz    = 1*ipchar
isegment_nz
   : ipchar +
   ;

/// isegment-nz-nc = 1*( iunreserved / pct-encoded / sub-delims / "@" )///                ; non-zero-length segment without any colon ":"
isegment_nz_nc
   : (iunreserved | pct_encoded | sub_delims | '@') +
   ;

/// ipchar         = iunreserved / pct-encoded / sub-delims / ":" / "@"
ipchar
   : iunreserved
   | pct_encoded
   | sub_delims
   | (':' | '@')
   ;

/// iquery         = *( ipchar / iprivate / "/" / "?" )
iquery
   : (ipchar | (IPRIVATE | '/' | '?'))*
   ;

/// ifragment      = *( ipchar / "/" / "?" )
ifragment
   : (ipchar | ('/' | '?'))*
   ;

/// iunreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~" / ucschar
iunreserved
   : ALPHA
   | DIGIT
   | ('-' | '.' | '_' | '~' | UCSCHAR)
   ;

/// scheme         = ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
scheme
   : ALPHA (ALPHA | DIGIT | ('+' | '-' | '.'))*
   ;

/// port           = *DIGIT
port
   : DIGIT*
   ;

/// IP-literal     = "[" ( IPv6address / IPvFuture  ) "]"
ip_literal
   : '[' (ip_v6_address | ip_v_future) ']'
   ;

/// IPvFuture      = "v" 1*HEXDIG "." 1*( unreserved / sub-delims / ":" )
ip_v_future
   : V HEXDIG + '.' (unreserved | sub_delims | ':') +
   ;

/// IPv6address    =                            6( h16 ":" ) ls32///                /                       "::" 5( h16 ":" ) ls32///                / [               h16 ] "::" 4( h16 ":" ) ls32///                / [ *1( h16 ":" ) h16 ] "::" 3( h16 ":" ) ls32///                / [ *2( h16 ":" ) h16 ] "::" 2( h16 ":" ) ls32///                / [ *3( h16 ":" ) h16 ] "::"    h16 ":"   ls32///                / [ *4( h16 ":" ) h16 ] "::"              ls32///                / [ *5( h16 ":" ) h16 ] "::"              h16///                / [ *6( h16 ":" ) h16 ] "::"
ip_v6_address
   : h16 ':' h16 ':' h16 ':' h16 ':' h16 ':' h16 ':' ls32
   | '::' h16 ':' h16 ':' h16 ':' h16 ':' h16 ':' ls32
   | h16? '::' h16 ':' h16 ':' h16 ':' h16 ':' ls32
   | ((h16 ':')? h16)? '::' h16 ':' h16 ':' h16 ':' ls32
   | (((h16 ':')? h16 ':')? h16)? '::' h16 ':' h16 ':' ls32
   | ((((h16 ':')? h16 ':')? h16 ':')? h16)? '::' h16 ':' ls32
   | (((((h16 ':')? h16 ':')? h16 ':')? h16 ':')? h16)? '::' ls32
   | ((((((h16 ':')? h16 ':')? h16 ':')? h16 ':')? h16 ':')? h16)? '::' h16
   | (((((((h16 ':')? h16 ':')? h16 ':')? h16 ':')? h16 ':')? h16 ':')? h16)? '::'
   ;

/// h16            = 1*4HEXDIG
h16
   : HEXDIG HEXDIG HEXDIG HEXDIG
   | HEXDIG HEXDIG HEXDIG
   | HEXDIG HEXDIG
   | HEXDIG
   ;

/// ls32           = ( h16 ":" h16 ) / IPv4address
ls32
   : h16 ':' h16
   | ip_v4_address
   ;

/// IPv4address    = dec-octet "." dec-octet "." dec-octet "." dec-octet
ip_v4_address
   : dec_octet '.' dec_octet '.' dec_octet '.' dec_octet
   ;

/// dec-octet      = DIGIT                 ; 0-9///                / %x31-39 DIGIT         ; 10-99///                / "1" 2DIGIT            ; 100-199///                / "2" %x30-34 DIGIT     ; 200-249///                / "25" %x30-35          ; 250-255
dec_octet
   : DIGIT
   | NON_ZERO_DIGIT DIGIT
   | D1 DIGIT DIGIT
   | D2 (D0 | D1 | D2 | D3 | D4) DIGIT
   | D2 D5 (D0 | D1 | D2 | D3 | D4 | D5)
   ;

/// pct-encoded    = "%" HEXDIG HEXDIG
pct_encoded
   : '%' HEXDIG HEXDIG
   ;

/// unreserved     = ALPHA / DIGIT / "-" / "." / "_" / "~"
unreserved
   : ALPHA
   | DIGIT
   | ('-' | '.' | '_' | '~')
   ;

/// reserved       = gen-delims / sub-delims
reserved
   : gen_delims
   | sub_delims
   ;

/// gen-delims     = ":" / "/" / "?" / "#" / "[" / "]" / "@"
gen_delims
   : ':'
   | '/'
   | '?'
   | '#'
   | '['
   | ']'
   | '@'
   ;

/// sub-delims     = "!" / "$" / "&" / "'" / "(" / ")"///                / "*" / "+" / "," / ";" / "="
sub_delims
   : '!'
   | '$'
   | '&'
   | '\''
   | '('
   | ')'
   | '*'
   | '+'
   | ','
   | ';'
   | '='
   ;



 