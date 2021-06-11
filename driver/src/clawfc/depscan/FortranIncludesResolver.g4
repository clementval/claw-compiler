/*
 * This file is released under terms of BSD license
 * See LICENSE file for more information
 * @author Mikhail Zhigun
*/
/**
 * ANTLR 4 Grammar file for detecting Fortran include statements.
 */
grammar FortranIncludesResolver;

root : (include_line)* EOF;

include_line : INCLUDE_STATEMENT_LINE;

INCLUDE_STATEMENT_LINE : SEP? INCLUDE SEP INCLUDE_STRING SEP? EOL;
OTHER_LINE : (~'\n')* EOL ->skip;

fragment INCLUDE_STRING : (DQ (~'"' | QUOTED_DQ)* DQ)
                | (SQ (~'\'' | QUOTED_SQ)* SQ);

fragment SEP : WS+;
fragment WS : [ \t];
fragment EOL : ('\r')? '\n';
fragment QUOTED_DQ : DQ DQ;
fragment QUOTED_SQ : SQ SQ;
fragment DQ : '"';
fragment SQ : '\'';
fragment INCLUDE : I N C L U D E;

fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment I : [iI];
fragment L : [lL];
fragment N : [nN];
fragment U : [uU];
