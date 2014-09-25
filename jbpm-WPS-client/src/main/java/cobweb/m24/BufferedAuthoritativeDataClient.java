package cobweb.m24;

import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;




public class BufferedAuthoritativeDataClient implements WorkItemHandler{

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		String wpsURL = (String) workItem.getParameter("wpsURL");
		String wpsProcessID = "pillar.authoritativedata.BufferDataComparison";
		String catalogURL = (String) workItem.getParameter("catalogURL");
		
		FeatureCollection fc = (FeatureCollection) workItem.getParameter("wpsCarriedData");
		
		HashMap <String, Object> wpsInputs = (HashMap<String, Object>) workItem.getParameter("wpsInputs");
		
		String output = (String) workItem.getParameter("output");
		
		System.out.println("Buffered Authoritative Data " + output);
		
		GenericWPSClient wpsClient = new GenericWPSClient(wpsURL, wpsProcessID, wpsInputs, catalogURL,fc);
		
		Map<String, Object> results = new HashMap<String,Object>();
	
		FeatureCollection outFc = ((GTVectorDataBinding) wpsClient.getOutputs().get("qual_result")).getPayload();
		
		results.put("output", outFc);
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		
		
	}
	
	

}
