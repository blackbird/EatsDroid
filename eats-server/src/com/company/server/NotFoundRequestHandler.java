package com.company.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Created by brian on 4/11/16.
 */
public class NotFoundRequestHandler implements  JsonRequestHandlerInterface{

    @Override
    public ResponseTuple handleLogic(HttpExchange t) throws IOException {
        ResponseTuple responseTuple = new ResponseTuple(404, "{\"error\":\"404 not found :/\"}");
        return responseTuple;
    }

    public static ResponseTuple throw404(HttpExchange t) throws  IOException{
        NotFoundRequestHandler notFoundRequestHandler = new NotFoundRequestHandler();
        return notFoundRequestHandler.handleLogic(t);
    }
}
