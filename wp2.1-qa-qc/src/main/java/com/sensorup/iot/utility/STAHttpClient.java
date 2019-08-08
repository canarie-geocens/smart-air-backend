package com.sensorup.iot.utility;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class STAHttpClient {

    private static final Logger logger = LoggerFactory
            .getLogger(STAHttpClient.class);


    public static STAHttpClient STAHttpClient;

    AsyncHttpClient client = null;

    public static STAHttpClient getInstance(){
        if(STAHttpClient == null) {
            STAHttpClient = new STAHttpClient();
            STAHttpClient.client = new DefaultAsyncHttpClient();
        }
        return STAHttpClient;
    }

    public Map<String,Object> doPost(String postBody, String postURL){
        try {
            String encoded = null;
            if(PropertyReader.getInstance().getProperty("sta-username") != null &&
                    PropertyReader.getInstance().getProperty("sta-password") != null) {
                encoded = Base64.encode((PropertyReader.getInstance().getProperty("sta-username") + ":" +
                        PropertyReader.getInstance().getProperty("sta-password")).getBytes());
            }
            byte[] postData = postBody.getBytes("UTF8");

            Response response = null;
            if(encoded == null) {
                response = client.preparePost(postURL)
                        .addHeader("Content-Type", "application/json")
                        .setBody(postData)
                        .execute().get();
            } else {
                response = client.preparePost(postURL)
                        .addHeader("Content-Type", "application/json")
                        .setBody(postData)
                        .addHeader("Authorization", "Basic " + encoded)
                        .execute().get();
            }

            Map<String, Object> result = new HashMap<String, Object>();
            result.put("response-code",response.getStatusCode());
            if(response.getStatusCode()==201) {
                result.put("response", response.getHeader("location"));
            } else {
                logger.error("An error occurred for posting entity to: " +postURL+" ! The request returned with status code: "+ response.getStatusCode());
                result.put("response", "");
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An exception occurred for posting entity: "+e.getMessage());
            return null;
        }
    }

    public Map<String,Object> doGet(String getURL){
        return doGet(getURL, false);
    }

    public Map<String,Object> doGet(String getURL, boolean isConfigurationServer){

        try {
            String url = getURL;
            String encoded = null;
            Response response;
            if(encoded == null){
                response = client.prepareGet(url).execute().get();
            } else{
                response = client.prepareGet(url).addHeader("Authorization", "Basic " + encoded).execute().get();
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("response-code",response.getStatusCode());
            if(response.getStatusCode()==200) {
                String responseBody = response.getResponseBody(StandardCharsets.UTF_8);
                result.put("response",responseBody);
            } else {
                logger.error("An error occured when GETting '"+ url +"'! The request returned with status code: "+ response.getStatusCode());
                result.put("response", "");
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An exception occurred during GETting Observation from STA: "+e.getMessage());
            return null;
        }
    }

    public Map<String,Object> doPut(String putBody, String putURL){

        try {
            String encoded = null;
            if(PropertyReader.getInstance().getProperty("sta-username") != null &&
                    PropertyReader.getInstance().getProperty("sta-password") != null) {
                encoded = Base64.encode((PropertyReader.getInstance().getProperty("sta-username") + ":" +
                        PropertyReader.getInstance().getProperty("sta-password")).getBytes());
            }
            byte[] postData = putBody.getBytes("UTF8");

            Response response = null;
            if(encoded == null) {
                response = client.preparePut(putURL)
                        .addHeader("Content-Type", "application/json")
                        .setBody(postData)
                        .execute().get();
            } else {
                response = client.preparePut(putURL)
                        .addHeader("Content-Type", "application/json")
                        .setBody(postData)
                        .addHeader("Authorization", "Basic " + encoded)
                        .execute().get();
            }

            Map<String, Object> result = new HashMap<String, Object>();
            result.put("response-code",response.getStatusCode());
            if(response.getStatusCode()==200) {
                result.put("response", response.getHeader("location"));
            } else {
                logger.error("An error occurred for putting entity to: " +putURL+" ! The request returned with status code: "+ response.getStatusCode());
                result.put("response", "");
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An exception occurred for putting entity: "+e.getMessage());
            return null;
        }
    }

    public Map<String,Object> doPatch(String patchBody, String patchURL){

        try {
            String encoded = null;
            if(PropertyReader.getInstance().getProperty("sta-username") != null &&
                    PropertyReader.getInstance().getProperty("sta-password") != null) {
                encoded = Base64.encode((PropertyReader.getInstance().getProperty("sta-username") + ":" +
                        PropertyReader.getInstance().getProperty("sta-password")).getBytes());
            }
            byte[] postData = patchBody.getBytes("UTF8");

            Response response = null;
            if(encoded == null) {
                response = client.preparePatch(patchURL)
                        .addHeader("Content-Type", "application/json")
                        .setBody(postData)
                        .execute().get();
            } else {
                response = client.preparePatch(patchURL)
                        .addHeader("Content-Type", "application/json")
                        .setBody(postData)
                        .addHeader("Authorization", "Basic " + encoded)
                        .execute().get();
            }

            Map<String, Object> result = new HashMap<String, Object>();
            result.put("response-code",response.getStatusCode());
            if(response.getStatusCode()==200) {
                result.put("response", response.getHeader("location"));
            } else {
                logger.error("An error occurred for patching entity to: " +patchURL+" ! The request returned with status code: "+ response.getStatusCode());
                result.put("response", "");
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An exception occurred for patching entity: "+e.getMessage());
            return null;
        }
    }

}
