@echo off
if "%1" == "" goto error
if "%2" == "" goto error
if "%3" == "" goto error
keytool -genkey -keyalg RSA -alias %1 -keystore %3 -storepass %2 -dname "CN=Game Prog Blog, OU=Game Prog Blog, O=Magnos, L=Mount Joy, ST=Pennsylvania, C=US" -validity 360
echo "Keystore successfully created!"
goto end
:error
echo "Usage: create_key.bat [User] [Password] [Keystore Filename]
:end