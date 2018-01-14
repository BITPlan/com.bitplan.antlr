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

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;
import org.apache.commons.io.FileUtils;

import com.bitplan.swingutil.JToolTipEventTextArea;
import com.bitplan.swingutil.LineNumberer;
import com.bitplan.swingutil.LinePainter;
import com.bitplan.swingutil.ZoomPane;

/**
 * Base class for Language Parsers
 * @author wf
 *
 */
public abstract class LanguageParser {
  boolean debug = false;
  boolean gui = false;
  public boolean REPORT_SYNTAX_ERRORS = true;
  public DescriptiveErrorListener errorListener = new DescriptiveErrorListener();
  public int errorCount;
  public static ParserMode parserMode=ParserMode.SLL_ONLY;
  String sourceFileName = "Text";
  private CommonTokenStream tokens;

  static CharStream in;

  protected enum ParserMode {LL_ONLY,TWO_STAGE,SLL_ONLY,LL_AMBIG_DETECTION}
  String inputText;

  /**
   * @return the inputText
   */
  public String getInputText() {
    return inputText;
  }

  /**
   * @param inputText
   *          the inputText to set
   */
  public void setInputText(String inputText) {
    this.inputText = inputText;
  }

  // the list of errors
  private List<Error> errors = new ArrayList<Error>();
  ParseTree rootContext;
  LinePainter linePainter;
  LineNumberer lineNumberer;
  protected LinePainter errorlinePainter;
  
  /**
   * a single Error
   */
  public static class Error {
  
    Object offendingSymbol;
    private int line;
    int charPositionInLine;
    private String msg;
    RecognitionException e;
    String sourceName;
  
    /**
     * create an Error
     * 
     * @param offendingSymbol
     * @param line
     * @param charPositionInLine
     * @param msg
     * @param e
     * @param sourceName
     */
    public Error(Object offendingSymbol, int line, int charPositionInLine,
        String msg, RecognitionException e, String sourceName) {
      super();
      this.offendingSymbol = offendingSymbol;
      this.setLine(line);
      this.charPositionInLine = charPositionInLine;
      this.setMsg(msg);
      this.e = e;
      this.sourceName = sourceName;
    }
  
    /**
     * get the errorLine;
     * 
     * @return
     */
    public String getErrorLine() {
      if (!sourceName.isEmpty()) {
        sourceName = String.format("%s:%d:%d: ", sourceName, getLine(),
            charPositionInLine);
      }
      String errorLine = sourceName + "line " + getLine() + ":" + charPositionInLine
          + " " + getMsg();
      return errorLine;
    }

    public String getMsg() {
      return msg;
    }

    public void setMsg(String msg) {
      this.msg = msg;
    }

    public int getLine() {
      return line;
    }

    public void setLine(int line) {
      this.line = line;
    }
  }

  /**
   * @return the debug
   */
  public boolean isDebug() {
    return debug;
  }

  /**
   * @param debug
   *          the debug to set
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  /**
   * @return the gui
   */
  public boolean isGui() {
    return gui;
  }

  /**
   * @param gui
   *          the gui to set
   */
  public void setGui(boolean gui) {
    this.gui = gui;
  }

  /**
   * @return the sourceFileName
   */
  public String getSourceFileName() {
    return sourceFileName;
  }

  /**
   * @param sourceFileName
   *          the sourceFileName to set
   */
  public void setSourceFileName(String sourceFileName) {
    this.sourceFileName = sourceFileName;
  }

  
  /**
   * @return the errorCount
   */
  public int getErrorCount() {
    return errorCount;
  }

  /**
   * @param errorCount
   *          the errorCount to set
   */
  public void setErrorCount(int errorCount) {
    this.errorCount = errorCount;
  }

  /**
   * parse the given inputText
   * 
   * @param inputText
   * @throws IOException
   */
  public ParseTree parse(String inputText) throws Exception {
    this.inputText = inputText;
    in = streamForText(inputText);
    ParseTree result = parse(in, inputText);
    return result;
  }
  
  /**
   * parse the given input File
   * 
   * @param inputFile
   * @return - the parse Tree
   * @throws Exception
   */
  public ParseTree parse(File inputFile) throws Exception {
    ParseTree parseTree = this.parse(inputFile, "utf-8");
    return parseTree;
  }

  /**
   * parse the given input File with the given encoding
   * 
   * @param inputFile
   * @param encoding
   * @return - the parse Tree
   * @throws IOException
   */
  public ParseTree parse(File inputFile, String encoding) throws Exception {
    this.setSourceFileName(inputFile.getPath());
    in = CharStreams.fromFileName(sourceFileName);
    inputText = FileUtils.readFileToString(inputFile, encoding);
    ParseTree result = parse(in, inputText);
    return result;
  }
  
  /**
   * get the CharStream for the given text
   * 
   * @param text
   * @return the CharStream
   * @throws IOException
   */
  public static CharStream streamForText(String text) throws IOException {
    InputStream stream = new ByteArrayInputStream(
        text.getBytes(StandardCharsets.UTF_8));
    in = CharStreams.fromStream(stream);
    return in;
  }

  /**
   * the the tree width of the given tree
   * 
   * @param tree
   * @return the tree width
   */
  public static int treeWidth(ParseTree tree) {
    int width = tree.getChildCount();
    for (int childIndex = 0; childIndex < tree.getChildCount(); childIndex++) {
      ParseTree child = tree.getChild(childIndex);
      width = Math.max(width, treeWidth(child));
    }
    return width;
  }

  /**
   * show the given Tree Viewer
   * 
   * @param tv
   * @param title
   * @param inputText
   * @param scale
   */
  public int showTreeViewer(TreeViewer tv, String title, String inputText,
      double scale) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JToolTipEventTextArea textArea = new JToolTipEventTextArea(15, 80);
        JTextArea errorArea = new JTextArea(5, 80);
        JScrollPane scrollPane = new JScrollPane(textArea);
        linePainter = new LinePainter(textArea);
        for (Error error : this.getErrors()) {
          linePainter.addErrorLine(error.getLine());
          String errMsg = "line " + error.getLine() + " at col: "
              + error.charPositionInLine + ":" + error.getMsg();
          textArea.addToolTip(error.getLine(), errMsg);
          errorArea.append(errMsg + "\n");
        }
        lineNumberer = new LineNumberer(scrollPane, textArea);
        // set Text aftr lineNumberer is activated to show line numbers
        textArea.setText(inputText);
        panel.add(scrollPane);
        JScrollPane errorScrollPane = new JScrollPane(errorArea);
        errorlinePainter = new LinePainter(errorArea);
        panel.add(errorScrollPane);
        tv.setScale(scale);
        ZoomPane tvZoomPane = new ZoomPane(tv, title + " " + this.errorCount
            + " errors",
            "to zoom use CMD +/- up/down left/right or the mouse wheel");
        panel.add(tvZoomPane.getScrollPane());
        panel.addHierarchyListener(new HierarchyListener() {
          public void hierarchyChanged(HierarchyEvent e) {
            Window window = SwingUtilities.getWindowAncestor(panel);
            if (window instanceof Dialog) {
              Dialog dialog = (Dialog) window;
              if (!dialog.isResizable()) {
                dialog.setResizable(true);
              }
            }
          }
        });
        return JOptionPane.showConfirmDialog(null, panel, "ParseTree",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      }

  /**
   * show the parseTree
   * 
   * @param parseTree
   * @param inputText
   * @param inputNames
   */
  public void showParseTree(ParseTree parseTree, String inputText, String[] ruleNames) {
    // http://stackoverflow.com/questions/30134121/drawing-parse-tree-in-antlr4-using-java/30137407#30137407
    List<String> ruleNamesList = Arrays.asList(ruleNames);
    // http://stackoverflow.com/questions/34832518/antlr4-dotgenerator-example
    TreeViewer tv = new TreeViewer(ruleNamesList, parseTree);
    double scale = 1;
    if (showTreeViewer(tv, sourceFileName, inputText, scale) == JOptionPane.CANCEL_OPTION) {
      System.exit(1);
    }
    /*
     * DotGenerator gen = new DotGenerator();
     * StringTemplate st = gen.toDOT((Tree) tree);
     * System.out.println(st);
     */
  }

  /**
   * show the parse Tree
   * @param parser 
   */
  public void showParseTree(Parser parser) {
    showParseTree(rootContext, inputText, parser.getRuleNames());
  }

  /**
   * print the Tree
   */
  public void printTree() {
    printTree(this.rootContext);
  }

  /**
   * get the tree width
   * 
   * @return - the tree width
   */
  public int treeWidth() {
    int result =treeWidth(this.rootContext);
    return result;
  }

  /**
   * print the given Tree
   * 
   * @param tree
   *          - the ParseTree to print
   */
  public static void printTree(ParseTree tree) {
    printTree(tree, 0);
  }

  /**
   * strip the given end from the string and wrap it with pre and post e.g.
   * strip ("TestImpl","Impl") returns "Test"
   * 
   * @param text
   * @param end
   * @return - the stripped end
   */
  public static String stripEnd(String text, String end) {
    if (text.endsWith(end)) {
      return text.substring(0, text.length() - end.length());
    } else {
      return text;
    }
  }

  /**
   * print the given tree at the given level
   * 
   * @param tree
   * @param level
   */
  public static void printTree(ParseTree tree, int level) {
    // indent level
    for (int i = 0; i < level; i++)
      System.out.print("__");
  
    // print node description: type code followed by token text
    String nodeName = tree.getClass().getSimpleName();
    nodeName = stripEnd(nodeName, "Context");
    nodeName = stripEnd(nodeName, "Impl");
    String text = tree.getText();
    if (nodeName.equals("TerminalNode")) {
      nodeName = "";
      text = "<" + text + ">";
    }
  
    System.out.println(" " + nodeName + " " + text);
  
    // print all children
    for (int childIndex = 0; childIndex < tree.getChildCount(); childIndex++) {
      ParseTree child = tree.getChild(childIndex);
      printTree(child, level + 1);
    }
  }
  
  /**
   * see
   * http://stackoverflow.com/questions/18132078/handling-errors-in-antlr4
   */
  public class DescriptiveErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
        Object offendingSymbol, int line, int charPositionInLine, String msg,
        RecognitionException e) {
      errorCount++;

      String sourceName = recognizer.getInputStream().getSourceName();
      Error error = new Error(offendingSymbol, line, charPositionInLine, msg,
          e, sourceName);
      getErrors().add(error);
      if (!REPORT_SYNTAX_ERRORS) {
        return;
      }
      if (debug) {
        System.err.println(error.getErrorLine());
      }

    }
  }

  public LanguageParser() {
    super();
  }

  // abstract functions that need to be implemented
  /**
   * get the Root Context for this parser
   * @param parser
   * @return - the root context
   */
  protected abstract ParseTree getRootContext(Parser parser);
  
  /**
   * parse the given input stream
   * @param in
   * @param inputText
   * @return - the parse tree
   * @throws Exception
   */
  protected abstract ParseTree parse(CharStream in, String inputText)
      throws Exception;
 
  /**
   * show the parse tree
   */
  public abstract void showParseTree();
  
  /**
   * get the tokens
   * @param lexer
   * @return
   */
  protected TokenStream getTokens(Lexer lexer) {
    tokens = new CommonTokenStream(lexer);
    return tokens;
  }
  
  /**
   * parse with the given Lexer and Parser
   * @param lexer
   * @param parser
   * @return
   */
  public ParseTree parse(Lexer lexer, Parser parser) {
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);
    switch (parserMode) {
    case LL_ONLY:
      parser.getInterpreter().setPredictionMode(PredictionMode.LL);
      rootContext = getRootContext(parser);
      break;
    case LL_AMBIG_DETECTION:
      parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
      rootContext = getRootContext(parser);
      break;    
    case SLL_ONLY:
      parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
      rootContext = getRootContext(parser);
      break;
    case TWO_STAGE:
      // see https://github.com/antlr/antlr4/issues/192
      try {
        BailErrorStrategy errorHandler = new BailErrorStrategy();
        parser.setErrorHandler(errorHandler);
        // set PredictionMode
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        rootContext = getRootContext(parser);
      } catch (Throwable th) {
        tokens.reset();
        parser.reset();
        parser.setErrorHandler(new DefaultErrorStrategy());
        errorCount = 0;
        getErrors().clear();
        parser.getInterpreter().setPredictionMode(PredictionMode.LL);
        rootContext = getRootContext(parser);
      }
    }
    if (debug) {
      if (gui) {
        showParseTree();
      }
      printTree(rootContext);
    }
    return rootContext;
  }

  public List<Error> getErrors() {
    return errors;
  }

  public void setErrors(List<Error> errors) {
    this.errors = errors;
  }


}