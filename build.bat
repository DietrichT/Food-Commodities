@echo off
rem Rebuilds jars/FoodCommodities.jar from src/.
rem Needs a JDK 17 on PATH (Starsector 0.98 runs on Java 17).
setlocal
cd /d "%~dp0"

set API=..\..\starsector-core\starfarer.api.jar
set LOG4J=..\..\starsector-core\log4j-1.2.9.jar

if not exist out\classes mkdir out\classes
if not exist jars mkdir jars

dir /s /b src\*.java > out\sources.txt
javac -encoding UTF-8 --release 17 -cp "%API%;%LOG4J%" -d out\classes @out\sources.txt
if errorlevel 1 goto :error

jar cf jars\FoodCommodities.jar -C out\classes .
if errorlevel 1 goto :error

echo Built jars\FoodCommodities.jar
goto :eof

:error
echo BUILD FAILED
exit /b 1
