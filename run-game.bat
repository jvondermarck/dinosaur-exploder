@echo off
set MAVEN_HOME=%USERPROFILE%\tools\apache-maven-3.9.15
set PATH=%MAVEN_HOME%\bin;%PATH%
cd /d "%~dp0"

echo Elige modo:
echo   1) Ventana de escritorio (por defecto)
echo   2) Navegador en http://localhost:8080
set /p mode="Opcion (1 o 2): "

if "%mode%"=="2" (
    echo Abriendo en navegador...
    mvn jpro:run
) else (
    echo Abriendo ventana de escritorio...
    mvn javafx:run
)
pause
