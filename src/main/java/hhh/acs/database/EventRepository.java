package hhh.acs.database;

import hhh.acs.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAll();
    List<Event> findAllByWidgetId(int id);
}
