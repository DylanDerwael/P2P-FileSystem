package com.sharing.Handlers.Tasks;

import com.sharing.Handlers.File.Integrity;

import java.io.File;
import java.security.NoSuchAlgorithmException;

/**
 * Creates a hash byte[] of any given file
 */
public class HashLeafTask implements Runnable{

    private File file;
    private byte[] hash;
    private int key;

    public HashLeafTask(int key, File file) {
        this.key = key;
        this.file = file;
    }

    public byte[] getHash() {
        return hash;
    }

    public int getKey(){
        return key;
    }

    @Override
    public void run() {

        try {
            hash = Integrity.LeafHash(file);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
