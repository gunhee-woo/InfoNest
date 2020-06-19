package com.dlog.info_nest.db;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dlog.info_nest.utilities.AppExecutors;
import com.dlog.info_nest.db.dao.BookmarkDao;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.utilities.UrlCrawling;

import java.util.List;

import static com.dlog.info_nest.utilities.AppExecutorsHelperKt.runOnDiskIO;
import static com.dlog.info_nest.utilities.CurrentDateKt.currentDate;

@Database(entities = {BookmarkEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase Instance;

    @VisibleForTesting
    public static final String DATABASE_NAME = "bookmark.db";

    public abstract BookmarkDao bookmarkDao();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context) {
        if(Instance == null) {
            synchronized (AppDatabase.class) {
                if(Instance == null) {
                    Instance = buildDatabase(context.getApplicationContext());
                    Instance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return Instance;
    }

    // DB 미리 채우기
    private static AppDatabase buildDatabase(final Context context) {
        final AppDatabase build = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    // 데이터베이스가 create 한 후 하는일
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        runOnDiskIO(() -> {
                            // Add a delay to simulate a long-running operation
                            addDelay();
                            // Generate the data for pre-population
                            ObservableArrayList<BookmarkEntity> bookmarkEntities = new ObservableArrayList<>();

                            String url = "http://www.spotvnews.co.kr/?mod=news&act=articleView&idxno=356439";
                            UrlCrawling urlCrawling = new UrlCrawling(url);
                            BookmarkEntity bookmarkEntity = new BookmarkEntity();
                            bookmarkEntity.setmUrl(url);
                            bookmarkEntity.setmTitle("네빌의 테베즈 비난 \"맨유 입단 2번째 시즌 만에 태업했다\"");
                            bookmarkEntity.setmDate(currentDate());
                            List<String> nouns = urlCrawling.getUrlToTop10NounsArray();
                            String tag = "";
                            for(String str : nouns)
                                tag += str + " ";
                            bookmarkEntity.setmImage(null);
                            bookmarkEntity.setmTags(tag);
                            bookmarkEntity.setmColor(1);
                            bookmarkEntity.setmNouns(nouns);
                            bookmarkEntity.setmIsLocked(false);
                            bookmarkEntity.setmIsStared(false);
                            bookmarkEntities.add(bookmarkEntity);

                            url = "https://news.naver.com/main/read.nhn?mode=LSD&mid=shm&sid1=102&oid=052&aid=0001440320";
                            urlCrawling = new UrlCrawling(url);
                            bookmarkEntity = new BookmarkEntity();
                            bookmarkEntity.setmUrl(url);
                            bookmarkEntity.setmTitle("[단독] 서울구치소 교도관 코로나19 확진 판정...\"270여 명 접촉\"");
                            bookmarkEntity.setmDate(currentDate());
                            nouns = urlCrawling.getUrlToTop10NounsArray();
                            tag = "";
                            for(String str : nouns)
                                tag += str + " ";
                            bookmarkEntity.setmTags(tag);
                            bookmarkEntity.setmColor(2);
                            bookmarkEntity.setmNouns(nouns);
                            bookmarkEntity.setmImage(null);
                            bookmarkEntity.setmIsLocked(false);
                            bookmarkEntity.setmIsStared(false);
                            bookmarkEntities.add(bookmarkEntity);
                            insertData(Instance, bookmarkEntities);
                            Instance.setDatabaseCreated();
                        });
                    }
                }).fallbackToDestructiveMigration().build();
        return build;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL("CREATE TABLE Bookmark_new (url Text, title Text, tag Text, date Text, color int, nouns Text, image binary, PRIMARY KEY (url))");
            // Remove the old table
            database.execSQL("DROP TABLE bookmarks");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE Bookmark_new RENAME TO bookmarks");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL(
                    "CREATE TABLE Bookmark_new (mUrl Text, mTitle Text, mTags Text, mDate Text, mColor Integer, mNouns Text, mImage binary, PRIMARY KEY (mUrl))");
            // Remove the old table
            database.execSQL("DROP TABLE Bookmark");
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE Bookmark_new RENAME TO Bookmark");
        }
    };

    /*private static AppDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                //.createFromAsset("") // Asset에 db파일 넣고 사용
                //.fallbackToDestructiveMigration()
                .build();
    }*/


    // 데이터베이스가 이미 존재하는지 확인
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    private static void insertData(final AppDatabase database, final ObservableArrayList<BookmarkEntity> bookmarks) {
        database.runInTransaction(() -> {
            database.bookmarkDao().insertAll(bookmarks);
        });
    }

    private static void addDelay() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ignored) {
            ignored.printStackTrace();
        }
    }
}
