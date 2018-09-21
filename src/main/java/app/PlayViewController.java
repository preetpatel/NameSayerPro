/**
 * PlayViewController.java
 * Scene for playing a selected creation
 *
 * Copyright Preet Patel, 2018
 * @Author Preet Patel
 * Date Created: 13 August, 2018
 */

package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.StringConverter;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
    private JFXComboBox<Label> versions = new JFXComboBox<>(); // Not sure about what type goes inside

    @FXML
    private JFXTreeTableView previousAttempts;

    @FXML
    private AnchorPane pane;

    private static List<Creation> _creationsList;
    private MediaPlayer mediaPlayer;

    /**
     * Initializes the Play Creation scene
     * Sets buttons for controlling the video
     * Loads the creation onto a media player and starts playing the video
     * The mediaToPlay variable must be set before loading playCreation
     */
    @FXML
    public void initialize() {


        Creation firstCreation = _creationsList.get(0);
        loadCreation(firstCreation);




    }

    private void loadCreation(Creation creation){



        //TODO version loading thing
        loadVersionsOfCreation(creation);
        //TODO load previous recordings from user
        loadPreviousUserRecordings();
        //TODO fuse the voice files together
        fuseNameFiles();

    }

    private void loadVersionsOfCreation(Creation creation){

        currentName.setText(creation.getCreationName());

        //TODO load all different permutations possible of the creation from different name versions

        String[] versionPerms = creation.getPermutations();

        for (String str : versionPerms){
            versions.getItems().add(new Label(str));
        }

        versions.setConverter(new StringConverter<Label>() {
            @Override
            public String toString(Label object) {
                return object==null? "" : object.getText();
            }

            @Override
            public Label fromString(String string) {
                return new Label(string);
            }
        });

        //pane.getChildren().add(versions);

        //TODO add handler to allow user to choose which version they want

        //TODO fuse voice files together

    }

    private void loadPreviousUserRecordings(){

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
