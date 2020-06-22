package com.dlog.info_nest.ui.palette;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dlog.info_nest.R;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.ui.main.MainViewModel;
import com.dlog.info_nest.ui.palette.adapters.BookMarkListAdapter;
import com.dlog.info_nest.utilities.Code;

import java.util.List;

public class BookMarkListPopupActivity  extends AppCompatActivity {
    private MainViewModel mMainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀바없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pop_up_widget_list);

        mMainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        List<BookmarkEntity> bookmarkEntities = mMainViewModel.getmBookmarkData();
        RecyclerView recyclerView = findViewById(R.id.rcyl_widget_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        BookMarkListAdapter adapter = new BookMarkListAdapter(bookmarkEntities);
        adapter.setOnItemClickListener(new BookMarkListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent addIntent = new Intent(getApplicationContext(), PopupActivity.class);
                addIntent.putExtra("Title", bookmarkEntities.get(position).mTitle);
                addIntent.putExtra("Url", bookmarkEntities.get(position).mUrl);
                addIntent.putExtra("RequestCode", Code.RQ_TOPOPUP_LIST_ADD);
                startActivityForResult(addIntent, Code.RQ_TOPOPUP_LIST_ADD);
            }
        });
        recyclerView.setAdapter(adapter);

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
}
