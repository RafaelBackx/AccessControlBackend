package hhh.acs.database;

import hhh.acs.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAll();
    List<Event> findAllByWidgetId(int id);
}
