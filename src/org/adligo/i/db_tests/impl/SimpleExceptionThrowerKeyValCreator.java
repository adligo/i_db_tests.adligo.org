package org.adligo.i.db_tests.impl;

import org.adligo.i.adi.shared.InvocationException;
import org.adligo.i.adig.shared.BaseGInvoker;
import org.adligo.i.adig.shared.I_GCheckedInvoker;
import org.adligo.i.db.I_ReadWriteConnection;
import org.adligo.i.db.I_ReadWriteQuery;

public class SimpleExceptionThrowerKeyValCreator extends BaseGInvoker implements I_GCheckedInvoker<CreateKeyValueRequest, Boolean> {

	public SimpleExceptionThrowerKeyValCreator() {
		super(CreateKeyValueRequest.class, Boolean.class);
	}

	@Override
	public Boolean invoke(CreateKeyValueRequest valueObject)
			throws InvocationException {
		I_ReadWriteConnection em = valueObject.getReadWriteConnection();
		I_ReadWriteQuery query = em.createNativeQueryForModify("update keyVal SET tid = 123");
		query.executeUpdate();
		return true;
	}
}
