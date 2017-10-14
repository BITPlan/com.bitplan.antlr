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

import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;

import com.bitplan.antlr.LanguageParser;

/**
 * Basetest with error collection
 * 
 * @author wf
 *
 */
public abstract class BaseTest {
  @Rule
  public ErrorCollector collector = new ErrorCollector();

  public abstract LanguageParser getParser();

  LanguageParser parser;
  boolean debug = true;
  boolean gui = true;
  boolean checkTree = true;
  boolean checkgui = true;
  
  /**
   * run parser with default options
   * @param inputText - the input text
   * @param expected - the number of expected errors
   * @return - the LanguageParser
   * @throws Exception
   */
  public LanguageParser runParser(String inputText, int expected) throws Exception {
    parser=this.getParser();
    parser.debug=debug;
    parser.gui=gui;
    return runParser(parser, inputText, expected);
  }

  /**
   * run the parser on the given input Text
   * 
   * @param parser - the parser to run
   * @param inputText - the input text
   * @param expected - the number of expected errors
   * @return - the LanguageParser
   * @throws Exception
   */
  public LanguageParser runParser(LanguageParser parser, String inputText,
      int expected) throws Exception {
    ParseTree parse = parser.parse(inputText);
    if (parse != null)
      check(parser, expected);
    else
      throw new Exception("no parse tree created");
    return parser;
  }

  /**
   * check
   * 
   * @param parser
   */
  protected int check(LanguageParser parser, int expected) {
    int errorCount = parser.getErrorCount();
    if (errorCount > 0) {
      if (debug)
        System.out.println(parser.getInputText());
      for (com.bitplan.antlr.LanguageParser.Error error : parser.getErrors()) {
        System.err.println(error.getErrorLine());
      }
      if (checkTree) {
        parser.printTree();
      }
    }
    if (errorCount >= 1 & checkgui) {
      parser.showParseTree();
    }
    // -1 flags that there should be no checks here
    if (expected >= 0)
      collector.checkThat("Found Parser Errors", errorCount,
          lessThanOrEqualTo(expected));
    return errorCount;
  }

}
