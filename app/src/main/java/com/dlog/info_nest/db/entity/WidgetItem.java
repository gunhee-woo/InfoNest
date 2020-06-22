package com.dlog.info_nest.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "widget")
public class WidgetItem implements Serializable {
    @NonNull
    @PrimaryKey
    private String url;
    private int figure_layout_id;
    private String color;
    private String title;
    //갤러리 이미지
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image = null;

    private int position = -1;

    private float x;
    private float y;
    public WidgetItem(int figure_layout_id, String color, String title, String url, int position, float x, float y){
        this.figure_layout_id = figure_layout_id;
        this.color = color;
        this.title = title;
        this.url = url;
        this.position = position;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public void setFigure_layout_id(int figure_layout_id) {
        this.figure_layout_id = figure_layout_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public String getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }

    public int getFigure_layout_id() {
        return figure_layout_id;
    }

    public String getUrl() {
        return url;
    }
}
