@echo off
if "%1" == "" goto error
if "%2" == "" goto error
if "%3" == "" goto error
if "%4" == "" goto error
jarsigner -keystore %3 -storepass %2 -keypass %2 %4 %1
goto end
:error
echo "Usage: sign_jar.bat [User] [Password] [Keystore Path] [Jar Path]"
:end