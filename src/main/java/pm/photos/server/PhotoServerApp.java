package pm.photos.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class PhotoServerApp {
	
	private static Logger LOGGER = configureLogger();
	
	private static Logger configureLogger() {
		// See https://stackoverflow.com/a/13825590
        System.setProperty("java.util.logging.manager", CustomLogManager.class.getName());
		try (InputStream config = PhotoServerApp.class.getResourceAsStream("/logging.properties")) {
			LogManager.getLogManager().readConfiguration(config);
			return Logger.getLogger(PhotoServerApp.class.getName());
		} catch (IOException e) {
			throw new RuntimeException("Failed to configure logger", e);
		}
	}
	
	public static class CustomLogManager extends LogManager {
        static CustomLogManager instance;
        public CustomLogManager() { instance = this; }
        @Override public void reset() { /* don't reset yet. */ }
        private void reset0() { super.reset(); }
        public static void resetFinally() { instance.reset0(); }
    }

	private final Path photosPath;
	private final String serverHost;
	private final int serverPort;
	
	private PhotoServer server;
	private PhotoServerJmDNS jmdns;
	
	public static void main(String[] args) {
		
		Option optPhotosDir = Option
				.builder("d")
				.longOpt("photos_dir")
				.hasArg(true)
				.argName("PHOTOS_DIR")
				.desc("path to the photos directory which will be served")
				.build();
		
		Option optServerHost = Option
				.builder("h")
				.longOpt("host")
				.hasArg(true)
				.argName("HOST")
				.desc("server host address")
				.build();
		
		Option optServerPort = Option
				.builder("p")
				.longOpt("port")
				.hasArg(true)
				.argName("PORT")
				.desc("server port")
				.build();
		
		Options options = new Options();
		options.addOption(optPhotosDir);
		options.addOption(optServerHost);
		options.addOption(optServerPort);
		
		try {
			CommandLine cmdLine = new DefaultParser().parse(options, args);
			String photosDir = cmdLine.getOptionValue(optPhotosDir.getOpt(), "photos");
			String serverHost = cmdLine.getOptionValue(optServerHost.getOpt(), "0.0.0.0");
			int serverPort = Integer.parseInt(cmdLine.getOptionValue(optServerPort.getOpt(), "40003"));
			
			PhotoServerApp photoServer = new PhotoServerApp(Paths.get(photosDir), serverHost, serverPort);
			photoServer.start();
			
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				LOGGER.log(Level.INFO, "Shutting down...");
				try { photoServer.stop(); }
				finally { CustomLogManager.resetFinally(); }
			}));
			
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			new HelpFormatter().printHelp("photo-server", options, true);
			System.exit(1);
		}
	}
	
	PhotoServerApp(Path photosPath, String serverHost, int serverPort) {
		this.photosPath = photosPath;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
	}
	
	void start() {
		try {
			LOGGER.log(Level.INFO, String.format("Serving photos from path '%s'", photosPath.toAbsolutePath()));
			
			server = new PhotoServer(serverHost, serverPort);
			server.setPhotosPath(photosPath);
			server.start();
			LOGGER.log(Level.INFO, String.format("Server started at %s:%d", serverHost, serverPort));
			
			jmdns = new PhotoServerJmDNS(serverHost, serverPort);
			jmdns.start();
			LOGGER.log(Level.INFO, "mDNS service started");
		}
		catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	void stop() {
		try {
			if (jmdns != null) {
				jmdns.stop();
				LOGGER.log(Level.INFO, "mDNS service stopped");
			}
			
			if (server != null) {
				server.stop();
				LOGGER.log(Level.INFO, "Server stopped");
			}
		}
		catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
