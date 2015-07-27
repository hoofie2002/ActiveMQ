package com.empired.activemq;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FailoverTransmission {

	
	/**
	 * Test will use the failover: protocol to send messages to a distributed queue
	 * Run the test with Broker 1 shut down and then with Broker 2 shut down
	 * In both cases the test will run and when you restart the broker, the messages
	 * will be available in the queue
	 * NOTE:Messages will not be transferred to a remote queue until a consumer exists
	 */
	
	Logger logger = Logger
			.getLogger(FailoverTransmission.class.getSimpleName());

	private static final int NUMBER_OF_MSGS_TO_SEND = 2;
	private static final String DUPLICATED_QUEUE_NAME = "Q.REMOTE.FAILOVER";
	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private List<String> msgIdList = new ArrayList<String>();
	private static final String TCP_ENDPOINT_BROKER_1 = "tcp://10.15.2.10:61616";
	private static final String TCP_ENDPOINT_BROKER_2 = "tcp://10.15.2.11:61616";
	private boolean isSecure=true;

	private static final String USERNAME = "jboss";
	private static final String PASSWORD = "jboss";


	@Before
	public void setUp() throws Exception {
		connectionFactory = new ActiveMQConnectionFactory("failover:("
				+ TCP_ENDPOINT_BROKER_1 + "," + TCP_ENDPOINT_BROKER_2 + ")");
		if (isSecure) {
			connection = connectionFactory.createConnection(USERNAME, PASSWORD);
		} else {
			connection = connectionFactory.createConnection();
		}
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	@After
	public void tearDown() throws Exception {
		// Clean up
		session.close();
		connection.close();

		session = null;
		connection = null;
		connectionFactory = null;
	}

	@Test
	public <T> void shouldSendMessageToFailover() {

		try {

			// Create the destination (Topic or Queue)
			Destination sendQ = session.createQueue(DUPLICATED_QUEUE_NAME);

			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session.createProducer(sendQ);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			for (int i = 0; i < NUMBER_OF_MSGS_TO_SEND; i++) {
				TextMessage msg = generateMessage();
				producer.send(msg);
				msgIdList.add(msg.getJMSMessageID());
				logger.info("Sending Message with JMS Message ID ["
						+ msg.getJMSMessageID() + "]");
			}

			producer.send(generateMessage());
			producer.close();

		} catch (Exception e) {
			fail("Failed due to exception [" + e.getLocalizedMessage() + "]");
		}
	}

	private TextMessage generateMessage() throws JMSException {
		TextMessage message = session.createTextMessage();
		message.setText("NEW MESSAGE");
		return message;
	}

}
