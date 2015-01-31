package cn.nju.edu.winews.crawler.data.exception;

import java.io.IOException;

public class MongoIOException extends IOException {

	private static final long serialVersionUID = 1963043266672745125L;

	public MongoIOException() {
		super();
	}

	public MongoIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public MongoIOException(String message) {
		super(message);
	}

	public MongoIOException(Throwable cause) {
		super(cause);
	}

	
}
