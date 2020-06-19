package com.dlog.info_nest;

import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.dlog.info_nest.db.AppDatabase;
import com.dlog.info_nest.db.entity.BookmarkEntity;

import java.util.List;

import static com.dlog.info_nest.utilities.AppExecutorsHelperKt.runOnDiskIO;

public class DataRepository {

    private static DataRepository mInstance;
    private final AppDatabase mDatabase;
    private MediatorLiveData<List<BookmarkEntity>> mObservableBookmarks;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
        mObservableBookmarks = new MediatorLiveData<>();
        mObservableBookmarks.addSource(mDatabase.bookmarkDao().loadAllLiveBookmarks(),
                bookmarkEntity -> {
                    if(mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableBookmarks.postValue(bookmarkEntity);
                    }
                });
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if(mInstance == null) {
            synchronized (DataRepository.class) {
                if(mInstance == null) {
                    mInstance = new DataRepository(database);
                }
            }
        }
        return mInstance;
    }

    public LiveData<List<BookmarkEntity>> getBookmarks() {
        return mObservableBookmarks;
    }

    public MutableLiveData<List<BookmarkEntity>> getBookmarkData() {
        return mObservableBookmarks;
    }

    public LiveData<BookmarkEntity> getBookmarkByUrl(final String url) {
        return mDatabase.bookmarkDao().loadBookmarkByUrl(url);
    }

    public void insert(BookmarkEntity bookmarkEntity) {
        runOnDiskIO(() -> {
            mDatabase.bookmarkDao().insert(bookmarkEntity);
        });
    }

    public void insertBookmarks(ObservableArrayList<BookmarkEntity> bookmarkEntities) {
        runOnDiskIO(() -> {
            mDatabase.bookmarkDao().insertAll(bookmarkEntities);
        });
    }

    public boolean checkDBEmpty() {
        return mDatabase.bookmarkDao().countBookmarksRow() == 0;
    }
}
