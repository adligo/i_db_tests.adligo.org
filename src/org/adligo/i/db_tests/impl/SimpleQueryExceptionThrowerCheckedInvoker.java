package org.adligo.i.db_tests.impl;

import org.adligo.i.adi.shared.InvocationException;
import org.adligo.i.adig.shared.BaseGInvoker;
import org.adligo.i.adig.shared.I_GCheckedInvoker;
import org.adligo.i.db.I_Query;
import org.adligo.i.db.I_ReadOnlyConnection;
import org.adligo.i.db_tests.entities.KeyValue;

public class SimpleQueryExceptionThrowerCheckedInvoker extends BaseGInvoker 
		implements I_GCheckedInvoker<SimpleKeyRequest, KeyValue> {

	public SimpleQueryExceptionThrowerCheckedInvoker() {
		super(SimpleKeyRequest.class, KeyValue.class);
	}

	@Override
	public KeyValue invoke(SimpleKeyRequest valueObject)
			throws InvocationException {
		
		I_ReadOnlyConnection obtainer = valueObject.getReadOnlyConnection();
		I_Query query = obtainer.createNativeQuery("Select key, valllllueee FROM keyVal WHERE key = ?" ,KeyValue.class);
		query.setParameter(1, valueObject.getKey());
		return (KeyValue) query.getSingleResult();
	}
	
}
