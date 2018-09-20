package app;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Name {

    File _file;
    private String _fullName;
    private String _name;
    private String _date;

    public Name(File file){
        _file = file;
        getFileName();
        getNameDate();
    }


    /**
     * generates a human readable date from creation title ONLY WORKS ON NON-COMBINED CREATIONS
     * @return String representing date: d-m-y h:m:s
     */
    public void getNameDate() {

        String processedDate = "";

        String displayDate = _fullName;
        displayDate = displayDate.replaceAll("^[^_]*_", "");
        displayDate = displayDate.replaceAll("[^\\d_-]", "");
        displayDate = displayDate.replaceAll("[.][^.]+$", "");
        displayDate = displayDate.replaceAll("_", " ");
        String[] dateAndTime = displayDate.split(" ", 2);
        dateAndTime[1] = dateAndTime[1].replaceAll("-", ":");

        processedDate = dateAndTime[0] + " " + dateAndTime[1];

        if (!processedDate.equals("")){
            _date = processedDate;
        } else {
            throw new IllegalArgumentException("Something went wrong with processing the file's date");
        }
    }

    private void getFileName() {

        String displayName = _file.getName();
        _fullName = displayName;
        displayName = displayName.replaceAll("^[^_]*_[^_]*_[^_]*_", "");
        displayName = displayName.replaceAll("[.][^.]+$", "");

        _name = displayName;
    }

    public boolean isValid() {

        if (!FilenameUtils.getExtension(_fullName).equals("wav")) {
            return false;
        }

        return true;
    }


    public String getName(){
        return _name;
    }

    public String getFullName(){
        return _fullName;
    }

    public File getFile(){
        return _file;
    }
}
