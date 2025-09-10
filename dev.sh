#!/bin/bash

mavenOption="$1"

if [ "$mavenOption" == "--java" ]; then
    mvn clean compile tomcat9:run
elif [ "$mavenOption" == "--all" ]; then
    mvn clean install compile tomcat9:run
else
    mvn tomcat9:run
fi
