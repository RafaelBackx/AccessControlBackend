package hhh.acs.database;

import hhh.acs.model.Door;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DoorService {

    @Autowired
    private DoorRepository doorRepository;

    public List<Door> getAll(){
        return doorRepository.findAll();
    }

    public Door get(int id) throws DatabaseException{
        var door = doorRepository.findById(id);
        return door.orElseThrow(() -> new DatabaseException("Door with id" + id + " does not exist"));
    }

    public Door insert(Door door) throws IllegalArgumentException{
        if (door != null){
            return doorRepository.save(door);
        }
        else{
            throw new IllegalArgumentException("Door cannot be null");
        }
    }
}
