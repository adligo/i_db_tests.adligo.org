package org.adligo.i.db;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.adligo.i.db.entities.KeyValue;
import org.adligo.i.db.entities.MockJpaDb;
import org.adligo.i.log.shared.Log;
import org.adligo.i.log.shared.LogFactory;
import org.adligo.tests.ATest;

/**
 * quick simple check to make sure JPA is setup
 * @author scott
 *
 */
public class JpaSetupTests extends ATest {
	private static final Log log = LogFactory.getLog(JpaSetupTests.class);
	
	public void setUp() throws Exception {
		MockJpaDb.commonSetup();
		MockJpaDb.createTestDb();
	}
	
	public void tearDown() throws Exception {
		MockJpaDb.commonTearDown();
	}
	
	public void testJpaHibernateSetup() throws Exception {
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
		
		EntityManager em = executeQueryWithoutClosingEntityManager();
		
		em.close();
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
	}

	public EntityManager executeQueryWithoutClosingEntityManager()
			throws Exception, SQLException {
		EntityManagerFactory emf = MockJpaDb.getReadWriteEntityManagerFactory();
		EntityManager em = emf.createEntityManager();
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
		
		log.info("em 1 is " + em);
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
		
		Query query = em.createNativeQuery("SELECT * FROM keyVal WHERE key IN('A','B')", KeyValue.class);
		assertEquals(0, MockJpaDb.getConnectionsUsedFromReadWritePool());
		
		List<KeyValue> results = (List<KeyValue>) query.getResultList();
		//interesting that the hibernate impl doesn't actually accquire the connection
		// from the pool until the query is run (query.getResultList)
		assertEquals(1, MockJpaDb.getConnectionsUsedFromReadWritePool());
		
		for (KeyValue val: results) {
			log.error(val);
		}
		assertEquals(2, results.size());
		return em;
	}
	
}
