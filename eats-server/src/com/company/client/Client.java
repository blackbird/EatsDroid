package com.company.client;

import com.company.Constants;
import com.company.sockets.Request;
import com.company.sockets.Response;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by brian on 4/16/16.
 */
public class Client implements Runnable{

   int port = Constants.SOCKET_PORT;
   Request request;
   ResponseInterface respCallback;

   public Client(Request request, ResponseInterface respCallback){
      this.request = request;
      this.respCallback = respCallback;
   }

   public void send(){
      new Thread(this).start();
   }

   public void run(){
      System.out.println("SOCKETS: Attempting to send from client");
      try {
         Socket s = new Socket(Constants.SOCKET_HOST, Constants.SOCKET_PORT);
         ObjectOutputStream  oos = new ObjectOutputStream(s.getOutputStream());
         oos.writeObject(request);
         ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
         Response resp = (Response) ois.readObject();
         this.respCallback.callback(resp);
      } catch (IOException e) {
         e.printStackTrace();
         this.respCallback.callback(null);
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
         this.respCallback.callback(null);
      } 
      new Thread(this.respCallback).start();
   }

}
