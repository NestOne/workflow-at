# Workflow Authoring Tool (wAT) 
 This tool has been developed for the particular use case of quality assurance within the COBWEB project and represents the workflow composition and execution part of the Quality Assurance workflow Authoring Tool (QAwAT).

wAT uses a workflow authoring tool for constructing geoprocessing workflows comprised of Open Geospatial Consortium Web Processing Services. For QAwAT the processes are quality controls (QC) and a workflows constitutes the QA whole process.

## Description

This repository contains the code for a tool to create compose workflows comprised of Web Processing Services, as might be found in the [QA_wps_processes](https://github.com/cobweb-eu/QA_wps_processes/) repository. The tool may also be used to execute standard WPS algorithms. [52NorthWPS](http://52north.org/communities/geoprocessing/wps/) Java, 52NorthWPS4R and [GeoServer WPS](http://geoserver.org/) implementations have been tested.

The tool is based on a modified client of the JBOSS jBPM framework. The tool can be used to author BPMN documents using an Eclipse plugin or within a web-based envrionment. The BPMN documents define a workflow which is executed by the jBPM engine. 

[![Demo Workflow Japanese Knotweed](http://www.nottingham.ac.uk/~psxjr/images/jkw_screenshot.png)](https://drive.google.com/open?id=0Byw3xiwycSF3cksxSEF5Z0NnX2c)


# Installation
The quickest way to get a local installation running for development is to use the full jBPM installer. This will download and set up JBOSS and JBPM with all the necessary dependencies using an Ant script.

## Pre-requisites for installation

* [Java 7 or higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Apache Maven](http://maven.apache.org/) 
* [Apache Ant] (http://ant.apache.org/)
* [git](http://git-scm.com/)

### Installation for local development (using Eclipse)

* Download the full jBPM installer: ```http://sourceforge.net/projects/jbpm/files/jBPM%206/jbpm-6.3.0.Final/jbpm-6.3.0.Final-installer-full.zip```
* Unzip to a convenient location: ```C:/jbpm-installer```or ```/home/jbpm-installer```
* CD to ```jbpm-installer``` and run the install demo (takes a while): ```ant install.demo```
* Start JBPM/JBOSS: ```ant start.demo```
* Wait a few minutes for Eclipse to boot
* Can now pull and import the ```jbpm-WPS-client``` code of this repository into Eclipse.

## Troubleshooting

* When passing outputs by reference the process result specified in the .WID definition should be set to StringDataType, temporary variable as String and the input it is mapped to as ObjectDataType.

* When compiling processes, a "only whitespace content allowed" compilation error probably indicates problems with the mapping of input/output variable data types in the workflow. E.g. the temporary variable data type must be a String when passing by reference OR if passing FeatureCollections it must be ObjectDataType.

* Errors are not currently returned when attempting to execute a work item which has not been registered. Remember to register all workItems (with names as defined in the .Wid) if a workflow appears to midway stop during execution.

* The web editor (Kie Workbench) environment has various browser related issues. When using Google Chrome the editor will get stuck on "Please Wait: Loading" if there is Anti-Virus software installed. Disabling all AV services (Sophos or AVG) appears to fix the problem. Internet Explorer (ver. 11 tested) works (with AV software still running), but the IE page rendering is terrible and workflow the drawing is buggy.


