package com.company.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class JsonRequestHandler implements  HttpHandler{

    private  JsonRequestHandlerInterface requestHandler;



    public JsonRequestHandler(JsonRequestHandlerInterface requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {

        ResponseTuple responseTuple = this.requestHandler.handleLogic(t);
        String response = responseTuple.responseBody;
        int statusCode = responseTuple.statusCode;

        Headers headers = t.getResponseHeaders();
        headers.add("Content-Type", "application/json");
        t.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}
