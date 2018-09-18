package app;


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

    public void destroy() {
        files.clear();
    }

}
