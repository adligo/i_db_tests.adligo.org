package org.adligo.i.db_tests.entities;

import java.io.InputStream;

import org.adligo.hibernate.db.I_HibernateMappingProvider;

public class MockHibernateMappingProvider implements I_HibernateMappingProvider {

	@Override
	public int size() {
		return 1;
	}

	@Override
	public InputStream get(int i) {
		if (i == 0) {
			return KeyValue.class.getResourceAsStream("keyValue.xml");
		}
		return null;
	}

}
