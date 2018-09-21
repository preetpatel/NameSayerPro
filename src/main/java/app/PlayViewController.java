/**
 * PlayViewController.java
 * Scene for playing a selected creation
 *
 * Copyright Preet Patel, 2018
 * @Author Preet Patel
 * Date Created: 13 August, 2018
 */

package app;

import com.jfoenix.controls.*;
import javafx.beans.InvalidationListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.swing.*;
import java.io.*;
import java.util.List;

import static java.lang.Math.round;

public class PlayViewController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Text currentName;

    @FXML
    private JFXButton demoButton;

    @FXML
    private JFXButton recordButton;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton previousButton;

    @FXML
    private  JFXButton nextButton;

    @FXML
    private JFXComboBox<String> versions; // Not sure about what type goes inside

    @FXML
    private JFXListView<String> previousAttempts;

    private static List<Creation> _creationsList;
    private MediaPlayer mediaPlayer;

    private Creation currentLoadedCreation;

    /**
     * Initializes the Play Creation scene
     * Sets buttons for controlling the video
     * Loads the creation onto a media player and starts playing the video
     * The mediaToPlay variable must be set before loading playCreation
     */
    @FXML
    public void initialize() {
        currentLoadedCreation = _creationsList.get(0);
        loadCreation(currentLoadedCreation);
    }

    private void loadCreation(Creation creation){
        currentName.setText(creation.getCreationName());

        //TODO version loading thing
        loadVersionsOfCreation();
        //TODO load previous recordings from user
        loadPreviousUserRecordings();
        //TODO fuse the voice files together
        fuseNameFiles();

    }

    private void loadVersionsOfCreation(){

    }

    private void loadPreviousUserRecordings(){
        File folder = new File(NameSayer.userRecordingsPath);
        File[] files = folder.listFiles();

        for (File file : files) {
            Name tempName = new Name(file);
            if (currentLoadedCreation.getCreationName().toLowerCase().equals(tempName.getName().toLowerCase()) && tempName.isValid()) {
                for (File eachFile : tempName.getAllFilesOfName(new File(NameSayer.userRecordingsPath))) {
                    previousAttempts.getItems().add(eachFile.getName());
                }
                break;
            }
        }
    }

    private void fuseNameFiles(){

    }

    @FXML
    public void loadMainMenuView(){
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("SearchNamesViewController.fxml"));
            anchorPane.getChildren().clear();
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null,"An error occurred: "+err.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void setCreationsList(List<Creation> creationsList){
        _creationsList = creationsList;
    }
}
