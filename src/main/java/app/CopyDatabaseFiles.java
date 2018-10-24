/**
 * CopyDatabaseFiles.java
 * Copyright Preet Patel, 2018
 * A class which, on startup, if a database does not exist, copies the database directory in the project file to the
 * NameSayer file in Home/Documents
 *
 * @Author Chuyang Chen
 * Date Created: 21 October, 2018
 */
package app;

import org.apache.commons.io.FileUtils;

import java.io.File;

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
