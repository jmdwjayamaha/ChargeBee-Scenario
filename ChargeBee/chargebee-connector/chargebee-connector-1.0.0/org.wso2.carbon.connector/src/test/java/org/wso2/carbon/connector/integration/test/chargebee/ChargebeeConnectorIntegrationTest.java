/*
 *  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.integration.test.chargebee;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ChargebeeConnectorIntegrationTest extends ConnectorIntegrationTestBase {

	private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();

	private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

	private Map<String, String> parametersMap = new HashMap<String, String>();

	private String apiUrl;

	private String currentTimeString;

	/**
	 * Set up the environment.
	 */
	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {

		init("chargebee");

		esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
		esbRequestHeadersMap.put("Content-Type", "application/json");

		// Generates the Base64 encoded apiKey to be used for the authorization
		// of direct API calls via Authorization header
		String token = connectorProperties.getProperty("apiKey") + ":";
		byte[] encodedToken = Base64.encodeBase64(token.getBytes());

		apiRequestHeadersMap.putAll(esbRequestHeadersMap);
		apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedToken));
		apiRequestHeadersMap.put("Content-Type", "application/x-www-form-urlencoded");

		currentTimeString = String.valueOf(System.currentTimeMillis());
		apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v1";
	}

	/**
	 * Positive test case for createPlan method with mandatory parameters.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.
	 */
	@Test(groups = { "wso2.esb" }, description = "planyo {createPlan} integration test with mandatory parameters.")
	public void testCreatePlanWithMandatoryParameters() throws IOException, JSONException {

		String createPlanId = "createPlanIdMandatory_" + currentTimeString;
		String createPlanName = "createPlanNameMandatory_" + currentTimeString;
		connectorProperties.setProperty("createPlanIdMandatory", createPlanId);
		connectorProperties.setProperty("createPlanNameMandatory", createPlanName);

		esbRequestHeadersMap.put("Action", "urn:createPlan");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
		        "esb_createPlan_mandatory.json");

		String apiEndPoint = apiUrl + "/plans/" + createPlanId;
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(createPlanId, apiRestResponse.getBody().getJSONObject("plan").getString("id"));
		Assert.assertEquals(createPlanName, apiRestResponse.getBody().getJSONObject("plan").getString("name"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("plan").getString("price"), apiRestResponse
		        .getBody().getJSONObject("plan").getString("price"));
	}

	/**
	 * Positive test case for createPlan method with optional parameters.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePlanWithMandatoryParameters" }, description = "planyo {createPlan} integration test with optional parameters.")
	public void testCreatePlanWithOptionalParameters() throws IOException, JSONException {

		String createPlanId = "createPlanIdOptional_" + currentTimeString;
		String createPlanName = "createPlanNameOptional_" + currentTimeString;
		String createPlanInvoiceName = "createPlanInvoiceName_" + currentTimeString;

		connectorProperties.setProperty("createPlanIdOptional", createPlanId);
		connectorProperties.setProperty("createPlanNameOptional", createPlanName);
		connectorProperties.setProperty("createPlanInvoiceName", createPlanInvoiceName);

		esbRequestHeadersMap.put("Action", "urn:createPlan");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
		        "esb_createPlan_optional.json");
		JSONObject esbPlanObject = esbRestResponse.getBody().getJSONObject("plan");

		String apiEndPoint = apiUrl + "/plans/" + createPlanId;
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		JSONObject apiPlanObject = apiRestResponse.getBody().getJSONObject("plan");

		Assert.assertEquals(createPlanId, apiPlanObject.getString("id"));
		Assert.assertEquals(createPlanName, apiPlanObject.getString("name"));

		Assert.assertEquals(createPlanInvoiceName, apiPlanObject.getString("invoice_name"));
		Assert.assertEquals(esbPlanObject.getString("invoice_notes"), apiPlanObject.getString("invoice_notes"));
		Assert.assertEquals(esbPlanObject.getString("redirect_url"), apiPlanObject.getString("redirect_url"));
		Assert.assertEquals(esbPlanObject.getString("price"), apiPlanObject.getString("price"));
		Assert.assertEquals(esbPlanObject.getString("setup_cost"), apiPlanObject.getString("setup_cost"));

	}

	/**
	 * Negative test case for createPlan method. Create a Plan with an existing
	 * name and Id.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePlanWithOptionalParameters" }, description = "planyo {createPlan} integration test with negative case.")
	public void testCreatePlanWithNegativeCase() throws IOException, JSONException {

		String createPlanId = "createPlanIdMandatory_" + currentTimeString;
		String createPlanName = "createPlanNameMandatory_" + currentTimeString;
		connectorProperties.setProperty("createPlanIdMandatory", createPlanId);
		connectorProperties.setProperty("createPlanNameMandatory", createPlanName);

		esbRequestHeadersMap.put("Action", "urn:createPlan");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
		        "esb_createPlan_negative.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);

		String apiEndPoint = apiUrl + "/plans";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap,
		        "api_createPlan_negative.json");

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(esbRestResponse.getBody().getString("message"),
		        apiRestResponse.getBody().getString("message"));
		Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
		        apiRestResponse.getBody().getString("error_code"));
	}

	/**
	 * Positive test case for getPlan method with mandatory parameters.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreatePlanWithNegativeCase" }, description = "planyo {getPlan} integration test with mandatory parameters.")
	public void testgetPlanWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getPlan");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
		        "esb_getPlan_mandatory.json");

		String apiEndPoint = apiUrl + "/plans/" + connectorProperties.getProperty("createPlanIdMandatory");
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("plan").getString("id"),
		        connectorProperties.getProperty("createPlanIdMandatory"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("plan").getString("name"), apiRestResponse
		        .getBody().getJSONObject("plan").getString("name"));
		Assert.assertEquals(esbRestResponse.getBody().getJSONObject("plan").getString("price"), apiRestResponse
		        .getBody().getJSONObject("plan").getString("price"));
	}

	/**
	 * Negative test case for getPlan method. Retrieve a plan using an invalid
	 * Id.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = { "testgetPlanWithMandatoryParameters" }, description = "planyo {getPlan} integration test with negative case.")
	public void testgetPlanWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getPlan");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
		        "esb_getPlan_negative.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);

		String apiEndPoint = apiUrl + "/plans/--INVALID--";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(esbRestResponse.getBody().getString("message"),
		        apiRestResponse.getBody().getString("message"));
		Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
		        apiRestResponse.getBody().getString("error_code"));
	}

	/**
	 * Positive test case for listPlans method with mandatory parameters.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = { "testgetPlanWithMandatoryParameters" }, description = "planyo {listPlans} integration test with mandatory parameters.")
	public void testListPlansWithMandatoryParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listPlans");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
		        "esb_listPlans_mandatory.json");
		JSONArray esbPlansListArray = esbRestResponse.getBody().getJSONArray("list");

		String apiEndPoint = apiUrl + "/plans";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		JSONArray apiPlansListArray = apiRestResponse.getBody().getJSONArray("list");

		Assert.assertEquals(esbPlansListArray.length(), apiPlansListArray.length());
		Assert.assertEquals(esbPlansListArray.getJSONObject(0).getJSONObject("plan").getString("id"), apiPlansListArray
		        .getJSONObject(0).getJSONObject("plan").getString("id"));
		Assert.assertEquals(esbPlansListArray.getJSONObject(0).getJSONObject("plan").getString("name"),
		        apiPlansListArray.getJSONObject(0).getJSONObject("plan").getString("name"));
	}

	/**
	 * Positive test case for listPlans method with optional parameters.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListPlansWithMandatoryParameters" }, description = "planyo {listPlans} integration test with optional parameters.")
	public void testListPlansWithOptionalParameters() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listPlans");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
		        "esb_listPlans_optional.json");
		JSONArray esbPlansListArray = esbRestResponse.getBody().getJSONArray("list");
		Assert.assertEquals(esbPlansListArray.length(), 1);

		String apiEndPoint = apiUrl + "/plans?limit=1";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
		JSONArray apiPlansListArray = apiRestResponse.getBody().getJSONArray("list");

		Assert.assertEquals(esbPlansListArray.length(), apiPlansListArray.length());
		Assert.assertEquals(esbPlansListArray.getJSONObject(0).getJSONObject("plan").getString("id"), apiPlansListArray
		        .getJSONObject(0).getJSONObject("plan").getString("id"));
		Assert.assertEquals(esbPlansListArray.getJSONObject(0).getJSONObject("plan").getString("name"),
		        apiPlansListArray.getJSONObject(0).getJSONObject("plan").getString("name"));
	}

	/**
	 * Negative test case for listPlans method.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.
	 */
	@Test(groups = { "wso2.esb" }, dependsOnMethods = { "testListPlansWithOptionalParameters" }, description = "planyo {listPlans} integration test with negative case.")
	public void testListPlansWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:listPlans");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
		        "esb_listPlans_negative.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);

		String apiEndPoint = apiUrl + "/plans?limit=0";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
		Assert.assertEquals(esbRestResponse.getBody().getString("message"),
		        apiRestResponse.getBody().getString("message"));
		Assert.assertEquals(esbRestResponse.getBody().getString("error_code"),
		        apiRestResponse.getBody().getString("error_code"));
	}

}
