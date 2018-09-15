/**
 * PlayViewController.java
 * Scene for playing a selected creation
 *
 * Copyright Preet Patel, 2018
 * @Author Preet Patel
 * Date Created: 13 August, 2018
 */

package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.DataInput;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.round;

public class MicTestViewController {

    @FXML
    private JFXSlider soundBar;

    @FXML
    private AnchorPane anchorPane;

    private Thread monitorThread;


    @FXML
    public void initialize() {

        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        try {

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open();

            monitorThread = new Thread() {
                @Override
                public void run() {
                    targetLine.start();
                    soundBar.setValue(0);
                    byte[] data = new byte[targetLine.getBufferSize() / 5];
                    int readBytes = 1;
                    while(readBytes != 0) {
                        readBytes = targetLine.read(data, 0, data.length);
                        soundBar.setValue(calculateRMSLevel(data));
                    }
                    targetLine.stop();
                    targetLine.close();
                }
            };

            monitorThread.start();

        } catch(LineUnavailableException lue) {
            lue.printStackTrace();
        }

    }

    @FXML
    private void backButtonHandler() {
        monitorThread.interrupt();
        System.out.println("made it");
        Pane newLoadedPane = null;
        try {
            newLoadedPane = FXMLLoader.load(getClass().getResource("HomeViewController.fxml"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
        anchorPane.getChildren().add(newLoadedPane);
    }

    private static int calculateRMSLevel(byte[] audioData)
    { // audioData might be buffered data read from a data line
        long lSum = 0;
        for (int i=0; i<audioData.length; i++) {
            lSum = lSum + audioData[i];
        }

        double dAvg = lSum / audioData.length;

        double sumMeanSquare = 0d;

        for (int j=0; j<audioData.length; j++) {
            sumMeanSquare = sumMeanSquare + (Math.pow(audioData[j] - dAvg, 2d));
        }

        double averageMeanSquare = sumMeanSquare / audioData.length;
        return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
    }
}
