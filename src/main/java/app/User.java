package app;

public class User {

    private String _username;
    private String _name;
    private int _score;

    public User(String username) {
        _username = username;

        // TODO Need to change this to reflect database connection
        _name = "Demo";
        _score = 0;
    }

    public String getName() {
        return _name;
    }

    public int getScore() {
        return _score;
    }
}