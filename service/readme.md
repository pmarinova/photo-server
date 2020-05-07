PhotoServer Windows service
===========================

The PhotoServer can be installed as a Windows service using the Apache Commons Daemon binaries.

To install the PhotoServer service:
1. Build the project with **mvn clean package**
2. Copy **target/server-1.0.jar** to **service/PhotoServer.jar**
3. From the **service** directory, run **service_install.bat**

To uninstall the PhotoServer service:
1. Stop the service
2. Run **service/service_uninstall.bat**

Double-click **PhotoServerw.exe** to manage the service after it is installed.



Apache Commons Daemon binaries
==============================

Downloaded from http://commons.apache.org/daemon/index.html. Binaries:
* PhotoServer.exe (commons-daemon-1.2.2-bin-windows.zip/amd64/prunsrv.exe)
* PhotoServerw.exe (commons-daemon-1.2.2-bin-windows.zip/prunmgr.exe)


How to update the PhotoServer icon
==================================

To personalize the PhotoServer binaries, the embedded icon was updated with PhotoServer.ico.
To change the icon, update PhotoServer.ico and follow the steps below:

1. Download WinRun4J from http://winrun4j.sourceforge.net/
2. Extract winrun4J-0.4.5.zip to winrun4J-0.4.5
3. Run the following commands to update the icons:

winrun4j\bin\RCEDIT.exe /I PhotoServer.exe PhotoServer.ico
winrun4j\bin\RCEDIT.exe /I PhotoServerw.exe PhotoServer.ico


Useful Links
============

* Apache Commons Daemon docs - https://commons.apache.org/proper/commons-daemon/procrun.html
* Procrun blog post - https://joerglenhard.wordpress.com/2012/05/29/build-windows-service-from-java-application-with-procrun/
* Procrun sample - https://github.com/lenhard/procrun-sample