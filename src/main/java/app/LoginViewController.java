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

public class LoginViewController {

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
    private void loginButtonHandler() {
        if (_username.getText().toLowerCase().equals("demo") && _password.getText().equals("demo")) {
            NameSayer.setCurrentUser(new User(_username.getText()));
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Pane newLoadedPane = FXMLLoader.load(getClass().getResource("SearchNamesViewController.fxml"));
                        anchorPane.getChildren().clear();
                        anchorPane.getChildren().add(newLoadedPane);
                    } catch (IOException err) {
                        JOptionPane.showMessageDialog(null, "An error occurred: " + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        } else {
            _username.setDisable(true);
            _password.setDisable(true);
            stackPane.setVisible(true);
            JFXDialogLayout dialogContent = new JFXDialogLayout();
            JFXDialog deleteDialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);

            Text header = new Text("Please enter a valid username and password or register if you are new");
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
}
