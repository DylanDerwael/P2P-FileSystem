package com.sharing;

/**
 * Created by dylan on 8/13/16.
 */
public interface Config {
    int NR_THREADS = 4;
    String HASH_ALGORITHM = "SHA-256";
    int ONE_MB = 1024 * 1024; // 1MB
    int HASH_BUFFER_SIZE = ONE_MB;
    int PARTIAL_FILE_SIZE = ONE_MB;

    String SERVER_IP = "127.0.0.1";
    String CLIENT_IP = "127.0.0.1";

    int SERVER_PORT = 7777;
    int SERVER_MAX_CONNECTIONS = 100;

    int PARTS_DUPLICATES = 2;

    String SECRET_PHRASE = "appelboom";
}
