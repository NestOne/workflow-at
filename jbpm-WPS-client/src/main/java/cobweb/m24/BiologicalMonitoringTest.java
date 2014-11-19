package cobweb.m24;

import java.util.HashMap;
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

public class BiologicalMonitoringTest {
	
	
public static void main(String args[]){
	KieServices ks = KieServices.Factory.get();
	KieContainer kContainer = ks.getKieClasspathContainer();
	//KieBase kbase = kContainer.getKieBase("kbase");
	KieSession ksession = kContainer.newKieSession("ksession-process");

/**	RuntimeManager manager = createRuntimeManager(kbase);
	RuntimeEngine engine = manager.getRuntimeEngine(null);
	KieSession ksession = engine.getKieSession();
	TaskService taskService = engine.getTaskService();**/
	
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
	ksession.getWorkItemManager().registerWorkItemHandler("Pillar 4 - Point In Polygon", 
			(WorkItemHandler) new GenericWorkItemHandlerClient());
	
	
	
	
	//variables for FilterOnAttribute
	String wpsURL = "http://localhost:8010/wps/WebProcessingService?";
	String processId = "pillar.cleaning.FilterOnAttribute";
	String catalogURL = "http://localhost:8010/geonetwork";
	String inputObservations = "https://dyfi.cobwebproject.eu/geoserver/cobweb/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=cobweb:sid-anonsurvey&maxFeatures=50&outputFormat=text/xml;%20subtype=gml/3.1.1";
	String fieldName = "Species";
	String featureName = "Hazel";
	String include = "true";
	
	HashMap<String, Object> wpsInputs = new HashMap<String, Object>();
	wpsInputs.put("inputObservations", inputObservations);
	wpsInputs.put("fieldName", fieldName);
	wpsInputs.put("featureName", featureName);
	wpsInputs.put("include", include);
	
	//variables for PointInPolygon
	HashMap<String, Object> wpsPolygonInputs = new HashMap<String, Object>();
	
	/**String processIdP = "pillar.authoritativedata.PointInPolygon";
	String processIdA = "pillar.cleaning.AttributeRange";
	String processIdG = "pillar.lbs.GetSpatialAccuracy";
	String processIdT = "pillar.bigdata.CountTweetsWithLocation";**/


	String inputAuthoritativeData = "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:Dyfi_Bio_Selection&outputFormat=gml3";
	//wpsPolygonInputs.put("inputObservations", inputObservations);
	wpsPolygonInputs.put("inputAuthoritativeData", inputAuthoritativeData);
	wpsPolygonInputs.put("inputObservations", inputObservations);
	
	//variables for ArrtibuteRange
	HashMap<String, Object> wpsAttributeInputs = new HashMap<String, Object>();
	
	//wpsAttributeInputs.put("inputObservations",  inputObservations);
	wpsAttributeInputs.put("attributeName", "pos_acc");
	wpsAttributeInputs.put("minRange", "30");
	wpsAttributeInputs.put("maxRange", "100");
	
	//variables for GetSpatialAccuracy
	HashMap<String, Object> wpsGetSpatialInputs = new HashMap<String, Object>();
//	wpsGetSpatialInputs.put("inputObservations", inputObservations);
	wpsGetSpatialInputs.put("inputSatelliteNumberField", "pos_sat");
	wpsGetSpatialInputs.put("inputAccuracyField", "pos_acc");
	wpsGetSpatialInputs.put("minSatNum", "0");
	wpsGetSpatialInputs.put("minAcc", "0");
	
	//twitter
	HashMap<String, Object> wpsTwitter = new HashMap<String, Object>();
	wpsTwitter.put("inputLocation", "52.56585, -3.82793");
	wpsTwitter.put("inputDistance", "1000");
	wpsTwitter.put("searchTerm", "#Hazel");
	wpsTwitter.put("dateSince", "2014-11-18");
	
	//variables for conflation
	String conflationWPS = "http://cobweb.gis.geo.tu-dresden.de:8080/wps_conflation/WebProcessingService?";
	String confProcID = "de.tudresden.gis.fusion.algorithm.GeometryDistance";
	HashMap<String, Object> conflationHash = new HashMap<String, Object>();
	String inputConflationData = "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:Dyfi_Bio_Selection&maxFeatures=50&outputFormat=gml3";
	conflationHash.put("IN_TARGET", inputConflationData);
	conflationHash.put("IN_REFERENCE", inputAuthoritativeData);
	conflationHash.put("IN_THRESHOLD", "0");
	
	//System.out.println(conflationHash.get("IN_REFERENCE"));
	


	HashMap<String, Object> params = new HashMap<String, Object>();
	
	
	params.put("pointInPolygonMap", wpsPolygonInputs);
	params.put("filterOnAttributeMap", wpsInputs);
	params.put("getSpatialAccuracyMap", wpsGetSpatialInputs);
	params.put("attributeRangeCheckMap", wpsAttributeInputs);
	params.put("countTweetsWithLocationMap", wpsTwitter);
	params.put("geometryConflationMap", conflationHash);
	
	ksession.startProcess("cobweb.m24.biologicalmonitoring", params);
	 
		}



	 

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
}
