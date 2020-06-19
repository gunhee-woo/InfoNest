package com.dlog.info_nest.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;

import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.model.Bookmark;
import com.moxun.tagcloudlib.view.TagsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by moxun on 16/1/19.
 */
public class TextTagsAdapter extends TagsAdapter {

    private List<String> dataSet = new ArrayList<>();

    public TextTagsAdapter(@NonNull List<BookmarkEntity> bookmarks) {
        List<String> bookmarkNouns = new ArrayList<>();
        for(BookmarkEntity bookmarkEntity : bookmarks) {
            bookmarkNouns.addAll(bookmarkEntity.getmNouns());
        }
        dataSet.clear();
        dataSet = bookmarkNouns;
    }

    public TextTagsAdapter() {

    }

    public void setTextTag(final List<BookmarkEntity> bookmarks) {
        List<String> bookmarkNouns = new ArrayList<>();
        for(BookmarkEntity bookmarkEntity : bookmarks) {
            bookmarkNouns.addAll(bookmarkEntity.getmNouns());
        }
        dataSet = bookmarkNouns;
        notifyDataSetChanged();
        /*if(dataSet == null) {
            dataSet = bookmarkNouns;
            notifyDataSetChanged();
        } else {
            final TextTagDiffCallback diffCallback = new TextTagDiffCallback(dataSet, bookmarkNouns);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            this.dataSet.clear();
            this.dataSet.addAll(bookmarkNouns);

            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return dataSet.size();
                }

                @Override
                public int getNewListSize() {
                    return bookmarkNouns.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return dataSet.get(oldItemPosition) == bookmarkNouns.get(newItemPosition);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    String newTags = bookmarkNouns.get(newItemPosition);
                    String oldTags = dataSet.get(oldItemPosition);
                    return newTags == oldTags;
                }
            });
            dataSet = bookmarkNouns;
            diffResult.dispatchUpdatesTo((ListUpdateCallback) this);
        }*/
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public View getView(final Context context, final int position, ViewGroup parent) {
        TextView tv = new TextView(context);
        tv.setText(dataSet.get(position));
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(v -> {
            Log.e("Click", "Tag " + position + " clicked.");
            Toast.makeText(context, dataSet.get(position), Toast.LENGTH_SHORT).show();
        });
        tv.setTextColor(Color.WHITE);
        return tv;
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public int getPopularity(int position) {
        return position % 7;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {
        view.setBackgroundColor(themeColor);
    }
}