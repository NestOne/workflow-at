package cobweb.m24.qaqc;

import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.kie.api.KieBase;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import cobweb.m24.ExceptionServiceHandler;
import cobweb.m24.GenericWorkItemHandlerClient;

public class Local_ReadFromPCAPIRest_Test {
	/**
	 * @author Julian Rosser
	 * @param args
	 * 
	 *            Main class harness for testing LoS / JKW workflow
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
				"ReadFromPCAPI", signallingTaskWrapper);
					
		/**
		 * use this to start a defined process, this can be found in
		 * /src/main/resources
		 */
		ksession.startProcess("cobweb.m24.qaqc.local_read_from_pcapi_rest");
		
	}

}
