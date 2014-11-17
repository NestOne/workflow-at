package cobweb.m24;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.DataType;
import net.opengis.wps.x100.ExecuteDocument;
import net.opengis.wps.x100.ExecuteResponseDocument;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.ProcessBriefType;
import net.opengis.wps.x100.ProcessDescriptionType;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;


import cobweb.m24.ExecuteResponseAnalyser;
import cobweb.m24.ExecuteRequestBuilder;


public class GenericWPSConflationClient {
	
	String wpsURL;
	String wpsProcessID;
	HashMap<String, Object> wpsInputs;
	HashMap<String, Object> outputs;
	String catalogURL;
	FeatureCollection featureCollection;
	FeatureCollection inputFeatureCollection;
	
	
public GenericWPSConflationClient(String wpsURL, String wpsProcessID, HashMap<String,Object> wpsInputs, String catalogURL){
	
	this.wpsURL = wpsURL;
	this.wpsProcessID = wpsProcessID;
	this.wpsInputs = wpsInputs;
	this.catalogURL = catalogURL;
	
	System.out.println("WPS URL " + wpsURL);
	System.out.println("WPS Process ID " + wpsProcessID);
	
        try {
                ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
                                wpsURL, wpsProcessID);
               // System.out.println(describeProcessDocument);
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        try {
                CapabilitiesDocument capabilitiesDocument = requestGetCapabilities(wpsURL);
                ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
                                wpsURL, wpsProcessID);
     
               // outputs = executeProcess(wpsURL, wpsProcessID,
                 //               describeProcessDocument, wpsInputs, inputFeatureCollection);
                
                outputs = executeProcess(wpsURL, wpsProcessID,
                        describeProcessDocument, wpsInputs);
                
                //Object metadata = outputs.get("metadata");
                Object result = outputs.get("OUT_TARGET");
                System.out.println("Result " + result.toString());
              //  Object qual_result = outputs.get("qual_result");
                
              /**  if (metadata instanceof GenericFileDataBinding){
                	
                	System.out.println("GENERIC METADATA FILE");
             	  
             	   GenericFileData xmlGenericData = ((GenericFileDataBinding)metadata).getPayload();
             	   
             	   
             	  System.out.println("GENERIC DATA " + xmlGenericData.getBaseFile(true).toString());
             	   
             	   File metaFile = parseXMLFromWPS(xmlGenericData);
             	  // insertGVQMetadata(metaFile, catalogURL);
              
         	    
                }**/
                if(result instanceof GTVectorDataBinding){
                	
                		System.out.println("inputData = GTVECTORDATABINDING");
                		
                		FeatureCollection out = ((GTVectorDataBinding) result).getPayload();
                		System.out.println("result number " + out.size());
                		SimpleFeatureIterator fit = (SimpleFeatureIterator) out.features();
                		System.out.println("result F " + fit.next().toString());
                		fit.close();
                }
                
           /**     if(qual_result instanceof GTVectorDataBinding){
                	FeatureCollection qual_out = ((GTVectorDataBinding) qual_result).getPayload();
                	System.out.println("qual result number " + qual_out.size());
                	SimpleFeatureIterator fit = (SimpleFeatureIterator) qual_out.features();
            		System.out.println("result F " + fit.next().toString());
            		fit.close();
                }**/
               
                
                
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
        
        //System.out.println(capabilities.toString());

        ProcessBriefType[] processList = capabilities.getCapabilities()
                        .getProcessOfferings().getProcessArray();

        for (ProcessBriefType process : processList) {
              //  System.out.println(process.getIdentifier().getStringValue());
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

public HashMap<String, Object> executeProcess(String url, String processID,
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
            	System.out.println("Feature Collection " + inputName + " " + inputValue);
                    IData data = new GTVectorDataBinding(
                                    (FeatureCollection) inputValue);
                    executeBuilder
                                    .addComplexData(
                                                    inputName,
                                                    data,
                                                    "http://schemas.opengis.net/gml/3.0.0/base/feature.xsd",
                                                    null, "text/xml; subtype=gml/3.0.0");
            }
          
            // Complexdata Reference
            if (inputValue instanceof String) {
            	System.out.println("Feature Collection reference " + inputName + " " + inputValue);
                    executeBuilder
                                    .addComplexDataReference(
                                                    inputName,
                                                    (String) inputValue,
                                                    "http://schemas.opengis.net/gml/3.0.0/base/feature.xsd",
                                                    null, "text/xml; subtype=gml/3.0.0");
            }

            if (inputValue == null && input.getMinOccurs().intValue() > 0) {
                    throw new IOException("Property not set, but mandatory: "
                                    + inputName);
            }
    }
}
//  executeBuilder.setMimeTypeForOutput("text/xml; subtype=gml/3.1.0", "result");
//executeBuilder.setSchemaForOutput(
//              "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd",
//            "result");

    
    executeBuilder.setMimeTypeForOutput("text/xml; subtype=gml/3.0.0","OUT_TARGET");
    executeBuilder.setSchemaForOutput(
                    "http://schemas.opengis.net/gml/3.0.0/base/feature.xsd",
            "OUT_TARGET");


// executeBuilder.setEncodingForOutput("base64", "output");

// executeBuilder.setMimeTypeForOutput("text/plain", "metadata");
// executeBuilder.setEncodingForOutput("UTF-8", "output");

//  executeBuilder.setSchemaForOutput("http://schemas.geoviqua.org/GVQ/4.0/GeoViQua_DataQuality.xsd", 
//		"metadata");

//executeBuilder.setMimeTypeForOutput("text/xml; subtype=geoviqua", "metadata");



ExecuteDocument execute = executeBuilder.getExecute();
execute.getExecute().setService("WPS");
WPSClientSession wpsClient = WPSClientSession.getInstance();
Object responseObject = wpsClient.execute(url, execute);

if (responseObject instanceof ExecuteResponseDocument) {
    ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
    ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(
                    execute, response, processDescription);
  
   DataType dataType = response.getExecuteResponse().getProcessOutputs().getOutputArray(0).getData();
   
   System.out.println("data output " + dataType.toString());
   
   HashMap<String, Object> dataReturn = new HashMap<String,Object>();
    
   Object data =  analyser.getComplexData("OUT_TARGET",
            GTVectorDataBinding.class);
  
    
    /**Object data2 = analyser.getComplexData("qual_result", 
    		GTVectorDataBinding.class);
    
    Object data3 = analyser.getComplexData("metadata", 
    		GenericFileDataBinding.class);**/
    
   // Object data3 = dataType.getLiteralData().getStringValue();
    
  /**  dataReturn[0] = (IData) data;
    
    dataReturn[1] = (IData) data2;**/
    System.out.println(dataType.toString());
    
    dataReturn.put("OUT_TARGET", data);
  
    return dataReturn;
}
throw new Exception("Exception: " + responseObject.toString());
}
	


public HashMap<String, Object> getOutputs(){
	
		return outputs;
	
	}



}

