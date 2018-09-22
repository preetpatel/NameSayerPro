package app;

import javax.swing.*;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectoryManager {
    private File databaseStorage;
    private File userCreationsStorage;
    private static File ratings;

    public DirectoryManager() {
        databaseStorage = new File(NameSayer.creationsPath);
        userCreationsStorage = new File(NameSayer.userRecordingsPath);

        ratings = new File(NameSayer.directoryPath +"/ratings.txt");

    }
    public void runChecks() {
        if (!databaseStorage.exists()) {
            if (!databaseStorage.mkdirs()) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load creations ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (!userCreationsStorage.exists()) {
            if (!userCreationsStorage.mkdirs()) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load creations ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (!ratings.exists()){
            try {
                ratings.createNewFile();
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load ratings file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static File getRatings(){
        return ratings;
    }
}
