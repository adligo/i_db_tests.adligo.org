package org.adligo.i.db.impl;

import org.adligo.i.adi.client.InvocationException;
import org.adligo.i.adig.client.BaseGInvoker;
import org.adligo.i.adig.client.I_GCheckedInvoker;
import org.adligo.i.db.I_Query;
import org.adligo.i.db.I_ReadOnlyConnection;
import org.adligo.i.db.entities.KeyValue;

public class SimpleQueryCheckedInvoker extends BaseGInvoker 
		implements I_GCheckedInvoker<SimpleKeyRequest, KeyValue> {

	public SimpleQueryCheckedInvoker() {
		super(SimpleKeyRequest.class, KeyValue.class);
	}

	@Override
	public KeyValue invoke(SimpleKeyRequest valueObject)
			throws InvocationException {
		
		I_ReadOnlyConnection obtainer = valueObject.getReadOnlyConnection();
		I_Query query = obtainer.createNativeQuery("Select key, val FROM keyVal WHERE key = ?" ,KeyValue.class);
		query.setParameter(1, valueObject.getKey());
		return (KeyValue) query.getSingleResult();
	}
	
}
