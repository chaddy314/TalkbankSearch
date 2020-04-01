package de.uniwue;

import java.io.*;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Corpus {
    private TreeMap<String,Utterance> utterances;
    private String fileEnding = ".cha";

    public Corpus () {
        this.utterances = new TreeMap<String,Utterance>();
    }

    public int putCorpus(String path) {
        int files = 0;  //number of files add, -1 if path not a folder
        File dir = new File(path);
        if(!dir.isDirectory()) {
            System.out.println("Not a Directory");
            return -1;
        }
        File[] FilesInDir = dir.listFiles((d, name) -> name.endsWith(fileEnding));

        try {
            for(File file : FilesInDir) {
                putFile(file);
                files++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public void putFile(File file) {
        String ID;
        String speaker;
        String statement;
        int[] timestamp;
        String timestampString;

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                if(line.startsWith("@")) {
                    //TODO parse and add METADATA functionality
                    line = reader.readLine();
                } else if (line.startsWith("*")) {
                    String tempLines[] = line.split(":",2);
                    speaker = tempLines[0];
                    if(Pattern.compile("\\d+_\\d+").matcher(tempLines[1]).find()) {
                        timestamp = parseTimestamp(Pattern.compile("(\\d+_\\d+)").matcher(tempLines[1]).group(1));

                    } else {

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }
    }

    private int[] parseTimestamp(String timestampString) {
        int[] timestamp = new int[2];
        String[] tempString = timestampString.split("_");
        timestamp[0] = Integer.parseInt(tempString[0]);
        timestamp[1] = Integer.parseInt(tempString[1]);

        return timestamp;
    }

}
