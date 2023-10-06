package pm.photos.server;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhotoServerApp {
	
	static {
		System.setProperty("org.jboss.logging.provider", "slf4j");
	}
	
	private static Logger LOGGER = LoggerFactory.getLogger(PhotoServerApp.class);

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
				LOGGER.info("Shutting down...");
				photoServer.stop();
			}));
			
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
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
			LOGGER.info(String.format("Serving photos from path '%s'", photosPath.toAbsolutePath()));
			
			server = new PhotoServer(serverHost, serverPort);
			server.setPhotosPath(photosPath);
			server.start();
			LOGGER.info(String.format("Server started at %s:%d", serverHost, serverPort));
			
			jmdns = new PhotoServerJmDNS(serverHost, serverPort);
			jmdns.start();
			LOGGER.info("mDNS service started");
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	void stop() {
		try {
			if (jmdns != null) {
				jmdns.stop();
				LOGGER.info("mDNS service stopped");
			}
			
			if (server != null) {
				server.stop();
				LOGGER.info("Server stopped");
			}
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
