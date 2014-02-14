package org.adligo.xml.parsers.template_jpa_tests;

import org.adligo.i.adi.shared.InvocationException;
import org.adligo.i.adig.shared.I_GCheckedInvoker;
import org.adligo.i.db.DbMethodWrappers;
import org.adligo.i.db_tests.entities.MockJpaDb;
import org.adligo.xml.parsers.template.Templates;
import org.adligo.xml.parsers.template_jpa.TemplateAsScriptExecutor;
import org.adligo.xml.parsers.template_jpa.TemplatesModifyEntityRequest;

public class CreateJpaDb {
	private static boolean createDb = false;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized static void createDb() throws InvocationException {
		if (!createDb) {
			createDb = true;
			
			TemplateAsScriptExecutor scriptExecutor = new TemplateAsScriptExecutor();
			I_GCheckedInvoker scriptInConnectionInvoker = DbMethodWrappers.createTransactionAndConnectionWrapper(
					scriptExecutor, 
					MockJpaDb.TEST_DB_NAME);
			TemplatesModifyEntityRequest request = new TemplatesModifyEntityRequest();
			request.setTemplates(new Templates("/org/adligo/xml/parsers/template_tests/jdbc/CreateTestDb.xml",
					true));
			scriptInConnectionInvoker.invoke(request);
		}
	}
}
