package cobweb.m24;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * 
 * @author Sam Meek
 * Class to create an interface between the JBPM workflow engine and the WPS client libraries
 * Provides an instantiation for a WorkItemHandler for each WPS process within JBPM
 *
 */

public class GenericWorkItemHandlerClient implements WorkItemHandler {
	
	/**
	 * This class has two main methods, executeWorkItem which executes for each exposed WPS process
	 * and abortWorkItem which executes when the workItemHandler is aborted
	 * 
	 */
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {				
		System.out.println("GenericWorkItemHandler: executing work item");		
		Map<String, Object> inputs = new HashMap<String, Object>();
		
		inputs = workItem.getParameters();
		
		Iterator<Entry<String, Object>> it = inputs.entrySet().iterator();
		String wpsURL = new String();
		String wpsProcessID = new String();
		
		HashMap<String, Object>variables = new HashMap<String, Object>();
		
		while (it.hasNext()){
			
			Map.Entry<String, Object> pairs = (Map.Entry<String, Object>)it.next();
			
			/**
			 * WPSs have two required inputs: 1) wpsURL (the url of the WPS e.g. http://geoprocessing.nottingham.ac.uk/wps/WebProcessingService?)
			 * 2) processDescription (the description of the process e.g. pillar.lbs.LineOfSight)
			 * This is currently hard coded and not handled as an error if not entered.
			 * 
			 */
			
			System.out.println("GenericWorkItemHandler: Hashamap key-vals: " + pairs.getKey() + " " + pairs.getValue());
			if (pairs.getKey().equalsIgnoreCase("wpsURL")){	
				
				wpsURL = (String) pairs.getValue();
 			}
			
			else if (pairs.getKey().equalsIgnoreCase("processDescription")){
				
				wpsProcessID  = (String) pairs.getValue();
			}
			
			else{				

				variables.put(pairs.getKey(), pairs.getValue());
				
			}
			
			it.remove();
			
		}
		
		/**
		 * GenericWPSClient instantiates the WPS client
		 * The results HashMap returns the WPS result(s) 
		 * 
		 */
		
		GenericWPSClient wpsClient = new GenericWPSClient(wpsURL, wpsProcessID, variables, null);
		
		HashMap<String, Object> results = wpsClient.getOutputs();
		
		/**
		 * executes workItem and returns results HashMap
		 * 
		 * 
		 */
		System.out.println("Finished client execution.");
		
		
		manager.completeWorkItem(workItem.getId(), results);
	}
	
	/** 
	 * This needs to be implemented to handle a WorkItemHandler abort called from within the console
	 */
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub
		System.out.println("Abort work item.");
	}
}
