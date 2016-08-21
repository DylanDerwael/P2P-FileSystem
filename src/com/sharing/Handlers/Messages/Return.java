package com.sharing.Handlers.Messages;

//import com.sharing.Handlers.File.TransportWrapper;

import com.sharing.Handlers.File.TransportWrapper;

import java.io.Serializable;

/**
 * Created by dylan on 6/6/16.
 */
public class Return implements Serializable {

    private TransportWrapper trans;

    public Return(TransportWrapper trans) {
        this.trans = trans;
    }

    public byte[] getFile() {
        return trans.getFile();
    }

    public String getFileName() {
        return trans.getName();
    }

}
