/**
 * Copyright (C) 2016 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
package com.bitplan.antlr;

import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.Test;

import com.bitplan.expr.exprLexer;
import com.bitplan.expr.exprParser;
import com.bitplan.expr.numexprLexer;
import com.bitplan.expr.numexprParser;
import com.bitplan.expr.primrecexprLexer;
import com.bitplan.expr.primrecexprParser;


/**
 * Test the Issue 994 performance
 * 
 * @author wf
 *
 */
public class TestIssue994 extends TestTwoPhaseParser {

  public static class ExprParserHolderFactory extends ParserHolderFactory {

    @Override
    ParserHolder getParserHolder(int index) throws Exception {
      return new ExprParserHolder(index);
    }

  }

  /**
   * 
   * @author wf
   *
   */
  public static class ExprParserHolder extends ParserHolder {

    public ExprParserHolder(int index) throws IOException {
      super(index);
    }

    private exprParser mParser;

    @Override
    protected org.antlr.v4.runtime.Parser getParser(CommonTokenStream tokens) {
      mParser = new exprParser(tokens);
      return mParser;
    }

    @Override
    protected Lexer getLexer(ANTLRInputStream in) {
      return new exprLexer(in);
    }

    @Override
    protected ParserRuleContext parse() {
      return mParser.expr();
    }

    @Override
    protected String getInput(int index) {
      String andClause = "not X0";
      for (int i = 0; i <= index; i++) {
        if (i % 4 == 1) {
          andClause += " or X" + i;
        } else {
          andClause += " and not X" + i;
        }
      }
      return andClause;
    }
  }

  public static class NumExprParserHolderFactory extends ParserHolderFactory {

    @Override
    ParserHolder getParserHolder(int index) throws Exception {
      return new NumExprParserHolder(index);
    }

  }

  /**
   * 
   * @author wf
   *
   */
  public static class NumExprParserHolder extends ParserHolder {

    public NumExprParserHolder(int index) throws IOException {
      super(index);
    }

    private numexprParser mParser;

    @Override
    protected org.antlr.v4.runtime.Parser getParser(CommonTokenStream tokens) {
      mParser = new numexprParser(tokens);
      return mParser;
    }

    @Override
    protected Lexer getLexer(ANTLRInputStream in) {
      return new numexprLexer(in);
    }

    @Override
    protected ParserRuleContext parse() {
      return mParser.numexpr();
    }

    @Override
    protected String getInput(int index) {
      String andClause = "if Value=0 ";
      for (int i = 0; i <= index; i++) {
        andClause += " and not Value" + i + "=" + i;
      }
      andClause += " endif";
      return andClause;
    }
  }

  public static class PrimRecExprParserHolderFactory extends
      ParserHolderFactory {

    @Override
    ParserHolder getParserHolder(int index) throws Exception {
      return new PrimRecParserHolder(index);
    }

  }

  /**
   * 
   * @author wf
   *
   */
  public static class PrimRecParserHolder extends ParserHolder {

    public PrimRecParserHolder(int index) throws IOException {
      super(index);
    }

    private primrecexprParser mParser;

    @Override
    protected org.antlr.v4.runtime.Parser getParser(CommonTokenStream tokens) {
      mParser = new primrecexprParser(tokens);
      return mParser;
    }

    @Override
    protected Lexer getLexer(ANTLRInputStream in) {
      return new primrecexprLexer(in);
    }

    @Override
    protected ParserRuleContext parse() {
      return mParser.primrecexpr();
    }

    @Override
    protected String getInput(int index) {
      String andClause = "if Value=0 ";
      for (int i = 0; i <= index; i++) {
        andClause += " and not Value" + i + "=" + i;
      }
      andClause += " endif";
      return andClause;
    }
  }

  /**
   * see https://github.com/antlr/antlr4/issues/994
   * 
   * @throws Exception
   */
  @Test
  public void testIssue994() throws Exception {
    super.testDuration(new ExprParserHolderFactory(), 80, 0.95, 1.2);
  }

  /**
   * see https://github.com/antlr/antlr4/issues/994
   * 
   * @throws Exception
   */
  @Test
  public void testIssue994NumExpr() throws Exception {
    // debug=true;
    super.testDuration(new NumExprParserHolderFactory(), 30, 1.0, 3.0);
  }

  /**
   * see https://github.com/antlr/antlr4/issues/1232
   * 
   * @throws Exception
   */
  @Test
  public void testIssue1232PrimRecursiveExpr() throws Exception {
    // debug = true;
    super.testDuration(new PrimRecExprParserHolderFactory(), 50,0.95,1.2);
  }

}
