package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RegisterViewController {


    @FXML
    Button _confirmButton;
    @FXML
    Button _cancelButton;
    @FXML
    TextField _usernameText;
    @FXML
    JFXPasswordField _passwordText;
    @FXML
    JFXPasswordField _passwordConfirmText;
    @FXML
    StackPane stackPane;
    @FXML
    AnchorPane anchorPane;

    @FXML
    public void initialize(){
        stackPane.setVisible(false);
    }

    public void confirmButtonHandler() {
        //check if username/password has a space in it
        String username = _usernameText.getText();
        String password = _passwordText.getText();
        String confirmPassword = _passwordConfirmText.getText();

        if (username.contains(" ") | password.contains(" ") | confirmPassword.contains(" ")) {
            showErrorDialog("Neither the Username nor the Password may contain a space!", "OK");
        } else if (username.isEmpty() | password.isEmpty() | confirmPassword.isEmpty()) {
            showErrorDialog("Please fill out all of the fields!", "OK");
        } else {
            //check if username is used

            //check if passwords match

        }

        //return user to login
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Pane newLoadedPane = FXMLLoader.load(getClass().getResource("LoginViewController.fxml"));
                    anchorPane.getChildren().clear();
                    anchorPane.getChildren().add(newLoadedPane);
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(null, "An error occurred: " + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }


    public void cancelButtonHandler(){
        //return to menu
    }

    private boolean checkUsername(String username){
        return false;
    }

    /**
     * makes an error popup on the window
     * @param headerText
     * @param buttonText
     */
    private void showErrorDialog(String headerText, String buttonText) {
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
