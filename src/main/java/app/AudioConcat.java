package app;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AudioConcat {


    private File _textFile;

    private List<List<File>> _listOfConcatenations;
    private String _fileName;
    /**
     *
     * @param toBeConcated the list of audio files to be concatenated
     */
    public AudioConcat(List<File> toBeConcated){
        _listOfConcatenations = new ArrayList<>();
        _listOfConcatenations.add(toBeConcated);
    }

    /**
     *
     * @param toBeConcated the list of audio files to be concatenated
     * @param fileName the name of the output file
     */
    public AudioConcat(List<File> toBeConcated, String fileName){
        _fileName=fileName;
        _listOfConcatenations = new ArrayList<>();
        _listOfConcatenations.add(toBeConcated);
    }

    /**
     *
     * @param textFileToBeConcated a text file containing the names of the files to be concatenated
     *                             The files that are to be concatenated must all be in NameSayer/Database directory
     *                             The structure of the text file MUST BE as follows:
     *                              - each line contains the names that are to be concatenated together
     *                              - each different file is separated by a space
     *                              - the exact file name must be used (with the extension)
     *                              - each new line represents a new bunch of files to be concatenated together
     */
    public AudioConcat(File textFileToBeConcated) throws IOException{
        _textFile = textFileToBeConcated;
        _listOfConcatenations = new ArrayList<>();
        processTextFile();
    }

    /**
     * makes the text file input into a list of list of files, where each list of files represents a different
     * concatenation to be performed.
     * @throws IOException
     */
    private void processTextFile() throws IOException{
        if (_textFile != null){
            BufferedReader br = new BufferedReader(new FileReader(_textFile));
            String line;

            //read each line of text file
            while ((line = br.readLine())!=null) {
                if (!line.equals("")) {

                    String[] stringsOfNamesToBeConcated = line.split("\\s+");

                    //for each line, construct the list of files to be concatenated
                    List<File> newConcatenations = new ArrayList<>();
                    for (String name : stringsOfNamesToBeConcated) {
                        File fileOfName = getFileOfName(name);
                        newConcatenations.add(fileOfName);
                    }
                    _listOfConcatenations.add(newConcatenations);
                }
            }
        }
    }

    /**
     *
     * Processes and Concatenates the lists of files to Documents/NameSayer/ConcatenatedNames/output_full.wav
     */
    public void concatenate() throws InterruptedException, IOException {



        for(List<File> toBeConcated : _listOfConcatenations) {

            List<String> normalisedList = new ArrayList<>();
            List<String> desilencedList = new ArrayList<>();

            //create a 0.25 second silent audio for concatenation purposes
            int i=0;
            ProcessBuilder builderSilence = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -f lavfi -i anullsrc=channel_layout=5.1:sample_rate=48000 -t 0.25 " +NameSayer.concatenationTempPath+"/silent.wav" );
            Process process = builderSilence.start();
            process.waitFor();

            //normalise the audio
            for (File fileToNormalise : toBeConcated) {
                String normalisedFile = "/normalised_" + Integer.toString(i) + ".wav";
                normalisedList.add(normalisedFile);
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -i " + NameSayer.creationsPath + "/" + fileToNormalise.getName() + " -filter:a loudnorm " + NameSayer.concatenationTempPath + normalisedFile);
                Process processNormal = builder.start();
                processNormal.waitFor();
                i++;
            }

            //desilence the audio
            i = 0;
            for (String normalisedFile : normalisedList) {
                String desilencedFile = "/desilenced_" + Integer.toString(i) + ".wav";
                desilencedList.add(desilencedFile);
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -hide_banner -y -i " + NameSayer.concatenationTempPath + normalisedFile + " -af silenceremove=1:0:-35dB:1:5:-35dB:0:peak " + NameSayer.concatenationTempPath + desilencedFile);
                Process processDesilence = builder.start();
                processDesilence.waitFor();
                i++;
            }


            //make a text file with all files to be concatenated
            File concat = new File(NameSayer.concatenationTempPath + "/concat.txt");
            if (!concat.exists()) {
                concat.createNewFile();
            }

            FileWriter writer = new FileWriter(concat);

            for (String desilencedFile : desilencedList) {
                writer.write("file '" + NameSayer.concatenationTempPath + desilencedFile + "'\n");
                writer.write("file '" + NameSayer.concatenationTempPath + "/silent.wav'\n");
            }
            writer.close();

            //get the full name of the audio files put together, if a name has not already been chosen by caller
            String fullName;
            if (_fileName == null) {
                fullName = "";
                for (File file : toBeConcated) {
                    String displayName = file.getName();
                    displayName = displayName.replaceAll("^[^_]*_[^_]*_[^_]*_", "");
                    displayName = displayName.replaceAll("[.][^.]+$", "");
                    displayName = displayName.substring(0,1).toUpperCase() + displayName.substring(1);
                    if (!fullName.equals("")) {
                        fullName = fullName + "_" + displayName;
                    } else {
                        fullName = fullName + displayName;
                    }
                }
            } else {
                fullName = _fileName;
            }

            //find a name that hasnt been used yet for the creation
            String concatedFileName = fullName + "_v1";
            File file = new File(NameSayer.concatenatedNamesPath + "/" + concatedFileName + ".wav");
            boolean exists = false;
            if (file.exists()) {
                 exists = true;
            }

            i=2;
            while (exists){
                concatedFileName = fullName + "_v" + Integer.toString(i);
                File file2 = new File(NameSayer.concatenatedNamesPath + "/" +concatedFileName+ ".wav");
                if (!file2.exists()) {
                    exists = false;
                }
                i++;
            }

            //do the concatenation
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -f concat -safe 0 -i " + NameSayer.concatenationTempPath + "/concat.txt -c copy '" + NameSayer.concatenatedNamesPath + "/" + concatedFileName + ".wav'");
            Process processConcat = builder.start();
            processConcat.waitFor();

            //deletes all temporary files used for concatenation
            FileUtils.cleanDirectory(new File(NameSayer.concatenationTempPath));
        }

    }

    /**
     * looks in the rating file to see if that name has ratings
     * returns the best file if all files are above rating 1
     * otherwise returns a random file of the name if no files are rated or no files have rating above 1
     *
     * @throws IOException
     * @throws FileNotFoundException if the given name input does not exist
     * @return bestFileVersion
     */
    private File getFileOfName(String name) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(NameSayer.directoryPath +"/ratings.txt"));
        String line;

        String nameLower = name.toLowerCase();
        File bestFileVersion = null;
        int maxRatingNumber = 1;

        //scan through the entire text file
        while ((line = br.readLine())!=null) {
            String lineLower = line.toLowerCase();

            //check if the line contains the name of the file.
            if (lineLower.contains("_" + nameLower + ".wav" )){

                String[] ratingInfo = line.split("\\s+");
                int ratingNumber = Integer.parseInt(ratingInfo[1]);

                //if the current file looked at is higher rated than the previous versions of that file, set it as the best file version
                if (ratingNumber > maxRatingNumber) {
                    maxRatingNumber = ratingNumber;
                    bestFileVersion = new File(NameSayer.creationsPath + "/" + ratingInfo[0]);
                }
            }
        }

        //if no rating or no rating above 1 exists for any version of the file
        if (bestFileVersion==null){
            FileFilter filter = new WildcardFileFilter("*_" + name + ".wav", IOCase.INSENSITIVE);
            File[] files = (new File(NameSayer.creationsPath)).listFiles(filter);
            try {
                bestFileVersion = files[0];
            }catch (NullPointerException e){
                throw new FileNotFoundException("No file for that name exists");
            }
        }

        return bestFileVersion;
    }

}
