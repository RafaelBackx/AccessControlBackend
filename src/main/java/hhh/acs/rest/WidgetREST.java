package hhh.acs.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hhh.acs.database.DoorRepository;
import hhh.acs.database.WidgetRepository;
import hhh.acs.database.dto.WidgetDTO;
import hhh.acs.model.Door;
import hhh.acs.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/create")
    public String create(@RequestBody Widget widget){
        System.out.println(widget);
        if (widget.getDoors() != null){
            for (Door door : widget.getDoors()){
                System.out.println(door);
                doorRepository.save(door);
            }
        }
        widgetRepository.save(widget);
        return "doors created";
    }

    //TODO add remove path
}
