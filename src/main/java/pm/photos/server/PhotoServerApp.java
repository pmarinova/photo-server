package pm.photos.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class PhotoServerApp {
	
	private static Logger LOGGER = configureLogger();
	
	private static JmDNS jmdns;
	private static PhotoServer server;
	
	public static void main(String[] args) {
		start(args);
	}
	
	public static void start(String[] args) {
		
		Path photosPath = (args.length > 0) ?
				Paths.get(args[0]) : Paths.get("photos");
				
		try {
			final String host = "0.0.0.0";
			final int port = 40003;
			
			String serviceType = "_photoserver._tcp.local";
			String serviceName = InetAddress.getLocalHost().getHostName();
			String serviceDescription = "Photo server service";
			ServiceInfo serviceInfo = ServiceInfo.create(serviceType, serviceName, port, serviceDescription);
			jmdns = JmDNS.create(InetAddress.getByName(host));
			jmdns.registerService(serviceInfo);
			
			server = new PhotoServer(host, port);
			server.setPhotosPath(photosPath);
			server.start();
		}
		catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			stop(args);
		}
	}
	
	public static void stop(String[] args) {
		try {
			jmdns.unregisterAllServices();
			jmdns.close();
			server.stop();
		}
		catch (IOException e) {
			e.printStackTrace();
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
