package com.sharing.Handlers.File;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

import static com.sharing.Config.PARTIAL_FILE_SIZE;

/**
 * Created by dylan on 6/3/16.
 * reading a file will always run serial, can't optimize this with a parallel process
 */
public class Divider {

    /**
     * Splits one file in to multiple pieces
     *
     * @param input
     * @return Array list containing all files it created
     * @throws IOException
     */
    public static ArrayList<File> Split(File input, File dir) throws IOException {
        int partCounter = 1;

        byte[] buffer = new byte[PARTIAL_FILE_SIZE];
        ArrayList output = new ArrayList();

        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(input))) {
            String name = input.getName();

            int tmp;
            while ((tmp = bis.read(buffer)) > 0) {
                File newFile = new File(dir, name + "." + String.format("%03d", partCounter++));
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, tmp);//tmp is chunk size
                    output.add(out);
                }
            }
        }
        return output;
    }

    /**
     * Restores a file, by merging all it's parts.
     * @param files
     * @return The restored file
     * @throws IOException
     */
    public static File Merge(ArrayList<File> files, File dir) throws IOException {

        String tmpName = files.get(0).getName();
        String destFileName = tmpName.substring(0, tmpName.lastIndexOf('.'));
        File output = new File(dir.toPath() + "/" + destFileName);

        try (BufferedOutputStream mergingStream = new BufferedOutputStream(
                new FileOutputStream(output))) {
            for (File f : files) {
                Files.copy(f.toPath(), mergingStream);
            }
        }
        return output;
    }



}
