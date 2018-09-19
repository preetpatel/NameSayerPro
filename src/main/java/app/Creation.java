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

    private List<File> files;

    public Creation() {
        files = new ArrayList<>();
    }


    public void addName(File file) {
        files.add(file);
    }

    public String getCreationName() {
        if (files.isEmpty()) {
            return null;
        }

        List<String> names = new ArrayList<>();

        for (File file : files) {
            String displayName = file.getName();
            displayName = displayName.replaceAll("^[^_]*_[^_]*_[^_]*_", "");
            displayName = displayName.replaceAll("[.][^.]+$", "");
            names.add(displayName);

        }

        return String.join(" ", names);
    }

    public void setButton(JFXButton button) {
        this.button = button;
    }

    public JFXButton getButton() {
        return button;
    }

    public boolean isValid() {
        for (File file : files) {
            if (!FilenameUtils.getExtension(file.getName()).equals("wav")) {
                return false;
            }
        }
        return true;
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
        files.clear();
    }

}
