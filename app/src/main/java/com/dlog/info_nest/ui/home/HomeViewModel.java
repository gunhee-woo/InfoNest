package com.dlog.info_nest.ui.home;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Transformation;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.dlog.info_nest.BasicApp;
import com.dlog.info_nest.DataRepository;
import com.dlog.info_nest.db.AppDatabase;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.model.Bookmark;
import com.dlog.info_nest.utilities.UrlCrawling;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.dlog.info_nest.utilities.AppExecutorsHelperKt.runOnDiskIO;
import static com.dlog.info_nest.utilities.AppExecutorsHelperKt.runOnMain;

public class HomeViewModel extends AndroidViewModel {
    private static final String QUERY_KEY = "QUERY";

    private final SavedStateHandle mSavedStateHandle;
    private final DataRepository mDataRepository;
    private final LiveData<List<BookmarkEntity>> mBookmarks;
    private final MutableLiveData<List<BookmarkEntity>> mBookmarkDatas;

    public HomeViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        mSavedStateHandle = savedStateHandle;
        mDataRepository = ((BasicApp) application).getDataRepository();
        mBookmarks = mDataRepository.getBookmarks();
        mBookmarkDatas = mDataRepository.getBookmarkData();

        // Transformations.switchMap
        // LiveData가 변경에 따라 이벤트를 발생 시키면 function을 적용하여 결과를 새로만든 새로운 LiveData에 set한다.
        // 또한 이때 새로운 LiveData에 등록된 observer들에게 재전송 된다.
        /*mBookmarks = Transformations.switchMap(savedStateHandle.getLiveData("QUERY", null),
                (Function<CharSequence, LiveData<List<BookmarkEntity>>>) query -> {
                    if (TextUtils.isEmpty(query)) {
                        return mDataRepository.getBookmarks();
                    }
                    return mDataRepository.searchProducts("*" + query + "*");
            });*/
    }

    public LiveData<List<BookmarkEntity>> getmBookmarks() {
        return mBookmarks;
    }

    public List<BookmarkEntity> getmBookmarkDatas() {
        return mBookmarkDatas.getValue();
    }

    public void insert(BookmarkEntity bookmarkEntity) {
        mDataRepository.insert(bookmarkEntity);
    }

    public void setQuery(CharSequence query) {
        // Save the user's query into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandle.set(QUERY_KEY, query);
    }


}
