package cobweb.m24;

import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.kie.api.KieBase;

import net.opengis.examples.packet.GMLPacketDocument;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;
import cobweb.m24.ExceptionServiceHandler;
import org.n52.wps.io.datahandler.parser.GML3BasicParser;

public class RGeometryPointsProcessTest {
	/**
	 * @author Julian Rosser
	 * @param args
	 * 
	 *            Main class harness for testing R vector process passing
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
				"Pillar 4 - Point In Polygon",
				new GenericWorkItemHandlerClient());
		ksession.getWorkItemManager().registerWorkItemHandler(
				"Pillar 4 - Point In Buffer",
				new GenericWorkItemHandlerClient());
		ksession.getWorkItemManager().registerWorkItemHandler(
				"Pillar 2 - Filter on Attribute",
				new GenericWorkItemHandlerClient());
		ksession.getWorkItemManager().registerWorkItemHandler(
				"Store results in WFS-T", new GenericWorkItemHandlerClient());
		ksession.getWorkItemManager().registerWorkItemHandler(
				"ConflationGeometryDistance", signallingTaskWrapper);

		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"rTestReturnGeometry",
				new GenericWorkItemHandlerClient());
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"rTestReturnGeometryPoints",
				new GenericWorkItemHandlerClient());
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"rTestReturnInputSurfaceModel",
				new GenericWorkItemHandlerClient());		
		
		
		/**
		 * use this to start a defined process, this can be found in
		 * /src/main/resources
		 */
		//ksession.startProcess("cobweb.m24.test_r_geometry_process_string_return");
		ksession.startProcess("cobweb.m24.test_r_geometry_points_string");
	}

}
