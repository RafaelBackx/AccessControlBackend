package hhh.acs.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hhh.acs.database.DatabaseException;
import hhh.acs.database.DoorRepository;
import hhh.acs.database.WidgetRepository;
import hhh.acs.model.Door;
import hhh.acs.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("/api")
public class WidgetREST {

    @Autowired
    public WidgetRepository widgetRepository;
    @Autowired
    public DoorRepository doorRepository;

    @CrossOrigin()
    @GetMapping("/widgets")
    public List<Widget> getAll(){
        return widgetRepository.findAll();
    }

    @CrossOrigin()
    @GetMapping("/widgets/{id}")
    public Widget get(@PathVariable("id") int id){
        return widgetRepository.findById(id).orElseThrow(() -> new DatabaseException("Widget with " + id + " not found in the database"));
    }

    @CrossOrigin()
    @PostMapping("/create")
    public Widget create(@RequestBody Widget widget){
        if (widget.getDoors() != null){
            for (Door door : widget.getDoors()){
                door.setWidget(widget);
            }
        }
        widgetRepository.save(widget);
        if (widget.getDoors() != null){
            for (Door door : widget.getDoors()){
                doorRepository.save(door);
            }
        }
        Widget addedWidget = widgetRepository.findById(widget.getId()).orElseGet(() -> null);
        return addedWidget;
    }

    @CrossOrigin()
    @DeleteMapping("/delete/{id}")
    public Widget deleteWidget(@PathVariable("id") int id){
        System.out.println("deleting widget with id " + id);
        Widget widget = widgetRepository.findById(id).orElseThrow(() -> new DatabaseException("Widget with " + id + " not found in the database"));
        widgetRepository.delete(widget);
        return widget;
    }

    @CrossOrigin()
    @PutMapping("/update/{id}")
    public String updateWidget(@PathVariable("id") int id, @RequestBody Widget widget){
        System.out.println(id);
        List<Door> oldDoors = doorRepository.findAllByWidgetId(id);
        List<Door> newDoors = widget.getDoors();
        if (newDoors != null){
            for (Door door : oldDoors){
                if (!newDoors.contains(door)){
                    doorRepository.delete(door);
                }
            }
            for (Door door : newDoors){
                door.setWidget(widget);
            }
        }
        widgetRepository.save(widget);
        return widget.toString();
    }
}
