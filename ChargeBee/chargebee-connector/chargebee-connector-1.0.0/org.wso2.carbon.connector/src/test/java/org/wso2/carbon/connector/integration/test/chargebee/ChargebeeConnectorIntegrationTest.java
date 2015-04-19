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

	/**
     * Positive test case for createCoupon method with mandatory parameters.
     * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.            
     */
    @Test(groups = { "wso2.esb" }, description = "chargebee {createCoupon} integration test with mandatory parameters.")
    public void testCreateCouponWithMandatoryParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createCoupon");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_mandatory.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       final String couponId = esbRestResponse.getBody().getJSONObject("coupon").getString("id");
       connectorProperties.setProperty("couponId", couponId);
       
       final String apiEndPoint = apiUrl+"/coupons/" + couponId;
       final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       final JSONObject apiResponseObject=apiRestResponse.getBody().getJSONObject("coupon");
       
       Assert.assertEquals(apiResponseObject.getString("id"), connectorProperties.getProperty("couponIdMand"));
       Assert.assertEquals(apiResponseObject.getString("name"), connectorProperties.getProperty("couponNameMand"));
       Assert.assertEquals(apiResponseObject.getString("discount_amount"), connectorProperties.getProperty("discountAmount"));
       Assert.assertEquals(apiResponseObject.getString("duration_type"), connectorProperties.getProperty("durationType"));
    }
    
    /**
     * Positive test case for createCoupon method with optional parameters.
     * 
     * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.     
     */
    @Test(groups = { "wso2.esb" }, description = "chargebee {createCoupon} integration test with optional parameters.")
    public void testCreateCouponWithOptionalParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createCoupon");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_optional.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       final String couponId = esbRestResponse.getBody().getJSONObject("coupon").getString("id");
       
       final String apiEndPoint = apiUrl+ "/coupons/" + couponId;
       final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       final JSONObject apiResponseObject=apiRestResponse.getBody().getJSONObject("coupon");
       
       Assert.assertEquals(apiResponseObject.getString("invoice_name"), connectorProperties.getProperty("invoiceNameOpt"));
       Assert.assertEquals(apiResponseObject.getString("invoice_notes"), connectorProperties.getProperty("invoiceNotesOpt"));
       Assert.assertEquals(apiResponseObject.getString("valid_till"), connectorProperties.getProperty("validTill"));
       Assert.assertEquals(apiResponseObject.getString("max_redemptions"), connectorProperties.getProperty("maxRedemptions"));   
    }
    
    /**
     * Negative test case for createCoupon method.
     * 
     * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.     
     */
    @Test(groups = { "wso2.esb" }, description = "chargebee {createCoupon} integration test with negative case.")
    public void testCreateCouponWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createCoupon");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_negative.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
       
       final String apiEndPoint = apiUrl + "/coupons";
       final RestResponse<JSONObject> apiRestResponse =
             sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap, "api_createCoupon_negative.json");
       
       Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
       Assert.assertEquals(esbRestResponse.getBody().getString("error_code"), apiRestResponse.getBody().getString("error_code"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error_msg"), apiRestResponse.getBody().getString("error_msg"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error_param"), apiRestResponse.getBody().getString("error_param"));
       Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString("message"));
    }
    
    /**
     * Positive test case for getCoupon method with mandatory parameters.
     * 
     * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.     
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCouponWithMandatoryParameters" }, description = "chargebee {getCoupon} integration test with mandatory parameters.")
    public void testGetCouponWithMandatoryParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:getCoupon");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCoupon_mandatory.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       
       final JSONObject esbResponseObject= esbRestResponse.getBody().getJSONObject("coupon");
       
       final String apiEndPoint = apiUrl + "/coupons/" + connectorProperties.getProperty("couponId");
       final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
       
       final JSONObject apiResponseObject= apiRestResponse.getBody().getJSONObject("coupon");
       
       Assert.assertEquals(esbResponseObject.getString("id"), apiResponseObject.getString("id"));
       Assert.assertEquals(esbResponseObject.getString("name"), apiResponseObject.getString("name"));
       Assert.assertEquals(esbResponseObject.getString("discount_type"), apiResponseObject.getString("discount_type"));
       Assert.assertEquals(esbResponseObject.getString("apply_on"), apiResponseObject.getString("apply_on"));
       Assert.assertEquals(esbResponseObject.getLong("created_at"), apiResponseObject.getLong("created_at"));
    }
    
    /**
	 * Negative test case for getCoupon method.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.
	 */
	@Test(groups = { "wso2.esb" }, description = "chargebee {getCoupon} integration test with negative case.")
	public void testgetCouponWithNegativeCase() throws IOException, JSONException {

		esbRequestHeadersMap.put("Action", "urn:getCoupon");
		RestResponse<JSONObject> esbRestResponse = sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap,
		        "esb_getCoupon_negative.json");
		Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);

		String apiEndPoint = apiUrl + "/coupons/INVALID";
		RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);

		Assert.assertEquals(esbRestResponse.getBody().getString("error_code"), apiRestResponse.getBody().getString("error_code"));
	    Assert.assertEquals(esbRestResponse.getBody().getString("error_msg"), apiRestResponse.getBody().getString("error_msg"));
	    Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString("message"));
	}
    
    /**
     * Positive test case for listCoupons method with mandatory parameters.
     * 
     * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.     
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCouponWithMandatoryParameters","testCreateCouponWithOptionalParameters" }, description = "chargebee {listCoupons} integration test with mandatory parameters.")
    public void testListCouponsWithMandatoryParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:listCoupons");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCoupons_mandatory.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       
       final int esbResponseArrayLength= esbRestResponse.getBody().getJSONArray("list").length();
       final JSONObject esbResponseObjectOne=esbRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("coupon");
       
       final String apiEndPoint = apiUrl + "/coupons";
       final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
       
       final int apiResponseArrayLength= apiRestResponse.getBody().getJSONArray("list").length();
       final JSONObject apiResponseObjectOne=apiRestResponse.getBody().getJSONArray("list").getJSONObject(0).getJSONObject("coupon");
       
       Assert.assertEquals(esbResponseArrayLength, apiResponseArrayLength);
       
       Assert.assertEquals(esbResponseObjectOne.getString("id"), apiResponseObjectOne.getString("id"));
       Assert.assertEquals(esbResponseObjectOne.getString("name"), apiResponseObjectOne.getString("name"));
       Assert.assertEquals(esbResponseObjectOne.getString("discount_type"), apiResponseObjectOne.getString("discount_type"));
       Assert.assertEquals(esbResponseObjectOne.getString("apply_on"), apiResponseObjectOne.getString("apply_on"));
       Assert.assertEquals(esbResponseObjectOne.getString("created_at"), apiResponseObjectOne.getString("created_at"));
    }
    
    /**
     * Positive test case for listCoupons method with optional parameters.
     * 
     * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.     
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCouponWithMandatoryParameters","testCreateCouponWithOptionalParameters" }, description = "chargebee {listCoupons} integration test with optional parameters.")
    public void testListCouponsWithOptionalParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:listCoupons");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCoupons_optional.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       
       int esbResponseArrayLength= esbRestResponse.getBody().getJSONArray("list").length();
       
       String apiEndPoint = apiUrl + "/coupons";
       RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
       
       int apiResponseArrayLength= apiRestResponse.getBody().getJSONArray("list").length();
       
       Assert.assertNotEquals(esbResponseArrayLength, apiResponseArrayLength);
       
       apiEndPoint = apiUrl + "/coupons?limit=1";
       apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       apiResponseArrayLength= apiRestResponse.getBody().getJSONArray("list").length();
       
       Assert.assertEquals(esbResponseArrayLength, apiResponseArrayLength);
   
    }
    
    /**
     * Negative test case for listCoupons method.
     * 
     * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSONException
	 *             if JSON exception occurred.     
     */
    @Test(groups = { "wso2.esb" }, description = "chargebee {listCoupons} integration test with negative case.")
    public void testListCouponsWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:listCoupons");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listCoupons_negative.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
       
       final String apiEndPoint = apiUrl + "/coupons?limit=INVALID";
       final RestResponse<JSONObject> apiRestResponse =
             sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
       Assert.assertEquals(esbRestResponse.getBody().getString("error_code"), apiRestResponse.getBody().getString("error_code"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error_msg"), apiRestResponse.getBody().getString("error_msg"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error_param"), apiRestResponse.getBody().getString("error_param"));
       Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString("message"));
    }

}
