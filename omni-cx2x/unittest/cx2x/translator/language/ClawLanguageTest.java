/*
 * This file is released under terms of BSD license
 * See LICENSE file for more information
 */

package cx2x.translator.language;

import static org.junit.Assert.*;

import cx2x.xcodeml.exception.IllegalDirectiveException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * JUnit test class for the CLAW language parser and information holding class.
 *
 * @author clementval
 */
public class ClawLanguageTest {

  /**
   * Test various input for the CLAW loop fusion directive.
   */
  @Test
  public void FusionTest(){
    // Valid directives
    analyzeValidClawLoopFusion("claw loop-fusion", null);
    analyzeValidClawLoopFusion("claw loop-fusion group(g1)", "g1");
    analyzeValidClawLoopFusion("claw loop-fusion group( g1 )", "g1");
    analyzeValidClawLoopFusion("claw loop-fusion group ( g1   ) ", "g1");
    analyzeValidClawLoopFusion("claw loop-fusiongroup(g1)", "g1");

    // Unvalid directives
    analyzeUnvalidClawLanguage("claw loop-fusion group");
    analyzeUnvalidClawLanguage("claw loop-fusion (i,j,k)");
    analyzeUnvalidClawLanguage("claw loop-fusion group()");
    analyzeUnvalidClawLanguage("claw loop-fusion group(   )");
  }

  /**
   * Assert the result for valid loop fusion CLAW directive
   * @param raw       Raw string valud of the CLAW directive to be analyzed.
   * @param groupName Group name to be found if any.
   */
  private void analyzeValidClawLoopFusion(String raw, String groupName){
    try {
      ClawLanguage l = ClawLanguage.analyze(raw);
      assertEquals(ClawDirective.LOOP_FUSION, l.getDirective());
      if(groupName != null){
        assertTrue(l.hasGroupOption());
        assertEquals(groupName, l.getGroupName());
      } else {
        assertFalse(l.hasGroupOption());
        assertNull(l.getGroupName());
      }
    } catch(IllegalDirectiveException idex){
      fail();
    }
  }

  /**
   * Assert any unvalid claw raw input
   * @param raw Raw string valud of the CLAW directive to be analyzed.
   */
  private void analyzeUnvalidClawLanguage(String raw){
    ClawLanguage l = null;
    try {
      l = ClawLanguage.analyze(raw);
      fail();
    } catch (IllegalDirectiveException pex){
      assertNull(l);
      assertNotNull(pex);
      assertNotNull(pex.getMessage());
    }
  }

  /**
   * Test various input for the CLAW loop interchange directive.
   */
  @Test
  public void InterchangeTest(){
    // Valid directives
    analyzeValidClawLoopInterchange("claw loop-interchange", null);
    analyzeValidClawLoopInterchange("claw loop-interchange (i,j,k)",
        Arrays.asList("i", "j", "k"));
    analyzeValidClawLoopInterchange("claw loop-interchange (  i,j,k  ) ",
        Arrays.asList("i", "j", "k"));

    // Unvalid directives
    analyzeUnvalidClawLanguage("claw loop-interchange ()");
    analyzeUnvalidClawLanguage("claw loop-interchange (i,j,k) group");
  }

  /**
   * Assert the result for valid loop interchange CLAW directive
   * @param raw       Raw string valud of the CLAW directive to be analyzed.
   * @param indexes   List of indexes to be found if any.
   */
  private void analyzeValidClawLoopInterchange(String raw,
                                               List<String> indexes)
  {
    try {
      ClawLanguage l = ClawLanguage.analyze(raw);
      assertEquals(ClawDirective.LOOP_INTERCHANGE, l.getDirective());
      if(indexes != null){
        assertTrue(l.hasIndexes());
        assertEquals(indexes.size(), l.getIndexes().size());
      } else {
        assertFalse(l.hasIndexes());
        assertNull(l.getIndexes());
      }
    } catch(IllegalDirectiveException idex){
      fail();
    }
  }

  /**
   * Test various input for the CLAW remove directive.
   */
  @Test
  public void RemoveTest(){
    // Valid directives
    analyzeValidSimpleClaw("claw remove", ClawDirective.REMOVE);
    analyzeValidSimpleClaw("claw end remove", ClawDirective.END_REMOVE);
    analyzeValidSimpleClaw("claw   end   remove  ", ClawDirective.END_REMOVE);

    // Unvalid directives
    analyzeUnvalidClawLanguage("claw");
    analyzeUnvalidClawLanguage("claw dummy");
    analyzeUnvalidClawLanguage("claw end re move");
  }

  /**
   * Assert the result for valid lo CLAW directive
   * @param raw       Raw string valud of the CLAW directive to be analyzed.
   * @param directive Directive to be match.
   */
  private void analyzeValidSimpleClaw(String raw, ClawDirective directive) {
    try {
      ClawLanguage l = ClawLanguage.analyze(raw);
      assertEquals(directive, l.getDirective());
    } catch(IllegalDirectiveException idex){
      fail();
    }
  }


  /**
   * Test various input for the CLAW loop extract directive.
   */
  @Test
  public void ExtractTest(){
    // Valid directives
    analyzeValidClawLoopExtract("claw loop-extract range(i=istart,iend)", "i",
        "istart", "iend", null);
    analyzeValidClawLoopExtract("claw loop-extract range(i=istart,iend,2)", "i",
        "istart", "iend", "2");
    analyzeValidClawLoopExtract("claw loop-extract range(i=1,10)", "i", "1",
        "10", null);
    analyzeValidClawLoopExtract("claw loop-extract range(i=1,10,2)",  "i", "1",
        "10", "2");

    // Unvalid directives
    analyzeUnvalidClawLanguage("claw loop-extract");
    analyzeUnvalidClawLanguage("claw loop   -   extract ");
  }

  /**
   * Assert the result for valid loop extract CLAW directive
   * @param raw       Raw string valud of the CLAW directive to be analyzed.
   * @param induction Induction var to be found.
   * @param lower     Lower bound value to be found.
   * @param upper     Upper bound value to be found.
   * @param step      Step valu to be found if any.
   *
   */
  private void analyzeValidClawLoopExtract(String raw, String induction,
                                           String lower, String upper,
                                           String step)
  {
    try {
      ClawLanguage l = ClawLanguage.analyze(raw);
      assertEquals(ClawDirective.LOOP_EXTRACT, l.getDirective());
      assertEquals(induction, l.getRange().getInductionVar());
      assertEquals(lower, l.getRange().getLowerBound());
      assertEquals(upper, l.getRange().getUpperBound());
      if(step != null){
        assertEquals(step, l.getRange().getStep());
      }
    } catch(IllegalDirectiveException idex){
      System.err.println(idex.getMessage());
      fail();
    }
  }

}