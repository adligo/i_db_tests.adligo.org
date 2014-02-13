package org.adligo.i.db_tests.impl;

import org.adligo.i.db.SelectRequest;

public class SimpleKeyRequest extends SelectRequest {
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
