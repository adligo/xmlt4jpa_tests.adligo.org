package org.adligo.xml.parsers.template_jpa_tests;

import java.io.InputStream;

import org.adligo.hibernate.db.I_HibernateMappingProvider;

public class MockJpaHibernateMappings implements I_HibernateMappingProvider {

	@Override
	public int size() {
		return 1;
	}

	@Override
	public InputStream get(int i) {
		if (i == 0) {
			return JpaMockPerson.class.getResourceAsStream(
					"/org/adligo/xml/parsers/template/jpa/tests/test_entities.xml");
		}
		return null;
	}

}
