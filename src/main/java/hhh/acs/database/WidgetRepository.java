package hhh.acs.database;

import hhh.acs.model.Door;
import hhh.acs.model.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Repository
public interface WidgetRepository extends JpaRepository<Widget,Integer> {
    List<Widget> findAll();

    @Modifying
    @Transactional
    @Query("update Widget w set w.counter = ?1 where w.id = ?2")
    void updateCounter(int new_counter, int id);

    @Query(nativeQuery = true, value = "select * from Widget w order by counter desc limit ?1")
    List<Widget> getTopUsedWidgets(int amount);
}
