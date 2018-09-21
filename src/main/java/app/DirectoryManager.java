package app;

import javax.swing.*;
import java.io.File;

public class DirectoryManager {
    private File databaseStorage;
    private File userCreationsStorage;

    public DirectoryManager() {
        databaseStorage = new File(NameSayer.creationsPath);
        userCreationsStorage = new File(NameSayer.userRecordingsPath);
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
    }
}
