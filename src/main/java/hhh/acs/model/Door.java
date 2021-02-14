package hhh.acs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.util.List;

@Entity
public class Door {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Widget widget;

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

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Door){
            Door other = (Door)obj;
            return  (other.getId() == this.id);
        }
        return false;
    }
}
