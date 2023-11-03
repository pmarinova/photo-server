package pm.photos.server;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class PhotoServerJmDNS {

	private final String host;

	private final int port;

	private JmDNS jmdns;

	public PhotoServerJmDNS(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void start() throws IOException {
		InetAddress ipAddress = InetAddress.getByName(host);
		jmdns = ipAddress.isAnyLocalAddress() ? JmDNS.create() : JmDNS.create(ipAddress);
		String serviceType = "_photo-server._tcp.local";
		String serviceName = jmdns.getInetAddress().getHostName();
		String serviceDescription = "Photo server service";
		ServiceInfo serviceInfo = ServiceInfo.create(serviceType, serviceName, port, serviceDescription);
		jmdns.registerService(serviceInfo);
	}

	public void stop() throws IOException {
		if (this.jmdns != null) {
			jmdns.unregisterAllServices();
			jmdns.close();
		}
	}
}
