package org.adligo.xml.parsers.template.jpa;

import java.io.InputStream;

import org.adligo.hibernate.storage.I_HibernateMappingProvider;

public class MockJpaHibernateMappings implements I_HibernateMappingProvider {

	@Override
	public int size() {
		return 1;
	}

	@Override
	public InputStream get(int i) {
		if (i == 0) {
			return JpaMockPerson.class.getResourceAsStream(
					"/org/adligo/xml/parsers/template/jpa/test_entities.xml");
		}
		return null;
	}

}
