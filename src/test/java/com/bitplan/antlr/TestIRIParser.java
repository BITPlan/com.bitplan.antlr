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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bitplan.iri.IRILanguageParser;

/**
 * test the tree part IRI-parser with example iris
 * @author wf
 *
 */
public class TestIRIParser extends BaseTest {
  @Override
  public LanguageParser getParser() {
    return new IRILanguageParser();
  }
  @Test
  /**
   * test parse ability of SiDIF files in examples directory
   */
  public void testIRISamples() throws Exception {
    File rootDir = new File("src/test/resources");
    debug=false;
    String[] extensions = { ".iri" };
    checkgui = false;
    super.checkTree=false;
    super.gui=false;
    List<SourceDirectory> sourceDirectories = new ArrayList<SourceDirectory>();
    SourceDirectory sidifSrc = new SourceDirectory(
        rootDir.getCanonicalPath() + "/iri", "utf-8", "IRI samples");
    sourceDirectories.add(sidifSrc);
    int progressStep = 1;
    int limit = 200;
    String[] ignorePrefixes = {};
    testParseFilesInDirectories(rootDir, sourceDirectories, extensions,
        ignorePrefixes, limit, progressStep);
  }

  

}
