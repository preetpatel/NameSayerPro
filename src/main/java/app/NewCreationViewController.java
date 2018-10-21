/**
 * NewCreationViewController.java
 * Scene for generating a new creation. Loads the initial pre-recording state
 * <p>
 * Copyright Preet Patel, 2018
 *
 * @Author Preet Patel
 * @Author Chuyang Chen
 * Date Created: 19 August, 2018
 */

package app;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewCreationViewController extends Controller{

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

    @FXML
    private JFXButton compareButton;

    private Thread _playThread;
    private boolean _playing;
    private static String _nameOfCreation;
    private MediaPlayer mediaPlayer;
    private static File databaseName;

    private int currentTimer = 5;

    public static void setNameOfCreation(String nameOfCreation) {
        _nameOfCreation = nameOfCreation;
    }

    /**
     * Handler for the close button to go back to the Home View.
     * Opens an error dialog if there is an error
     */
    @FXML
    private void handleCloseButton() {
        _playing = false;
        Thread deleteAudio = new Thread(new deleteAudioFile());
        deleteAudio.start();
        switchController("PlayViewController.fxml", anchorPane);
    }

    public static void setDatabaseName(File file) {
        databaseName = file;
    }

    /**
     * Initialises the scene by setting buttons visible status and loading the stage
     */
    @FXML
    private void initialize() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setupVolume();
            }
        });

        loaderText.setText("Press the button below and pronounce the name: \"" + _nameOfCreation + "\"");
        listenAudio.setVisible(false);
        keepAudio.setVisible(false);
        redoAudio.setVisible(false);
        compareButton.setVisible(false);
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
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    loaderText.setText("");
                }
            });
            while (currentTimer > -1) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loaderText.setText(Integer.toString(currentTimer));
                    }
                });

                Thread.sleep(1000);
                currentTimer -= 1;
            }
            currentTimer = 5;
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
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listenAudio.setVisible(true);
                    keepAudio.setVisible(true);
                    redoAudio.setVisible(true);
                    close.setDisable(false);
                    if (databaseName != null) {
                        compareButton.setVisible(true);

                    }
                }
            });
            return null;
        }
    }

    /**
     * Runs the playAudioFile thread
     */
    @FXML
    private void listenButtonHandler() {
        String path = NameSayer.userRecordingsPath + "/" + _nameOfCreation + "_audio.wav";
        playAudioFile(path);
    }

    /**
     * Thread for creating a new creation while recording audio. Generates an audio file
     */

    private class createAudioFile extends Task<Void> {

        @Override
        protected Void call() throws Exception {

            String recordProcess = "";
            if(System.getProperty("os.name").toLowerCase().contains("mac")) {
                recordProcess = "ffmpeg -t 5 -f avfoundation -ac 2 -i \":0\" " + NameSayer.userRecordingsPath + "/'" + _nameOfCreation + "_audio.wav'";
            } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                recordProcess = "ffmpeg -t 5 -f dshow -ac 2 -i \":0\" " + NameSayer.userRecordingsPath + "/'" + _nameOfCreation + "_audio.wav'";
            } else {
                recordProcess = "ffmpeg -t 5 -f alsa -ac 2 -i default " + NameSayer.userRecordingsPath + "/'" + _nameOfCreation + "_audio.wav'";
            }

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", recordProcess);
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
        switchController("NewCreationViewController.fxml",anchorPane);
    }

    /**
     * Deletes the temporary audio file. Is called when the redo button is pressed
     */
    private class deleteAudioFile extends Task<Void> {

        @Override
        protected Void call() {
            File file = new File(NameSayer.userRecordingsPath);
            File[] files = file.listFiles();
            for (File check : files) {
                if (check.getName().equals(_nameOfCreation + "_audio.wav")) {
                    check.delete();
                }
            }
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

        File[] userCreations = new File(NameSayer.userRecordingsPath).listFiles();
        int counter = 1;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        Date date = new Date();

        File tempAudiofile = null;

        for (File file : userCreations) {
            String displayName = file.getName();
            displayName = displayName.replaceAll("^[^_]*_[^_]*_[^_]*_", "");
            displayName = displayName.replaceAll("[.][^.]+$", "");
            if (displayName.contains(_nameOfCreation + "_V")) {
                displayName = displayName.replace(_nameOfCreation + "_V", "");
                int compare = Integer.parseInt(displayName);
                if (compare >= counter) {
                    counter = compare + 1;
                }
            }
            if (displayName.contains("_audio")) {
                tempAudiofile = file;
            }
        }

        if (tempAudiofile != null) {
            File destinationFile = new File(NameSayer.userRecordingsPath + "/namesayer_" + dateFormat.format(date) + "_" + _nameOfCreation + "_V" + counter + ".wav");
            tempAudiofile.renameTo(destinationFile);
        }

        // Set database name to null to prevent unauthorised playback
        databaseName = null;
        switchController("PlayViewController.fxml", anchorPane);
    }

    @FXML
    private void compareButtonHandler() {
        if (!_playing) {
            compareButton.setText("Stop");
            _playing = !_playing;
            String myAudioPath = NameSayer.userRecordingsPath + "/" + _nameOfCreation + "_audio.wav";
            String databaseAudioPath = databaseName.toURI().toString();
            playAudioFileOnLoop(databaseAudioPath, myAudioPath);
            _playThread.start();

        } else {
            compareButton.setText("Compare");
            _playing = !_playing;
            _playThread.stop();
            listenAudio.setDisable(false);
            keepAudio.setDisable(false);
            redoAudio.setDisable(false);
        }
    }

    /**
     * Plays a file using the ffplay module
     * @param filePath A string specifying the absolute path to the file that is intended to be played
     */
    private void playAudioFile(String filePath) {
        listenAudio.setDisable(true);
        keepAudio.setDisable(true);
        redoAudio.setDisable(true);
        compareButton.setDisable(true);
        Thread monitorThread = new Thread() {
            @Override
            public void run() {
                try {
                    ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffplay -nodisp -autoexit " + filePath);
                    Process process = builder.start();
                    process.waitFor();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        listenAudio.setDisable(false);
                        keepAudio.setDisable(false);
                        redoAudio.setDisable(false);
                        compareButton.setDisable(false);
                    }
                });
            }
        };
        monitorThread.start();
    }

    private void playAudioFileOnLoop(String filePath1, String filePath2){
        listenAudio.setDisable(true);
        keepAudio.setDisable(true);
        redoAudio.setDisable(true);
        _playThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(_playing) {
                        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffplay -nodisp -autoexit " + filePath1);
                        Process process = builder.start();
                        process.waitFor();
                        ProcessBuilder builder2 = new ProcessBuilder("/bin/bash", "-c", "ffplay -nodisp -autoexit " + filePath2);
                        Process process2 = builder2.start();
                        process2.waitFor();
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            listenAudio.setDisable(false);
                            keepAudio.setDisable(false);
                            redoAudio.setDisable(false);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }
}
