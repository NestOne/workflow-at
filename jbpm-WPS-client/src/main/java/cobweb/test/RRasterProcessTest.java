package cobweb.test;

import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.kie.api.KieBase;

import net.opengis.examples.packet.GMLPacketDocument;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;

import cobweb.m24.ExceptionServiceHandler;
import cobweb.m24.GenericWorkItemHandlerClient;

import org.n52.wps.io.datahandler.parser.GML3BasicParser;

public class RRasterProcessTest {
	/**
	 * @author Julian Rosser
	 * @param args
	 * 
	 *            Main class harness for testing R vector and raster process passing
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
				"rTestReturnGeometry",
				new GenericWorkItemHandlerClient());
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"rTestReturnInputSurfaceModel",
				new GenericWorkItemHandlerClient());

		ksession.getWorkItemManager().registerWorkItemHandler(
				"rRocRasterCutoff",
				new GenericWorkItemHandlerClient());
		
		/**
		 * use this to start a defined process, this can be found in
		 * /src/main/resources
		 */
		ksession.startProcess("cobweb.m24.test.local_r_raster_two_process_string_return");

	}

}
