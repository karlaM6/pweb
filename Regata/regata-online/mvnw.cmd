@REM Maven Wrapper startup script for Windows
@ECHO OFF
setlocal
set ERROR_CODE=0

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set MAVEN_PROJECTBASEDIR=%DIRNAME%

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

if exist %WRAPPER_JAR% (
  rem jar exists
) else (
  echo Downloading Maven Wrapper jar...
  powershell -NoProfile -Command "$ProgressPreference='SilentlyContinue'; $u='%DOWNLOAD_URL%'; $d='%WRAPPER_JAR%'.Replace('\"',''); New-Item -ItemType Directory -Force -Path (Split-Path $d) | Out-Null; Invoke-WebRequest -Uri $u -OutFile $d"
)

set JAVA_EXE=java
if defined JAVA_HOME set JAVA_EXE="%JAVA_HOME%\bin\java.exe"

%JAVA_EXE% -classpath %WRAPPER_JAR% %WRAPPER_LAUNCHER% %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
endlocal & exit /b %ERROR_CODE%
