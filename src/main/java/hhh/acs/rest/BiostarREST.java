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

import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        biostarAPIRequests = new BiostarAPIRequests(backend.getUrl() + ":" + backend.getPort(), backend);
        biostarAPIRequests.logIn(backend.getUsername(),backend.getPassword());
    }

    @CrossOrigin()
    @PostMapping("/login")
    public String login(@RequestBody String body) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        JSONObject jsonObject = new JSONObject(body);
        JSONObject user = jsonObject.getJSONObject("User");
        String username = user.getString("login_id");
        String password = user.getString("password");
        try{
            var result =  biostarAPIRequests.logIn(username, password);
            return result;
        }catch (HttpClientErrorException e){
            return null;
        }

    }

    /**
     *
     * @param event het event dat toegevoegd moet worden
     * @return een json die aangeeft of het event goed is aangemaakt deze json bevat de id van het event,
     * een string die zegt of het event goed is aangemaakt of niet en een boolean die aangeeft of het event goed is aangemaakt
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     *
     * Deze functie voegt een event toe aan de database en incrementeert een eventueel gekopelde widget om zo bij te houden hoe vaak een widget geactiveerd is
     * het event wordt ook direct geactiveerd
     */


    @CrossOrigin()
    @PostMapping("/create/event")
    public String addEvent(@RequestBody Event event) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, DatabaseException {

        LocalDateTime currentDateTime = LocalDateTime.now();
        Long unixtimestamp1 = currentDateTime.toEpochSecond(ZoneOffset.UTC);

        /*LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        Long unixtimestamp2 = currentDate.toEpochSecond(currentTime, ZoneOffset.UTC);
        System.out.println(unixtimestamp2);*/

        event.setStartTime(unixtimestamp1);



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
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // cancel event wanneer event niet kan starten
            eventRepository.deleteId(persistedEvent.getId());
            json.put("id", -1);
            json.put("message", "kon niet geopend worden!");
            json.put("success",false);
            return json.toString();
        }
        // schedule the event
        eventController.addEvent(event);
        json.put("id", persistedEvent.getId());
        json.put("message", "succesvol geopend!");
        json.put("success",true);
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

    @RequestMapping(value = "/forward",method = RequestMethod.OPTIONS)
    public void forwardOptions(HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Access-Control-Allow-Methods", "*");
    }

    @CrossOrigin()
    @PostMapping("/forward")
    public String forwardRequest(@RequestBody String body, HttpServletResponse response) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        JSONObject jsonbody = new JSONObject(body);
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Access-Control-Allow-Methods", "*");
        return biostarAPIRequests.forwardRequest(jsonbody);
    }
}
