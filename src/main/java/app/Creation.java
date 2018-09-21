package app;


import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Creation {

    private JFXButton button;

    private List<Name> names;

    public Creation() {
        names = new ArrayList<>();
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
            if (displayName.equals("")) {
                displayName = name.getName();
            } else {
                displayName = displayName + " " + name.getName();
            }
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

        // each name is a 'layer'
        // each layer must have every element in that layer link to every element in the next layer
        // this goes on until every layer has been explored


        int versionsNumber = 1;

        for (int j = 0; j < names.size(); j++){
            versionsNumber = versionsNumber*names.get(j).getNameVersionsNumber();

        }

        List<CreationFile> versionCreationFileList = new ArrayList<>();
        List<String> versionNameList = new ArrayList<>();

        //create the version names and the version objects
        for (int j = 0; j < versionsNumber; j++){
            String versionName = getCreationName()+ " V" + Integer.toString(j);
            versionNameList.add(versionName);

            CreationFile creationFile = new CreationFile(versionName);

            versionCreationFileList.add(creationFile);
        }

        for (int i = 0; i < names.size()-1; i++){



            List<File> upperLayer = names.get(i).getFiles();
            List<File> lowerLayer = names.get(i+1).getFiles();

            int creationVersionNumber = 0;



            //TODO fuse the files here
        }

        return null;
    }

    private void permutate(){

    }


}
