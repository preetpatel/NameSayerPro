package Utils.MessageBox;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ModalBox {

    private Text _header = new Text("");
    private JFXButton _confirmButton = new JFXButton();
    private JFXDialog _dialog;

    public ModalBox(StackPane stackPane, String headerText, String buttonText) {

        stackPane.setVisible(true);
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        _header.setStyle("-fx-font-size: 30; -fx-font-family: 'Lato Heavy'");
        dialogContent.setHeading(_header);

        _confirmButton.setText("");
        _confirmButton.setStyle("-fx-background-color: #03b5aa; -fx-text-fill: white; -fx-font-family: 'Lato Medium'; -fx-font-size: 25;");
        setHeaderText(headerText);
        setButtonText(buttonText);
        dialogContent.setActions(_confirmButton);
        _dialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.CENTER);
    }

    public void setHeaderText(String text) {
        _header.setText(text);
    }

    public void setButtonText(String text) {
        _confirmButton.setText(text);
    }

    public void showDialog() {
        _dialog.show();
    }

    public void setHandlers(EventHandler eventHandler) {
        _confirmButton.setOnAction(eventHandler);
        EventHandler<ActionEvent> current = _confirmButton.getOnAction();
        _confirmButton.setOnAction(e -> {
            current.handle(e);
            _dialog.close();
        });
        _dialog.setOnDialogClosed(eventHandler);
    }
}
