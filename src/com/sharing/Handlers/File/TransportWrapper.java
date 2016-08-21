package com.sharing.Handlers.File;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

/**
 * Abstraction to transport the data to other users in the network
 * with the integrity check so they can validate that what was received is a valid value
 */
public class TransportWrapper implements Serializable{
    private byte[] integerty;
    private byte[] file;
    private String name;

    /**
     * Constructor
     * @param file : is transformed from a File object to an array of bytes
     */
    public TransportWrapper(File file) {
        this.name = file.getName();
        this.file = convertFile(file);
    }

    /**
     * constructor: same as above, but with an added integrity array byte
     * @param integrity : included so upon arrival of a file that we can validate th transfer was successful
     * @param file
     */
    public TransportWrapper(byte[] integrity, File file) {
        this.name = file.getName();
        this.integerty = integrity;
        this.file = convertFile(file);

    }

    /**
     * Converts a File object to an array of bytes
     * @param f
     * @return
     */
    private byte[] convertFile(File f){
        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * returns the integrity byte array that was passed with
     * @return
     */
    public byte[] getIntegrity() {
        return integerty;
    }

    /**
     * returns the file that was saved in the form of an array of bytes
     * @return
     */
    public byte[] getFile() {
        return file;
    }

    /**
     * returns the name of a file
     * @return
     */
    public String getName() {
        return name;
    }
}
