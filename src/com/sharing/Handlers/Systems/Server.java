package com.sharing.Handlers.Systems;


import com.sharing.Handlers.Messages.Offline;
import com.sharing.Handlers.Messages.Online;
import com.sharing.Handlers.Messages.Update;
import com.sharing.Handlers.Tasks.CreatorTask;
import com.sharing.Handlers.Tasks.MessageSendTask;
import com.sharing.Handlers.User.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import static com.sharing.Config.SERVER_MAX_CONNECTIONS;
import static com.sharing.Config.SERVER_PORT;


/**
 * Created by dylan on 8/13/16.
 */
public class Server implements Runnable {

    private ArrayList<User> users;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private final Object lock = new Object();

    public Server() {
        users = new ArrayList<>();
        run();
    }

    /**
     * Add's a user to the online list nd update all the users of the new amount of users online
     * @param user
     */
    public void AddUser(User user) {
        synchronized (lock) {
            System.out.println("User " + user.getName() + " online" + "\n");
            users.add(user);
            NotifyUsers();
        }
    }

    /**
     * the function that will notify all the users by looping through the list of online users
     */
    public  void NotifyUsers() {
        ArrayList<User> updates = this.users;

        for (User u : updates) {
            CreatorTask.Start(new MessageSendTask(u.getIp(), u.getPort(), new Update(updates)));
        }
    }

    /**
     * Removes a user from the online list and update all the users of the new amount of users online
     * @param user
     */
    public void RemoveUser(User user) {
        synchronized (lock) {
            Iterator<User> it = users.iterator();
            while (it.hasNext()) {
                User u = it.next();
                if (user.getName().equals(u.getName())) {
                    it.remove();
                }
            }
            NotifyUsers();
        }
    }

    /**
     * Network listener
     */
    @Override
    public void run() {

        try {
            ServerSocket server = new ServerSocket(SERVER_PORT, SERVER_MAX_CONNECTIONS);
            System.out.println("Server is running");
            while (true) {
                //waits for a connection
                this.connection = server.accept();

                try {
                    output = new ObjectOutputStream(connection.getOutputStream());
                    output.flush();
                    input = new ObjectInputStream(connection.getInputStream());

                    Object x = input.readObject();

                    //This will make a distinction of what to do given a certain object (see Handlers.Messages.*)
                    if (x.getClass() == Online.class) {
                        AddUser(((Online) x).getUser());
                    } else if (x.getClass() == Offline.class) {
                        RemoveUser(((Offline) x).getUser());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

