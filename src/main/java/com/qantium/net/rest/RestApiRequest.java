package com.qantium.net.rest;

/**
 * Created by Solan on 22.01.2016.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ASolyankin
 */
public class RestApiRequest {

    private final HashMap<String, String> headers = new HashMap();
    private String responseEncoding = "UTF-8";
    private final String host;
    private File file;
    private boolean writeToFile;
    private int successfulResponseCode = HttpURLConnection.HTTP_OK;


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

            if (responseCode == successfulResponseCode) {
                String response = getResponseContent(connection.getInputStream());
                return new RestApiResponse(response, responseCode);
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

    private String getResponseContent(InputStream in) throws IOException {
        if(writeToFile) {
            file.delete();
            Files.copy(in, file.toPath());
            return new String(Files.readAllBytes(file.toPath()));
        } else {
            return readStreamToString(in, responseEncoding);
        }

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

    public RestApiRequest writeToFile(File file) {
        this.file = file;
        return writeToFile(true);
    }

    public RestApiRequest writeToFile(boolean writeToFile) {
        this.writeToFile = writeToFile;
        return this;
    }

    public int getSuccessfulResponseCode() {
        return successfulResponseCode;
    }

    public RestApiRequest withSuccessfulResponseCode(int successfulResponseCode) {
        this.successfulResponseCode = successfulResponseCode;
        return this;
    }
}
