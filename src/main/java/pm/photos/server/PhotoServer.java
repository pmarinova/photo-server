package pm.photos.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import io.undertow.Handlers;
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
		
		Undertow.builder()
			.addHttpListener(this.port, this.host)
			.setHandler(Handlers.path()
				.addPrefixPath("/photos/list", new PhotoListHandler(this.photosPath, getBaseURL()))
				.addPrefixPath("/photos", newPhotoResourceHandler(this.photosPath))
			)
			.build()
			.start();
	}
	
	private URL getBaseURL() {
		try {
			return new URL("http", this.host, this.port, "");
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
