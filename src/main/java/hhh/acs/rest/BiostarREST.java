package hhh.acs.rest;

import hhh.acs.configuration.BackendProperties;
import hhh.acs.controller.EventController;
import hhh.acs.database.*;
import hhh.acs.model.*;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.xml.crypto.Data;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController()
@RequestMapping("/api")
public class BiostarREST implements InitializingBean {
    @Autowired
    private BackendProperties backend;
    private BiostarAPIRequests biostarAPIRequests;
    @Autowired
    private EventService eventRepository;
    @Autowired
    private WidgetService widgetrepository;
    @Autowired
    private DoorService doorRepository;
    @Autowired
    private EventController eventController;

    public BiostarREST() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException { }

    @Override
    public void afterPropertiesSet() throws Exception {
        biostarAPIRequests = new BiostarAPIRequests(backend.getUrl());
        biostarAPIRequests.logIn("admin","t");
    }

    @GetMapping("/login")
    public void login() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String result = biostarAPIRequests.logIn("admin","t");
        System.out.println(result);
    }

    @CrossOrigin()
    @PostMapping("/create/event")
    public String addEvent(@RequestBody Event event) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        System.out.println(event);
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        Long unixtimestamp = currentDate.toEpochSecond(currentTime, ZoneOffset.UTC);
        event.setStartTime(unixtimestamp);
        List<Door> persistedDoors = new ArrayList<>();
        for (Door door : event.getDoors()){
            try{
                Door d = doorRepository.get(door.getId());
                persistedDoors.add(d);
            }catch (DatabaseException e){
                System.out.println(e.getMessage());
                persistedDoors.add(doorRepository.insert(door));
            }
        }
        event.setDoors(persistedDoors);
        // increment widget counter
        var widget = event.getWidget();
        if (widget != null){
            try{
                Widget persistedWidget = widgetrepository.get(widget.getId());
                System.out.println("incremented");
                persistedWidget.increment();
                widgetrepository.updateCounter(persistedWidget.getCounter(),persistedWidget.getId());
            }catch (DatabaseException e){
                System.out.println("Widget does not exist");
            }
        }
        var persistedEvent = eventRepository.insert(event);
        var ids = eventController.transformDoorsToIds(event);
        JSONObject json = new JSONObject();
        try {
            biostarAPIRequests.lockUnlockReleaseDoor(ids,Mode.UNLOCK);
            System.out.println("opened");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            json.put("id", -1);
            json.put("message", "kon niet geopend worden!");
            json.put("success",false);
            System.out.println(json);
            return json.toString();
        }
        // schedule the event
        eventController.addEvent(event);
        json.put("id", persistedEvent.getId());
        json.put("message", "succesvol geopend!");
        json.put("success",true);
        System.out.println(json);
        return json.toString();
    }

    @CrossOrigin()
    @GetMapping("/events")
    public List<Event> getEvents(){
        return eventRepository.getAll();
    }

    @CrossOrigin()
    @GetMapping("/events/{id}")
    public List<Event> getEventsByWidget(@PathVariable("id") int widgetId){
        return eventRepository.getAllByWidgetId(widgetId);
    }

    @CrossOrigin()
    @PostMapping("/events/cancel")
    public Event cancelEvent(@RequestBody String body) throws DatabaseException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        JSONObject jsonbody = new JSONObject(body);
        long eventId = jsonbody.getLong("event_id");
        Event event = eventRepository.get(eventId);
        eventController.cancelEvent(eventId);
        eventRepository.deleteId(eventId);
        return event;
    }
}
