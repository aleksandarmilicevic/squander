#!/bin/bash

OUT_DIR="$SQUANDER_HOME/src/edu/mit/csail/sdg/squander/parser"

java -cp $ANTLR_JAR org.antlr.Tool -o $OUT_DIR JFSL.g

sed -i '/^public class.*/ i\
@SuppressWarnings({"all"})' $OUT_DIR/JFSLParser.java $OUT_DIR/JFSLLexer.java

rm -rf output
