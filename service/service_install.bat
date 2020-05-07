copy ..\target\server-1.0.jar PhotoServer.jar

PhotoServer.exe ^
//IS ^
--Install="%~dp0PhotoServer.exe" ^
--DisplayName="Photo Server" ^
--Jvm=auto ^
--Classpath="%~dp0PhotoServer.jar" ^
--Startup=auto ^
--StartMode=jvm ^
--StartClass=pm.photos.server.PhotoServerApp ^
--StartMethod=start ^
--StopMode=jvm ^
--StopClass=pm.photos.server.PhotoServerApp ^
--StopMethod=stop

  
pause