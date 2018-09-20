package app;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Name {

    List<File> _files;
    private String _fullName;
    private String _name;

    public Name(File file){
        getFileName(file);
        getAllFilesOfName();
    }

    private void getFileName(File file) {

        String displayName = file.getName();

        _fullName = displayName;

        displayName = displayName.replaceAll("^[^_]*_[^_]*_[^_]*_", "");
        displayName = displayName.replaceAll("[.][^.]+$", "");

        _name = displayName;
    }

    /**
     * checks if the file provided for the name is a valid .wav file
     * @return true if te file is valid
     */
    public boolean isValid() {

        if (!FilenameUtils.getExtension(_fullName).equals("wav")) {
            return false;
        }

        return true;
    }

    private void getAllFilesOfName(){
        //TODO get all files that include that name
        File dir = new File(NameSayer.creationsPath);
        FileFilter filter = new WildcardFileFilter("*"+_name+".wav");
        File[] files = dir.listFiles(filter);
        _files = Arrays.asList(files);

    }

    public String getName(){
        return _name;
    }

}
