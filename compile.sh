#!/bin/bash

source bench/env.sh

find -name "*.java" | xargs javac -cp $CP -d bin