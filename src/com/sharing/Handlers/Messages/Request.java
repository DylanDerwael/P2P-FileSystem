package com.sharing.Handlers.Messages;

/**
 * Wrapper class to make the message sending and recieving more abstract
 * providing cleaner code
 */

import com.sharing.Handlers.User.User;

import java.io.Serializable;

public class Request implements Serializable {

    private String fileName;
    private User user;

    public Request(String fileName, User user) {
        this.fileName = fileName;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFileName() {
        return fileName;
    }
}
