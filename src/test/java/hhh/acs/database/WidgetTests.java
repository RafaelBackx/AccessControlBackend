package hhh.acs.database;


import hhh.acs.model.Event;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;

@SpringBootTest
public class WidgetTests {

    WidgetService widgetRepository = new WidgetService();
    DoorService doorRepository = new DoorService();
    EventService eventRepository = new EventService();

    @Before
    public void setup(){
        clearDatabase();
    }

    @Test
    public void testAddWidget(){
        
    }

    @Test
    public void test(){
        assertEquals(1,1);
    }

    private void clearDatabase() {
        eventRepository.clear();
        widgetRepository.clear();
        doorRepository.clear();
    }

}
