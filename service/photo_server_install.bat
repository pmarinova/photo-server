copy ..\target\server-1.0.jar photo_server.jar

photo_server.exe ^
//IS//PhotoServer ^
--Install="%~dp0photo_server.exe" ^
--Jvm=auto --Startup=auto --StartMode=jvm ^
--Classpath="%~dp0photo_server.jar" ^
--StartClass=pm.photos.server.PhotoServerApp
  
pause