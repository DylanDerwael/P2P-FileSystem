package com.sharing.Handlers.Messages;

import com.sharing.Handlers.User.User;

import java.io.Serializable;

/**
 * Wrapper class to make the message sending and receiving more abstract
 * providing cleaner code
 */
public class Offline implements Serializable {
    private User user;

    public Offline(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
