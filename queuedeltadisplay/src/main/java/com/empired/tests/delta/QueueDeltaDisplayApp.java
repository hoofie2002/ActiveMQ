package com.empired.tests.delta;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.management.MBeanServerConnection;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

public class QueueDeltaDisplayApp {

	private Logger logger = Logger.getLogger(QueueDeltaDisplayApp.class.getSimpleName());
	
	private static final String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
	private static final String TOPIC_JMX_SEARCH = "org.apache.activemq:type=Broker,brokerName=*,destinationType=Topic,destinationName=*";
	private static final String QUEUE_JMX_SEARCH = "org.apache.activemq:type=Broker,brokerName=*,destinationType=Queue,destinationName=*";
	//private static final String QUEUE_JMX_SEARCH = "org.apache.activemq:type=Broker,brokerName=amq,destinationType=Queue,*";
	JFrame frame = new JFrame(); // creates frame
	JLabel headerInfo = null;
	JPanel leftPanel = null;
	JPanel rightPanel = null;

	List<LineStructure> qLines = new ArrayList<LineStructure>();
	List<LineStructure> tLines = new ArrayList<LineStructure>();
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	List<QueueInfo> prevQueueInfo = null;
	List<QueueInfo> prevTopicInfo = null;
	QueueUtilities utils = new QueueUtilities();
	MBeanServerConnection conn = null;

	public List<QueueInfo> getQueueData() throws Exception {
		Map<String, Integer> qDepthMap = utils.dumpQueueNames(this.conn,
				QUEUE_JMX_SEARCH);
		return formatQueueInfo(qDepthMap);

	}

	public List<QueueInfo> getTopicData() throws Exception {
		Map<String, Integer> tDepthMap = utils.dumpQueueNames(this.conn,
				TOPIC_JMX_SEARCH);
		return formatQueueInfo(tDepthMap);

	}

	public List<QueueInfo> formatQueueInfo(Map<String, Integer> queues) {
		List<QueueInfo> info = new ArrayList<QueueInfo>();
		Iterator<String> it2 = queues.keySet().iterator();
		while (it2.hasNext()) {
			String serviceName = (String) it2.next();
			int queueDepth = queues.get(serviceName);
			String queueName = (Utils.extractSName(serviceName));
			int delta = 0;
			info.add(new QueueInfo(queueDepth, queueName, delta));
		}
		return info;
	}

	public QueueDeltaDisplayApp(String serverName, String port) throws Exception { // constructor

		setUpFrame(serverName);
		
		this.conn = utils.connect(serverName, port);

		
		setupDataDisplay(leftPanel, getQueueData(), qLines);
		
		setupDataDisplay(rightPanel, getTopicData(), tLines);
		
		drawQueueData(getQueueData());
		drawTopicData(getTopicData());

		frame.setSize(800, 1000);
		frame.setLocation(30, 30);
		frame.setVisible(true); // makes frame visible
		frame.addWindowListener(new MyWindowListener());
		
		ActionListener taskPerformer = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					drawQueueData(getQueueData());
					drawTopicData(getTopicData());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		new Timer(5000, taskPerformer).start();
		
	}

	public void setUpFrame(String serverName) {
		MigLayout masterLayout = new MigLayout("wrap 2", "[500][500]", "[top][top]");
		frame.setLayout(masterLayout); // set layout

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack(); // sets appropriate size for frame
		frame.setTitle("Active MQ Display - " + serverName);

		Font boldFont = new Font("Verdana", Font.BOLD, 12);
		
		leftPanel = new JPanel();
		MigLayout leftLayout = new MigLayout("wrap 3",
				"[50][60][50]", "");
		leftPanel.setLayout(leftLayout);
		leftPanel.setBackground(Color.BLACK);
		leftPanel.setBorder(LineBorder.createGrayLineBorder());
		JLabel qHeader = new JLabel("Queues",JLabel.CENTER);
		qHeader.setForeground(Color.WHITE);
		qHeader.setBorder(LineBorder.createGrayLineBorder());
		qHeader.setFont(boldFont);
		leftPanel.add("width ::80, span 3", qHeader);

		rightPanel = new JPanel();
		MigLayout rightLayout = new MigLayout("wrap 3",
				"[50][60][50]", "");
		rightPanel.setLayout(rightLayout);
		rightPanel.setBackground(Color.BLACK);
		rightPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		rightPanel.setBorder(LineBorder.createGrayLineBorder());
		JLabel tHeader = new JLabel("Topics",JLabel.CENTER);
		tHeader.setBorder(LineBorder.createGrayLineBorder());
		tHeader.setForeground(Color.WHITE);
		tHeader.setFont(boldFont);
		rightPanel.add("span 3", tHeader);

		headerInfo = new JLabel("Connecting to Server : " + serverName, SwingConstants.CENTER);
		headerInfo.setForeground(Color.WHITE);
		headerInfo.setBorder(LineBorder.createGrayLineBorder());
		frame.add(headerInfo, "span 2");
		frame.add(leftPanel);
		frame.add(rightPanel, "wrap");
		frame.getContentPane().setBackground(Color.BLACK);
	}

	public void setupDataDisplay(JPanel panel, List<QueueInfo> data, List<LineStructure> lines) throws Exception {
		for (QueueInfo qi : data) {
			LineStructure ls = new LineStructure(qi.getCount(),
					qi.getQueueName(), qi.getDelta());
			panel.add(ls.getQueueName()); // adds button to grid
			panel.add(ls.getCount()); // adds button to grid
			panel.add(ls.getDelta(), "wrap");
			lines.add(ls);
		}
	}

	public void exit() {
		try {
			logger.info("...disconnecting..");
			this.utils.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void drawQueueData(final List<QueueInfo> currentInfo) throws Exception {

		//final List<QueueInfo> currentInfo = getQueueData();

		// Store Previous Data
		if (this.prevQueueInfo != null) {
			// OK need to calculate deltas
			for (int x = 0; x < currentInfo.size(); x++) {
				QueueInfo newInfo = currentInfo.get(x);
				QueueInfo oldInfo = this.prevQueueInfo.get(x);
				if (newInfo.getCount() != oldInfo.getCount()) {
					int delta = newInfo.getCount() - oldInfo.getCount();
					newInfo.setDelta(delta);
				}
			}
		}
		// Set into old data
		this.prevQueueInfo = currentInfo;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int i = 0;
				for (QueueInfo qi : currentInfo) {
					LineStructure ls = QueueDeltaDisplayApp.this.qLines.get(i);
					ls.setCount(qi.getCount());
					ls.setQueueName(qi.getQueueName());
					ls.setDelta(qi.getDelta());
					i++;
				}
				QueueDeltaDisplayApp.this.headerInfo.setText(sdf.format(Calendar
						.getInstance().getTime()));
			}
		});
	}

	public void drawTopicData(final List<QueueInfo> currentInfo) throws Exception {

		//final List<QueueInfo> currentInfo = getQueueData();

		// Store Previous Data
		if (this.prevTopicInfo != null) {
			// OK need to calculate deltas
			for (int x = 0; x < currentInfo.size(); x++) {
				QueueInfo newInfo = currentInfo.get(x);
				QueueInfo oldInfo = this.prevTopicInfo.get(x);
				if (newInfo.getCount() != oldInfo.getCount()) {
					int delta = newInfo.getCount() - oldInfo.getCount();
					newInfo.setDelta(delta);
				}
			}
		}
		// Set into old data
		this.prevTopicInfo = currentInfo;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int i = 0;
				for (QueueInfo qi : currentInfo) {
					LineStructure ls = QueueDeltaDisplayApp.this.tLines.get(i);
					ls.setCount(qi.getCount());
					ls.setQueueName(qi.getQueueName());
					ls.setDelta(qi.getDelta());
					i++;
				}
			}
		});
	}
	
	public static void main(String[] args) throws Exception {

		String serverName = null;
		String port = "1099";

		if (args.length < 1) {
			System.out.println("Need to specify Server IP and port");
			System.exit(-1);
		}

		serverName = args[0];

		if (args.length == 2) {
			port = args[1];
		}

		new QueueDeltaDisplayApp(serverName, port);// makes new ButtonGrid with 2
											// parameters
	}

	public class MyWindowListener implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(WindowEvent e) {
			exit();
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}
	}

}
