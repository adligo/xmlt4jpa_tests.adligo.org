package org.adligo.xml.parsers.template.jpa;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.adligo.i.storage.EntityModifier;
import org.adligo.i.storage.EntityObtainer;
import org.adligo.i.storage.I_ReadWriteTypedQuery;
import org.adligo.i.storage.I_TypedQuery;
import org.adligo.i.storage.entities.MockJpaDb;
import org.adligo.models.params.client.Params;
import org.adligo.models.params.client.SqlOperators;
import org.adligo.tests.ATest;
import org.adligo.tests.xml.parsers.template.jdbc.MockDatabase;
import org.adligo.xml.parsers.template.Template;
import org.adligo.xml.parsers.template.Templates;
import org.adligo.xml.parsers.template.jdbc.BaseSqlOperators;

public class JpqlQueryTests extends ATest {
	private Templates templates = new Templates(
			"/org/adligo/xml/parsers/template/jpa/Persons1_0_JPQL.xml", true);
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
	
	public void testJpaQuery() {
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
		
		TypedQuery<JpaMockPerson> query = JpaTemplateParserEngine.parseJPQL(input, JpaMockPerson.class);
		List<JpaMockPerson> persons = query.getResultList();
		
		assertEquals(1, persons.size());
		JpaMockPerson person = persons.get(0);
		assertEquals(new Integer(1), person.getTid());
		assertEquals(new Integer(0), person.getVersion());
		assertEquals("john", person.getFname());
		assertEquals("doe", person.getLname());
		
		em.close();
		System.out.println("yea " + person.getFname());
	}
	
	public void testJpaObtainerQuery() {
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
		
		I_TypedQuery<JpaMockPerson> query = JpaTemplateParserEngine.parseJPQL(input, JpaMockPerson.class);
		List<JpaMockPerson> persons = query.getResultList();
		
		assertEquals(1, persons.size());
		JpaMockPerson person = persons.get(0);
		assertEquals(new Integer(1), person.getTid());
		assertEquals(new Integer(0), person.getVersion());
		assertEquals("john", person.getFname());
		assertEquals("doe", person.getLname());
		
		em.close();
		System.out.println("yea " + person.getFname());
	}
	
	public void testJpaModifierQuery() {
		Params params = new Params();
		params.addParam("default");
		Params where_params = params.addWhereParams();
		where_params.addParam("lname",SqlOperators.EQUALS, "doe");
		
		Template personsTemp = templates.getTemplate("persons");
		
		EntityManager em = emf.createEntityManager();
		EntityModifier emod = new EntityModifier(em);
		JpaReadOnlyEngineInput input = new JpaReadOnlyEngineInput();
		input.setTemplate(personsTemp);
		input.setEntityObtainer(emod);
		input.setAllowedOperators(BaseSqlOperators.OPERATORS);
		input.setParams(params);
		
		I_TypedQuery<JpaMockPerson> query = 
				JpaTemplateParserEngine.parseJPQL(
						input, JpaMockPerson.class);
		List<JpaMockPerson> persons = query.getResultList();
		
		assertEquals(1, persons.size());
		JpaMockPerson person = persons.get(0);
		assertEquals(new Integer(1), person.getTid());
		assertEquals(new Integer(0), person.getVersion());
		assertEquals("john", person.getFname());
		assertEquals("doe", person.getLname());
		
		em.close();
		System.out.println("yea " + person.getFname());
	}
	
	
	public void testJpaObtainerQueryFirstAndLastNameOnly() {
		Params params = new Params();
		params.addParam("name");
		Params where_params = params.addWhereParams();
		Params addressParams = 
			where_params.addParams("persons_to_addresses");
		addressParams.addParam("zip", SqlOperators.EQUALS, 60647);
		addressParams.addParam("zip", SqlOperators.EQUALS, 60660);
		
		EntityManager em = emf.createEntityManager();
		EntityObtainer eo = new EntityObtainer(em);
		JpaReadOnlyEngineInput input = new JpaReadOnlyEngineInput();
		input.setTemplate(templates.getTemplate("persons"));
		input.setEntityObtainer(eo);
		input.setAllowedOperators(BaseSqlOperators.OPERATORS);
		input.setParams(params);
		
		I_TypedQuery<String> query = JpaTemplateParserEngine.parseJPQL(input, String.class);
		List<String> persons = query.getResultList();
		
		assertEquals(2, persons.size());
		
		assertEquals("john doe", persons.get(0));
		assertEquals("lisa Smith", persons.get(1));
		
		em.close();
	}
	
	public void testJpaUpdate() {
		Params params = new Params();
		params.addParam("tid", 1);
		
		
		EntityManager em = emf.createEntityManager();
		EntityModifier emod = new EntityModifier(em);
		JpaReadWriteEngineInput input = new JpaReadWriteEngineInput();
		input.setTemplate(templates.getTemplate("personsUpdate"));
		input.setEntityModifier(emod);
		input.setAllowedOperators(BaseSqlOperators.OPERATORS);
		input.setParams(params);
		
		EntityTransaction tran = em.getTransaction();
		tran.begin();
		int result = JpaTemplateParserEngine.executeUpdate(input);
		assertEquals(1, result);
		tran.commit();
		em.close();
	}
}
