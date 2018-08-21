# com.bitplan.antlr
| Issues        | Maven         | Project pages  | 
| ------------- | ------------: | ---------------| 
| [com.bitplan.antlr](https://github.com/BITPlan/com.bitplan.antlr/issues) | [0.0.7](https://search.maven.org/artifact/com.bitplan.antlr/com.bitplan.antlr/0.0.7/jar)      |   [Library with helpers for ANTLR Language development](https://BITPlan.github.io/com.bitplan.antlr) |

# Creator 
[![BITPlan](http://wiki.bitplan.com/images/wiki/thumb/3/38/BITPlanLogoFontLessTransparent.png/198px-BITPlanLogoFontLessTransparent.png)](http://www.bitplan.com)

[![Travis (.org)](https://img.shields.io/travis/BITPlan/com.bitplan.antlr.svg)](https://travis-ci.org/BITPlan/com.bitplan.antlr)

### Distribution
[Available via maven repository](https://search.maven.org/artifact/com.bitplan.antlr/com.bitplan.antlr/0.0.7/jar)

Maven dependency
```xml
<dependency>
  <groupId>com.bitplan.antlr</groupId>
  <artifactId>com.bitplan.antlr</artifactId>
  <version>0.0.7</version>
</dependency>
```
### How to build
```
git clone https://github.com/BITPlan/com.bitplan.antlr
cd com.bitplan.antlr
mvn install
```
### Version History
* 2017-10-13 0.0.1 - initial release
* 2017-10-14 0.0.2 - fixes #1 support for files from source directories
                     and #2 support for timeout check
* 2018-01-14 0.0.3 - make railroad-diagrammer dependency optional
* 2018-01-14 0.0.4 - upgrades to ANTLR 4.7.1 
* 2018-01-14 0.0.5 - include ANTLR tools 
* 2018-01-14 0.0.6 - use railroad diagrammer 0.2.0 (forked) 
* 2018-08-20 0.0.7 - use com.bitplan.pom parent pom

