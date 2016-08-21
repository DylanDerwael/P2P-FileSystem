package com.sharing.Handlers.Tasks;

import java.io.File;
import java.util.ArrayList;

/**
 * This is mainly to clean the temp file of all partials that don't need to be there any more.
 * Works but due to the concurrency this is deleting the files before all duplicates have been send to all other users
 */
public class CleanUpTempTask implements Runnable {

    private ArrayList<File> files;

    public CleanUpTempTask(ArrayList<File> files) {
        this.files = files;
    }

    @Override
    public void run() {
        for(File f : files){
            f.delete();
        }
    }
}
