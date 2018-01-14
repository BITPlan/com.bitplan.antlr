/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.antlr open source project
 *
 * Copyright © 2016-2018 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.antlr;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.Test;

import com.bitplan.expr.ExprLexer;
import com.bitplan.expr.ExprParser;
import com.bitplan.expr.NumexprLexer;
import com.bitplan.expr.NumexprParser;
import com.bitplan.expr.PrimrecexprLexer;
import com.bitplan.expr.PrimrecexprParser;


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

    private ExprParser mParser;

    @Override
    protected org.antlr.v4.runtime.Parser getParser(CommonTokenStream tokens) {
      mParser = new ExprParser(tokens);
      return mParser;
    }

    @Override
    protected Lexer getLexer(CharStream in) {
      return new ExprLexer(in);
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

    private NumexprParser mParser;

    @Override
    protected org.antlr.v4.runtime.Parser getParser(CommonTokenStream tokens) {
      mParser = new NumexprParser(tokens);
      return mParser;
    }

    @Override
    protected Lexer getLexer(CharStream in) {
      return new NumexprLexer(in);
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

    private PrimrecexprParser mParser;

    @Override
    protected org.antlr.v4.runtime.Parser getParser(CommonTokenStream tokens) {
      mParser = new PrimrecexprParser(tokens);
      return mParser;
    }

    @Override
    protected Lexer getLexer(CharStream in) {
      return new PrimrecexprLexer(in);
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

  static double MIN_RATIO=0.9;
  /**
   * see https://github.com/antlr/antlr4/issues/994
   * 
   * @throws Exception
   */
  @Test
  public void testIssue994() throws Exception {
    super.testDuration(new ExprParserHolderFactory(), 80, MIN_RATIO, 1.2);
  }

  /**
   * see https://github.com/antlr/antlr4/issues/994
   * 
   * @throws Exception
   */
  @Test
  public void testIssue994NumExpr() throws Exception {
    // debug=true;
    super.testDuration(new NumExprParserHolderFactory(), 30, MIN_RATIO, 3.0);
  }

  /**
   * see https://github.com/antlr/antlr4/issues/1232
   * 
   * @throws Exception
   */
  @Test
  public void testIssue1232PrimRecursiveExpr() throws Exception {
    // debug = true;
    super.testDuration(new PrimRecExprParserHolderFactory(), 50,MIN_RATIO,1.2);
  }

}
