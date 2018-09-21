package app;


import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Creation {

    private JFXButton button;

    private List<Name> names;

    private List<CreationFile> creationFiles;

    public Creation() {
        names = new ArrayList<>();
        creationFiles = new ArrayList<>();
    }


    public void addName(File file) {
        names.add(new Name(file));
    }


    public String getCreationName() {
        if (names.isEmpty()) {
            return null;
        }
        String displayName = "";
        for (Name name : names) {
            displayName = displayName + " "+ name.getName();
        }

        return displayName;
    }

    public void setButton(JFXButton button) {
        this.button = button;
    }

    public JFXButton getButton() {
        return button;
    }

    /**
     * generates a button for the specific creation
     */
    public JFXButton generateButton(List<JFXButton> selectedButtonsList){
        //create a new button to represent the item
        JFXButton button = new JFXButton();
        button.setMnemonicParsing(false);
        button.setText(this.getCreationName());
        button.setId(this.getCreationName());
        button.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!selectedButtonsList.contains(button)) {
                    button.setStyle("-fx-background-color: #256961; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
                    selectedButtonsList.add(button);
                } else {
                    button.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
                    selectedButtonsList.remove(button);
                }
            }
        });

        this.button = button;
        return button;
    }

    public void destroy() {
        names.clear();
    }

    public String[] getPermutations(){

        int numberOfVersions = 1;

        for (Name currentName : names){
            numberOfVersions = numberOfVersions*currentName.getNameVersionsNumber();
        }

        String[] versionStrings = new String[numberOfVersions];

        //set the whole array to empty strings
        for (int k = 0; k < versionStrings.length; k++) {
            versionStrings[k] = "";
        }

        //go through all the names and create an array to match the number of permutations of all names
        for (int j =0; j < names.size(); j++) {

            for (int i = 0; i < numberOfVersions; i++) {

                if (!versionStrings[i].equals("")) {
                    versionStrings[i] = versionStrings[i] + " " + names.get(j).getName();
                } else {
                    versionStrings[i] = versionStrings[i] + names.get(j).getName();
                }


            }
        }

        for (Name name : names){
            //TODO fuse the files here


        }

        for (int l = 0; l < versionStrings.length; l++) {
            versionStrings[l] = versionStrings[l] + " V" + Integer.toString(l+1);

        }
        //TODO fuse the files here

        return versionStrings;
    }

}
