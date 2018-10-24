package app;

import com.jfoenix.controls.JFXProgressBar;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.io.File;

public class PlayBarProgresser implements Runnable{

    private JFXProgressBar _playBar;
    private File _file;

    public PlayBarProgresser (JFXProgressBar playBar, File file){
        _playBar = playBar;
        _file = file;
    }

    public void run(){
        try {

            //Following code retrieved from 'https://stackoverflow.com/questions/3009908/how-do-i-get-a-sound-files-total-time-in-java'

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(_file);
            AudioFormat format = audioInputStream.getFormat();
            long audioFileLength = _file.length();

            int frameSize = format.getFrameSize();
            float frameRate = format.getFrameRate();
            float durationInSeconds = (audioFileLength / (frameSize * frameRate));


            double progressBarPercentage = 0;
            int hundredSecondsPassed = 0;

            while (progressBarPercentage < 1) {

                progressBarPercentage = hundredSecondsPassed/(durationInSeconds*100);

                _playBar.setProgress(progressBarPercentage);
                Thread.sleep(10);
                hundredSecondsPassed = hundredSecondsPassed + 1;
            }
            //end of code segment from 'https://stackoverflow.com/questions/3009908/how-do-i-get-a-sound-files-total-time-in-java'

        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }


    /**
     * sets the progress bar to play for the duration of the audio file
     * @param file audio file that the progress bar is set to
     */

}
