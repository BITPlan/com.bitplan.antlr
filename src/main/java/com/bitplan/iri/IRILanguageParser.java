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
package com.bitplan.iri;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

import com.bitplan.antlr.LanguageParser;

public class IRILanguageParser extends LanguageParser {
  private IRIParser parser;
  IRITokenLexer lexer;
  
  public IRIParser getParser() {
    return parser;
  }
  
  @Override
  protected ParseTree getRootContext(Parser parser) {
    if (!(parser instanceof IRIParser)) {
      throw new RuntimeException("wrong parser type for getRootContext, expected Rule but got "+parser.getClass().getName());
    } else {
      IRIParser expParser=(IRIParser) parser;
      return expParser.iri();
    }
  }

  @Override
  protected ParseTree parse(CharStream in, String inputText)
      throws Exception {
    lexer = new IRITokenLexer(in);
    parser=new IRIParser(getTokens(lexer));
    ParseTree result=super.parse(lexer,getParser());
    return result;
  }

  @Override
  public void showParseTree() {
//    super.showParseTree(getParser());
  }
}
