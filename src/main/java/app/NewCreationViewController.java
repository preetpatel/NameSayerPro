/**
 * NewCreationViewController.java
 * Scene for generating a new creation. Loads the initial pre-recording state
 *
 * Copyright Preet Patel, 2018
 * @Author Preet Patel
 * Date Created: 13 August, 2018
 */

package app;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class NewCreationViewController {

    @FXML
    private JFXButton close;

    @FXML
    private JFXButton record;

    @FXML
    private Text loaderText;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton listenAudio;

    @FXML
    private JFXButton keepAudio;

    @FXML
    private JFXButton redoAudio;


    private static String _nameOfCreation;
    private MediaPlayer mediaPlayer;

    public static void setNameOfCreation(String nameOfCreation) {
        _nameOfCreation = nameOfCreation;
    }

    /**
     * Handler for the close button to go back to the Home View.
     * Opens an error dialog if there is an error
     */
    @FXML
    private void handleCloseButton() {
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("HomeViewController.fxml"));
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    /**
     * Initialises the scene by setting buttons visible status and loading the stage
     */
    @FXML
    private void initialize() {
        StringValidator validate = new StringValidator(_nameOfCreation);
        if (!validate.isValid()) {
            validate.deleteFile();
        }
        loaderText.setText("Press the button below and pronounce the name: \"" + _nameOfCreation + "\"");
        listenAudio.setVisible(false);
        keepAudio.setVisible(false);
        redoAudio.setVisible(false);
    }

    /**
     * Runs the record and timer threads on a background process and sets buttons to disabled
     */
    @FXML
    private void startRecord() {

        record.setDisable(true);
        record.setText("Recording...");
        close.setDisable(true);
        Thread timerThread = new Thread(new timerShow());
        timerThread.start();
        Thread thread = new Thread(new createAudioFile());
        thread.start();
        record.setVisible(false);
        Thread buttonThread = new Thread(new showButtons());
        buttonThread.start();

    }

    /**
     * Thread for showing the countdown text
     */
    private class timerShow extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            int i = 5;
            loaderText.setText("");
            while (i>0) {
                loaderText.setText(loaderText.getText() + Integer.toString(i) + "...");
                Thread.sleep(1000);
                i-=1;
            }
            return null;
        }
    }

    /**
     * Thread to show buttons after recording
     */
    private class showButtons extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            Thread.sleep(5000);
            listenAudio.setVisible(true);
            keepAudio.setVisible(true);
            redoAudio.setVisible(true);
            return null;
        }
    }

    /**
     * Runs the playAudioFile thread
     */
    @FXML
    private void playAudio() {
        Thread playAudio = new Thread(new playAudioFile());
        playAudio.start();

    }

    /**
     * Thread for playing an audio file in the background
     */
    private class playAudioFile extends Task<Void> {

        @Override
        protected Void call() {
            String path =  NameSayer.creationsPath +"/" + _nameOfCreation + "_audio.mp3";
            File file = new File(path);
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.play();
                }
            });
            return null;
        }
    }

    /**
     * Thread for creating a new creation while recording audio. Generates an audio file
     */

    private class createAudioFile extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -t 5 -f alsa -ac 2 -i default " + NameSayer.creationsPath +"/'"+ _nameOfCreation + "_audio.mp3'");
            builder.start();
            return null;
        }
    }

    /**
     * Reloads the NewCreationViewController to enable user to redo an audio recording. The deleteAudioFile thread is called
     * that deletes the temporary file
     */
    @FXML
    private void redoButtonHandler() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        Thread deleteAudio = new Thread(new deleteAudioFile());
        deleteAudio.start();
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("NewCreationViewController.fxml"));
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    /**
     * Deletes the temporary audio file. Is called when the redo button is pressed
     */
    private class deleteAudioFile extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "rm " + NameSayer.creationsPath +"/'"+ _nameOfCreation + "_audio.mp3'");
            Process process = builder.start();
            return null;
        }
    }

    /**
     * Button handler for the keep button that runs the bash command for merging audio and video only files into one
     * Format of generated video is MP4
     * The creation is handled in a different thread
     */
    @FXML
    private void keepButtonHandler() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        loaderText.setText("Saving your creation...");

        Thread thread = new Thread(task);
        thread.start();
    }

    Task<Void> task = new Task<Void>() {
        @Override
        public Void call() throws Exception {
            String command = "ffmpeg -f lavfi -i color=c=white:s=1920x1080:d=5 -vf \"drawtext=fontsize=60: " +
                    "fontcolor=black:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _nameOfCreation +"'\" " + NameSayer.creationsPath +"/'"+ _nameOfCreation +"_video.mp4' 2>/dev/null && " +
                    "ffmpeg -i " + NameSayer.creationsPath +"/'"+ _nameOfCreation +"_video.mp4' -i " + NameSayer.creationsPath +"/'"+ _nameOfCreation +"_audio.mp3' -codec copy -shortest " +
                    "" + NameSayer.creationsPath +"/'"+ _nameOfCreation +".mp4' 2> /dev/null && " +
                    "rm " + NameSayer.creationsPath +"/'"+ _nameOfCreation +"_video.mp4' 2>/dev/null && " +
                    "rm " + NameSayer.creationsPath +"/'"+ _nameOfCreation +"_audio.mp3' 2>/dev/null";
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
            Process process = builder.start();
            process.waitFor();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    goBack();
                }
            });

            return null;
        }
    };

    /**
     * Loads the HomeViewController scene
     */
    private void goBack() {
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("HomeViewController.fxml"));
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException io) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + io.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
