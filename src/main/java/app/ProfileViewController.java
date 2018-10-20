package app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;


public class ProfileViewController extends Controller{

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button _backButton;
    @FXML
    private Text _myRecordingsText;
    @FXML
    private Text _myPracticesText;
    @FXML
    private ListView _leaderboardList;
    @FXML
    private Text _myPlacementText;

    @FXML
    private void initialize() {

    }

    private void loadLeaderboard(){
        //loads a leaderboard with everything
    }

    private void getScore(){
        //changes the practicesText to the score the user currently has
    }

    private void getRecordingsNumber(){
        //check the file for how many recordings exist within that file
    }

    private void getPlacement(){
        //shows what place the user is in terms of SCORE ONLY
    }


    @FXML
    private void backButtonHandler(){
        switchController("SearchNamesViewController.fxml", anchorPane);
    }
}
