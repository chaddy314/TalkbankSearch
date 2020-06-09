package de.uniwue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Corpus {
    private List<Utterance> utterances;
    private String fileEnding = ".cha";
    private String filePath = "";

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
                filePath = file.getAbsolutePath();
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
        longString = longString.replaceAll("\r?\n", "");
        longString = longString.replaceAll("\t","");
        longString = longString.replaceAll("\\(\\d*\\.\\d*\\)", "");
        String[] splitString = longString.split("\u0015");

        Boolean started = false;
        String speaker = "NO_SPEAKER";
        String statement = "NO_STATEMENT";
        Statement oStatement;
        List<Statement> lStatement = new ArrayList<>();
        String[] temp;
        int start = 0;
        int end = 0;

        if(splitString.length == 1) {
            return utteranceList;
        }
        String lastname = "";
        for(String segment : splitString) {
            //System.out.println("split is: " + segment);
            if(segment.startsWith("@")) {
                if(!segment.startsWith("@End")) {
                    temp = segment.split("\\*");
                    lStatement = parseStatements("*"+temp[1].trim());
                }
            } else if(segment.startsWith("%")){
                if(segment.contains("*")) {
                    started = true;
                    int starPos = segment.indexOf("*");
                    segment = segment.substring(starPos);
                    lStatement = parseStatements(segment.trim());
                    lastname = lStatement.get(lStatement.size()-1).getSpeaker();
                }
            } else if(segment.startsWith("*")) {
                started = true;
                lStatement = parseStatements(segment.trim());
                lastname = lStatement.get(lStatement.size()-1).getSpeaker();
            } else if(Pattern.compile("\\d+_\\d+").matcher(segment).find()) {
                temp = segment.split("_");
                start =  Integer.parseInt(clean(temp[0]));
                end = Integer.parseInt(clean(temp[1]));

                if(end <= start) {
                    System.out.println("ENDING BEFORE START, STOPPING...");
                    break;
                }
                utteranceList.add(new Utterance(id,lStatement,start,end,filePath));
                lStatement.clear();
            } else {
                lStatement.add(new Statement(lastname,clean(segment),segment.trim()));
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

    public List<Utterance> getUtterances() {
        return utterances;
    }

    private List<Statement> parseStatements(String string) {
        List<Statement> lStatement= new ArrayList<>();

        String pattern = "\\*(\\w+)\\:";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(string);

        String[] strings = string.split("\\*(\\w+)\\:");
        int n = 1;
        int findings = 0;
        String statement;
        String literalStatement;
        while(m.find()) {

            String name = m.group(1);
            if(strings.length == 0) {
                statement = "NO SPOKEN WORD";
                literalStatement = statement;
            } else {
                /*if(strings[n].equals("") && strings[n].isEmpty()) {
                    n++;
                }*/
                //statement = strings[n].trim().replaceAll("[\\W\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}&&[^\\s]]", "");
                statement = clean(strings[n]);
                literalStatement = strings[n].trim();
            }
            lStatement.add(new Statement(name,statement,literalStatement));
            findings++;
            n++;
        }
        /*for(String sub : strings) {
            if(!sub.equals("") && !sub.isEmpty()) {
                String[] sm = sub.split(":",2);
                sm[1] = sm[1].replaceAll("\\p{Cc}", "");
                sm[1] = sm[1].replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}&&[^\\s]]", "");
                sm[1] = sm[1].trim().replaceAll(" +", " ");
                lStatement.add(new Statement(sm[0],sm[1]));
            }
        }*/
        return lStatement;
    }

    private String clean(String string) {
        return string.trim().replaceAll("[\\W\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}&&[^\\s]]", "");
    }
}
