@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo ========================================================
echo Iniciando MultiMedia Digital - Spring Boot (Java 21)
echo ========================================================
call mvnw.cmd spring-boot:run
pause
