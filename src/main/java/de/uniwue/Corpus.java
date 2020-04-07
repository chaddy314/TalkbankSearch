package de.uniwue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Corpus {
    private List<Utterance> utterances;
    private String fileEnding = ".cha";

    public Corpus () {
        this.utterances = new ArrayList<Utterance>();
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
                utterances.addAll(parseFile(file));
                System.out.println("File " + file.getName() + " added to Corpus");
                files++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public List<Utterance> parseFile(File file) {
        List<Utterance> utteranceList = new ArrayList<Utterance>();
        String id = file.getName();
        String longString = readAllBytes(file.getAbsolutePath());
        String[] splitString = longString.split("\u0015");

        Boolean started = false;
        String speaker = "NO_SPEAKER";
        String statement = "NO_STATEMENT";
        String[] temp;
        int start = 0;
        int end = 0;

        for(String segment : splitString) {
            if(segment.startsWith("@")) {
                if(!segment.startsWith("@End")) {
                    temp = segment.split("\\*");
                    temp = temp[1].split(":");
                    speaker = temp[0];
                    System.out.println(speaker);
                    statement = temp[1];
                    System.out.println(statement);
                }
            } else if(segment.startsWith("*")) {
                started = true;
                temp=segment.split(":",2);
                speaker = temp[0].substring(1);
                statement = temp[1];
                statement = statement.replaceAll("\\p{Cc}", "");
                statement = statement.trim().replaceAll(" +", " ");
            } else if(Pattern.compile("\\d+_\\d+").matcher(segment).find()) {
                temp = segment.split("_");
                start =  Integer.parseInt(temp[0]);
                end = Integer.parseInt(temp[1]);

                if(end <= start) {
                    System.out.println("ENDING BEFOR START, STOPPING...");
                    break;
                }
                utteranceList.add(new Utterance(id,speaker,statement,start,end));
            }
        }

        return utteranceList;

        /*String ID = file.getName();
        String speaker;
        String statement;
        int[] timestamp;
        String timestampString;

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                if(line.startsWith("@")) {
                    //TODO parse and add METADATA functionality
                    line = reader.readLine();
                    System.out.println(line);
                } else if (line.startsWith("*")) {
                    String tempLines[] = line.split(":",2);
                    speaker = tempLines[0];
                    System.out.println(speaker);
                    statement = tempLines[1];
                    System.out.println(statement);
                    line = tempLines[1];
                    while (line != null && !Pattern.compile("\\d+_\\d+").matcher(line).find()) {
                        line = reader.readLine();
                        System.out.println("line in while: "+line);
                        statement += line;
                        System.out.println(statement);
                    }
                    statement += line;
                    if(Pattern.compile("\\d+_\\d+").matcher(line).find()) {
                        System.out.println("THE FUCKING LINe IS: "+ line);
                        timestamp = parseTimestamp(Pattern.compile("\\d+_\\d+").matcher(line).group(1));
                        System.out.println(statement);
                        utterances.put(ID,new Utterance(ID,speaker,statement,timestamp[0],timestamp[1]));
                    } else {
                        System.out.println("templines 1: " +tempLines[1]);
                    }

                    line = reader.readLine();
                } else {
                    System.out.println("YOU SHOULD NOT BE HERE, Or meta comment");
                    System.out.println("line is" + line);
                    line = reader.readLine();
                }
                System.out.println("Statement is:");
//                System.out.println(utterances.get(ID).getSpeaker() + " : " + utterances.get(ID).getStatement());
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }*/
    }
    private static String readAllBytes(String filePath)
    {
        String content = "";
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return content;
    }
    private int[] parseTimestamp(String timestampString) {
        int[] timestamp = new int[2];
        String[] tempString = timestampString.split("_");
        timestamp[0] = Integer.parseInt(tempString[0]);
        timestamp[1] = Integer.parseInt(tempString[1]);

        return timestamp;
    }

}
