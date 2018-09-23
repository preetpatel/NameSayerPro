/**
 * DirectoryManager.java
 * Processes directories and files necessary for the program to run properly
 * <p>
 * Copyright Preet Patel, 2018
 *
 * @Author Preet Patel
 * @Auther Chuyang Chen
 * Date Created: 19 August, 2018
 */

package app;

import javax.swing.*;
import java.io.File;

public class DirectoryManager {
    private File databaseStorage;
    private File userCreationsStorage;
    private static File ratings;

    public DirectoryManager() {
        databaseStorage = new File(NameSayer.creationsPath);
        userCreationsStorage = new File(NameSayer.userRecordingsPath);

        ratings = new File(NameSayer.directoryPath + "/ratings.txt");

    }

    /**
     * Checks that the directories and files which are to be used exist
     */
    public void runChecks() {
        if (!databaseStorage.exists()) {
            if (!databaseStorage.mkdirs()) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load creations ", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (databaseStorage.listFiles().length == 0) {
                    JOptionPane.showMessageDialog(null, "The database contains no files. Please add files as instructed via the README. NameSayer will not work correctly unless this is done.", "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
        }

        if (!userCreationsStorage.exists()) {
            if (!userCreationsStorage.mkdirs()) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load creations ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (!ratings.exists()) {
            try {
                ratings.createNewFile();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load ratings file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static File getRatings() {
        return ratings;
    }
}
