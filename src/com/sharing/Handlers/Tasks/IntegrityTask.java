package com.sharing.Handlers.Tasks;

import java.io.File;
import java.util.ArrayList;

/**
 * Handler for all that involves the integrity of a file, or a tree for a collection of files
 */
public class IntegrityTask {


    /**
     * calculates the hashlist provided of the list of files (also leafs of merkle tree)
     * @param in
     * @return
     */
    public static ArrayList<byte[]> FileIntegrity(ArrayList<File> in){
        ArrayList<byte[]> hashedLeafs = new ArrayList<>(in.size());
        ArrayList<HashLeafTask> tasks = new ArrayList<>(in.size());

        //Populates the task to do
        for(int i= 0; i < in.size(); i++){
            tasks.add(new HashLeafTask(i, in.get(i)));
        }

        //executes the tasks to do
        tasks.parallelStream().forEach(t -> t.run());

        //retrieves the out come of all the tasks and populates the result array
        for (HashLeafTask task: tasks) {
            hashedLeafs.add(task.getHash());
        }

        return hashedLeafs;
    }

    /**
     * calculates the merkle root node
     * @param leafs
     * @return
     */
    public static byte[] MerkleNode(ArrayList<byte[]> leafs){

        ArrayList<byte[]> branches = new ArrayList<>(leafs); // copy constructor makes treadsafe
        ArrayList<byte[]> newBranches;
        ArrayList<HashBranchTask> tasks;
        byte[] save;

        while(branches.size() > 1){
            if(branches.size() % 2 == 0){
                newBranches = new ArrayList<>(branches.size()/2);
                tasks = new ArrayList<>(branches.size()/2);
                save = null;
            } else {
                newBranches = new ArrayList<>((branches.size() - 1) /2);
                tasks = new ArrayList<>((branches.size() - 1) /2);
                save = branches.get(branches.size() -1 );
                branches.remove(branches.size()-1);
            }

            //Populates the task to do
            for(int i= 0; i < branches.size(); i += 2){
                tasks.add(new HashBranchTask(branches.get(i), branches.get(i+1)));
            }

            //executes the tasks to do
            tasks.parallelStream().forEach(t -> t.run());

            //retrieves the out come of all the tasks and populates the result array
            for (HashBranchTask task: tasks) {
                newBranches.add(task.getHash());
            }

            branches = newBranches;
            if (save != null){
                branches.add(save);
            }

        }

        return branches.get(0);
    }
}
