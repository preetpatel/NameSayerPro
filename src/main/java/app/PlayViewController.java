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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.StringConverter;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.io.*;
import java.util.HashMap;
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
    private JFXButton micTestButton;

    @FXML
    private JFXButton previousButton;

    @FXML
    private  JFXButton nextButton;

    @FXML
    private JFXComboBox<Label> versions; // Not sure about what type goes inside

    @FXML
    private JFXListView<String> previousAttempts;

    private static List<Name> _creationsList;
    private MediaPlayer mediaPlayer;
    private Name currentLoadedCreation;
    private static int currentSelection = 0;
    private HashMap<String, File> versionPerms;
    private File _fileToPlay;

    /**
     * Initializes the Play Creation scene
     * Sets buttons for controlling the video
     * Loads the creation onto a media player and starts playing the video
     * The mediaToPlay variable must be set before loading playCreation
     */
    @FXML
    public void initialize() {
        currentLoadedCreation = _creationsList.get(currentSelection);
        loadCreation(currentLoadedCreation);
        previousButton.setText("< MENU");
        if (_creationsList.size()==1) {
            nextButton.setText("FINISH >");
        }
    }

    private void loadCreation(Name creation){
        currentName.setText(creation.getName());

        //TODO version loading thing
        loadVersionsOfCreation(creation);
        //TODO load previous recordings from user
        loadPreviousUserRecordings();
        //TODO fuse the voice files together
        fuseNameFiles();

    }

    private void loadVersionsOfCreation(Name creation){

        currentName.setText(creation.getName());

        //TODO load all different permutations possible of the creation from different name versions

        versionPerms = creation.getVersions();
        _fileToPlay = versionPerms.get("Version 1");
        versions.getItems().clear();

        for (int i = 0; i< versionPerms.size(); i++){
            versions.getItems().add(new Label("Version " + (i+1)));
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

        versions.setEditable(false);
        versions.getSelectionModel().selectFirst();

        //TODO add handler to allow user to choose which version they want

        //TODO fuse voice files together

    }

    private void loadPreviousUserRecordings(){
        previousAttempts.getItems().clear();
        File folder = new File(NameSayer.userRecordingsPath);
        File[] files = folder.listFiles();

        for (File file : files) {
            Name tempName = new Name(file);
            Name currentFile = new Name(_fileToPlay);
            if (currentFile.getName().toLowerCase().equals(tempName.getName().toLowerCase()) && tempName.isValid()) {
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
    public void nextButtonHandler(){
        currentSelection++;
        if(currentSelection < _creationsList.size()) {
            loadCreation(_creationsList.get(currentSelection));
            previousButton.setText("< BACK");
        }
        if (currentSelection == _creationsList.size()-1){
            nextButton.setText("FINISH >");
        }
        if (currentSelection == _creationsList.size()){
            loadMainMenuView();
        }
    }

    @FXML
    public void backButtonHandler(){
        currentSelection--;
        if(currentSelection >= 0 ) {
            loadCreation(_creationsList.get(currentSelection));
            nextButton.setText("NEXT >");
        }
        if (currentSelection == 0){
            previousButton.setText("< MENU");
        }
        if (currentSelection < 0){
            loadMainMenuView();
        }
    }


    @FXML
    public void loadMainMenuView(){
        _creationsList.clear();
        currentSelection=0;
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("SearchNamesViewController.fxml"));
            anchorPane.getChildren().clear();
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null,"An error occurred: "+err.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void setCreationsList(List<Name> creationsList){
        _creationsList = creationsList;
    }
    @FXML
    public void demoButtonHandler() {
        demoButton.setDisable(true);
        Thread monitorThread = new Thread() {
            @Override
            public void run() {
                try{
                    AudioInputStream ais = AudioSystem.getAudioInputStream(_fileToPlay);
                    Clip test = AudioSystem.getClip();

                    test.open(ais);
                    test.start();

                    while (!test.isRunning())
                        Thread.sleep(10);
                    while (test.isRunning())
                        Thread.sleep(10);

                    test.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                demoButton.setDisable(false);
            }
        };
        monitorThread.start();
    }

    // Sets the correct file to play when combo box selection is changed
    @FXML
    public void versionSelectionHandler(ActionEvent e) {
        if (versions.getSelectionModel().getSelectedItem() != null) {
            _fileToPlay = versionPerms.get(versions.getSelectionModel().getSelectedItem().getText());
        }
    }

    @FXML
    public void micTestButtonHandler() {
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("MicTestViewController.fxml"));
            anchorPane.getChildren().clear();
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null,"An error occurred: "+err.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    public void recordButtonHandler() {
        try {
            NewCreationViewController.setNameOfCreation(currentLoadedCreation.getName());
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("NewCreationViewController.fxml"));
            anchorPane.getChildren().clear();
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null,"An error occurred: "+err.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
