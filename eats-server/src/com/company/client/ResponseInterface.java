package com.company.client;

import com.company.sockets.Response;

/**
 * Created by brian on 4/16/16.
 */
public interface ResponseInterface extends Runnable{

    public void callback(Response resp);

}
