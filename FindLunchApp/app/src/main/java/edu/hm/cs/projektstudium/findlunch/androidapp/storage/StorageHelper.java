package edu.hm.cs.projektstudium.findlunch.androidapp.storage;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * The type Storage helper
 * provides methods that
 * allow to check for availability
 * of an external storage
 * and to read files from it.
 */
public class StorageHelper {

    /**
     * The constant SEPARATION_CHARACTER.
     */
    private static final String SEPARATION_CHARACTER = "=";

    /**
     * Instantiates a new Storage helper.
     */
    public StorageHelper() {
    }

    /**
     * Method that returns <code>true</code>
     * if the external storage is available to
     * at least read.
     *
     * @return <code>true</code>
     * if the external storage is available to
     * at least read.
     */
    private boolean isExternalStorageReadable() {
        // state of the external storage
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    /**
     * Method that reads key-value-pairs
     * from a file that are separated by a "=".
     * It returns a list of key-value-pairs.
     *
     * @param fileName the file to read from
     * @return the content of the file
     */
    public Map<String, String> readFile(@SuppressWarnings("SameParameterValue") String fileName) {
        // the content read from the file
        Map<String, String> fileContent = new HashMap<>();

        if(isExternalStorageReadable()) {
            // full path of the file to read (on the external storage)
            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName);

            if (f.exists()) {
                if(f.canRead()) {
                    try {
                        // file input stream to read from the file with
                        FileInputStream inputStream = new FileInputStream(f);
                        // buffer reader to read from the file with
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(inputStream));
                        // current row
                        String row;
                        // splitted parts of the current row
                        String[] splittedRow;
                        while ((row = reader.readLine()) != null) {
                            splittedRow = row.split(SEPARATION_CHARACTER);
                            if(splittedRow.length == 2) {
                                for(int i = 0; i < splittedRow.length; i++) {
                                    if(splittedRow[i] != null) {
                                        splittedRow[i] = splittedRow[i].trim();
                                    }
                                }
                                fileContent.put(splittedRow[0], splittedRow[1]);
                            }
                        }

                        reader.close();
                    } catch (IOException e) {
                        Log.e(getClass().getName(), e.getMessage());
                    }
                } else {
                    Log.v(getClass().getName(), fileName + " can not be read!");
                }
            } else {
                Log.v(getClass().getName(), fileName + " does not exist!");
            }
        }
        return fileContent;
    }
}
