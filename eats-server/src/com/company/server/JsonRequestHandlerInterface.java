package com.company.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Created by brian on 4/11/16.
 */

public interface JsonRequestHandlerInterface {

    public ResponseTuple handleLogic(HttpExchange t) throws IOException;

}
