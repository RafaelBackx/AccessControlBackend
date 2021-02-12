package hhh.acs.database.dto;

import hhh.acs.model.Door;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

public class WidgetDTO {
    private int id;
    private String name;
    private BigInteger duration;
    private String color;
    private String icon;
    private List<Door> doors;

    public WidgetDTO(){}

    public WidgetDTO(String name, BigInteger  duration, String color, List<Door> doors, String icon){
        this.setName(name);
        this.setDuration(duration);
        this.setColor(color);
        this.setDoors(doors);
        this.setIcon(icon);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getDuration() {
        return duration;
    }

    public void setDuration(BigInteger duration) {
        this.duration = duration;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public void setDoors(List<Door> doors) {
        this.doors = doors;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Widget{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", color='" + color + '\'' +
                ", icon='" + icon + '\'' +
                ", doors=" + doors +
                '}';
    }
}
