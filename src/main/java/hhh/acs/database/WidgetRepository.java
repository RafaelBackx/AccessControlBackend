package hhh.acs.database;

import hhh.acs.model.Door;
import hhh.acs.model.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface WidgetRepository extends JpaRepository<Widget,Integer> {
    List<Widget> findAll();

    @Modifying
    @Transactional
    @Query("update Widget w set w.counter = ?1 where w.id = ?2")
    void updateCounter(int new_counter, int id);
//    @Modifying
//    @Transactional()
//    @Query("update Widget w set w.name = ?1, w.color = ?2, w.duration = ?3, w.icon =  ?4, w.doors = ?5 where w.id = ?6")
//    void updateWidget(String name, String color, BigInteger duration, String icon,List<Door> doors, int id);
}
