package hhh.acs.model;

import org.springframework.data.jpa.repository.Modifying;

public enum Mode {
    UNLOCK("unlock"),
    LOCK("lock"),
    RELEASE("release");

    private String command;

    Mode(String command){
        this.command = command;
    }

    public String getCommand(){
        return this.command;
    }
}
