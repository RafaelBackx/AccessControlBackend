package hhh.acs.database;

import hhh.acs.model.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WidgetRepository extends JpaRepository<Widget,Integer> {
    List<Widget> findAll();
}
