package com.sharing;

import com.sharing.Handlers.Systems.Client;
import com.sharing.Handlers.Systems.Server;

public class Main {

    public static void main(String[] args) {

        if(args[0].toLowerCase().equals("server")){
            new Server();
        } else {
            new Client(args[0].toLowerCase(), args[1]);
        }
    }
}
