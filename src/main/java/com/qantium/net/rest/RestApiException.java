package com.qantium.net.rest;

/**
 * @author Anton Solyankin
 */
public class RestApiException extends Exception {

    private int responseCode = -1;

    public RestApiException() {
    }

    public RestApiException(String message) {
        super(message);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestApiException setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    @Override
    public String toString() {
        return super.toString() + "\nResponse code: " + getResponseCode();
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\nResponse code: " + getResponseCode();
    }

}