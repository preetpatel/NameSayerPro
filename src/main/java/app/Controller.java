package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
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
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

abstract public class Controller {

    @FXML
    private Button _helpButton;

    @FXML
    public void helpButtonHandler(){
        String url = "https://github.com/PreetPatel/NameSayerPro/wiki";

        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));

            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
