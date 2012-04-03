package org.adligo.xml.parsers.template.jpa;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.adligo.i.storage.EntityModifier;
import org.adligo.i.storage.EntityObtainer;
import org.adligo.i.storage.I_Query;
import org.adligo.i.storage.entities.MockJpaDb;
import org.adligo.models.params.client.Params;
import org.adligo.models.params.client.SqlOperators;
import org.adligo.tests.ATest;
import org.adligo.tests.xml.parsers.template.jdbc.MockDatabase;
import org.adligo.xml.parsers.template.Template;
import org.adligo.xml.parsers.template.Templates;
import org.adligo.xml.parsers.template.jdbc.BaseSqlOperators;

public class SqlQueryTests extends ATest {
	private Templates templates = new Templates(
			"/org/adligo/tests/xml/parsers/template/jdbc/Persons2_0_SQL.xml", true);
	private EntityManagerFactory emf;
	
	public void setUp() throws Exception {
		MockDatabase.createTestDb();
		List<InputStream> inputs = new ArrayList<InputStream>();
		inputs.add(
				JpaMockPerson.class.getResourceAsStream(
				"/org/adligo/xml/parsers/template/jpa/test_entities.xml"));
		emf = MockJpaDb.createEntityManagerFactory(inputs);
	}
	
	public void tearDown() throws Exception {
		emf.close();
	}
	
	public void testJpaSqlQuery() throws Exception  {
		//for (int i = 0; i < 100; i++) {
			assertSqlQuery();
		//}
	}

	public void assertSqlQuery() {
		Params params = new Params();
		params.addParam("default");
		Params where_params = params.addWhereParams();
		where_params.addParam("fname",SqlOperators.EQUALS, "john");
		
		Template personsTemp = templates.getTemplate("persons");
		
		EntityManager em = emf.createEntityManager();
		JpaEngineInput input = new JpaEngineInput();
		input.setTemplate(personsTemp);
		input.setEntityManager(em);
		input.setAllowedOperators(BaseSqlOperators.OPERATORS);
		input.setParams(params);
		
		Query query = JpaTemplateParserEngine.parseNative(input, JpaMockPerson.class);
		@SuppressWarnings("unchecked")
		List<JpaMockPerson> persons = (List<JpaMockPerson>) query.getResultList();
		
		assertEquals(1, persons.size());
		JpaMockPerson person = persons.get(0);
		assertEquals(new Integer(1), person.getTid());
		assertEquals(new Integer(0), person.getVersion());
		assertEquals("john", person.getFname());
		assertEquals("doe", person.getLname());
		
		em.close();
		System.out.println("yea " + person.getFname());
	}
	
	public void testJpaSqlObtainerQuery() throws Exception {
		
		//for (int i = 0; i < 100; i++) {
			assertSqlObtainerQuery();
		/*	
		}
		System.out.println("testing for memory leak");
		Thread.sleep(Integer.MAX_VALUE);
		*/
	}

	public void assertSqlObtainerQuery() {
		Params params = new Params();
		params.addParam("default");
		Params where_params = params.addWhereParams();
		where_params.addParam("fname",SqlOperators.EQUALS, "john");
		where_params.addParam("lname",SqlOperators.EQUALS, "doe");
		
		Template personsTemp = templates.getTemplate("persons");
		
		EntityManager em = emf.createEntityManager();
		EntityObtainer eo = new EntityObtainer(em);
		JpaReadOnlyEngineInput input = new JpaReadOnlyEngineInput();
		input.setTemplate(personsTemp);
		input.setEntityObtainer(eo);
		input.setAllowedOperators(BaseSqlOperators.OPERATORS);
		input.setParams(params);
		
		I_Query query = JpaTemplateParserEngine.parseNative(input, JpaMockPerson.class);
		@SuppressWarnings("unchecked")
		List<JpaMockPerson> persons = (List<JpaMockPerson>) query.getResultList();
		
		assertEquals(1, persons.size());
		JpaMockPerson person = persons.get(0);
		assertEquals(new Integer(1), person.getTid());
		assertEquals(new Integer(0), person.getVersion());
		assertEquals("john", person.getFname());
		assertEquals("doe", person.getLname());
		
		em.close();
		System.out.println("yea " + person.getFname());
	}
	
	public void testJpaSqlModifierQuery() {
		Params params = new Params();
		params.addParam("default");
		Params where_params = params.addWhereParams();
		where_params.addParam("lname",SqlOperators.EQUALS, "doe");
		
		Template personsTemp = templates.getTemplate("persons");
		
		EntityManager em = emf.createEntityManager();
		EntityModifier emod = new EntityModifier(em);
		JpaReadWriteEngineInput input = new JpaReadWriteEngineInput();
		input.setTemplate(personsTemp);
		input.setEntityModifier(emod);
		input.setAllowedOperators(BaseSqlOperators.OPERATORS);
		input.setParams(params);
		
		I_Query query = JpaTemplateParserEngine.parseNative(input, JpaMockPerson.class);
		@SuppressWarnings("unchecked")
		List<JpaMockPerson> persons = (List<JpaMockPerson>) query.getResultList();
		
		assertEquals(1, persons.size());
		JpaMockPerson person = persons.get(0);
		assertEquals(new Integer(1), person.getTid());
		assertEquals(new Integer(0), person.getVersion());
		assertEquals("john", person.getFname());
		assertEquals("doe", person.getLname());
		
		em.close();
		System.out.println("yea " + person.getFname());
	}
}
