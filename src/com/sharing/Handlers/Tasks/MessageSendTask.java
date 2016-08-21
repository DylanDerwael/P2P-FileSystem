package com.sharing.Handlers.Tasks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Abstraction to send messages from one machine another provided the IP, PORT and the message
 */
public class MessageSendTask implements Runnable {

    private String ip;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private Object msg;
    private int port;


    public MessageSendTask(String ip, int port, Object msg) {
        this.ip = ip;
        this.port = port;
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
            connection = new Socket(ip, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            input = new ObjectInputStream(connection.getInputStream());
            output.flush();

            output.writeObject(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
