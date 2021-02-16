package hhh.acs.database;

import hhh.acs.model.Event;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAll(){
        return eventRepository.findAll();
    }

    public List<Event> getAllByWidgetId(int id){
        return eventRepository.findAllByWidgetId(id);
    }
}
