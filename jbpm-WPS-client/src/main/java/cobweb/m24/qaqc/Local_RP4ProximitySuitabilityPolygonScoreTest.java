package cobweb.m24.qaqc;

import cobweb.m24.*;

import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.kie.api.KieBase;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import cobweb.m24.ExceptionServiceHandler;

public class Local_RP4ProximitySuitabilityPolygonScoreTest {

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
				"Pillar4ProximitySuitabilityPolygonScore",
				new GenericWorkItemHandlerClient());
		
		/**
		 * use this to start a defined process, this can be found in
		 * /src/main/resources
		 */
		ksession.startProcess("cobweb.m24.qaqc.local_qaqc_knotweed_p4suitability");
	}

}
