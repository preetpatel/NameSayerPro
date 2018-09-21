package app;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.io.*;
import java.util.*;

import static javafx.scene.layout.StackPane.getAlignment;
import static javafx.scene.layout.StackPane.setAlignment;

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

    @FXML
    private JFXButton startPracticeButton;

    @FXML
    private JFXButton removeButton;

    private ObservableList<JFXButton> creationsButtonList = FXCollections.<JFXButton>observableArrayList();
    private List<JFXButton> selectedButtonsList = new ArrayList<>();

    private List<Name> creationsList = new ArrayList<>();

    /**
     * Method that makes stack pane invisible on startup to prevent conflicting with the GUI.
     * Initialises properties of the scroll view
     */
    @FXML
    private void initialize() {
        selectedButtonsList = new ArrayList<>();
        creationsList = new ArrayList<>();
        startPracticeButton.setVisible(false);
        removeButton.setVisible(false);

        // Add Enter key listener on search field
        searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    addButtonHandler(new ActionEvent());
                }
            }
        });


        // Sets scroll pane to match the style of the app by disabling visible scroll bars
        stackPane.setVisible(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #023436; -fx-background: #023436");
        scrollPane.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaX() != 0) {
                    event.consume();
                }
            }
        });

        // Checks for all required directories
        DirectoryManager manager = new DirectoryManager();
        manager.runChecks();

    }

    @FXML
    private void removeButtonHandler(ActionEvent e) {


        for (JFXButton button : selectedButtonsList) {
            creationsButtonList.remove(button);

            Iterator<Name> creations = creationsList.iterator();
            while (creations.hasNext()) {

                JFXButton comparedButton = creations.next().getButton();

                if (button.equals(comparedButton)) {
                    creations.remove();
                    creationsButtonList.remove(comparedButton);
                    break;
                }
            }
        }

        creationsPane.getChildren().clear();
        creationsPane.getChildren().addAll(creationsButtonList);

        // Set remove button to invisible if list has no creations left
        if (creationsList.isEmpty()) {
            removeButton.setVisible(false);
            startPracticeButton.setVisible(false);
        }
    }


    @FXML
    /**
     * Allows adding creations to the list of creations to be played
     */
    private void addButtonHandler(ActionEvent e) {
        creationsPane.getChildren().clear();
        stackPane.setVisible(false);
        stackPane.getChildren().clear();
        searchField.setDisable(true);

        String searchedItems = searchField.getText().trim();
        if (searchedItems.equals("")) {

            showErrorDialog("Please enter a valid name", "Ok");

        } else {

            Name creation;

            File folder = new File(NameSayer.creationsPath);
            File[] files = folder.listFiles();
            boolean fileFound = false;

            for (File file : files) {

                fileFound = false;
                Name tempName = new Name(file);
                if (searchedItems.toLowerCase().equals(tempName.getName().toLowerCase()) && tempName.isValid()) {
                    creation = new Name(file);
                    fileFound = true;
                    if (creation.getName() != null) {

                        JFXButton button = creation.generateButton(selectedButtonsList);

                        boolean buttonExists = false;

                        //see if that item has already been added to the list
                        for (JFXButton currentButton : creationsButtonList) {
                            if (creation.getName().toLowerCase().equals(currentButton.getId().toLowerCase())) {
                                buttonExists = true;
                            }
                        }

                        if (!buttonExists) {
                            creationsButtonList.add(button);
                            creationsList.add(creation);
                            startPracticeButton.setVisible(true);
                            removeButton.setVisible(true);
                        } else {
                            showErrorDialog("This name has already been added", "Ok");
                        }
                    }
                    searchField.setDisable(false);
                    break;
                }
            }
            if (!fileFound) {
                // If no name is found
                showErrorDialog("This name could not be found on the database", "Ok");
            }
            creationsPane.getChildren().addAll(creationsButtonList);

        }

    }

    @FXML
    private void startPracticeHandler(ActionEvent e) {
        stackPane.setVisible(true);
        stackPane.getChildren().clear();
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        JFXDialog randomiseDialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

        Text header = new Text("Do you wish to randomise the list order?");
        header.setStyle("-fx-font-size: 30; -fx-font-family: 'Lato Heavy'");
        dialogContent.setHeading(header);

        JFXButton confirmRandomise = new JFXButton();

        confirmRandomise.setText("Randomise and play");
        confirmRandomise.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
        confirmRandomise.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                randomiseDialog.close();
                stackPane.setVisible(false);
                Collections.shuffle(creationsList);
                PlayViewController.setCreationsList(creationsList);
                loadPracticeView();
            }
        });

        JFXButton confirmPlay = new JFXButton();
        confirmPlay.setText("Play");
        confirmPlay.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
        confirmPlay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                randomiseDialog.close();
                stackPane.setVisible(false);
                PlayViewController.setCreationsList(creationsList);
                loadPracticeView();
            }
        });

        setAlignment(confirmRandomise, Pos.BASELINE_RIGHT);
        setAlignment(confirmPlay, Pos.BASELINE_LEFT);

        randomiseDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
            @Override
            public void handle(JFXDialogEvent event) {
                stackPane.setVisible(false);
            }
        });

        dialogContent.setActions(confirmRandomise, confirmPlay);

        randomiseDialog.show();
    }

    private void loadPracticeView() {
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("PlayViewController.fxml"));
            anchorPane.getChildren().clear();
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                searchField.setDisable(false);
                stackPane.setVisible(false);

            }
        });

        deleteDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
            @Override
            public void handle(JFXDialogEvent event) {
                stackPane.setVisible(false);
                searchField.setDisable(false);
            }
        });

        dialogContent.setActions(confirmDelete);
        deleteDialog.show();
    }

    //gets the list of creations
    public List<Name> getCreationsList() {
        return creationsList;
    }
}