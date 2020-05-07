package pm.photos.server;

public class PhotoServerApp {
	
	private static PhotoServer server;
	
	public static void main(String[] args) {
		if (args.length == 0 || "start".equals(args[0])) {
			start(args);
		} else if ("stop".equals(args[0])) {
			stop(args);
		}
	}
	
	public static void start(String[] args) {
		server = new PhotoServer("localhost", 9090);
		server.start();
	}
	
	public static void stop(String[] args) {
		server.stop();
	}
}
