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

public class FlickrFloodTest {
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
				"Store results in WFS-T", new GenericWorkItemHandlerClient());
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"rTestReturnGeometry",
				new GenericWorkItemHandlerClient());
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"rTestReturnInputSurfaceModel",
				new GenericWorkItemHandlerClient());

		ksession.getWorkItemManager().registerWorkItemHandler(
				"rRocRasterCutoff",
				new GenericWorkItemHandlerClient());

		ksession.getWorkItemManager().registerWorkItemHandler(
				"rRocRasterCutoff",
				new GenericWorkItemHandlerClient());
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"reclass-quantile",
				new GenericWorkItemHandlerClient());
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"CalculateWoEWeights",
				new GenericWorkItemHandlerClient());

		
		/**
		 * use this to start a defined process, this can be found in
		 * /src/main/resources
		 */
		ksession.startProcess("cobweb.m24.flickr_flood");
		
	}

}
