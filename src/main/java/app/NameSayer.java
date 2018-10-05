/**
 * Namesayer.java
 * Entry point for app. Initialises the first scene
 * <p>
 * Copyright Preet Patel, 2018
 *
 * @Author Preet Patel
 * @Auther Chuyang Chen
 * Date Created: 13 August, 2018
 */

package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NameSayer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /** Path for storing universal creations */
    public static final String creationsPath = System.getProperty("user.home") + "/Documents/NameSayer/Database";
    public static final String userRecordingsPath = System.getProperty("user.home") + "/Documents/NameSayer/UserRecordings";
    public static final String directoryPath = System.getProperty("user.home") + "/Documents/NameSayer";
    public static final String concatenatedNamesPath = System.getProperty("user.home") + "/Documents/NameSayer/ConcatenatedNames";
    public static final String concatenationTempPath = System.getProperty("user.home") + "/Documents/NameSayer/ConcatenatedNames/temp";
    @Override
    public void start(Stage primaryStage) throws Exception {
        loadScene("SearchNamesViewController.fxml", primaryStage);

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
    }


}
