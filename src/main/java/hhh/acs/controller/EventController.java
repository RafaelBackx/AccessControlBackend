package hhh.acs.controller;

import hhh.acs.database.DatabaseException;
import hhh.acs.database.EventRepository;
import hhh.acs.model.BiostarAPIRequests;
import hhh.acs.model.Door;
import hhh.acs.model.Event;
import hhh.acs.model.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class EventController {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private BiostarAPIRequests biostarAPIRequests;
    @Autowired
    private EventRepository eventRepository;
    private Map<Long,ScheduledFuture<?>> scheduledEvents;

    public EventController() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        scheduledEvents = new HashMap<>();
        biostarAPIRequests = new BiostarAPIRequests("https://localhost");
        biostarAPIRequests.logIn("admin","t");
    }

    public void cancelEvent(long id) throws DatabaseException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        System.out.println(id);
        var scheduledEvent = this.scheduledEvents.get(id);
        Event event = this.eventRepository.findById(id).orElseThrow(()->new DatabaseException("No such event" + id));
        List<Door> doors = event.getDoors();
        int idArray[] = new int [doors.size()];
        for(int i =0; i<doors.size(); i++){
           idArray[i] = doors.get(i).getId();
        }
        biostarAPIRequests.lockUnlockReleaseDoor(idArray, Mode.RELEASE);
        scheduledEvent.cancel(true);
    }

    public void addEvent(Event event){
        final Runnable eventExecutor = new Runnable() {
            @Override
            public void run() {
                var ids = transformDoorsToIds(event);
                try {
                    System.out.println("released");
                    biostarAPIRequests.lockUnlockReleaseDoor(ids, Mode.RELEASE);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
                System.out.println("deleting event");
                eventRepository.delete(event);
                System.out.println("event deleted");
            }
        };
        final ScheduledFuture<?> eventHandle = scheduler.schedule(eventExecutor,event.getDuration(), TimeUnit.SECONDS);
        scheduledEvents.put(event.getId(),eventHandle);
    }

    public int[] transformDoorsToIds(Event event){
        int[] ids = new int[event.getDoors().size()];
        for (int i = 0;i<ids.length;i++){
            ids[i] = event.getDoors().get(i).getId();
        }
        return ids;
    }
}
