package hhh.acs.database;


import hhh.acs.model.Door;
import hhh.acs.model.Widget;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WidgetTests {

    @Autowired
    WidgetService widgetService;
    @Autowired
    DoorService doorService;
    @Autowired
    EventService eventService;


    @Before
    public void setup(){
        clearDatabase();
        insertDoors();
        insertWidget();
    }

    @After
    public void cleanUp(){
        clearDatabase();
    }

    private void insertDoors() {
        Door door1 = new Door();
        door1.setName("test door 1");
        door1.setId(1);
        doorService.insert(door1);
    }

    private void insertWidget() {
        Widget widget = new Widget();
        widget.setName("test widget test=(testAddWidget)");
        widget.setColor("#123456");
        widget.setIcon("parking");
        widget.setCounter(0);
        widget.setDuration(500L);
        var doors = doorService.getAll();
        List<Door> widgetDoors = new ArrayList<>();
        if (doors.size()>0){
            widgetDoors.add(doors.get(0));
        }
        Door door = new Door();
        door.setName("test door");
        door.setId(2);
        widgetDoors.add(door);
        widget.setDoors(widgetDoors);
        widgetService.create(widget);
    }

    @Test
    public void testAddWidget(){
        var widgets = widgetService.getAll();
        assertFalse(widgets.isEmpty());
        var widget = widgets.get(0);
        // checking
        var db_widget = widgetService.getAll().get(0);
        assertEquals(widget.getName(),db_widget.getName());
        assertEquals(widget.getColor(),db_widget.getColor());
        assertEquals(widget.getCounter(),db_widget.getCounter());
        for (int i=0;i<widget.getDoors().size();i++){
            assertEquals(widget.getDoors().get(i),db_widget.getDoors().get(i));
        }
        assertEquals(widget.getDuration(),db_widget.getDuration());
        assertEquals(widget.getIcon(),db_widget.getIcon());
    }

    @Test
    public void updateWidget() throws DatabaseException {
        var widgets = widgetService.getAll();
        assertFalse(widgets.isEmpty());
        var widget = widgets.get(0);
        widget.setName("updated name");
        widget.setDoors(doorService.getAll());
        widget.setDuration(550L);
        widget.setCounter(5);
        widget.setIcon("elevator");
        widget.setColor("#111111");
        widgetService.updateWidget(widget.getId(),widget);
        // widgetService.updateWidget(widget.getId(),widget);
        // checking
        var updatedWidget = widgetService.get(widget.getId());
        assertEquals(updatedWidget.getName(),widget.getName());
        assertEquals(updatedWidget.getColor(),widget.getColor());
        assertEquals(updatedWidget.getIcon(),widget.getIcon());
        assertEquals(updatedWidget.getDuration(),widget.getDuration());
        for (int i = 0;i < widget.getDoors().size();i++){
            assertEquals(updatedWidget.getDoors().get(i),widget.getDoors().get(i));
        }
        assertEquals(updatedWidget.getCounter(),widget.getCounter());
    }

    @Test(expected = DatabaseException.class)
    public void testGetNonExistentWidget() throws DatabaseException {
        clearDatabase();
        var widget = widgetService.get(500);
    }

    @Test
    public void testGetAllWidgets() {
        List<Widget> widgets = widgetService.getAll();
        assertNotNull(widgets);
        assertFalse(widgets.isEmpty());
        assertEquals(1, widgets.size());
    }

    @Test
    public void deleteWidget() throws DatabaseException {
        var widgets = widgetService.getAll();
        assertFalse(widgets.isEmpty());
        var widget = widgets.get(0);
        widgetService.delete(widget.getId());
        assertTrue(widgetService.getAll().isEmpty());
    }

    private void clearDatabase() {
        eventService.clear();
        widgetService.clear();
        doorService.clear();
    }
}
