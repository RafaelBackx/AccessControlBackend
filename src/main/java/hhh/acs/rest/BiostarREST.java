package hhh.acs.rest;

import hhh.acs.controller.EventController;
import hhh.acs.database.DoorRepository;
import hhh.acs.database.EventRepository;
import hhh.acs.database.WidgetRepository;
import hhh.acs.model.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.*;

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
public class BiostarREST {

    private BiostarAPIRequests biostarAPIRequests = new BiostarAPIRequests("https://localhost");
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private WidgetRepository widgetrepository;
    @Autowired
    private DoorRepository doorRepository;
    @Autowired
    private EventController eventController;

    public BiostarREST() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        biostarAPIRequests.logIn("admin","t");
    }

    @GetMapping("/login")
    public void login() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String result = biostarAPIRequests.logIn("admin","t");
        System.out.println(result);
    }

    @GetMapping("/lock")
    public void lock() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        int[] ids = {2};
        biostarAPIRequests.lockUnlockReleaseDoor(ids, Mode.LOCK);
    }

    @GetMapping("/unlock")
    public void unlock() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        int[] ids = {2};
        biostarAPIRequests.lockUnlockReleaseDoor(ids, Mode.UNLOCK);
    }

    @GetMapping("/release")
    public void release() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        int[] ids = {2};
        biostarAPIRequests.lockUnlockReleaseDoor(ids, Mode.RELEASE);
    }

    @CrossOrigin()
    @PostMapping("/create/event")
    public void addEvent(@RequestBody Event event) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        System.out.println(event);
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        Long unixtimestamp = currentDate.toEpochSecond(currentTime, ZoneOffset.UTC);
        event.setStartTime(unixtimestamp);
        List<Door> persistedDoors = new ArrayList<>();
        for (Door door : event.getDoors()){
            Door d = doorRepository.findById(door.getId()).orElseGet(() -> null);
            if (d == null){
                System.out.println(door);
                persistedDoors.add(doorRepository.save(door));
            }
            else{
                persistedDoors.add(d);
            }
        }
        event.setDoors(persistedDoors);
        // increment widget counter
        var widget = event.getWidget();
        if (widget != null){
            Widget persistedWidget = widgetrepository.findById(widget.getId()).orElse(null);
            if (persistedWidget != null){
                System.out.println("incremented");
                persistedWidget.increment();
                widgetrepository.updateCounter(persistedWidget.getCounter(),persistedWidget.getId());
            }
        }
        eventRepository.save(event);
        var ids = eventController.transformDoorsToIds(event);
        if (event.isState()){
            System.out.println("opened");
            biostarAPIRequests.lockUnlockReleaseDoor(ids,Mode.UNLOCK);
        }
        else{
            System.out.println("closed");
            biostarAPIRequests.lockUnlockReleaseDoor(ids, Mode.LOCK);
        }
        // schedule the event
        eventController.addEvent(event);
    }

    @CrossOrigin()
    @GetMapping("/events")
    public List<Event> getEvents(){
        return eventRepository.findAll();
    }

    @CrossOrigin()
    @GetMapping("/events/{id}")
    public List<Event> getEventsByWidget(@PathVariable("id") int widgetId){
        return eventRepository.findAllByWidgetId(widgetId);
    }

    @CrossOrigin()
    @PostMapping("/events/cancel")
    public Event cancelEvent(@RequestBody String body){
        JSONObject jsonbody = new JSONObject(body);
        long eventId = jsonbody.getLong("event_id");
        eventController.cancelEvent(eventId);
        var event = eventRepository.findById(eventId).orElse(null);
        eventRepository.deleteById(eventId);
        return event;
    }


}
