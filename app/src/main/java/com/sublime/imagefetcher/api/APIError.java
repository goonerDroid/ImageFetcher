package com.sublime.imagefetcher.api;

import java.io.Serializable;

public class APIError implements Serializable {

	private String error;

	public APIError() {
		this.error = null;
	}

	public APIError(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
