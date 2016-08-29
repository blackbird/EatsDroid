package com.company.sockets;

import com.company.models.DatabaseAbstraction;
import com.company.models.Menu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by brian on 4/16/16.
 */
public class RequestHandler implements Runnable{

    public Socket clientSocket;
    public RequestHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        System.out.println("SOCKETS: Handler beginning");
        try {
            // Deal with responding to the client
            ObjectInputStream ois =
                    new ObjectInputStream(clientSocket.getInputStream());
            try {
                Request request = (Request)ois.readObject();
                // Route
                if(request.data instanceof Request.RequestCheckAuthentication) {
                    System.out.println("SOCKETS: Checking authentication sent");
                    Request.RequestCheckAuthentication auth = (Request.RequestCheckAuthentication)request.data;
                    respond(new Response(new AuthenticationChecker(auth).isValid(), null));
                }else{
                    if(!new AuthenticationChecker(request.auth).isValid()) {
                        respond(new Response(false, null));
                    }else if(request.data instanceof  Request.RequestMenuAdd) {
                        Request.RequestMenuAdd addCtx = (Request.RequestMenuAdd)request.data;
                        respond(new Response(new DatabaseAbstraction().addItem(addCtx._id, addCtx.meal_identifier, addCtx.section_identifier, addCtx.food_identifier, addCtx.food_name), null));
                    }else if(request.data instanceof  Request.RequestMenuDelete) {
                        Request.RequestMenuDelete addCtx = (Request.RequestMenuDelete)request.data;
                        respond(new Response(new DatabaseAbstraction().removeItem(addCtx._id, addCtx.meal_identifier, addCtx.section_identifier, addCtx.food_identifier), null));
                    }else if(request.data instanceof  Request.RequestPullMenus) {
                        Request.RequestPullMenus addCtx = (Request.RequestPullMenus)request.data;
                        List<Menu> menus = new DatabaseAbstraction().getMenus(addCtx.date);
                        respond(new Response(true, menus));
                    }
                }
            } catch (ClassNotFoundException e) {
                respond(new Response(false, null));
                e.printStackTrace();
                return;
            }
        } catch (IOException e) {
            respond(new Response(false, null));
            e.printStackTrace();
            return;
        }
    }

    public void respond(Response resp){
        ObjectOutputStream oos;
        try {
            oos = new
                    ObjectOutputStream(clientSocket.getOutputStream());
            oos.writeObject(resp);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
