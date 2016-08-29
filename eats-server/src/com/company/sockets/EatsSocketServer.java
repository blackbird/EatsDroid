package com.company.sockets;

import com.company.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by brian on 4/16/16.
 */
public class EatsSocketServer implements Runnable{

    private static volatile EatsSocketServer serverInstance;

    // Adapted from http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html
    protected int port = Constants.SOCKET_PORT;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;

    public EatsSocketServer() {

    }

    public void run(){
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                System.out.println("SOCKETS: Blocking on accept.");
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            System.out.println("SOCKETS: Handler thread created.");
            new Thread(
                    new RequestHandler(clientSocket) // Start worker to process request
            ).start();
        }
        System.out.println("Server Stopped.") ;
    }


    // Have to be thread safe
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }

    public EatsSocketServer(int port){
        this.port = port;
    }


    public static void startServer(){
        if(EatsSocketServer.serverInstance == null){
            EatsSocketServer.serverInstance = new EatsSocketServer();
            new Thread(EatsSocketServer.serverInstance).start();
        }
    }


    public static void stopServer(){
        if(EatsSocketServer.serverInstance != null){
            EatsSocketServer.serverInstance.stop();
            EatsSocketServer.serverInstance = null;
        }
    }

}
