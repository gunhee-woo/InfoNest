package com.dlog.info_nest.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "widget_list")
public class WidgetItem2 implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String url;
    private String title;


    private float x;
    private float y;
    public WidgetItem2( String title, String url, float x, float y){
        this.title = title;
        this.url = url;
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }


    public String getUrl() {
        return url;
    }
}

