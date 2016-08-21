package com.sharing.Handlers.File;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dylan on 6/14/16.
 */
public class PartialCollector {
    /**
     * collects all parts given the base of the name and the directory
     * @param baseName
     * @param parrentDir
     * @return
     */
    public static ArrayList<File> Collect (String baseName, File parrentDir) {
        File[] partials = parrentDir.listFiles((File dir, String name) -> name.matches(baseName + "[.]\\d+"));
        Arrays.sort(partials);
        return new ArrayList<>(Arrays.asList(partials));
    }
}
