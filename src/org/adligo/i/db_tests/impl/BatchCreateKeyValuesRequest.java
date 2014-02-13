package org.adligo.i.db_tests.impl;

import java.util.List;

import org.adligo.i.db.ModifyRequest;
import org.adligo.i.db_tests.entities.KeyValue;

public class BatchCreateKeyValuesRequest extends ModifyRequest {
	private List<KeyValue> items;

	public List<KeyValue> getItems() {
		return items;
	}
	public void setItems(List<KeyValue> items) {
		this.items = items;
	}

	
}
