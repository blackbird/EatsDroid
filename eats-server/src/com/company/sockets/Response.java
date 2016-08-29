package com.company.sockets;

import com.company.models.Menu;
import com.sun.tools.javac.util.List;

import java.io.Serializable;

/**
 * Created by brian on 4/16/16.
 */
public class Response implements Serializable {
    public boolean requestSuccess;
    public Object data;

    // Pragma: Request
    public static class ResponsePullMenus  implements Serializable{
        List<Menu> menus;
    }

    public Response(boolean requestSuccess, Object data) {
        this.requestSuccess = requestSuccess;
        this.data = data;
    }
}
