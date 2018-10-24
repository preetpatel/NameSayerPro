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

    @FXML private AnchorPane _anchorPane;
    @FXML private Text _mainText;
    @FXML private StackPane _stackPane;
    @FXML private JFXMasonryPane _addedCreationsPane;
    @FXML private ScrollPane _addedScrollPane;
    @FXML private CustomTextField _searchField;
    @FXML private JFXButton _addButton;
    @FXML private JFXButton _changeDatabaseButton;
    @FXML private JFXButton _startPracticeButton;
    @FXML private JFXButton _removeButton;
    @FXML private JFXButton _profileButton;
    @FXML private JFXPopup _userPopup = new JFXPopup();
    @FXML private JFXButton _removeAllButton;
    private ObservableList<JFXButton> _creationsButtonList = FXCollections.observableArrayList();
    private ObservableList<JFXButton> _selectedButtonsList = FXCollections.observableArrayList();
    private List<Name> _creationsList = new ArrayList<>();
    private List<String> _selectedNames = new ArrayList<>();
    private List<String> _databaseNames = new ArrayList<>();
    private List<String> _concatSafeNames = new ArrayList<>();
    private AutoCompletionBinding<String> _searchBinding;
    private SuggestionProvider<String> _databaseConcatProvider = SuggestionProvider.create(_concatSafeNames);
    private File _uploadList = null;

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
        _searchBinding = TextFields.bindAutoCompletion(_searchField, _databaseConcatProvider);
        _databaseConcatProvider.clearSuggestions();
        _databaseConcatProvider.addPossibleSuggestions(_concatSafeNames);
        _searchBinding.setHideOnEscape(true);

        // Add Enter key listener on search field
        _searchField.setOnKeyPressed(event ->  {
            if (event.getCode() == KeyCode.ENTER) {
                addButtonHandler(new ActionEvent());
            }
        });

        _searchField.setOnKeyReleased(event ->  {
            // arbitrary char assignment
            char compare = 'a';

            if (_searchField.getLength() > 0) {
                compare = _searchField.getText().charAt(_searchField.getLength() - 1);
            }
            if (compare == ' ' || _searchField.getLength() == 0) {
                _concatSafeNames.clear();
                int i = 0;
                while (i < _databaseNames.size()) {
                    _concatSafeNames.add(_searchField.getText() + _databaseNames.get(i));
                    i++;
                }
                _databaseConcatProvider.clearSuggestions();
                _databaseConcatProvider.addPossibleSuggestions(_concatSafeNames);
            }

        });
    }

    /**
     * Disables being able to scroll right on the list of added panes
     */
    public void disableHorizontalScrolling() {
        _addedScrollPane.addEventFilter(ScrollEvent.SCROLL, event ->  {
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
        File selectedDirectory = chooser.showDialog(_anchorPane.getScene().getWindow());
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
                _searchField.setDisable(true);
                _stackPane.setVisible(true);
                String filesFound = "There were " + filesCount + " valid files found";
                ModalBox modalBox = new ModalBox(_stackPane, filesFound, "Ok");
                modalBox.setHandlers(event ->  {
                        NameSayer.audioPath = selectedDirectory.getPath();
                        Platform.runLater(() -> {
                                switchController("SearchNamesViewController.fxml", _anchorPane);
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
        _stackPane.setVisible(false);

        File folder = new File(NameSayer.audioPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                Name tempName = new Name(file);

                // Adds names to the database that suggests names in the search field
                String dataName = tempName.getName();
                dataName = dataName.substring(0, 1).toUpperCase() + dataName.substring(1);
                if (!_databaseNames.contains(dataName)) {
                    _databaseNames.add(dataName);
                    _concatSafeNames.add(dataName);
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


        for (JFXButton button : _selectedButtonsList) {
            _creationsButtonList.remove(button);

            Iterator<Name> creations = _creationsList.iterator();
            while (creations.hasNext()) {

                JFXButton comparedButton = creations.next().getButton();

                if (button.equals(comparedButton)) {
                    creations.remove();
                    _creationsButtonList.remove(comparedButton);

                    break;
                }
            }
        }

        _addedCreationsPane.getChildren().clear();
        _addedCreationsPane.getChildren().addAll(_creationsButtonList);

        // Set remove button to invisible if list has no creations left
        if (_creationsList.isEmpty()) {
            _removeButton.setVisible(false);
            _startPracticeButton.setVisible(false);
            _removeAllButton.setVisible(false);
        }
    }

    /**
     * removes all the buttons from the list
     * @param e
     */
    @FXML
    private void removeAllButtonHandler(ActionEvent e) {
        _creationsList.clear();
        _creationsButtonList.clear();
        _selectedButtonsList.clear();

        _addedCreationsPane.getChildren().clear();
        _addedCreationsPane.getChildren().addAll(_creationsButtonList);
        _removeButton.setVisible(false);
        _startPracticeButton.setVisible(false);
        _removeAllButton.setVisible(false);
    }


    @FXML
    /**
     * Allows adding creations to the list of creations to be played
     */
    private void addButtonHandler(ActionEvent e) {

        _stackPane.setVisible(false);
        _stackPane.getChildren().clear();
        _searchField.setDisable(true);

        String searchedItems = _searchField.getText().trim();

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
        _addedCreationsPane.getChildren().clear();

        if (searchedItems.equals("")) {
            showErrorDialog("Please enter a valid name", "Ok");
            _addedCreationsPane.getChildren().addAll(_creationsButtonList);
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
                for (JFXButton currentButton : _creationsButtonList) {
                    if (processedSearchedItems.toLowerCase().equals(currentButton.getId().toLowerCase())) {
                        buttonExists = true;
                    }
                }

                //add the button to the list of buttons if no button already exists
                if (!buttonExists) {
                    JFXButton button = name.generateButton(_selectedButtonsList);
                    _creationsButtonList.add(button);
                    _creationsList.add(name);
                    _startPracticeButton.setVisible(true);
                    _removeButton.setVisible(true);
                    _removeAllButton.setVisible(true);
                } else if (errors){
                    showErrorDialog("This name has already been added", "Ok");
                    addButtonsToListAndClearSearchfield();
                    return false;
                } else {
                    addButtonsToListAndClearSearchfield();
                    return false;
                }

                _searchField.setDisable(false);

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
        _addedCreationsPane.getChildren().addAll(_creationsButtonList);
        _searchField.setText("");
    }

    /**
     * allows user to upload a text file as the database
     */
    @FXML
    private void uploadButtonHandler() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text file", "*.txt"));
        File result = fileChooser.showOpenDialog((Stage) _anchorPane.getScene().getWindow());
        if (result != null) {
            _uploadList = result;

            try {
                BufferedReader br = new BufferedReader(new FileReader(_uploadList));
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


        for (Name name: _creationsList) {
            _selectedNames.add(name.getName());
        }

        //if more than 1 selected creations
        if (_addedCreationsPane.getChildren().size() > 1) {
            _stackPane.setVisible(true);
            _stackPane.getChildren().clear();
            //create popup
            JFXDialogLayout dialogContent = new JFXDialogLayout();
            JFXDialog randomiseDialog = new JFXDialog(_stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

            Text header = new Text("Do you wish to randomise the list order?");
            header.setStyle("-fx-font-size: 30; -fx-font-family: 'Lato Heavy'");
            dialogContent.setHeading(header);

            //button for randomisation
            JFXButton confirmRandomise = new JFXButton();
            confirmRandomise.setText("Randomise and play");
            confirmRandomise.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
            confirmRandomise.setOnAction(event -> {

                    randomiseDialog.close();
                    _stackPane.setVisible(false);
                    if (_creationsList.size() != 0) {
                        Collections.shuffle(_selectedNames);
                        PlayViewController.setCreationsList(_selectedNames);
                    }
                    if (PlayViewController.creationsExist()) {
                        switchController("PlayViewController.fxml", _anchorPane);
                    } else {
                        showErrorDialogOnStackpane("Please enter at least one existing creation in your text file!", "OK", _stackPane);
                    }
            });

            //button for normal play
            JFXButton confirmPlay = new JFXButton();
            confirmPlay.setText("Play");
            confirmPlay.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
            confirmPlay.setOnAction(event -> {

                    _mainText.setText("This wont be long");
                    randomiseDialog.close();
                    _stackPane.setVisible(false);
                    if (_creationsList.size() != 0) {
                        PlayViewController.setCreationsList(_selectedNames);
                    }
                    if (PlayViewController.creationsExist()) {
                        switchController("PlayViewController.fxml", _anchorPane);
                    } else {
                        showErrorDialogOnStackpane("Please enter at least one existing creation in your text file!", "OK", _stackPane);
                    }
            });

            setAlignment(confirmRandomise, Pos.BASELINE_RIGHT);
            setAlignment(confirmPlay, Pos.BASELINE_LEFT);

            randomiseDialog.setOnDialogClosed(event -> {
                _stackPane.setVisible(false);
            });

            dialogContent.setActions(confirmRandomise, confirmPlay);
            randomiseDialog.show();

        //for list with single name
        } else {
            if (_creationsList.size() != 0) {
                PlayViewController.setCreationsList(_selectedNames);
            }
            if (PlayViewController.creationsExist()) {
                switchController("PlayViewController.fxml", _anchorPane);
            } else {
                showErrorDialogOnStackpane("Please enter at least one existing creation in your text file!", "OK", _stackPane);
            }
        }
    }

    /**
     * makes an error popup on the window
     * @param headerText
     * @param buttonText
     */
    private void showErrorDialog(String headerText, String buttonText) {
        _searchField.setDisable(true);
        _stackPane.setVisible(true);
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        JFXDialog deleteDialog = new JFXDialog(_stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

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
                _searchField.setDisable(false);
                _stackPane.setVisible(false);

            }
        });

        deleteDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
            @Override
            public void handle(JFXDialogEvent event) {
                _stackPane.setVisible(false);
                _searchField.setDisable(false);
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
                _userPopup.hide();
                NameSayer.performUserLogout();
                switchController("LoginViewController.fxml", _anchorPane);
            }
        });

        JFXButton openProfileButton = new JFXButton("Profile");
        openProfileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _userPopup.hide();
                switchController("ProfileViewController.fxml", _anchorPane);
            }
        });

        VBox vbox = new VBox(openProfileButton, logoutButton);
        _userPopup.setPopupContent(vbox);
        _userPopup.show(_profileButton, JFXPopup.PopupVPosition.BOTTOM, JFXPopup.PopupHPosition.LEFT);
    }
}
