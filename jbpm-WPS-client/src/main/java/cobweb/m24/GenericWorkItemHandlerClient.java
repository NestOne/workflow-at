package cobweb.m24;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.geotools.feature.FeatureCollection;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

public class GenericWorkItemHandlerClient implements WorkItemHandler {
	
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		
		Map<String, Object> inputs = new HashMap<String, Object>();
		
		inputs = workItem.getParameters();
		
		Iterator<Entry<String, Object>> it = inputs.entrySet().iterator();
		String wpsURL = new String();
		String wpsProcessID = new String();
		
		HashMap<String, Object>variables = new HashMap<String, Object>();
		
		while (it.hasNext()){
			
			Map.Entry<String, Object> pairs = (Map.Entry<String, Object>)it.next();
			
			System.out.println(pairs.getKey() + " " + pairs.getValue());
			if (pairs.getKey().equalsIgnoreCase("wpsURL")){	
				System.out.println("GWIC HERE 1 " + pairs.getKey() + " " + pairs.getValue());

				
				wpsURL = (String) pairs.getValue();
 			}
			
			else if (pairs.getKey().equalsIgnoreCase("processDescription")){
				
				System.out.println("GWIC HERE 2 " + pairs.getKey() + " " + pairs.getValue());

				wpsProcessID  = (String) pairs.getValue();
			}
			
			else{				
				System.out.println("GWIC HERE 3 " + pairs.getKey() + " " + pairs.getValue());

				variables.put(pairs.getKey(), pairs.getValue());
				
			}
			
			it.remove();
			
		}
		
		
		
		GenericWPSClient wpsClient = new GenericWPSClient(wpsURL, wpsProcessID, variables, null);
		
		HashMap<String, Object> results = wpsClient.getOutputs();
		
		
		
		/**HashMap <String, Object> wpsInputs = (HashMap<String, Object>) workItem.getParameter("wpsInputs");
		
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
		results.put("qual_result", qual_result);**/
		
		manager.completeWorkItem(workItem.getId(), results);
	}
	
	
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}
}
