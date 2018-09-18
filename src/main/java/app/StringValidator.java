/**
 * StringValidator.java
 * Provides methods for a string such as if it is a valid format, has a file with that string name and allows
 * a file to be deleted
 *
 * Copyright Preet Patel, 2018
 * @Author Preet Patel
 * Date Created: 13 August, 2018
 */

package app;

import javax.swing.*;
import java.io.*;

public class StringValidator {

    private String fileName;

    public StringValidator(String filename) {
        fileName = filename;
    }

    /**
     * Checks for if the string is valid for being a file name. Accepted characters include:
     * A-Z, a-z, (Spaces), hyphens
     * @return Returns true if the string is valid
     */
    public boolean isValid() {
        boolean invalidName = fileName.contains("_audio") || fileName.contains("_video");
        boolean validInput = fileName.matches("^[\\w\\- ]+$");
        boolean empty = fileName.isEmpty();
        boolean fileExists = checkFileExists();
        if (!validInput || empty || fileExists || invalidName) {
            return false;
        }else {
            return true;
        }
    }

    /**
     * Checks if a file with the filename exists. The files are checked in the
     * creations path provided in the NameSayer.java class.
     * @return true if file exists; otherwise false.
     */
    public boolean checkFileExists() {
            File file = new File(NameSayer.creationsPath);
            for (String check : file.list()) {
                if((fileName + ".mp4").equalsIgnoreCase(check)) {
                    return true;
                }
            }
            return false;
    }

    /**
     * Deletes the file from the creations path via a bash command
     * Opens a dialog box if an error occurs during the deletion process
     */
    public void deleteFile() {
        if (checkFileExists()) {
            try {
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "find " + NameSayer.creationsPath + " -maxdepth 1 -iname \"" + fileName + ".mp4" + "\" -exec rm {} \\; ");
                builder.start();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
