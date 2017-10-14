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
package com.bitplan.swingutil;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

/**
 * Zoomable Pane
 * 
 * @author wf
 *
 */
public class ZoomPane extends JPanel {

  private static final long serialVersionUID = 1L;
  
  /**
   * Helper class for Zoom factor
   */
  public static class Zoom {
    private double zoomx = 1d;
    private double zoomy = 1d;  
    Zoom (double x,double y) {
      zoomx=x;
      zoomy=y;
    }
  }
  
  Zoom zoom=new Zoom(1,1);
  InputMap im;
  ActionMap am;
  boolean debug=false;
  JScrollPane scrollPane;
  String title;
  String toolTip;

  /**
   * @return the zoom
   */
  public Zoom getZoom() {
    return zoom;
  }

  /**
   * @return the scrollPane
   */
  public JScrollPane getScrollPane() {
    return scrollPane;
  }

  /**
   * @param scrollPane the scrollPane to set
   */
  public void setScrollPane(JScrollPane scrollPane) {
    this.scrollPane = scrollPane;
  }

  /**
   * change the zoom by the given delta
   * @param dx
   * @param dy
   */
  public void changeZoom(double dx, double dy) {
    zoom.zoomx+=dx;
    zoom.zoomy+=dy;
    updateZoom();
  }
  
  /**
   * update the Zoom factor
   */
  public void updateZoom() {
    if (debug)      
      System.out.println("zoom:" + zoom.zoomx+ "/" +zoom.zoomy);
    invalidate();
    repaint();   
  }
  
  /**
   * @param d
   *          the zoom to set
   */
  public void setZoomX(Zoom zoom) {
    this.zoom = zoom;
    updateZoom();
  }

  /**
   * add a Zoom Modifier
   * @param vkKey
   * @param action
   * @param dx
   * @param dy
   */
  public void addZoomModifier(int vkKey, String action, final double dx,final double dy) {
    im.put(KeyStroke.getKeyStroke(vkKey,InputEvent.META_DOWN_MASK), action);
    am.put(action, new AbstractAction() {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        changeZoom(dx,dy);
      }
    });
  }
  /**
   * zoom the given component
   * 
   * @param component
   */
  public ZoomPane(JComponent component, String title, String toolTip) {
    super();
    this.title=title;
    this.toolTip=toolTip;
    setBorder(BorderFactory.createTitledBorder(title));
    setToolTipText(toolTip);
    JPanel inner = new JPanel();
    add(inner);
    // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.add(component);
    scrollPane=new JScrollPane(this);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setAutoscrolls(true);
    im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
    am = getActionMap();
    addZoomModifier(KeyEvent.VK_PLUS, "plus",0.1d,0.1d);
    addZoomModifier(KeyEvent.VK_MINUS,"minus",-0.1d,-0.1d);
    addZoomModifier(KeyEvent.VK_UP,   "yplus",0,0.1d);
    addZoomModifier(KeyEvent.VK_DOWN, "yminus",0,-0.1d);
    addZoomModifier(KeyEvent.VK_RIGHT,"xplus",0.1d,0);
    addZoomModifier(KeyEvent.VK_LEFT, "xminus",-0.1d,0);
  
    addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        // Rectangle r=scrollPane.getViewport().getViewRect();
        // r.translate(e.getX()/2,e.getY()/2);
        double delta= e.getPreciseWheelRotation() / 10;
        changeZoom(delta,delta);
        // System.out.println(r);

        // scrollPane.getViewport().scrollRectToVisible(r);
      }

    });

    setFocusable(true);
    requestFocusInWindow();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.scale(zoom.zoomx, zoom.zoomy);
  }

  /**
   * get my preferred Size
   * let the Layoutmanager doe the rest
   */
  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    d.height = (int) (d.height * zoom.zoomy);
    d.width = (int) (d.width * zoom.zoomx);
    return d;
  }
  
}
