package com.dlog.info_nest.db.entity;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.dlog.info_nest.db.Converters;
import com.dlog.info_nest.model.Bookmark;
import com.dlog.info_nest.utilities.UrlCrawling;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "bookmarks")
public class BookmarkEntity implements Bookmark, Serializable {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "url")
    public String mUrl;
    @ColumnInfo(name = "title")
    public String mTitle;
    @ColumnInfo(name = "tag")
    public String mTags;
    @ColumnInfo(name = "date")
    public String mDate;
    @ColumnInfo(name = "color")
    public int mColor;
    @TypeConverters(Converters.class)
    @ColumnInfo(name = "nouns")
    public List<String> mNouns;
    @ColumnInfo(name = "image")
    public byte[] mImage;
    @Ignore
    public boolean mIsLocked;
    @Ignore
    public boolean mIsStared;

    @NotNull
    @Override
    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    @Override
    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    @Override
    public String getmTags() {
        return mTags;
    }

    public void setmTags(String mTags) {
        this.mTags = mTags;
    }

    @Override
    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    @Override
    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor){
        this.mColor = mColor;
    }

    @Override
    public List<String> getmNouns() {
        return mNouns;
    }

    public void setmNouns(List<String> mNouns) {
        this.mNouns = mNouns;
    }

    @Override
    public byte[] getmImage() {
        return mImage;
    }


    public void setmImage(byte[] mImage) {
        this.mImage = mImage;
    }

    @Override
    public boolean getmIsLocked() {
        return mIsLocked;
    }

    @Override
    public boolean getmIsStared() {
        return mIsStared;
    }

    public void setmIsLocked(boolean mIsLocked) {
        this.mIsLocked = mIsLocked;
    }

    public void setmIsStared(boolean mIsStared) {
        this.mIsStared = mIsStared;
    }

    public BookmarkEntity() {}

    public BookmarkEntity(String mTitle, String mUrl, String mTags, String mDate, int mColor, List<String> mNouns, byte[] mImage) {
        this.mTitle = mTitle;
        this.mUrl = mUrl;
        this.mTags = mTags;
        this.mDate = mDate;
        this.mColor = mColor;
        this.mNouns = mNouns;
        this.mImage = mImage;
    }

    public BookmarkEntity(String mTitle, String mUrl, String mTags, String mDate, int mColor, List<String> mNouns, byte[] mImage, boolean mIsStared, boolean mIsLocked) {
        this.mTitle = mTitle;
        this.mUrl = mUrl;
        this.mTags = mTags;
        this.mDate = mDate;
        this.mColor = mColor;
        this.mNouns = mNouns;
        this.mImage = mImage;
        this.mIsLocked = mIsLocked;
        this.mIsStared = mIsStared;
    }

    public BookmarkEntity(Bookmark bookmark) {
        this.mTitle = bookmark.getmTitle();
        this.mUrl = bookmark.getmUrl();
        this.mTags = bookmark.getmTags();
        this.mDate = bookmark.getmDate();
        this.mColor = bookmark.getmColor();
    }

}
