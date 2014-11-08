#!/bin/bash

OUT_DIR="$SQUANDER_HOME/spec/squander/specfile/parser"

java -cp $ANTLR_JAR org.antlr.Tool -o $OUT_DIR SpecFile.g

sed -i '/^public class.*/ i\
@SuppressWarnings({"unused"})' $OUT_DIR/SpecFileParser.java $OUT_DIR/SpecFileLexer.java