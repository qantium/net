package com.qantium.net.rest;

/**
 * Created by Solan on 22.01.2016.
 */

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ASolyankin
 */
public class RestApiRequest {

    private final HashMap<String, String> headers = new HashMap();
    private String responseEncoding = "UTF-8";
    private final String host;


    public RestApiRequest(String host) {
        this.host = host;
    }

    public RestApiRequest(URL url) {
        this.host = url.toString();
    }

    public String getHost() {
        return host;
    }

    public void setResponseEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding;
    }

    public String getResponseEncoding() {
        return responseEncoding;
    }

    public RestApiRequest setHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public RestApiRequest setHeaders(Map<String, String> headers) {

        for (String key : headers.keySet()) {
            setHeader(key, headers.get(key));
        }
        return this;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    private boolean isJSON(Object request) {
        return isJSONObject(request) || isJSONArray(request);
    }


    private boolean isJSONObject(Object request) {
        try {
            new JSONObject(request.toString());
            return true;
        } catch (JSONException ex) {
            return false;
        }
    }

    private boolean isJSONArray(Object request) {
        try {
            new JSONArray(request.toString());
            return true;
        } catch (JSONException ex) {
            return false;
        }
    }

    public RestApiResponse send(Method method, Object request) throws RestApiException {

        HttpURLConnection connection = null;
        String url = host;

        if (isJSON(request) || method == Method.DELETE) {
            setHeader("Content-Type", "application/json");
        }

        try {

            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method.toString());

            for (String key : headers.keySet()) {
                connection.setRequestProperty(key, headers.get(key));
            }

            if (method != Method.GET) {
                connection.setDoOutput(true);
                OutputStream writer = new BufferedOutputStream(connection.getOutputStream());
                writer.write(request.toString().getBytes(responseEncoding));
                writer.flush();
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = readStreamToString(connection.getInputStream(), responseEncoding);
                return new RestApiResponse(response);
            } else {
                throw new RestApiException(responseCode).setHost(host).setRequest(request);
            }

        } catch (IOException ex) {
            throw new RestApiException(ex).setHost(host).setRequest(request);
        }  finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public RestApiResponse get() throws RestApiException {
        return send(Method.GET, "");
    }

    public RestApiResponse post(Object request) throws RestApiException {
        return send(Method.POST, request);
    }

    public RestApiResponse put(Object request) throws RestApiException {
        return send(Method.PUT, request);
    }

    public RestApiResponse delete(Object request) throws RestApiException {
        return send(Method.DELETE, request);
    }

    public RestApiResponse delete() throws RestApiException {
        return delete("");
    }

    private String readStreamToString(InputStream in, String encoding) throws IOException {
        StringBuilder builder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in, encoding);
        int c;

        while ((c = reader.read()) != -1) {
            builder.append((char) c);
        }

        return builder.toString();
    }

}
