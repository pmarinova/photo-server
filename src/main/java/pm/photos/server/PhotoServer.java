package pm.photos.server;

import java.nio.file.Path;

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

public class PhotoServer {
	
	private int port;

	private String host;
	
	private Path photosPath;
	
	public PhotoServer withPort(int port) {
		this.port = port;
		return this;
	}
	
	public PhotoServer withHost(String host) {
		this.host = host;
		return this;
	}
	
	public PhotoServer withPhotosPath(Path photosPath) {
		this.photosPath = photosPath;
		return this;
	}
	
	public void start() {
		
		ResourceHandler photosHandler = new ResourceHandler(new PathResourceManager(this.photosPath));
		photosHandler.setDirectoryListingEnabled(true);
		
		Undertow.builder()
			.addHttpListener(this.port, this.host)
			.setHandler(photosHandler)
			.build()
			.start();
	}
}
