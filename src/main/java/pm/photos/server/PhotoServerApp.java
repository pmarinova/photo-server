package pm.photos.server;

import java.nio.file.Paths;

public class PhotoServerApp {
	
	public static void main(String[] args) {
		
		new PhotoServer()
			.withHost("localhost")
			.withPort(9090)
			.withPhotosPath(Paths.get("photos"))
			.start();
	}
}
