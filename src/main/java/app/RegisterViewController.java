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

public class RegisterViewController extends Controller{


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
            showErrorDialogOnStackpane("Neither the Username nor the Password may contain a space!", "OK", stackPane);
        } else if (username.isEmpty() | password.isEmpty() | confirmPassword.isEmpty()) {
            showErrorDialogOnStackpane("Please fill out all of the fields!", "OK", stackPane);
        } else {
            //check if username is used

            //check if passwords match

        }

        //return user to login
        switchController("LoginViewController.fxml", anchorPane);
    }


    public void cancelButtonHandler(){
        //return to menu
    }

    private boolean checkUsername(String username){
        return false;
    }

}
