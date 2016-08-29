package com.company.server;

/**
 * Created by brian on 4/11/16.
 */
public class ResponseTuple {
    int statusCode;
    String responseBody;
    public ResponseTuple(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}
