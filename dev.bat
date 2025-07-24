@REM run command with multiple option using mvn using custom args

echo off

set mavenOption=%1

if "%mavenOption%" == "--java" (
    mvn clean compile tomcat9:run
) else if "%mavenOption%" == "--all" (
    mvn clean install compile tomcat9:run
) else (
    mvn tomcat9:run
)