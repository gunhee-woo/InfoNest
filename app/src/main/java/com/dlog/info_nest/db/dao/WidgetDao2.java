package com.dlog.info_nest.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dlog.info_nest.db.entity.WidgetItem2;

import java.util.List;

@Dao
public interface WidgetDao2 {
    @Query("SELECT * FROM widget_list")
    List<WidgetItem2> getAllWidget();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWidgetItem(WidgetItem2... widgetItems);

    @Delete
    void delete(WidgetItem2 widgetItem);

    @Query("DELETE FROM widget_list")
    void deleteAll();

    @Query("select * from widget_list order by id desc limit 9")
    List<WidgetItem2> getLatelyWidget();

    @Query("DELETE FROM widget_list WHERE url = :url")
    void deleteByUrl(String url);
}
