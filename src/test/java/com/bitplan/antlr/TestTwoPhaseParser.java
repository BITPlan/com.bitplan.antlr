package com.bitplan.antlr;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.Parser;

/**
 * 
 * @author wf
 *         see https://github.com/antlr/antlr4/issues/994
 */
public abstract class TestTwoPhaseParser {
  static boolean debug = false;

  public static abstract class ParserHolderFactory {
    abstract ParserHolder getParserHolder(int index) throws Exception;
  }

  /**
   * hold a parser and some input;
   */
  public static abstract class ParserHolder {
    ANTLRInputStream in;
    CommonTokenStream tokens;
    Parser parser;
    private Lexer lexer;
    private String input;

    /**
     * create a parser Holder for the given index
     * 
     * @param index
     * @throws IOException
     */
    public ParserHolder(int index) throws IOException {
      input = getInput(index);
      init(input);
    }

    /**
     * create a parser holder for the given input
     * 
     * @param input
     * @throws IOException
     */
    public void init(String input) throws IOException {
      if (debug)
        System.out.println(input);
      in = streamForText(input);
      lexer = getLexer(in);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      parser = getParser(tokens);
    }

    /**
     * get an input of the given index/size
     * 
     * @param index
     * @return a string to be tested
     */
    protected abstract String getInput(int index);

    protected abstract Parser getParser(CommonTokenStream tokens);

    protected abstract Lexer getLexer(ANTLRInputStream in);

    protected abstract ParserRuleContext parse();

    /**
     * get the ANTLRInputStream for the given text
     * 
     * @param text
     * @return
     * @throws IOException
     */
    public static ANTLRInputStream streamForText(String text)
        throws IOException {
      InputStream stream = new ByteArrayInputStream(
          text.getBytes(StandardCharsets.UTF_8));
      ANTLRInputStream in = new ANTLRInputStream(stream);
      return in;
    }
  }

  /**
   * test how long the parsing takes
   * 
   * @param parserHolderFactory
   * @throws Exception
   */
  public void testDuration(ParserHolderFactory parserHolderFactory, int max, double expectedRatioMin, double expectedRatioMax)
      throws Exception {
    long prevduration = 0;
    double ratiosum = 0;
    int ratiocount = 0;
    for (int i = 1; i <= max; i++) {
      System.gc(); // allow garbage collection first
      long start = System.nanoTime()/1000;
      ParserHolder parserHolder = parserHolderFactory.getParserHolder(i);
      doTestParser(parserHolder, PredictionMode.SLL, PredictionMode.LL);
      long stop = System.nanoTime()/1000;
      long duration = stop - start;
      if (duration < 1)
        duration = 1;
      if (i >= 2) {
        double ratio = duration * 1.0 / (prevduration * 1.0);
        System.out.println(String.format("%6d %8d usecs %5.1f", i, duration,
            ratio));
        // ignore extrem ratios a factor of 2 already would be bad ...
        if (ratio<5) {
          ratiosum += ratio;
          ratiocount++;
        }
      }
      prevduration = duration;
    }
    double averageRatio = ratiosum / ratiocount;
    System.out.println(String.format("ratio: %3.2f", averageRatio));
    assertTrue("Performance issue https://github.com/antlr/antlr4/issues/994 <="+expectedRatioMax,
        averageRatio <= expectedRatioMax);
    assertTrue("Performance issue https://github.com/antlr/antlr4/issues/994 >="+expectedRatioMin,
        averageRatio >=expectedRatioMin);

  }

  /**
   * tes the parser
   * 
   * @param parserHolder
   * @param mode
   * @param fallBackMode
   * @return
   * @throws IOException
   */
  protected ParserRuleContext doTestParser(ParserHolder parserHolder,
      PredictionMode mode, PredictionMode fallBackMode) throws IOException {
    ParserRuleContext result;
    try {
      BailErrorStrategy errorHandler = new BailErrorStrategy();
      parserHolder.parser.setErrorHandler(errorHandler);
      // set PredictionMode
      parserHolder.parser.getInterpreter().setPredictionMode(mode);
      if (mode.equals(PredictionMode.LL_EXACT_AMBIG_DETECTION)) {
        parserHolder.parser.addErrorListener(new DiagnosticErrorListener());
      }
      result = parserHolder.parse();
    } catch (Throwable th) {
      if (th instanceof ParseCancellationException) {
        ParseCancellationException pce = (ParseCancellationException) th;
        if (pce.getCause() instanceof RecognitionException) {
          RecognitionException re = (RecognitionException) pce.getCause();
          ParserRuleContext context = (ParserRuleContext) re.getCtx();
          throw context.exception;
        }
      }
      if (fallBackMode != null) {
        parserHolder.tokens.reset();
        parserHolder.parser.reset();
        parserHolder.parser.addErrorListener(ConsoleErrorListener.INSTANCE);
        parserHolder.parser.setErrorHandler(new DefaultErrorStrategy());
        parserHolder.parser.getInterpreter().setPredictionMode(fallBackMode);
        result = parserHolder.parse();
      } else {
        throw th;
      }
    }
    if (debug) {
      System.out.println(result.toStringTree());
    }
    return result;
  }

}
