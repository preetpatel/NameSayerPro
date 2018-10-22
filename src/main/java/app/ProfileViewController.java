package app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.File;
import java.util.Map;


public class ProfileViewController extends Controller{

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button _backButton;
    @FXML
    private Text _myScoreText;
    @FXML
    private Text _myPracticesText;
    @FXML
    private ListView _leaderboardList;
    @FXML
    private Text _myPlacementText;
    @FXML
    private ProgressIndicator _progressIndicator;

    @FXML
    private void initialize() {
        getScore();
        getPlacement();
        loadLeaderboard();

        // Just some feature testing
        _progressIndicator.setProgress(0.2);

    }

    private void loadLeaderboard(){
        Leaderboard scoresLeaderBoard = new Leaderboard(new File(NameSayer.directoryPath + "/score.txt"));
        for (Map.Entry<String, Integer> entry : scoresLeaderBoard.getScores().entrySet()) {
            _leaderboardList.getItems().add(entry.getValue() + " | " + entry.getKey());
        }
    }

    private void getScore(){
        //changes the practicesText to the score the user currently has
        _myScoreText.setText("My Score: " + String.valueOf(NameSayer.getCurrentUser().getScore()));
    }

    private void getRecordingsNumber(){
        //check the file for how many recordings exist within that file
    }

    private void getPlacement(){
        //shows what place the user is in terms of SCORE ONLY
        int rank = NameSayer.getCurrentUser().getRank();
        System.out.println(rank % 10);
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


    @FXML
    private void backButtonHandler(){
        switchController("SearchNamesViewController.fxml", anchorPane);
    }
}
