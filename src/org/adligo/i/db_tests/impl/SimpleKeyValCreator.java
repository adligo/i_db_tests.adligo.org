package org.adligo.i.db_tests.impl;

import org.adligo.i.adi.shared.InvocationException;
import org.adligo.i.adig.shared.BaseGInvoker;
import org.adligo.i.adig.shared.I_GCheckedInvoker;
import org.adligo.i.db.I_ReadWriteConnection;
import org.adligo.i.db_tests.entities.KeyValue;

public class SimpleKeyValCreator extends BaseGInvoker implements I_GCheckedInvoker<CreateKeyValueRequest, Boolean> {

	public SimpleKeyValCreator() {
		super(CreateKeyValueRequest.class, Boolean.class);
	}

	@Override
	public Boolean invoke(CreateKeyValueRequest valueObject)
			throws InvocationException {
		I_ReadWriteConnection em = valueObject.getReadWriteConnection();
		KeyValue kv = valueObject.getItem();
		em.persist(kv);
		return true;
	}
}
