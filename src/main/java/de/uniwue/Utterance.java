package de.uniwue;

public class Utterance {
    private String ID;
    private String speaker;
    private String statement;
    private int start;  //supports timestamps of up to ~596 hours
    private int end;        //supports timestamps of up to ~596 hours

    public Utterance(String ID, String speaker, String statement, int start, int end) {
        this.ID = ID;
        this.speaker = speaker;
        this.statement = statement;
        this.start = start;
        this.end = end;
    }

    public String getID() {
        return ID;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getStatement() {
        return statement;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int[] getTimestamp() {
        int[] timestamp = new int[2];
        timestamp[0] = start;
        timestamp[1] = end;
        return timestamp;
    }
}
