package com.dlog.info_nest.db.dao;

import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.model.Bookmark;

import java.util.List;

@Dao
public interface BookmarkDao {
    @Query("select * from bookmarks")
    List<BookmarkEntity> loadAllBookmarks();

    @Query("select * from bookmarks")
    LiveData<List<BookmarkEntity>> loadAllLiveBookmarks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ObservableArrayList<BookmarkEntity> bookmarkEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BookmarkEntity bookmarkEntity);

    @Query("select * from bookmarks where url = :url")
    LiveData<BookmarkEntity> loadBookmarkByUrl(String url);

    @Query("select count(*) from bookmarks")
    int countBookmarksRow();

    @Update
    void update(BookmarkEntity bookmarkEntity);

    @Delete
    void delete(BookmarkEntity bookmarkEntity);

}
