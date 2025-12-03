@echo off
setlocal enabledelayedexpansion
echo Compilando Sky Runner: Carrera en las Nubes...
echo.

rem Buscar Java en el sistema
for /f "tokens=*" %%i in ('where java 2^>nul') do (
    set "JAVA_PATH=%%~dpi"
    goto :found_java
)

:found_java
if defined JAVA_PATH (
    echo Java encontrado en: !JAVA_PATH!
    rem Intentar encontrar javac en la misma ubicación
    if exist "!JAVA_PATH!javac.exe" (
        set "JAVAC_CMD="!JAVA_PATH!javac.exe""
    ) else if exist "!JAVA_PATH!..\javac.exe" (
        set "JAVAC_CMD="!JAVA_PATH!..\javac.exe""
    ) else (
        echo No se encontró javac.exe, buscando en PATH...
        set "JAVAC_CMD=javac"
    )
) else (
    echo Usando javac del PATH...
    set "JAVAC_CMD=javac"
)

echo Compilador Java: !JAVAC_CMD!
echo.

rem Crear carpeta de compilación
if not exist "bin" mkdir bin

rem Compilar todas las clases Java
echo Compilando clases...
!JAVAC_CMD! --release 21 -d bin -cp . *.java

if !errorlevel! neq 0 (
    echo.
    echo ERROR: Falló la compilación
    echo.
    echo Posibles soluciones:
    echo 1. Asegúrate de que Java JDK esté instalado (no solo JRE)
    echo 2. Descarga Java JDK desde: https://adoptium.net/
    echo 3. Instala Java JDK y reinicia tu computadora
    pause
    exit /b 1
)

echo.
echo Compilación exitosa!
echo.

rem Crear archivo JAR ejecutable
echo Creando archivo JAR...
cd bin

rem Buscar jar.exe
if exist "!JAVA_PATH!jar.exe" (
    set "JAR_CMD="!JAVA_PATH!jar.exe""
) else if exist "!JAVA_PATH!..\jar.exe" (
    set "JAR_CMD="!JAVA_PATH!..\jar.exe""
) else (
    set "JAR_CMD=jar"
)

!JAR_CMD! cfe SkyRunner.jar MainMenu *.class

if !errorlevel! neq 0 (
    echo Error creando el archivo JAR
    cd ..
    pause
    exit /b 1
)

cd ..

echo.
echo Archivo JAR creado exitosamente!
echo.

rem Mover el JAR a la carpeta principal
if exist "bin\SkyRunner.jar" (
    move "bin\SkyRunner.jar" "SkyRunner.jar" >nul
echo JAR movido a la carpeta principal
)

echo.
echo ========================================
echo JUEGO COMPILADO EXITOSAMENTE
echo ========================================
echo.
echo Iniciando el menú principal...
java -jar SkyRunner.jar
echo.
echo Si tienes problemas para ejecutar:
echo   1. Asegúrate de tener Java instalado
echo   2. Prueba: java -version
echo   3. Si no funciona, instala Java desde: https://adoptium.net/
echo.
echo Controles:
echo   - Flechas: Mover la nave
echo   - Espacio: Disparar
echo   - ESC: Pausar el juego
echo.
echo ¡Disfruta de Sky Runner!
echo ========================================

pause
