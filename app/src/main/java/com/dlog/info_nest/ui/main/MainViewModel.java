package com.dlog.info_nest.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dlog.info_nest.BasicApp;
import com.dlog.info_nest.DataRepository;
import com.dlog.info_nest.db.entity.BookmarkEntity;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final DataRepository mDataRepository;
    private final LiveData<List<BookmarkEntity>> mBookmarks;
    private final MutableLiveData<List<BookmarkEntity>> mBookmarkData;
    private List<BookmarkEntity> lockedBookmarkData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.mDataRepository = ((BasicApp) application).getDataRepository();
        mBookmarks = mDataRepository.getBookmarks();
        mBookmarkData = mDataRepository.getBookmarkData();
    }

    public LiveData<List<BookmarkEntity>> getmBookmarks() {
        return mBookmarks;
    }

    public List<BookmarkEntity> getmBookmarkData() {
        return mBookmarkData.getValue();
    }

    public void insert(BookmarkEntity bookmarkEntity) {
        mDataRepository.insert(bookmarkEntity);
    }

    public void setmBookmarks(List<BookmarkEntity> bookmarks) {
        mBookmarkData.postValue(bookmarks);
    }

    public void setLockedBookmarkData(List<BookmarkEntity> bookmarkData) {
        lockedBookmarkData = bookmarkData;
    }

    public List<BookmarkEntity> getLockedBookmarkData() {
        return lockedBookmarkData;
    }

}
