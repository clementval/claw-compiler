/*
 * This file is released under terms of BSD license
 * See LICENSE file for more information
 * @author Mikhail Zhigun
*/
/**
 * ANTLR 4 Grammar file for detecting CLAW directives.
 */
grammar FortranCLAWScanner;

root : (claw_directive_line | claw_guard_line)* EOF;

claw_directive_line : CLAW_DIRECTIVE_LINE;
claw_guard_line : CLAW_GUARD_LINE;

CLAW_DIRECTIVE_LINE : SEP? '!' SEP? '$' CLAW (SEP NOT_EOL_CHR*)?;
CLAW_GUARD_LINE :     SEP? '!' SEP? '$' (ACC | OMP) SEP CLAW_GUARD SEP?;
OTHER_LINE : NOT_EOL_CHR+ ->skip;

EOL : ('\r')? '\n' ->skip;
fragment NOT_EOL_CHR:  ~[\r\n];

fragment ACC : A C C;
fragment CLAW : C L A W;
fragment CLAW_GUARD : C L A W '-' G U A R D;
fragment OMP : O M P;
fragment SEP : WS+;
fragment WS : [ \t];

fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment G : [gG];
fragment L : [lL];
fragment M : [mM];
fragment O : [oO];
fragment P : [pP];
fragment R : [rR];
fragment U : [uU];
fragment W : [wW];
