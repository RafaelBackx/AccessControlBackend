package hhh.acs.model;

import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Entity
public class Widget {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private Long duration;
    private String color;
    private String icon;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Door> doors;
    private int counter;

    public Widget(){}

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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
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

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Widget){
            Widget other = (Widget) obj;
            return  (other.getId() == this.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void increment() {
        this.counter++;
    }
}
