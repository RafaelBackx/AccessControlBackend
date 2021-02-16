package hhh.acs.database;

import hhh.acs.model.Door;
import org.junit.Assert;
import org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DoorDbTests {
    @Autowired
    DoorService doorService;
    Door standaard = new Door();

    @Before
    public void setUp() {
        clearDatabase();
        standaard.setId(1);
        standaard.setName("Standaard");
        doorService.insert(standaard);
    }

    @Test
    public void testAddDoor() {
        Door door = new Door();
        door.setId(2);
        door.setName("Test");
        doorService.insert(door);
        List<Door> doors = doorService.getAll();
        assertFalse(doors.isEmpty());
        assertEquals(2, doors.size());
        assertEquals(door, doors.get(1));
    }

    @Test
    public void testGetDoor() throws DatabaseException {
        Door door = doorService.get(1);
        assertNotNull(door);
        assertEquals(standaard, door);
    }

    @Test (expected = DatabaseException.class)
    public void testGetNonExistentDoor() throws DatabaseException{
        Door door = doorService.get(50);
    }

    @Test
    public void testGetAllDoors() {
        List<Door> doors = doorService.getAll();
        assertNotNull(doors);
        assertFalse(doors.isEmpty());
        assertEquals(1, doors.size());
    }

    @Test
    public void testDeleteDoor() {
        Door door = doorService.delete(standaard);
        List<Door> doors = doorService.getAll();
        assertTrue(doors.isEmpty());
        assertEquals(0, doors.size());
    }

    @Test
    public void testClearDb() {
        doorService.clear();
        List<Door> doors = doorService.getAll();
        assertTrue(doors.isEmpty());
        assertEquals(0, doors.size());
    }

    private void clearDatabase() {
        doorService.clear();
    }
}
