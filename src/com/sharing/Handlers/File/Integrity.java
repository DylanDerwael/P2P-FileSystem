package com.sharing.Handlers.File;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.sharing.Config.HASH_ALGORITHM;
import static com.sharing.Config.HASH_BUFFER_SIZE;

/**
 * Created by dylan on 6/3/16.
 */
public class Integrity {

    /**
     * Produces a Hash key of a branch node
     *
     * @param left
     * @param right
     * @return
     */
    public static byte[] BranchHash(byte[] left, byte[] right) throws NoSuchAlgorithmException {
        MessageDigest md = null;

        md = MessageDigest.getInstance(HASH_ALGORITHM);
        md.update(left);
        md.update(right);

        return md.digest();
    }

    /**
     * produces a hash key of a leaf node
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] LeafHash(File data) throws NoSuchAlgorithmException {

        InputStream fis;
        MessageDigest complete = MessageDigest.getInstance(HASH_ALGORITHM);
        try {

            int numRead;

            byte[] buffer = new byte[HASH_BUFFER_SIZE];
            fis = new FileInputStream(data);

            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
            fis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return complete.digest();
    }


    /**
     * Compares if a file is uncorrupted by producing a hash and compare it to the provided hash
     * @param toCheck
     * @param validation
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static boolean IsCorrupt(File toCheck, byte[] validation) throws NoSuchAlgorithmException {
        return (Arrays.equals(LeafHash(toCheck), validation)) ? true : false;
    }


}
