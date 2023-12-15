package pm.photos.server;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class PhotoListHandler implements HttpHandler {
	
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting().create();
	
	private final Path photosPath;
	
	public PhotoListHandler(Path photosPath) {
		this.photosPath = photosPath;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		List<String> photos = Files.walk(this.photosPath)
				.filter(this::isImageFile)
				.map(this::getRelativePath)
				.collect(Collectors.toList());
		send(exchange, GSON.toJson(photos));
	}
	
	private void send(HttpExchange exchange, String response) throws IOException {
		var bytes = response.getBytes(UTF_8);
		exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
		exchange.sendResponseHeaders(200, bytes.length);
		try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
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
