package de.uniwue;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class Utterance {
    private String ID;
    private List<Statement> statements = new ArrayList<>();
    private int start;  //supports timestamps of up to ~596 hours
    private int end;        //supports timestamps of up to ~596 hours
    private String filePath;

    public Utterance(String ID, List<Statement> lStatement, int start, int end, String filePath) {
        this.ID = ID;
        this.statements.addAll(lStatement);
        this.start = start;
        this.end = end;
        this.filePath = filePath;
    }

    public String getID() {
        return ID;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public String getStatementsAsString() {
        String string = "";
        for(Statement statement : statements) {
            string += statement.getStatement();
        }
        return string;
    }

    public String getLiteralStatementAsString() {
        String string = "";
        for(Statement statement : statements) {
            string += statement.getLiteralStatement();
        }
        return string;
    }

    public String getAllSpeakersasString() {
        String speakers = "";
        for(Statement statement : statements) {
            speakers += statement.getSpeaker();
        }
        return speakers;
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

    public String getTimestampAsString() {
        return getStart() + "-" + getEnd();
    }

    public String toString() {
        String uttString = "";
        uttString+=("file id   : " + ID + "\n");
        for(Statement statement : statements) {
            uttString += "statement: " + statement.toString() + "\n";
        }
        uttString+=("timeSTART : " + start + "\n");
        uttString+=("timeEND   : " + end + "\n");
        return uttString;
    }

    public String[][] toArray() {
        String[][] array = new String[statements.size()][4];
        Statement[] statementArray = (Statement[]) statements.toArray();
        for(int i = 0; i < statements.size(); i++) {
            array[i][0] = statementArray[i].getSpeaker();
            array[i][1] = statementArray[i].getStatement();
            array[i][2] = this.ID;
            array[i][3] = this.start + " - " + this.end;
        }

        return array;
    }

    public Boolean playMedia() throws UnsupportedAudioFileException,
            IOException, LineUnavailableException, JavaLayerException {

        MediaPlayer mp = new MediaPlayer(filePath,start,end);
        mp.playMedia();

        return true;
    }
}
