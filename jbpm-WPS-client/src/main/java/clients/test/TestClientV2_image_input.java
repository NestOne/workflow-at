package clients.test;

/**import it.geosolutions.geonetwork.GNClient;
 import it.geosolutions.geonetwork.exception.GNLibException;
 import it.geosolutions.geonetwork.exception.GNServerException;
 import it.geosolutions.geonetwork.op.GNMetadataGet;
 import it.geosolutions.geonetwork.util.GNInsertConfiguration;**/

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.DataType;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType.DataInputs;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.xmlbeans.XmlOptions;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geoviqua.gmd19157.DQDataQualityType;
import org.geoviqua.qualityInformationModel.x40.GVQDataQualityType;
import org.geoviqua.qualityInformationModel.x40.GVQDiscoveredIssueType;
import org.geoviqua.qualityInformationModel.x40.GVQMetadataDocument;
import org.geoviqua.qualityInformationModel.x40.GVQMetadataType;
import org.jdom.Element;
import org.n52.wps.client.ExecuteRequestBuilder;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.commons.WPSConfig;
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.n52.wps.io.data.binding.complex.GTRasterDataBinding;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataWithGTBinding;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;

public class TestClientV2_image_input {

	public void testExecute() {

		//String wpsURL = "http://geoprocessing.nottingham.ac.uk/wps/WebProcessingService?";
		String wpsURL = "http://localhost:8010/wps/WebProcessingService?";
		// String processID = "storage.geoserver.StoreWFSOutput";
		String processID = "org.n52.wps.server.r.test.return.surfacemodel";

		// WPSConfig.getInstance("/usr/local/apache-tomcat-7.0.55/webapps/wps/config/wps_config_geotools.xml");
		WPSConfig
		.getInstance("C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\wps\\config\\wps_config_geotools.xml"); // For

		try {
			ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
					wpsURL, processID);
			// System.out.println(describeProcessDocument);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			CapabilitiesDocument capabilitiesDocument = requestGetCapabilities(wpsURL);
			ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
					wpsURL, processID);
			// define inputs
			HashMap<String, Object> inputs = new HashMap<String, Object>();
			// complex data by reference

			DataInputs di = describeProcessDocument.getDataInputs();
			//File file = new File ("C:\\oxflood.jpg");
			File file = new File("C:\\testTiff.tiff");

			InputStream image = null;
			try {

				image = new BufferedInputStream(new FileInputStream(file));
				// image = new FileInputStream(file);

			} catch (Exception e) {
				System.out.println("File read in exception " + e);
			}
			GenericFileData gd = new GenericFileData(image, "image/tiff");
			//GeotiffBinding = new GeotiffBinding(image, "image/png");
			//FeatureCollection result = ((GeotiffBinding) wpsClient.getOutputs().get("OUT_TARGET")).getPayload();				
			
			inputs.put("inputRasterModel", new GenericFileDataBinding(gd));

			Object[] data = new Object[3];

			data = executeProcess(wpsURL, processID, describeProcessDocument,
					inputs);

			// System.out.println(data[1].toString());

			if (data[0] instanceof LiteralIntBinding) {
				int i = ((LiteralIntBinding) data[0]).getPayload();
				// System.out.println(data[0]);
			}

			if (data[0] instanceof GTVectorDataBinding) {
				FeatureCollection featureCollection = ((GTVectorDataBinding) data[0])
						.getPayload();

				SimpleFeatureIterator sf = (SimpleFeatureIterator) featureCollection
						.features();
				System.out.println("result " + featureCollection.size());

				for (int i = 0; i < 7; i++) {

					System.out.println("result " + sf.next().toString());

				}

			}
			if (data[0] instanceof GenericFileDataBinding) {
				System.out.println("HERE:");
				GenericFileData fd = ((GenericFileDataBinding) data[0])
						.getPayload();

				BufferedImage bi = ImageIO.read(fd.getBaseFile(true));

				File newfile = new File("C:\\oxfloodTest.jpg");

				ImageIO.write(bi, ".jpg", newfile);

			}

			if (data[1] instanceof GTVectorDataBinding) {
				FeatureCollection featureCollection = ((GTVectorDataBinding) data[1])
						.getPayload();

				SimpleFeatureIterator sf = (SimpleFeatureIterator) featureCollection
						.features();
				System.out.println("qual_result " + featureCollection.size());

				for (int i = 0; i < 3; i++) {

					System.out.println("qual_result " + sf.next().toString());

				}

			}

			else {
				System.out.println("NOT GTVECTORDATABINDING");
			}

		} catch (WPSClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CapabilitiesDocument requestGetCapabilities(String url)
			throws WPSClientException {

		WPSClientSession wpsClient = WPSClientSession.getInstance();

		wpsClient.connect(url);

		CapabilitiesDocument capabilities = wpsClient.getWPSCaps(url);

		// System.out.println(capabilities.toString());

		ProcessBriefType[] processList = capabilities.getCapabilities()
				.getProcessOfferings().getProcessArray();

		for (ProcessBriefType process : processList) {
			// System.out.println(process.getIdentifier().getStringValue());
		}
		return capabilities;
	}

	public ProcessDescriptionType requestDescribeProcess(String url,
			String processID) throws IOException {

		WPSClientSession wpsClient = WPSClientSession.getInstance();

		ProcessDescriptionType processDescription = wpsClient
				.getProcessDescription(url, processID);

		InputDescriptionType[] inputList = processDescription.getDataInputs()
				.getInputArray();

		for (InputDescriptionType input : inputList) {
			// System.out.println(input.getIdentifier().getStringValue());
		}
		return processDescription;
	}

	public Object[] executeProcess(String url, String processID,
			ProcessDescriptionType processDescription,
			HashMap<String, Object> inputs) throws Exception {
		ExecuteRequestBuilder executeBuilder = new ExecuteRequestBuilder(
				processDescription);

		for (InputDescriptionType input : processDescription.getDataInputs()
				.getInputArray()) {
			String inputName = input.getIdentifier().getStringValue();
			Object inputValue = inputs.get(inputName);
			if (input.getLiteralData() != null) {
				if (inputValue instanceof String) {
					System.out.println(inputValue);
					executeBuilder.addLiteralData(inputName,
							(String) inputValue);
				}

				if (inputValue instanceof Integer) {
					System.out.println(inputValue);
					executeBuilder.addLiteralData(inputName,
							(String) inputValue);

				}
			} else if (input.getComplexData() != null) {
				// Complexdata by value
				if (inputValue instanceof FeatureCollection) {

					IData data = new GTVectorDataBinding(
							(FeatureCollection) inputValue);
					executeBuilder
					.addComplexData(
							inputName,
							data,
							"http://schemas.opengis.net/gml/3.1.0/base/feature.xsd",
							null, "text/xml; subtype=gml/3.1.0");
				}

				if (inputName.equals("inputSurfaceModel")) {

					executeBuilder.addComplexDataReference(inputName,
							(String) inputValue, null, null, "text/plain");
				} else if (inputValue instanceof String) {
					System.out.println("inputValue " + inputValue);
					executeBuilder
					.addComplexDataReference(inputName,
							(String) inputValue, null, null,
							"application/json");
				}

				else if (inputName.equalsIgnoreCase("inputRasterModel")) {

					System.out.println("HERE GenericFileDataBinding "+ inputValue.toString());

					GeotiffBinding inputData = (GeotiffBinding) inputValue;
					System.out.println("HERE input "+ inputValue.toString());
					
					executeBuilder.addComplexData(inputName, inputData, null, null, "image/tiff");
				}

				if (inputValue == null && input.getMinOccurs().intValue() > 0) {
					throw new IOException("Property not set, but mandatory: "
							+ inputName);
				}
			}
		}

		ExecuteDocument execute = executeBuilder.getExecute();
		execute.getExecute().setService("WPS");

		System.out.println(execute);
		WPSClientSession wpsClient = WPSClientSession.getInstance();
		Object responseObject = wpsClient.execute(url, execute);

		if (responseObject instanceof ExecuteResponseDocument) {
			ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
			ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(
					execute, response, processDescription);

			DataType dataType = response.getExecuteResponse()
					.getProcessOutputs().getOutputArray(0).getData();

			Object[] dataReturn = new Object[3];
			Object data;
			data = analyser.getComplexData("HistogramImage",
					GenericFileDataBinding.class);
			if (data == null) {
				System.out.println("no results for result");
				throw new RuntimeException();

			}

			/**
			 * Object data2 = null; try{ data2 =
			 * analyser.getComplexData("qual_result",
			 * GTVectorDataBinding.class);
			 * 
			 * } catch (NullPointerException e){
			 * System.out.println("no qual results"); }
			 * 
			 * /** Object data3 = analyser.getComplexData("metadata",
			 * GenericFileDataBinding.class);
			 **/

			// Object data3 = dataType.getLiteralData().getStringValue();

			/**
			 * dataReturn[0] = (IData) data;
			 * 
			 * dataReturn[1] = (IData) data2;
			 **/
			// System.out.println(dataType.toString());**/

			dataReturn[0] = (GenericFileDataBinding) data;
			// dataReturn[1] = (GTVectorDataBinding) data2;

			return dataReturn;
		}
		throw new Exception("Exception: " + responseObject.toString());
	}

	public static void main(String[] args) {
		TestClientV2_image_input client = new TestClientV2_image_input();
		client.testExecute();
	}

}
