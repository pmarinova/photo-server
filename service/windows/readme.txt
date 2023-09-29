To install the photo server service run the following command:
> photo-server-winsw.exe install

To start/stop the photo server:
> photo-server-winsw.exe start/stop

To uninstall the photo server service:
> photo-server-winsw.exe uninstall


To set the path to the photos directory you need to edit the file 'photo-server-winsw.xml'.
Uncomment the 'arguments' tag and specify the photos path by setting the '--photos_dir' option.