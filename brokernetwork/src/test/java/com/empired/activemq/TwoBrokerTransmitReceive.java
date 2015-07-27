package com.empired.activemq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TwoBrokerTransmitReceive {

	/**
	 * The test will send NUMBER_OF_MSGS_TO_SEND to Broker 1
	 * and then retrieve them from broker 2
	 * The message ID's are checked to ensure it is the same message
	 */
	
	Logger logger = Logger.getLogger(TwoBrokerTransmitReceive.class.getSimpleName());

	private static final int NUMBER_OF_MSGS_TO_SEND = 2;
	private static final String DUPLICATED_QUEUE_NAME = "Q.REMOTE.DUPLICATE";
	private static final String TCP_ENDPOINT_BROKER_1 = "tcp://10.15.2.10:61616";
	private static final String TCP_ENDPOINT_BROKER_2 = "tcp://10.15.2.11:61616";
	private static final String USERNAME = "jboss";
	private static final String PASSWORD = "jboss";

	private ActiveMQConnectionFactory connectionFactory1;
	private ActiveMQConnectionFactory connectionFactory2;
	private Connection connection1;
	private Connection connection2;
	private Session session1;
	private Session session2;
	private List<String> msgIdList = new ArrayList<String>();
	private boolean isSecure=true;
	


	@Before
	public void setUp() throws Exception {
		connectionFactory1 = new ActiveMQConnectionFactory(
				TCP_ENDPOINT_BROKER_1);
		if (isSecure) {
			connection1 = connectionFactory1.createConnection(USERNAME, PASSWORD);
		} else {
			connection1 = connectionFactory1.createConnection();
		}
		connection1.start();
		session1 = connection1.createSession(false, Session.AUTO_ACKNOWLEDGE);

		connectionFactory2 = new ActiveMQConnectionFactory(
				TCP_ENDPOINT_BROKER_2);
		if (isSecure) {
			connection2 = connectionFactory2.createConnection(USERNAME, PASSWORD);
		} else {
			connection2 = connectionFactory2.createConnection();
		}

		connection2.start();
		session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	@After
	public void tearDown() throws Exception {
		// Clean up
		session1.close();
		connection1.close();
		session2.close();
		connection2.close();

		session1 = null;
		connection1 = null;
		connectionFactory1 = null;
		session2 = null;
		connection2 = null;
		connectionFactory2 = null;
	}

	@Test
	public <T> void shouldSendMessagesToBroker1ReceiveOnBroker2() {

		try {

			// Create the destination (Topic or Queue)
			Destination sendQ = session1.createQueue(DUPLICATED_QUEUE_NAME);

			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session1.createProducer(sendQ);
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

			// Sleep for 3 Seconds
			Thread.sleep(3000);

			// Now Receive
			// Create the destination (Topic or Queue)
			Destination receiveQ = session2.createQueue(DUPLICATED_QUEUE_NAME);
			// Create a MessageProducer from the Session to the Topic or Queue
			MessageConsumer consumer = session2.createConsumer(receiveQ);
			boolean processed= false;
			int numReceived = 0;
			while(!processed) {
				TextMessage msg = (TextMessage) consumer.receive();
				logger.info("Receiving Message with JMS Message ID ["
						+ msg.getJMSMessageID() + "]");
				if (this.msgIdList.contains(msg.getJMSMessageID())) {
					numReceived++;
				}
				// Look for Correct Count
				if (numReceived == NUMBER_OF_MSGS_TO_SEND) {
					processed = true;
				}
			}
			logger.info("Closing Received - all messages received");
			consumer.close();
			
			assertEquals(numReceived, NUMBER_OF_MSGS_TO_SEND);

		} catch (Exception e) {
			fail("Failed due to exception [" + e.getLocalizedMessage() + "]");
		}
	}

	private TextMessage generateMessage() throws JMSException {
		TextMessage message = session1.createTextMessage();
		message.setText("NEW MESSAGE");
		return message;
	}

}
