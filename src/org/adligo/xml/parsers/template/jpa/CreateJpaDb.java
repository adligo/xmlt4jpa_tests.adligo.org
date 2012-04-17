package org.adligo.xml.parsers.template.jpa;

import org.adligo.i.adi.client.InvocationException;
import org.adligo.i.adig.client.I_GCheckedInvoker;
import org.adligo.i.storage.StorageWrappers;
import org.adligo.i.storage.entities.MockJpaDb;
import org.adligo.xml.parsers.template.Templates;

public class CreateJpaDb {
	private static boolean createDb = false;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized static void createDb() throws InvocationException {
		if (!createDb) {
			createDb = true;
			
			TemplateAsScriptExecutor scriptExecutor = new TemplateAsScriptExecutor();
			I_GCheckedInvoker scriptInConnectionInvoker = StorageWrappers.createTransactionAndConnectionWrapper(
					scriptExecutor, 
					MockJpaDb.TEST_DB_NAME);
			TemplatesModifyEntityRequest request = new TemplatesModifyEntityRequest();
			request.setTemplates(new Templates("/org/adligo/tests/xml/parsers/template/jdbc/CreateTestDb.xml",
					true));
			scriptInConnectionInvoker.invoke(request);
		}
	}
}
