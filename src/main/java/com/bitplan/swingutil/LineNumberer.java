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

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

/**
 * Line Numberer for a JTextArea in a JScrollPane
 * http://www.javaprogrammingforums.com/java-swing-tutorials/915-how-add-line-numbers-your-jtextarea.html
 * 
 * @author wf
 *
 */
public class LineNumberer implements DocumentListener {
  JTextArea jta;
  JTextArea lines;
  JScrollPane jsp;

  /**
   * create a line Numberer for the given scrollpane and text area
   * @param jsp
   * @param jta
   */
  public LineNumberer(JScrollPane jsp, JTextArea jta) {
    this.jsp=jsp;
    this.jta=jta;
    lines = new JTextArea("00001");
    
    lines.setBackground(new Color(220,220,220));
    lines.setEditable(false);
    jsp.setRowHeaderView(lines);
    jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jta.getDocument().addDocumentListener(this);
  }
  
  /**
   * get the number/stringformat for the given number of lines
   * @param maxLines
   * @return
   */
  public String getFormat(int maxLines) {
    // calculate the width e.g. 1 for 8, 2 for 87, 3 for 257 ...
    int width=1; // for 0-9
    while (maxLines>=10) {
      width++; // increment width per 10 longer lines
      maxLines=maxLines/10; // 
    }
    String format=" %0"+width+"d ";   // e.g. " %4d " for 1697
    return format;
  }
  
  /**
   * get the line number for the given line
   * @param line
   * @return
   */
  public String lineNumber(String format,int line) {
    String lineNumber=String.format(format, line) + System.getProperty("line.separator");
    return lineNumber;
  }
  
  /**
   * get the text
   * @return
   */
  public String getText(){
    int caretPosition = jta.getDocument().getLength();
    Element root = jta.getDocument().getDefaultRootElement();
    int maxLine=root.getElementIndex( caretPosition ) + 2;
    String format=getFormat(maxLine);
    String text = lineNumber(format,1);
    for(int i = 2; i < maxLine; i++){
      text += lineNumber(format,i);
    }
    return text;
  }
  
  @Override
  public void changedUpdate(DocumentEvent de) {
    lines.setText(getText());
  }

  @Override
  public void insertUpdate(DocumentEvent de) {
    lines.setText(getText());
  }

  @Override
  public void removeUpdate(DocumentEvent de) {
    lines.setText(getText());
  }
}
