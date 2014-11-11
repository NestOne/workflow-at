package cobweb.m24;

import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

public class GenericWorkItemHandlerClient implements WorkItemHandler {
	
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
			
		String wpsURL = (String) workItem.getParameter("wpsURL");
		String wpsProcessID = (String) workItem.getParameter("processDescription");
		String catalogURL = (String) workItem.getParameter("catalogURL");
		
		
		
		HashMap <String, Object> wpsInputs = (HashMap<String, Object>) workItem.getParameter("wpsInputs");
		
		FeatureCollection fc = null;
		
		if(workItem.getParameter("inputCarriedData")!=null){
		
			fc = (FeatureCollection) workItem.getParameter("inputCarriedData");
			
			System.out.println("WPS WORKITEM HANDLER " + wpsProcessID + " " + fc.size());
			wpsInputs.remove("inputObservations");
			wpsInputs.put("inputObservations", fc);
		
		}
		
		//String output = (String) workItem.getParameter("output");
		//change to test for qual_result = null;
		GenericWPSClient wpsClient = new GenericWPSClient(wpsURL, wpsProcessID, wpsInputs, catalogURL);
		
		Map<String, Object> results = new HashMap<String,Object>();
		
			FeatureCollection result = ((GTVectorDataBinding) wpsClient.getOutputs().get("result")).getPayload();
			
		FeatureCollection qual_result = null;
			
		if (((GTVectorDataBinding) wpsClient.getOutputs().get("qual_result")).getPayload()!=null){
			 qual_result = ((GTVectorDataBinding) wpsClient.getOutputs().get("qual_result")).getPayload();
		}
		
		
		results.put("result", result);
		results.put("qual_result", qual_result);
		
		manager.completeWorkItem(workItem.getId(), results);
	}
	
	
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}
}
