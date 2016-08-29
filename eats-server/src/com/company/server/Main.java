package com.company.server;

import com.company.models.DatabaseAbstraction;
import com.company.models.Menu;
import com.company.sockets.EatsSocketServer;
import com.sun.net.httpserver.HttpServer;
import org.joda.time.DateTime;

import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

class Main {

    public static void main(String[] args) throws Exception {

//        System.out.println(new AuthorizeController().getUserFromToken("ya29..xwIHVcMPO2UMDlB2Xeju40eBl6rwEz1UyUZvzlLYpWA4qad8vUm5OaM-dAGLFTsZ4g"));

        java.util.Date date = new DateTime( "2016-04-13T00:00:00Z" ).toDate();
        System.out.println(date);

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
        c.set(Calendar.YEAR, 2016); // regular year
        c.set(Calendar.MONTH, 3); // from 0 - 11
        c.set(Calendar.DATE, 17);  // from 1 - 31..
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        List<Menu> menus = new DatabaseAbstraction().getMenus("2016-04-13");
        System.out.println("OUTS: "+menus.size());
//        if(true){
//            return;
//        }
        System.out.println("Starting socket server on port 8001...");
        EatsSocketServer.startServer();



        System.out.println("Starting server on port 8000...");
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new Router());
        server.setExecutor(null); // creates a default executor
        server.start();

    }

}
