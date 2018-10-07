/**
 * Name.java
 * Represents all files of a given name
 * <p>
 * Copyright Preet Patel, 2018
 *
 * @Author Preet Patel
 * @Auther Chuyang Chen
 * Date Created: 19 August, 2018
 */

package app;

import com.jfoenix.controls.JFXButton;
import com.sun.xml.internal.ws.util.StringUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

public class Name {

    private List<File> _files;
    private String _fullName;
    private String _name;
    private JFXButton _button;

    public Name(File file) {
        _name = getFileName(file);
        _files = getAllFilesOfName(new File(NameSayer.creationsPath));
    }

    public Name(String name) {
        _name = name;
        _files = getAllFilesOfName(new File(NameSayer.creationsPath));
    }

    /**
     * processes a file name to become human readable; so removes all of the dates from the wav file
     * @param file file to be processed
     * @return A string which is the simplified name
     */
    private String getFileName(File file) {
        String displayName = file.getName();
        _fullName = displayName;
        displayName = displayName.replaceAll("^[^_]*_[^_]*_[^_]*_", "");
        displayName = displayName.replaceAll("[.][^.]+$", "");
        return displayName;
    }

    /**
     * checks if the file provided for the name is a valid .wav file
     * @return true if the file is valid
     */
    public boolean isValid() {

        return FilenameUtils.getExtension(_fullName).equals("wav");
    }

    /**
     * creates a hashmap representing the different versions of a particular name, the key being their full file name
     * @return
     */
    public HashMap<String, File> getVersions() {
        HashMap<String, File> returnVersions = new HashMap<>();
        int i = 1;
        for (File file : _files) {
            returnVersions.put("Version " + i, file);
            i++;
        }
        return returnVersions;
    }

    /**
     *
     * @param dir directory in which the names are located
     * @return a list of all files which include the name object
     */
    public List<File> getAllFilesOfName(File dir) {
        FileFilter filter = new WildcardFileFilter("*_" + _name + ".wav");
        File[] files = dir.listFiles(filter);
        return Arrays.asList(files);

    }

    public String getName() {
        return _name;
    }


    /**
     * Creates a button for a given name
     * @param selectedButtonsList the list which allows selections of buttons to delete
     * @return The button which is created
     */
    public JFXButton generateButton(List<JFXButton> selectedButtonsList) {
        //create a new button to represent the item
        JFXButton button = new JFXButton();
        button.setMnemonicParsing(false);
        String name = StringUtils.capitalize(this.getName());
        button.setText(name);
        button.setId(this.getName());
        button.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 20;");

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!selectedButtonsList.contains(button)) {
                    button.setStyle("-fx-background-color: #256961; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 20;");
                    selectedButtonsList.add(button);
                } else {
                    button.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 20;");
                    selectedButtonsList.remove(button);
                }
            }
        });

        _button = button;
        return button;
    }

    public JFXButton getButton() {
        return _button;
    }
}
