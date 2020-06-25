package com.dlog.info_nest.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dlog.info_nest.db.entity.WidgetItem;

import java.util.List;

@Dao
public interface WidgetDao {
    @Query("SELECT * FROM widget")
    List<WidgetItem> getAllWidget();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWidgetItem(WidgetItem... widgetItems);

    @Delete
    void delete(WidgetItem widgetItem);

    @Query("DELETE FROM widget")
    void deleteAll();

    @Query("DELETE FROM widget WHERE url = :url")
    void deleteByUrl(String url);
}
