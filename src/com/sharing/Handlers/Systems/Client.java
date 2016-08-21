package com.sharing.Handlers.Systems;

import com.sharing.Handlers.Exception.CryptoException;
import com.sharing.Handlers.File.*;
import com.sharing.Handlers.Messages.*;
import com.sharing.Handlers.Tasks.*;
import com.sharing.Handlers.User.User;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static com.sharing.Config.*;

/**
 * Created by dylan on 8/13/16.
 */
public class Client implements Runnable {


    private String name, pathVault, pathDwnl, pathTmp;
    private int port;
    public static ArrayList<User> onlineUsers;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private static User self;
    private ArrayList<LocationWrapper> bookkeeping; //Not very efficient, needs to go into SQLite kinda thing!
    private final Object lockUser = new Object();
    private final Object lockBookkeeping = new Object();
    private final Object lockRecieved = new Object();
    private int cntrRecieved = 1;

    public Client(String name, String port) {
        this.name = name;
        this.port = Integer.parseInt(port);
        this.pathDwnl =  System.getProperty("user.dir") + "/users/" + this.name + "/downloads";
        this.pathVault  = System.getProperty("user.dir") + "/users/" + this.name + "/vault";
        this.pathTmp  = System.getProperty("user.dir") + "/users/" + this.name + "/tmp";
        bookkeeping = new ArrayList<>();

        self = new User(this.name, this.port, CLIENT_IP);
        Notification("Connecting with server");
        CreatorTask.Start(new MessageSendTask(SERVER_IP, SERVER_PORT, new Online(self)));
        Notification("Checking local folders");
        CheckFolders();

        System.out.println("client " + name + " is running");

        CreatorTask.Start(new CliTask(self, this)); // Start cli listener
        this.run(); // start listening on port
    }


    /**
     * Notification function that displays a message in the command line
     * @param message
     */
    public void Notification(final String message) {
        System.out.println(message + "\n");
    }

    /**
     * Checks if the working folders are already present for a user and if not creates them
     */
    private void CheckFolders() {
        //TODO Checks if vault exists, if not creates the vault
        File fVault = new File(pathVault);
        File fDownloads = new File(pathDwnl);
        File fTmp = new File(pathTmp);
        if (!fVault.exists()) {
            fVault.mkdirs();
            Notification("Creation Vault");
        }
        if (!fDownloads.exists()) {
            fDownloads.mkdirs();
            Notification("Creation downloads");
        }
        if (!fTmp.exists()) {
            fTmp.mkdirs();
            Notification("Creation tmp");
        }
    }

    /**
     * Updates the online users and makes a notification about it
     * @param updateUsers
     */
    public void UpdateOnlineUsers(ArrayList<User> updateUsers) {
        synchronized (lockUser) {
            onlineUsers = updateUsers;
            Notification((onlineUsers.size() - 1) + " Users online");
        }
    }

    /**
     * Manage all the steps and respects the order of to spread a file properly in the network
     */
    public  void Put(String filePath){
        File in = new File (filePath);
        String baseName = in.getName();
        File out= new File (pathTmp + "/" + baseName);
        ArrayList<File> files;
        ArrayList<byte[]> hashList;

        try {
            Notification("Encrypting the file");
            Encryption.Encrypt(SECRET_PHRASE, in, out);
            Notification("Splitting the file into parts");
            Divider.Split(out, new File (pathTmp));
            Notification("remove complete file from temporary folder");
            out.delete();
            files  = PartialCollector.Collect(baseName, new File(pathTmp));
            Notification("Constructing hash list");
            hashList = IntegrityTask.FileIntegrity(files);
            Notification("Constructing Merkle integrity tree");
            byte[] merkleNode = IntegrityTask.MerkleNode(hashList);
            Notification("Spreading partials in network");
            SpreadPartials(baseName, files, hashList, merkleNode);
            Notification("Transfer complete cleaning up temp files");
            CreatorTask.Start(new CleanUpTempTask(files));
        } catch (CryptoException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Spreads the partials in the network and updates the book keeping
     */
    private void SpreadPartials (String baseName, ArrayList<File> files, ArrayList<byte[]> leafs, byte[] merkleNode) {

        ArrayList<PartWrapper> lstParts = new ArrayList<>();
        synchronized (lockUser) {
            int parts = files.size();
            int users = onlineUsers.size();
            int indxUsers = 0;
            int indxParts = 0;
            int cntr = 0;

            //lock this
            while (indxParts < parts) {

                User x = onlineUsers.get(indxUsers);
                if (cntr < PARTS_DUPLICATES) {
                    if (x.getPort() != self.getPort()) {
                        Notification("Sending " + files.get(indxParts).getName() + " to " + x.getName());
                        //Send Part to user
                        TransportWrapper wrap = new TransportWrapper(leafs.get(indxParts), files.get(indxParts));
                        CreatorTask.Start(new MessageSendTask(x.getIp(), x.getPort(), new Transfer(wrap)));
                        cntr++;
                        lstParts.add(new PartWrapper(files.get(indxParts).getName(), x));
                    }
                    indxUsers++;
                    indxUsers = (indxUsers < users) ? indxUsers : 0; // make the user list circular
                } else {
                    cntr = 0;
                    indxParts++;
                }
            }
        }
        synchronized (lockBookkeeping) {
            bookkeeping.add(new LocationWrapper(baseName, lstParts, merkleNode));
        }
    }

    /**
     * Public accessible function to save a file to the vault
     * @param msg
     */
    public void SaveToVault(Transfer msg) {
        //TODO : integerty check if transfer was good
        Save(pathVault + "/" + msg.getFileName(), msg.getFile());
    }

    /**
     * writes any given byte content to a file path
     * @param fPath
     * @param content
     */
    private void Save(String fPath, byte[] content) {
        try {
            Files.write(Paths.get(fPath), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Manages all the steps of the Get procedure
     */
    public void Get(String fileName) {
        LocationWrapper file = null;

        synchronized (lockBookkeeping) {
            for (LocationWrapper f : bookkeeping) {
                if (f.getFileName().equals(fileName)) {
                    file = f;
                    break;
                }
            }
        }

        int cntrRequests = 0;
        String lastPart = null;

        for(PartWrapper p : file.getParts()){
            String partName = p.getPartName();
            User u = p.getUser();
            //TODO: add is user online functionality
            if(!partName.equals(lastPart)){
                Notification("Requesting " + partName + " from " + p.getUser().getName());
                CreatorTask.Start(new MessageSendTask(u.getIp(), u.getPort(), new Request(partName, self)));
                lastPart = partName;
                cntrRequests++;
            }
        }

        while(cntrRecieved < (cntrRequests - 1) ){
            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        cntrRecieved = 1;

        try {
            Notification("merging the file");
            ArrayList<File> parts = PartialCollector.Collect(fileName, new File(pathTmp));

            ArrayList<byte[]> hashList = IntegrityTask.FileIntegrity(parts);
            byte[] merkleNode = IntegrityTask.MerkleNode(hashList);

            if (Arrays.equals(merkleNode, file.getMerkleNode())){
                Notification("file got retrieved correctly");
            } else {
                Notification("an error occurred while retrieving the file please try again");
            }

            File merged = Divider.Merge(parts, new File(pathDwnl));
            Notification("Transfer complete cleaning up temp files");
            CreatorTask.Start(new CleanUpTempTask(parts));
            Notification("Decrypting the file");
            Encryption.Decrypt(SECRET_PHRASE, merged, merged);
            Notification("File is ready for use");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handler for a file request, will send the file when found
     * TODO: Error handling if the file is not found, and or signal requester to ask a different user
     * @param r
     */
    private void GetRequest(Request r) {
        TransportWrapper wrap = new TransportWrapper(new File(pathVault + "/" + r.getFileName()));
        CreatorTask.Start(new MessageSendTask(r.getUser().getIp(), r.getUser().getPort(), new Return(wrap)));
    }

    /**
     * Handler for the Return call between two clients
     * @param msg
     */
    public void SaveToTemp(Return msg) {
        Notification("Received - " + msg.getFileName());
        Save(pathTmp + "/" + msg.getFileName(), msg.getFile());
    }

    /**
     * Displays the files stored within the network
     */
    public void DisplaySavedFiles() {
        Notification("Begin list files stored in network");
        for(LocationWrapper l : bookkeeping){
            Notification(l.getFileName());
        }
        Notification("End list files stored in network");
    }

    /**
     * Network listener
     */
    @Override
    public void run() {
        try {
            ServerSocket clientListener = new ServerSocket(port, SERVER_MAX_CONNECTIONS);
            Notification("Listening at " + clientListener.getInetAddress().toString() + " on port:" + Integer.toString(clientListener.getLocalPort()));
            while (true) {
                Socket connection = clientListener.accept();

                output = new ObjectOutputStream(connection.getOutputStream());
                output.flush();
                input = new ObjectInputStream(connection.getInputStream());

                Object x = input.readObject();

                //This will make a distinction of what to do given a certain object (@see #Handlers.Messages.*)
                if (x.getClass() == Transfer.class) {
                    Notification("recieved " + ((Transfer) x).getFileName());
                    SaveToVault(((Transfer) x));
                } else if (x.getClass() == Request.class) {
                    GetRequest(((Request)x));
                } else if (x.getClass() == Return.class) {
                    synchronized (lockRecieved) {
                        cntrRecieved++;
                    }
                    SaveToTemp(((Return) x));
                } else if (x.getClass() == Update.class) {
                    //Client.onlineUser = ((Update) x).getUser();
                    UpdateOnlineUsers(((Update)x).getUser());
                    Notification("Updated amount of users ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
