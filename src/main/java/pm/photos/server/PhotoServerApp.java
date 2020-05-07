package pm.photos.server;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PhotoServerApp {
	
	private static PhotoServer server;
	
	public static void main(String[] args) {
		start(args);
	}
	
	public static void start(String[] args) {
		
		Path photosPath = (args.length > 0) ?
				Paths.get(args[0]) : Paths.get("photos");
		
		server = new PhotoServer("localhost", 9090);
		server.setPhotosPath(photosPath);
		server.start();
	}
	
	public static void stop(String[] args) {
		server.stop();
	}
}
