package app;

import javax.swing.*;
import java.io.*;

public class User {

    private String _username;
    private String _name;
    private String _password;
    private int _score;
    private static int VALIDSESSION = 1800000;

    public User(String username){
        _username = username;
        _name = "Demo";
        readScores();
    }

    public User(String username, String password){
        _username = username;
        _password = password;
        _name = _username;
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

    public void setPassword(String password) {
        _password = password;
    }

    public String getPassword() {
        return _password;
    }

    private void readScores(){
        try {
        _score = 0;
        BufferedReader br = new BufferedReader(new FileReader(NameSayer.directoryPath +"/score.txt"));
        String line;
            while ((line = br.readLine()) != null) {
                String[] scoreInfo = line.split("\\s+");
                if (scoreInfo[0].equals(_username)) {
                    _score = Integer.parseInt(scoreInfo[1]);
                }
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }


    }

    /**
     * Checks if the user object's username and password match those in the text file
     * @return true if the load was successful
     */
    public boolean usernamePasswordMatch(){
        boolean passwordMatchesUsername= false;
        try {
            BufferedReader br = new BufferedReader(new FileReader(NameSayer.directoryPath +"/users.txt"));
            String line;
            while ((line = br.readLine()) != null) {

                String[] userInfo = line.split("\\s+");
                if (userInfo[0].equals(_username) & userInfo[1].equals(_password)) {
                    passwordMatchesUsername= true;
                }
            }

        }catch (IOException e){
            JOptionPane.showMessageDialog(null, "An error occurred while loading the user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return passwordMatchesUsername;
    }

    public boolean exists(){
        //check if user exists
        boolean userExists = false;

        try {
            BufferedReader br = new BufferedReader(new FileReader(NameSayer.directoryPath +"/users.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] userInfo = line.split("\\s+");
                if (userInfo[0].equals(_username)){
                    userExists = true;
                }
            }

        }catch (IOException e){
            JOptionPane.showMessageDialog(null, "An error occurred while loading the user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return userExists;
    }

    /**
     * saves the user's details into the text files containing all the users
     */
    public void saveUser(){
        BufferedWriter bwUsers;
        try {
            bwUsers = new BufferedWriter(new FileWriter(new File(NameSayer.directoryPath +"/users.txt"), true));
            bwUsers.write(_username + " " + _password + "\n");
            bwUsers.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while saving the user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * saves the user's current score in the score file, rewriting if necessary
     */
    public void saveScore(){
        BufferedWriter writer;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(NameSayer.directoryPath +"/score.txt")));
            String line;

            //check if file is already given a rating
            boolean userExists = false;

            while ((line = br.readLine()) != null) {
                if (line.contains("username")) {
                    userExists = true;
                    break;
                }
            }

            //if a score does not exist, add a new rating
            if (!userExists) {
                writer = new BufferedWriter(new FileWriter(NameSayer.directoryPath +"/score.txt", true));
                writer.write(_username + " " + _score + "\n");
                writer.close();

                //if a score does exist, replace the old rating
            } else {
                String old = "";
                BufferedReader reader = new BufferedReader(new FileReader(DirectoryManager.getRatings()));
                String line2 = reader.readLine();

                while (line2 != null) {
                    old = old + line2 + System.lineSeparator();
                    line2 = reader.readLine();


                }
                String newContent = old.replaceAll(_username + " " + "\\d+", Integer.toString(_score));
                FileWriter writer2 = new FileWriter(DirectoryManager.getRatings());
                writer2.write(newContent);
                reader.close();
                writer2.close();
            }

        }catch (IOException e2) {
            JOptionPane.showMessageDialog(null, "An error occurred during saving score", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateLoginTime() {
        try {
            /*
             * File writing and replacement code sourced from:
             * https://stackoverflow.com/questions/1377279/find-a-line-in-a-file-and-remove-it
             */
            File inputFile = new File(NameSayer.directoryPath + "/users.txt");
            File tempFile = new File("TempFile.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = _username + " " + _password;
            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                String[] strings = trimmedLine.split("\\s");
                if (strings.length >= 2) {
                    trimmedLine = strings[0] + " " + strings[1];
                    if (trimmedLine.equals(lineToRemove)) {
                        writer.write(trimmedLine + " " + System.currentTimeMillis() + System.getProperty("line.separator"));
                    } else {
                        writer.write(currentLine + System.getProperty("line.separator"));
                    }
                }
            }
            writer.close();
            reader.close();
            tempFile.renameTo(inputFile);
        } catch (IOException e) {
            //TODO Add some form of handling here
        }
    }

    public static User getSessionValidUser() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(NameSayer.directoryPath + "/users.txt")));
            String line;

            while ((line = br.readLine()) != null) {
                String[] strings = line.split("\\s");
                if (strings.length == 3) {
                    if ((System.currentTimeMillis() - VALIDSESSION) < Long.parseLong(strings[2])) {
                        User loggedUser = new User(strings[0]);
                        loggedUser.setPassword(strings[1]);
                        return loggedUser;
                    }
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}