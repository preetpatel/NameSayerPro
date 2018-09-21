package app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//Stores the different files which make up a single creation version
public class CreationFile {

    List<File> _files;
    String _creationVersion;

    public CreationFile(String creationVersion){
        _files = new ArrayList<>();
        _creationVersion = creationVersion;
    }

    public CreationFile addFile(File file){
        _files.add(file);
        return this;
    }

    public int getFilesNumber(){
        return _files.size();
    }

}
