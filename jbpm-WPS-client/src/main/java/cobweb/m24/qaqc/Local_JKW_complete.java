package cobweb.m24.qaqc;

import cobweb.m24.*;

import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import cobweb.m24.ExceptionServiceHandler;
import cobweb.m24.GenericWorkItemHandlerClient;

public class Local_JKW_complete {

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
				"GetLineOfSight", signallingTaskWrapper);
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"LaplacePhotoBlurCheck", signallingTaskWrapper);
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"Pillar2LocationQuality",
				new GenericWorkItemHandlerClient());
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"Pillar5ProximitySuitabilityPolygonScore",
				new GenericWorkItemHandlerClient());
		
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"Pillar3AttributeRange",
				new GenericWorkItemHandlerClient());		
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"Pillar5ReliabilityDistribution",
				new GenericWorkItemHandlerClient());
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"Pillar2UsabilityFilterOut",
				new GenericWorkItemHandlerClient());
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"Pillar4ProximitySuitabilityPolygonScore",
				new GenericWorkItemHandlerClient());
				
		ksession.getWorkItemManager().registerWorkItemHandler(
				"Pillar4PointInPolygon",
				new GenericWorkItemHandlerClient());
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"Store results in WFS-T", new GenericWorkItemHandlerClient());

		
		/**
		 * use this to start a defined process, this can be found in
		 * /src/main/resources
		 */
		ksession.startProcess("cobweb.m24.qaqc.local_qaqc_knotweed_complete");
	}

}
