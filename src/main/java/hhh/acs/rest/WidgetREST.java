package hhh.acs.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hhh.acs.database.*;
import hhh.acs.model.Door;
import hhh.acs.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

@RestController()
@RequestMapping("/api")
public class WidgetREST {

    @Autowired
    public WidgetService widgetService;
    @Autowired
    public DoorService doorService;

    @CrossOrigin()
    @GetMapping("/widgets")
    public List<Widget> getAll(HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Access-Control-Allow-Methods", "*");
        return widgetService.getAll();
    }

    @CrossOrigin()
    @GetMapping("/widgets/top5used")
    public List<Widget> getTopUsedWidgets(){return widgetService.topUsedWidgets(5);}

    @CrossOrigin()
    @GetMapping("/widgets/{id}")
    public Widget get(@PathVariable("id") int id){
        try {
            return widgetService.get(id);
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @CrossOrigin()
    @PostMapping("/create")
    public Widget create(@RequestBody Widget widget){
        return widgetService.create(widget);
    }

    @CrossOrigin()
    @DeleteMapping("/delete/{id}")
    public Widget deleteWidget(@PathVariable("id") int id){
        try {
            return widgetService.delete(id);
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @CrossOrigin()
    @PutMapping("/update/{id}")
    public String updateWidget(@PathVariable("id") int id, @RequestBody Widget widget){
        try {
            return widgetService.updateWidget(id, widget);
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
