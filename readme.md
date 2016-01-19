# workflow-AT #

This is the authoring tool for constructing geoprocessing workflows comprised of Web Processing Services. The tool is based on a jBPM framework.


## Troubleshooting

* When compiling processes, a "only whitespace content allowed" compilation error probably indicates problems with the mapping of input/output variable data types in the workflow. E.g. the temporary variable data type must be a String when passing by reference OR if passing FeatureCollections it must be ObjectDataType.
