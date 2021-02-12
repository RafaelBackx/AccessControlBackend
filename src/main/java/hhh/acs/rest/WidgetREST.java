package hhh.acs.rest;

import hhh.acs.database.DoorRepository;
import hhh.acs.database.WidgetRepository;
import hhh.acs.model.Door;
import hhh.acs.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("/api")
public class WidgetREST {

    @Autowired
    public WidgetRepository repository;
    @Autowired
    public DoorRepository doorRepository;

    @GetMapping("/widgets")
    public List<Widget> getAll(){
        return repository.findAll();
    }

    @PostMapping("/create")
    public String create(@ModelAttribute @Valid Widget widget){
        System.out.println(widget);
        return null;
//        System.out.println("hello, you are creating a widget");
//        Door door = new Door();
//        door.setId(5);
//        door.setName("mijn deur");
//        List<Door> deuren = new ArrayList<>();
//        deuren.add(door);
//        Widget w = new Widget("test", BigInteger.valueOf(200),"#123456",deuren, "open-door");
//        doorRepository.save(door);
//        repository.save(w);
//        return "Widget saved succesfully";
    }
}
