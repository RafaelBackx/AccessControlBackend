package hhh.acs.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Door {
    @Id
    private int id;
    private String name;

    public Door(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Door{" +
                "id=" + id +
                ", name='" + name + '\'' +
                "}";
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Door){
            Door other = (Door)obj;
            return  (other.getId() == this.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
