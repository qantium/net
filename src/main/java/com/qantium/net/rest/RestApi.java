package com.qantium.net.rest;

import javax.xml.bind.DatatypeConverter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anton Solyankin
 */
public class RestApi {

    private String host;
    private String login;
    private String password;
    private String basicAuthorization;
    private final Map<String, String> headers = new HashMap();
    private String responseEncoding = "UTF-8";
    private Integer connectTimeout;
    private Integer readTimeout;

    public RestApi(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public RestApi setHost(String host) {
        this.host = host;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getBasicAuthorization() {
        return basicAuthorization;
    }

    public RestApi setHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public RestApi setHeaders(Map<String, String> headers) {

        for (String key : headers.keySet()) {
            setHeader(key, headers.get(key));
        }
        return this;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public RestApi setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public RestApi setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getResponseEncoding() {
        return responseEncoding;
    }

    public RestApi setResponseEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding;
        return this;
    }

    public RestApi setAuthorization(String login, String password) {
        this.login = login;
        this.password = password;
        basicAuthorization = null;
        return this;
    }

    public RestApi setBase64BasicAuthorization(String login, String password) {
        setAuthorization(login, password);
        basicAuthorization = DatatypeConverter.printBase64Binary((login + ":" + password).getBytes());
        setHeader("Authorization", "Basic " + getBasicAuthorization());
        return this;
    }

    public RestApiRequest createRequest() {
        return createRequest("");
    }

    public RestApiRequest createRequest(String url) {
        RestApiRequest request = new RestApiRequest(getHost() + url);
        request.setConnectTimeout(connectTimeout);
        request.setReadTimeout(readTimeout);
        request.setResponseEncoding(responseEncoding);
        request.setHeaders(headers);
        return request;
    }
}