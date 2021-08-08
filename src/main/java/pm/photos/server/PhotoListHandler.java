package pm.photos.server;

import java.io.File;
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
	
	public PhotoListHandler(Path photosPath) {
		this.photosPath = photosPath;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		
		List<String> photos = Files.walk(this.photosPath)
				.filter(this::isImageFile)
				.map(this::getRelativePath)
				.collect(Collectors.toList());
		
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
		exchange.getResponseSender().send(GSON.toJson(photos));
	}

	private boolean isImageFile(Path path) {
		File file = path.toFile();
		return file.isFile() && file.getName().matches("(?i).*\\.jpg$");
	}
	
	private String getRelativePath(Path path) {
		Path relativePath = this.photosPath.relativize(path);
		return relativePath.toString().replace("\\", "/");
	}
}