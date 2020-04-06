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

    public int putCorpus(File dir) {
        int files = 0;  //number of files add, -1 if path not a folder
        if(!dir.isDirectory()) {
            System.out.println("Not a Directory");
            return -1;
        }
        File[] FilesInDir = dir.listFiles((d, name) -> name.endsWith(fileEnding));

        try {
            for(File file : FilesInDir) {
                putFile(file);
                System.out.println("File " + file.getName() + " added to Corpus");
                files++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public void putFile(File file) {
        String ID = file.getName();
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
                    statement = tempLines[1];
                    line = tempLines[1];
                    while (!Pattern.compile("\\d+_\\d+").matcher(line).find()) {
                        line = reader.readLine();
                        statement += line;
                    }
                    statement += line;
                    timestamp = parseTimestamp(Pattern.compile("(\\d+_\\d+)").matcher(tempLines[1]).group(1));
                    utterances.put(ID,new Utterance(ID,speaker,statement,timestamp[0],timestamp[1]));
                    /*Üif(Pattern.compile("\\d+_\\d+").matcher(tempLines[1]).find()) {
                        timestamp = parseTimestamp(Pattern.compile("(\\d+_\\d+)").matcher(tempLines[1]).group(1));
                        utterances.put(ID,new Utterance(ID,speaker,statement,timestamp[0],timestamp[1]));
                    } else {
                        line = reader.readLine();
                        while(line != null && !Pattern.compile("\\d+_\\d+").matcher(tempLines[1]).find()) {
                            statement += line;
                            line = reader.readLine();
                        }
                        statement += line;
                        timestamp = parseTimestamp(Pattern.compile("(\\d+_\\d+)").matcher(tempLines[1]).group(1));
                        utterances.put(ID,new Utterance(ID,speaker,statement,timestamp[0],timestamp[1]));
                    }*/
                    line = reader.readLine();
                } else {
                    System.out.println("YOU SHOULD NOT BE HERE");
                    System.out.println("line is" + line);
                }
                System.out.println("Statement is:");
                System.out.println(utterances.get(ID).getSpeaker() + " : " + utterances.get(ID).getStatement());
                System.out.println();
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
