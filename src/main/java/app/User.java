package app;

import java.io.*;

public class User {

    private String _username;
    private String _name;
    private int _score;

    public User(String username) throws FileNotFoundException, IOException {
        _username = username;

        // TODO Need to change this to reflect database connection
        _name = "Demo";
        readScores();
    }

    public String getName() {
        return _name;
    }

    public int getScore() {
        return _score;
    }

    public String getUsername() {
        return _username;
    }

    private void readScores() throws FileNotFoundException, IOException {
        _score = 0;
        BufferedReader br = new BufferedReader(new FileReader(NameSayer.directoryPath +"/score.txt"));
        String line;

        while ((line = br.readLine())!=null) {
            String[] scoreInfo = line.split("\\s+");
            if (scoreInfo[0].equals(_username)){
                _score = Integer.parseInt(scoreInfo[1]);
            }
        }


    }
}