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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.bitplan.antlr.TestExpressionParser;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestExpressionParser.class, TestIssue994.class})
/**
 * TestSuite
 * @author wf
 *
 * no content necessary - annotation has info
 */
public class TestSuite {
}
