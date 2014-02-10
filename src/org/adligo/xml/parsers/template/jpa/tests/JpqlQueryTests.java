package org.adligo.xml.parsers.template.jpa.tests;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;

import org.adligo.i.db.I_TypedQuery;
import org.adligo.i.db.ReadOnlyConnection;
import org.adligo.i.db.ReadWriteConnection;
import org.adligo.i.db.entities.MockJpaDb;
import org.adligo.models.params.client.Params;
import org.adligo.models.params.client.SqlOperators;
import org.adligo.tests.ATest;
import org.adligo.xml.parsers.template.Template;
import org.adligo.xml.parsers.template.Templates;
import org.adligo.xml.parsers.template.jdbc.BaseSqlOperators;
import org.adligo.xml.parsers.template.jpa.JpaEngineInput;
import org.adligo.xml.parsers.template.jpa.JpaReadOnlyEngineInput;
import org.adligo.xml.parsers.template.jpa.JpaReadWriteEngineInput;
import org.adligo.xml.parsers.template.jpa.JpaTemplateParserEngine;

public class JpqlQueryTests extends ATest {
	private Templates templates = new Templates(
			"/org/adligo/xml/parsers/template/jpa/tests/Persons1_0_JPQL.xml", true);
	private EntityManagerFactory emf;
	
	public void setUp() throws Exception {
		MockJpaDb.commonSetup(new MockJpaHibernateMappings());
		CreateJpaDb.createDb();
		
		emf = MockJpaDb.getReadWriteEntityManagerFactory();
	}
	
	public void tearDown() throws Exception {
		MockJpaDb.commonTearDown();
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
		
		Set<EntityType<?>> entities = em.getMetamodel().getEntities();
		for (EntityType<?> et: entities) {
			System.out.println("et is " + et.getName());
		}
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
		ReadOnlyConnection eo = new ReadOnlyConnection(em);
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
		ReadWriteConnection emod = new ReadWriteConnection(em);
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
		ReadOnlyConnection eo = new ReadOnlyConnection(em);
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
		ReadWriteConnection emod = new ReadWriteConnection(em);
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
