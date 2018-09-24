/**
 * MicTestController.java
 * GUI for testing microphone input
 * <p>
 * Copyright Preet Patel, 2018
 *
 * @Author Preet Patel
 * Date Created: 19 August, 2018
 */

package app;


import com.jfoenix.controls.JFXSlider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.IOException;


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
            targetLine.start();
            soundBar.setValue(0);

            monitorThread = new Thread() {
                @Override
                public void run() {

                    byte[] data = new byte[targetLine.getBufferSize() / 5];
                    int readBytes = 1;
                    while (readBytes != 0) {
                        readBytes = targetLine.read(data, 0, data.length);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                soundBar.setValue(calculateRMSLevel(data));
                            }
                        });

                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            targetLine.stop();
                            targetLine.close();
                        }
                    });

                }
            };

            monitorThread.start();

        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
        }

    }

    /**
     * allows the user to go back to the PlayViewController GUI
     */
    @FXML
    private void backButtonHandler() {
        monitorThread.interrupt();
        Pane newLoadedPane = null;
        try {
            newLoadedPane = FXMLLoader.load(getClass().getResource("PlayViewController.fxml"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An Error occurred while trying to continue: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
        anchorPane.getChildren().add(newLoadedPane);
    }

    /**
     * converts the audio data into a number
     * @param audioData
     * @return a number representing the audio level
     */
    private static int calculateRMSLevel(byte[] audioData) { // audioData might be buffered data read from a data line
        long lSum = 0;
        for (int i = 0; i < audioData.length; i++) {
            lSum = lSum + audioData[i];
        }

        double dAvg = lSum / audioData.length;

        double sumMeanSquare = 0d;

        for (int j = 0; j < audioData.length; j++) {
            sumMeanSquare = sumMeanSquare + (Math.pow(audioData[j] - dAvg, 2d));
        }

        double averageMeanSquare = sumMeanSquare / audioData.length;
        return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
    }
}
