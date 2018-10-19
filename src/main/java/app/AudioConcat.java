package app;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.*;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AudioConcat {


    private File _textFile;
    private List<List<File>> _listOfConcatenations;
    private List<String> nonExistantNames;

    /**
     * @param toBeConcated the list of names to be concatenated
     * @throws FileNotFoundException if an input name is not found within the database
     */
    public AudioConcat(List<String> toBeConcated)throws IOException {
        _listOfConcatenations = new ArrayList<>();
        List<File> bestFileList = new ArrayList<>();
        nonExistantNames = new ArrayList<>();

        if (toBeConcated.size() == 1){
            Name name = new Name(toBeConcated.get(0));
            for (File file : name.getAllFilesOfName(new File(NameSayer.creationsPath))){
                List<File> fileList = new ArrayList<>();
                fileList.add(file);
                _listOfConcatenations.add(fileList);
            }

        } else {
            for (String name : toBeConcated) {
                if (getFileOfName(name) != null) {
                    bestFileList.add(getFileOfName(name));
                } else {
                    throw new FileNotFoundException();
                }
            }
        }
        _listOfConcatenations.add(bestFileList);
    }

    /**
     *
     * @param textFileToBeConcated a text file containing the names of the files to be concatenated
     *                             The files that are to be concatenated must all be in NameSayer/Database directory
     *                             The structure of the text file MUST BE as follows:
     *                              - each line contains the names that are to be concatenated together
     *                              - each different names is separated by a space
     *                              - each new line represents a new list of names to be concatenated together
     */
    public AudioConcat(File textFileToBeConcated) throws IOException {
        _textFile = textFileToBeConcated;
        _listOfConcatenations = new ArrayList<>();
        nonExistantNames = new ArrayList<>();
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
                    boolean allNamesExist = true;

                    String[] stringsOfNamesToBeConcated = line.split("\\s+");

                    //for each line, construct the list of files to be concatenated
                    List<File> newConcatenations = new ArrayList<>();
                    for (String name : stringsOfNamesToBeConcated) {

                        File fileOfName = getFileOfName(name);
                        if (fileOfName != null) {
                            newConcatenations.add(fileOfName);
                        } else {
                            allNamesExist = false;
                        }
                    }

                    if (allNamesExist) {
                        _listOfConcatenations.add(newConcatenations);
                    }
                }
            }
        }

    }

    /**
     *
     * Processes and Concatenates the lists of files to Documents/NameSayer/ConcatenatedNames/[OUTPUT].wav
     *
     * @return NonExistantNames - a list of strings which contains all the input names that do not exist
     */
    public List<String> concatenate() throws InterruptedException, IOException {

        for(List<File> toBeConcated : _listOfConcatenations) {

            List<String> normalisedList = new ArrayList<>();
            List<String> desilencedList = new ArrayList<>();

            //create a 0.25 second silent audio for concatenation purposes
            ProcessBuilder builderSilence = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -f lavfi -i anullsrc=channel_layout=5.1:sample_rate=48000 -t 0.2 " +NameSayer.concatenationTempPath+"/silent.wav" );
            Process process = builderSilence.start();
            process.waitFor();
            int i=0;

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
                writer.write("file '"+NameSayer.concatenationTempPath+"/silent.wav'\n");
            }
            writer.close();

            //get the full name of the audio files put together, if a name has not already been chosen by caller
            String fullName;

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


            //find a name that hasnt been used yet for the creation
            String concatedFileName = fullName;
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

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            Date date = new Date();

            //do the concatenation
            if(toBeConcated.size() == 1) {
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -f concat -safe 0 -i " + NameSayer.concatenationTempPath + "/concat.txt -c copy '" + NameSayer.concatenatedNamesPath + "/" + toBeConcated.get(0).getName()+ "'");
                Process processConcat = builder.start();
                processConcat.waitFor();
            } else {
                ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "ffmpeg -y -f concat -safe 0 -i " + NameSayer.concatenationTempPath + "/concat.txt -c copy '" + NameSayer.concatenatedNamesPath + "/namesayer_" + dateFormat.format(date) + "_" + concatedFileName + ".wav'");
                Process processConcat = builder.start();
                processConcat.waitFor();
            }

            //deletes all temporary files used for concatenation
            FileUtils.cleanDirectory(new File(NameSayer.concatenationTempPath));
        }

        return nonExistantNames;

    }

    /**
     * looks in the rating file to see if that name has ratings
     * returns the best file if all files are above rating 1
     * otherwise returns a random file of the name if no files are rated or no files have rating above 1
     *
     * @throws IOException
     * @return bestFileVersion is null if a file does not exist for given name, otherwise returns the highest rated file for that name
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

        //if no rating or no rating above 1 exists for any version of the file, get the first version that comes up
        if (bestFileVersion==null){
            FileFilter filter = new WildcardFileFilter("*_" + name + ".wav", IOCase.INSENSITIVE);
            File[] files = (new File(NameSayer.creationsPath)).listFiles(filter);

            try {
                bestFileVersion = files[files.length-1];
            }catch (Exception e){
                nonExistantNames.add(name);
                return null;
            }
        }

        return bestFileVersion;
    }

    /**
     * deletes all concatenated name files from NameSayer/ConcatenatedNames
     * @throws IOException If deletion goes wrong for some reason
     */
    public static void deleteAllFiles() throws IOException{
        //deletes all temporary files used for concatenation
        FileUtils.cleanDirectory(new File(NameSayer.concatenatedNamesPath));
        File concatenatedTempStorage = new File(NameSayer.concatenationTempPath);

        if (!concatenatedTempStorage.exists()) {
            if (!concatenatedTempStorage.mkdirs()) {
                throw new IOException("Something went wrong with creating the temporary storage");
            }
        }
    }

}