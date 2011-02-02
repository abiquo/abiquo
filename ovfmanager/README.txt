Basic POM.XML usage
===================
0.- Don't forget to upload the 'nomaven-libs' to the MAVEN repository
=====================================================================
Some libraries used in the project are not published in public MAVEN repositories. Then they have to be uploaded following the instructions in nomaven-libs

1.- Generate full project structure
===================================
The first the overall structure of the project has to be generated. In order to create the structure execute:

mvn clean install -Pgenerate-sources

The project will compile, build, and tests will run and finally a file will be uploaded to the local maven repositories:

OVFManager-0.7.0-SNAPSHOT.jar

2.- Generate project structure for eclipse
==========================================
To adjust the MAVEN project structure to ECLIPSE, you only need to execute:

mvn eclipse:clean eclipse:eclipse -Pgenerate-sources

Refresh the OVFManager project and you are ready to work. Configure the M2_REPO manually if necessary.

3.- Generate project structure without regenerating the source code
===================================================================
If the self-generated code has not changed, you can improve the deployment time just executing avoiding the -Pgenerate-sources profile flag.

mvn clean install

NOTES:
======
* This pom.xml must be considered WORK IN PROGRESS.
* Do not use the Eclipse MAVEN Plugin. It's pure crap.
