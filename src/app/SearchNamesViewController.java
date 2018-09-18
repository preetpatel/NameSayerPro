package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SearchNamesViewController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXMasonryPane creationsPane;

    @FXML
    private JFXMasonryPane addedCreationsPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ScrollPane addedScrollPane;

    @FXML
    private JFXTextField searchField;

    @FXML
    private JFXButton addButton;

    ObservableList<JFXButton> creationsButtonList = FXCollections.<JFXButton>observableArrayList();

    @FXML
    /**
     * Allows adding creations to the list of creations to be played
     */
    private void addButtonHandler(ActionEvent e) {
        creationsPane.getChildren().clear();
        stackPane.setVisible(false);

        String searchedItems = searchField.getText();
        String[] searchedItemsArray = searchedItems.split(" ");

        File storage = new File(NameSayer.creationsPath);
        if (!storage.exists()) {
            if (!storage.mkdirs()) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load creations ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        Process process;

        //search for the item to put into list
        try {
            for (String currentSearchedItem : searchedItemsArray) {
                String command = "ls " + NameSayer.creationsPath + "/ -1  | sed -e 's/\\..*$//' | grep -iow \"" + currentSearchedItem + "\"";

                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
                process = builder.start();
                process.waitFor();

                InputStream stdout = process.getInputStream();
                BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));

                String line = stdoutBuffered.readLine();

                //create a new button to represent the item
                if (line != null) {

                    JFXButton button = new JFXButton();
                    button.setMnemonicParsing(false);
                    button.setText(line);
                    button.setId(line);
                    button.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");

                    boolean buttonExists = false;

                    //see if that item has already been added to the list
                    for (JFXButton currentButton : creationsButtonList) {
                        if (line.equals(currentButton.getId())) {
                            buttonExists = true;
                        }
                    }

                    if (!buttonExists) {
                        creationsButtonList.add(button);
                    } else {
                        System.out.println("That has already been added");
                    }

                } else {
                    //if no name is found
                    System.out.println(currentSearchedItem + "Name Not Found");
                }

            }

            creationsPane.getChildren().addAll(creationsButtonList);

        } catch (IOException e2) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException e3) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e3.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }


    }

    /**
     * Method that makes stack pane invisible on startup.
     * This method exists due to being required by JavaFX
     */
    @FXML
    private void initialize() {
        stackPane.setVisible(false);
    }
}
