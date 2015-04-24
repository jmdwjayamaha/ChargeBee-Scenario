Product: Integration tests for WSO2 ESB ChargeBee connector

Pre-requisites:

 - Maven 3.x
 - Java 1.6 or above
 - The org.wso2.esb.integration.integration-base project is required. The test suite has been configured to download this project automatically. If the automatic download fails, download the following project and compile it using the mvn clean install command to update your local repository:
   https://github.com/wso2-dev/esb-connectors/tree/master/integration-base

Tested Platform: 

 - Microsoft WINDOWS V-7
 - UBUNTU 13.04
 - WSO2 ESB 4.8.1

Steps to follow in setting integration test.

 1. Download ESB 4.8.1 from official website.

 2. Deploy relevant patches, if applicable and the ESB should be configured as below.
	 Please make sure that the below mentioned Axis configurations are enabled (\repository\conf\axis2\axis2.xml).
		
		<messageFormatter contentType="text/html" 
									class="org.wso2.carbon.relay.ExpandingMessageFormatter"/>
		<messageBuilder contentType="text/html" 
									class="org.wso2.carbon.relay.BinaryRelayBuilder"/>

 3. Compress modified ESB as wso2esb-4.8.1.zip and copy that zip file in to location "{ChargeBee_Connector_Home}/chargebee-connector/chargebee-connector-1.0.0/org.wso2.carbon.connector/repository/".

 4. Create a ChargeBee trial account and derive the API Key.
	i) 		Using the URL "http://www.chargebee.com/" create a ChargeBee trial account.
	ii)		Login to the created ChargeBee account and go to https://{Unique ChargeBee URL for created account}.chargebee.com/exec/devapp/oauth_clients Create a dummy client of yours and Get a permanent Access Token from there.

 5. Update the ChargeBee properties file at location "{ChargeBee_Connector_Home}/chargebee-connector/chargebee-connector-1.0.0/org.wso2.carbon.connector/src/test/resources/artifacts/ESB/connector/config" as below.
	
	i)		apiUrl 							- 	The API URL specific to the created account.
	ii) 	apiKey							-   Use the API Key obtained under Step 4 ii).
	iii)	discountAmount					- 	Use a Integer value as a discount amount in coupon.
	iv)		durationType					-	Use a valid discount type of a coupon as documented in the API documentation
	v)		invoiceNameOpt					-	Use a valid string value as invoice name.
	vi)		invoiceNameOpt					-	Use a valid string value as invoice name.
	vii)	validTill						-	Use a valid future UTC timestamp in seconds.
	viii)	maxRedemptions					-	Use a Integer value.
	ix)		paidOnAfter						-   Use a valid past UTC timestamp in seconds. Note that there should be a paid invoice after this timestamp.
	x)		eventType						-	Use a valid event type of a events as documented in the API documentation
	xi)	    companyName			        	-   Use a valid string value as company name.
	xii)   	email			    			-   Use a any email address which is correctly formatted.
	xi)	    firstName			    		-   Use a valid string value as customer's first name.
	xii)	lastName			    		- 	Use a valid string value as customer's last name.
	xiii)	notes			    			- 	Use a valid string value as note's content.
	xiv)	firstNameUpdated				- 	Use a valid string value as customer's first name.
	xv)	    lastNameUpdated					- 	Use a valid string value as customer's last name.
	xvi)	notesUpdated					- 	Use a valid string value as note's content.
	xvii)   emailUpdated					-   Use a any email address which is correctly formatted.

	Note :- There should be a paid invoice which was paid after the value mentioned in (ix) property.
	
 6. Navigate to "{ChargeBee_Connector_Home}/chargebee-connector/chargebee-connector-1.0.0/org.wso2.carbon.connector/" and run the following command.
      $ mvn clean install
		