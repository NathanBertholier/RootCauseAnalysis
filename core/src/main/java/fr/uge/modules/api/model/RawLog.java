package fr.uge.modules.api.model;

public class RawLog {
    private String log;

    public RawLog(){}
    public RawLog(String log){
        this.log = log;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public String toString() {
        return "Log{" +
                "log='" + log + '\'' +
                '}';
    }
}
