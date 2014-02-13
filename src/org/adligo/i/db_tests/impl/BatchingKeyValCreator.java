package org.adligo.i.db_tests.impl;

import java.util.List;

import org.adligo.i.adi.shared.InvocationException;
import org.adligo.i.adig.shared.BaseGInvoker;
import org.adligo.i.adig.shared.GRegistry;
import org.adligo.i.adig.shared.I_GCheckedInvoker;
import org.adligo.i.db_tests.entities.KeyValue;

public class BatchingKeyValCreator extends BaseGInvoker implements I_GCheckedInvoker<BatchCreateKeyValuesRequest, Integer>{
	private static final I_GCheckedInvoker<CreateKeyValueRequest, Boolean> KEY_VALUE_CREATOR =
		GRegistry.getCheckedInvoker(MockDbCheckedInvokerNames.CREATE_KEY_VALUE_IN_TRANSACTION,
		CreateKeyValueRequest.class, Boolean.class);
	
	public BatchingKeyValCreator() {
		super(BatchCreateKeyValuesRequest.class, Integer.class);
	}

	@Override
	public Integer invoke(BatchCreateKeyValuesRequest valueObject)
			throws InvocationException {
		
		List<KeyValue> values = valueObject.getItems();
		int count = 0;
		for (KeyValue keyVal: values) {
			CreateKeyValueRequest req = new CreateKeyValueRequest();
			req.setItem(keyVal);
			req.passValues(valueObject);
			if (KEY_VALUE_CREATOR.invoke(req)) {
				count++;
			}
		}
		return count;
	}
}
