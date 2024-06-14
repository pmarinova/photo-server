package pm.photos.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class PhotoServerJmDNS {

	private final String host;

	private final int port;

	private JmDNS jmdns;
	
	private NetworkInterface networkInterface;

	public PhotoServerJmDNS(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void start() throws IOException {
		InetAddress ipAddress = InetAddress.getByName(host);
		jmdns = ipAddress.isAnyLocalAddress() ? JmDNS.create() : JmDNS.create(ipAddress);
		String serviceType = "_photo-server._tcp.local";
		String serviceName = getHostName();
		
		networkInterface = NetworkInterface.getByInetAddress(jmdns.getInetAddress());
		byte[] macAddress = networkInterface.getHardwareAddress();
		
		ServiceInfo serviceInfo = ServiceInfo.create(serviceType, serviceName, port, "MAC=" + formatMACAddress(macAddress));
		jmdns.registerService(serviceInfo);
	}

	public void stop() throws IOException {
		if (this.jmdns != null) {
			jmdns.unregisterAllServices();
			jmdns.close();
		}
	}
	
	public NetworkInterface getNetworkInterface() {
		return this.networkInterface;
	}
	
	private String getHostName() {
		String hostname = System.getenv("COMPUTERNAME"); // Windows
		if (hostname != null)
			return hostname;
		
        hostname = System.getenv("HOSTNAME"); // Unix/Linux
        if (hostname != null)
        	return hostname;
        
        try {
        	return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        	return "UNKNOWN_HOST";
        }
	}
	
	private static String formatMACAddress(byte[] mac) {
		if (mac.length != 6) {
			throw new IllegalArgumentException("Invalid MAC address: " + Arrays.toString(mac));
		}
		return String.format("%02x:%02x:%02x:%02x:%02x:%02x",
				mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
	}
}
