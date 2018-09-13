/**
 * HomeViewController.java
 * Main Menu Class. Displays the home screen for Namesayer
 *
 * Copyright Preet Patel, 2018
 * @Author Preet Patel
 * Date Created: 13 August, 2018
 */

package app;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.swing.*;
import java.io.*;

public class HomeViewController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXButton playButton;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private JFXButton newCreationButton;

    @FXML
    private Text introText;

    @FXML
    private JFXMasonryPane creationsPane;

    @FXML
    private ScrollPane scrollPane;

    private String selectedCreation;

    /**
     * Play button event handler.
     * Loads a new PlayViewController scene onto the same stage.
     * @throws Exception
     */
    @FXML
    private void playButtonHandler(ActionEvent e) {

        PlayViewController.setMediaToPlay(selectedCreation);
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("PlayViewController.fxml"));
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null,"An error occurred: "+err.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);

        }

    }

    /**
     * Deletes the .mp4 file from the creations directory. Runs a bash command to remove a creation from the
     * creations folder
     * @param creationToDelete String for the file to delete.
     */
    private void deleteCreation(String creationToDelete) {
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "rm " + NameSayer.creationsPath +"/'" + creationToDelete + "'.*");
            Process process = builder.start();
            loadCreationsOntoPane();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"An error occurred: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Delete button event handler.
     * Creates a modal dialog to confirm file deletion. Calls the deleteCreation method on the
     * file selected to be removed.
     * @link #deleteCreation
     */
    @FXML
    private void deleteButtonHandler(ActionEvent e) {

        stackPane.setVisible(true);
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        JFXDialog deleteDialog = new JFXDialog(stackPane,dialogContent,JFXDialog.DialogTransition.CENTER);

        Text header = new Text("Delete " + selectedCreation);
        header.setStyle("-fx-font-size: 30; -fx-font-family: 'Lato Heavy'");
        dialogContent.setHeading(header);

        Text content = new Text("Do you really want to delete this?");
        content.setStyle("-fx-font-size: 25; -fx-font-family: 'Lato Medium'");
        dialogContent.setBody(content);

        JFXButton confirmDelete = new JFXButton();
        confirmDelete.setText("Delete");
        confirmDelete.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
        confirmDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteCreation(selectedCreation);
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

    /**
     * Create button event handler
     * Creates a modal dialog for generating a new creation.
     * Modal checks for valid file names and existing file names
     * If an invalid string is entered, the modal text field turns red
     * If a creation exists with that name, an override button appears
     */
    @FXML
    private void createButtonHandler(ActionEvent e) {

                stackPane.setVisible(true);
                JFXDialogLayout dialogContent = new JFXDialogLayout();
                JFXDialog createDialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

                Text dialogHeader = new Text("Add new Creation");
                dialogHeader.setStyle("-fx-font-size: 30; -fx-font-family: 'Lato Heavy'");
                dialogContent.setHeading(dialogHeader);

                Text content = new Text("Please Enter A Name For This Creation:");
                content.setStyle("-fx-font-size: 25; -fx-font-family: 'Lato Medium'");
                dialogContent.setBody(content);

                JFXTextField field = new JFXTextField();
                field.setPromptText("Enter name here");
                dialogContent.setBody(field);
                JFXButton nextStep = new JFXButton();
                nextStep.setDisable(true);

                field.setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        nextStep.setText("Next");
                        dialogHeader.setText("Add new Creation");
                        String fileName = field.getText().trim();
                        StringValidator stringValidator = new StringValidator(fileName);

                        if(field.getText().length() > 25) {
                            nextStep.setDisable(true);
                            field.setStyle("-fx-background-color: #ff4b52;");
                            dialogHeader.setText("Keep it short. Try abbreviations");
                        } else if (!stringValidator.isValid()) {
                            nextStep.setDisable(true);
                            field.setStyle("-fx-background-color: #ff4b52;");
                            dialogHeader.setText("Please type a valid name");

                            //Checks if file already exists and sets override option button
                            if (stringValidator.checkFileExists()) {
                                dialogHeader.setText("Creation already exists");
                                nextStep.setDisable(false);
                                nextStep.setText("Override Creation");
                            }
                        } else {
                            field.setStyle("-fx-background-color: white;");
                            nextStep.setDisable(false);
                            if (event.getCode() == KeyCode.ENTER) {
                                loadCreateCreationView(field.getText());
                            }
                        }
                    }
                });

                nextStep.setText("Next");
                nextStep.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
                nextStep.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        loadCreateCreationView(field.getText());
                    }
                });

                createDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
                    @Override
                    public void handle(JFXDialogEvent event) {
                        stackPane.setVisible(false);
                    }
                });
                dialogContent.setActions(nextStep);
                createDialog.show();
    }

    /**
     * loads the NewCreationViewController.java view on the same stage to continue with recording
     * @param text a string that is the name of the file being created without any file extensions.
     */
    private void loadCreateCreationView(String text) {
        stackPane.setVisible(false);
        try {
            NewCreationViewController.setNameOfCreation(text);
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("NewCreationViewController.fxml"));
            anchorPane.getChildren().add(newLoadedPane);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Button handler for selecting any creation on the list of creations.
     * Sets play and delete buttons to visible
     * Sets introText to match the name of creations
     * @param e
     */
    @FXML
    private void existingCreationButtonHandler(ActionEvent e) {
        playButton.setVisible(true);
        deleteButton.setVisible(true);
        selectedCreation = ((JFXButton)e.getSource()).getText();
        introText.setText(selectedCreation);
    }

    /**
     * Sets elements onto stage and loads creations from the creations folder defined in Namesayer.creationsPath
     * If the folder does not exist, creates a new folder for storing creations
     */
    private void loadCreationsOntoPane() {
        creationsPane.getChildren().clear();
        stackPane.setVisible(false);
        playButton.setVisible(false);
        deleteButton.setVisible(false);
        introText.setVisible(true);
        introText.setText("Select a Creation");

            File storage = new File(NameSayer.creationsPath);
            if (!storage.exists()) {
                if (!storage.mkdirs()) {
                    JOptionPane.showMessageDialog(null, "An Error occurred while trying to load creations ", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        // Removes any temporary files left in the creations folder due to unhandled disposals or other errors
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "rm " + NameSayer.creationsPath +"/*_audio.*; rm " + NameSayer.creationsPath +"/*_video.*");
            Process process = builder.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        //Loads all creations onto view
        ObservableList<JFXButton> creationsList = FXCollections.<JFXButton>observableArrayList();
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ls " + NameSayer.creationsPath +"/ -1 | sed -e 's/\\..*$//'");
            Process process = builder.start();
            InputStream stdout = process.getInputStream();
            BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
            String line;
            while ((line = stdoutBuffered.readLine()) != null )
            {
                JFXButton button = new JFXButton();
                button.setMnemonicParsing(false);
                button.setText(line);
                button.setId(line);
                button.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
                button.setOnAction(this::existingCreationButtonHandler);
                creationsList.add(button);

            }
            creationsPane.getChildren().addAll(creationsList);


        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        /* Sets handlers for buttons */
        playButton.setOnAction(this::playButtonHandler);
        deleteButton.setOnAction(this::deleteButtonHandler);
        newCreationButton.setOnAction(this::createButtonHandler);

        /* Sets properties for the scrollview within which the creationsPane sits */
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
    }

    /**
     * Method that calls loadCreationsOntoPane() method.
     * This method exists due to being required by JavaFX
     */
    @FXML
    private void initialize() {
        loadCreationsOntoPane();
    }

}
