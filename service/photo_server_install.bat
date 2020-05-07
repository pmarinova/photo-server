copy ..\target\server-1.0.jar photo_server.jar

photo_server.exe ^
//IS//PhotoServer ^
--Install="%~dp0photo_server.exe" ^
--Jvm=auto ^
--Classpath="%~dp0photo_server.jar" ^
--Startup=auto ^
--StartMode=jvm ^
--StartClass=pm.photos.server.PhotoServerApp ^
--StartMethod=start ^
--StopMode=jvm ^
--StopClass=pm.photos.server.PhotoServerApp ^
--StopMethod=stop

  
pause