package hhh.acs.database;

import hhh.acs.model.Door;
import hhh.acs.model.Event;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EventsTests {

    @Autowired
    private EventService eventService;
    @Autowired
    private DoorService doorService;
    @Autowired
    private WidgetService widgetService;


    @Before
    public void setup() throws DatabaseException {
        clearDatabase();
        insertEvent();
    }

    @Test
    public void insertEvent() throws DatabaseException {
        Door door = new Door();
        door.setId(1);
        door.setName("event_door_test");
        doorService.insert(door);
        var doors = doorService.getAll();
        List<Door> eventDoors = new ArrayList<>();
        eventDoors.add(doors.get(0));
        Event event = new Event();
        event.setStartTime(50000L);
        event.setDuration(4500L);
        event.setState(true);
        event.setWidget(null); // this is a door event
        event.setDoors(eventDoors);
        eventService.insert(event);
        // checking
        var events = eventService.getAll();
        var persisted_event = eventService.get(events.get(0).getId());
        assertEquals(persisted_event.getStartTime(),event.getStartTime());
        assertEquals(persisted_event.getDuration(),event.getDuration());
        List<Door> persisted_doors = persisted_event.getDoors();
        for (int i=0;i<persisted_doors.size();i++){
            assertEquals(persisted_event.getDoors().get(i).getName(),persisted_doors.get(i).getName());
            assertEquals(persisted_event.getDoors().get(i).getId(),persisted_doors.get(i).getId());
        }
    }

    private void clearDatabase() {
        this.eventService.clear();
        this.widgetService.clear();
        this.doorService.clear();
    }

}
