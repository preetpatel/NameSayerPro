/**
 * SearchNamesViewController.java
 * Scene for selecting creations
 * <p>
 * Copyright Preet Patel, 2018
 *
 * @Author Preet Patel
 * @Author Chuyang Chen
 * Date Created: 19 August, 2018
 */

package app;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.*;
import java.util.*;
import java.util.List;

import static javafx.scene.layout.StackPane.setAlignment;

public class SearchNamesViewController extends Controller{

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXMasonryPane addedCreationsPane;

    @FXML
    private ScrollPane addedScrollPane;

    @FXML
    private JFXTextField searchField;

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton _changeDatabaseButton;

    @FXML
    private JFXButton startPracticeButton;

    @FXML
    private JFXButton removeButton;

    @FXML
    private JFXButton profileButton;

    @FXML
    private JFXPopup userPopup = new JFXPopup();

    @FXML
    private JFXButton removeAllButton;

    private ObservableList<JFXButton> creationsButtonList = FXCollections.observableArrayList();

    private ObservableList<JFXButton> selectedButtonsList = FXCollections.observableArrayList();

    private List<Name> creationsList = new ArrayList<>();

    private List<String> selectedNames = new ArrayList<>();

    private List<String> databaseNames = new ArrayList<>();

    private List<String> concatSafeNames = new ArrayList<>();

    private AutoCompletionBinding<String> searchBinding;

    private File uploadList = null;

    /**
     * Method that makes stack pane invisible on startup to prevent conflicting with the GUI.
     * Initialises properties of the scroll view
     */
    @FXML
    private void initialize() {

        DirectoryManager manager = new DirectoryManager();
        manager.runChecks();

        loadCreationsOntoPane();

        searchBinding = TextFields.bindAutoCompletion(searchField, concatSafeNames);

        // Add Enter key listener on search field
        searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    addButtonHandler(new ActionEvent());
                }
            }
        });
        searchField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                char compare = 'a';
                if (searchField.getLength() > 0) {
                    compare = searchField.getText().charAt(searchField.getLength() - 1);
                }
                    if (compare == ' ' || searchField.getLength() == 0) {
                        int i = 0;
                        concatSafeNames.clear();
                        while (i < databaseNames.size()) {
                            concatSafeNames.add(searchField.getText() + databaseNames.get(i));
                            i++;
                        }
                        searchBinding.dispose();
                        searchBinding = TextFields.bindAutoCompletion(searchField,concatSafeNames);
                    }
            }
        });

        addedScrollPane.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaX() != 0) {
                    event.consume();
                }
            }
        });

    }

    @FXML
    /**
     * Allows the user to change their database directory
     */
    private void changeDatabaseButtonHandler(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a new Database");
        File defaultDirectory = new File(NameSayer.audioPath);
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog((Stage)anchorPane.getScene().getWindow());
        if (selectedDirectory.isDirectory()){
            NameSayer.audioPath = selectedDirectory.getPath();
            switchController("SearchNamesViewController.fxml", anchorPane);
        }
    }

    /**
     * Initialises the left pane to show every existing wav file in the directory
     */
    private void loadCreationsOntoPane() {
        stackPane.setVisible(false);

        File folder = new File(NameSayer.audioPath);
        File[] files = folder.listFiles();

        for (File file : files) {
            Name tempName = new Name(file);

            // Adds names to the database that suggests names in the search field
            String dataName = tempName.getName();
            dataName = dataName.substring(0,1).toUpperCase() + dataName.substring(1);
            if (!databaseNames.contains(dataName)) {
                databaseNames.add(dataName);
                concatSafeNames.add(dataName);
            }
        }
    }

    /**
     * Allows removal of all the buttons selected by the user
     */
    @FXML
    private void removeButtonHandler(ActionEvent e) {


        for (JFXButton button : selectedButtonsList) {
            creationsButtonList.remove(button);

            Iterator<Name> creations = creationsList.iterator();
            while (creations.hasNext()) {

                JFXButton comparedButton = creations.next().getButton();

                if (button.equals(comparedButton)) {
                    creations.remove();
                    creationsButtonList.remove(comparedButton);

                    break;
                }
            }
        }

        addedCreationsPane.getChildren().clear();
        addedCreationsPane.getChildren().addAll(creationsButtonList);

        // Set remove button to invisible if list has no creations left
        if (creationsList.isEmpty()) {
            removeButton.setVisible(false);
            startPracticeButton.setVisible(false);
            removeAllButton.setVisible(false);
        }
    }

    /**
     * removes all the buttons from the list
     * @param e
     */
    @FXML
    private void removeAllButtonHandler(ActionEvent e) {
        creationsList.clear();
        creationsButtonList.clear();
        selectedButtonsList.clear();

        addedCreationsPane.getChildren().clear();
        addedCreationsPane.getChildren().addAll(creationsButtonList);
        removeButton.setVisible(false);
        startPracticeButton.setVisible(false);
        removeAllButton.setVisible(false);
    }


    @FXML
    /**
     * Allows adding creations to the list of creations to be played
     */
    private void addButtonHandler(ActionEvent e) {

        stackPane.setVisible(false);
        stackPane.getChildren().clear();
        searchField.setDisable(true);

        String searchedItems = searchField.getText().trim();
        addNamesToList(searchedItems, true);

    }

    /**
     * adds given searched item to the list of names
     *
     * @param searchedItems item to be added
     * @param errors if we want error messages or not
     * @return true if file is successfully added
     */
    private boolean addNamesToList(String searchedItems, boolean errors){
        addedCreationsPane.getChildren().clear();

        if (searchedItems.equals("")) {
            showErrorDialog("Please enter a valid name", "Ok");
            addedCreationsPane.getChildren().addAll(creationsButtonList);
        } else {

            File folder = new File(NameSayer.audioPath);
            File[] files = folder.listFiles();
            boolean fileFound = false;
            String processedSearchedItems = searchedItems.replaceAll("-", " ");
            String[] names = processedSearchedItems.split(" ");

            for (String name: names) {
                fileFound = false;
                for (File file : files) {
                    Name tempName = new Name(file);
                    if (name.toLowerCase().equals(tempName.getName().toLowerCase()) && tempName.isValid()) {
                        fileFound = true;
                    }
                }
                if (!fileFound) {
                    break;
                }
            }

            if (fileFound) {
                Name name = new Name(processedSearchedItems);

                boolean buttonExists = false;

                //see if that item has already been added to the list
                for (JFXButton currentButton : creationsButtonList) {
                    if (processedSearchedItems.toLowerCase().equals(currentButton.getId().toLowerCase())) {
                        buttonExists = true;
                    }
                }

                if (!buttonExists) {
                    JFXButton button = name.generateButton(selectedButtonsList);
                    creationsButtonList.add(button);
                    creationsList.add(name);
                    startPracticeButton.setVisible(true);
                    removeButton.setVisible(true);
                    removeAllButton.setVisible(true);

                } else if (errors){
                    showErrorDialog("This name has already been added", "Ok");
                }

                searchField.setDisable(false);

            } else if (errors){
                // If no name is found
                showErrorDialog("This name could not be found on the database", "Ok");
                return false;
            }
            addedCreationsPane.getChildren().addAll(creationsButtonList);
            searchField.setText("");
            searchBinding.dispose();
            return true;
        }
        return false;
    }

    /**
     * allows user to upload a text file as the database
     */
    @FXML
    private void uploadButtonHandler() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text file", "*.txt"));
        File result = fileChooser.showOpenDialog((Stage)anchorPane.getScene().getWindow());
        if (result != null) {
            uploadList = result;
            try {
                BufferedReader br = new BufferedReader(new FileReader(uploadList));
                String line;
                boolean success = true;
                boolean atLeastOneFailure = false;
                //scan through the entire text file
                while ((line = br.readLine()) != null) {
                    success = addNamesToList(line, false);
                    if (!success){
                        atLeastOneFailure = true;
                    }
                }

                if (atLeastOneFailure){
                    showErrorDialogOnStackpane("Some names in the list could not be added because they were invalid", "OK",stackPane);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Allows the user to pick between starting their practice with a randomised list or a non randomised list
     */
    @FXML
    private void startPracticeHandler(ActionEvent e) {

        for (Name name: creationsList) {
            selectedNames.add(name.getName());
        }

        //if more than 1 selected creations
        if (addedCreationsPane.getChildren().size() > 1) {
            stackPane.setVisible(true);
            stackPane.getChildren().clear();
            //create popup
            JFXDialogLayout dialogContent = new JFXDialogLayout();
            JFXDialog randomiseDialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

            Text header = new Text("Do you wish to randomise the list order?");
            header.setStyle("-fx-font-size: 30; -fx-font-family: 'Lato Heavy'");
            dialogContent.setHeading(header);

            //button for randomisation
            JFXButton confirmRandomise = new JFXButton();
            confirmRandomise.setText("Randomise and play");
            confirmRandomise.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
            confirmRandomise.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    randomiseDialog.close();
                    stackPane.setVisible(false);
                    if (uploadList != null) {
                        PlayViewController.setCreationsListFromFile(uploadList);
                    } else if (creationsList.size() != 0) {
                        Collections.shuffle(selectedNames);
                        PlayViewController.setCreationsList(selectedNames);
                    }
                    if (PlayViewController.creationsExist()) {
                        switchController("PlayViewController.fxml", anchorPane);
                    } else {
                        showErrorDialogOnStackpane("Please enter at least one existing creation in your text file!", "OK", stackPane);
                    }
                }
            });

            //button for normal play
            JFXButton confirmPlay = new JFXButton();
            confirmPlay.setText("Play");
            confirmPlay.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
            confirmPlay.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    randomiseDialog.close();
                    stackPane.setVisible(false);
                    if (uploadList != null) {
                        PlayViewController.setCreationsListFromFile(uploadList);
                    } else if (creationsList.size() != 0) {
                        PlayViewController.setCreationsList(selectedNames);
                    }
                    if (PlayViewController.creationsExist()) {
                        switchController("PlayViewController.fxml", anchorPane);
                    } else {
                        showErrorDialogOnStackpane("Please enter at least one existing creation in your text file!", "OK", stackPane);
                    }
                }
            });

            setAlignment(confirmRandomise, Pos.BASELINE_RIGHT);
            setAlignment(confirmPlay, Pos.BASELINE_LEFT);

            randomiseDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
                @Override
                public void handle(JFXDialogEvent event) {
                    stackPane.setVisible(false);
                }
            });

            dialogContent.setActions(confirmRandomise, confirmPlay);
            randomiseDialog.show();

            //for uploaded lists
        } else {
            if (uploadList != null) {
                PlayViewController.setCreationsListFromFile(uploadList);
            } else if (creationsList.size() != 0) {
                PlayViewController.setCreationsList(selectedNames);
            }
            if (PlayViewController.creationsExist()) {
                switchController("PlayViewController.fxml", anchorPane);
            } else {
                showErrorDialogOnStackpane("Please enter at least one existing creation in your text file!", "OK", stackPane);
            }
        }
    }

    /**
     * makes an error popup on the window
     * @param headerText
     * @param buttonText
     */
    private void showErrorDialog(String headerText, String buttonText) {
        searchField.setDisable(true);
        stackPane.setVisible(true);
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        JFXDialog deleteDialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

        Text header = new Text(headerText);
        header.setStyle("-fx-font-size: 30; -fx-font-family: 'Lato Heavy'");
        dialogContent.setHeading(header);

        JFXButton confirmDelete = new JFXButton();
        confirmDelete.setText(buttonText);
        confirmDelete.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
        confirmDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteDialog.close();
                searchField.setDisable(false);
                stackPane.setVisible(false);

            }
        });

        deleteDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
            @Override
            public void handle(JFXDialogEvent event) {
                stackPane.setVisible(false);
                searchField.setDisable(false);
            }
        });

        dialogContent.setActions(confirmDelete);
        deleteDialog.show();
    }


    /**
     * loads the profile view
     */
    @FXML
    public void profileButtonHandler() {
        JFXButton logoutButton = new JFXButton("Logout");
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                userPopup.hide();
                NameSayer.performUserLogout();
                switchController("LoginViewController.fxml", anchorPane);
            }
        });

        JFXButton openProfileButton = new JFXButton("Profile");
        openProfileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                userPopup.hide();
                switchController("ProfileViewController.fxml", anchorPane);
            }
        });

        VBox vbox = new VBox(openProfileButton, logoutButton);
        userPopup.setPopupContent(vbox);
        userPopup.show(profileButton, JFXPopup.PopupVPosition.BOTTOM, JFXPopup.PopupHPosition.LEFT);
    }
}
