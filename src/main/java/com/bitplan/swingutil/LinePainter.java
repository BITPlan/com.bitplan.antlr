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
package com.bitplan.swingutil;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/*
 *  Track the movement of the Caret by painting a background line at the
 *  current caret position.
 *  see https://tips4java.wordpress.com/2008/10/29/line-painter/
 */
public class LinePainter implements Highlighter.HighlightPainter,
    CaretListener, MouseListener, MouseMotionListener {
  private JTextComponent component;

  private Color color;
  private Color errorColor;
  java.util.List<Integer> errorLines = new ArrayList<Integer>();

  private Rectangle lastView;

  private boolean debug=false;

  /*
   * The line color will be calculated automatically by attempting to make the
   * current selection lighter by a factor of 1.2.
   * 
   * @param component text component that requires background line painting
   */
  public LinePainter(JTextComponent component) {
    this(component, null);
    // http://www.colourlovers.com/color/FF9494/error_red
    errorColor = new Color(255, 148, 148);
    setLighter(component.getSelectionColor());
  }

  /**
   * add the given error line
   * 
   * @param line
   */
  public void addErrorLine(int line) {
    this.errorLines.add(line);
  }

  /*
   * Manually control the line color
   * 
   * @param component text component that requires background line painting
   * 
   * @param color the color of the background line
   */
  public LinePainter(JTextComponent component, Color color) {
    this.component = component;
    setColor(color);

    // Add listeners so we know when to change highlighting

    component.addCaretListener(this);
    component.addMouseListener(this);
    component.addMouseMotionListener(this);

    // Turn highlighting on by adding a dummy highlight

    try {
      component.getHighlighter().addHighlight(0, 0, this);
    } catch (BadLocationException ble) {
    }
  }

  /*
   * You can reset the line color at any time
   * 
   * @param color the color of the background line
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /*
   * Calculate the line color by making the selection color lighter
   * 
   * @return the color of the background line
   */
  public void setLighter(Color color) {
    int red = Math.min(255, (int) (color.getRed() * 1.2));
    int green = Math.min(255, (int) (color.getGreen() * 1.2));
    int blue = Math.min(255, (int) (color.getBlue() * 1.2));
    setColor(new Color(red, green, blue));
  }

  /**
   * highlight the given position
   * 
   * @param lineNumber
   *          - if -1 use the caretPosition
   * @param g
   *          - graphics
   * @param textC
   * @param color
   * @return - the highlighted rectangle
   */
  public Rectangle highLightPosition(int lineNumber, Graphics g,
      JTextComponent textC, Color color) {
    try {
      int position = 0;
      if (lineNumber < 0) {
        position = textC.getCaretPosition();
      } else {
        if (component instanceof JTextArea) {
          JTextArea jt = (JTextArea) component;
          position = jt.getLineStartOffset(lineNumber-1);
        }
      }

      Rectangle r = textC.modelToView(position);
      g.setColor(color);
      g.fillRect(0, r.y, textC.getWidth(), r.height);
      return r;
    } catch (BadLocationException ble) {
      if (debug)
        System.out.println(ble + ":" + lineNumber);
    }
    return null;
  }

  // Paint the background highlight
  public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
    Rectangle r = highLightPosition(-1, g, c, color);
    // highlight all errors
    for (int line : this.errorLines) {
      highLightPosition(line, g, c, errorColor);
    }
    if (lastView == null)
      lastView = r;
  }

  /*
   * Caret position has changed, remove the highlight
   */
  private void resetHighlight() {
    // Use invokeLater to make sure updates to the Document are completed,
    // otherwise Undo processing causes the modelToView method to loop.

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          int offset = component.getCaretPosition();
          Rectangle currentView = component.modelToView(offset);

          // Remove the highlighting from the previously highlighted line

          if (lastView.y != currentView.y) {
            component.repaint(0, lastView.y, component.getWidth(),
                lastView.height);
            lastView = currentView;
          }
        } catch (BadLocationException ble) {
        }
      }
    });
  }

  // Implement CaretListener

  public void caretUpdate(CaretEvent e) {
    resetHighlight();
  }

  // Implement MouseListener

  public void mousePressed(MouseEvent e) {
    resetHighlight();
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  // Implement MouseMotionListener

  public void mouseDragged(MouseEvent e) {
    resetHighlight();
  }

  public void mouseMoved(MouseEvent e) {
  }
}
