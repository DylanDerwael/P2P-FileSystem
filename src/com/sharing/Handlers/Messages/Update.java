package com.sharing.Handlers.Messages;

import com.sharing.Handlers.User.User;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Wrapper class to make the message sending and recieving more abstract
 * providing cleaner code
 */
public class Update implements Serializable {
    private ArrayList<User> users;

    public Update(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<User> getUser() {
        return users;
    }

    public void setUser(ArrayList<User> users) {
        this.users = users;
    }
}
