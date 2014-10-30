package cobweb.m24;

import java.util.HashMap;

import org.geotools.feature.FeatureCollection;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class StoreWFSWorkItemHandlerClient implements WorkItemHandler{

	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		String wpsURL = (String) workItem.getParameter("wpsURL");
		String wpsProcessID = "storage.geoserver.StoreWFSOutput";
		String catalogURL = (String) workItem.getParameter("catalogURL");
		
		HashMap<String, Object> wpsInputs = new HashMap<String, Object>();
		
		FeatureCollection fc = (FeatureCollection) workItem.getParameter("inputCarriedData");
		wpsInputs.put("inputObservations", fc);
		
		StoreWFSClient client = new StoreWFSClient(wpsURL, wpsProcessID, wpsInputs,catalogURL );
		
		HashMap<String, Object> results = new HashMap<String,Object>();
		
		manager.completeWorkItem(workItem.getId(), results);
		
	}
	
	
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		
	}

}
