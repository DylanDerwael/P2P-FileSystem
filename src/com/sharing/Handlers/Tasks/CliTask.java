package com.sharing.Handlers.Tasks;


import com.sharing.Handlers.Messages.Offline;
import com.sharing.Handlers.Systems.Client;
import com.sharing.Handlers.User.User;

import java.util.Scanner;

import static com.sharing.Config.SERVER_IP;
import static com.sharing.Config.SERVER_PORT;


/**
 * Creates dedicated task to listening for any commands given in the command line
 */
public class CliTask implements Runnable {

    private User me;
    private Client self;

    public CliTask(User me, Client self) {
        this.me = me;
        this.self = self;
    }

    @Override
    public void run() {
        self.Notification("Cli listener on");
        Scanner readLoop = new Scanner(System.in);
        String cmd;
        boolean cont = true;
        while (cont) {
            cmd = readLoop.nextLine();
            String[] args = cmd.split("\\s+");
            if (args[0].toLowerCase().equals("put")) {
                self.Notification("looking for " + args[1]);
                self.Put(args[1]);
            } else if (args[0].toLowerCase().equals("get")) {
                self.Notification("looking for " + args[1]);
                self.Get(args[1]);
            } else if (args[0].toLowerCase().equals("list")) {
                self.Notification("retrieving list");
                self.DisplaySavedFiles();
            } else if (args[0].toLowerCase().equals("exit")) {
                CreatorTask.Start(new MessageSendTask(SERVER_IP, SERVER_PORT, new Offline(me)));
                self.Notification("Logout");
                cont = false;
            } else {
                self.Notification("Unknown command");
            }
        }
    }
}
