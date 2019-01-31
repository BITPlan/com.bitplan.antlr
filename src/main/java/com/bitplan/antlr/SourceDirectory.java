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
/**
  Copyright (C) 2016 BITPlan GmbH
  Pater-Delp-Str. 1
  D-47877 Willich-Schiefbahn
  http://www.bitplan.com
 */
package com.bitplan.antlr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * helper class for checking the Parser Language contents of a complete source
 * directory
 * 
 * @author wf
 */
public class SourceDirectory {
  String path;
  public String info;
  public File directory;
  public String encoding;
  public String title;
  public int totalErrors = 0;
  public int totalFiles = 0;
  public int errorFiles = 0;
  public int totalLines = 0;
  public int expectedErrors = 0;

  /**
   * get the list of SourceDirectories starting from the given root
   * 
   * @param root
   *          - the root source Directory
   * @return the list of source directories
   */
  public static List<SourceDirectory> getSourceDirectories(File root) {
    List<SourceDirectory> result = new ArrayList<SourceDirectory>();
    String[] subFiles = root.list();
    if (subFiles != null)
      for (String subFileName : subFiles) {
        File subFile = new File(root, subFileName);
        if (subFile.isDirectory()) {
          SourceDirectory sourceDirectory = new SourceDirectory(
              subFile.getPath(), "UTF-8", subFileName);
          result.add(sourceDirectory);
        }
      }
    return result;
  }

  /**
   * create the given SourceDirectory
   * 
   * @param path
   * @param encoding
   * @param title
   */
  public SourceDirectory(String path, String encoding, String title) {
    super();
    this.path = path;
    directory = new File(path);
    this.encoding = encoding;
    this.title = title;
  }

  /**
   * show the result
   */
  public void showResult() {
    if (totalFiles > 0) {
      System.out.println("|" + this.title + ":" + this.path);
      System.out
          .println(String.format("|%5d/%7d (%5.2f %%) error/total/%% files",
              errorFiles, totalFiles, 100.0 * errorFiles / totalFiles));
      System.out
          .println(String.format("|%5d/%7d (%5.2f %%) error/total/%% lines",
              totalErrors, totalLines, 100.0 * totalErrors / totalLines));
    }
  }

}