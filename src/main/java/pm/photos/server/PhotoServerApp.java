package pm.photos.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class PhotoServerApp {
	
	private static JmDNS jmdns;
	private static PhotoServer server;
	
	public static void main(String[] args) {
		start(args);
	}
	
	public static void start(String[] args) {
		
		Path photosPath = (args.length > 0) ?
				Paths.get(args[0]) : Paths.get("photos");
				
		try {
			final InetAddress address = getDefaultLocalAddress();
			final String host = address.getHostAddress();
			final int port = 9090;
			
			String serviceType = "_photoserver._tcp.local";
			String serviceName = InetAddress.getLocalHost().getHostName();
			String serviceDescription = "Photo server service";
			ServiceInfo serviceInfo = ServiceInfo.create(serviceType, serviceName, port, serviceDescription);
			jmdns = JmDNS.create(address);
			jmdns.registerService(serviceInfo);
			
			server = new PhotoServer(host, port);
			server.setPhotosPath(photosPath);
			server.start();
		}
		catch (IOException e) {
			e.printStackTrace();
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
	
	/*
	 *  Returns the host local address associated with the preferred network interface.
	 *  
	 *  The problem is that a host could have lots of network interfaces, and an interface
	 *  could be bound to more than one IP address. And to top that, not all IP addresses 
	 *  will be reachable outside of your machine or your LAN. For example, they could be
	 *  IP addresses for virtual network devices, private network IP addresses, and so on.
	 *  See https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
	 */
	private static InetAddress getDefaultLocalAddress() throws IOException {
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			return socket.getLocalAddress();
		}
	}
}
