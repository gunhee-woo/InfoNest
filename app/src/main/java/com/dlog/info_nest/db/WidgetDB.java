package com.dlog.info_nest.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dlog.info_nest.db.dao.WidgetDao;
import com.dlog.info_nest.db.entity.WidgetItem;

@Database(entities = {WidgetItem.class}, version = 1)
public abstract class WidgetDB extends RoomDatabase {
    public abstract WidgetDao widgetDao();
}
