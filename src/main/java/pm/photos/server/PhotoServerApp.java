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
	
	private static PhotoServerJmDNS jmdns;
	private static PhotoServer server;
	
	public static void main(String[] args) {
		start(args);
	}
	
	public static void start(String[] args) {
		
		Option optPhotosDir = Option
				.builder("d")
				.longOpt("photos_dir")
				.required(true)
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
			String photosDir = cmdLine.getOptionValue(optPhotosDir.getOpt());
			String serverHost = cmdLine.getOptionValue(optServerHost.getOpt(), "0.0.0.0");
			int serverPort = Integer.parseInt(cmdLine.getOptionValue(optServerPort.getOpt(), "40003"));
			
			startServer(photosDir, serverHost, serverPort);
			
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			new HelpFormatter().printHelp("photo-server", options, true);
			System.exit(1);
		}
	}
	
	public static void startServer(String photosDir, String host, int port) {
		
		Path photosPath = Paths.get(photosDir);
				
		try {
			server = new PhotoServer(host, port);
			server.setPhotosPath(photosPath);
			server.start();
			LOGGER.log(Level.INFO, String.format("Server started at %s:%d", host, port));
			
			jmdns = new PhotoServerJmDNS(host, port);
			jmdns.start();
			LOGGER.log(Level.INFO, "mDNS service started");
		}
		catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			stopServer();
		}
	}
	
	public static void stop(String[] args) {
		stopServer();
	}
	
	public static void stopServer() {
		try {
			jmdns.stop();
			LOGGER.log(Level.INFO, "mDNS service stopped");
			
			server.stop();
			LOGGER.log(Level.INFO, "Server stopped");
		}
		catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private static Logger configureLogger() {
		try (InputStream config = PhotoServerApp.class.getResourceAsStream("/logging.properties")) {
			LogManager.getLogManager().readConfiguration(config);
			return Logger.getLogger(PhotoServerApp.class.getName());
		} catch (IOException e) {
			throw new RuntimeException("Failed to configure logger", e);
		}
	}
}
