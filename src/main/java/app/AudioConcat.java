package app;

import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AudioConcat {

    private List<File> _toBeConcated;
    private String _concatName;

    /**
     *
     * @param toBeConcated the list of audio files to be concatenated
     * @param concatName the name given to the output file
     */
    public AudioConcat(List<File> toBeConcated, String concatName){
        _concatName = concatName;
        if (toBeConcated.size() > 1) {
            _toBeConcated = toBeConcated;
        } else {
            throw new IllegalArgumentException("Must have more than one file to concatenate");
        }
    }

    /**
     *
     * Processes and Concatenates the given files to Documents/NameSayer/ConcatenatedNames/output_full.wav
     */
    public void concatenate() throws InterruptedException, IOException {

        List<String> normalisedList = new ArrayList<>();
        List<String> desilencedList = new ArrayList<>();

        //create a 0.25 second silent audio for concatenation purposes
        int i=0;
        ProcessBuilder builderSilence = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -f lavfi -i anullsrc=channel_layout=5.1:sample_rate=48000 -t 0.25 " +NameSayer.concatenationTempPath+"/silent.wav" );
        Process process = builderSilence.start();
        process.waitFor();

        //normalise the audio
        for (File fileToNormalise : _toBeConcated) {
            String normalisedFile = "/normalised_" + Integer.toString(i) + ".wav";
            normalisedList.add(normalisedFile);
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -i " + NameSayer.creationsPath+"/"+ fileToNormalise.getName() + " -filter:a loudnorm " +NameSayer.concatenationTempPath+ normalisedFile );
            Process processNormal = builder.start();
            processNormal.waitFor();
            i++;
         }



        //desilence the audio
        i=0;
        for (String normalisedFile : normalisedList){
            String desilencedFile = "/desilenced_" + Integer.toString(i) + ".wav";
            desilencedList.add(desilencedFile);
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -hide_banner -y -i " +NameSayer.concatenationTempPath+normalisedFile +" -af silenceremove=1:0:-35dB:1:5:-35dB:0:peak " + NameSayer.concatenationTempPath+desilencedFile);
            Process processDesilence = builder.start();
            processDesilence.waitFor();
            i++;
        }


        //make a text file with all files to be concatenated
        File concat = new File(NameSayer.concatenationTempPath + "/concat.txt");
        if (!concat.exists()){
            concat.createNewFile();
        }

        FileWriter writer = new FileWriter(concat);

        for (String desilencedFile : desilencedList){
            writer.write("file '"+NameSayer.concatenationTempPath+ desilencedFile + "'\n");
            writer.write("file '"+NameSayer.concatenationTempPath+"/silent.wav'\n");
        }
        writer.close();

        //do the concatenation
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -f concat -safe 0 -i "+ NameSayer.concatenationTempPath+"/concat.txt -c copy '" + NameSayer.concatenatedNamesPath+"/"+_concatName+".wav'");
        builder.start();
        System.out.println("ffmpeg -y -f concat -safe 0 -i "+ NameSayer.concatenationTempPath+"/concat.txt -c copy '" + NameSayer.concatenatedNamesPath+"/"+_concatName+".wav'");

        //deletes all temp files for concatenation

        //FileUtils.cleanDirectory(new File(NameSayer.concatenationTempPath));

    }

}
