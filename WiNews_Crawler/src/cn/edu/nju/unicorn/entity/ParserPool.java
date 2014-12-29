package cn.edu.nju.unicorn.entity;

import java.util.ArrayList;

public class ParserPool {
	public static final int DEFAULT_THREAD_LIMITS = 3;
	private static ParserPool instance = null;
	private ArrayList<Thread> l = new ArrayList<Thread>();
	private int threadLimits = DEFAULT_THREAD_LIMITS;

	private ParserPool() {

	}

	public static ParserPool getInstance() {
		return instance == null ? instance = new ParserPool() : instance;
	}

	public synchronized void registerThread(Thread t) {
		while(l.size() > threadLimits) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		l.add(t);
	}
	
	public void closeThread(Thread t) {
		if(t.isAlive()) {
			t.interrupt();
		}
		l.remove(t);
	}
}
