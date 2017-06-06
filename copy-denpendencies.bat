@echo off
title copy-dependencies %cd%
color 0a
echo delete runtime jar.
cd /d %~dp0
del src\main\webapp\WEB-INF\lib\*.jar
::echo update releases and snapshots.
::cmd /c "mvn clean -U"
echo copy-dependencies.
cmd /c "mvn dependency:copy-dependencies -DoutputDirectory=src/main/webapp/WEB-INF/lib"
echo done.
pause