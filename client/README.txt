Basic POM.XML usage
===================
0.- Don't forget to upload the 'nomaven-libs' to the MAVEN repository
=====================================================================

Some libraries used in the project are not published in public MAVEN repositories. 
Then they have to be uploaded following the instructions in nomaven-libs


1.- Compile and generate a full functional build
===================================

Just run: mvn clean install

There are two modules: client-swf and client-war. The first one is the Flex project, whereas the second one is an webapp project, that generates a WAR file
containing the Flex project, ready to be deployed



2.- Generate project structure for FlexBuilder
==========================================
To adjust the MAVEN project structure to Flex Builder, you only need to execute (under swf directory):

mvn org.sonatype.flexmojos:flexmojos-maven-plugin:3.6.1:flexbuilder

Then, under Flex Builder just make Import -> Existing Projects into Workspace, and select the client/swf directory. Flex Builder will detect the Flex project.
No further project configuration is needed.


NOTES:
======
* IMPORTANT: This Maven's configuration is set to work with Flex Builder 3, a test has been done with Flash Builder (Flex 4) and it threw lot of incompatibilites

* This project uses Maven's flexmojos plug-in (http://flexmojos.sonatype.org/, version 3.2.0) in order to work under Maven with a Flex project. Plug-in dependecy is defined in parent's pom.xml.

* Flex SDK 3.2.0 will automatically downloaded from Sonatype's repositores.

* Flex Application uses two Java applets to make remote connections to RDP and VNC servers. The JAR files are signed using a certificate
from Abiquo, but if you want to sign them with your own certificate, there are an unsigned jar copy under each applet directory. You
just have to replace the Abiquo's signed jar file with our signed ones. We recommend to leave the jar files under the unsigned-jar directory
unsigned.

* Flex Application's configuration file is located in resources/config/client-config.xml.jsp Due this, Flex Application must be deployed in
a JSP application server

* This pom.xml must be considered WORK IN PROGRESS.
* Do not use the Eclipse MAVEN Plugin. It's pure crap.
