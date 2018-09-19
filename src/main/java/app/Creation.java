package app;


import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Creation {

    private List<File> files;

    public Creation() {
        files = new ArrayList<>();
    }

    public void addName(File file) {
        files.add(file);
    }

    public String getCreationName() {
        if (files.isEmpty()) {
            return null;
        }

        List<String> names = new ArrayList<>();

        for (File file : files) {
            String displayName = file.getName();
            displayName = displayName.replaceAll("^[^_]*_[^_]*_[^_]*_", "");
            displayName = displayName.replaceAll("[.][^.]+$", "");
            names.add(displayName);

        }

        return String.join(" ", names);
    }

    public boolean isValid() {
        for (File file : files) {
            if (!FilenameUtils.getExtension(file.getName()).equals("wav")) {
                return false;
            }
        }
        return true;
    }

    public void destroy() {
        files.clear();
    }

}
