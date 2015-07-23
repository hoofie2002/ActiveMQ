package com.empired.tests.delta;

class QueueInfo {
	private int count = 0;
	private String queueName = null;
	private int delta = 0;

	public QueueInfo(int count, String queueName, int delta) {
		super();
		this.count = count;
		this.queueName = queueName;
		this.delta = delta;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

}
