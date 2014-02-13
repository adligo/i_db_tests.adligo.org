package org.adligo.i.db_tests.impl;

import java.util.List;

import org.adligo.i.adi.shared.InvocationException;
import org.adligo.i.adig.shared.BaseGInvoker;
import org.adligo.i.adig.shared.GRegistry;
import org.adligo.i.adig.shared.I_GCheckedInvoker;
import org.adligo.i.db_tests.entities.KeyValue;

public class BatchingExceptionThrowerKeyValCreator extends BaseGInvoker implements I_GCheckedInvoker<BatchCreateKeyValuesRequest, Integer>{
	private static final I_GCheckedInvoker<CreateKeyValueRequest, Boolean> KEY_VALUE_CREATOR =
		GRegistry.getCheckedInvoker(MockDbCheckedInvokerNames.CREATE_KEY_VALUE_IN_TRANSACTION_EXCEPTION_THROWER,
		CreateKeyValueRequest.class, Boolean.class);
	
	public BatchingExceptionThrowerKeyValCreator() {
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
