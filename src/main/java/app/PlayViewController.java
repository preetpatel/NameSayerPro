/**
 * PlayViewController.java
 * Scene for playing a selected creation
 * <p>
 * Copyright Preet Patel, 2018
 *
 * @Author Preet Patel
 * Date Created: 13 August, 2018
 */

package app;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import jdk.nashorn.internal.runtime.ECMAException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private JFXButton nextButton;

    @FXML
    private JFXComboBox<Label> versions; // Not sure about what type goes inside

    @FXML
    private JFXListView<String> previousAttempts;

    @FXML
    private ImageView star1;

    @FXML
    private ImageView star2;

    @FXML
    private ImageView star3;

    @FXML
    private ImageView star4;

    @FXML
    private ImageView star5;


    private static List<Name> _creationsList;
    private MediaPlayer mediaPlayer;
    private Name currentLoadedCreation;
    private static int currentSelection = 0;
    private HashMap<String, File> versionPerms;
    private HashMap<String, File> userFiles;
    private File _fileToPlay;
    private Image noTouch = new Image("star_notouch.png");
    private Image touch = new Image("star_touch.png");

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
        if (_creationsList.size() == 1) {
            nextButton.setText("FINISH >");
        }
    }

    /**
     *  Allows the user to set a rating onto different versions of different names
     */
    @FXML
    private void logRating(int rating) {
        BufferedWriter writer;
        try {

            String ratingString = _fileToPlay.getName() + " " + Integer.toString(rating);

            BufferedReader br = new BufferedReader(new FileReader(DirectoryManager.getRatings()));
            String line;

            //check if file is already given a rating
            boolean lineExists = false;
            while ((line = br.readLine()) != null) {
                if (line.contains(_fileToPlay.getName())) {
                    lineExists = true;
                    break;
                }
            }

            //if a rating does not exist, add a new rating
            if (!lineExists) {
                writer = new BufferedWriter(new FileWriter(DirectoryManager.getRatings(), true));
                writer.write(ratingString + "\n");
                writer.close();

                //if a rating does exist, replace the old rating
            } else {
                String old = "";
                BufferedReader reader = new BufferedReader(new FileReader(DirectoryManager.getRatings()));
                String line2 = reader.readLine();

                while (line2 != null) {
                    old = old + line2 + System.lineSeparator();
                    line2 = reader.readLine();


                }
                String newContent = old.replaceAll(_fileToPlay.getName() + " [12345]", ratingString);
                FileWriter writer2 = new FileWriter(DirectoryManager.getRatings());
                writer2.write(newContent);
                reader.close();
                writer2.close();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please Enter a number from 1-5", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e2) {
            JOptionPane.showMessageDialog(null, "An error occurred printing rating", "Error", JOptionPane.ERROR_MESSAGE);
        }


    }

    /**
     * reads the text file in which ratings are stored, which is then displayed onto the GUI
     */
    private void updateRating() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(DirectoryManager.getRatings()));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains(_fileToPlay.getName())) {
                    String[] nameCreation = line.split("\\s+");
                    int rating = Integer.parseInt(nameCreation[1]);
                    updateStars(rating);
                    break;
                } else {
                    updateStars(0);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred printing rating", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStars(int rating) {
        List<ImageView> stars = new ArrayList<>();
        stars.add(star1);
        stars.add(star2);
        stars.add(star3);
        stars.add(star4);
        stars.add(star5);

        for (ImageView star : stars) {
            star.setImage(noTouch);
        }

        for (int i = 1; i <= rating; i++) {
            stars.get(i - 1).setImage(touch);
        }
    }

    /**
     * changes the creations
     * @param creation
     */
    private void loadCreation(Name creation) {
        currentName.setText(creation.getName());
        loadVersionsOfCreation(creation);
        loadPreviousUserRecordings();
        updateRating();
    }

    private void loadVersionsOfCreation(Name creation) {

        currentName.setText(creation.getName());
        versions.getItems().clear();

        versionPerms = creation.getVersions();
        _fileToPlay = versionPerms.get("Version 1");

        for (int i = 0; i < versionPerms.size(); i++) {
            versions.getItems().add(new Label("Version " + (i + 1)));
        }

        versions.setConverter(new StringConverter<Label>() {
            @Override
            public String toString(Label object) {
                return object == null ? "" : object.getText();
            }

            @Override
            public Label fromString(String string) {
                return new Label(string);
            }
        });

        versions.setEditable(false);
        versions.getSelectionModel().selectFirst();


    }

    private void loadPreviousUserRecordings() {
        previousAttempts.getItems().clear();
        File folder = new File(NameSayer.userRecordingsPath);
        File[] files = folder.listFiles();
        userFiles = new HashMap<>();
        for (File file : files) {
            Name tempName = new Name(file);
            Name currentFile = new Name(_fileToPlay);

            String compareString = currentFile.getName() + "_V";
            compareString = compareString.toLowerCase();
            if (tempName.getName().toLowerCase().contains(compareString) && tempName.isValid()) {
                previousAttempts.getItems().add(tempName.getName());
                userFiles.put(tempName.getName(), file);
            }
        }
    }

    @FXML
    public void nextButtonHandler() {
        currentSelection++;
        if (currentSelection < _creationsList.size()) {
            currentLoadedCreation = _creationsList.get(currentSelection);
            loadCreation(_creationsList.get(currentSelection));
            previousButton.setText("< BACK");
        }
        if (currentSelection == _creationsList.size() - 1) {
            nextButton.setText("FINISH >");
        }
        if (currentSelection == _creationsList.size()) {
            loadMainMenuView();
        }
    }

    @FXML
    public void backButtonHandler() {
        currentSelection--;
        if (currentSelection >= 0) {
            currentLoadedCreation = _creationsList.get(currentSelection);
            loadCreation(_creationsList.get(currentSelection));
            nextButton.setText("NEXT >");
        }
        if (currentSelection == 0) {
            previousButton.setText("< MENU");
        }
        if (currentSelection < 0) {
            loadMainMenuView();
        }
    }


    @FXML
    public void loadMainMenuView() {
        _creationsList.clear();
        currentSelection = 0;
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("SearchNamesViewController.fxml"));
            anchorPane.getChildren().clear();
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void setCreationsList(List<Name> creationsList) {
        _creationsList = creationsList;
    }

    @FXML
    public void demoButtonHandler() {
        playFile(_fileToPlay);
    }

    public void playFile(File fileToPlay) {
        demoButton.setDisable(true);
        recordButton.setDisable(true);
        micTestButton.setDisable(true);
        nextButton.setDisable(true);
        previousButton.setDisable(true);

        Thread monitorThread = new Thread() {
            @Override
            public void run() {
                try {
                    ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffplay -nodisp -autoexit " + fileToPlay.toURI().toString());
                    Process process = builder.start();
                    process.waitFor();
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
                demoButton.setDisable(false);
                recordButton.setDisable(false);
                micTestButton.setDisable(false);
                nextButton.setDisable(false);
                previousButton.setDisable(false);
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
        updateRating();
    }

    @FXML
    public void micTestButtonHandler() {
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("MicTestViewController.fxml"));
            anchorPane.getChildren().clear();
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    public void recordButtonHandler() {
        try {
            NewCreationViewController.setDatabaseName(_fileToPlay);
            NewCreationViewController.setNameOfCreation(currentLoadedCreation.getName());
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("NewCreationViewController.fxml"));
            anchorPane.getChildren().clear();
            anchorPane.getChildren().add(newLoadedPane);
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    public void playUserCreatedFile() {

        String file = previousAttempts.getSelectionModel().getSelectedItems().toString();
        file = file.replace("[", "");
        file = file.replace("]", "");
        if (!file.equals("")) {
            playFile(userFiles.get(file));
        }
    }

    @FXML
    public void star1ButtonHandler() {
        updateStars(1);
        logRating(1);
    }

    @FXML
    public void star2ButtonHandler() {
        updateStars(2);
        logRating(2);
    }

    @FXML
    public void star3ButtonHandler() {
        updateStars(3);
        logRating(3);
    }

    @FXML
    public void star4ButtonHandler() {
        updateStars(4);
        logRating(4);
    }

    @FXML
    public void star5ButtonHandler() {
        updateStars(5);
        logRating(5);
    }
}
