package com.sharing.Handlers.Messages;

/**
 * Wrapper class to make the message sending and recieving more abstract
 * providing cleaner code
 */

import com.sharing.Handlers.File.TransportWrapper;

import java.io.Serializable;

public class Transfer implements Serializable {

    private TransportWrapper trans;

    public Transfer(TransportWrapper trans) {
        this.trans = trans;
    }

    public byte[] getFile() {
        return trans.getFile();
    }

    public String getFileName() {return trans.getName();}

    public byte[] getHash() {return trans.getIntegrity();}

}
