package com.dlog.info_nest.ui.main;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.RecyclerView;

import com.dlog.info_nest.R;
import com.dlog.info_nest.db.entity.BookmarkEntity;

import static com.dlog.info_nest.ui.main.MainAdapter.byteArrayToBitmap;

public class AdapterBindings {
    @BindingAdapter("bind:item")
    public static void bindItem(RecyclerView recyclerView, ObservableArrayList<BookmarkEntity> bookmarkEntities) {
        MainAdapter adapter = (MainAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            ObservableArrayList<BookmarkEntity> observableArrayList = new ObservableArrayList<>();
            if(bookmarkEntities != null) {
                for (BookmarkEntity bookmarkEntity : bookmarkEntities) {
                    if (!bookmarkEntity.getmIsLocked())
                        observableArrayList.add(bookmarkEntity);
                }
            }
            adapter.setItem(observableArrayList);
            //adapter.setItem(bookmarkEntities);
        }
    }

    @BindingAdapter({"bind:background"})
    public static void setImage(ImageView imageView, byte[] image) {
        if(image == null) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setImageBitmap(byteArrayToBitmap(image));
            imageView.setVisibility(View.VISIBLE);
        }
    }

    @BindingAdapter({"bind:drawable"})
    public static void setDrawable(ImageView imageView, Boolean isClicked) {
        if(imageView.getId() == R.id.main_star_btn) {
            if(isClicked)
                imageView.setImageResource(R.drawable.ic_star_black_24dp);
            else
                imageView.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
        if(imageView.getId() == R.id.main_lock_btn) {
            if(isClicked)
                imageView.setImageResource(R.drawable.ic_lock_outline_black_24dp);
            else
                imageView.setImageResource(R.drawable.ic_lock_open_black_24dp);
        }
    }

    @BindingAdapter({"bind:visibility"})
    public static void setVisibility(ImageView imageView, Boolean visible) {
        if(visible) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    @BindingAdapter({"image_resource"})
    public static void setSrc(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }


}
