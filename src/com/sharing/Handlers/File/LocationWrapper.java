package com.sharing.Handlers.File;

import java.util.ArrayList;

/**
 * Created by dylan on 8/16/16.
 */
public class LocationWrapper {
    private String fileName;
    private ArrayList<PartWrapper> parts;
    private byte[] merkleNode;

    public LocationWrapper(String fileName, ArrayList<PartWrapper> parts, byte[] merkleNode) {
        this.fileName = fileName;
        this.parts = parts;
        this.merkleNode = merkleNode;
    }

    public ArrayList<PartWrapper> getParts() {
        return parts;
    }

    public void setParts(ArrayList<PartWrapper> parts) {
        this.parts = parts;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getMerkleNode() {
        return merkleNode;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
