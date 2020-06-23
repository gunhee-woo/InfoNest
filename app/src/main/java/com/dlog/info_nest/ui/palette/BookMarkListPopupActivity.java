package com.dlog.info_nest.ui.palette;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dlog.info_nest.R;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.ui.main.MainViewModel;
import com.dlog.info_nest.ui.palette.adapters.BookMarkListAdapter;
import com.dlog.info_nest.utilities.Code;

import java.util.List;

public class BookMarkListPopupActivity  extends AppCompatActivity implements TextWatcher {
    private MainViewModel mMainViewModel;
    BookMarkListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀바없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pop_up_widget_list);

        EditText edt_search = findViewById(R.id.edt_search_bookmark_widget);
        edt_search.addTextChangedListener(this);

        mMainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        List<BookmarkEntity> bookmarkEntities = mMainViewModel.getmBookmarkData();
        RecyclerView recyclerView = findViewById(R.id.rcyl_widget_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new BookMarkListAdapter(bookmarkEntities);
        mAdapter.setOnItemClickListener(new BookMarkListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent addIntent = new Intent(getApplicationContext(), PopupActivity.class);
                addIntent.putExtra("Title", mAdapter.getmFilterBookmarkList().get(position).mTitle);
                addIntent.putExtra("Url", mAdapter.getmFilterBookmarkList().get(position).mUrl);
                addIntent.putExtra("RequestCode", Code.RQ_TOPOPUP_LIST_ADD);
                startActivityForResult(addIntent, Code.RQ_TOPOPUP_LIST_ADD);
            }
        });
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Code.RS_TOWIDGET_ADD){
            if(data != null) {
                Intent result = new Intent();
                result.putExtra("WidgetItem", data.getSerializableExtra("WidgetItem"));
                setResult(Code.RS_TOWIDGET_ADD, result);
                finish();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try{
            mAdapter.getFilter().filter(s);
        }catch (Exception e){
            Log.e("TTT", "text changed error " + e.getMessage());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
