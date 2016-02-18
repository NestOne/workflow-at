# workflow-AT #

This is the authoring tool for constructing geoprocessing workflows comprised of Web Processing Services. The tool is based on a jBPM framework.


## Troubleshooting

* When passing outputs by reference the process result specified in the .WID definition should be set to StringDataType, temporary variable as String and the input it is mapped to as ObjectDataType.

* When compiling processes, a "only whitespace content allowed" compilation error probably indicates problems with the mapping of input/output variable data types in the workflow. E.g. the temporary variable data type must be a String when passing by reference OR if passing FeatureCollections it must be ObjectDataType.



