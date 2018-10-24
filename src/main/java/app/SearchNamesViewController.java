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
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import Utils.MessageBox.ModalBox;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

import java.io.*;
import java.util.*;
import java.util.List;

import static javafx.scene.layout.StackPane.setAlignment;

public class SearchNamesViewController extends Controller{

    @FXML private AnchorPane anchorPane;
    @FXML private Text mainText;
    @FXML private StackPane stackPane;
    @FXML private JFXMasonryPane addedCreationsPane;
    @FXML private ScrollPane addedScrollPane;
    @FXML private CustomTextField searchField;
    @FXML private JFXButton addButton;
    @FXML private JFXButton _changeDatabaseButton;
    @FXML private JFXButton startPracticeButton;
    @FXML private JFXButton removeButton;
    @FXML private JFXButton profileButton;
    @FXML private JFXPopup userPopup = new JFXPopup();
    @FXML private JFXButton removeAllButton;
    private ObservableList<JFXButton> creationsButtonList = FXCollections.observableArrayList();
    private ObservableList<JFXButton> selectedButtonsList = FXCollections.observableArrayList();
    private List<Name> creationsList = new ArrayList<>();
    private List<String> selectedNames = new ArrayList<>();
    private List<String> databaseNames = new ArrayList<>();
    private List<String> concatSafeNames = new ArrayList<>();
    private AutoCompletionBinding<String> searchBinding;
    private SuggestionProvider<String> _DatabaseConcatProvider = SuggestionProvider.create(concatSafeNames);
    private File uploadList = null;

    /**
     * Method that makes stack pane invisible on startup to prevent conflicting with the GUI.
     * Initialises properties of the scroll view
     */
    @FXML
    private void initialize() {
        DirectoryManager manager = new DirectoryManager();
        manager.runChecks();

        loadCreations();
        disableHorizontalScrolling();
        initializeSearchField();
    }

    /**
     * Initialise search field with autocomplete
     */
    private void initializeSearchField() {
        searchBinding = TextFields.bindAutoCompletion(searchField, _DatabaseConcatProvider);
        _DatabaseConcatProvider.clearSuggestions();
        _DatabaseConcatProvider.addPossibleSuggestions(concatSafeNames);
        searchBinding.setHideOnEscape(true);

        // Add Enter key listener on search field
        searchField.setOnKeyPressed(event ->  {
            if (event.getCode() == KeyCode.ENTER) {
                addButtonHandler(new ActionEvent());
            }
        });

        searchField.setOnKeyReleased(event ->  {
            // arbitrary char assignment
            char compare = 'a';

            if (searchField.getLength() > 0) {
                compare = searchField.getText().charAt(searchField.getLength() - 1);
            }
            if (compare == ' ' || searchField.getLength() == 0) {
                concatSafeNames.clear();
                int i = 0;
                while (i < databaseNames.size()) {
                    concatSafeNames.add(searchField.getText() + databaseNames.get(i));
                    i++;
                }
                _DatabaseConcatProvider.clearSuggestions();
                _DatabaseConcatProvider.addPossibleSuggestions(concatSafeNames);
            }

        });
    }

    /**
     * Disables being able to scroll right on the list of added panes
     */
    public void disableHorizontalScrolling() {
        addedScrollPane.addEventFilter(ScrollEvent.SCROLL, event ->  {
                if (event.getDeltaX() != 0) {
                    event.consume();
                }
        });
    }

    /**
     * Allows the user to change their database directory
     */
    @FXML private void changeDatabaseButtonHandler() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a new Database");
        File defaultDirectory = new File(NameSayer.audioPath);
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(anchorPane.getScene().getWindow());
        if (selectedDirectory != null && selectedDirectory.isDirectory()) {
            int filesCount = 0;
            File[] files = selectedDirectory.listFiles();
            if(files != null) {
                for (File file : files) {
                    if (FilenameUtils.getExtension(file.getName()).equals("wav")) {
                        filesCount++;
                    }
                }
            }
            if (filesCount > 0) {
                searchField.setDisable(true);
                stackPane.setVisible(true);
                String filesFound = "There were " + filesCount + " valid files found";
                ModalBox modalBox = new ModalBox(stackPane, filesFound, "Ok");
                modalBox.setHandlers(event ->  {
                        NameSayer.audioPath = selectedDirectory.getPath();
                        Platform.runLater(() -> {
                                switchController("SearchNamesViewController.fxml", anchorPane);
                        });
                });
                modalBox.showDialog();

            }
        }
    }

    /**
     * Initialises the lists that contain names of valid files
     */
    private void loadCreations() {
        stackPane.setVisible(false);

        File folder = new File(NameSayer.audioPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                Name tempName = new Name(file);

                // Adds names to the database that suggests names in the search field
                String dataName = tempName.getName();
                dataName = dataName.substring(0, 1).toUpperCase() + dataName.substring(1);
                if (!databaseNames.contains(dataName)) {
                    databaseNames.add(dataName);
                    concatSafeNames.add(dataName);
                }
            }
        } else {
            showErrorDialog("There was an error loading the database. The app may behave unexpectedly","Ok");
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

        // Replace every redundant space in the name
        searchedItems = searchedItems.replaceAll("\\w[ ]{2,}\\w", " ");

        // Convert every word's first character to uppercase
        char[] array = searchedItems.toCharArray();
        if (array.length > 0) {
            array[0] = Character.toUpperCase(array[0]);
        }

        for (int i = 1; i < array.length; i++) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i]);
            }
        }
        searchedItems = new String(array);

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

            //if the file is found
            if (fileFound) {
                Name name = new Name(processedSearchedItems);
                boolean buttonExists = false;

                //see if that item has already been added to the list
                for (JFXButton currentButton : creationsButtonList) {
                    if (processedSearchedItems.toLowerCase().equals(currentButton.getId().toLowerCase())) {
                        buttonExists = true;
                    }
                }

                //add the button to the list of buttons if no button already exists
                if (!buttonExists) {
                    JFXButton button = name.generateButton(selectedButtonsList);
                    creationsButtonList.add(button);
                    creationsList.add(name);
                    startPracticeButton.setVisible(true);
                    removeButton.setVisible(true);
                    removeAllButton.setVisible(true);
                } else if (errors){
                    showErrorDialog("This name has already been added", "Ok");
                    addButtonsToListAndClearSearchfield();
                    return false;
                } else {
                    addButtonsToListAndClearSearchfield();
                    return false;
                }

                searchField.setDisable(false);

            } else if (errors){
                // If no name is found
                showErrorDialog("This name could not be found on the database", "Ok");
                addButtonsToListAndClearSearchfield();
                return false;
            } else {
                addButtonsToListAndClearSearchfield();
                return false;
            }

            addButtonsToListAndClearSearchfield();
            return true;
        }
        return false;
    }

    /**
     * Adds all buttons to the masonary pane and clears the search field
     */
    private void addButtonsToListAndClearSearchfield(){
        addedCreationsPane.getChildren().addAll(creationsButtonList);
        searchField.setText("");
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
;
                boolean atLeastOneFailure = false;
                //scan through the entire text file
                int notFoundNamesNumber = 0;
                while ((line = br.readLine()) != null) {
                    boolean success = addNamesToList(line, false);
                    if(!success){
                        notFoundNamesNumber++;
                    }
                }

                if (notFoundNamesNumber>0){
                    showErrorDialog(Integer.toString(notFoundNamesNumber) +" name(s) could not be added because they were invalid", "OK");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private boolean _loading = false;


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
            confirmRandomise.setOnAction(event -> {

                    randomiseDialog.close();
                    stackPane.setVisible(false);
                    if (creationsList.size() != 0) {
                        Collections.shuffle(selectedNames);
                        PlayViewController.setCreationsList(selectedNames);
                    }
                    if (PlayViewController.creationsExist()) {
                        switchController("PlayViewController.fxml", anchorPane);
                    } else {
                        showErrorDialogOnStackpane("Please enter at least one existing creation in your text file!", "OK", stackPane);
                    }
            });

            //button for normal play
            JFXButton confirmPlay = new JFXButton();
            confirmPlay.setText("Play");
            confirmPlay.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
            confirmPlay.setOnAction(event -> {

                    mainText.setText("This wont be long");
                    randomiseDialog.close();
                    stackPane.setVisible(false);
                    if (creationsList.size() != 0) {
                        PlayViewController.setCreationsList(selectedNames);
                    }
                    if (PlayViewController.creationsExist()) {
                        switchController("PlayViewController.fxml", anchorPane);
                    } else {
                        showErrorDialogOnStackpane("Please enter at least one existing creation in your text file!", "OK", stackPane);
                    }
            });

            setAlignment(confirmRandomise, Pos.BASELINE_RIGHT);
            setAlignment(confirmPlay, Pos.BASELINE_LEFT);

            randomiseDialog.setOnDialogClosed(event -> {
                stackPane.setVisible(false);
            });

            dialogContent.setActions(confirmRandomise, confirmPlay);
            randomiseDialog.show();

        //for list with single name
        } else {
            if (creationsList.size() != 0) {
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
