package cobweb.m24;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import net.opengis.ows.x11.BoundingBoxType;
import net.opengis.ows.x11.CodeType;
import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ComplexDataCombinationType;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.DataInputsType;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.InputType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;

import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.n52.wps.client.ExecuteRequestBuilder;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.commons.WPSConfig;
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.complex.PlainStringBinding;
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;














import com.metaworkflows.MetaWorkflow;


public class GenericWPSClient {
	/**
	 * @author Sam Meek & Julian Rosser. This is the main class that executes a WPS and handles
	 *         the result returned as a HashMap. The class creates an interface between workitems 
	 *         and WPS by extracting the data and parameters for a work-item / WPS-endpoint, 
	 *         mapping this into an execute request object, executing this on the WPS and then
	 *         parsing the responses.  
	 *         
	 *         Vector data and raster inputs and outputs are supported without requiring bindings
	 *         to specific data formats within this client code by passing references to data to the
	 *         WPS and specifying asReference(TRUE) in the output data request.
	 */
	
	
	/* DEBUG OPTIONS */
	final boolean DEBUG = true;
	//String tempDir = "/tmp/tomcat7-tomcat7-tmp/"; // linux
	String tempDir = "C:\\tmp\\"; // windows
	final boolean DEBUG_DUMP_REQS_TO_FILE = true; //write out the exec reqs and responses to file in the tempDir	 
	
	
	/* WPS OPTIONS */
	final static boolean globalSetAsReference = true; //Results are returned as url references (i.e pointing to temp dir on the WPS)  
	final static String globalPreferredOutputMimeType = "text/xml; subtype=gml/3.1.1";
	//final static String globalPreferredOutputMimeType = "application/json";
	
	//For pushing to WFS using the 52North WFS generator, the input data appears to need to be JSON for some reason!
	//final static String globalPreferredOutputMimeType = "application/WFS"; 	final static boolean globalSetAsReference = true;

	
	private boolean readFromGeonetwork = false; //Set as TRUE for bpmn catalogue profile execution
	private boolean writeOutputToGeonetwork = false; //Set as TRUE for both BPMN and WPS Wrapper profile execution
	
	private boolean downloadResponseData = true; 
	
	String wpsURL;
	String wpsProcessID;
	HashMap<String, Object> wpsInputs;
	HashMap<String, Object> outputs;
	Map<String, Object> wpsOutputs;
	String catalogURL;
	FeatureCollection featureCollection;
	FeatureCollection inputFeatureCollection;

	public GenericWPSClient(String wpsURL, String wpsProcessID,
			HashMap<String, Object> wpsInputs, String catalogURL) {

		this.wpsURL = wpsURL;
		this.wpsProcessID = wpsProcessID;
		this.wpsInputs = wpsInputs;
		this.catalogURL = catalogURL;
		//this.useGeonetwork = useGeoNetwork;

		System.out.println("WPS URL " + wpsURL);
		System.out.println("WPS Process ID " + wpsProcessID);
		/**
		 * this is a hard coded path for the Linux installation and should be
		 * passed as a variable
		 */
		// WPSConfig.getInstance("/usr/local/apache-tomcat-7.0.55/webapps/wps/config/wps_config_geotools.xml");
		WPSConfig
		.getInstance("C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\wps\\config\\wps_config_geotools.xml"); // For win installation			

		try {
			CapabilitiesDocument capabilitiesDocument = requestGetCapabilities(wpsURL); //Doesn't appear to be used
			ProcessDescriptionType describeProcessDocument = requestDescribeProcess(wpsURL, wpsProcessID);

			//ExecuteProcessAsLinks replaced executeProcess Jan 2016
			outputs = executeProcessAsLinks(wpsURL, wpsProcessID,	describeProcessDocument, wpsInputs);

		} catch (WPSClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param url
	 *            - WPS url
	 * @return capabilities - the capabilities document of the WPS
	 * @throws WPSClientException
	 *             - a 52 North class to handle WPS exceptions
	 */
	public CapabilitiesDocument requestGetCapabilities(String url) throws WPSClientException {
		System.out.println("Requesting get capabilities document..." + url);

		WPSClientSession wpsClient = WPSClientSession.getInstance();
		wpsClient.connect(url);
		CapabilitiesDocument capabilities = wpsClient.getWPSCaps(url);
		ProcessBriefType[] processList = capabilities.getCapabilities()
				.getProcessOfferings().getProcessArray();
		
		System.out.println("Number of capabilities " + processList.length);
		//For debugging...
		/*
		if (DEBUG) { 
			for (ProcessBriefType process : processList) {
				System.out.println(process.getIdentifier().getStringValue());
			}
		}*/
		
		return capabilities;
	}

	public ProcessDescriptionType requestDescribeProcess(String url,String processID) throws IOException {
		System.out.println("Requesting describe process document...");		
		WPSClientSession wpsClient = WPSClientSession.getInstance();
		ProcessDescriptionType processDescription = wpsClient.getProcessDescription(url, processID);
		if (DEBUG)System.out.println("Process description:");
		if (DEBUG)System.out.println(processDescription);		
		InputDescriptionType[] inputList = processDescription.getDataInputs().getInputArray();
		/*
		if (DEBUG) {
			for (InputDescriptionType input : inputList) {
				System.out.println("Describe process identifier: " + input.getIdentifier().getStringValue());
			}
		}*/
		return processDescription;
	}
	
		
	/**
	 * Adds in the WPS input details to an executeRequest 
	 * 
	 * @param executeBuilder - the execReq being built
	 * @param inputs - hashmap of data and values for the process from the BPMN
	 * @param processDescription - the processDescription details for this process  
	 * @return ExecuteResquestBuilder 
	 * @throws IOException
	 */	
	private ExecuteRequestBuilder inputSetter(org.n52.wps.client.ExecuteRequestBuilder executeBuilder, HashMap<String, Object> inputs, ProcessDescriptionType processDescription) throws IOException {

		//Loop over the inputs (data and params) and create the ExectuteRequest   
		for (InputDescriptionType input : processDescription.getDataInputs().getInputArray()) {

			String inputName = input.getIdentifier().getStringValue();
			Object inputValue = inputs.get(inputName);
			System.out.println("WPS URL " + wpsURL);
			
		
			//Handle literal data
			if (input.getLiteralData() != null) {
				System.out.println("Handling as literal data for " + inputName);
				if (inputValue instanceof String) {
					executeBuilder.addLiteralData(inputName, (String) inputValue);
				}						
			} else if (inputName.equals("outputBBOX"))  {
				System.out.println("Generic WPS Client Get bbox data" + input.getBoundingBoxData().toString().toString());
				/*
				String hardcodedBbox = "<Data><BoundingBoxData crs='EPSG:4326' dimensions='2'><ns:LowerCorner>-179.983 -85.6221</ns:LowerCorner><ns:UpperCorner>180.0 85.0</ns:UpperCorner></BoundingBoxData></Data>";
				System.out.println("hardcodedBbox: "+ hardcodedBbox.toString());
				*/				
				//Manually add in the bbox to the request  
				DataInputsType diType = executeBuilder.getExecute().getExecute().getDataInputs();		
				InputType ide2 = diType.addNewInput();
				ide2.addNewIdentifier().setStringValue("outputBBOX");
				BoundingBoxType bb =ide2.addNewData().addNewBoundingBoxData();
				bb.setCrs("EPSG:4326");
				ArrayList ll = new ArrayList<Double>();
				ll.add(-179.983);
				ll.add(-85.6221);
				bb.setLowerCorner(ll);
				ArrayList ur = new ArrayList<Double>();
				ur.add(180.0);//{180.0, 85.01}; 
				ur.add(85.01);
				bb.setUpperCorner(ur);
				//System.out.println("executeBuilder.getExecute()" + executeBuilder.getExecute().toString());
				
				/*
			} else if (inputName.equals("filter"))  { // This is a hack really
				System.out.println("Filter for CQL");
				//executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null,"text/plain; subtype=cql");
				executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null,"text/plain; subtype=cql");
				*/				
			} else if (input.getComplexData() != null) {						
				System.out.println("Generic WPS Client HERE 3 " + inputName	+ " " + inputValue + " ");										
				
				if (inputValue instanceof String) {
					System.out.println("instance of string. inputName: " + inputName);
					System.out.println("this.wpsProcessID " + wpsProcessID );
					
					
					if (downloadResponseData && wpsProcessID.equals("gs:Heatmap")) {
						System.out.println("add in file data");
						//executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null,null);	//Use the default of the input data
												
							
						String tempData = readFile("C:\\tmp\\temporary.xml");
						try {
							executeBuilder.addComplexData(inputName, tempData, null, null, null);
						} catch (WPSClientException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("added file data to exec");
						/*
						//Manually add in the bbox to the request  
						DataInputsType diType = executeBuilder.getExecute().getExecute().getDataInputs();		
						InputType ide2 = diType.addNewInput();
						ide2.addNewIdentifier().setStringValue("outputBBOX");
						BoundingBoxType bb =ide2.addNewData().addNewBoundingBoxData();
						bb.setCrs("EPSG:4326");
						ArrayList ll = new ArrayList<Double>();
						ll.add(-179.983);
						ll.add(-85.6221);
						bb.setLowerCorner(ll);
						ArrayList ur = new ArrayList<Double>();
						ur.add(180.0);//{180.0, 85.01}; 
						ur.add(85.01);
						*/
						
					} else {						
						executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null,null);	//Use the default of the input data
					}	
					//executeBuilder.addComplexDataReference((String) inputName,(String) inputValue,null,null, "application/json");					
					//executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null,"text/xml; subtype=gml/3.1.0"); //Request gml3 from WFS
				}
			}
			if (inputValue == null && input.getMinOccurs().intValue() > 0) {
				System.out.println("Null inputValue for a mandatory field.");	
				try {
					throw new IOException("Property not set, but mandatory: "
							+ inputName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("Finished constructing execute request inputs");
		return executeBuilder;			
	
	}//eof
	
	
	
	String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
	
	
	/**
	 * Adds in the WPS input details from a catalogue entry to an executeRequest 
	 * 
	 * @param executeBuilder - the execReq being built
	 * @param inputs - hashmap of data and values for the process from the BPMN
	 * @param processDescription - the processDescription details for this process  
	 * @return ExecuteResquestBuilder 
	 * @throws IOException
	 */	
	private ExecuteRequestBuilder inputSetterCatalogue(org.n52.wps.client.ExecuteRequestBuilder executeBuilder, HashMap<String, Object> inputs, ProcessDescriptionType processDescription) throws IOException {

		//Loop over the inputs (data and params) and create the ExectuteRequest   
		for (InputDescriptionType input : processDescription.getDataInputs().getInputArray()) {
			String inputName = input.getIdentifier().getStringValue();
			Object inputValue = inputs.get(inputName);
			System.out.println("WPS URL " + wpsURL);
					
			//Handle literal data
			if (input.getLiteralData() != null) {
				System.out.println("WPS URL " + wpsURL);
				if (inputValue instanceof String) {
					executeBuilder.addLiteralData(inputName, (String) inputValue);
				}	
				
			} else if (inputName.equals("outputBBOX"))  {
				System.out.println("Generic WPS Client Get bbox data" + input.getBoundingBoxData().toString().toString());
				/*
				String hardcodedBbox = "<Data><BoundingBoxData crs='EPSG:4326' dimensions='2'><ns:LowerCorner>-179.983 -85.6221</ns:LowerCorner><ns:UpperCorner>180.0 85.0</ns:UpperCorner></BoundingBoxData></Data>";
				System.out.println("hardcodedBbox: "+ hardcodedBbox.toString());
				*/				
				//Manually add in the bbox to the request  
				DataInputsType diType = executeBuilder.getExecute().getExecute().getDataInputs();		
				InputType ide2 = diType.addNewInput();
				ide2.addNewIdentifier().setStringValue("outputBBOX");
				BoundingBoxType bb =ide2.addNewData().addNewBoundingBoxData();
				bb.setCrs("EPSG:4326");
				ArrayList ll = new ArrayList<Double>();
				ll.add(-179.983);
				ll.add(-85.6221);
				bb.setLowerCorner(ll);
				ArrayList ur = new ArrayList<Double>();
				ur.add(180.0);//{180.0, 85.01}; 
				ur.add(85.01);
				bb.setUpperCorner(ur);
				//System.out.println("executeBuilder.getExecute()" + executeBuilder.getExecute().toString());
				
			//Handle as ComplexData ie vectors, rasters
			} else if (input.getComplexData() != null) {						
				System.out.println("Generic WPS Client HERE 3 " + inputName	+ " " + inputValue + " ");										
				
				if (inputValue instanceof String) {
					
					System.out.println("GeoNetwork: using GeoNetwork to inputValue");
					try {
				    	MetaWorkflow metaWorkflow = new MetaWorkflow();
				    	Element retrievedElement = metaWorkflow.GetMetadata((String)inputValue);			        
						//get the location of the retrieved element
				        String Urllocation = metaWorkflow.getLocationElement(retrievedElement).getText();				        
				        System.out.println("GeoNetwork: UrlLocation for request " + Urllocation); 
				        inputValue = Urllocation;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					System.out.println("instance of string. inputName: " + inputName);					
					executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null,null);	//Use the default of the input data
					
					//executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null,"application/json"); //Request json from WFS
					//executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null,"text/xml; subtype=gml/3.1.0"); //Request gml3 from WFS
				}
			}
			if (inputValue == null && input.getMinOccurs().intValue() > 0) {
				System.out.println("Null inputValue for a mandatory field.");	
				try {
					throw new IOException("Property not set, but mandatory: "
							+ inputName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("Finished constructing execute request inputs");
		return executeBuilder;			
	
	}//eof
	
	
	
	
	
	/**
	 * Adds in the WPS output details to an executeRequest 
	 * 
	 * @param executeBuilder - the execReq being built
	 * @param inputs - hashmap of data and values for the process from the BPMN
	 * @param processDescription - the processDescription details for this process  
	 * @return ExecuteResquestBuilder 
	 * @throws IOException
	 */	
	private ExecuteRequestBuilder outputSetter(org.n52.wps.client.ExecuteRequestBuilder executeBuilder, HashMap<String, Object> inputs, ProcessDescriptionType processDescription) throws IOException {
						
		//Loop over process outputs to determine what output types should be requested
		for (OutputDescriptionType output : processDescription.getProcessOutputs().getOutputArray()) {
			System.out.println("Looping over output types to hardcode schema");	
			String outputName = output.getIdentifier().getStringValue();
			System.out.println("Current output id: " + output.getIdentifier().getStringValue());	
			if (output.getComplexOutput() != null) {			
				System.out.println("Setting mimetype and schema for complex output: " + outputName);		
			
				String[] mimeTypeAndSchema = determineMimeTypeAndSchema(output);				
				System.out.println("Mime type and scehma " + mimeTypeAndSchema[0] + " " + mimeTypeAndSchema[1]);	
				
				//Get an appropriate mimeType and schema for this output
				executeBuilder.setMimeTypeForOutput(mimeTypeAndSchema[0],outputName);				
				if (mimeTypeAndSchema[1] != null) {
					executeBuilder.setSchemaForOutput(mimeTypeAndSchema[1],outputName);
				}			
				
				executeBuilder.setAsReference(outputName, globalSetAsReference); //set the return output value as a reference									 
				
			} else if (output.getLiteralOutput() != null) {
				System.out.println("Literal type for output ");				
				executeBuilder.setResponseDocument(outputName, null, null, null);				
				executeBuilder.setAsReference(outputName, globalSetAsReference); //set the return output value as a reference				
			}
		}
		return executeBuilder;
	} //eof
	
	
	
	/**
	 * Takes an OutputDescriptionType and returns an array of mimeType [0]
	 * and the schema [1] taking into account a preference for output format
	 * 
	 * @param output is the output type.
	 * @return array of mimeType and schema
	 */	
	private static String[] determineMimeTypeAndSchema(OutputDescriptionType output) {
	
		ComplexDataCombinationType defaultType = output.getComplexOutput().getDefault();
		ComplexDataCombinationsType supportedFormats = output.getComplexOutput().getSupported();
		
        String[] mimeAndSchema;
        mimeAndSchema = new String[2];  	    
	    
 	    //Check if the process supports the workflow preferred format  
		for (int index = 0; index < supportedFormats.sizeOfFormatArray(); index++) {
			//System.out.println(supportedFormats.getFormatArray(index).getMimeType());
			ComplexDataDescriptionType format = supportedFormats.getFormatArray(index);
			if (format.getMimeType().equalsIgnoreCase(globalPreferredOutputMimeType)) {
				mimeAndSchema[0] = format.getMimeType();
				mimeAndSchema[1]= format.getSchema();
			} 					
		}
		//if still not got a good mimeType use the default
		if (mimeAndSchema[0] == null){
			mimeAndSchema[0] = defaultType.getFormat().getMimeType();
			mimeAndSchema[1] = defaultType.getFormat().getSchema();
		}		
		return mimeAndSchema;		
	}
	
	
	
	/**
	 * 
	 * @param url
	 *            - WPS url
	 * @param processID
	 *            - process description
	 * @return outputs - hashmap of the results (as a URL links to data (e.g. not FeatureCollections)
	 * @throws IOException
	 *             - this needs replacing
	 */
	public HashMap<String, Object> executeProcessAsLinks(String url, String processID,	ProcessDescriptionType processDescription,	HashMap<String, Object> inputs) throws IOException {
				
		org.n52.wps.client.ExecuteRequestBuilder executeBuilder = new org.n52.wps.client.ExecuteRequestBuilder(processDescription);
		System.out.println("Trying to execute process...");
		HashMap<String, Object> result = new HashMap<String, Object>();				

		//Set the input data types	
		if (readFromGeonetwork) {
			System.out.println("GeoNetwork: using GeoNetwork for input values");
			executeBuilder = inputSetterCatalogue(executeBuilder, inputs, processDescription);
		} else {
			executeBuilder = inputSetter(executeBuilder, inputs, processDescription);
		}
				
		//Set the output data types
		executeBuilder = outputSetter(executeBuilder, inputs, processDescription);
		
		//Get the ExecuteDocument and get ready to execute
		ExecuteDocument execute = executeBuilder.getExecute();	
		
		if (DEBUG) System.out.println("Execute Request: ");
		//if (DEBUG) System.out.println(execute.toString());		
		if (DEBUG_DUMP_REQS_TO_FILE) dumpTextToFile(execute.toString(), processID);
		
		execute.getExecute().setService("WPS");	
		WPSClientSession wpsClient = WPSClientSession.getInstance();
		Object responseObject;
		try {
			responseObject = wpsClient.execute(url, execute);
			if (responseObject instanceof ExecuteResponseDocument) {
				System.out.println("Handling responseObject as instanceof ExecuteResponseDocument");
				ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
				ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser( execute, response, processDescription);
				System.out.println("HERE 6");
				System.out.println("Extracting outputs from process description...");
				System.out.println("Process Outputs Array size: " + processDescription.getProcessOutputs().getOutputArray().length);				
				int outputCounter = 0;
				for (OutputDescriptionType output : processDescription.getProcessOutputs().getOutputArray()) {
					System.out.println("getting an ouputIdentifier..."); 
					//System.out.println("Output counter: "+ outputCounter); 				
					
					String outputName = output.getIdentifier().getStringValue();
					System.out.println("ouputIdentifier: "+ outputName);
					System.out.println("ouputIdentifierToString: " + output.toString());									
					System.out.println("ouput Class: "+ output.getClass());			
					System.out.println("ouput Identifier Class (toString): "+ output.getIdentifier().getClass().toString());					 
					try {
						System.out.println("Attempting to resolve output format");

							//Handling vector output
							System.out.println("Handling output as vector or literal");													
							//Hardcoded check if R extra data is here, can ignore 
							if (outputName.equals("sessionInfo") || outputName.equals("warnings")) {
								System.out.println("skipping R outputs");			
							} else {
								System.out.println("checking for literal or vector string link as output");
								if (DEBUG_DUMP_REQS_TO_FILE) dumpTextToFile(responseObject.toString(), processID + " vector response");								
								
								if (output.getLiteralOutput() != null) {
									System.out.println("Handling literal output in process as string");
									//Round about way to get a literal value using 52n client lib: 
									String literalStr = response.getExecuteResponse().getProcessOutputs().getOutputArray(outputCounter).getData().getLiteralData().getStringValue();
									System.out.print("Literal output: " + literalStr);
									result.put(outputName, literalStr);	
								} else {									
									System.out.println("Handling as a string link");					
									//Handling vector as a string link, as might be returned by WPS execute request return by reference 
									//Note, a "only whitespace content allowed" compilation error indicates problems with the definition of data types
									//between the input / output variables e.g. as defined in CustomWorkItem work item doc.
									//Object outputValue2 = analyser.getComplexReferenceByIndex(0);
									
									if (downloadResponseData) {
										System.out.println("Attempt to download response data for ...  " + outputName);
										System.out.println("download start: " + System.currentTimeMillis());
										
										//Object tempValue = analyser.getComplexData(outputName, GenericFileDataBinding.class);
										Object tempValue  = analyser.getComplexReferenceByIndex(outputCounter);
										String tempValue2 = (String) tempValue;
										//URL myURL = new URL(tempValue2);
										InputStream input = new URL(tempValue2).openStream();											
										
										dumpTextToFile(getStringFromInputStream(input), processID + " temporary");
				
										//String tempResultDownload= getStringFromInputStream(input);									
										//System.out.println(tempResultDownload);
										System.out.println("download finish " + System.currentTimeMillis());
										
										System.out.println("downloaded data...");
									}
									
									Object outputValue2 = analyser.getComplexReferenceByIndex(outputCounter); 																		
									String outputValue = (String) outputValue2;								
									System.out.println("Vector Output, outputValue2: " + outputValue.toString());								
									System.out.println("Vector Output, string location resolved");
									System.out.println("Vector Output, reference: " + analyser.getComplexReferenceByIndex(0));
									

									//GeoNetwork registration of result	
									if (writeOutputToGeonetwork) {
										System.out.println("GeoNetwork: going to use GeoNetwork for registration");
										try {
									    	MetaWorkflow metaWorkflow = new MetaWorkflow();
											String insertedIdUrlEndpoint = metaWorkflow.RegisterResult(processID, outputValue);
											System.out.println("GeoNetwork: insertedIdUrlEndpoint " + insertedIdUrlEndpoint);
											result.put(outputName, insertedIdUrlEndpoint); 
										} catch (Exception e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									} else { //non-geonetwork
										if (outputValue != null && outputValue instanceof String) {
											//Add the result to the hashmap
											result.put(outputName, outputValue); 								
										} else {
											System.out.println("Getting vector file reference not successful");
										} 	
									}
								}												
							}						

					} catch (NullPointerException e) {
						System.out.println("Output " + outputName
								+ " contains no results");
					}
					outputCounter++;	
				}
				System.out.println("Completed loop of Outputs Array");		
			} else {
				System.out.println("responseObject not an instanceof ExecuteResponseDocument");
				System.out.println("Dumping responseObject to file...");
				if (DEBUG_DUMP_REQS_TO_FILE) dumpTextToFile(responseObject.toString(), processID + " response");
			}
		} catch (WPSClientException e1) {
			System.out.println("error generating response object " + e1);
			e1.printStackTrace();
		}

		System.out.println("result collection size " + result.size());

		System.out.println("Printing hashmap...");
		Iterator iterator = result.keySet().iterator();		  
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			String value = result.get(key).toString();
			System.out.println("HASHMAP: " + key + " " + value);
		}
		return result;
	}

	
	// convert InputStream to String
	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
	
	
	//Write data to a file for debugging of requests and responses
	private void dumpTextToFile(String textToDump, String outputName) {
		try {	        
			Date date = new Date() ;
			UUID uuid = UUID.randomUUID();
			String randomUUIDString = uuid.toString();

			String outputNameClean =  outputName.replaceAll(":", "_");
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
			File file = new File(tempDir + dateFormat.format(date) +"_" + randomUUIDString +"_"+ outputNameClean +".xml") ;
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(textToDump);
			out.close();
		} catch (IOException iox) {
			//do stuff with exception
			iox.printStackTrace();
		}
	}


	public HashMap<String, Object> getOutputs() {
		return outputs;
	}

	/**
	 * 
	 * @param xmlGenericData
	 * @return this needs to be implemented This method is partially implemented
	 *         to deal with metadata documents for GeoNetwork however, this was
	 *         somewhat abandoned when it was decided that metadata should be
	 *         tightly coupled to the features.
	 */

	private File parseXMLFromWPS(GenericFileData xmlGenericData) {
		File file = xmlGenericData.getBaseFile(true);
		InputStream fis;
		try {
			fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			File newFile = File.createTempFile("temp2", "xml");

			FileWriter fw = new FileWriter(newFile);

			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				String newLine = line.replaceAll("&gt;", ">")
						.replaceAll("&lt;", "<").replaceAll("&amp;", "&");
				System.out.println("NEWLINE " + newLine);
				fw.write(newLine);

			}
			fw.close();
			fis.close();
			return newFile;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
