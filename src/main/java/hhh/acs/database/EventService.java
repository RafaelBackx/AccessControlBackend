package hhh.acs.database;

import hhh.acs.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAll(){
        return eventRepository.findAll();
    }

    public List<Event> getAllByWidgetId(int id){
        return eventRepository.findAllByWidgetId(id);
    }

    public Event get(long id) throws DatabaseException {
        return eventRepository.findById(id).orElseThrow(()-> new DatabaseException("Event with id " + id + " does not exist"));
    }

    public Event insert(Event event){
        return eventRepository.save(event);
    }
}
