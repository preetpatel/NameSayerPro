/**
 * PlayViewController.java
 * Scene for playing a selected creation
 * <p>
 * Copyright Preet Patel, 2018
 *
 * @Author Preet Patel
 * @Author Chuyang Chen
 * Date Created: 19 August, 2018
 */

package app;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayViewController extends Controller{

    @FXML
    private AnchorPane _anchorPane;

    @FXML
    private Text _currentName;

    @FXML
    private JFXButton _demoButton;

    @FXML
    private JFXButton _recordButton;

    @FXML
    private JFXButton _micTestButton;

    @FXML
    private JFXButton _previousButton;

    @FXML
    private JFXButton _nextButton;

    @FXML
    private JFXComboBox<Label> _versions;

    @FXML
    private JFXListView<String> _previousAttempts;

    @FXML
    private ImageView _star1;

    @FXML
    private ImageView _star2;

    @FXML
    private ImageView _star3;

    @FXML
    private ImageView _star4;

    @FXML
    private ImageView _star5;

    @FXML
    private StackPane _stackPane;

    private static List<Name> _creationsList = new ArrayList<>();
    private Name _currentLoadedCreation;
    private static int _currentSelection = 0;
    private HashMap<String, File> _versionPerms;
    private HashMap<String, File> _userFiles;
    private File _fileToPlay;
    private Image _noTouch = new Image("star_notouch.png");
    private Image _touch = new Image("star_touch.png");

    /**
     * Initializes the Play Creation scene
     * Sets buttons for controlling the video
     * Loads the creation onto a media player and starts playing the video
     * The mediaToPlay variable must be set before loading playCreation
     */
    @FXML
    public void initialize() {
        _stackPane.setDisable(true);
        if(_creationsList.size() > 0 ) {
            _currentLoadedCreation = _creationsList.get(_currentSelection);
            loadCreation(_currentLoadedCreation);
        }
        if(_currentSelection ==0) {
            _previousButton.setText("< Menu");
        }
        if (_creationsList.size() == 1) {
            _nextButton.setText("Finish >");
        }
    }

    /**
     * Allows the user to set a rating onto different _versions of different names
     */
    @FXML
    private void logRating(int rating) {
        BufferedWriter writer;
        try {

            String ratingString = _fileToPlay.getName() + " " + Integer.toString(rating);

            BufferedReader br = new BufferedReader(new FileReader(DirectoryManager.get_ratings()));
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
                writer = new BufferedWriter(new FileWriter(DirectoryManager.get_ratings(), true));
                writer.write(ratingString + "\n");
                writer.close();

                //if a rating does exist, replace the old rating
            } else {
                String old = "";
                BufferedReader reader = new BufferedReader(new FileReader(DirectoryManager.get_ratings()));
                String line2 = reader.readLine();

                while (line2 != null) {
                    old = old + line2 + System.lineSeparator();
                    line2 = reader.readLine();
                }
                String newContent = old.replaceAll(_fileToPlay.getName() + " [12345]", ratingString);
                FileWriter writer2 = new FileWriter(DirectoryManager.get_ratings());
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
            BufferedReader br = new BufferedReader(new FileReader(DirectoryManager.get_ratings()));
            String line;
            updateStars(0);
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

    /**
     * Updates the stars for the rating functionality to display the rating
     * @param rating an integer specifying the number of stars to enable
     */
    private void updateStars(int rating) {
        List<ImageView> stars = new ArrayList<>();
        stars.add(_star1);
        stars.add(_star2);
        stars.add(_star3);
        stars.add(_star4);
        stars.add(_star5);

        for (ImageView star : stars) {
            star.setImage(_noTouch);
        }

        for (int i = 1; i <= rating; i++) {
            stars.get(i - 1).setImage(_touch);
        }
    }

    /**
     * changes the creations based on what Name is selected
     * @param creation the Name for which the PlayView scene needs to be rendered
     */
    private void loadCreation(Name creation) {

        _currentName.setText(creation.getName().replaceAll("_"," "));
        loadVersionsOfCreation(creation);
        loadPreviousUserRecordings();
        updateRating();

    }

    /**
     * Loads all _versions of the name that is selected into the combo box
     * @param creation The Name for which _versions need to be loaded
     */
    private void loadVersionsOfCreation(Name creation) {

        _versions.getItems().clear();

        _versionPerms = creation.getVersions();
        _fileToPlay = _versionPerms.get("Version 1");

        for (int i = 0; i < _versionPerms.size(); i++) {
            _versions.getItems().add(new Label("Version " + (i + 1)));
        }

        _versions.setConverter(new StringConverter<Label>() {
            @Override
            public String toString(Label object) {
                return object == null ? "" : object.getText();
            }

            @Override
            public Label fromString(String string) {
                return new Label(string);
            }
        });

        _versions.setEditable(false);
        _versions.getSelectionModel().selectFirst();


    }

    /**
     * Loads the previous user creations into the "Previous Attempts" list
     */
    private void loadPreviousUserRecordings() {
        _previousAttempts.getItems().clear();
        File folder = new File(NameSayer.userRecordingsPath);
        File[] files = folder.listFiles();
        _userFiles = new HashMap<>();
        if (folder.isDirectory()) {
            for (File file : files) {
                Name tempName = new Name(file);
                Name currentFile = new Name(_fileToPlay);

                String compareString = currentFile.getName() + "_V";
                compareString = compareString.toLowerCase();
                String currentFileName = tempName.getName().replaceAll("\\d$", "").toLowerCase();
                if (currentFileName.equals(compareString) && tempName.isValid()) {
                    String fileName = tempName.getName().replace("_", " ");
                    _previousAttempts.getItems().add(fileName);
                    _userFiles.put(fileName, file);
                }
            }
        }

    }

    /**
     * Loads the next Name to be practiced in the creations list.
     * Changes to "Finish" if the current creation is the last on in the list
     */
    @FXML
    private void nextButtonHandler() {
        _currentSelection++;
        if (_currentSelection < _creationsList.size()) {
            _currentLoadedCreation = _creationsList.get(_currentSelection);
            loadCreation(_creationsList.get(_currentSelection));
            _previousButton.setText("< Previous");
        }
        if (_currentSelection == _creationsList.size() - 1) {
            _nextButton.setText("Finish >");
        }
        if (_currentSelection == _creationsList.size()) {
            loadMainMenuView();
        }
    }

    /**
     * Loads the previous Name to be practiced in the creations list.
     * Changes to "Menu" if the current creation is the first on in the list
     */
    @FXML
    private void backButtonHandler() {
        _currentSelection--;

        if (_currentSelection >= 0) {
            _currentLoadedCreation = _creationsList.get(_currentSelection);
            loadCreation(_creationsList.get(_currentSelection));
            _nextButton.setText("Next >");
        }
        if (_currentSelection == 0) {
            _previousButton.setText("< Menu");
        }
        if (_currentSelection < 0) {
            loadMainMenuView();
        }
    }


    /**
     * Loads the main menu for NameSayer
     */
    @FXML
    private void loadMainMenuView() {
        _creationsList.clear();
        _currentSelection = 0;
        switchController("SearchNamesViewController.fxml", _anchorPane);
    }

    /**
     * Sets the list of names to be practised in the PlayViewController
     * @param creationsList
     * @return
     */
    public static List<String> setCreationsList(List<String> creationsList) {
        try {
            AudioConcat.deleteAllFiles();
        }catch (IOException e) {
            System.out.println(e.getStackTrace());
        }

        List<String> notFoundNames = new ArrayList<>();

        for (String name:creationsList) {
            List<String> brokenName = new ArrayList<>();
            String[] array = name.split(" ");
            for (String partName : array) {
                brokenName.add(partName);
            }
            try {
                AudioConcat concatNames = new AudioConcat(brokenName);
                concatNames.concatenate();
            } catch (InterruptedException e) {
                System.out.println("Interupt" + e.getMessage());
            } catch (IOException w) {
                notFoundNames.add(brokenName + " in " + name);
            }
        }
        addNamesToCreationsList(creationsList);

        return notFoundNames;
    }

    /**
     * A method to check whether the list of creations is empty
     * @return true if the creations list is not empty
     */
    public static boolean creationsExist(){
        return (!_creationsList.isEmpty());
    }

    /**
     * adds all unique names from the cancatenated names directory into the PlayViewController's list of names to be
     * practiced, in the order of the creationsList
     */
    private static void addNamesToCreationsList(List<String> creationsList){
        File[] directory = new File(NameSayer.concatenatedNamesPath).listFiles();

        if (directory != null && directory.length > 0) {
            for (String s : creationsList) {
                for (File file : directory) {
                    if (!file.isDirectory()) {
                        String currentString = s.replaceAll(" ", "_");
                        Name currentName = new Name(currentString, new File(NameSayer.concatenatedNamesPath));
                        //if the name has NOT already been added
                        if (!_creationsList.contains(currentName)) {
                            _creationsList.add(currentName);
                        }
                    }
                }
            }
        }
    }


    /**
     * adds all unique names from the cancatenated names directory into the PlayViewController's list of names to be
     * practiced
     */
    private static void addNamesToCreationsList(){
        File[] directory = new File(NameSayer.concatenatedNamesPath).listFiles();
        for (File file : directory) {
            if (!file.isDirectory()) {
                Name currentName = new Name(file, new File(NameSayer.concatenatedNamesPath));
                //if the name has NOT already been added
                if (!_creationsList.contains(currentName)) {
                    _creationsList.add(currentName);
                }
            }
        }
    }

    @FXML
    public void demoButtonHandler() {

        playFile(_fileToPlay);
    }

    /**
     * Plays an audio file using the ffplay module
     * @param fileToPlay the file that is intended to be played
     */
    private void playFile(File fileToPlay) {
        setPlayBar(fileToPlay);
        _previousAttempts.setDisable(true);
        _demoButton.setDisable(true);
        _recordButton.setDisable(true);
        _micTestButton.setDisable(true);
        _nextButton.setDisable(true);
        _previousButton.setDisable(true);

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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        _previousAttempts.setDisable(false);
                        _demoButton.setDisable(false);
                        _recordButton.setDisable(false);
                        _micTestButton.setDisable(false);
                        _nextButton.setDisable(false);
                        _previousButton.setDisable(false);

                        // Score is increased by 1 for listening to a database name
                        NameSayer.currentUser.increaseListenNameScore(1);


                    }
                });

            }
        };
        monitorThread.start();
    }

    /**
     * Sets the correct file to play when combo box selection is changed
     */
    @FXML
    public void versionSelectionHandler(ActionEvent e) {
        if (_versions.getSelectionModel().getSelectedItem() != null) {
            _fileToPlay = _versionPerms.get(_versions.getSelectionModel().getSelectedItem().getText());
        }
        updateRating();
    }

    /**
     * Loads the mic test view
     */
    @FXML
    public void micTestButtonHandler() {
        switchController("MicTestViewController.fxml", _anchorPane);
    }

    /**
     * Loads the user recording creation view
     */
    @FXML
    public void recordButtonHandler() {

        NewCreationViewController.setDatabaseName(_fileToPlay);
        NewCreationViewController.setNameOfCreation(_currentLoadedCreation.getName());
        switchController("NewCreationViewController.fxml", _anchorPane);

    }

    /**
     * Selection handler for the ListView for previous user attempts. Plays the selected file.
     */
    @FXML
    public void playUserCreatedFile() {

        String file = _previousAttempts.getSelectionModel().getSelectedItems().toString();
        file = file.replace("[", "");
        file = file.replace("]", "");
        if (!file.equals("")) {
            playFile(_userFiles.get(file));
        }
    }

    /**
     * Handlers for each of the stars in the rating system
     */

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
