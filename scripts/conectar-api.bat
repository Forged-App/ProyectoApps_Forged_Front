@echo off
setlocal
title Conectar app Forged con la API

rem Crea el tunel entre el emulador/celular y la API que corre en este PC.
rem Ejecuta este archivo DESPUES de iniciar la API y el emulador.
rem Si reinicias el emulador o desconectas el celular, vuelve a ejecutarlo.

set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
if not exist "%ADB%" set "ADB=adb"

echo ============================================
echo  Conectando la app con la API Forged
echo  API:  http://127.0.0.1:8080  (tu PC)
echo ============================================
echo.

"%ADB%" devices
echo.

"%ADB%" reverse --remove-all >nul 2>&1
"%ADB%" reverse tcp:8080 tcp:8080

if errorlevel 1 (
    echo.
    echo No se pudo crear el tunel.
    echo Verifica que el emulador o el celular aparezcan como "device" arriba.
) else (
    echo.
    echo Tunel listo. Ya puedes abrir la app e iniciar sesion.
)

echo.
pause
