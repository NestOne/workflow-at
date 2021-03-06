package cobweb.test;

import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.kie.api.KieBase;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import cobweb.m24.ExceptionServiceHandler;
import cobweb.m24.GenericWorkItemHandlerClient;

public class Local_StoreToWFSTest{
	/**
	 * @author Sam Meek
	 * @param args
	 * 
	 *            This is a test class for the Eclipse plugin. Workflow
	 *            processes can be setup programmatically and run outside of the
	 *            console and run here. To use this class, make sure JBPM
	 *            workflow plugins are installed using the Ant script.
	 */

	public static void main(String args[]) {

		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbase");
		KieSession ksession = kContainer.newKieSession("ksession-process");

		String eventType = "error-code";
		SignallingTaskHandlerDecorator signallingTaskWrapper = new SignallingTaskHandlerDecorator(
				GenericWorkItemHandlerClient.class, eventType);
		signallingTaskWrapper
		.setWorkItemExceptionParameterName(ExceptionServiceHandler.exceptionParameterName);

		ksession.getWorkItemManager().registerWorkItemHandler(
				"Store results in WFS-T", new GenericWorkItemHandlerClient());

		
		/**
		 * use this to start a defined process, this can be found in
		 * /src/main/resources
		 */
		ksession.startProcess("cobweb.m24.test.local_store_to_wfs");

	}

}
