package hhh.acs.database;

import hhh.acs.model.Door;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoorRepository extends JpaRepository<Door,Integer> {
    List<Door> findAll();
    List<Door> findAllByWidgetId(int id);
}
