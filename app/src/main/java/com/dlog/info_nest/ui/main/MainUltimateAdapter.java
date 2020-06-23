package com.dlog.info_nest.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dlog.info_nest.BR;
import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.MainRecyclerItemBinding;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.ui.WebViewActivity;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;
import java.util.List;

// 스와이프 기능을 담고 있는 리사이클러뷰 라이브러리를 사용하는 어댑터
public class MainUltimateAdapter extends UltimateViewAdapter {

    private List<BookmarkEntity> mBookmarkList;
    private Context mContext;
    private Boolean isVisible = true;
    private List<BookmarkEntity> lockedBookmarkList;

    public MainUltimateAdapter(Context context) {
        mBookmarkList = new ArrayList<>();
        lockedBookmarkList = new ArrayList<>();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder newFooterHolder(View view) {
        return new UltimateRecyclerviewViewHolder<>(view);
    }

    @Override
    public RecyclerView.ViewHolder newHeaderHolder(View view) {
        return new UltimateRecyclerviewViewHolder<>(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

        MainRecyclerItemBinding mainRecyclerItemBinding = MainRecyclerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                /*.inflate(LayoutInflater.from(parent.getContext()), R.layout.main_recycler_item,
                        parent, false);*/
        return new MainUltimateViewHolder(mainRecyclerItemBinding);
    }

    @Override
    public int getAdapterItemCount() {
        return mBookmarkList.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BookmarkEntity bookmarkEntity = mBookmarkList.get(position);

        if(bookmarkEntity.getmIsStared()) {
            ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainStarBtn.setImageResource(R.drawable.ic_star_black_24dp);
        }

        ((MainUltimateViewHolder)holder).imageVisible(isVisible);

        ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainImageView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("url", bookmarkEntity.getmUrl());
            mContext.startActivity(intent);
        });

        switch (bookmarkEntity.getmColor()) {
            case 1:
                ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.bookmarkItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGreen));
                ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainTagEdit.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.colorGreen));
                break;
            case 2:
                ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.bookmarkItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorRed));
                ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainTagEdit.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.colorRed));
                break;
            case 3:
                ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.bookmarkItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainTagEdit.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.colorPrimary));
                break;
            case 4:
                ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.bookmarkItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainTagEdit.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.white));
                break;
        }

        ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainTagEdit.setTags(bookmarkEntity.getmTags().split(" "));

        ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainTagEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.d("test", v.toString());
                return false;
            }
        });

        /*((MainUltimateViewHolder)holder).mainRecyclerItemBinding.bookmarkItem.setOnTouchListener(new View.OnTouchListener() {
            float _xSwipe1;
            float _xSwipe2;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        _xSwipe1 = event.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        _xSwipe2 = event.getX();

                        float deltaX = _xSwipe2 - _xSwipe1;

                        if (deltaX < 0)
                        {
                            Log.e("SWIPE", "Right to Left swipe");
                        }

                        else if (deltaX >0)
                        {
                            Log.e("SWIPE", "Left to right swipe");
                        }

                        break;
                }
                return false;
            }
        });*/

        ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainTitleView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("url", bookmarkEntity.getmUrl());
            mContext.startActivity(intent);
        });

        ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainStarBtn.setOnClickListener(v -> {
            if(bookmarkEntity.getmIsStared()) { //즐겨찾기 되어있으면
                bookmarkEntity.setmIsStared(false);
                ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainStarBtn.setImageResource(R.drawable.ic_star_border_black_24dp);
            } else {
                bookmarkEntity.setmIsStared(true);
                ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainStarBtn.setImageResource(R.drawable.ic_star_black_24dp);
            }
        });

        ((MainUltimateViewHolder)holder).mainRecyclerItemBinding.mainLockBtn.setOnClickListener(v -> {
            if(bookmarkEntity.getmIsLocked()) {
                openAlertDialog(mContext, bookmarkEntity, position, 0);
            } else {
                openAlertDialog(mContext, bookmarkEntity, position, 1);
            }
        });

        ((MainUltimateViewHolder)holder).bind(bookmarkEntity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }



    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public void openAlertDialog(Context context, BookmarkEntity bookmarkEntity, int position, int ix) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        if(ix == 0) {
            builder.setMessage("북마크 잠금을 해제하시겠습니까?")
                    .setTitle("알림")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bookmarkEntity.setmIsLocked(false);
                            Toast.makeText(mContext, "잠금이 해제되었습니다", Toast.LENGTH_SHORT).show();
                            lockedBookmarkList.remove(bookmarkEntity);
                            notifyDataSetChanged();
                        }
                    })
                    .setNeutralButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mContext, "취소했습니다", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setCancelable(false)
                    .show();
        } else if(ix == 1) {
            builder.setMessage("북마크를 잠그시겠습니까?")
                    .setTitle("알림")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bookmarkEntity.setmIsLocked(true);
                            Toast.makeText(mContext, "비밀 북마크로 이동합니다", Toast.LENGTH_SHORT).show();
                            lockedBookmarkList.add(bookmarkEntity);
                            mBookmarkList.remove(position);
                            notifyDataSetChanged();
                        }
                    })
                    .setNeutralButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mContext, "취소했습니다", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

    }


    public static Bitmap byteArrayToBitmap(byte[] $byteArray ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;
        return bitmap ;
    }

    void setItem(List<BookmarkEntity> bookmarkEntities) {
        if (mBookmarkList == null) {
            mBookmarkList = bookmarkEntities;
            notifyItemRangeInserted(0, bookmarkEntities.size());
        } else {
            //final BookmarkDiffCallback diffCallback = new BookmarkDiffCallback(this.mBookmarkList, bookmarkEntities);
            //final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            mBookmarkList = bookmarkEntities;
            notifyDataSetChanged();
            //diffResult.dispatchUpdatesTo(this);
        }

    }

    public void setVisible(boolean b) {
        isVisible = b;
    }

    public List<BookmarkEntity> getLockedBookmarkList() {
        return lockedBookmarkList;
    }

    public List<BookmarkEntity> getmBookmarkList() {
        return mBookmarkList;
    }

    public class MainUltimateViewHolder extends UltimateRecyclerviewViewHolder {
        final MainRecyclerItemBinding mainRecyclerItemBinding;

        public MainUltimateViewHolder(MainRecyclerItemBinding mainItemBinding) {
            super(mainItemBinding.getRoot());
            this.mainRecyclerItemBinding = mainItemBinding;
        }

        void bind(BookmarkEntity bookmarkEntity) {
            mainRecyclerItemBinding.setVariable(BR.bookmarkrcyitem, bookmarkEntity);
        }

        void imageVisible(Boolean b) {
            mainRecyclerItemBinding.setVariable(BR.imageVisible, b);
        }
    }
}
