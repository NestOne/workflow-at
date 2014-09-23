package com.sample;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.runtime.manager.impl.SimpleRegisterableItemsFactory;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;

import cobweb.m24.AuthoritativeDataClient;
import cobweb.m24.BufferedAuthoritativeDataClient;

/**
 * This is a sample file to launch a process.
 */
public class ProcessTest {

    public static final void main(String[] args) {
        try {
        	
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
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

            kSession.startProcess("com.sample.bpmn.hello", params);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
