/**
 * Copyright (C) 2016-2017 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
package com.bitplan.antlr;

import static org.junit.Assert.assertEquals;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import com.bitplan.exp.ExpLexer;
import com.bitplan.exp.ExpParser;
/**
 * test the expression parser example
 * @author wf
 *
 */
public class TestExpressionParser {

	@Test
	public void testExpressionParser() throws Exception {
		String expression = "12*(5-6)";
		ANTLRInputStream in = LanguageParser.streamForText(expression);
		ExpLexer lexer = new ExpLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ExpParser parser = new ExpParser(tokens);
		double result = parser.eval().value;
		// System.out.println(result);
		assertEquals(-12.0, result, 0.0001);
	}

}
