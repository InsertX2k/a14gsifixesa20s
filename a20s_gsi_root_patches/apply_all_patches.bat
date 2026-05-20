@echo off
:init
set CURRENT_DIRECTORY=%~dp0
:main
cls
title Apply A20s GSI Patches for A14+ TD GSI
echo ####################################
echo # A20s GSI Patches for A14+ TD GSI #
echo ####################################
echo Current Directory or GSI Root: %CURRENT_DIRECTORY%
echo.
echo Stage 1 - Applying SELinux Patch...
echo.
rem Copy SELinux patch script to CURRENT_DIRECTORY/system/etc/selinux
copy /V /Y "%CURRENT_DIRECTORY%\apply_se_patch.py" "%CURRENT_DIRECTORY%\system\etc\selinux\apply_se_patch.py"
rem Prop Patches python script file will remain in the current directory
rem Run Apply SELinux Patch script file
pushd "%CURRENT_DIRECTORY%\system\etc\selinux"
python "%CURRENT_DIRECTORY%\system\etc\selinux\apply_se_patch.py"
echo.
echo Stage 2 - Applying Build Props Patch...
echo.
popd
python "%CURRENT_DIRECTORY%\apply_prop_patches.py"
echo.
if ERRORLEVEL 0 ( echo Successfully applied patches. ) else ( echo Failed to apply some patches... )
:cleanup
del /F /Q /A "%CURRENT_DIRECTORY%\system\etc\selinux\apply_se_patch.py"
del /F /Q /A "%CURRENT_DIRECTORY%\apply_prop_patches.py"
del /F /Q /A "%CURRENT_DIRECTORY%\apply_se_patch.py"
echo Attempting to delete current script file, if it isn't deleted automatically, Please delete it manually...
del /F /Q /A "%CURRENT_DIRECTORY%\%0"


pause
exit /b 0

