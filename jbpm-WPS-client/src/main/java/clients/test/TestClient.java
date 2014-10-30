package clients.test;



import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.op.GNMetadataGet;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import cobweb.m24.ExecuteRequestBuilder;
import cobweb.m24.ExecuteResponseAnalyser;


public class TestClient {

        public void testExecute() {

        	String wpsURL = "http://cobweb.gis.geo.tu-dresden.de:8080/wps_conflation/WebProcessingService?";

            String processID = "de.tudresden.gis.fusion.algorithm.GeometryDistance";


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
                        
                        DataInputs di = describeProcessDocument.getDataInputs();
                        //GenericFileData inputMetFile = new GenericFileData(new File("/Users/lgzsam/Documents/Projects/GRASP/Apsim_Examples/Sutton Bonnington Examples/Sutton_Met_data.met"), "text/plain") ;
                       // GenericFileData inputFile = new GenericFileData(new File("/Users/lgzsam/Documents/Projects/GRASP/Apsim_Examples/Sutton Bonnington Examples/masoud_4.apsim"), "text/xml");
                        GenericFileData inputMetFile = new GenericFileData(new File("/Users/lgzsam/Documents/Projects/GRASP/Apsim_Examples/Examples/MetFiles/Dalby.met"), "text/plain") ;
                        GenericFileData inputFile = new GenericFileData(new File("/Users/lgzsam/Documents/Projects/GRASP/Apsim_Examples/Examples/Canopy1.apsim"), "text/xml");
                        String sampleDataURL = "http://localhost:8010/geoserver/cobweb/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=cobweb:SampleData&outputFormat=gml3";
                        
                      //  System.out.println("DI inputArray " + di.getInputArray(1).getIdentifier().getStringValue());
                      //  inputs.put("inputLocation", "52.9500, -1.1333");
                       // inputs.put("inputDistance", "2.0");
                       // inputs.put("searchTerm", "#nottingham");
                       // inputs.put("dateSince", "2014-09-30");
                        
                        inputs.put(
                                "IN_TARGET",
                                "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:SampleData&outputFormat=gml3&srsName=EPSG:4236&maxFeatures=10"
                                );
                        inputs.put("IN_REFERENCE", "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:Dyfi_Bio_Selection&outputFormat=gml3");
                        inputs.put("IN_THRESHOLD", "0.005");
                        
                        
                       // inputs.put("inputAuthoritativeData", 
                        //		"http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_state_boundaries&outputFormat=GML2"
                        	//	);
                       
                        //inputs.put("inputName", "Canopy.out");
                       // inputs.put("inputMetName", "Dalby1.met");
                         
                        
                        Object [] data = new Object[3];
                        
                        
                        data = executeProcess(wpsURL, processID,
                                        describeProcessDocument, inputs);
                    
                    	//System.out.println(data[1].toString());
                        
                        if(data[0] instanceof LiteralIntBinding){
                        	int i = ((LiteralIntBinding)data[0]).getPayload();
                        	System.out.println(data[0]);
                        }
                         
                      if (data[0] instanceof GTVectorDataBinding) {
                                FeatureCollection featureCollection = ((GTVectorDataBinding)data[0])
                                                .getPayload();
                                
                                SimpleFeatureIterator sf = (SimpleFeatureIterator) featureCollection.features();
                                System.out.println(featureCollection.size());
                                
                                while (sf.hasNext()){
                                	
                                	System.out.println(sf.next().toString());
                                	
                                }
                                
                              
                        }
          
                        else{
                        	System.out.println("NOT GTVECTORDATABINDING");
                        }
                        
                       if (data[0] instanceof GenericFileDataBinding){
                    	  
                    	   GenericFileData xmlGenericData = ((GenericFileDataBinding)data[0]).getPayload();
                           
                    	   File file =  xmlGenericData.getBaseFile(true);
                    	   
                    	   InputStream fis = new FileInputStream(file);
                    	   
                           BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                           
                        
                           
                           File newFile = File.createTempFile("temp2", ".out");
                          
                           FileWriter fw = new FileWriter(newFile);
                           
                           for (String line = br.readLine(); line != null; line = br.readLine()) {                               
                               String newLine = line.replaceAll("&gt;",">").replaceAll("&lt;", "<").replaceAll("&amp;","&");                               
                               System.out.println(newLine);          
                               fw.write(newLine);

                            }
                           
                           fw.close();
                    	   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
               			   DocumentBuilder builder =
               			            factory.newDocumentBuilder();
               			   Document tempXML = builder.newDocument();
               			  
               			            tempXML = builder.parse(newFile);
                    	   
                    	   
                    	   NodeList list = tempXML.getElementsByTagName("xs:element");
                           
                           System.out.println("LIST LENGTH " + list.getLength());
                           
                           insertMetdataValues(newFile, "http://localhost:8010/geonetwork");

                    	   
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
                
                System.out.println(capabilities.toString());

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
                              
                                // Complexdata Reference
                                if (inputValue instanceof String) {
                           
                                        executeBuilder
                                                        .addComplexDataReference(
                                                                        inputName,
                                                                        (String) inputValue,
                                                                        "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd",
                                                                        null, "text/xml; subtype=gml/3.1.0");
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
                
                        
                        executeBuilder.setMimeTypeForOutput("text/xml; subtype=gml/3.1.0","OUT_TARGET");
                        executeBuilder.setSchemaForOutput(
                                        "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd",
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
                       
                        Object[] dataReturn = new Object[3];
                        
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
                      
                        dataReturn[0] = (GTVectorDataBinding) data;
                        
                       
                       
                        return dataReturn;
                }
                throw new Exception("Exception: " + responseObject.toString());
        }
        
       public void insertMetdataValues(File xmlDocument, String url){
        	
        	ArrayList< ? > validationErrors = new ArrayList<Object>();
        	XmlOptions options; 
        	options = new XmlOptions();
        	options.setSavePrettyPrint();
        	options.setSaveAggressiveNamespaces();
        
        	HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
        	suggestedPrefixes.put("http://www.geoviqua.org/QualityInformationModel/4.0", "gvq");
        	options.setSaveSuggestedPrefixes(suggestedPrefixes);
        
        	options.setErrorListener(validationErrors);
        
        	
        	GVQMetadataDocument doc = GVQMetadataDocument.Factory.newInstance();
        	GVQMetadataType gvqMetadata = doc.addNewGVQMetadata();
        	gvqMetadata.addNewLanguage().setCharacterString("en");
            gvqMetadata.addNewMetadataStandardName().setCharacterString("GVQ");
            gvqMetadata.addNewMetadataStandardVersion().setCharacterString("1.0.0");
            gvqMetadata.addNewDateStamp().setDate(Calendar.getInstance());
            DQDataQualityType quality = gvqMetadata.addNewDataQualityInfo2().addNewDQDataQuality();
            GVQDataQualityType gvqQuality = (GVQDataQualityType) quality.substitute(new QName("http://www.geoviqua.org/QualityInformationModel/4.0",
                                                                                                  "GVQ_DataQuality"),
                                                                                        GVQDataQualityType.type);
                GVQDiscoveredIssueType issue = gvqQuality.addNewDiscoveredIssue().addNewGVQDiscoveredIssue();
                issue.addNewKnownProblem().setCharacterString("problem");
                issue.addNewWorkAround().setCharacterString("solution");
        
                // validate schema conformity
                boolean isValid = doc.validate();
                if ( !isValid)
                    System.out.println(Arrays.toString(validationErrors.toArray()));
        
                // print out as XML
                System.out.println(doc.xmlText(options));
                
               
                
        
                // store in catalog
               // new CatalogClient("http://catalog.url/csw").store(doc);
        
        
			
        	try {
        		 File file = File.createTempFile("test", ".xml");
        		 
        		 doc.save(file);
        		          
        		String un = "admin";
        		String pw = "admin";
        		
        	//	File sampleDocument = new File("/Users/lgzsam/Downloads/c9ef44fc-5974-41e2-a425-f9634963ba81/metadata/metadata.xml");
        		 GNClient client = new GNClient(url,un,pw);
        		 
        		 
        		 //PostMethod loginMethod = new PostMethod(url + "/srv/eng/login.form");
        		 
             		//client.login(un, pw);
        		 	GNInsertConfiguration cfg = new GNInsertConfiguration();
	        	    cfg.setCategory("datasets");
	        	    
	        	    cfg.setGroup("1"); // group 1 is usually "all"
	        	    cfg.setStyleSheet("_none_");
	        	    cfg.setValidate(Boolean.FALSE);
	        	     
	        	    long id = client.insertMetadata(cfg, file);

	        	    System.out.println("Metadata created with id " + id);
        		 
        	
	
			} catch (GNLibException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GNServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

        }
       
       public Document getMetadataRecord(String UUID, String url){
    	   Document metadataRecord = null;
    	   String un = "admin";
   			String pw = "admin";
    	   GNClient client = new GNClient(url, un,pw);
    	   
    	   //GNMetadataGet get = new GNMetadataGet();
    	   
   	     
   	    	try {
				Element id = client.get(UUID);
				
				System.out.println(id.toString());
			} catch (GNLibException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GNServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	   
    	   return metadataRecord;
    	   
       }
        
        

        public static void main(String[] args) {
                TestClient client = new TestClient();
                client.testExecute();
        }
       

}
