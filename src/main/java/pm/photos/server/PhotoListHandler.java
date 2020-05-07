package pm.photos.server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class PhotoListHandler implements HttpHandler {
	
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting().create();
	
	private final Path photosPath;
	
	private final URL baseURL;
	
	public PhotoListHandler(Path photosPath, URL baseURL) {
		this.photosPath = photosPath;
		this.baseURL = baseURL;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		
		List<URL> photos = Files.walk(this.photosPath)
				.filter(this::isImageFile)
				.map(this::getURL)
				.collect(Collectors.toList());
		
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
		exchange.getResponseSender().send(GSON.toJson(photos));
	}

	private boolean isImageFile(Path path) {
		File file = path.toFile();
		return file.isFile() && file.getName().endsWith(".jpg");
	}
	
	private URL getURL(Path path) {
		try {
			return new URL(this.baseURL, path.toString().replace("\\", "/"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
