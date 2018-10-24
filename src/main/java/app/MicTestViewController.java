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
import javafx.scene.layout.AnchorPane;
import javax.sound.sampled.*;


public class MicTestViewController extends Controller{

    @FXML
    private JFXSlider _soundBar;

    @FXML
    private AnchorPane _anchorPane;

    private Thread _monitorThread;


    @FXML
    public void initialize() {

        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        try {

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open();
            targetLine.start();
            _soundBar.setValue(0);

            _monitorThread = new Thread() {
                @Override
                public void run() {

                    byte[] data = new byte[targetLine.getBufferSize() / 5];
                    int readBytes = 1;
                    while (readBytes != 0) {
                        readBytes = targetLine.read(data, 0, data.length);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                _soundBar.setValue(calculateRMSLevel(data));
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

            _monitorThread.start();

        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
        }

    }

    /**
     * allows the user to go back to the PlayViewController GUI
     */
    @FXML
    private void backButtonHandler() {
        _monitorThread.interrupt();
        switchController("PlayViewController.fxml", _anchorPane);
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
