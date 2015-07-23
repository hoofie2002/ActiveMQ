package com.empired.tests.delta;

import java.util.Iterator;
import java.util.Map;

public class Utils {

	
	public static int findLargestValue(Map<String, Integer> queues) {
		// Find Largest [top one]
		Iterator<Integer> it = queues.values().iterator();
		int max = 0;
		while (it.hasNext()) {
			int value = (Integer)it.next();
			if (value > max) {
				max = value;
			}
		}
		return max;
	}
	
	public static void outputBar(int size) {
		for (int i =0; i < size; i++) {
			System.out.print("=");
		}
		System.out.println();

	}
	
	public static int findLongestString(Map<String, Integer> queues) {
		// Find Largest [top one]
		Iterator<String> it = queues.keySet().iterator();
		int max = 0;
		while (it.hasNext()) {
			String name = extractSName((String)it.next());
			if (name.length()> max) {
				max = name.length();
			}
		}
		return max;
	}
	
	
	
	public static String[][] generateHistogram(Map<String, Integer> queues) {
		String[][] results = new String[queues.size()][3];
		int y = 0;
		int max = Utils.findLargestValue(queues);
		int longest = Utils.findLongestString(queues);
		int factor = max/(130 -8 - longest);
		Iterator<String> it2 = queues.keySet().iterator();
		while (it2.hasNext()) {
			System.out.println(y);
			String serviceName = (String)it2.next();
			int queueDepth = queues.get(serviceName);
			if (queueDepth == -999) {
				results[y][0] = "-Error-";
			} else {
				results[y][0] = String.valueOf(queueDepth);
			}
			results[y][1] = (extractSName(serviceName));
			if (factor > 0 && queueDepth > 0) {
				results[y][2] = new String();
				for (int i =0; i <= (queueDepth/ factor); i++) {
					results[y][2] = results[y][2].concat("*");
				}
			}
			y++;
		}		
		return results;
	}
	
	public static void drawHistogram(Map<String, Integer> queues) {
		int max = Utils.findLargestValue(queues);
		int longest = Utils.findLongestString(queues);
		int factor = max/(130 -8 - longest);
		Iterator<String> it2 = queues.keySet().iterator();
		while (it2.hasNext()) {
			String serviceName = (String)it2.next();
			int queueDepth = queues.get(serviceName);
			if (queueDepth == -999) {
				System.out.print("-Error-");
			} else {
				System.out.format("%07d", queueDepth);
			}
			System.out.print(" | ");
			System.out.print(String.format("%-" + (longest + 1) + "s | ", extractSName(serviceName)));
			if (factor > 0 && queueDepth > 0) {
				for (int i =0; i <= (queueDepth/ factor); i++) {
					System.out.print("*");
				}
			}
			System.out.println();
		}
	}
	
	public static String extractSName(String qName) {
		int start = qName.indexOf("destinationName=");
		int end = qName.indexOf("destinationType=");
		return qName.substring(start+16, end);
	}
	
	
}
