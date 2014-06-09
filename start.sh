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
JAVA_ARGS=
TMP_SCRIPT_FILE="./__script.rss"

SCRIPT_NAME=`basename $0`
UNIX_STYLE_HOME=$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)/`basename "${BASH_SOURCE[0]}"`
UNIX_STYLE_HOME=`echo $UNIX_STYLE_HOME | sed "s/$SCRIPT_NAME//g"`

####
# MAIN
####

if [ -f $INPUT_FILE ]; then
	cat $INPUT_FILE | sed 's/#.*//g' > $TMP_SCRIPT_FILE
fi

while [ $# -gt 0 ]; do                   
	case "$1" in
	-debug)
		JAVA_ARGS=$JAVA_ARGS" -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n"
	;;
	*)
		if [[ $1 == -* ]]; then
			NAME=`echo $1 | tail -c +2`
			
			cat $TMP_SCRIPT_FILE | sed "s/\$$NAME/$2/g" > "tmp"
			mv "tmp" $TMP_SCRIPT_FILE
			
			shift
		fi
	;;
	esac
			
	shift
done

if [ -f "$INPUT_FILE" ]; then
	#prüfen ob alle Variablen ersetzt wurden...
	UNRESOLVED=`cat $TMP_SCRIPT_FILE | grep '\\$' | wc -l`
	if [ $UNRESOLVED -gt 0 ]; then
		echo "Es wurden nicht alle Variablen ersetzt!"
		cat -n $TMP_SCRIPT_FILE | grep '\$'
		exit 1
	fi
	
    echo "Skript wird ausgeführt: "$INPUT_FILE
	
	cat $TMP_SCRIPT_FILE | grep -v '^\s*$' | sed 's/^\s*//g' | java $JAVA_ARGS -jar $UNIX_STYLE_HOME/$JAR_NAME
else
	java $JAVA_ARGS -jar $UNIX_STYLE_HOME/$JAR_NAME
fi

rm $TMP_SCRIPT_FILE 2> /dev/null