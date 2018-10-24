/**
 * User.java
 * Class representing individual users for the program
 * Copyright Preet Patel, 2018
 *
 * @Author Preet Patel
 * @Author Chuyang Chen
 * Date Created: 19 October, 2018
 */

package app;

import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import java.io.*;

public class User {

    private String _username;
    private String _name;
    private String _password;
    private int _listenToNameScore = 0;
    private int _recordingNameButNotSaving = 0;
    private int _recordingNameAndSaving = 0;
    private int _compareAudio = 0;
    private static int VALIDSESSION = 600000;
    private int _rank;

    public User(String username){
        _username = username;
        _name = "Demo";
        readScores();
    }

    public User(String username, String password){
        _username = username;
        _password = DigestUtils.sha256Hex(password);
        _name = _username;
    }

    public String getName() {
        return _name;
    }

    public int getTotalScore() {
        readScores();
        return _listenToNameScore + _recordingNameButNotSaving + _recordingNameAndSaving + _compareAudio;
    }

    public int getListenToNameScore() {
        return _listenToNameScore;
    }

    public int getRecordingNameAndSavingScore() {
        return _recordingNameAndSaving;
    }

    public int getRecordingNameAndNotSavingScore() {
        return _recordingNameButNotSaving;
    }

    public int getCompareAudioScore() {
        return _compareAudio;
    }

    public void increaseListenNameScore(int i){
        _listenToNameScore+=i;
        saveScore();
    }

    public void increaseRecordButNotSaveScore(int i) {
        _recordingNameButNotSaving += i;
        saveScore();
    }

    public void increaseRecordAndSaveScore(int i) {
        _recordingNameAndSaving += i;
        saveScore();
    }

    public void increaseCompareScore(int i) {
         _compareAudio += i;
        saveScore();
    }

    public int getRank() {
        readScores();
        return _rank;
    }

    public String getUsername() {
        return _username;
    }

    public void setPassword(String password) {
        _password = DigestUtils.sha256Hex(password);
    }

    private void readScores(){
        try {
        BufferedReader br = new BufferedReader(new FileReader(NameSayer.directoryPath +"/score.txt"));
        String line;
        int rank = 1;
            while ((line = br.readLine()) != null) {
                String[] scoreInfo = line.split("\\s+");
                if (scoreInfo.length >= 5 && scoreInfo[0].equals(_username)) {
                    _listenToNameScore = Integer.parseInt(scoreInfo[1]);
                    _recordingNameButNotSaving = Integer.parseInt(scoreInfo[2]);
                    _recordingNameAndSaving = Integer.parseInt(scoreInfo[3]);
                    _compareAudio = Integer.parseInt(scoreInfo[4]);
                }
            }
            br.close();
            BufferedReader rankReader = new BufferedReader(new FileReader(NameSayer.directoryPath +"/score.txt"));
            int totalScore = _listenToNameScore + _recordingNameButNotSaving +_recordingNameAndSaving + _compareAudio;
            while ((line = rankReader.readLine()) != null) {
                String[] scoreInfo = line.split("\\s+");
                if (scoreInfo.length >= 5 && (Integer.parseInt(scoreInfo[1]) + Integer.parseInt(scoreInfo[2]) +
                        Integer.parseInt(scoreInfo[3]) + Integer.parseInt(scoreInfo[4]))  > totalScore) {
                    rank++;
                }
            }
            rankReader.close();
            _rank = rank;
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
                if (line.contains(_username)) {
                    userExists = true;
                    break;
                }
            }

            //if a score does not exist, add a new score
            if (!userExists) {
                writer = new BufferedWriter(new FileWriter(DirectoryManager.get_score(), true));
                writer.write(_username + " " + _listenToNameScore + " " + _recordingNameButNotSaving + " " + _recordingNameAndSaving + " " + _compareAudio + System.getProperty("line.separator"));
                writer.close();

                //if a score does exist, replace the old score
            } else {
                String old = "";
                BufferedReader reader = new BufferedReader(new FileReader(DirectoryManager.get_score()));
                String line2 = reader.readLine();

                while (line2 != null) {
                    old = old + line2 + System.lineSeparator();
                    line2 = reader.readLine();


                }
                String newContent = old.replaceAll(_username + " " + "\\d+" + " " + "\\d+" + " " + "\\d+" + " " +
                        "\\d+", _username + " " + Integer.toString(_listenToNameScore) + " " + Integer.toString(_recordingNameButNotSaving)
                        + " " + Integer.toString(_recordingNameAndSaving) + " " + Integer.toString(_compareAudio));
                File temp = new File("tempScoreFile.txt");
                FileWriter writer2 = new FileWriter(temp);
                writer2.write(newContent);
                temp.renameTo(DirectoryManager.get_score());
                reader.close();
                writer2.close();
            }

        }catch (IOException e2) {
            JOptionPane.showMessageDialog(null, "An error occurred during saving score", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the loging to for the current user
     * @param valid
     */
    public void updateLoginTime(boolean valid) {
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
                        if (valid) {
                            writer.write(trimmedLine + " " + System.currentTimeMillis() + System.getProperty("line.separator"));
                        } else {
                            writer.write(trimmedLine + System.getProperty("line.separator"));
                        }
                    } else {
                        writer.write(currentLine + System.getProperty("line.separator"));
                    }
                }
            }
            writer.close();
            reader.close();
            tempFile.renameTo(inputFile);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred during updating login time", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gets the current user that is supposed to be logged in.
     * @return
     */
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
        } catch (IOException | NumberFormatException e) {
            return null;
        }
    }
}