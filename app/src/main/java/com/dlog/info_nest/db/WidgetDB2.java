package com.dlog.info_nest.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dlog.info_nest.db.dao.WidgetDao2;
import com.dlog.info_nest.db.entity.WidgetItem2;

@Database(entities = {WidgetItem2.class}, version = 1)
public abstract class WidgetDB2 extends RoomDatabase {
    public abstract WidgetDao2 widgetDao2();
}
