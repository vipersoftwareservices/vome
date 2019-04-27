@echo off 
 
java -Xmx1024M -jar "%~dp0/../lib/vome.jar" --config "%~dp0/../etc/databases.xml" %*