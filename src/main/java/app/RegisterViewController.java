package app;

import com.jfoenix.controls.JFXPasswordField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;


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
        } else if (!password.equals(confirmPassword)) {
            showErrorDialogOnStackpane("The passwords must match!", "OK", stackPane);
        } else{
            //check if username is used
            User newUser = new User(username, password);
            if (newUser.exists()){
                showErrorDialogOnStackpane("A user of that name already exists!", "OK", stackPane);
            } else {
                newUser.saveUser();
                newUser.saveScore();
                //return user to login
                switchController("LoginViewController.fxml", anchorPane);
            }
        }
    }

    public void cancelButtonHandler(){
        //return to menu
        switchController("LoginViewController.fxml", anchorPane);
    }


}
