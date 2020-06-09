package de.uniwue;

public class Statement {
    String speaker;
    String statement;

    public String getLiteralStatement() {
        return literalStatement;
    }

    public void setLiteralStatement(String literalStatement) {
        this.literalStatement = literalStatement;
    }

    String literalStatement;

    public Statement(String speaker, String statement, String literalStatement) {
        this.speaker = speaker;
        this.statement = statement;
        this.literalStatement = literalStatement;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String toString(){
        return "\n\tSpeaker: " + speaker + ";\n\tText: " + statement + ";\n\tLiteral: " + literalStatement;
    }
}
