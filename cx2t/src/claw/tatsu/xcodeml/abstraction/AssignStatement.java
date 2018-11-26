/*
 * This file is released under terms of BSD license
 * See LICENSE file for more information
 */
package claw.tatsu.xcodeml.abstraction;

import claw.tatsu.xcodeml.xnode.common.Xcode;
import claw.tatsu.xcodeml.xnode.common.Xnode;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstraction of assignment statement to be able to categorize them and
 * easily extract some information.
 *
 * @author clementval
 */
public class AssignStatement extends Xnode {

  /**
   * Constructs an Xnode object from an element in the AST.
   *
   * @param element Base element for the Xnode object.
   */
  public AssignStatement(Element element) {
    super(element);
  }

  /**
   * Get the variable name on the left hand-side of the assignment.
   *
   * @return Left hand-side variable name.
   */
  public String getLhsName() {
    Xnode lhs = getLhs();
    if(lhs != null) {
      return (lhs.opcode() == Xcode.VAR) ? lhs.value() :
          lhs.matchSeq(Xcode.VAR_REF, Xcode.VAR).value();
    }
    return "";
  }

  /**
   * Get the left hand-side node of the assignment.
   *
   * @return Left hans-side node.
   */
  public Xnode getLhs() {
    return child(Xnode.LHS);
  }

  /**
   * Get the right hand-side node of the assignment.
   *
   * @return Right hans-side node.
   */
  public Xnode getRhs() {
    return child(Xnode.RHS);
  }

  /**
   * Check if the current assignment statement is a child of a give type of
   * node.
   *
   * @param opcode Opcode of the ancestor to check for.
   * @return True if on of the ancestor is of the given kind. Search is
   * contained in the function definition itself.
   */
  public boolean isChildOf(Xcode opcode) {
    Xnode crt = this;
    while(crt != null) {
      if(crt.ancestor().opcode() == opcode) {
        return true;
      }
      // Stop searching when FfunctionDefinition is reached
      if(crt.ancestor().opcode() == Xcode.F_FUNCTION_DEFINITION) {
        return false;
      }
      crt = crt.ancestor();
    }
    return false;
  }

  /**
   * Get list of array variable names used in assignment statement.
   *
   * @return List of variables.
   */
  public Set<String> getVarNames() {
    List<Xnode> vars = matchAll(Xcode.VAR);
    Set<String> names = new HashSet<>();
    for(Xnode var : vars) {
      names.add(var.value());
    }
    return names;
  }

  /**
   * Get all variables names on the RHS.
   *
   * @return Set of variables names used on the RHS.
   */
  public Set<String> getReadNames() {
    List<Xnode> vars = getRhs().matchAll(Xcode.VAR);
    Set<String> names = new HashSet<>();
    for(Xnode var : vars) {
      names.add(var.value());
    }
    return names;
  }
}
