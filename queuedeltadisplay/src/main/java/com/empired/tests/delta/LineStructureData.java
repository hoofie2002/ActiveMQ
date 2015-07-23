package com.empired.tests.delta;

import java.awt.Color;

import javax.swing.JLabel;

public class LineStructureData {
	public JLabel lblCount;
	public JLabel lblQueueName;
	public JLabel lblDelta;
	public Color neutralColor;

	public LineStructureData(JLabel lblCount, JLabel lblQueueName,
			JLabel lblDelta, Color neutralColor) {
		this.lblCount = lblCount;
		this.lblQueueName = lblQueueName;
		this.lblDelta = lblDelta;
		this.neutralColor = neutralColor;
	}
}