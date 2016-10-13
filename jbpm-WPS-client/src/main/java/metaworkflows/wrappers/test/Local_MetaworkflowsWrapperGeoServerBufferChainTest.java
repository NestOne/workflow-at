package metaworkflows.wrappers.test;

import cobweb.m24.*;
import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.kie.api.KieBase;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import cobweb.m24.ExceptionServiceHandler;

public class Local_MetaworkflowsWrapperGeoServerBufferChainTest {
	/**
	 * @author Julian Rosser
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
				"GSBufferFeatureCollectionWrapper",
				new GenericWorkItemHandlerClient());
		
		/**
		 * use this to start a defined process, this can be found in
		 * /src/main/resources
		 */
		ksession.startProcess("metaworkflows.wrappers.test.local_metaworkflow_wrapper_geoserver_buffer_chain");
	}

}
