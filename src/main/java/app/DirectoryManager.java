/**
 * DirectoryManager.java
 * Processes directories and files necessary for the program to run properly
 * <p>
 * Copyright Preet Patel, 2018
 *
 * @Author Preet Patel
 * @Author Chuyang Chen
 * Date Created: 19 August, 2018
 */

package app;

import javax.swing.*;
import java.io.File;

public class DirectoryManager {
    private File databaseStorage;
    private File userCreationsStorage;
    private static File ratings;
    private File concatenatedNamesStorage;
    private File concatenatedTempStorage;
    private static File score;
    private static File users;

    public DirectoryManager() {
        databaseStorage = new File(NameSayer.creationsPath);
        userCreationsStorage = new File(NameSayer.userRecordingsPath);
        concatenatedNamesStorage = new File(NameSayer.concatenatedNamesPath);
        concatenatedTempStorage = new File(NameSayer.concatenationTempPath);
        ratings = new File(NameSayer.directoryPath + "/ratings.txt");
        score = new File(NameSayer.directoryPath + "/score.txt");
        users = new File(NameSayer.directoryPath + "/users.txt");
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

        checkForDirectory(userCreationsStorage);

        //these concatenated names storage must be in this order
        checkForDirectory(concatenatedNamesStorage);
        checkForDirectory(concatenatedTempStorage);

        checkForTextFile(ratings);
        checkForTextFile(score);
        checkForTextFile(users);

    }

    public static File getRatings() {
        return ratings;
    }

    public static File getScore() {
        return score;
    }

    /**
     * checks whether the input text file exists, and creates it if it doesnt
     * @param file
     */
    private void checkForTextFile(File file){
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load score file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /**
     * checks whether the input directory exists, and creates it if it doesnt
     * @param file
     */
    private void checkForDirectory(File file){
        if (!file.exists()) {
            if (!file.mkdirs()) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load creations ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
