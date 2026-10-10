@echo off
setlocal enabledelayedexpansion

echo ==============================================================================
echo   Generating Self-Signed PKCS12 Keystore for VR Therapy WebXR (HTTPS)
echo ==============================================================================

rem Detect all active non-loopback LAN IPv4 addresses
for /f "usebackq tokens=*" %%a in (`powershell -NoProfile -Command "$ips = (Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.InterfaceAlias -notmatch 'Loopback' -and $_.IPAddress -notmatch '^169\.254' -and $_.IPAddress -notmatch '^127\.' } | Select-Object -ExpandProperty IPAddress); $list = @('127.0.0.1', '192.168.0.237'); if ($ips) { $list += $ips }; $sanParts = @('dns:localhost'); foreach ($ip in ($list | Select-Object -Unique)) { $sanParts += ('ip:' + $ip) }; $sanParts -join ','"`) do (
    set SAN=%%a
)

for /f "usebackq tokens=*" %%a in (`powershell -NoProfile -Command "(Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.InterfaceAlias -notmatch 'Loopback' -and $_.IPAddress -notmatch '^169\.254' -and $_.IPAddress -notmatch '^127\.' } | Select-Object -ExpandProperty IPAddress | Select-Object -First 1)"`) do (
    set PRIMARY_IP=%%a
)

if "%SAN%"=="" (
    set SAN=dns:localhost,ip:127.0.0.1,ip:192.168.0.237
)
if "%PRIMARY_IP%"=="" (
    set PRIMARY_IP=192.168.0.237
)

echo [INFO] Detected Primary LAN IP: %PRIMARY_IP%
echo [INFO] Subject Alternative Names (SAN): %SAN%

set KEYSTORE_NAME=vrtherapy.p12
set KEYSTORE_PASS=vrtherapy123
set ALIAS=vrtherapy

rem Ensure src/main/resources directory exists
if not exist "src\main\resources" mkdir "src\main\resources"

rem Remove old keystore files if present
if exist "src\main\resources\%KEYSTORE_NAME%" del /f /q "src\main\resources\%KEYSTORE_NAME%"
if exist "%KEYSTORE_NAME%" del /f /q "%KEYSTORE_NAME%"

echo [INFO] Running keytool to generate RSA 2048-bit certificate valid for 365 days...
keytool -genkeypair ^
    -alias %ALIAS% ^
    -keyalg RSA ^
    -keysize 2048 ^
    -validity 365 ^
    -keystore "src\main\resources\%KEYSTORE_NAME%" ^
    -storetype PKCS12 ^
    -storepass %KEYSTORE_PASS% ^
    -keypass %KEYSTORE_PASS% ^
    -dname "CN=VR Therapy Platform, OU=Telehealth Rehabilitation, O=Connected VR, L=Academic, ST=Virtual, C=US" ^
    -ext "SAN=%SAN%"

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] keytool failed to generate keystore. Check Java JDK installation.
    exit /b %ERRORLEVEL%
)

rem Copy to workspace root and target/classes for zero-configuration pickup
copy /y "src\main\resources\%KEYSTORE_NAME%" "%KEYSTORE_NAME%" >nul
if exist "target\classes" copy /y "src\main\resources\%KEYSTORE_NAME%" "target\classes\%KEYSTORE_NAME%" >nul

echo ==============================================================================
echo [SUCCESS] Keystore generated successfully:
echo           - src\main\resources\%KEYSTORE_NAME%
echo           - %KEYSTORE_NAME%
echo.
echo   Keystore Password: %KEYSTORE_PASS%
echo   Certificate Alias: %ALIAS%
echo.
echo   Local Access:       https://localhost:8443/
echo   Phone/Quest Access: https://%PRIMARY_IP%:8443/
echo ==============================================================================
