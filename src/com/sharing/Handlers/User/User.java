package com.sharing.Handlers.User;

import java.io.Serializable;

/**
 * An abstraction to to represent an user
 */
public class User implements Serializable {

    private String name;
    private String ip;
    private int port;

    public User(String name, int port, String ip) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
