package cobweb.m24;

import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

public class GenericWorkItemHandlerConflationClient implements WorkItemHandler {
	
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
			
		String wpsURL = (String) workItem.getParameter("wpsURL");
		String wpsProcessID = (String) workItem.getParameter("processDescription");
		String catalogURL = (String) workItem.getParameter("catalogURL");
		
		
		
		HashMap <String, Object> wpsInputs = (HashMap<String, Object>) workItem.getParameter("wpsInputs");
		
		FeatureCollection fc = null;
		
		if(workItem.getParameter("inputCarriedData")!=null){
		
			fc = (FeatureCollection) workItem.getParameter("inputCarriedData");
			
			System.out.println("WPS WORKITEM HANDLER " + wpsProcessID + " " + fc.size());
		
		
			wpsInputs.remove("IN_TARGET");
		
			wpsInputs.put("IN_TARGET", fc);
		
		}
		
		//wpsInputs.put("IN_TARGET", "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:SampleData&outputFormat=gml3&srsName=EPSG:4236");
		
		//String output = (String) workItem.getParameter("output");
		
		GenericWPSConflationClient wpsClient = new GenericWPSConflationClient(wpsURL, wpsProcessID, wpsInputs, catalogURL);
		HashMap<String, Object> outputs = new HashMap<String, Object>();
		
		outputs = wpsClient.getOutputs();
		
		System.out.println("outputs " +outputs.size());
		
		Map<String, Object> results = new HashMap<String,Object>();
		//System.out.println("OUTPUTS SIZE " + wpsClient.getOutputs().size());
	
			FeatureCollection result = ((GTVectorDataBinding) wpsClient.getOutputs().get("OUT_TARGET")).getPayload();
			FeatureCollection qual_result = ((GTVectorDataBinding) wpsClient.getOutputs().get("OUT_TARGET")).getPayload();
		
		results.put("result", result);
		results.put("qual_result", qual_result);
		
		manager.completeWorkItem(workItem.getId(), results);
	}
	
	
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}
}
