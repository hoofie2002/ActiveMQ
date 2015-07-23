package com.empired.tests.delta;

import java.awt.Color;

import javax.swing.JLabel;

class LineStructure {
	LineStructureData data = new LineStructureData(null, null, null,
			Color.WHITE);

	public LineStructure(int count, String queueName, int delta) {
		this.data.lblCount = new JLabel(String.valueOf(count));
		this.data.lblCount.setHorizontalAlignment(JLabel.RIGHT);
		this.data.lblCount.setVerticalAlignment(JLabel.TOP);
		this.data.lblCount.setSize(50, 10);
		this.data.lblCount.setOpaque(true);

		this.data.lblQueueName = new JLabel(queueName);
		this.data.lblQueueName.setHorizontalAlignment(JLabel.LEFT);
		this.data.lblQueueName.setVerticalAlignment(JLabel.TOP);
		this.data.lblQueueName.setSize(100, 10);
		this.data.lblQueueName.setOpaque(true);

		this.data.lblDelta = new JLabel(formatDelta(delta));
		data.lblDelta.setForeground(data.neutralColor);
		this.data.lblDelta.setHorizontalAlignment(JLabel.LEFT);
		this.data.lblDelta.setVerticalAlignment(JLabel.TOP);
		this.data.lblDelta.setOpaque(true);
	}

	public void setCount(int value) {
		data.lblCount.setText(String.valueOf(value));
	}

	public void setQueueName(String value) {
		data.lblQueueName.setText(value);
	}

	public void setDelta(int value) {
		data.lblDelta.setText(formatDelta(value));
		if (value == 0) {
			data.lblDelta.setForeground(data.neutralColor);
			data.lblQueueName.setForeground(data.neutralColor);
			data.lblCount.setForeground(data.neutralColor);
			data.lblDelta.setBackground(Color.BLACK);
			data.lblQueueName.setBackground(Color.BLACK);
			data.lblCount.setBackground(Color.BLACK);
		}
		if (value > 0) {
			data.lblDelta.setForeground(Color.BLACK);
			data.lblCount.setForeground(Color.BLACK);
			data.lblQueueName.setForeground(Color.BLACK);
			data.lblDelta.setBackground(Color.RED);
			data.lblCount.setBackground(Color.RED);
			data.lblQueueName.setBackground(Color.RED);
		}
		if (value < 0) { 
			data.lblDelta.setForeground(Color.BLACK);
			data.lblQueueName.setForeground(Color.BLACK);
			data.lblCount.setForeground(Color.BLACK);
			data.lblDelta.setBackground(Color.GREEN);
			data.lblQueueName.setBackground(Color.GREEN);
			data.lblCount.setBackground(Color.GREEN);
		}
	}

	private String formatDelta(int value) {
		if (value == 0)
			return "--";
		
		if (value > 0)
			return "+"+value;
		
		return ""+value;
	}

	public JLabel getCount() {
		return this.data.lblCount;
	}

	public JLabel getQueueName() {
		return this.data.lblQueueName;
	}

	public JLabel getDelta() {
		return this.data.lblDelta;
	}

	public void repaint() {
		this.data.lblCount.repaint();
		this.data.lblQueueName.repaint();
		this.data.lblDelta.repaint();
	}

}
