package cobweb.m24;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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
			System.out.println("trying to get describeProcessDocument");
			System.out.println(describeProcessDocument);
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
			ProcessDescriptionType processDescription,HashMap<String, Object> inputs) {
		org.n52.wps.client.ExecuteRequestBuilder executeBuilder = new org.n52.wps.client.ExecuteRequestBuilder(
				processDescription);

		HashMap<String, Object> result = new HashMap<String, Object>();

		for (InputDescriptionType input : processDescription.getDataInputs()
				.getInputArray()) {
			String inputName = input.getIdentifier().getStringValue();
			Object inputValue = inputs.get(inputName);

			if (input.getLiteralData() != null) {
				if (inputValue instanceof String) {
					executeBuilder.addLiteralData(inputName,(String) inputValue);
				}
			} else if (input.getComplexData() != null) {
				System.out.println("Generic WPS Client HERE 3 " + inputName	+ " " + inputValue + " " + inputValue.getClass());
				if (inputValue instanceof FeatureCollection	|| inputValue instanceof GTVectorDataBinding) {
					System.out.println("instance of FeatureCollection || ObjectDataType "+ inputName);
					IData data = (IData) inputValue;
					try {
						executeBuilder.addComplexData((String) inputName, data,	null, null, "application/json");
					} catch (WPSClientException e) {
						e.printStackTrace();
					}
				}
				if (inputName.equals("inputSurfaceModel")) {
					executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null, "text/plain");
				} else if (inputValue instanceof String) {
					System.out.println("instance of string " + inputName);
					executeBuilder.addComplexDataReference(inputName,(String) inputValue, null, null,"application/json");
				}

			}
			if (inputValue == null && input.getMinOccurs().intValue() > 0) {
				try {
					throw new IOException("Property not set, but mandatory: " + inputName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		for (OutputDescriptionType output : processDescription.getProcessOutputs().getOutputArray()) {
			String outputName = output.getIdentifier().getStringValue();
			if (output.getComplexOutput() != null) {
				executeBuilder.setSchemaForOutput("application/json",outputName);
			}
			else if (output.getLiteralOutput() != null) {

			}
		}

		ExecuteDocument execute = executeBuilder.getExecute();
		execute.getExecute().setService("WPS");
		WPSClientSession wpsClient = WPSClientSession.getInstance();

		Object responseObject;
		try {
			responseObject = wpsClient.execute(url, execute);

			// parse response document

			if (responseObject instanceof ExecuteResponseDocument) {
				ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
				ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(
						execute, response, processDescription);

				for (OutputDescriptionType output : processDescription
						.getProcessOutputs().getOutputArray()) {

					String outputName = output.getIdentifier().getStringValue();
					try {
						Object outputValue = analyser.getComplexData(
								outputName, GTVectorDataBinding.class);
						FeatureCollection tempF = ((GTVectorDataBinding) outputValue)
								.getPayload();

						if (outputValue != null
								&& outputValue instanceof GTVectorDataBinding) {
							result.put(outputName, outputValue);

						}

						else if (output.getLiteralOutput() != null) {

							Object literalOutput = output.getLiteralOutput();
							result.put(outputName, literalOutput);
						}

					}

					catch (NullPointerException e) {
						System.out.println("Output " + outputName
								+ " contains no results");
					}
				}
			}
		} catch (WPSClientException e1) {
			System.out.println("error generating response object " + e1);
			e1.printStackTrace();
		}

		System.out.println("result collection size " + result.size());
		return result;
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
