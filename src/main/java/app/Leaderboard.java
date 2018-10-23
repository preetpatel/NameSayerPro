package app;


import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Leaderboard {

    private Map<String, Integer> _scores = new HashMap<>();


    public Leaderboard() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(NameSayer.directoryPath + "/score.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] userInfo = line.split("\\s+");
                if (userInfo.length >= 5){
                    _scores.put(userInfo[0], Integer.parseInt(userInfo[1]) + Integer.parseInt(userInfo[2]) + Integer.parseInt(userInfo[3]) + Integer.parseInt(userInfo[4]));
                }
            }
            _scores = sortByValue(_scores);

        }catch (IOException e){
            JOptionPane.showMessageDialog(null, "An error occurred while loading the scores: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Map<String, Integer> getScores() {
        return _scores;
    }

    /**
     * Sorts hash map by value.
     * Code courtesy of mkyong from mkyong.com
     * @param unsortMap
     * @return
     */
    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
