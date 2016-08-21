package com.sharing.Handlers.File;

import com.sharing.Handlers.User.User;

/**
 * Created by dylan on 8/16/16.
 */
public class PartWrapper {

    private String partName;
    private User user;

    public PartWrapper(String partName, User user) {
        this.partName = partName;
        this.user = user;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
