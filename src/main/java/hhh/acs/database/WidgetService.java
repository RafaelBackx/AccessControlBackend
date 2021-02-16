package hhh.acs.database;

import hhh.acs.model.Widget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class WidgetService {

    @Autowired
    private WidgetRepository widgetRepository;

    List<Widget> getAll() {
        return widgetRepository.findAll();
    }

    void updateCounter(int new_counter, int id) {
        widgetRepository.updateCounter(new_counter, id);
    }
}
