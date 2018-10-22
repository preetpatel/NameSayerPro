/**
 * Namesayer.java
 * Entry point for app. Initialises the first scene
 * <p>
 * Copyright Preet Patel, 2018
 *
 * @Author Preet Patel
 * @Author Chuyang Chen
 * Date Created: 13 August, 2018
 */

package app;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;

public class NameSayer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /** Path for storing universal creations */
    public static User currentUser;
    public static final String audioPath = System.getProperty("user.home") + "/Documents/NameSayer/Database";
    public static String userRecordingsPath = System.getProperty("user.home") + "/Documents/NameSayer/UserRecordings";
    public static final String directoryPath = System.getProperty("user.home") + "/Documents/NameSayer";
    public static final String concatenatedNamesPath = System.getProperty("user.home") + "/Documents/NameSayer/ConcatenatedNames";
    public static final String concatenationTempPath = System.getProperty("user.home") + "/Documents/NameSayer/ConcatenatedNames/temp";

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User propCurrentUser) {
        currentUser = propCurrentUser;
        userRecordingsPath = userRecordingsPath + "/" + currentUser.getUsername();
    }

    public static void performUserLogout() {
        currentUser.updateLoginTime(false);
        currentUser = null;
        userRecordingsPath = directoryPath + "/UserRecordings";
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        User user = User.getSessionValidUser();
        if (user != null) {
            currentUser = user;
            userRecordingsPath = userRecordingsPath + "/" + currentUser.getUsername();
            loadScene("SearchNamesViewController.fxml", primaryStage);
        } else {
            loadScene("LoginViewController.fxml", primaryStage);
        }

    }

    /** Loads FXML onto the scene and renders the first scene: HomeViewController.java */
    public void loadScene(String source, Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource(source));
        Parent layout = loader.load();
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/css/bar.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("NameSayer");
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (currentUser != null) {
                    currentUser.updateLoginTime(true);
                }
            }
        });
    }


}
