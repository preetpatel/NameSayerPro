package app;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

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

    private ObservableList<JFXButton> creationsButtonList = FXCollections.<JFXButton>observableArrayList();

    @FXML
    /**
     * Allows adding creations to the list of creations to be played
     */
    private void addButtonHandler(ActionEvent e) {
        creationsPane.getChildren().clear();
        stackPane.setVisible(false);

        String searchedItems = searchField.getText().trim();
        if (searchedItems.equals("")) {

            showErrorDialog("Please enter a valid name", "Ok");

        } else {

            String[] searchedItemsArray = searchedItems.split(" ");
            Creation creation = new Creation();

            for (String currentSearchedItem : searchedItemsArray) {
                File folder = new File(NameSayer.creationsPath);
                File[] files = folder.listFiles();
                boolean fileFound = false;

                for (File file : files) {

                    fileFound = false;
                    Creation tempName = new Creation();
                    tempName.addName(file);

                    if (currentSearchedItem.toLowerCase().equals(tempName.getCreationName().toLowerCase())) {
                        creation.addName(file);
                        fileFound = true;
                        break;
                    }
                }

                if (!fileFound) {

                    // If no name is found
                    showErrorDialog("This name could not be found on the database", "Ok");
                    creation.destroy();
                    break;
                }
            }

            if (creation.getCreationName() != null) {

                //create a new button to represent the item
                JFXButton button = new JFXButton();
                button.setMnemonicParsing(false);
                button.setText(creation.getCreationName());
                button.setId(creation.getCreationName());
                button.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");

                boolean buttonExists = false;

                //see if that item has already been added to the list
                for (JFXButton currentButton : creationsButtonList) {
                    if (creation.getCreationName().toLowerCase().equals(currentButton.getId().toLowerCase())) {
                        buttonExists = true;
                    }
                }

                if (!buttonExists) {
                    creationsButtonList.add(button);
                } else {

                    showErrorDialog("This name has already been added", "Ok");
                }
            }

            creationsPane.getChildren().addAll(creationsButtonList);

        }

    }

    /**
     * Method that makes stack pane invisible on startup.
     * This method exists due to being required by JavaFX
     */
    @FXML
    private void initialize() {

        // Sets scroll pane to match the style of the app by disabling visible scroll bars
        stackPane.setVisible(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #023436; -fx-background: #023436");
        scrollPane.addEventFilter(ScrollEvent.SCROLL,new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaX() != 0) {
                    event.consume();
                }
            }
        });

        // Checks for if the database folder exists or not
        File storage = new File(NameSayer.creationsPath);
        if (!storage.exists()) {
            if (!storage.mkdirs()) {
                JOptionPane.showMessageDialog(null, "An Error occurred while trying to load creations ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void showErrorDialog(String headerText, String buttonText) {
        stackPane.setVisible(true);
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        JFXDialog deleteDialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

        Text header = new Text(headerText);
        header.setStyle("-fx-font-size: 30; -fx-font-family: 'Lato Heavy'");
        dialogContent.setHeading(header);

        JFXButton confirmDelete = new JFXButton();
        confirmDelete.setText(buttonText);
        confirmDelete.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
        confirmDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteDialog.close();
                stackPane.setVisible(false);

            }
        });

        deleteDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
            @Override
            public void handle(JFXDialogEvent event) {
                stackPane.setVisible(false);
            }
        });

        dialogContent.setActions(confirmDelete);
        deleteDialog.show();
    }
}
