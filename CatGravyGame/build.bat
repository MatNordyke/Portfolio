@echo off
javac -d build src/*.java
java -cp build GameLauncher
pause
