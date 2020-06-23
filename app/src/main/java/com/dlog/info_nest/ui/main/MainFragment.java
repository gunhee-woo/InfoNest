package com.dlog.info_nest.ui.main;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.dlog.info_nest.BasicApp;
import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.MainFragmentBinding;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.ui.PopupActivity;
import com.dlog.info_nest.ui.SettingActivity;
import com.dlog.info_nest.ui.WebViewActivity;
import com.dlog.info_nest.utilities.Code;
import com.dlog.info_nest.utilities.HtmlWriter;
import com.dlog.info_nest.utilities.ItemTouchHelperCallback;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.dlog.info_nest.utilities.CurrentDateKt.currentDateLong;

public class MainFragment extends Fragment implements TextWatcher {

    public static final String TAG ="MainFragment";
    private static final int PICK_FROM_EXTERNAL_STORAGE = 2;

    private MainAdapter mMainAdapter;
    private MainFragmentBinding mMainFragmentBinding;
    private Boolean isFabOpen = false;
    private Animation mBtnOpen, mBtnClose;
    private ItemTouchHelper helper;
    private Boolean isStarBtnClicked = false;
    private MainViewModel mMainViewModel;
    private Boolean isSearchBtnClicked = false;
    private Boolean isTogglingBtnClicked = false;
    private HashMap<String, byte[]> haveImageBookmarkList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mMainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        mMainAdapter = new MainAdapter(getContext());
        mMainFragmentBinding.rcyBookmarkList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mMainFragmentBinding.rcyBookmarkList.setAdapter(mMainAdapter);
        mMainFragmentBinding.editSearch.addTextChangedListener(this);
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(mMainAdapter, getContext()));
        helper.attachToRecyclerView(mMainFragmentBinding.rcyBookmarkList);
        return mMainFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mBtnOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        mBtnClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        subscribeUi(mMainViewModel.getmBookmarks()); // DB에 저장된 전체 북마크 불러옴
        setBtnListener();
    }

    private void subscribeUi(LiveData<List<BookmarkEntity>> liveData) {
        liveData.observe(getViewLifecycleOwner(), bookmarks -> {
            if (bookmarks != null) {
                ObservableArrayList<BookmarkEntity> bookmarkEntities = new ObservableArrayList<>();
                bookmarkEntities.addAll(bookmarks);
                mMainFragmentBinding.setBookmarkEntities(bookmarkEntities);
                mMainAdapter.setItem(bookmarkEntities);
                mMainFragmentBinding.rcyBookmarkList.setAdapter(mMainAdapter);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            //            // sync.
            mMainFragmentBinding.executePendingBindings();
        });
    }

    public void anim() {
        if (isFabOpen) {
            mMainFragmentBinding.fab1.setImageResource(R.drawable.ic_add_black_24dp);
            mMainFragmentBinding.fab2.startAnimation(mBtnClose);
            mMainFragmentBinding.fab3.startAnimation(mBtnClose);
            mMainFragmentBinding.fab2.setClickable(false);
            mMainFragmentBinding.fab3.setClickable(false);
            isFabOpen = false;
        } else {
            mMainFragmentBinding.fab1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_close_black_36dp));
            mMainFragmentBinding.fab2.startAnimation(mBtnOpen);
            mMainFragmentBinding.fab3.startAnimation(mBtnOpen);
            mMainFragmentBinding.fab2.setClickable(true);
            mMainFragmentBinding.fab3.setClickable(true);
            isFabOpen = true;
        }
    }



    private void setBtnListener() {
        mMainFragmentBinding.mainHideLayout.setOnClickListener(v -> {
            mMainFragmentBinding.mainHideLayout.setVisibility(View.INVISIBLE);
        });

        mMainFragmentBinding.btnNavigationDrawer.setOnClickListener(v -> {
            //Animation ani = AnimationUtils.loadAnimation(getContext(), R.anim.setting_slide);
            //ani.setDuration(500);
            mMainFragmentBinding.mainHideLayout.setVisibility(View.VISIBLE);
            //mMainFragmentBinding.mainDrawerLayout.startAnimation(ani);
            mMainFragmentBinding.mainDrawerLayout.setVisibility(View.VISIBLE);

            ArrayList<DrawerListItem> drawerListItems = new ArrayList<>();
            drawerListItems.add(new DrawerListItem(R.drawable.ic_lock_outline_black_24dp, "비밀 북마크 보여주기"));
            drawerListItems.add(new DrawerListItem(R.drawable.ic_turned_in_not_black_36dp, "오늘의 북마크 보여주기"));
            drawerListItems.add(new DrawerListItem(R.drawable.ic_file_upload_black_24dp, "북마크 가져오기"));
            drawerListItems.add(new DrawerListItem(R.drawable.ic_file_download_black_24dp, "북마크 내보내기"));
            drawerListItems.add(new DrawerListItem(R.drawable.ic_settings_black_24dp, "설정"));
            drawerListItems.add(new DrawerListItem(R.drawable.ic_help_black_24dp, "도움말"));

            MainDrawerAdapter mainDrawerAdapter = new MainDrawerAdapter(drawerListItems);
            mMainFragmentBinding.mainDrawerListView.setAdapter(mainDrawerAdapter);

        });

        mMainFragmentBinding.mainDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    final EditText editText = new EditText(getContext());
                    builder.setView(editText);
                    if(BasicApp.prefs.getPasswordPreferences().equals("")) {
                        builder.setMessage("비밀번호 만들기");
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = editText.getText().toString();
                                BasicApp.prefs.savePreferences(hashingPassword(password));
                                mMainAdapter.setItem(mMainAdapter.getLockedBookmarkList());
                                mMainFragmentBinding.mainHideLayout.setVisibility(View.INVISIBLE);
                                mMainFragmentBinding.mainDrawerLayout.setVisibility(View.INVISIBLE);
                            }
                        });
                        builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        builder.setCancelable(false).show();

                    } else {
                        builder.setMessage("비밀번호를 입력해 주세요");
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = editText.getText().toString();
                                if(BasicApp.prefs.getPasswordPreferences().equals(hashingPassword(password))) {
                                    mMainAdapter.setItem(mMainAdapter.getLockedBookmarkList());
                                    mMainFragmentBinding.mainHideLayout.setVisibility(View.INVISIBLE);
                                    mMainFragmentBinding.mainDrawerLayout.setVisibility(View.INVISIBLE);
                                } else {
                                    Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        builder.setCancelable(false);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.getWindow().setLayout(400,400);
                        alertDialog.show();
                    }

                } else if(position == 1) {
                    List<BookmarkEntity> todayBookmarkEntities = new ArrayList<>();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
                    Calendar calendar = Calendar.getInstance();
                    for(BookmarkEntity bookmarkEntity : mMainViewModel.getmBookmarkData()) {
                        try {
                            int day = formatter.parse(bookmarkEntity.getmDate()).getDate();
                            if(day == calendar.get(Calendar.DATE)) {
                                todayBookmarkEntities.add(bookmarkEntity);
                            }
                        } catch (ParseException e) {
                            e.toString();
                        }
                    }
                    mMainAdapter.setItem(todayBookmarkEntities);
                    mMainFragmentBinding.mainHideLayout.setVisibility(View.INVISIBLE);
                    mMainFragmentBinding.mainDrawerLayout.setVisibility(View.INVISIBLE);
                } else if(position == 2) {
                    if(isExternalStorageReadable()) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        startActivityForResult(intent, PICK_FROM_EXTERNAL_STORAGE);
                    } else {
                        Toast.makeText(getContext(), "접근 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                    }
                } else if(position == 3){//북마크 내보내기
                    createFile();
                } else if(position == 4) {
                    Intent intent =  new Intent(getContext(), SettingActivity.class);
                    startActivity(intent);
                }
            }
        });

        mMainFragmentBinding.fab1.setOnClickListener(v -> {
            anim();
        });

        mMainFragmentBinding.fab2.setOnClickListener(v -> {
            anim();
            Intent intent = new Intent(getContext(), WebViewActivity.class);
            startActivity(intent);
        });

        mMainFragmentBinding.fab3.setOnClickListener(v -> {
            anim();
            Intent intent = new Intent(getContext(), PopupActivity.class);
            intent.putExtra("activity", "floatingButton");
            startActivityForResult(intent, 99);
        });

        mMainFragmentBinding.mainFilteringBarStar.setOnClickListener(v -> {

            if(isStarBtnClicked) {
                isStarBtnClicked = false;
                subscribeUi(mMainViewModel.getmBookmarks());
                //Drawable image = getContext().getResources().getDrawable(R.drawable.ic_star_black_24dp);
                //mMainFragmentBinding.mainFilteringBarStar.setCompoundDrawables(image, null, null, null);
            } else {
                isStarBtnClicked = true;
                MainAdapter mainAdapter = (MainAdapter) mMainFragmentBinding.rcyBookmarkList.getAdapter();
                List<BookmarkEntity> staredBookmarkEntities = new ArrayList<>();
                for(BookmarkEntity bookmarkEntity : mainAdapter.getmBookmarkList()) {
                    if(bookmarkEntity.getmIsStared()) {
                        staredBookmarkEntities.add(bookmarkEntity);
                    }
                }
                mMainAdapter.setItem(staredBookmarkEntities);
                //mMainFragmentBinding.setBookmarkEntities(staredBookmarkEntities);
                //Drawable image = getContext().getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
                //mMainFragmentBinding.mainFilteringBarStar.setCompoundDrawables(image, null, null, null);
            }
        });

        mMainFragmentBinding.btnSearch.setOnClickListener(v -> {
            if(isSearchBtnClicked) {
                if(!mMainFragmentBinding.editSearch.getText().toString().isEmpty()) {
                    String text = mMainFragmentBinding.editSearch.getText().toString();
                    MainAdapter mainAdapter = (MainAdapter) mMainFragmentBinding.rcyBookmarkList.getAdapter();
                    List<BookmarkEntity> bookmarkEntities = new ArrayList<>();
                    for(BookmarkEntity bookmarkEntity : mainAdapter.getmBookmarkList()) {
                        for(String tag : bookmarkEntity.getmTags().split(" ")) {
                            if(text.equals(tag)) {
                                bookmarkEntities.add(bookmarkEntity);
                                break;
                            }
                        }
                    }
                    mMainAdapter.setItem(bookmarkEntities);
                } else {
                    isSearchBtnClicked = false;
                    mMainFragmentBinding.btnListToggling.setVisibility(View.VISIBLE);
                    mMainFragmentBinding.editSearch.setVisibility(View.GONE);
                }
            } else {
                isSearchBtnClicked = true;
                mMainFragmentBinding.btnListToggling.setVisibility(View.GONE);
                mMainFragmentBinding.editSearch.setVisibility(View.VISIBLE);
            }
        });

        mMainFragmentBinding.btnListToggling.setOnClickListener(v -> {
            if(isTogglingBtnClicked) {
                isTogglingBtnClicked = false;
                mMainFragmentBinding.btnListToggling.setImageResource(R.drawable.ic_format_list_bulleted_black_36dp);
                MainAdapter mainAdapter = (MainAdapter) mMainFragmentBinding.rcyBookmarkList.getAdapter();
                List<BookmarkEntity> bookmarkEntities = mainAdapter.getmBookmarkList();
                for(BookmarkEntity bookmarkEntity : bookmarkEntities) {
                    for(HashMap.Entry<String, byte[]> map : haveImageBookmarkList.entrySet()) {
                        if(bookmarkEntity.getmUrl().equals(map.getKey())) {
                            bookmarkEntity.setmImage(map.getValue());
                            break;
                        }
                    }
                }
                mMainAdapter.setItem(bookmarkEntities);
                mMainAdapter.setVisible(true);
            } else {
                isTogglingBtnClicked = true;
                mMainFragmentBinding.btnListToggling.setImageResource(R.drawable.ic_image_black_24dp);
                haveImageBookmarkList = new HashMap<>();
                MainAdapter mainAdapter = (MainAdapter) mMainFragmentBinding.rcyBookmarkList.getAdapter();
                List<BookmarkEntity> bookmarkEntities = mainAdapter.getmBookmarkList();
                for(BookmarkEntity bookmarkEntity : bookmarkEntities) {
                    if(bookmarkEntity.getmImage() != null) {
                        haveImageBookmarkList.put(bookmarkEntity.getmUrl(), bookmarkEntity.getmImage());
                        bookmarkEntity.setmImage(null);
                    }
                }
                mMainAdapter.setItem(bookmarkEntities);
            }
        });

        mMainFragmentBinding.mainFilteringBarSpinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortName = (String) mMainFragmentBinding.mainFilteringBarSpinnerSort.getItemAtPosition(position);
                if(sortName.equals("선택")) {
                    subscribeUi(mMainViewModel.getmBookmarks());
                } else if(sortName.equals("최신순")){
                    subscribeUi(mMainViewModel.getmBookmarks());
                    MainAdapter mainAdapter = (MainAdapter) mMainFragmentBinding.rcyBookmarkList.getAdapter();
                    List<BookmarkEntity> sortedBookmarkEntities = mainAdapter.getmBookmarkList();
                    Collections.sort(sortedBookmarkEntities, new Comparator<BookmarkEntity>() {
                        @Override
                        public int compare(BookmarkEntity o1, BookmarkEntity o2) {
                            return currentDateLong(o1.getmDate()) <= currentDateLong(o1.getmDate()) ? -1 : 1;
                        }
                    });
                    mMainAdapter.setItem(sortedBookmarkEntities);

                } else if(sortName.equals("오래된순")) {
                    subscribeUi(mMainViewModel.getmBookmarks());
                    MainAdapter mainAdapter = (MainAdapter) mMainFragmentBinding.rcyBookmarkList.getAdapter();
                    List<BookmarkEntity> sortedBookmarkEntities = mainAdapter.getmBookmarkList();
                    Collections.sort(sortedBookmarkEntities, new Comparator<BookmarkEntity>() {
                        @Override
                        public int compare(BookmarkEntity o1, BookmarkEntity o2) {
                            return currentDateLong(o1.getmDate()) <= currentDateLong(o1.getmDate()) ? 1 : -1;
                        }
                    });
                    mMainAdapter.setItem(sortedBookmarkEntities);

                } else if(sortName.equals("최근사용순")) {


                } else if(sortName.equals("읽지않음")) {


                } else if(sortName.equals("비밀북마크")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    final EditText editText = new EditText(getContext());
                    builder.setView(editText);
                    if(BasicApp.prefs.getPasswordPreferences().equals("")) {
                        builder.setMessage("비밀번호 만들기");
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = editText.getText().toString();
                                BasicApp.prefs.savePreferences(hashingPassword(password));
                                mMainAdapter.setItem(mMainAdapter.getLockedBookmarkList());
                                mMainFragmentBinding.mainHideLayout.setVisibility(View.INVISIBLE);
                                mMainFragmentBinding.mainDrawerLayout.setVisibility(View.INVISIBLE);
                            }
                        });
                        builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        builder.setCancelable(false).show();

                    } else {
                        builder.setMessage("비밀번호를 입력해 주세요");
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String password = editText.getText().toString();
                                if(BasicApp.prefs.getPasswordPreferences().equals(hashingPassword(password))) {
                                    mMainAdapter.setItem(mMainAdapter.getLockedBookmarkList());
                                    mMainFragmentBinding.mainHideLayout.setVisibility(View.INVISIBLE);
                                    mMainFragmentBinding.mainDrawerLayout.setVisibility(View.INVISIBLE);
                                } else {
                                    Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        builder.setCancelable(false);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.getWindow().setLayout(600,400);
                        alertDialog.show();

                        //mMainAdapter.setItem(mMainAdapter.getLockedBookmarkList());
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMainFragmentBinding.mainFilteringBarSpinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String color = (String) mMainFragmentBinding.mainFilteringBarSpinnerColor.getItemAtPosition(position);

                if(color.equals("초록")) {
                    subscribeUi(mMainViewModel.getmBookmarks());
                    MainAdapter mainAdapter = (MainAdapter) mMainFragmentBinding.rcyBookmarkList.getAdapter();
                    List<BookmarkEntity> sortedBookmarkEntities = new ArrayList<>();
                    for(BookmarkEntity bookmarkEntity : mainAdapter.getmBookmarkList()) {
                        if(bookmarkEntity.getmColor() == 1) {
                            sortedBookmarkEntities.add(bookmarkEntity);
                        }
                    }
                    mMainAdapter.setItem(sortedBookmarkEntities);

                } else if(color.equals("빨강")) {
                    subscribeUi(mMainViewModel.getmBookmarks());
                    MainAdapter mainAdapter = (MainAdapter) mMainFragmentBinding.rcyBookmarkList.getAdapter();
                    List<BookmarkEntity> sortedBookmarkEntities = new ArrayList<>();
                    for(BookmarkEntity bookmarkEntity : mainAdapter.getmBookmarkList()) {
                        if(bookmarkEntity.getmColor() == 2) {
                            sortedBookmarkEntities.add(bookmarkEntity);
                        }
                    }
                    mMainAdapter.setItem(sortedBookmarkEntities);
                } else if(color.equals("선택")) {
                    subscribeUi(mMainViewModel.getmBookmarks());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        // filter to only show openable items.
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Create a file with the requested Mime type
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_TITLE, "bookmarks.html");
        startActivityForResult(intent, Code.WRITE_REQUEST_CODE);
    }
    public void writeInFile(@NonNull Uri uri, @NonNull String text){
        OutputStream outputStream;
        try{
            outputStream = getActivity().getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(text);
            bw.flush();
            bw.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) { // 북마크 추가
            mMainFragmentBinding.executePendingBindings();
        }else if(requestCode == Code.WRITE_REQUEST_CODE){//북마크 내보내기
            switch (resultCode){
                case Activity.RESULT_OK :
                    if (data != null && data.getData() != null){
                        HtmlWriter hw = new HtmlWriter(mMainViewModel.getmBookmarkData());
                        writeInFile(data.getData(), hw.writeHtml());
                    }
                    break;
            }
        } else if(requestCode == PICK_FROM_EXTERNAL_STORAGE) {
            HashMap<String, String> hashMap = new HashMap<>();
            if(data == null)
                return;
            Uri uri = data.getData();
            try {
                InputStream inputStream = this.getContext().getContentResolver().openInputStream(uri);
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    if(line.trim().startsWith("<DT><A")) {
                        String url = line.split("\"")[1];
                        String title = line.split("<")[2].split(">")[1];
                        hashMap.put(url, title);
                        new WebViewActivity.networkAsyncTask(getContext(), title, url).execute();
                        Log.d("test", title);
                    }
                }
            } catch (Exception e) {
                e.toString();
            }
        }

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public static String hashingPassword(String password) { // SHA-1 apache commons codec library 사용
        return new String(Hex.encodeHex(DigestUtils.sha1(password)));
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
