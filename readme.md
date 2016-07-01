# Quality Assurance Authoring Tool #

This is the authoring tool for constructing geoprocessing workflows comprised of Web Processing Services. The tool is based on a jBPM framework.

[![Demo Workflow Japanese Knotweed](http://www.nottingham.ac.uk/~psxjr/images/jkw_screenshot.png)](https://drive.google.com/open?id=0Byw3xiwycSF3cksxSEF5Z0NnX2c)


## Troubleshooting

* When passing outputs by reference the process result specified in the .WID definition should be set to StringDataType, temporary variable as String and the input it is mapped to as ObjectDataType.

* When compiling processes, a "only whitespace content allowed" compilation error probably indicates problems with the mapping of input/output variable data types in the workflow. E.g. the temporary variable data type must be a String when passing by reference OR if passing FeatureCollections it must be ObjectDataType.

* Errors are not currently returned when attempting to execute a work item which has not been registered. Remember to register all workItems (with names as defined in the .Wid) if a workflow appears to midway stop during execution.

* The web editor (Kie Workbench) environment has various browser related issues. When using Google Chrome the editor will get stuck on "Please Wait: Loading" if there is Anti-Virus software installed. Disabling all AV services (Sophos or AVG) appears to fix the problem. Internet Explorer (ver. 11 tested) works (with AV software still running), but the IE page rendering is terrible and workflow the drawing is buggy.


