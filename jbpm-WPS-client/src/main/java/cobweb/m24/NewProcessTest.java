package cobweb.m24;

import org.kie.api.KieBase;
import net.opengis.examples.packet.GMLPacketDocument;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;
import org.n52.wps.io.datahandler.parser.GML3BasicParser;

public class NewProcessTest {
	
	
	public static void main (String args[]){
	
	KieServices ks = KieServices.Factory.get();
	KieContainer kContainer = ks.getKieClasspathContainer();
	//KieBase kbase = kContainer.getKieBase("kbase");
	KieSession ksession = kContainer.newKieSession("ksession-process");

/**	RuntimeManager manager = createRuntimeManager(kbase);
	RuntimeEngine engine = manager.getRuntimeEngine(null);
	KieSession ksession = engine.getKieSession();
	TaskService taskService = engine.getTaskService();**/
	ksession.getWorkItemManager().registerWorkItemHandler("Pillar 4 - Point In Polygon", 
			(WorkItemHandler) new GenericWorkItemHandlerClient());
	ksession.getWorkItemManager().registerWorkItemHandler("Store results in WFS-T", 
			(WorkItemHandler) new GenericWorkItemHandlerClient());
	ksession.getWorkItemManager().registerWorkItemHandler("ConflationGeometryDistance", 
			(WorkItemHandler) new GenericWorkItemHandlerClient());
	
	String wpsURL = "http://localhost:8010/wps/WebProcessingService?";
	String processDescription = "pillar.authoritativedata.PointInPolygon";
	
	String inputObservations = "https://dyfi.cobwebproject.eu/geoserver/cobweb/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=cobweb:floodingview&maxFeatures=50&outputFormat=text/xml;%20subtype=gml/3.1.1";
	String inputAuthoritativeData = "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:Biospheric_Reserves&maxFeatures=50&outputFormat=gml3&srsName=EPSG:4236";
	
	ksession.startProcess("cobweb.m24.test_process");
	
	}

}
