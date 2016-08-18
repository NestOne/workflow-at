package clients.test;



/**import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.op.GNMetadataGet;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;**/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
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

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.n52.wps.client.ExecuteRequestBuilder;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.commons.WPSConfig;
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import cobweb.m24.ExecuteResponseAnalyser;


public class TestClient {

        public void testExecute() {

        	String wpsURL = "http://geoprocessing.nottingham.ac.uk/wps/WebProcessingService?";
      
            String processID = "pillar.lbs.LineOfSightCoordinates";
            
            WPSConfig.getInstance("/usr/local/apache-tomcat-7.0.55/webapps/wps/config/wps_config_geotools.xml");
            
           

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
                        
                        inputs.put(
                                "inputObservations",
                               
                                "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:_10Markers&outputFormat=application/json&srsName=EPSG:27700");
                        
                     //   inputs.put("inputPolygons", 
                       // 		"http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:OSGB_Grid_50km&outputFormat=application/json&srsName=EPSG:27700");
                        
                       // inputs.put(
                        //		"fileName",
                        	//	"JKW");
                       // inputs.put(
                        //		"giveNumber", 1);
                        
                      //  inputs.put("inputAuthoritativeData",
                        //		"http://geoprocessing.nottingham.ac.uk/authoritative_lists/butterfly-plant.csv"
                        	//	);
                        
                       // inputs.put("obsFieldName2", "What_plant");
                        //i/nputs.put("obsFieldName1", "Which_butt");
                   //    inputs.put("urlPrefix", "http://geoprocessing.nottingham.ac.uk/");
                     //  inputs.put("threshold", "240");
                       //inputs.put("urlFieldName", "photo_loc");
                        
                        
                  
                        
                        //inputs.put("IN_REFERENCE", "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/wcs?service=wcs&version=1.0.0&request=getcoverage&coverage=CobwebTest:upcampusresample&CRS=EPSG:4277&bbox=454000,338000,455000,339000&width=500&height=500&format=ArcGrid");
                        //inputs.put(
                          //      "inputAuthoritativeData",
                            //    "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:Biospheric_Reserves&outputFormat=application/json&srsName=EPSG:4236"
                              //  );
                        
                       // inputs.put("fieldName", "Your_plant_skills?");
                        //inputs.put("featureName", "ok");
                        //inputs.put("include", "true");
                        
                        //inputs.put("IN_THRESHOLD", "0.01");
                        
                       // inputs.put("inputObservations", "http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:SampleData&outputFormat=application/json");
                       
                      // inputs.put("inputAuthoritativeData", 
                       	//"http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=CobwebTest:Biospheric_Reserves&maxFeatures=50&outputFormat=application/json&srsName=EPSG:4236"
                        	//);
                        
                        //inputs.put("inputBufferDistance", "0.001");
                        inputs.put("inputSurfaceModel", 
                        		"http://grasp.nottingham.ac.uk:8010/geoserver/CobwebTest/wcs?service=wcs&version=1.0.0&request=getcoverage&coverage=CobwebTest:Taly_DSM&CRS=EPSG:27700&bbox=265000,289000,266000,290000&width=1000&height=1000&format=ArcGrid");
                        inputs.put("inputBaringFieldName", "Baring");
                        inputs.put("inputTiltFieldName", "Roll");
                        inputs.put("inputUserHeight", "1.5");
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
                                
                           /**     String pillar = "storage.geoserver.StoreWFSNamedOutput";
                                ProcessDescriptionType describeProcessDocument2 = null;
                                
                                try {
                                   describeProcessDocument2 = requestDescribeProcess(
                                                    wpsURL, pillar);
                                    //System.out.println(describeProcessDocument);
                            } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                            }
                                
                                HashMap<String, Object> inputs2 = new HashMap<String, Object>();
                                
                                inputs2.put("inputObservations", featureCollection);
                                inputs2.put("fileName", "output");
                                
                                Object [] data2 = new Object[3];
                                
                                
                                data2 = executeProcess(wpsURL, pillar,
                                                describeProcessDocument2, inputs2);**/
                                
                                
                                
                                
                                for(int i = 0; i < 7; i++){
                                	
                                	System.out.println("result " + sf.next().toString());
                                	
                                }
                                
                              
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
                        
                       if (data[0] instanceof GenericFileDataBinding){
                    	  
                    	   GenericFileData xmlGenericData = ((GenericFileDataBinding)data[0]).getPayload();
                           
                    	   File file =  xmlGenericData.getBaseFile(true);
                    	   
                    	   InputStream fis = new FileInputStream(file);
                    	   
                           BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                           
                        
                           
                           File newFile = File.createTempFile("temp2", ".out");
                          
                           FileWriter fw = new FileWriter(newFile);
                           
                           for (String line = br.readLine(); line != null; line = br.readLine()) {                               
                               String newLine = line.replaceAll("&gt;",">").replaceAll("&lt;", "<").replaceAll("&amp;","&");                               
                               //System.out.println(newLine);          
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
                           
                       //    insertMetdataValues(newFile, "http://localhost:8010/geonetwork");

                    	   
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
                   //     System.out.println(process.getIdentifier().getStringValue());
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
                                	
                                        executeBuilder.addLiteralData(inputName,
                                                        (String) inputValue);
                                }
                                else if (inputName.equalsIgnoreCase("giveNumber")) {
                                	System.out.println("inputValue " + inputValue);
                                        executeBuilder
                                                        .addLiteralData(
                                                                        inputName,
                                                                        (String)inputValue);
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
                                                                        null,
                                                                        null, "application/json");
                                }
                         
                              
                               else if(inputName.equals("inputSurfaceModel")){
                                 
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
                                
                               
                                
                               
                               

                                if (inputValue == null && input.getMinOccurs().intValue() > 0) {
                                        throw new IOException("Property not set, but mandatory: "
                                                        + inputName);
                                }
                        }
                }
                //executeBuilder.setMimeTypeForOutput("application/json","result");
              //  executeBuilder.setMimeTypeForOutput("application/json","qual_result");
                //executeBuilder.setSchemaForOutput(
                  //                     "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd",
                    //           "result");
                 executeBuilder.setMimeTypeForOutput("application/json", "result");
                 //executeBuilder.setSchemaForOutput(
                   //      "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd",
                 //"qual_result");
               
                ExecuteDocument execute = executeBuilder.getExecute();
              System.out.println(executeBuilder.getExecute());
                execute.getExecute().setService("WPS");
               
                WPSClientSession wpsClient = WPSClientSession.getInstance();
                
                Object responseObject = wpsClient.execute(url, execute);
                
          //      System.out.println(executeBuilder.getExecute());
                if (responseObject instanceof ExecuteResponseDocument) {
                        ExecuteResponseDocument response = (ExecuteResponseDocument) responseObject;
                        //System.out.println(response.getExecuteResponse().toString());
                        ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(
                                        execute, response, processDescription);
                      
                       DataType dataType = response.getExecuteResponse().getProcessOutputs().getOutputArray(0).getData();
                       
                        Object[] dataReturn = new Object[3];
                        Object data;
                       // data = dataType.getLiteralData().getStringValue();
                        
                        //System.out.println(data.toString());
                        data =  analyser.getComplexData("result",
                                GTVectorDataBinding.class);
                        if(data == null  ){
                        	System.out.println("no results for result");
                        	throw new RuntimeException();
                         
                        }
                        
                        Object data2 = null;
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
                        //System.out.println(dataType.toString());
                      
                        dataReturn[0] = (GTVectorDataBinding) data;
                        dataReturn[1] = (GTVectorDataBinding) data2;
                        
                       
                       
                        return dataReturn;
                }
                throw new Exception("Exception: " + responseObject.toString());
        }
        
  /**     public void insertMetdataValues(File xmlDocument, String url){
        	
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
    	   
       }**/
        
        

        public static void main(String[] args) {
                TestClient client = new TestClient();
                client.testExecute();
        }
        
     
       

}
