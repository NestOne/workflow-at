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


public class TestClientV2_julian {

        public void testExecute() {

        	String wpsURL = "http://geoprocessing.nottingham.ac.uk/wps/WebProcessingService?";
        	//String wpsURL = "http://localhost:8010/wps/WebProcessingService?";
            //String processID = "storage.geoserver.StoreWFSOutput";
        	String processID = "pillar.automaticvalidation.LaplacePhotoBlurCheckClient";
            
            //WPSConfig.getInstance("/usr/local/apache-tomcat-7.0.55/webapps/wps/config/wps_config_geotools.xml");            
    		WPSConfig
    		.getInstance("C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\wps\\config\\wps_config_geotools.xml"); // For


                try {
                        ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
                                        wpsURL, processID);
                        //System.out.println(describeProcessDocument);
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
                        //GenericFileData inputMetFile = new GenericFileData(new File("/Users/lgzsam/Documents/Projects/GRASP/Apsim_Examples/Sutton Bonnington Examples/Sutton_Met_data.met"), "text/plain") ;
                       // GenericFileData inputFile = new GenericFileData(new File("/Users/lgzsam/Documents/Projects/GRASP/Apsim_Examples/Sutton Bonnington Examples/masoud_4.apsim"), "text/xml");
                       // GenericFileData inputMetFile = new GenericFileData(new File("/Users/lgzsam/Documents/Projects/GRASP/Apsim_Examples/Examples/MetFiles/Dalby.met"), "text/plain") ;
                        //GenericFileData inputFile = new GenericFileData(new File("/Users/lgzsam/Documents/Projects/GRASP/Apsim_Examples/Examples/Canopy1.apsim"), "text/xml");
                        //String sampleDataURL = "http://localhost:8010/geoserver/cobweb/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=cobweb:SampleData&outputFormat=gml3";
                        
                      //  System.out.println("DI inputArray " + di.getInputArray(1).getIdentifier().getStringValue());
                      //  inputs.put("inputLocation", "52.9500, -1.1333");
                       // inputs.put("inputDistance", "2.0");
                       // inputs.put("searchTerm", "#nottingham");
                       // inputs.put("dateSince", "2014-09-30");
                        //File file = new File ("/Users/lgzsam/Downloads/1531117.jpg");
                        File file = new File ("C:\\oxflood.jpg");
                        
                        InputStream image = null;
                        try{
                        
                        	image = new BufferedInputStream(new FileInputStream(file));
                        	
                        	//image = new FileInputStream(file);
                        
                       
                        }
                        catch (Exception e){
                        	System.out.println("File read in exception " + e);
                        }
                        
                        
                  
                     
                        String threshold = ""+240;
                        
                        GenericFileData gd = new GenericFileData(image,"image/png");
                        
                        inputs.put(
                                "inputImage",
                                new GenericFileDataBinding(gd)
                                );
                       
                        inputs.put(
                        		"inputThreshold",
                        		threshold);
                    
                        
                        //inputs.put("IN_REFERENCE", "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/wcs?service=wcs&version=1.0.0&request=getcoverage&coverage=CobwebTest:upcampusresample&CRS=EPSG:4277&bbox=454000,338000,455000,339000&width=500&height=500&format=ArcGrid");
                        //inputs.put(
                          //      "inputAuthoritativeData",
                            //    "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:Biospheric_Reserves&outputFormat=application/json&srsName=EPSG:4236"
                              //  );
                        
                       // inputs.put("fieldName", "Your_plant_skills?");
                        //inputs.put("featureName", "ok");
                        //inputs.put("include", "true");
                        
                        //inputs.put("IN_THRESHOLD", "0.01");
                       
                      //  inputs.put("inputAuthoritativeData", 
                        //	"http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:SampleData&outputFormat=gml3"
                        	//);
                        
                        //inputs.put("inputBufferDistance", "0.001");
                       // inputs.put("inputBaringFieldName", "Orientatio");
                       // inputs.put("inputTiltFieldName", "Elevation");
                        //inputs.put("inputUserHeight", "1.5");
                        //inputs.put("featureName", "DownyBirch");
                       
                        //inputs.put("inputName", "Canopy.out");
                       // inputs.put("inputMetName", "Dalby1.met");
                         
                        
                        Object [] data = new Object[3];
                        
                        
                        data = executeProcess(wpsURL, processID,
                                        describeProcessDocument, inputs);
                    
                    	//System.out.println(data[1].toString());
                        
                        if(data[0] instanceof LiteralIntBinding){
                        	int i = ((LiteralIntBinding)data[0]).getPayload();
                        	//System.out.println(data[0]);
                        }
                         
                      if (data[0] instanceof GTVectorDataBinding) {
                                FeatureCollection featureCollection = ((GTVectorDataBinding)data[0])
                                                .getPayload();
                                
                                SimpleFeatureIterator sf = (SimpleFeatureIterator) featureCollection.features();
                                System.out.println("result " + featureCollection.size());
                                
                                for (int i = 0; i < 7; i++){
                                	
                                	System.out.println("result " + sf.next().toString());
                                	
                                }
                                
                              
                        }
                      if(data[0] instanceof GenericFileDataBinding){
                    	  System.out.println("HERE:");
                    	  GenericFileData fd = ((GenericFileDataBinding)data[0]).getPayload();
                    	  
                    	  BufferedImage bi = ImageIO.read(fd.getBaseFile(true));
                    	  
                    	  
                    	  File newfile = new File ("/Users/lgzsam/Downloads/test.jpg");
                    	  
                    	  ImageIO.write(bi, ".jpeg", newfile);
                    	  
                    	  
                      }
                      
                      if (data[1] instanceof GTVectorDataBinding) {
                                FeatureCollection featureCollection = ((GTVectorDataBinding)data[1])
                                                .getPayload();
                                
                                SimpleFeatureIterator sf = (SimpleFeatureIterator) featureCollection.features();
                                System.out.println("qual_result " +  featureCollection.size());
                                
                                for (int i = 0; i < 3; i++){
                                	
                                	System.out.println("qual_result " + sf.next().toString());
                                	
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
                
                //System.out.println(capabilities.toString());

                ProcessBriefType[] processList = capabilities.getCapabilities()
                                .getProcessOfferings().getProcessArray();

                for (ProcessBriefType process : processList) {
                        //System.out.println(process.getIdentifier().getStringValue());
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
                                
                                if(inputValue instanceof Integer){
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
                              
                                if(inputName.equals("inputSurfaceModel")){
                                 
                                	  executeBuilder
                                      .addComplexDataReference(
                                                      inputName,
                                                      (String) inputValue,
                                                      null,
                                                      null, "text/plain");
                                }
                                else if (inputValue instanceof String) {
                                	System.out.println("inputValue " + inputValue);
                                        executeBuilder
                                                        .addComplexDataReference(
                                                                        inputName,
                                                                        (String) inputValue,
                                                                        null,
                                                                        null, "application/json");
                                }
                                
                                else if (inputName.equalsIgnoreCase("inputImage")){
                                	
                                	System.out.println("HERE GenericFileDataBinding " + inputValue.toString());
                                	
                                	IData inputData = (IData) inputValue;
                                	
                                	executeBuilder
                                    .addComplexData(
                                                    inputName,
                                                    inputData,
                                                    null,
                                                    "base64", "image/jpeg");
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
                
                        
                     //  executeBuilder.setMimeTypeForOutput("image/jpeg","HistogramImage");
                      // executeBuilder.setMimeTypeForOutput("image/jpeg","LaplaceImage");
                      // executeBuilder.setMimeTypeForOutput("application/json", "qual_result");
                        //executeBuilder.setSchemaForOutput(
                          //              "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd",
                            //    "result");
                       // executeBuilder.setMimeTypeForOutput("application/WFS","result");
                       // executeBuilder.setSchemaForOutput(
                         //                     "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd",
                           //           "result");
                       // executeBuilder.setMimeTypeForOutput("text/xml; subtype=gml/3.1.0", "qual_result");
                        //executeBuilder.setSchemaForOutput(
                          //      "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd",
                        //"qual_result");
                
               // executeBuilder.setEncodingForOutput("base64", "output");
                
               // executeBuilder.setMimeTypeForOutput("text/plain", "metadata");
               // executeBuilder.setEncodingForOutput("UTF-8", "output");
             
              //  executeBuilder.setSchemaForOutput("http://schemas.geoviqua.org/GVQ/4.0/GeoViQua_DataQuality.xsd", 
                //		"metadata");
                
                //executeBuilder.setMimeTypeForOutput("text/xml; subtype=geoviqua", "metadata");
                
                
               
                ExecuteDocument execute = executeBuilder.getExecute();
                execute.getExecute().setService("WPS");
                
                System.out.println(execute);
                WPSClientSession wpsClient = WPSClientSession.getInstance();
                Object responseObject = wpsClient.execute(url, execute);
               
                if (responseObject instanceof ExecuteResponseDocument) {
                        ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
                        ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(
                                        execute, response, processDescription);
                      
                       DataType dataType = response.getExecuteResponse().getProcessOutputs().getOutputArray(0).getData();
                       
                        Object[] dataReturn = new Object[3];
                        Object data;
                        data =  analyser.getComplexData("HistogramImage",
                                GenericFileDataBinding.class);
                        if(data == null  ){
                        	System.out.println("no results for result");
                        	throw new RuntimeException();
                         
                        }
                        
                        /**Object data2 = null;
                       try{
                        	data2 = analyser.getComplexData("qual_result", 
                        		GTVectorDataBinding.class);
                    
                        }
                        catch (NullPointerException e){
                        	System.out.println("no qual results");
                        }
                      
                       /** Object data3 = analyser.getComplexData("metadata", 
                        		GenericFileDataBinding.class);**/
                        
                       // Object data3 = dataType.getLiteralData().getStringValue();
                        
                      /**  dataReturn[0] = (IData) data;
                        
                        dataReturn[1] = (IData) data2;**/
                        //System.out.println(dataType.toString());**/
                      
                        dataReturn[0] = (GenericFileDataBinding) data;
                      //  dataReturn[1] = (GTVectorDataBinding) data2;
                        
                       
                       
                        return dataReturn;
                }
                throw new Exception("Exception: " + responseObject.toString());
        }
        
 
        

        public static void main(String[] args) {
                TestClientV2_julian client = new TestClientV2_julian();
                client.testExecute();
        }
       

}
