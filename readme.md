

# Workflow Authoring Tool (wAT) 
This tool enables creation and execution of workflows defined using Business Process Modelling and Notation (BPMN). The repository extends a Business Process Modelling platform (JBoss jBPM) to enable construction of geoprocessing workflows comprised of Open Geospatial Consortium Web Processing Services. 

## Description

This repository contains the code for a tool to compose workflows comprised of OGC Web Processing Services. For creating and executing Quality Assurance workflows, these processes may be quality controls (QC), as might be found in the repository [QA_wps_processes](https://github.com/cobweb-eu/QA_wps_processes/), and a workflow constitutes the QA whole process.

The tool may also be used to execute standard WPS algorithms. [52North WPS](http://52north.org/communities/geoprocessing/wps/) Java, [52North WPS4R](https://wiki.52north.org/Geostatistics/WPS4R) and [GeoServer WPS](http://geoserver.org/) implementations have been tested.

The tool is based on a modified client of the JBoss jBPM framework (tested using v6.3 of jBPM). A user may author BPMN documents using an Eclipse plugin or within a web-based environment. The BPMN documents define a workflow which is executed by the jBPM engine. 

## Structure

 - Branch 'master' contains the code for executing Web Processing Services with data defined as OGC data end-points. 
 - Branch 'meta-get-set2' contains a proof-of-concept for defining workflows comprised of metadata records, located within an OGC catalogue service.

### Demo Quality Assurance workflow of Japanese knotweed reporting
[![Demo Workflow Japanese Knotweed](https://raw.githubusercontent.com/cobweb-eu/workflow-at/master/jbpm-WPS-client/jkw_screenshot.png)](https://drive.google.com/open?id=0Byw3xiwycSF3cksxSEF5Z0NnX2c)

## Credits

This development was initiated at the University of Nottingham (Nottingham Geospatial Institute) and has been supported by the project “Citizen Observatory WEB” (COBWEB) funded by the European Union under the FP7 ENV.2012.6.5-1 funding scheme, EU Grant Agreement Number: 308513. 


References:

Meek, S. Jackson, M. and Leibovici, DG. (2016) A BPMN solution for chaining OGC services to quality assure location-based crowdsourced data. Computers & Geosciences, 87: 76–83 [https://doi.org/10.1016/j.cageo.2015.12.003](https://doi.org/10.1016/j.cageo.2015.12.003)    

Rosser, J.F., Jackson, M.J. Leibovici, DG. (2018) Full Metadata Object Profiling for flexible geoprocessing workflows. Transactions in GIS. [https://doi.org/10.1111/tgis.12460](https://doi.org/10.1111/tgis.12460)    

Rosser, J.F., Pourabdolllah, A. Brackin, R. Jackson, M.J. Leibovici, DG. (2016) Full Meta Objects for Flexible Geoprocessing Workflows: profiling WPS or BPMN? 19th AGILE, 14-17 June 2016, Helsinki, Finland. (Best paper session).    

Higgins, CI. Williams, J. Leibovici, DG. Simonis,I. Davis, MJ. Muldoon, C. and O’Grady, M. (2016) Citizen OBservatory WEB (COBWEB): A Generic Infrastructure Platform to Facilitate the Collection of Citizen Science data for Environmental Monitoring. IJSDIR,  2016, Vol.11, 20-48, DOI: 10.2902/1725-0463.2016.11.art3
 
Leibovici, DG Williams, J Rosser, JF Hodges, C Scott, D Chapman, C Higgins,  C and Jackson, MJ  ( 2016) Earth Observation for Citizen Science validation, or, Citizen Science for Earth Observation validation? Role of the Quality Assurance of Volunteered Observations. Remote Sensing, special issue ‘ Citizen Science and Earth Observation’.

# Installation
The quickest way to get a local installation running for development is to use the full jBPM installer. This will download and set up JBoss and JBPM with all the necessary dependencies using an Ant script.

## Pre-requisites for installation

* [Java 7 or higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Apache Maven](http://maven.apache.org/) 
* [Apache Ant] (http://ant.apache.org/)
* [git](http://git-scm.com/)

### Installation for local development (using Eclipse)

* Download the full jBPM installer: ```http://sourceforge.net/projects/jbpm/files/jBPM%206/jbpm-6.3.0.Final/jbpm-6.3.0.Final-installer-full.zip```
* Unzip to a convenient location: ```C:/jbpm-installer```or ```/home/jbpm-installer```
* CD to ```jbpm-installer``` and run the install demo (takes a while): ```ant install.demo```
* Start jBPM/JBoss: ```ant start.demo```
* Wait a few minutes for Eclipse to boot
* Can now pull and import the ```jbpm-WPS-client``` code of this repository into Eclipse.
* Try running the test WPS processes in src/main/java/cobweb/test You will need to edit them to point to your own WPS installation.
* Modification of src/main/java/cobweb/m24/GenericWPSClient.java may also be required to fit the operating system and local file paths. See log file and WPS.config variables.

## Troubleshooting

* When passing outputs by reference the process result specified in the .WID definition should be set to StringDataType, temporary variable as String and the input it is mapped to as ObjectDataType.

* When compiling processes, a "only whitespace content allowed" compilation error probably indicates problems with the mapping of input/output variable data types in the workflow. E.g. the temporary variable data type must be a String when passing by reference OR if passing FeatureCollections it must be ObjectDataType.

* Errors are not currently returned when attempting to execute a work item which has not been registered. Remember to register all workItems (with names as defined in the .Wid) if a workflow appears to midway stop during execution.

* The web editor (Kie Workbench) environment has various browser related issues. When using Google Chrome the editor will get stuck on "Please Wait: Loading" if there is Anti-Virus software installed. Disabling all AV services (Sophos or AVG) appears to fix the problem. Internet Explorer (ver. 11 tested) works (with AV software still running), but the IE page rendering is terrible and workflow the drawing is buggy.

