package hhh.acs.controller;

import hhh.acs.configuration.BackendProperties;
import hhh.acs.database.DatabaseException;
import hhh.acs.database.EventRepository;
import hhh.acs.model.BiostarAPIRequests;
import hhh.acs.model.Door;
import hhh.acs.model.Event;
import hhh.acs.model.Mode;
import org.hibernate.annotations.common.reflection.XProperty;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class EventController implements InitializingBean {
    @Autowired
    private BackendProperties backend;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private BiostarAPIRequests biostarAPIRequests;
    @Autowired
    private EventRepository eventRepository;
    private Map<Long,ScheduledFuture<?>> scheduledEvents;

    public EventController() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        scheduledEvents = new HashMap<>();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        biostarAPIRequests = new BiostarAPIRequests(backend.getUrl() + ":" + backend.getPort(), backend);
        biostarAPIRequests.logIn(backend.getUsername(),backend.getPassword());
    }

    /**
     *
     * @param id de id van het event dat gecanceled moet worden
     * @throws DatabaseException wanneer het event niet in de database gevonden kan worden
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     *
     * Deze functie annuleerd een event waarbij de deur terug gereleased wordt en het event uit de database verwijderd wordt
     */

    public void cancelEvent(long id) throws DatabaseException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
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

    /**
     *
     * @param event het event dat ingepland moet worden
     *
     * Deze functie neemt een event als parameter en wordt ingepland om over een bepaalde tijd terug gereleased te worden
     * deze planner zit in memory en wordt dus niet gepersist naar een database, nadat de tijd verstreken is wordt het event ook verwijderd
     */
    public void addEvent(Event event){
        final Runnable eventExecutor = new Runnable() {
            @Override
            public void run() {
                var ids = transformDoorsToIds(event);
                try {
                    biostarAPIRequests.lockUnlockReleaseDoor(ids, Mode.RELEASE);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
                eventRepository.delete(event);
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
