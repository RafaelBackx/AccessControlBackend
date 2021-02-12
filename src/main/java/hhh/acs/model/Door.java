package hhh.acs.model;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.util.List;

@Entity
public class Door {
    private String name;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToMany(mappedBy = "doors")
    private List<Widget> widgets;
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
}
