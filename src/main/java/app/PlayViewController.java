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
    private JFXComboBox<Label> versions;

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

    @FXML
    private StackPane stackPane;

    private static List<Name> _creationsList = new ArrayList<>();
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
        stackPane.setDisable(true);
        if(_creationsList.size() > 0 ) {
            currentLoadedCreation = _creationsList.get(currentSelection);
            loadCreation(currentLoadedCreation);
        }
        if(currentSelection==0) {
            previousButton.setText("< Menu");
        }
        if (_creationsList.size() == 1) {
            nextButton.setText("Finish >");
        }
    }

    /**
     * Allows the user to set a rating onto different versions of different names
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
     * changes the creations based on what Name is selected
     * @param creation the Name for which the PlayView scene needs to be rendered
     */
    private void loadCreation(Name creation) {

        currentName.setText(creation.getName().replaceAll("_"," "));
        loadVersionsOfCreation(creation);
        loadPreviousUserRecordings();
        updateRating();

    }

    /**
     * Loads all versions of the name that is selected into the combo box
     * @param creation The Name for which versions need to be loaded
     */
    private void loadVersionsOfCreation(Name creation) {

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

    /**
     * Loads the previous user creations into the "Previous Attempts" list
     */
    private void loadPreviousUserRecordings() {
        previousAttempts.getItems().clear();
        File folder = new File(NameSayer.userRecordingsPath);
        File[] files = folder.listFiles();
        userFiles = new HashMap<>();
        if (folder.isDirectory()) {
            for (File file : files) {
                Name tempName = new Name(file);
                Name currentFile = new Name(_fileToPlay);

                String compareString = currentFile.getName() + "_V";
                compareString = compareString.toLowerCase();
                String currentFileName = tempName.getName().replaceAll("\\d$", "").toLowerCase();
                if (currentFileName.equals(compareString) && tempName.isValid()) {
                    String fileName = tempName.getName().replace("_", " ");
                    previousAttempts.getItems().add(fileName);
                    userFiles.put(fileName, file);
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
        currentSelection++;
        if (currentSelection < _creationsList.size()) {
            currentLoadedCreation = _creationsList.get(currentSelection);
            loadCreation(_creationsList.get(currentSelection));
            previousButton.setText("< Previous");
        }
        if (currentSelection == _creationsList.size() - 1) {
            nextButton.setText("Finish >");
        }
        if (currentSelection == _creationsList.size()) {
            loadMainMenuView();
        }
    }

    /**
     * Loads the previous Name to be practiced in the creations list.
     * Changes to "Menu" if the current creation is the first on in the list
     */
    @FXML
    private void backButtonHandler() {
        currentSelection--;

        if (currentSelection >= 0) {
            currentLoadedCreation = _creationsList.get(currentSelection);
            loadCreation(_creationsList.get(currentSelection));
            nextButton.setText("Next >");
        }
        if (currentSelection == 0) {
            previousButton.setText("< Menu");
        }
        if (currentSelection < 0) {
            loadMainMenuView();
        }
    }


    /**
     * Loads the main menu for NameSayer
     */
    @FXML
    private void loadMainMenuView() {
        _creationsList.clear();
        currentSelection = 0;
        switchController("SearchNamesViewController.fxml",anchorPane);
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
        previousAttempts.setDisable(true);
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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        previousAttempts.setDisable(false);
                        demoButton.setDisable(false);
                        recordButton.setDisable(false);
                        micTestButton.setDisable(false);
                        nextButton.setDisable(false);
                        previousButton.setDisable(false);

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
        if (versions.getSelectionModel().getSelectedItem() != null) {
            _fileToPlay = versionPerms.get(versions.getSelectionModel().getSelectedItem().getText());
        }
        updateRating();
    }

    /**
     * Loads the mic test view
     */
    @FXML
    public void micTestButtonHandler() {
        switchController("MicTestViewController.fxml", anchorPane);
    }

    /**
     * Loads the user recording creation view
     */
    @FXML
    public void recordButtonHandler() {

        NewCreationViewController.setDatabaseName(_fileToPlay);
        NewCreationViewController.setNameOfCreation(currentLoadedCreation.getName());
        switchController("NewCreationViewController.fxml", anchorPane);

    }

    /**
     * Selection handler for the ListView for previous user attempts. Plays the selected file.
     */
    @FXML
    public void playUserCreatedFile() {

        String file = previousAttempts.getSelectionModel().getSelectedItems().toString();
        file = file.replace("[", "");
        file = file.replace("]", "");
        if (!file.equals("")) {
            playFile(userFiles.get(file));
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
