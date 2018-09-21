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
        _name = getFileName(file);
        _files = getAllFilesOfName(new File(NameSayer.creationsPath));
    }

    private String getFileName(File file) {
        String displayName = file.getName();
        _fullName = displayName;
        displayName = displayName.replaceAll("^[^_]*_[^_]*_[^_]*_", "");
        displayName = displayName.replaceAll("[.][^.]+$", "");
        return displayName;
    }

    /**
     * checks if the file provided for the name is a valid .wav file
     * @return true if the file is valid
     */
    public boolean isValid() {

        if (!FilenameUtils.getExtension(_fullName).equals("wav")) {
            return false;
        }

        return true;
    }

    public List<File> getAllFilesOfName(File dir){
        //File dir = new File(NameSayer.creationsPath);
        FileFilter filter = new WildcardFileFilter("*_"+_name+".wav");
        File[] files = dir.listFiles(filter);
        return Arrays.asList(files);

    }

    public int getNameVersionsNumber(){
        return _files.size();
    }

    public String getName(){
        return _name;
    }

    public List<File> getFiles() {
        return _files;
    }
}
