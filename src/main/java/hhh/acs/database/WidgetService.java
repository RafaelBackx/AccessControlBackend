package hhh.acs.database;

import hhh.acs.model.Door;
import hhh.acs.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

@Service
public class WidgetService {

    @Autowired
    private WidgetRepository widgetRepository;

    @Autowired
    private DoorService doorService;

    public List<Widget> getAll() {
        return widgetRepository.findAll();
    }

    public Widget get(int id) throws DatabaseException{
        return widgetRepository.findById(id).orElseThrow(() -> new DatabaseException("Widget with " + id + " not found in the database"));
    }

    public Widget create(Widget widget) throws DatabaseException{
        List<Door> persistedDoors = new ArrayList<>();
        for (Door door : widget.getDoors()){
            Door d = doorService.get(widget.getId());
            if (d == null){
                persistedDoors.add(doorService.insert(door));
            }
            else{
                persistedDoors.add(d);
            }
        }
        widget.setDoors(persistedDoors);
        return widgetRepository.save(widget);
    }

    public Widget delete (int id) throws DatabaseException {
        Widget widget = get(id);
        widgetRepository.delete(widget);
        return widget;
    }

    public void updateCounter(int new_counter, int id) {
        widgetRepository.updateCounter(new_counter, id);
    }

    public String updateWidget(int id, Widget widget) throws DatabaseException {
        List<Door> newDoors = widget.getDoors();
        Widget oldWidget = get(id);
        List<Door> oldDoors = oldWidget.getDoors();
        if (newDoors != null){
            for (Door door : oldDoors){
                if (!newDoors.contains(door)){
                    doorService.delete(door);
                }
            }
        }
        create(widget);
        return widget.toString();
    }
}
