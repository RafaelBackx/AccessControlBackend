package hhh.acs.database;

import hhh.acs.model.Door;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DoorService {

    @Autowired
    private DoorRepository doorRepository;

    public List<Door> getAll(){
        return doorRepository.findAll();
    }
}
