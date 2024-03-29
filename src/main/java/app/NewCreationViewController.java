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
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewCreationViewController extends Controller{

    @FXML
    private JFXButton _closeButton;

    @FXML
    private JFXButton _recordButton;

    @FXML
    private Text _loaderText;

    @FXML
    private AnchorPane _anchorPane;

    @FXML
    private JFXButton _listenAudioButton;

    @FXML
    private JFXButton _keepAudioButton;

    @FXML
    private JFXButton _redoAudioButton;

    @FXML
    private JFXButton _compareButton;

    private Thread _playThread;
    private boolean _playing;
    private static String _nameOfCreation;
    private MediaPlayer _mediaPlayer;
    private static File databaseName;

    public static void setNameOfCreation(String nameOfCreation) {
        _nameOfCreation = nameOfCreation;
    }

    /**
     * Handler for the _closeButton button to go back to the Home View.
     * Opens an error dialog if there is an error
     */
    @FXML
    private void handleCloseButton() {
        _playing = false;
        Thread deleteAudio = new Thread(new deleteAudioFile());
        deleteAudio.start();
        switchController("PlayViewController.fxml", _anchorPane);
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
               // setupVolume();
            }
        });

        String nameToPronounce = _nameOfCreation.replaceAll("_", " ");
        _loaderText.setText("Press the button below and pronounce: \n \"" + nameToPronounce + "\"");
        _listenAudioButton.setVisible(false);
        _keepAudioButton.setVisible(false);
        _redoAudioButton.setVisible(false);
        _compareButton.setVisible(false);
    }

    //This boolean field is used for polling  for recording
    private boolean _recordOn = false;

    /**
     * Runs the _recordButton and timer threads on a background process and sets buttons to disabled
     */
    @FXML
    private void startRecord() {


        if (!_recordOn) {
            _recordOn = true;
            _recordButton.setText("Stop");
            _closeButton.setDisable(true);

            Thread thread = new Thread(new createAudioFile());
            thread.start();
        } else {
            _recordOn = false;
            Thread buttonsThread = new Thread(new showButtons());
            buttonsThread.start();
        }

    }

    /**
     * Thread to show buttons after recording
     */
    private class showButtons extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    _closeButton.setDisable(false);
                    _recordButton.setText("Record");
                    _recordButton.setVisible(false);
                    _listenAudioButton.setVisible(true);
                    _keepAudioButton.setVisible(true);
                    _redoAudioButton.setVisible(true);
                    _closeButton.setDisable(false);
                    if (databaseName != null) {
                        _compareButton.setVisible(true);

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
        File playFile = new File(path);
        setPlayBar(playFile);
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
                recordProcess = "ffmpeg -y  -f avfoundation -ac 2 -i \":0\" " + NameSayer.userRecordingsPath + "/'" + _nameOfCreation + "_audio.wav'";
            } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                recordProcess = "ffmpeg -y  -f dshow -ac 2 -i \":0\" " + NameSayer.userRecordingsPath + "/'" + _nameOfCreation + "_audio.wav'";
            } else {
                recordProcess = "ffmpeg -y -f alsa -ac 2 -i default " + NameSayer.userRecordingsPath + "/'" + _nameOfCreation + "_audio.wav'";
            }

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", recordProcess);
            Process process = builder.start();

            //poll if the user has stopped recording
            while(_recordOn){
                Thread.sleep(30);
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            writer.write("q");
            writer.flush();
            writer.close();
            return null;
        }
    }

    /**
     * Reloads the NewCreationViewController to enable user to redo an audio recording. The deleteAudioFile thread is called
     * that deletes the temporary file
     */
    @FXML
    private void redoButtonHandler() {
        if (_mediaPlayer != null) {
            _mediaPlayer.stop();
        }
        Thread deleteAudio = new Thread(new deleteAudioFile());
        deleteAudio.start();
        switchController("NewCreationViewController.fxml", _anchorPane);
    }

    /**
     * Deletes the temporary audio file. Is called when the redo button is pressed
     */
    private class deleteAudioFile extends Task<Void> {

        @Override
        protected Void call() {
            NameSayer.currentUser.increaseRecordButNotSaveScore(1);
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
        if (_mediaPlayer != null) {
            _mediaPlayer.stop();
        }
        _loaderText.setText("Saving your creation...");
        NameSayer.currentUser.increaseRecordAndSaveScore(2);

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
        switchController("PlayViewController.fxml", _anchorPane);
    }

    @FXML
    /**
     * provides compare functionality for system. plays User recording and database recording on loop
     */
    private void compareButtonHandler() {
        //if not playing, button allows playing
        if (!_playing) {
            NameSayer.currentUser.increaseCompareScore(1);
            _compareButton.setText("Stop");
            _playing = !_playing;
            String myAudioPath = NameSayer.userRecordingsPath + "/" + _nameOfCreation + "_audio.wav";
            String databaseAudioPath = databaseName.toURI().toString();
            playAudioFileOnLoop(databaseAudioPath, myAudioPath);
            _playThread.start();
            //if playing, button allows stopping
        } else {
            _compareButton.setText("Compare");
            _playing = !_playing;
            _playThread.stop();
            _listenAudioButton.setDisable(false);
            _keepAudioButton.setDisable(false);
            _redoAudioButton.setDisable(false);
        }
    }

    /**
     * Plays a file using the ffplay module
     * @param filePath A string specifying the absolute path to the file that is intended to be played
     */
    private void playAudioFile(String filePath) {
        _listenAudioButton.setDisable(true);
        _keepAudioButton.setDisable(true);
        _redoAudioButton.setDisable(true);
        _compareButton.setDisable(true);
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
                        _listenAudioButton.setDisable(false);
                        _keepAudioButton.setDisable(false);
                        _redoAudioButton.setDisable(false);
                        _compareButton.setDisable(false);
                    }
                });
            }
        };
        monitorThread.start();
    }


    /**
     * functionality for the program to play the files given on loop
     * @param filePath1 path to first file
     * @param filePath2 path to second file
     */
    private void playAudioFileOnLoop(String filePath1, String filePath2){
        _listenAudioButton.setDisable(true);
        _keepAudioButton.setDisable(true);
        _redoAudioButton.setDisable(true);
        _playThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(_playing) {

                        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffplay -nodisp -autoexit " + filePath1);
                        Process playProcess1 = builder.start();
                        playProcess1.waitFor();
                        ProcessBuilder builder2 = new ProcessBuilder("/bin/bash", "-c", "ffplay -nodisp -autoexit " + filePath2);
                        Process playProcess2 = builder2.start();
                        playProcess2.waitFor();

                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            _listenAudioButton.setDisable(false);
                            _keepAudioButton.setDisable(false);
                            _redoAudioButton.setDisable(false);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

}
