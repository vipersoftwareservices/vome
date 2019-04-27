#!/bin/sh

BASEDIR=$(dirname "$0") 
java -Xmx1024M -jar "$BASEDIR/../lib/vome.jar" --config "$BASEDIR/../etc/databases.xml"