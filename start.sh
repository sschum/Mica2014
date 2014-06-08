#!/bin/bash
#
# Dieses Script startet die runnable-jar-file.
# Zusätzlich kann man noch direkt eine Datei übergeben.
# Der Inhalt der Datei wird dann als Kommandoeingabe für
# Die Anwendung benutzt. 
#
# Diese "Skriptdateien" können Kommentare in folgender Form beinhalten:
#
# ...
# #Dies ist ein Kommentar
# befehlX #Dies ist ein Kommentar
# befelY
# ...
#

####
# VARIABLES
####

INPUT_FILE=$1
JAR_NAME="RayShip.jar"

SCRIPT_NAME=`basename $0`
UNIX_STYLE_HOME=$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)/`basename "${BASH_SOURCE[0]}"`
UNIX_STYLE_HOME=`echo $UNIX_STYLE_HOME | sed "s/$SCRIPT_NAME//g"`

####
# MAIN
####

if [ -z "$INPUT_FILE" ]; then
    java -jar $UNIX_STYLE_HOME/$JAR_NAME
else
	echo "Skript wird ausgeführt: "$INPUT_FILE
	
	cat $INPUT_FILE | sed 's/#.*//g' | grep -v '^\s*$' | sed 's/^\s*//g' | java -jar $UNIX_STYLE_HOME/$JAR_NAME
fi
