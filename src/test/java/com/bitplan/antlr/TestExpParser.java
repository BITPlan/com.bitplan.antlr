/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.antlr open source project
 *
 * Copyright © 2016-2017 BITPlan GmbH https://github.com/BITPlan
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

import static org.junit.Assert.assertEquals;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import com.bitplan.exp.ExpLanguageParser;
import com.bitplan.exp.ExpLexer;
import com.bitplan.exp.ExpParser;

/**
 * test the expression parser example
 * 
 * @author wf
 *
 */
public class TestExpParser extends BaseTest {
  
  @Override
  public LanguageParser getParser() {
    return new ExpLanguageParser();
  }

  @Test
  public void testExpressionParser() throws Exception {
    String expressions[] = { "2*3", "4+5", "(2+3)*(4+5)",
        // uncomment following line to test gui feature
        // "(4+5)--(6-7)" 
    };
    ExpLanguageParser exprParser = new ExpLanguageParser();
    for (String expression : expressions) {
      super.runParser(exprParser, expression, 0);
    }
  }

  @Test
  public void testExpParser() throws Exception {
    String expression = "12*(5-6)";
    CharStream in = LanguageParser.streamForText(expression);
    ExpLexer lexer = new ExpLexer(in);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ExpParser parser = new ExpParser(tokens);
    double result = parser.eval().value;
    // System.out.println(result);
    assertEquals(-12.0, result, 0.0001);
  }

}
