/**
 * RegisterViewController.java
 * Copyright Preet Patel, 2018
 * Handles functionality for the registration screen
 *
 * @Author Preet Patel
 * Date Created: 19 October, 2018
 */

package app;

import com.jfoenix.controls.JFXPasswordField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;


public class RegisterViewController extends Controller{

    @FXML
    private Button _confirmButton;
    @FXML
    private Button _cancelButton;
    @FXML
    private TextField _usernameText;
    @FXML
    private JFXPasswordField _passwordText;
    @FXML
    private JFXPasswordField _passwordConfirmText;
    @FXML
    private StackPane _stackPane;
    @FXML
    private AnchorPane _anchorPane;

    @FXML
    public void initialize(){
        _stackPane.setVisible(false);
    }

    public void confirmButtonHandler() {
        //check if username/password has a space in it
        String username = _usernameText.getText();
        String password = _passwordText.getText();
        String confirmPassword = _passwordConfirmText.getText();


        if (username.contains(" ") | password.contains(" ") | confirmPassword.contains(" ")) {
            showErrorDialogOnStackpane("Neither the Username nor the Password may contain a space!", "OK", _stackPane);
        } else if (username.isEmpty() | password.isEmpty() | confirmPassword.isEmpty()) {
            showErrorDialogOnStackpane("Please fill out all of the fields!", "OK", _stackPane);
        } else if (!password.equals(confirmPassword)) {
            showErrorDialogOnStackpane("The passwords must match!", "OK", _stackPane);
        } else{
            //check if username is used
            User newUser = new User(username, password);
            if (newUser.exists()){
                showErrorDialogOnStackpane("A user of that name already exists!", "OK", _stackPane);
            } else {
                newUser.saveUser();
                newUser.saveScore();
                //return user to login
                switchController("LoginViewController.fxml", _anchorPane);
            }
        }
    }

    public void cancelButtonHandler(){
        //return to menu
        switchController("LoginViewController.fxml", _anchorPane);
    }


}
