package cobweb.m24;

import java.util.HashMap;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.test.JBPMHelper;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;

/**
 * This is a sample file to launch a process.
 */
public class ProcessTest {
	
	
	public static void main (String[] args){
	
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
		ksession.getWorkItemManager().registerWorkItemHandler("Pillar 2 - Attribute Range Check",
				(WorkItemHandler) new GenericWorkItemHandlerClient());
		ksession.getWorkItemManager().registerWorkItemHandler("Store results in WFS-T", 
				(WorkItemHandler) new StoreWFSWorkItemHandlerClient());
		ksession.getWorkItemManager().registerWorkItemHandler("Pillar 1 - Get Spatial Accuracy", 
				(WorkItemHandler) new GenericWorkItemHandlerClient());
		ksession.getWorkItemManager().registerWorkItemHandler("Pillar 6 - Count Tweets with location",
				(WorkItemHandler) new GenericWorkItemHandlerClient());
		ksession.getWorkItemManager().registerWorkItemHandler("Conflation - Geometry Distance",
				(WorkItemHandler) new GenericWorkItemHandlerConflationClient());
		ksession.getWorkItemManager().registerWorkItemHandler("Pillar 2 - Filter on attribute", 
	             (WorkItemHandler) new GenericWorkItemHandlerClient());
		
		
		
		
		//variables for FilterOnAttribute
		String wpsURL = "http://localhost:8010/wps/WebProcessingService?";
		String processId = "pillar.cleaning.FilterOnAttribute";
		String catalogURL = "http://localhost:8010/geonetwork";
		String inputObservations = "https://dyfi.cobwebproject.eu/geoserver/FloodingData/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=FloodingData:observations&outputFormat=gml3";
		String fieldName = "pos_tech";
		String featureName = "NETWORK";
		String include = "true";
		
		HashMap<String, Object> wpsInputs = new HashMap<String, Object>();
		wpsInputs.put("inputObservations", inputObservations);
		wpsInputs.put("fieldName", fieldName);
		wpsInputs.put("featureName", featureName);
		wpsInputs.put("include", include);
		
		//variables for PointInPolygon
		HashMap<String, Object> wpsPolygonInputs = new HashMap<String, Object>();
		
		String processIdP = "pillar.authoritativedata.PointInPolygon";
		String processIdA = "pillar.cleaning.AttributeRange";
		String processIdG = "pillar.lbs.GetSpatialAccuracy";
		String processIdT = "pillar.bigdata.CountTweetsWithLocation";

	
		String inputAuthoritativeData = "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:Ireland&outputFormat=gml3&srsName=EPSG:4236";
		//wpsPolygonInputs.put("inputObservations", inputObservations);
		wpsPolygonInputs.put("inputAuthoritativeData", inputAuthoritativeData);
		
		//variables for ArrtibuteRange
		HashMap<String, Object> wpsAttributeInputs = new HashMap<String, Object>();
		
		//wpsAttributeInputs.put("inputObservations",  inputObservations);
		wpsAttributeInputs.put("attributeName", "pos_sat");
		wpsAttributeInputs.put("minRange", "0");
		wpsAttributeInputs.put("maxRange", "100");
		
		//variables for GetSpatialAccuracy
		HashMap<String, Object> wpsGetSpatialInputs = new HashMap<String, Object>();
	//	wpsGetSpatialInputs.put("inputObservations", inputObservations);
		wpsGetSpatialInputs.put("inputSatelliteNumberField", "pos_sat");
		wpsGetSpatialInputs.put("inputAccuracyField", "pos_acc");
		wpsGetSpatialInputs.put("minSatNum", "0");
		wpsGetSpatialInputs.put("minAcc", "0");
		
		//variables for Count tweets process
		HashMap<String, Object> wpsTwitter = new HashMap<String, Object>();
		wpsTwitter.put("inputLocation", "52.56585, -3.82793");
		wpsTwitter.put("inputDistance", "1000");
		wpsTwitter.put("searchTerm", "#flooding");
		wpsTwitter.put("dateSince", "2014-11-01");
		
		//variables for conflation
		String conflationWPS = "http://cobweb.gis.geo.tu-dresden.de:8080/wps_conflation/WebProcessingService?";
		String confProcID = "de.tudresden.gis.fusion.algorithm.GeometryDistance";
		HashMap<String, Object> conflationHash = new HashMap<String, Object>();
		conflationHash.put("IN_TARGET", inputObservations);
		conflationHash.put("IN_REFERENCE", inputAuthoritativeData);
		conflationHash.put("IN_THRESHOLD", "0.05");
		
		//System.out.println(conflationHash.get("IN_REFERENCE"));
		


		HashMap<String, Object> params = new HashMap<String, Object>();
		
		params.put("wpsURL", wpsURL);
		params.put("catalogURL", catalogURL);
		params.put("processDescription", processId);
		params.put("wpsInputs", wpsInputs);
		params.put("PolyProcessDescription", processIdP);
		params.put("PolyInputs", wpsPolygonInputs);
		params.put("bufferProcessDescription", processIdA);
		params.put("bufferInputs", wpsAttributeInputs);
		params.put("GetSpatialInputs", wpsGetSpatialInputs);
		params.put("GetSpatialProcessId", processIdG);
		params.put("TwitterPD", processIdT);
		params.put("TwitterInputs", wpsTwitter);
		params.put("WPSConflationName", conflationWPS);
		params.put("WPSConflationProcessID", confProcID);
		params.put("WPSConflationVar", conflationHash);
		
		ksession.startProcess("com.sample.bpmn.flooding", params);
		 
	}
	

		 	//manager.disposeRuntimeEngine(engine);
		 	//System.exit(0);
		
	
    private static RuntimeManager createRuntimeManager(KieBase kbase) {
		JBPMHelper.startH2Server();
		JBPMHelper.setupDataSource();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get()
			.newDefaultBuilder().entityManagerFactory(emf)
			.knowledgeBase(kbase);
		return RuntimeManagerFactory.Factory.get()
			//.newSingletonRuntimeManager(builder.get(), "com.sample:example:1.0");
				.newSingletonRuntimeManager(builder.get(), "cobweb.m24:example:1.0");
	}
    

	/** ksession.getWorkItemManager().registerWorkItemHandler("AuthoritativeDataComparison", 
             (WorkItemHandler) new AuthoritativeDataClient());
	 ksession.getWorkItemManager().registerWorkItemHandler("BufferedAuthoritativeDataComparison",
				(WorkItemHandler) new BufferedAuthoritativeDataClient());
	 ksession.getWorkItemManager().registerWorkItemHandler("LineOfSight", 
			 (WorkItemHandler) new LineOfSightClient());



	String variable3 = "http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_cities&outputformat=gml3";
	 String variable4 = "http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_state_boundaries&outputformat=gml3";
	 String variable5 = "http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_state_boundaries&outputformat=gml3";
	 String variable6 = "0.01";

	 HashMap<String, Object> wpsInputsAuth1 = new HashMap<String, Object>();
	 wpsInputsAuth1.put("inputObservations", variable3);
	 wpsInputsAuth1.put("inputAuthoritativeData", variable4);

	 HashMap<String, Object> wpsInputsAuth2 = new HashMap<String, Object>();
	 wpsInputsAuth2.put("inputObservations", variable3);
	 wpsInputsAuth2.put("inputAuthoritativeData", variable5);
	 wpsInputsAuth2.put("bufferSize", variable6);
	 
	 HashMap<String, Object> wpsInputsLBS = new HashMap<String, Object>();
	 wpsInputsLBS.put("inputObservations", variable3);
	 
	 

	 Map<String, Object> params = new HashMap<String, Object>();
	 params.put("variable1", "http://localhost:8010/wps/WebProcessingService?");
	 params.put("variable2", "pillar.authoritativedata.AuthoritativeDataComparison");
	 params.put("variable3", wpsInputsAuth1);
	 params.put("variable4", wpsInputsAuth2);
	 params.put("variable5", "http://localhost:8010/geonetwork");
	 params.put("variable6", wpsInputsLBS);**/


}
