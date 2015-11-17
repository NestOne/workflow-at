package cobweb.m24;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;

import org.geotools.feature.FeatureCollection;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.commons.WPSConfig;
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;

public class GenericWPSClient {
	/**
	 * @author Sam Meek This is the main class that executes the WPS and handles
	 *         the result returned as a HashMap It is currently written to deal
	 *         with vector data and does not output raster data, the raster data
	 *         inputs are passed as a link and parsed server side.
	 * 
	 *         It also only uses JSON (application/json) but could be changed
	 *         quite easily to implement the default.
	 */

	String wpsURL;
	String wpsProcessID;
	HashMap<String, Object> wpsInputs;
	HashMap<String, Object> outputs;
	Map<String, Object> wpsOutputs;
	String catalogURL;
	FeatureCollection featureCollection;
	FeatureCollection inputFeatureCollection;

	String tempDir;

	public GenericWPSClient(String wpsURL, String wpsProcessID,
			HashMap<String, Object> wpsInputs, String catalogURL) {

		this.wpsURL = wpsURL;
		this.wpsProcessID = wpsProcessID;
		this.wpsInputs = wpsInputs;
		this.catalogURL = catalogURL;

		System.out.println("WPS URL " + wpsURL);
		System.out.println("WPS Process ID " + wpsProcessID);
		/**
		 * this is a hard coded path for the Linux installation and should be
		 * passed as a variable
		 */
		// WPSConfig.getInstance("/usr/local/apache-tomcat-7.0.55/webapps/wps/config/wps_config_geotools.xml");
		WPSConfig
		.getInstance("C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\wps\\config\\wps_config_geotools.xml"); // For

		// tempDir = "/tmp/tomcat7-tomcat7-tmp/"; // linux
		tempDir = "C:\\tmp\\"; // windows

		try {
			ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
					wpsURL, wpsProcessID);
			System.out.println("trying to get describeProcessDocument...");
			//System.out.println(describeProcessDocument);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			CapabilitiesDocument capabilitiesDocument = requestGetCapabilities(wpsURL);
			ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
					wpsURL, wpsProcessID);

			outputs = executeProcess(wpsURL, wpsProcessID,
					describeProcessDocument, wpsInputs);

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

	public CapabilitiesDocument requestGetCapabilities(String url)
			throws WPSClientException {

		System.out.println("Requesting get capabilities document");

		WPSClientSession wpsClient = WPSClientSession.getInstance();

		wpsClient.connect(url);

		CapabilitiesDocument capabilities = wpsClient.getWPSCaps(url);

		ProcessBriefType[] processList = capabilities.getCapabilities()
				.getProcessOfferings().getProcessArray();

		for (ProcessBriefType process : processList) {
			// System.out.println(process.getIdentifier().getStringValue());
		}
		return capabilities;
	}

	/**
	 * 
	 * @param url
	 *            - WPS url
	 * @param processID
	 *            - process description
	 * @return outputs - hashmap of the results (usually a FeatureCollection
	 * @throws IOException
	 *             - this needs replacing
	 */

	public ProcessDescriptionType requestDescribeProcess(String url,
			String processID) throws IOException {

		WPSClientSession wpsClient = WPSClientSession.getInstance();

		ProcessDescriptionType processDescription = wpsClient
				.getProcessDescription(url, processID);

		InputDescriptionType[] inputList = processDescription.getDataInputs()
				.getInputArray();

		for (InputDescriptionType input : inputList) {
			System.out.println(input.getIdentifier().getStringValue());
		}
		return processDescription;
	}
	
	

	public HashMap<String, Object> executeProcess(String url, String processID,
			ProcessDescriptionType processDescription,
			HashMap<String, Object> inputs) {
		org.n52.wps.client.ExecuteRequestBuilder executeBuilder = new org.n52.wps.client.ExecuteRequestBuilder(
				processDescription);

		HashMap<String, Object> result = new HashMap<String, Object>();

		for (InputDescriptionType input : processDescription.getDataInputs()
				.getInputArray()) {
			String inputName = input.getIdentifier().getStringValue();
			Object inputValue = inputs.get(inputName);

			if (input.getLiteralData() != null) {
				System.out.println("WPS URL " + wpsURL);
				if (inputValue instanceof String) {
					executeBuilder.addLiteralData(inputName,
							(String) inputValue);
				}
			} else if (input.getComplexData() != null) {		
							
				System.out.println("Generic WPS Client HERE 3 " + inputName
						+ " " + inputValue + " " + inputValue.getClass());
				// System.out.println("Here 4 " + inputValue.toString());
				// Complexdata by value
				if (inputValue instanceof FeatureCollection
						|| inputValue instanceof GTVectorDataBinding) {
					System.out
							.println("instance of FeatureCollection || ObjectDataType "
									+ inputName);
					// IData data = new GTVectorDataBinding(
					// (FeatureCollection) inputValue);
					IData data = (IData) inputValue;
					try {
						executeBuilder.addComplexData((String) inputName, data,
								null, null, "application/json");
					} catch (WPSClientException e) {
						System.out.println("add complex data exception " + e);
						e.printStackTrace();
					}
				}
				if (inputName.equals("inputSurfaceModel")) {
					System.out.println("got an inputSurfaceModel");					
					executeBuilder.addComplexDataReference(inputName,
							(String) inputValue, null, null, "text/plain");
				} 
				if (inputName.equals("inputRasterModel")) {
					System.out.println("got an inputRasterModel");					
					executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null, "image/tiff");	
					
				}
				else if (inputValue instanceof String) {
					System.out.println("instance of string " + inputName);	
					executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null,"application/json");
				}

			}
			if (inputValue == null && input.getMinOccurs().intValue() > 0) {
				try {
					throw new IOException("Property not set, but mandatory: "
							+ inputName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		for (OutputDescriptionType output : processDescription
				.getProcessOutputs().getOutputArray()) {

			System.out.println("Looping over output types to hardcode schema");
			
			String outputName = output.getIdentifier().getStringValue();
						
			if (output.getComplexOutput() != null) {
				
				System.out.println("Setting schema for output: " + outputName);
				
				if (outputName.equals("outputRasterModel")) {		
					System.out.println("Setting schema for an outputRasterModel: " + outputName);
					executeBuilder.setSchemaForOutput("image/tiff",	outputName);
					
				} else if (processID.contains("org.n52.wps.server.r")) { 
					System.out.println("Setting output mime type for R process: " + outputName);
					 executeBuilder.setMimeTypeForOutput("text/xml; subtype=gml/3.1.0", outputName); 
					 executeBuilder.setSchemaForOutput("http://schemas.opengis.net/gml/3.1.0/base/feature.xsd", outputName);					
				}else {
					System.out.println("Setting schema for: " + outputName);
					executeBuilder.setSchemaForOutput("application/json",outputName);
				}
				
				 
				/**
				 * String mimeType =
				 * output.getComplexOutput().getSupported().getFormatArray
				 * (1).getMimeType();
				 * executeBuilder.setMimeTypeForOutput(mimeType, outputName);
				 * 
				 * String schema =
				 * output.getComplexOutput().getSupported().getFormatArray
				 * (1).getSchema(); if(schema!=null){
				 * 
				 * executeBuilder.setSchemaForOutput( schema, outputName) }
				 * System.out.println("outputName " + outputName + " mimeType "
				 * + mimeType + " schema " + schema);
				 * 
				 * }
				 **/
			}

			else if (output.getLiteralOutput() != null) {

			}
		}

		/**
		 * executeBuilder.setMimeTypeForOutput("text/xml; subtype=gml/3.1.0",
		 * "result"); executeBuilder.setSchemaForOutput(
		 * "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd", "result");
		 * 
		 * executeBuilder.setMimeTypeForOutput("text/xml; subtype=gml/3.1.0",
		 * "qual_result"); executeBuilder.setSchemaForOutput(
		 * "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd",
		 * "qual_result");
		 **/

		// executeBuilder.setMimeTypeForOutput("text/plain", "metadata");

		ExecuteDocument execute = executeBuilder.getExecute();
		execute.getExecute().setService("WPS");
		WPSClientSession wpsClient = WPSClientSession.getInstance();

		Object responseObject;
		try {
			responseObject = wpsClient.execute(url, execute);
			//System.out.println("printing execute request...");
			//System.out.println(execute.toString());
			dumpTextToFile(execute.toString(), processID);
				
			if (responseObject instanceof ExecuteResponseDocument) {
				ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
				ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser( execute, response, processDescription);
				System.out.println("HERE 6");
				System.out.println("Extracting outputs from process description...");
				System.out.println("Process Outputs Array size: " + processDescription.getProcessOutputs().getOutputArray().length);				
								
				for (OutputDescriptionType output : processDescription.getProcessOutputs().getOutputArray()) {
					System.out.println("getting an ouputIdentifier..."); 
					String outputName = output.getIdentifier().getStringValue();
					System.out.println("ouputIdentifier: "+ outputName);
					System.out.println("ouputIdentifierToString: " + output.toString());									
					System.out.println("ouput Class: "+ output.getClass());			
					System.out.println("ouput Identifier Class (toString): "+ output.getIdentifier().getClass().toString());			
					 
					try {
						System.out.println("Attempting to resolve output format");
						
						//Check if output raster or vector data
						if (outputName.equals("outputRasterModel")) {
							System.out.println("Handling output as raster");
							//handling raster outputz
							Object outputValue = analyser.getComplexData(outputName, GenericFileDataBinding.class); //What does this do
					
							System.out.println("HERE 7 " + outputName);
							
							GenericFileData tempGrid = ((GenericFileDataBinding) outputValue).getPayload();
							if (outputValue != null && outputValue instanceof GenericFileDataBinding) {
								System.out.println("HERE 8 output name " + outputName);
								result.put(outputName, outputValue);								
							} else{
								System.out.println("GridCoverage creation not successful");
							}
						} else {			
							System.out.println("Handling output as vector or literal");
							//Handling vector output							
							if (outputName.equals("sessionInfo") || outputName.equals("warnings")) {
								System.out.println("skipping R outputs");
							} else {
								System.out.println("Assumming GTVectorDataBinding");
								Object outputValue = analyser.getComplexData(outputName, GTVectorDataBinding.class); //What does this do							
								System.out.println("HERE 7 " + outputName + " "	+ outputValue);
								FeatureCollection tempF = ((GTVectorDataBinding) outputValue).getPayload();
	
								if (outputValue != null && outputValue instanceof GTVectorDataBinding) {
									System.out.println("HERE 8 output name "
											+ outputName + " outputValue size "
											+ tempF.size());
									result.put(outputName, outputValue);
								}
								else if (output.getLiteralOutput() != null) {
									Object literalOutput = output.getLiteralOutput();
									result.put(outputName, literalOutput);
								}
							}
						}					

					} catch (NullPointerException e) {
						System.out.println("Output " + outputName
								+ " contains no results");
					}
				}
				System.out.println("Completed loop of Outputs Array");		
			} else {
				System.out.println("responseObject not an instanceof ExecuteResponseDocument");		
			}
		} catch (WPSClientException e1) {
			System.out.println("error generating response object " + e1);
			e1.printStackTrace();
		}

		System.out.println("result collection size " + result.size());
		return result;
	}
	
	
		private void dumpTextToFile(String textToDump, String outputName) {
			try {	        
				Date date = new Date() ;
				UUID uuid = UUID.randomUUID();
				String randomUUIDString = uuid.toString();

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
				File file = new File(tempDir + dateFormat.format(date) +"_" + randomUUIDString +"_"+ outputName +".txt") ;
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
