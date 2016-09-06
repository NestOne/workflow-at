package clients.test;


import java.io.IOException;
import java.util.HashMap;

import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType.DataInputs;

import org.geotools.feature.FeatureCollection;
import org.n52.wps.client.ExecuteRequestBuilder;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

public class TestClientV2_geoserver {

	public static void main(String[] args) {
		TestClientV2_geoserver client = new TestClientV2_geoserver();
		client.testExecute();
	}

	public void testExecute() {

		String wpsURL = "http://localhost:8000/geoserver/ows?";
		// String wpsURL = "http://localhost:8010/wps/WebProcessingService?";
		// String processID = "storage.geoserver.StoreWFSOutput";
		String processID = "gs:CollectGeometries";

		try {
			ProcessDescriptionType describeProcessDocument = requestDescribeProcess(wpsURL, processID);
			System.out.println(describeProcessDocument);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			CapabilitiesDocument capabilitiesDocument = requestGetCapabilities(wpsURL);
			// System.out.println(capabilitiesDocument.toString());
			ProcessDescriptionType describeProcessDocument = requestDescribeProcess(wpsURL, processID);
			// define inputs
			HashMap<String, Object> inputs = new HashMap<String, Object>();
			// complex data by reference

			DataInputs di = describeProcessDocument.getDataInputs();
			// String sampleDataURL =
			// "http://localhost:8010/geoserver/cobweb/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=cobweb:SampleData&outputFormat=gml3";
			inputs.put("features", "http://localhost:8000/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:SnowdoniaNationalParkJapaneseKnotweedSurvey_IdAsString&outputFormat=application/json&srsName=EPSG:27700&maxfeatures=100");

			ExecuteResponseDocument executeResult;
			executeResult = executeProcess(wpsURL, processID, describeProcessDocument, inputs);

			System.out.println("result of execute:");
			System.out.println(executeResult.toString());

		} catch (WPSClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CapabilitiesDocument requestGetCapabilities(String url) throws WPSClientException {

		WPSClientSession wpsClient = WPSClientSession.getInstance();

		wpsClient.connect(url);

		CapabilitiesDocument capabilities = wpsClient.getWPSCaps(url);

		// System.out.println(capabilities.toString());

		ProcessBriefType[] processList = capabilities.getCapabilities().getProcessOfferings().getProcessArray();

		for (ProcessBriefType process : processList) {
			// System.out.println(process.getIdentifier().getStringValue());
		}
		return capabilities;
	}

	public ProcessDescriptionType requestDescribeProcess(String url, String processID) throws IOException {

		WPSClientSession wpsClient = WPSClientSession.getInstance();

		ProcessDescriptionType processDescription = wpsClient.getProcessDescription(url, processID);

		InputDescriptionType[] inputList = processDescription.getDataInputs().getInputArray();

		for (InputDescriptionType input : inputList) {
			// System.out.println(input.getIdentifier().getStringValue());
		}
		return processDescription;
	}

	public ExecuteResponseDocument executeProcess(String url, String processID, ProcessDescriptionType processDescription, HashMap<String, Object> inputs) throws Exception {
		ExecuteRequestBuilder executeBuilder = new ExecuteRequestBuilder(processDescription);

		for (InputDescriptionType input : processDescription.getDataInputs().getInputArray()) {
			String inputName = input.getIdentifier().getStringValue();
			Object inputValue = inputs.get(inputName);
			if (input.getLiteralData() != null) {
				if (inputValue instanceof String) {
					System.out.println(inputValue);
					executeBuilder.addLiteralData(inputName, (String) inputValue);
				}

				if (inputValue instanceof Integer) {
					System.out.println(inputValue);
					executeBuilder.addLiteralData(inputName, (String) inputValue);

				}
			} else if (input.getComplexData() != null) {
				// Complexdata by value
				if (inputValue instanceof FeatureCollection) {
					IData data = new GTVectorDataBinding((FeatureCollection) inputValue);
					executeBuilder.addComplexData(inputName, data, "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd", null, "text/xml; subtype=gml/3.1.0");
				}

				if (inputName.equals("inputSurfaceModel")) {

					executeBuilder.addComplexDataReference(inputName, (String) inputValue, null, null, "text/plain");
				} else if (inputValue instanceof String) {
					System.out.println("inputValue " + inputValue);
					executeBuilder.addComplexDataReference(inputName, (String) inputValue, null, null, "application/json");
				}

				else if (inputName.equalsIgnoreCase("inputImage")) {

					System.out.println("HERE GenericFileDataBinding " + inputValue.toString());

					IData inputData = (IData) inputValue;

					executeBuilder.addComplexData(inputName, inputData, null, null, "image/jpeg");
				}

				if (inputValue == null && input.getMinOccurs().intValue() > 0) {
					throw new IOException("Property not set, but mandatory: " + inputName);
				}
			}
		}

		ExecuteDocument execute = executeBuilder.getExecute();
		execute.getExecute().setService("WPS");

		System.out.println("execute resquest: ");

		System.out.println(execute);
		WPSClientSession wpsClient = WPSClientSession.getInstance();
		Object responseObject = wpsClient.execute(url, execute);

		if (responseObject instanceof ExecuteResponseDocument) {
			ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
			ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(execute, response, processDescription);

			System.out.println("response:");
			System.out.println(response);

			// DataType dataType =
			// response.getExecuteResponse().getProcessOutputs().getOutputArray(0).getData();
			// response.getExecuteResponse().getProcessOutputs();

			return response;
		}

		throw new Exception("Exception: " + responseObject.toString());
	}

}
