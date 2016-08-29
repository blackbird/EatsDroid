package com.company.server;

import com.company.controllers.AuthorizeController;
import com.company.controllers.MenuController;
import com.company.controllers.RestaurantController;
import com.company.controllers.UserController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

/**
 * Created by brian on 4/11/16.
 */
public class Router implements HttpHandler {

    HashMap<String, JsonRequestHandlerInterface> mapping = new HashMap<String, JsonRequestHandlerInterface>();
    Trie trie;
    public Router(){
        trie = new Trie();
        trie.insert("/users", new UserController());
        trie.insert("/restaurants", new RestaurantController());
        trie.insert("/menus", new MenuController());
        trie.insert("/authorize", new AuthorizeController());
        registerRoute("/users", new UserController());
        registerRoute("/restaurants", new RestaurantController());
        registerRoute("/menus", new MenuController());
        registerRoute("/authorize", new AuthorizeController());
    }

    public void registerRoute(String path, JsonRequestHandlerInterface requestHandler){
        mapping.put(path, requestHandler);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        URI uri = httpExchange.getRequestURI();
        String[] pathElements = uri.getPath().split("/");

        String mappingString = "/";
        if(pathElements.length > 0) { // Not "/" or ""
            mappingString = "/"+pathElements[1];
        }


        System.out.println("Mapping String:"+mappingString);
        JsonRequestHandlerInterface requestHandler = mapping.get(mappingString);
//        JsonRequestHandlerInterface requestHandler = null;
//        TrieNode node = trie.searchNode(mappingString);
//        if(node != null){
//            requestHandler = node.jsonRequestHandlerInterface;
//        }

        if(requestHandler == null){
            System.out.println("handler is null...");
            new JsonRequestHandler(new NotFoundRequestHandler()).handle(httpExchange);
        } else {
            new JsonRequestHandler(requestHandler).handle(httpExchange);
        }
    }
}
