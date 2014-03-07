package org.adligo.i.db_tests.impl;

import javax.persistence.EntityManagerFactory;

import org.adligo.i.adig.shared.GRegistry;
import org.adligo.i.db.DbCheckedInvokerNames;
import org.adligo.i.db.DbConnectionProvider;
import org.adligo.i.db.DbMethodWrappers;

public class MockDbRegistry {


	@SuppressWarnings("unchecked")
	public static void setup(String name, EntityManagerFactory emf, EntityManagerFactory emfWrite) {
		
		
		DbConnectionProvider provider = DbConnectionProvider.create(emf, emfWrite, name);
		
		
		GRegistry.addOrReplaceCheckedInvoker(DbCheckedInvokerNames.STORAGE_CONNECTION_PROVIDER, provider);
		GRegistry.addOrReplaceCheckedInvoker(MockDbCheckedInvokerNames.GET_KEY_VALUE_BY_KEY, 
				DbMethodWrappers.createConnectionWrapper(new SimpleQueryCheckedInvoker(), name));
		GRegistry.addOrReplaceCheckedInvoker(MockDbCheckedInvokerNames.GET_KEY_VALUE_BY_KEY_EXCEPTION_THROWER, 
				DbMethodWrappers.createConnectionWrapper(new SimpleQueryExceptionThrowerCheckedInvoker(), name));
		
		GRegistry.addOrReplaceCheckedInvoker(MockDbCheckedInvokerNames.CREATE_KEY_VALUE_IN_CONNECTION, 
				DbMethodWrappers.createTransactionAndConnectionWrapper(new SimpleKeyValCreator(), name));
		GRegistry.addOrReplaceCheckedInvoker(MockDbCheckedInvokerNames.CREATE_KEY_VALUE_IN_TRANSACTION, 
				DbMethodWrappers.createTransactionWrapper(new SimpleKeyValCreator()));
		GRegistry.addOrReplaceCheckedInvoker(MockDbCheckedInvokerNames.BATCH_CREATE_KEY_VALUE, 
				DbMethodWrappers.createReadWriteConnectionWrapper(new BatchingKeyValCreator(), name));
		
		GRegistry.addOrReplaceCheckedInvoker(MockDbCheckedInvokerNames.CREATE_KEY_VALUE_IN_CONNECTION_EXCEPTION_THROWER, 
				DbMethodWrappers.createTransactionAndConnectionWrapper(new SimpleExceptionThrowerKeyValCreator(), name));
		GRegistry.addOrReplaceCheckedInvoker(MockDbCheckedInvokerNames.CREATE_KEY_VALUE_IN_TRANSACTION_EXCEPTION_THROWER, 
				DbMethodWrappers.createTransactionWrapper(new SimpleExceptionThrowerKeyValCreator()));
		GRegistry.addOrReplaceCheckedInvoker(MockDbCheckedInvokerNames.BATCH_CREATE_KEY_VALUE_EXCEPTION_THROWER, 
				DbMethodWrappers.createReadWriteConnectionWrapper(new BatchingExceptionThrowerKeyValCreator(), name));
	}
}
