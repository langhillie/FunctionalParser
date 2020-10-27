#!/bin/sh

# Compile source
javac scanner.java && javac parser.java && java scanner | java parser
 
