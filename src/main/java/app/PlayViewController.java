/**
 * PlayViewController.java
 * Scene for playing a selected creation
 *
 * Copyright Preet Patel, 2018
 * @Author Preet Patel
 * @Auther Chuyang Chen
 * Date Created: 13 August, 2018
 */

package app;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import javax.swing.*;
import java.io.*;
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
    private  JFXButton nextButton;

    @FXML
    private JFXComboBox<Label> versions;

    @FXML
    private JFXListView<String> previousAttempts;

    @FXML
    private javafx.scene.control.TextField ratingText;

    @FXML
    private JFXButton confirmRatingButton;

    private static List<Name> _creationsList;
    private MediaPlayer mediaPlayer;
    private Name currentLoadedCreation;
    private static int currentSelection = 0;
    private HashMap<String, File> versionPerms;
    private HashMap<String, File> userFiles;
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

        ratingText.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 1 ? change : null));
    }

    /**
     *  Allows the user to set a rating onto different versions of different names
     */
    @FXML
    private void ratingButtonHandler(){
        String userRating = ratingText.getText();
        BufferedWriter writer;
        try {

            int ratingNumber = Integer.parseInt(userRating);
            if (ratingNumber > 5 | ratingNumber < 1){
                throw new NumberFormatException();
            }

            String ratingString = _fileToPlay.getName() + " " + Integer.toString(ratingNumber);

            BufferedReader br = new BufferedReader(new FileReader(DirectoryManager.getRatings()));
            String line;

            //check if file is already given a rating
            boolean lineExists = false;
            while ((line = br.readLine()) != null) {
                    if (line.contains(_fileToPlay.getName())){
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

                while (line2 != null)
                {
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
        } catch (IOException e2){
            JOptionPane.showMessageDialog(null, "An error occurred printing rating", "Error", JOptionPane.ERROR_MESSAGE);
        }


    }

    /**
     * reads the text file in which ratings are stored, which is then displayed onto the GUI
     */
    private void updateRating(){
        try {
            BufferedReader br = new BufferedReader(new FileReader(DirectoryManager.getRatings()));
            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains(_fileToPlay.getName())) {
                    String[] nameCreation = line.split("\\s+");
                    ratingText.setText(nameCreation[1]);
                    break;
                } else {
                    ratingText.setText("");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred printing rating", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * changes the creations
     * @param creation
     */
    private void loadCreation(Name creation){
        currentName.setText(creation.getName());

        loadVersionsOfCreation(creation);

        loadPreviousUserRecordings();

        updateRating();

    }

    /**
     * Processes the different version files of the given name to allow it to be loaded
     * @param creation the name to be loaded and processed
     */
    private void loadVersionsOfCreation(Name creation){

        currentName.setText(creation.getName());
        versions.getItems().clear();

        versionPerms = creation.getVersions();
        _fileToPlay = versionPerms.get("Version 1");

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


    }

    /**
     * Causes the user's recordings of the name to be loaded onto the list
     */
    private void loadPreviousUserRecordings(){
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

    /**
     * Allows the nextButton to take the user to the next name to practice
     */
    @FXML
    public void nextButtonHandler(){
        currentSelection++;
        if(currentSelection < _creationsList.size()) {
            currentLoadedCreation = _creationsList.get(currentSelection);
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

    /**
     * Allows the backButton to take the user to the previous name to practice
     */
    @FXML
    public void backButtonHandler(){
        //change the current selection index
        currentSelection--;
        if(currentSelection >= 0 ) {
            currentLoadedCreation = _creationsList.get(currentSelection);
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

    /**
     * Returns to the main menu
     */
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

    /**
     * sets the list of names that are to be practiced
     * @param creationsList
     */
    public static void setCreationsList(List<Name> creationsList){
        _creationsList = creationsList;
    }

    /**
     * Allows the demo button to play the current version of the name that is chosen by the user
     */
    @FXML
    public void demoButtonHandler() {
        //change buttons so user cannot perform unexpected behaviour
        demoButton.setDisable(true);
        recordButton.setDisable(true);
        micTestButton.setDisable(true);
        nextButton.setDisable(true);
        previousButton.setDisable(true);

        Thread monitorThread = new Thread() {
            @Override
            public void run() {
                try{
                    Media media = new Media(_fileToPlay.toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setOnReady(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.play();
                        }
                    });
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                //reenable buttons once playing is done
                demoButton.setDisable(false);
                recordButton.setDisable(false);
                micTestButton.setDisable(false);
                nextButton.setDisable(false);
                previousButton.setDisable(false);
            }
        };
        monitorThread.start();
    }

    /**
     *    Sets the correct file to play when combo box selection is changed
     */

    @FXML
    public void versionSelectionHandler(ActionEvent e) {
        if (versions.getSelectionModel().getSelectedItem() != null) {
            _fileToPlay = versionPerms.get(versions.getSelectionModel().getSelectedItem().getText());
        }
        updateRating();
    }

    /**
     * Functionality for the button which switches the scene to the microphone testing GUI
     */
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

    /**
     * Functionality for the button which switches the scene to the recording GUI
     */
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
