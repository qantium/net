package com.qantium.net.rest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Anton Solyankin
 */
public class RestApiResponse {

    private final String response;
    private final int responseCode;

    RestApiResponse(String response, int responseCode) {
        this.response = response;
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        return response;
    }

    public JSONObject toJSONObject() throws JSONException {
        try {
            return new JSONObject(response);
        } catch (JSONException ex) {
            throw new JSONException("Cannot get JSONObject from response: " + response + "\nCause: " + ex);
        }
    }

    public JSONArray toJSONArray() throws JSONException {
        try {
            return new JSONArray(response);
        } catch (JSONException ex) {
            throw new JSONException("Cannot get JSONArray from response: " + response + "\nCause: " + ex);
        }
    }
}
