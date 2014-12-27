package cn.edu.nju.unicorn.entity;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;

public class URLSet {
//	private static URLSet instance = null;
	private HashSet<String> hasFetched = new HashSet<String>();
	private LinkedList<URL> waitForFetching = new LinkedList<URL>();

//	public static URLSet getInstance() {
//		return instance == null ? instance = new URLSet() : instance;
//	}

	public void addUrl(URL u) {
		if(!hasFetched.contains(u.toString())) {
			waitForFetching.add(u);
		} else {
			System.out.println("This url has already fetched.");
		}
		
	}

	public synchronized URL getUrl() throws EmptySetException {
		if (!waitForFetching.isEmpty()) {
			URL u = waitForFetching.getFirst();
			waitForFetching.remove(0);
			hasFetched.add(u.toString());
			return u;
		} else {
			throw new EmptySetException();
		}
	}
	
	public boolean isEmpty() {
		return waitForFetching.isEmpty();
	}
}
