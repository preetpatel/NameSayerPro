/**
 * ProfileViewController.java
 * Copyright Preet Patel, 2018
 * Handles functionality for the profile screen
 *
 * @Author Preet Patel
 * Date Created: 21 October, 2018
 */

package app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;


public class ProfileViewController extends Controller{

    @FXML private AnchorPane anchorPane;
    @FXML private Button _backButton;
    @FXML private Text _myScoreText;
    @FXML private Text _myProfileText;
    @FXML private ListView _leaderboardList;
    @FXML private Text _myPlacementText;
    @FXML private ImageView _practiceFirstName;
    @FXML private ImageView _recordFirstName;
    @FXML private ImageView _compareFirstName;
    @FXML private ImageView _compare50Names;
    @FXML private ImageView _compare100Names;
    @FXML private ImageView _rate10Recordings;
    @FXML private ImageView _practice20Names;
    @FXML private ImageView _practice50Names;
    @FXML private ImageView _practice200Names;
    @FXML private ImageView _record50Recordings;
    @FXML private ImageView _record100Recordings;
    @FXML private ImageView _get5Users;
    private ColorAdjust _blackWhiteFilter = new ColorAdjust();
    private User _currentSessionUser;

    @FXML
    private void initialize() {
        //get information on the user
        getScore();
        getPlacement();
        loadLeaderboard();
        addHoverTextToAwards();
        _blackWhiteFilter.setBrightness(1);
        _currentSessionUser = NameSayer.getCurrentUser();

        // Update awards
        updatePracticeAwards();
        updateRecordAwards();
        updateCompareAwards();
        updateRatingAwards();
        updateUserCountAward();

        // Set greeting name on profile
        String name = _currentSessionUser.getUsername().substring(0, 1).toUpperCase() + _currentSessionUser.getUsername().substring(1);
        _myProfileText.setText("Hello " + name);


    }

    /**
     * Updates the leaderboard for current score information
     */
    private void loadLeaderboard(){
        Leaderboard scoresLeaderBoard = new Leaderboard();
        for (Map.Entry<String, Integer> entry : scoresLeaderBoard.getScores().entrySet()) {
            _leaderboardList.getItems().add(entry.getValue() + " | " + entry.getKey());
        }
    }

    private void getScore(){
        //changes the practicesText to the score the user currently has
        _myScoreText.setText("My Score: " + String.valueOf(NameSayer.getCurrentUser().getTotalScore()));
    }

    /**
     * Updates the badges for the amount of practices a user has done
     */
    private void updatePracticeAwards(){
        int listenScore = _currentSessionUser.getListenToNameScore();
        if(listenScore < 200) {
            _practice200Names.setEffect(_blackWhiteFilter);
        }
        if (listenScore < 50) {
            _practice50Names.setEffect(_blackWhiteFilter);
        }
        if (listenScore < 20) {
            _practice20Names.setEffect(_blackWhiteFilter);
        }
        if (listenScore < 1) {
            _practiceFirstName.setEffect(_blackWhiteFilter);
        }
    }

    /**
     * Updates the badges for the amount of recordings a user has done
     */
    private void updateRecordAwards() {
        int recordScore = (_currentSessionUser.getRecordingNameAndSavingScore() /2) + _currentSessionUser.getRecordingNameAndNotSavingScore();
        if (recordScore < 100) {
            _record100Recordings.setEffect(_blackWhiteFilter);
        }
        if (recordScore < 50) {
            _record50Recordings.setEffect(_blackWhiteFilter);
        }
        if (recordScore < 1) {
            _recordFirstName.setEffect(_blackWhiteFilter);
        }
    }


    /**
     * Updates the badges for the amount of compares a user has done
     */
    private void updateCompareAwards() {
        int compareScore = _currentSessionUser.getCompareAudioScore();
        if (compareScore < 100) {
            _compare100Names.setEffect(_blackWhiteFilter);
        }
        if (compareScore < 50) {
            _compare50Names.setEffect(_blackWhiteFilter);
        }
        if (compareScore < 1) {
            _compareFirstName.setEffect(_blackWhiteFilter);
        }
    }

    /**
     * Updates the badges for the amount of rating a user has done
     */
    private void updateRatingAwards() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(NameSayer.directoryPath + "/ratings.txt"));
            int ratingScore = 0;
            while ((br.readLine()) != null) {
                ratingScore++;
            }
            if (ratingScore < 10) {
                _rate10Recordings.setEffect(_blackWhiteFilter);
            }
            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the badges for the amount of score a user has
     */
    private void updateUserCountAward() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(NameSayer.directoryPath + "/users.txt"));
            int userScore = 0;
            while ((br.readLine()) != null) {
                userScore++;
            }
            if (userScore < 5) {
                _get5Users.setEffect(_blackWhiteFilter);
            }
            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gets the leaderboard placement of the current user in terms of score
     */
    private void getPlacement(){
        //shows what place the user is in terms of SCORE ONLY
        int rank = NameSayer.getCurrentUser().getRank();
        switch(rank % 10) {
            case 1:
                _myPlacementText.setText("My Placement: " + rank + "st");
                break;
            case 2:
                _myPlacementText.setText("My Placement: " + rank + "nd");
                break;
            case 3:
                _myPlacementText.setText("My Placement: " + rank + "rd");
                break;
            default:
                _myPlacementText.setText("My Placement: " + rank + "th");
        }
    }

    /**
     * Allows badge information to be presented when hovering over the badge
     */
    private void addHoverTextToAwards() {
        Tooltip.install(_practiceFirstName, new Tooltip("Practice First Name"));
        Tooltip.install(_recordFirstName, new Tooltip("Record First Name"));
        Tooltip.install(_compareFirstName, new Tooltip("Compare First Name"));
        Tooltip.install(_compare50Names, new Tooltip("Compare 50 Names"));
        Tooltip.install(_compare100Names, new Tooltip("Compare 100 Names"));
        Tooltip.install(_rate10Recordings, new Tooltip("Rate 10 Recordings"));
        Tooltip.install(_practice20Names, new Tooltip("Practice 20 Names"));
        Tooltip.install(_practice50Names, new Tooltip("Practice 50 Names"));
        Tooltip.install(_practice200Names, new Tooltip("Practice 200 Names"));
        Tooltip.install(_record50Recordings, new Tooltip("Record 50 Names"));
        Tooltip.install(_record100Recordings, new Tooltip("Record 100 Names"));
        Tooltip.install(_get5Users, new Tooltip("Get 5 users"));
    }


    @FXML
    private void backButtonHandler(){
        switchController("SearchNamesViewController.fxml", anchorPane);
    }
}
