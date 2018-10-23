/**
 * LoginViewController.java
 * Copyright Preet Patel, 2018
 * A class which handles the functionality of the login screen
 *
 * @Author Preet Patel
 * @Author Chuyang Chen
 * Date Created: 17 October, 2018
 */

package app;

import com.jfoenix.controls.*;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.swing.*;
import java.io.IOException;

public class LoginViewController extends Controller{

    @FXML
    private JFXTextField _username;
    @FXML
    private JFXPasswordField _password;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private StackPane stackPane;

    @FXML
    private void initialize() {
        stackPane.setVisible(false);

        // Checks for all required directories
        DirectoryManager manager = new DirectoryManager();
        manager.runChecks();
    }

    @FXML
    /**
     * Functionality for the logging in button
     */
    private void loginButtonHandler() {
        //get the info from the fields
        String username = _username.getText();
        String password = _password.getText();
        User user = new User(username, password);

        //check whether the information allows a login to be performed
        if (user.usernamePasswordMatch()) {
            User authorisedUser = new User(_username.getText());
            authorisedUser.setPassword(_password.getText());
            NameSayer.setCurrentUser(authorisedUser);
            switchController("SearchNamesViewController.fxml", anchorPane);
        } else {
            //error for inability to login
            _username.setDisable(true);
            _password.setDisable(true);
            stackPane.setVisible(true);
            JFXDialogLayout dialogContent = new JFXDialogLayout();
            JFXDialog deleteDialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

            Text header = new Text("Please enter the correct username or password");
            header.setStyle("-fx-font-size: 30; -fx-font-family: 'Lato Heavy'");
            dialogContent.setHeading(header);

            JFXButton confirmDelete = new JFXButton();
            confirmDelete.setText("Ok");
            confirmDelete.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
            confirmDelete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    deleteDialog.close();
                    _username.setDisable(false);
                    _password.setDisable(false);
                    stackPane.setVisible(false);

                }
            });

            deleteDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
                @Override
                public void handle(JFXDialogEvent event) {
                    stackPane.setVisible(false);
                    _username.setDisable(false);
                    _password.setDisable(false);
                }
            });

            dialogContent.setActions(confirmDelete);
            deleteDialog.show();
        }
    }

    @FXML
    /**
     * handles login to change pane to the search names view controller
     */
    private void newUserButtonHandler() {
        switchController("RegisterViewController.fxml",anchorPane);
    }
}
