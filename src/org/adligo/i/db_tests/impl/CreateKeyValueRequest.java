package org.adligo.i.db_tests.impl;

import org.adligo.i.db.ModifyRequest;
import org.adligo.i.db_tests.entities.KeyValue;

public class CreateKeyValueRequest extends ModifyRequest {
	private KeyValue item;

	public KeyValue getItem() {
		return item;
	}

	public void setItem(KeyValue item) {
		this.item = item;
	}
	
}
