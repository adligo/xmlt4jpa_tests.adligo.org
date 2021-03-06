package org.adligo.xml.parsers.template_jpa_tests;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.adligo.i.db.I_Query;
import org.adligo.i.db.ReadOnlyConnection;
import org.adligo.i.db.ReadWriteConnection;
import org.adligo.i.db_tests.entities.MockJpaDb;
import org.adligo.models.params.shared.Params;
import org.adligo.models.params.shared.SqlOperators;
import org.adligo.tests.ATest;
import org.adligo.xml.parsers.template.Template;
import org.adligo.xml.parsers.template.Templates;
import org.adligo.xml.parsers.template.jdbc.BaseSqlOperators;
import org.adligo.xml.parsers.template_jpa.JpaEngineInput;
import org.adligo.xml.parsers.template_jpa.JpaReadOnlyEngineInput;
import org.adligo.xml.parsers.template_jpa.JpaReadWriteEngineInput;
import org.adligo.xml.parsers.template_jpa.JpaTemplateParserEngine;

public class SqlQueryTests extends ATest {
	private Templates templates = new Templates(
			"/org/adligo/xml/parsers/template_tests/jdbc/Persons2_0_SQL.xml", true);
	private Templates updateTemplates = new Templates(
			"/org/adligo/xml/parsers/template_tests/jdbc/ExecuteUpdate.xml", true);
	private EntityManagerFactory emf;
	
	public void setUp() throws Exception {
		MockJpaDb.commonSetup(new MockJpaHibernateMappings());
		CreateJpaDb.createDb();
		emf = MockJpaDb.getReadWriteEntityManagerFactory();
	}
	
	public void tearDown() throws Exception {
		MockJpaDb.commonTearDown();
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
		ReadOnlyConnection eo = new ReadOnlyConnection(em);
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
		params.addParam("name", "foo");
		params.addParam("tid", 1);
		
		Template personsTemp = updateTemplates.getTemplate("insert");
		
		EntityManager em = emf.createEntityManager();
		ReadWriteConnection emod = new ReadWriteConnection(em);
		JpaReadWriteEngineInput input = new JpaReadWriteEngineInput();
		input.setTemplate(personsTemp);
		input.setEntityModifier(emod);
		input.setAllowedOperators(BaseSqlOperators.OPERATORS);
		input.setParams(params);
		
		EntityTransaction tran = em.getTransaction();
		tran.begin();
		int result = JpaTemplateParserEngine.executeNativeUpdate(input);
		assertEquals(1, result);
		tran.commit();
		em.close();
	}
}
