#!/bin/sh
@REM 2> /dev/null; # START_OF_SHELL_SCRIPT
@REM 2> /dev/null; #
@REM 2> /dev/null; if true; then #
@REM 2> /dev/null;     echo "ola" #
@REM 2> /dev/null; exit 1; #
@REM 2> /dev/null; fi #
@REM 2> /dev/null; #
@REM 2> /dev/null; #
@REM 2> /dev/null; cat > /dev/null << END_OF_BATCH
@REM END_OF_SHELL_SCRIPT

@REM START_OF_BATCH
@ECHO OFF

SETLOCAL

if defined JAVA_HOME (
    GOTO execution
)

::- Get the Java Version
set KEY="HKLM\SOFTWARE\JavaSoft\Java Development Kit"
set VALUE=CurrentVersion
reg query %KEY% /v %VALUE% >nul 2>nul || (
    echo JDK not installed 
    exit /b 1
)
set JDK_VERSION=
for /f "tokens=2,*" %%a in ('reg query %KEY% /v %VALUE% ^| findstr %VALUE%') do (
    set JDK_VERSION=%%b
)

REM echo JDK VERSION: %JDK_VERSION%

::- Get the JavaHome
set KEY="HKLM\SOFTWARE\JavaSoft\Java Development Kit\%JDK_VERSION%"
set VALUE=JavaHome
reg query %KEY% /v %VALUE% >nul 2>nul || (
    echo JavaHome not installed
    exit /b 1
)

set JAVA_HOME=
for /f "tokens=2,*" %%a in ('reg query %KEY% /v %VALUE% ^| findstr %VALUE%') do (
    set JAVA_HOME=%%b
)

:execution
set EXTRA_ARGS=-locale en_US -encoding UTF-8 -quiet -source 8
echo.
echo.
echo.
echo Java Home: %JAVA_HOME%
echo All args: %*
echo Extra args: %EXTRA_ARGS%
echo.
echo.
echo.
"%JAVA_HOME%/bin/javadoc.exe" %EXTRA_ARGS% %*
ENDLOCAL & EXIT /B %ERRORLEVEL% 

EXIT /B 123
END_OF_BATCH
