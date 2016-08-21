package com.sharing.Handlers.Tasks;

import com.sharing.Handlers.File.Integrity;

import java.security.NoSuchAlgorithmException;

/**
 * Creates a hash byte[] of any given file
 */
public class HashBranchTask implements Runnable{

    private byte[] lBranch;
    private byte[] rBranch;
    private byte[] hash;

    public HashBranchTask( byte[] lBranch, byte[] rBranch) {
        this.rBranch = rBranch;
        this.lBranch = lBranch;
    }

    public byte[] getHash() {
        return hash;
    }


    @Override
    public void run() {
        try {
            hash = Integrity.BranchHash(lBranch, rBranch);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
