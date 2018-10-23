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
    private ColorAdjust blackWhiteFilter = new ColorAdjust();
    private User currentSessionUser;

    @FXML
    private void initialize() {
        getScore();
        getPlacement();
        loadLeaderboard();
        addHoverTextToAwards();
        blackWhiteFilter.setBrightness(1);
        currentSessionUser = NameSayer.getCurrentUser();

        // Update awards
        updatePracticeAwards();
        updateRecordAwards();
        updateCompareAwards();

        // Set greeting name on profile
        String name = currentSessionUser.getUsername().substring(0, 1).toUpperCase() + currentSessionUser.getUsername().substring(1);
        _myProfileText.setText("Hello " + name);


    }

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

    private void updatePracticeAwards(){
        int listenScore = currentSessionUser.getListenToNameScore();
        if(listenScore < 200) {
            _practice200Names.setEffect(blackWhiteFilter);
        }
        if (listenScore < 50) {
            _practice50Names.setEffect(blackWhiteFilter);
        }
        if (listenScore < 20) {
            _practice20Names.setEffect(blackWhiteFilter);
        }
        if (listenScore < 1) {
            _practiceFirstName.setEffect(blackWhiteFilter);
        }
    }

    private void updateRecordAwards() {
        int recordScore = (currentSessionUser.getRecordingNameAndSavingScore() /2) + currentSessionUser.getRecordingNameAndNotSavingScore();
        if (recordScore < 100) {
            _record100Recordings.setEffect(blackWhiteFilter);
        }
        if (recordScore < 50) {
            _record50Recordings.setEffect(blackWhiteFilter);
        }
        if (recordScore < 1) {
            _recordFirstName.setEffect(blackWhiteFilter);
        }
    }

    private void updateCompareAwards() {
        int compareScore = currentSessionUser.getCompareAudioScore();
        if (compareScore < 100) {
            _compare100Names.setEffect(blackWhiteFilter);
        }
        if (compareScore < 50) {
            _compare50Names.setEffect(blackWhiteFilter);
        }
        if (compareScore < 1) {
            _compareFirstName.setEffect(blackWhiteFilter);
        }
        System.out.println("Compare score: " + compareScore);

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
