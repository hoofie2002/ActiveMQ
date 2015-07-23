package com.empired.tests.delta;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.QueueView;
import org.apache.activemq.broker.jmx.TopicView;

public class QueueUtilities {

	private Logger logger = Logger.getLogger(QueueUtilities.class.getSimpleName());

	public String JMX_SERVICE_URL = null;
	private MBeanServerConnection serverConnection;
	private JMXConnector jmxc;
		
	public MBeanServerConnection connect(String ipAddress, String port) {

		/* Fuse Connector */
		//JMX_SERVICE_URL = "service:jmx:rmi:///jndi/rmi://" + ipAddress + ":" + port + "/karaf-root";

		/* ActiveMQ Connector */
		JMX_SERVICE_URL = "service:jmx:rmi:///jndi/rmi://" + ipAddress + ":" + port + "/jmxrmi";

		final Map<String, Object> props = new HashMap<String, Object>();
		/**
		 * Adding Credentials
		 */
		final String[] credentials = new String[] { "admin" , "admin123!"};
		props.put("jmx.remote.credentials", credentials);
		JMXServiceURL url;
		try {
			url = new JMXServiceURL(JMX_SERVICE_URL);
			this.jmxc = JMXConnectorFactory.connect(url, props);
			serverConnection = this.jmxc.getMBeanServerConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get an MBeanServerConnection
		//
		
		return serverConnection;
	}
	
	public void disconnect() throws IOException {
		this.jmxc.close();
	}
	
	public Map<String, Integer> dumpQueueNames(MBeanServerConnection serverConnection, String query) throws MalformedObjectNameException, NullPointerException, IOException, InstanceNotFoundException {
		Set<ObjectName> result = serverConnection.queryNames(new ObjectName(query), null);
		
		Map<String, Integer> names = new TreeMap<String, Integer>();
		
		// Iterate Through Results
		Iterator<ObjectName> it = result.iterator();
		while (it.hasNext()) {
			ObjectName foundName = (ObjectName)it.next();
			String name = foundName.getCanonicalName();
			logger.log(Level.INFO, "Found Queue:" + name);
			if (serverConnection.isInstanceOf(foundName, QueueView.class.getName()) || serverConnection.isInstanceOf(foundName, TopicView.class.getName())) {
				int count = this.countMessagesInObject(serverConnection, name);
				names.put(name, count);
			}
		}
		return names;
	}
	
	private int countMessagesInObject(MBeanServerConnection serverConnection,
			String queueName) {

		int msgCount = 0;

		Object enqueue = null;
		Object dequeue = null;
		try {
			enqueue = serverConnection.getAttribute(new ObjectName(
					queueName), "EnqueueCount");
			dequeue = serverConnection.getAttribute(new ObjectName(
					queueName), "DequeueCount");
		} catch (Exception e) {
				msgCount = -999;
		}
		if (enqueue != null && dequeue != null) {
			msgCount = Integer.parseInt(enqueue.toString()) - Integer.parseInt(dequeue.toString());
		}

		return msgCount;
	}

	
	
}
