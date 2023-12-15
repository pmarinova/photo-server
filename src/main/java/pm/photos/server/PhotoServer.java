package pm.photos.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;

public class PhotoServer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PhotoServer.class.getName());
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");
	
	private final Filter logFilter = Filter.afterHandler("logger", (e) -> {
		LOGGER.info(String.format("%s - - [%s] \"%s %s %s\" %d %s", 
				e.getRemoteAddress().getHostString(),
				OffsetDateTime.now().format(FORMATTER),
				e.getRequestMethod(), 
				e.getRequestURI(),
				e.getProtocol(),
				e.getResponseCode(),
				e.getResponseHeaders().getFirst("Content-length")));
	});

	private final String host;
	
	private final int port;
	
	private Path photosPath;
	
	private HttpServer httpServer;
	private ExecutorService threadPool;
	
	public PhotoServer(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public Path getPhotosPath() {
		return this.photosPath;
	}
	
	public void setPhotosPath(Path photosPath) {
		this.photosPath = photosPath.toAbsolutePath();
	}
	
	public void start() throws IOException {
		var socketAddress = new InetSocketAddress(host, port);
		this.httpServer = HttpServer.create(socketAddress, 0);
		
		addHandler("/photos", SimpleFileServer.createFileHandler(this.photosPath));
		addHandler("/photos/list", new PhotoListHandler(this.photosPath));
		
		this.threadPool = Executors.newFixedThreadPool(3);
		this.httpServer.setExecutor(this.threadPool);
		this.httpServer.start();
	}
	
	public void stop() {
		this.httpServer.stop(0);
		this.threadPool.shutdown();
	}
	
	private HttpContext addHandler(String path, HttpHandler handler) {
		var ctx = this.httpServer.createContext(path, handler);
		ctx.getFilters().add(this.logFilter);
		return ctx;
	}
}
