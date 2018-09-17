package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.swing.*;
import java.io.*;

public class SearchNamesViewController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXMasonryPane creationsPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private JFXTextField searchField;

    @FXML
    private JFXButton addButton;

    ObservableList<JFXButton> creationsList = FXCollections.<JFXButton>observableArrayList();

    @FXML
    /**
     * Allows adding creations to the list of creations to be played
     */
    private void addButtonHandler(ActionEvent e) {
        creationsPane.getChildren().clear();
        stackPane.setVisible(false);

        String searchedItem = searchField.getText();

        File storage = new File(NameSayer.creationsPath);
        if (!storage.exists()) {
            if (!storage.mkdirs()) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load creations ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        Process process;

        try {
            String command = "ls " + NameSayer.creationsPath + "/ -1  | sed -e 's/\\..*$//' | grep -i " + searchedItem;
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
            process = builder.start();
            process.waitFor();

            InputStream stdout = process.getInputStream();
            BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));

            String line = stdoutBuffered.readLine();

            if (line != null ){

                JFXButton button = new JFXButton();
                button.setMnemonicParsing(false);
                button.setText(line);
                button.setId(line);
                button.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");

                //button.setOnAction(this::existingCreationButtonHandler); //make it so when clicked it is removed
                creationsList.add(button);

            } else {
                System.out.println("Name Not Found");
            }

            creationsPane.getChildren().addAll(creationsList);

        } catch (IOException e2) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException e3) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e3.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }


    }

    /**
     * Method that calls nothing.
     * This method exists due to being required by JavaFX
     */
    @FXML
    private void initialize() {
        stackPane.setVisible(false);
    }
}
