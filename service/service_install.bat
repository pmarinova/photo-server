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
--StopMethod=stop ^
--LogPath="%~dp0logs" ^
--LogPrefix="photo-server" ^
--LogLevel=Debug ^
--StdOutput="%~dp0logs\stdout.log" ^
--StdError="%~dp0logs\stderr.log" ^
++StartParams="path_to_photos_dir"

  
pause