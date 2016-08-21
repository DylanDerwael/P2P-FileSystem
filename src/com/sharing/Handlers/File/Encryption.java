package com.sharing.Handlers.File;


import com.sharing.Handlers.Exception.CryptoException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.sharing.Config.HASH_ALGORITHM;


/**
 * Utility class for encrypting/decrypting files.
 * http://www.codejava.net/coding/file-encryption-and-decryption-simple-example
 */
public class Encryption {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * will start the encrypting of a file
     *
     * @param key
     * @param inputFile
     * @param outputFile
     * @throws CryptoException
     */
    public static void Encrypt(String key, File inputFile, File outputFile) throws CryptoException {
        DoCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    /**
     * Will start the decrypting of a file
     *
     * @param key
     * @param inputFile
     * @param outputFile
     * @throws CryptoException
     */
    public static void Decrypt(String key, File inputFile, File outputFile) throws CryptoException {
        DoCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    /**
     * Executes the crypting or decrypting algorthim on a given file and will write it to the hard disk
     *
     * @param cipherMode
     * @param key
     * @param inputFile
     * @param outputFile
     * @throws CryptoException
     */
    private static void DoCrypto(int cipherMode, String key, File inputFile, File outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(buildKey(key.toCharArray()), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }

    /**
     * Builds a key phrase of the correct size for the encryption
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static byte[] buildKey(char[] password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digester = MessageDigest.getInstance(HASH_ALGORITHM);
        digester.update(String.valueOf(password).getBytes("UTF-8"));
        return digester.digest();
    }
}
