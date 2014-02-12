package org.adligo.i.db.entities;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.adligo.hibernate.db.EntityManagerFactoryBuilder;
import org.adligo.hibernate.db.I_HibernateMappingProvider;
import org.adligo.i.adig.client.GRegistry;
import org.adligo.i.db.DbCheckedInvokerNames;
import org.adligo.i.db.DbConnectionProvider;
import org.adligo.i.db.impl.MockDbRegistry;
import org.adligo.i.log.shared.Log;
import org.adligo.i.log.shared.LogFactory;
import org.adligo.models.params.client.Param;
import org.adligo.xml.parsers.template.Template;
import org.adligo.xml.parsers.template.Templates;
import org.adligo.xml.parsers.template.jdbc.JdbcEngineInput;
import org.adligo.xml.parsers.template.jdbc.JdbcTemplateParserEngine;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.hibernate.cfg.AvailableSettings;

public class MockJpaDb {
	public static final String TEST_DB_NAME = "testDbName";
	private static final Log log = LogFactory.getLog(MockJpaDb.class);
	private static boolean createdDb = false;
	private static BasicDataSource readOnlyDataSource;
	private static BasicDataSource readWriteDataSource;
	private static EntityManagerFactory readOnlyEntityManagerFactory;
	private static EntityManagerFactory readWriteEntityManagerFactory;
	private static I_HibernateMappingProvider MOCK_HIBERNATE_MAPPING_PROVIDER = new MockHibernateMappingProvider();
	
	public static EntityManagerFactory getReadOnlyEntityManagerFactory() {
		if (readWriteEntityManagerFactory == null) {
			throw new NullPointerException("readOnlyEntityManagerFactory is null please call commonSetup first");
		}
		return readOnlyEntityManagerFactory;
	}

	public static EntityManagerFactory getReadWriteEntityManagerFactory() {
		if (readWriteEntityManagerFactory == null) {
			throw new NullPointerException("readWriteEntityManagerFactory is null please call commonSetup first");
		}
		return readWriteEntityManagerFactory;
	}
	
	public static BasicDataSource getReadOnlyDataSource() {
		if (readOnlyDataSource == null) {
			throw new NullPointerException("readOnlyDataSource is null please call commonSetup first");
		}
		return readOnlyDataSource;
	}

	public static BasicDataSource getReadWriteDataSource() {
		if (readWriteDataSource == null) {
			throw new NullPointerException("readWriteDataSource is null please call commonSetup first");
		}
		return readWriteDataSource;
	}

	
	/**
	 * note this uses the connection name  
	 * {@link MockDbRegistry#I_STORAGE_TEST_DB}
	 * 
	 * @throws Exception
	 */
	public static void commonSetup() throws Exception {
		commonSetup(MOCK_HIBERNATE_MAPPING_PROVIDER);
	}
	
	/**
	 * note this uses the connection name  TEST_DB_NAME from the constant this class
	 * 
	 * @throws Exception
	 */
	public static void commonSetup(I_HibernateMappingProvider mappingProvider) throws Exception {
		commonSetup(TEST_DB_NAME, mappingProvider);
	}
	
	public static void commonSetup(String connectionName, I_HibernateMappingProvider mappingProvider) throws Exception {
		Properties props = new Properties();
		props.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLInnoDBDialect");
		if (log.isDebugEnabled()) {
			props.setProperty(AvailableSettings.SHOW_SQL,"true");
		} else {
			log.warn("You may show the sql by setting " + MockJpaDb.class.getName() + " to debug");
		}
		EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder();
		readOnlyDataSource = createNewBasicDataSource();
		builder.setDataSource(readOnlyDataSource);
		builder.setProps(props);
		builder.setMappingProvider(mappingProvider);
		readOnlyEntityManagerFactory = builder.build();
		
		EntityManagerFactoryBuilder readWriteBuilder = new EntityManagerFactoryBuilder();
		readWriteDataSource = createNewBasicDataSource();
		readWriteBuilder.setDataSource(readWriteDataSource);
		readWriteBuilder.setProps(props);
		readWriteBuilder.setMappingProvider(mappingProvider);
		readWriteEntityManagerFactory = readWriteBuilder.build();
		
		MockDbRegistry.setup(connectionName, readOnlyEntityManagerFactory, readWriteEntityManagerFactory);
	}
	
	public static void commonTearDown () throws Exception {
		readOnlyEntityManagerFactory.close();
		readOnlyDataSource.close();
		
		readWriteEntityManagerFactory.close();
		readWriteDataSource.close();
	}

	
	public static synchronized void createTestDb() throws Exception {
		if (!createdDb) {
			try {
				Class.forName("org.hsqldb.jdbcDriver").newInstance();
			} catch (Exception x) {
				x.printStackTrace();
			}

			Connection connection = readWriteDataSource.getConnection();
			
			Templates templates = new Templates();
			templates.parseResource("/org/adligo/i/db/entities/CreateTestDb.xml");
			
			
			
			Iterator<String> names = templates.getTemplateNames();
			while (names.hasNext()) {
				JdbcEngineInput values = new JdbcEngineInput();
				values.setParams(new Param());
				values.setConnection(connection);
				executeTemplate(templates, values, names.next());
			}
			connection.close();
			createdDb = true;
		}
		
	}

	
	public static synchronized BasicDataSource createNewBasicDataSource() throws Exception {
		Properties props = new Properties();
		props.setProperty("driverClassName", "org.hsqldb.jdbcDriver");
		props.setProperty("url", "jdbc:hsqldb:mem:aname");
		props.setProperty("username", "sa");
		props.setProperty("password", "");
		
		return (BasicDataSource) BasicDataSourceFactory.createDataSource(props);
	}
	
	
	public static Properties getProperties() {
		Properties props = new Properties();
		props.setProperty("hibernate.dialect",
				"org.hibernate.dialect.MySQLInnoDBDialect");
		
		props.setProperty("driverClassName", "org.hsqldb.jdbcDriver");
		props.setProperty("url", "jdbc:hsqldb:mem:aname");
		props.setProperty("username", "sa");
		props.setProperty("password", "");
		return props;
	}
	public static void logOpenConnections() throws SQLException {
		Exception x = new Exception();
		x.fillInStackTrace();
		StackTraceElement [] elements =  x.getStackTrace();
		
		log.info("at  " + elements[1] + " datasource has " + readWriteDataSource.getNumActive() + " used connections in the read write pool" );
		log.info("at  " + elements[1] + " datasource has " + readOnlyDataSource.getNumActive() + " used connections in the read only pool" );
				
	}
	
	public static int getConnectionsUsedFromReadWritePool() throws SQLException {
		return readWriteDataSource.getNumActive();
	}
	
	public static int getConnectionsUsedFromReadOnlyPool() throws SQLException {
		return readOnlyDataSource.getNumActive();
	}
	
	private static void executeTemplate(Templates templates,
			JdbcEngineInput values, String templateName) throws SQLException {
		log.info("executing template " + templateName);
		
		Template temp = templates.getTemplate(templateName);
		values.setTemplate(temp);
		JdbcTemplateParserEngine.execute(values);
	}
	
}
