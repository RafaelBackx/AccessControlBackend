package hhh.acs.rest;

import hhh.acs.database.DoorRepository;
import hhh.acs.database.WidgetRepository;
import hhh.acs.model.Door;
import hhh.acs.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController()
public class WidgetREST {

    @Autowired
    public WidgetRepository repository;
    @Autowired
    public DoorRepository doorRepository;

    @GetMapping("/widgets")
    public List<Widget> getAll(){
        return repository.findAll();
    }

    @GetMapping("/create")
    public String create(){
        System.out.println("hello, you are creating a widget");
        Door door = new Door();
        door.setId(5);
        door.setName("mijn deur");
        List<Door> deuren = new ArrayList<>();
        deuren.add(door);
        Widget widget = new Widget("test", BigInteger.valueOf(200),"#123456",deuren, "open-door");
        doorRepository.save(door);
        repository.save(widget);
        return "Widget saved succesfully";
    }
}
