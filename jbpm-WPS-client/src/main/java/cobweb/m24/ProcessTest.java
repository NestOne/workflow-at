package cobweb.m24;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.runtime.manager.impl.SimpleRegisterableItemsFactory;
import org.jbpm.test.JBPMHelper;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;

import cobweb.m24.AuthoritativeDataClient;
import cobweb.m24.BufferedAuthoritativeDataClient;

/**
 * This is a sample file to launch a process.
 */
public class ProcessTest {

 /**   public static final void main(String[] args) {
        try {
        	
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
    	    KieBase kbase = kContainer.getKieBase("kbase");
        	KieSession kSession = kContainer.newKieSession("ksession-process");
    	    
    	 
    	    

            // start a new process instance
           
            
            kSession.getWorkItemManager().registerWorkItemHandler("AuthoritativeDataComparison", 
            		                                       (WorkItemHandler) new AuthoritativeDataClient());
            kSession.getWorkItemManager().registerWorkItemHandler("BufferedAuthoritativeDataComparison",
            												(WorkItemHandler) new BufferedAuthoritativeDataClient());
           
            
            
           
            String variable3 = "http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_cities&outputformat=gml3";
            String variable4 = "http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_state_boundaries&outputformat=gml3";
            String variable5 = "http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_state_boundaries&outputformat=gml3";
            String variable6 = "0.01";
            
            HashMap<String, Object> wpsInputsAuth1 = new HashMap<String, Object>();
            wpsInputsAuth1.put("inputObservations", variable3);
            wpsInputsAuth1.put("inputAuthoritativeData", variable4);
            
            HashMap<String, Object> wpsInputsAuth2 = new HashMap<String, Object>();
            //wpsInputsAuth2.put("inputObservations", variable3);
            wpsInputsAuth2.put("inputAuthoritativeData", variable5);
            wpsInputsAuth2.put("bufferSize", variable6);
            		          
            
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("variable1", "http://localhost:8010/wps/WebProcessingService");
            params.put("variable2", "pillar.authoritativedata.AuthoritativeDataComparison");
            params.put("variable3", wpsInputsAuth1);
            params.put("variable4", wpsInputsAuth2);
            params.put("variable5", "http://localhost:8010/geonetwork");

            kSession.startProcess("com.sample.bpmn.flooding", params);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
    }**/
	
	
	public static void main (String[] args){
	
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		//KieBase kbase = kContainer.getKieBase("kbase");
		KieSession ksession = kContainer.newKieSession("ksession-process");

	/**	RuntimeManager manager = createRuntimeManager(kbase);
		RuntimeEngine engine = manager.getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		TaskService taskService = engine.getTaskService();**/

		

		 ksession.getWorkItemManager().registerWorkItemHandler("AuthoritativeDataComparison", 
                 (WorkItemHandler) new AuthoritativeDataClient());
		 ksession.getWorkItemManager().registerWorkItemHandler("BufferedAuthoritativeDataComparison",
					(WorkItemHandler) new BufferedAuthoritativeDataClient());




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


		 Map<String, Object> params = new HashMap<String, Object>();
		 params.put("variable1", "http://localhost:8010/wps/WebProcessingService?");
		 params.put("variable2", "pillar.authoritativedata.AuthoritativeDataComparison");
		 params.put("variable3", wpsInputsAuth1);
		 params.put("variable4", wpsInputsAuth2);
		 params.put("variable5", "http://localhost:8010/geonetwork");

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

}
