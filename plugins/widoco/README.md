# Widoco plugin
Allows generating HTML documentation using [Widoco](https://github.com/dgarijo/Widoco)
at the end of the publication process.

## Difference to GUI Wizard
Widoco has several bugs causing different documentation being generated when 
windowed GUI Wizard is used and when invoked through command line (CLI).

The Widoco JAR can be downloaded at [GitHub releases](https://github.com/dgarijo/Widoco/releases).

To test results locally, you can execute Widoco from cmmand line as
```bash
java -jar widoco-VERSION-jar-with-dependencies.jar -ontFile myOntology.ttl -outFolder widoco_output -includeAnnotationProperties -noPlaceHolderText -uniteSections -webVowl 
```

The arguments passed to the execution depends on the form options configured during the publishing process in OntoPuS.

All available options can be found in [Widoco's README in Execution options section](https://github.com/dgarijo/Widoco#execution-options).

