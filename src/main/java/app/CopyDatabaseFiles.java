package app;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CopyDatabaseFiles implements Runnable {

    public CopyDatabaseFiles(){
    }

    @Override
    /**
     *Copies the database in the given directory to the database file in documents
     */
    public void run(){

        if (!(new File(NameSayer.audioPath).exists())) {
            File sourceDirectory = new File(System.getProperty("user.dir") + "/Database");
            File targetDirectory = new File(NameSayer.directoryPath + "/Database");

            //copy source to target
            try {
                FileUtils.copyDirectory(sourceDirectory, targetDirectory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
