package app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.File;
import java.util.Map;


public class ProfileViewController extends Controller{

    @FXML private AnchorPane anchorPane;
    @FXML private Button _backButton;
    @FXML private Text _myScoreText;
    @FXML private Text _myPracticesText;
    @FXML private ListView _leaderboardList;
    @FXML private Text _myPlacementText;
    @FXML private ImageView _practiceFirstName;
    @FXML private ImageView _recordFirstName;
    @FXML private ImageView _compareFirstName;
    @FXML private ImageView _rate10Recordings;
    @FXML private ImageView _practice20Names;
    @FXML private ImageView _practice50Names;
    @FXML private ImageView _practice200Names;
    @FXML private ImageView _record50Recordings;
    @FXML private ImageView _record100Recordings;
    @FXML private ImageView _get5Users;
    private ColorAdjust blackWhiteFilter = new ColorAdjust();

    @FXML
    private void initialize() {
        getScore();
        getPlacement();
        loadLeaderboard();
        addHoverTextToAwards();
        blackWhiteFilter.setBrightness(1);


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

    private void addHoverTextToAwards() {
        Tooltip.install(_practiceFirstName, new Tooltip("Practice First Name"));
        Tooltip.install(_recordFirstName, new Tooltip("Record First Name"));
        Tooltip.install(_compareFirstName, new Tooltip("Compare First Name"));
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
