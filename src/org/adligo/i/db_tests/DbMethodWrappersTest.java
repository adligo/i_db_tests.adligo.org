package org.adligo.i.db_tests;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.adligo.i.adi.shared.InvocationException;
import org.adligo.i.adig.shared.GRegistry;
import org.adligo.i.adig.shared.I_GCheckedInvoker;
import org.adligo.i.db_tests.entities.KeyValue;
import org.adligo.i.db_tests.entities.MockJpaDb;
import org.adligo.i.db_tests.impl.BatchCreateKeyValuesRequest;
import org.adligo.i.db_tests.impl.CreateKeyValueRequest;
import org.adligo.i.db_tests.impl.MockDbCheckedInvokerNames;
import org.adligo.i.db_tests.impl.SimpleExceptionThrowerKeyValCreator;
import org.adligo.i.db_tests.impl.SimpleKeyRequest;
import org.adligo.i.db_tests.impl.SimpleQueryExceptionThrowerCheckedInvoker;
import org.adligo.i.log.shared.Log;
import org.adligo.i.log.shared.LogFactory;
import org.adligo.tests.ATest;

public class DbMethodWrappersTest extends ATest {
	private static final Log log = LogFactory.getLog(DbMethodWrappersTest.class);
	private static I_GCheckedInvoker<SimpleKeyRequest, KeyValue> GET_KEY_VALUES = GRegistry.getCheckedInvoker(MockDbCheckedInvokerNames.GET_KEY_VALUE_BY_KEY, 
			SimpleKeyRequest.class, KeyValue.class);
	public void setUp() throws Exception {
		MockJpaDb.commonSetup();
		MockJpaDb.createTestDb();
	}
	
	public void tearDown() throws Exception {
		MockJpaDb.commonTearDown();
	}
	
	public void testSimpleWrappedQueryTest() throws Exception {
		for (int i = 0; i < 1000; i++) {
			assertQueryA();
		}
	}
	
	public void testMemoryLeaks() throws Exception {
		//uncomment and attach net beans and filter for org.adligo.i.storage to look for leaks
		//Thread.sleep(Integer.MAX_VALUE);
	}

	public void assertQueryA() throws SQLException, InvocationException {
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadOnlyPool());
		I_GCheckedInvoker<SimpleKeyRequest, KeyValue> invoker = GRegistry.getCheckedInvoker(MockDbCheckedInvokerNames.GET_KEY_VALUE_BY_KEY, 
				SimpleKeyRequest.class, KeyValue.class);
		SimpleKeyRequest request = new SimpleKeyRequest();
		request.setKey("A");
		KeyValue result = GET_KEY_VALUES.invoke(request);
		assertNotNull(result);
		assertEquals("A", result.getKey());
		assertEquals("A value", result.getValue());
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadOnlyPool());
	}

	public void testSimpleWrappedQueryTestWithException() throws Exception {
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadOnlyPool());
		I_GCheckedInvoker<SimpleKeyRequest, KeyValue> invoker = GRegistry.getCheckedInvoker(
				MockDbCheckedInvokerNames.GET_KEY_VALUE_BY_KEY_EXCEPTION_THROWER, 
				SimpleKeyRequest.class, KeyValue.class);
		SimpleKeyRequest request = new SimpleKeyRequest();
		request.setKey("A");
		KeyValue result = null;
		Exception caught= null;
		try {
		   result = invoker.invoke(request);
		} catch (InvocationException ix) {
			caught = ix;
			//log.error(ix.getMessage(), ix);
		}
		assertNotNull(caught);
		assertEquals("org.hibernate.exception.SQLGrammarException: Column not found: VALLLLLUEEE in statement [Select key, valllllueee FROM keyVal WHERE key = ?]",
				caught.getMessage());
		assertStackTraceContainsClass(caught, SimpleQueryExceptionThrowerCheckedInvoker.class);
		assertNull(result);
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadOnlyPool());
	}

	public void assertStackTraceContainsClass(Exception caught, Class<?> c) {
		StackTraceElement [] traceElements = caught.getStackTrace();
		Throwable cause = caught.getCause();
		if (cause != null) {
			traceElements = cause.getStackTrace();
		}
		boolean foundIt = false;
		for (StackTraceElement e: traceElements) {
			if (e.getClassName().contains(c.getSimpleName())) {
				foundIt = true;
			}
		}
		assertTrue("The stack trace should link back to " + c, foundIt);
	}
	
	
	public void testCreateKeyValueTest() throws Exception {
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
		I_GCheckedInvoker<CreateKeyValueRequest, Boolean> invoker = GRegistry.getCheckedInvoker(
				MockDbCheckedInvokerNames.CREATE_KEY_VALUE_IN_CONNECTION, 
				CreateKeyValueRequest.class, Boolean.class);
		
		CreateKeyValueRequest request = new CreateKeyValueRequest();
		KeyValue kv = new KeyValue();
		kv.setKey("C");
		kv.setValue("C value");
		request.setItem(kv);
		Boolean result = invoker.invoke(request);
		assertTrue(result);
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
		
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadOnlyPool());
		
		SimpleKeyRequest getRequest = new SimpleKeyRequest();
		getRequest.setKey("C");
		KeyValue keyValResult = GET_KEY_VALUES.invoke(getRequest);
		assertNotNull(keyValResult);
		assertEquals("C", keyValResult.getKey());
		assertEquals("C value", keyValResult.getValue());
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadOnlyPool());
	}
	
	public void testFailToCreateKeyValueTest() throws Exception {
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
		I_GCheckedInvoker<CreateKeyValueRequest, Boolean> invoker = GRegistry.getCheckedInvoker(
				MockDbCheckedInvokerNames.CREATE_KEY_VALUE_IN_CONNECTION_EXCEPTION_THROWER, 
				CreateKeyValueRequest.class, Boolean.class);
		
		CreateKeyValueRequest request = new CreateKeyValueRequest();
		KeyValue kv = new KeyValue();
		kv.setKey("C1");
		kv.setValue("C value");
		request.setItem(kv);
		
		Exception caught = null;
		try {
			invoker.invoke(request);
		} catch (InvocationException ix) {
			caught = ix;
			//log.error(ix.getMessage(), ix);
		}
		assertNotNull(caught);
		assertEquals("org.hibernate.exception.SQLGrammarException: Column not found: TID in statement [update keyVal SET tid = 123]",
				caught.getMessage());
		assertStackTraceContainsClass(caught, SimpleExceptionThrowerKeyValCreator.class);
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());

	}
	
	public void testBatchCreateKeyValueTest() throws Exception {
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
		I_GCheckedInvoker<BatchCreateKeyValuesRequest, Integer> invoker = GRegistry.getCheckedInvoker(
				MockDbCheckedInvokerNames.BATCH_CREATE_KEY_VALUE, 
				BatchCreateKeyValuesRequest.class, Integer.class);
		
		BatchCreateKeyValuesRequest request = new BatchCreateKeyValuesRequest();
	
		List<KeyValue> items = new ArrayList<KeyValue>();
		
		KeyValue kv = new KeyValue();
		kv.setKey("D");
		kv.setValue("D value");
		items.add(kv);
		
		kv = new KeyValue();
		kv.setKey("E");
		kv.setValue("E value");
		items.add(kv);
		
		kv = new KeyValue();
		kv.setKey("F");
		kv.setValue("F value");
		items.add(kv);
		
		request.setItems(items);
		
		int result = invoker.invoke(request);
		
		assertEquals(3, result);
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
		
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadOnlyPool());
		checkIfExists("D");
		checkIfExists("E");
		checkIfExists("F");
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadOnlyPool());
	}

	public void checkIfExists(String key)
			throws InvocationException {
		SimpleKeyRequest getRequest = new SimpleKeyRequest();
		getRequest.setKey(key);
		KeyValue keyValResult = GET_KEY_VALUES.invoke(getRequest);
		assertNotNull(keyValResult);
		assertEquals(key, keyValResult.getKey());
		assertEquals(key + " value", keyValResult.getValue());
	}
	
	
	public void testFailBatchCreateKeyValueTest() throws Exception {
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
		I_GCheckedInvoker<BatchCreateKeyValuesRequest, Integer> invoker = GRegistry.getCheckedInvoker(
				MockDbCheckedInvokerNames.BATCH_CREATE_KEY_VALUE_EXCEPTION_THROWER, 
				BatchCreateKeyValuesRequest.class, Integer.class);
		
		BatchCreateKeyValuesRequest request = new BatchCreateKeyValuesRequest();
	
		List<KeyValue> items = new ArrayList<KeyValue>();
		
		KeyValue kv = new KeyValue();
		kv.setKey("F");
		kv.setValue("F value");
		items.add(kv);
		
		request.setItems(items);
		Exception caught = null;
		try {
			invoker.invoke(request);
		} catch (InvocationException ix) {
			caught = ix;
			log.error(ix.getMessage(), ix);
		}
		assertNotNull(caught);
		assertEquals("org.hibernate.exception.SQLGrammarException: Column not found: TID in statement [update keyVal SET tid = 123]",
				caught.getMessage());
		assertStackTraceContainsClass(caught, SimpleExceptionThrowerKeyValCreator.class);
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
	}
}
