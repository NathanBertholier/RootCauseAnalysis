package fr.uge.modules.api.server.external.model;

import java.io.Serializable;

public class Rawlog implements Serializable {
    private String log;

    public Rawlog(){}
    public Rawlog(String log){
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
