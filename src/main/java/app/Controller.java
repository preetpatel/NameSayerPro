/**
 * Controller.java
 * Copyright Preet Patel, 2018
 * an abstract class which contains the shared functionality by all the controllers
 *
 * @Author Preet Patel
 * @Author Chuyang Chen
 * Date Created: 15 October, 2018
 */

package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class Controller {

    @FXML private Button _helpButton;
    @FXML private JFXSlider _volumeBar;

    /**
     * Opens a browser to the wiki
     * The code for this method pulled from 'https://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java'
     */
    @FXML public void helpButtonHandler(){
        Runtime rt = Runtime.getRuntime();
        String url = "https://docdro.id/VePeHbN";
        String[] browsers = { "epiphany", "firefox", "mozilla", "konqueror",
                "netscape", "opera", "links", "lynx" };

        StringBuffer cmd = new StringBuffer();
        for (int i = 0; i < browsers.length; i++)
            if(i == 0)
                cmd.append(String.format(    "%s \"%s\"", browsers[i], url));
            else
                cmd.append(String.format(" || %s \"%s\"", browsers[i], url));
        try {
            rt.exec(new String[]{"sh", "-c", cmd.toString()});
        } catch (Exception e){
                e.printStackTrace();
        }

    }

    /**
     * Method to switch the controller for a given anchorpane to a given fxml file
     * @param fxmlFile the fxml file containing the file to be switched to
     * @param anchorPane anchorpane to be switched
     */
    public void switchController(String fxmlFile, AnchorPane anchorPane){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Pane newLoadedPane = FXMLLoader.load(getClass().getResource(fxmlFile));
                    anchorPane.getChildren().clear();
                    anchorPane.getChildren().add(newLoadedPane);
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(null, "An error occurred: " + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * makes an error popup on the window
     * @param headerText
     * @param buttonText
     */
    public void showErrorDialogOnStackpane(String headerText, String buttonText, StackPane stackPane) {
        stackPane.setVisible(true);
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        JFXDialog errorDialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

        Text header = new Text(headerText);
        header.setStyle("-fx-font-size: 30; -fx-font-family: 'Lato Heavy'");
        dialogContent.setHeading(header);

        JFXButton button = new JFXButton();
        button.setText(buttonText);
        button.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                errorDialog.close();
                stackPane.setVisible(false);

            }
        });

        errorDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
            @Override
            public void handle(JFXDialogEvent event) {
                stackPane.setVisible(false);
            }
        });

        dialogContent.setActions(button);
        errorDialog.show();
    }
}
