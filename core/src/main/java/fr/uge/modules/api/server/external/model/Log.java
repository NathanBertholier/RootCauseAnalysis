package fr.uge.modules.api.server.external.model;

import java.io.Serializable;

public class Log implements Serializable {
    private int id;
    private String log;

    public Log(){}
    public Log(Integer id, String log){
        this.id = id;
        this.log = log;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
                "id=" + id +
                ", log='" + log + '\'' +
                '}';
    }
}
