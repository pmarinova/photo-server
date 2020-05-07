package pm.photos.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

public class PhotoServer {

	private final String host;
	
	private final int port;
	
	private Path photosPath;
	
	private Undertow undertow;
	
	public PhotoServer (String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public Path getPhotosPath() {
		return this.photosPath;
	}
	
	public void setPhotosPath(Path photosPath) {
		this.photosPath = photosPath;
	}
	
	public void start() {
		
		this.undertow = Undertow.builder()
			.addHttpListener(this.port, this.host)
			.setHandler(Handlers.path()
				.addPrefixPath("/photos/list", new PhotoListHandler(this.photosPath, getURL("/photos")))
				.addPrefixPath("/photos", newPhotoResourceHandler(this.photosPath))
			).build();
		
		this.undertow.start();
	}
	
	public void stop() {
		this.undertow.stop();
	}
	
	private URL getURL(String file) {
		try {
			return new URL("http", this.host, this.port, file);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private ResourceHandler newPhotoResourceHandler(Path photosPath) {
		ResourceHandler handler = new ResourceHandler(new PathResourceManager(photosPath));
		handler.setDirectoryListingEnabled(true);
		return handler;
	}
}
