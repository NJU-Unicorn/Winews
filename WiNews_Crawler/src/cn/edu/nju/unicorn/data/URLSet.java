package cn.edu.nju.unicorn.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;

public class URLSet {
	private static URLSet instance = null;
	private HashSet<String> hasFetched = new HashSet<String>();
	private LinkedList<String> waitForFetching = new LinkedList<String>();

	public static URLSet getInstance() {
		return instance == null ? instance = new URLSet() : instance;
	}

	private URLSet() {

	}

	public void addUrl(String u) {
		if (hasFetched.contains(u)) {
			// System.out.println("This url has already fetched.");
		} else if (waitForFetching.contains(u)) {
			// System.out.println("This url waiting for fetcing.");
		} else {
			// System.out.println("Add: " + u);
			waitForFetching.add(u);
		}

	}

	public synchronized URL getUrl() throws EmptySetException,
			MalformedURLException {
		if (!waitForFetching.isEmpty()) {
			URL u = new URL(waitForFetching.getFirst());
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
