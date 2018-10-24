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
    private File _databaseStorage;
    private File _userCreationsStorage;
    private static File _ratings;
    private File _concatenatedNamesStorage;
    private File _concatenatedTempStorage;
    private static File _score;
    private static File _users;

    public DirectoryManager() {
        _databaseStorage = new File(NameSayer.audioPath);
        _userCreationsStorage = new File(NameSayer.userRecordingsPath);
        _concatenatedNamesStorage = new File(NameSayer.concatenatedNamesPath);
        _concatenatedTempStorage = new File(NameSayer.concatenationTempPath);
        _ratings = new File(NameSayer.directoryPath + "/_ratings.txt");
        _score = new File(NameSayer.directoryPath + "/_score.txt");
        _users = new File(NameSayer.directoryPath + "/_users.txt");
    }

    /**
     * Checks that the directories and files which are to be used exist
     */
    public void runChecks() {

        if (!_databaseStorage.exists()) {
            if (!_databaseStorage.mkdirs()) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load creations ", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (_databaseStorage.listFiles().length == 0) {
                    JOptionPane.showMessageDialog(null, "The database contains no files. Please add files as instructed via the README. NameSayer will not work correctly unless this is done.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        checkForDirectory(_userCreationsStorage);

        //these concatenated names storage must be in this order
        checkForDirectory(_concatenatedNamesStorage);
        checkForDirectory(_concatenatedTempStorage);

        checkForTextFile(_ratings);
        checkForTextFile(_score);
        checkForTextFile(_users);

    }

    public static File get_ratings() {
        return _ratings;
    }

    public static File get_score() {
        return _score;
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
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load _score file", "Error", JOptionPane.ERROR_MESSAGE);
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
