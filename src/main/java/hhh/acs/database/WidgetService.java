package hhh.acs.database;

import hhh.acs.model.Door;
import hhh.acs.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public Widget create(Widget widget){
        List<Door> persistedDoors = new ArrayList<>();
        for (Door door : widget.getDoors()){
            try {
                Door d = doorService.get(door.getId());
                persistedDoors.add(d);
            }catch (DatabaseException e){
                persistedDoors.add(doorService.insert(door));
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
        Widget oldWidget = get(id);
        oldWidget.setName(widget.getName());
        oldWidget.setColor(widget.getColor());
        oldWidget.setIcon(widget.getIcon());
        oldWidget.setCounter(widget.getCounter());
        oldWidget.setDuration(widget.getDuration());
        oldWidget.setDoors(widget.getDoors());
        create(oldWidget);
        return widget.toString();
    }

    public void clear() {
        this.widgetRepository.deleteAll();
    }
    public List<Widget> topUsedWidgets(int amount) {
        return widgetRepository.getTopUsedWidgets(amount);
    }
}
