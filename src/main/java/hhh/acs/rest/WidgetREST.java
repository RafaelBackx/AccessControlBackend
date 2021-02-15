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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

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
        List<Door> persistedDoors = new ArrayList<>();
        for (Door door : widget.getDoors()){
            Door d = doorRepository.findById(door.getId()).orElseGet(() -> null);
            if (d == null){
                persistedDoors.add(doorRepository.save(door));
            }
            else{
                persistedDoors.add(d);
            }
        }
        widget.setDoors(persistedDoors);
        return widgetRepository.save(widget);
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
        List<Door> newDoors = widget.getDoors();
        Widget oldWidget = widgetRepository.findById(id).orElseThrow(() -> new DatabaseException("Widget with " + id + " not found in the database"));
        List<Door> oldDoors = oldWidget.getDoors();
        if (newDoors != null){
            for (Door door : oldDoors){
                if (!newDoors.contains(door)){
                    doorRepository.delete(door);
                }
            }
        }
        widgetRepository.save(widget);
        return widget.toString();
    }
}
