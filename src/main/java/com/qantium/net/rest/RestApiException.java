package com.qantium.net.rest;

/**
 * @author Anton Solyankin
 */
public class RestApiException extends Exception {

    private int responseCode = -1;
    private String host;
    private Object request;
    private Object response;

    public RestApiException(int responseCode) {
        this("Cannot send response", responseCode);

    }

    public RestApiException(String message, int responseCode) {
        super(message);
        this.responseCode = responseCode;
    }

    public RestApiException(String message, Throwable ex) {
        super(message, ex);
    }

    public RestApiException(Throwable ex) {
        this("Cannot send request", ex);
    }

    public String getHost() {
        return host;
    }

    public RestApiException setHost(String host) {
        this.host = host;
        return this;
    }

    public Object getRequest() {
        return request;
    }

    public RestApiException setRequest(Object request) {
        this.request = request;
        return this;
    }

    public Object getResponse() {
        return response;
    }

    public RestApiException setResponse(Object response) {
        this.response = response;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder error = new StringBuilder();
        error.append(getMessage());

        if(host != null && !host.toString().isEmpty()) {
            error.append(" to ").append(host);
        }

        error.append("!");

        if(responseCode > -1) {
            error.append("\n").append("Response code: ").append(responseCode);
        }

        if (response != null && !response.toString().isEmpty()) {
            error
                    .append("\n")
                    .append("Response: ").append(response);
        }

        if (request != null && !request.toString().isEmpty()) {
            error
                    .append("\n")
                    .append("Request: ").append(request);
        }

        return error.toString();
    }
}