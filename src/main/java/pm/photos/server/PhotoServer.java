package pm.photos.server;

import java.nio.file.Path;
import java.util.logging.Logger;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

public class PhotoServer {
	
	private static final Logger LOGGER = Logger.getLogger(PhotoServer.class.getName());

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
		
		HttpHandler rootHandler = Handlers.path()
				.addPrefixPath("/photos/list", new PhotoListHandler(this.photosPath))
				.addPrefixPath("/photos", newPhotoResourceHandler(this.photosPath));
		
		HttpHandler logHandler = new AccessLogHandler(rootHandler,
				message -> LOGGER.info(message), "common", PhotoServer.class.getClassLoader()); 
		
		this.undertow = Undertow.builder()
			.addHttpListener(this.port, this.host)
			.setHandler(logHandler)
			.build();
		
		this.undertow.start();
	}
	
	public void stop() {
		this.undertow.stop();
	}
	
	private ResourceHandler newPhotoResourceHandler(Path photosPath) {
		ResourceHandler handler = new ResourceHandler(new PathResourceManager(photosPath));
		handler.setDirectoryListingEnabled(true);
		return handler;
	}
}
