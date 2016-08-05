Example to show how QA people can place expected responses to the class path for given requests, to be able to "configure" the behavior for the mock for their needs.

# Configure JBoss
Add

<subsystem xmlns="urn:jboss:domain:ee:1.0" >
  <global-modules>
    <module name="config" />
  </global-modules>
</subsystem>

to the standalone.xml file.

Copy the ${basedir}/module.xml file to ${JBOSS_HOME}/modules/config/main

Copy all *.xml files from ${basedir}/src/test/resources to ${JBOSS_HOME}/modules/config/main

# Build
Execute: mvn clean install

# Install
Copy ${basedir}/target/jaxws-mock-sample.war to ${JBOSS_HOME}/standalone/deployments

# Run
Execute the org.apache.cmueller.sample.service.EBookServiceIT integration tests

# Interface
You can lookup the WSDL file at http://localhost:8080/jaxws-mock-sample/EBookStoreImpl?wsdl