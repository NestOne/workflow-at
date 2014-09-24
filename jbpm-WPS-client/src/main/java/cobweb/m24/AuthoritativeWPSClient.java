package cobweb.m24;

import java.io.IOException;
import java.util.HashMap;

import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import com.sample.ExecuteRequestBuilder;
import com.sample.ExecuteResponseAnalyser;

public class AuthoritativeWPSClient {

    public void test() {

    	String wpsURL = "http://localhost:8010/wps/WebProcessingService";

        String processID = "pillar.authoritativedata.AuthoritativeDataComparison";


            try {
                    ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
                                    wpsURL, processID);
                    System.out.println(describeProcessDocument);
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
                    inputs.put(
                            "inputObservations",
                            "http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_cities"
                            );
                    inputs.put("inputAuthoritativeData", 
                    		"http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_state_boundaries"
                    		);
                    //inputs.put("bufferSize", "0.0001");
                    
              
                    IData data = executeProcess(wpsURL, processID,
                                    describeProcessDocument, inputs);
                    
                   FeatureCollection fc = (FeatureCollection)data.getPayload();
                    		
          
                    System.out.println(fc.size());

                    if (data instanceof GTVectorDataBinding) {
                         
                            SimpleFeatureIterator sf = (SimpleFeatureIterator) fc.features();
                            System.out.println(fc.size());
                          
                            while (sf.hasNext()){
                            	
                            	System.out.println(sf.next().toString());
                        	
                            }
                           
                    }
                    
                    else{
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

            ProcessBriefType[] processList = capabilities.getCapabilities()
                            .getProcessOfferings().getProcessArray();

            for (ProcessBriefType process : processList) {
                    System.out.println(process.getIdentifier().getStringValue());
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
                    System.out.println(input.getIdentifier().getStringValue());
            }
            return processDescription;
    }

    public IData executeProcess(String url, String processID,
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
                                                                    "http://schemas.opengis.net/gml/2.1.1/feature.xsd",
                                                                    null, "text/xml");
                            }
                            // Complexdata Reference
                            if (inputValue instanceof String) {
                                    executeBuilder
                                                    .addComplexDataReference(
                                                                    inputName,
                                                                    (String) inputValue,
                                                                    "http://schemas.opengis.net/gml/2.1.1/feature.xsd",
                                                                    null, "text/xml; subtype=gml/2.1.1");
                            }

                            if (inputValue == null && input.getMinOccurs().intValue() > 0) {
                                    throw new IOException("Property not set, but mandatory: "
                                                    + inputName);
                            }
                    }
            }
            executeBuilder.setMimeTypeForOutput("text/xml; subtype=gml/3.1.1", "qual_result");
            executeBuilder.setSchemaForOutput(
                            "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd",
                            "qual_result");
            
           // executeBuilder.setMimeTypeForOutput("text/xml", "number");
           
            ExecuteDocument execute = executeBuilder.getExecute();
            execute.getExecute().setService("WPS");
            WPSClientSession wpsClient = WPSClientSession.getInstance();
            Object responseObject = wpsClient.execute(url, execute);
            
            if (responseObject instanceof ExecuteResponseDocument) {
                    ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
                    ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(
                                    execute, response, processDescription);
                    //IData data = (IData) analyser.getComplexDataByIndex(0,
                      //              GTVectorDataBinding.class);
                  IData data = (IData) analyser.getComplexData("qual_result", GTVectorDataBinding.class);
                    //IData data = (IData) analyser.getComplexDataByIndex(0, LiteralIntBinding.class);
                    return data;
            }
            throw new Exception("Exception: " + responseObject.toString());
    }
    public static void main(String []args){
    	AuthoritativeWPSClient test = new AuthoritativeWPSClient();
    	test.test();
    	}
}
