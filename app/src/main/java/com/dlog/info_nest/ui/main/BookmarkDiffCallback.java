package com.dlog.info_nest.ui.main;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.dlog.info_nest.db.entity.BookmarkEntity;

import java.util.List;

public class BookmarkDiffCallback extends DiffUtil.Callback {
    private final List<BookmarkEntity> mOldWords;
    private final List<BookmarkEntity> mNewWords;

    public BookmarkDiffCallback(List<BookmarkEntity> mOldWords, List<BookmarkEntity> mNewWords) {
        this.mOldWords = mOldWords;
        this.mNewWords = mNewWords;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        BookmarkEntity newItem = mNewWords.get(newItemPosition);
        BookmarkEntity oldItem = mOldWords.get(oldItemPosition);
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }

    @Override
    public int getOldListSize() {
        return mOldWords.size();
    }

    @Override
    public int getNewListSize() {
        return mNewWords.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldWords.get(oldItemPosition).getmUrl().equals(mNewWords.get(newItemPosition).getmUrl());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        BookmarkEntity newTags = mNewWords.get(newItemPosition);
        BookmarkEntity oldTags = mOldWords.get(oldItemPosition);
        return newTags.getmIsLocked() == oldTags.getmIsLocked()
                && newTags.getmIsStared() == oldTags.getmIsStared()
                && TextUtils.equals(newTags.getmDate(), oldTags.getmDate())
                && newTags.getmImage() == oldTags.getmImage()
                && newTags.getmColor() == oldTags.getmColor()
                && newTags.getmNouns().equals(oldTags.getmNouns())
                && TextUtils.equals(newTags.getmTags(), oldTags.getmTags())
                && TextUtils.equals(newTags.getmTitle(), oldTags.getmTitle());
    }
}
