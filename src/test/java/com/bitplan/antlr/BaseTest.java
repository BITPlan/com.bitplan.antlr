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

import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.io.FileUtils;
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
  protected static final ExecutorService THREAD_POOL = Executors
        .newCachedThreadPool();
  protected static int MAX_TIMEOUT = 10000;
  protected static <T> T timedCall(Callable<T> c, long timeout, TimeUnit timeUnit)
      throws InterruptedException, ExecutionException, TimeoutException {
        FutureTask<T> task = new FutureTask<T>(c);
        THREAD_POOL.execute(task);
        return task.get(timeout, timeUnit);
      }

  // http://stackoverflow.com/questions/10221891/continuing-test-execution-in-junit4-even-when-one-of-the-asserts-fails
  @Rule
  public ErrorCollector collector = new ErrorCollector();

  public abstract LanguageParser getParser();

  protected LanguageParser parser;
  protected boolean debug = true;
  protected boolean gui = true;
  protected boolean checkTree = false; // if true in case of an error the parse tree will
  // be printed
  protected boolean checkgui = false; // if true in case of an error a interactive gui
  // with
  // the source code and parseTree will be shown

  /**
   * run parser with default options
   * 
   * @param inputText
   *          - the input text
   * @param expected
   *          - the number of expected errors
   * @return - the LanguageParser
   * @throws Exception
   */
  public LanguageParser runParser(String inputText, int expected)
      throws Exception {
    parser = this.getParser();
    parser.debug = debug;
    parser.gui = gui;
    return runParser(parser, inputText, expected);
  }

  /**
   * run the parser on the given input Text
   * 
   * @param parser
   *          - the parser to run
   * @param inputText
   *          - the input text
   * @param expected
   *          - the number of expected errors
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

  /**
   * check the given file
   * 
   * @param inputFile
   * @param srcFileDirectory
   * @return - the number of errors
   * @throws Exception
   * @return the parser
   */
  public LanguageParser doTestParser(File inputFile, SourceDirectory srcFileDirectory) throws Exception {
    @SuppressWarnings("unchecked")
    List<String> lines = FileUtils.readLines(inputFile,
        srcFileDirectory.encoding);
    srcFileDirectory.totalLines += lines.size();
    srcFileDirectory.totalFiles++;
  
    String inputText = FileUtils.readFileToString(inputFile, "UTF-8");
    if (debug) {
      System.out.println(inputFile.getPath());
      System.out.println(inputText);
    }
    LanguageParser result = this.doTestParser(inputText, -1);
    result.setSourceFileName(inputFile.getPath());
    if (result.errorCount > 0)
      srcFileDirectory.errorFiles++;
    srcFileDirectory.totalErrors += result.errorCount;
    return result;
  }

  /**
   * test the given input Text
   * 
   * @param inputText
   * @throws Exception
   */
  public LanguageParser doTestParser(String inputText) throws Exception {
    return doTestParser(inputText, 0);
  }

  /**
   * test the given input Text
   * 
   * @param inputText
   * @throws Exception
   * @return the LanguageParser
   */
  public LanguageParser doTestParser(String inputText, int expectedErrors) throws Exception {
    return doTestParser(inputText, expectedErrors, MAX_TIMEOUT);
  }

  /**
   * test the given rule with the given timeout
   * 
   * @param inputText
   * @param expectedErrors
   * @param timeOutMSecs
   * @throws Exception
   * @return the parser
   */
  public LanguageParser doTestParser(final String inputText, final int expectedErrors, int timeOutMSecs)
      throws Exception {
        if (debug) {
          System.out.println(inputText);
        }
        LanguageParser result = timedCall(new Callable<LanguageParser>() {
          public LanguageParser call() throws Exception {
            return runParser(inputText, expectedErrors);
          }
        }, timeOutMSecs, TimeUnit.MILLISECONDS);
        return result;
      }

  /**
   * test parse files in directories
   * 
   * @param rootDir
   * @param sourceDirectories
   * @param extensions
   * @param ignorePrefixes
   * @param limit
   * @param progressStep
   *          - how often to show the progress
   * @return the list of failed LanguageParsers
   * @throws Exception
   */
  public List<LanguageParser> testParseFilesInDirectories(File rootDir, List<SourceDirectory> sourceDirectories,
      String[] extensions, String[] ignorePrefixes, int limit, int progressStep) throws Exception {
        List<LanguageParser> result = new ArrayList<LanguageParser>();
        int count = 0;
      
        SourceDirectory totalCount = new SourceDirectory(rootDir.getPath(), "/",
            "total");
        for (SourceDirectory srcFileDirectory : sourceDirectories) {
          // get all files from the given Directory
          String srcFileNames[] = srcFileDirectory.directory.list();
          if (srcFileNames != null) {
            for (String srcFileName : srcFileNames) {
              File srcFile = new File(srcFileDirectory.directory, srcFileName);
              if (!srcFile.isDirectory()) {
      
                boolean knownExtension = false;
                for (String extension : extensions) {
                  if (srcFile.getName().endsWith(extension)) {
                    knownExtension = true;
                  }
                }
                boolean doIgnore = !knownExtension;
                for (String prefix : ignorePrefixes) {
                  if (srcFile.getName().startsWith(prefix)) {
                    doIgnore = true;
                  }
                }
                if (!doIgnore) {
                  count++;
                  if (count > limit)
                    break;
                  if (count % progressStep == 1) {
                    System.out.println(String.format("test %4d:", count)
                        + srcFile.getPath());
                  }
                  try {
                    LanguageParser parser = doTestParser(srcFile, srcFileDirectory);
                    if (parser.errorCount > 0) {
                      System.err.println(srcFile.getPath()+": "+parser.errorCount+" errors");
                      result.add(parser);
                    }
                  } catch (TimeoutException toe) {
                    System.err.println(srcFile.getPath()+": Timeout");
                    srcFileDirectory.errorFiles++;
                  }
                }
              }
            }
          }
          collector.checkThat("Found Parser Errors in " + srcFileDirectory.title
              + "(" + srcFileDirectory.info + ")", srcFileDirectory.totalErrors,
              lessThanOrEqualTo(srcFileDirectory.expectedErrors));
          srcFileDirectory.showResult();
          totalCount.totalErrors += srcFileDirectory.totalErrors;
          totalCount.errorFiles += srcFileDirectory.errorFiles;
          totalCount.totalFiles += srcFileDirectory.totalFiles;
          totalCount.totalLines += srcFileDirectory.totalLines;
        }
        totalCount.showResult();
        return result;
      }

}
