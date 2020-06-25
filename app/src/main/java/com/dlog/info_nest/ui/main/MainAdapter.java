package com.dlog.info_nest.ui.main;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.dlog.info_nest.BR;
import com.dlog.info_nest.BasicApp;
import com.dlog.info_nest.DataRepository;
import com.dlog.info_nest.MainActivity;
import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.MainFragmentBinding;
import com.dlog.info_nest.databinding.MainRecyclerItemBinding;
import com.dlog.info_nest.db.WidgetDB;
import com.dlog.info_nest.db.WidgetDB2;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.ui.PopupActivity;
import com.dlog.info_nest.ui.WebViewActivity;
import com.dlog.info_nest.ui.palette.My_Widget_Provider;
import com.dlog.info_nest.ui.palette.My_Widget_Provider2;
import com.dlog.info_nest.utilities.ItemTouchHelperListener;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> implements ItemTouchHelperListener , Filterable {

    private List<BookmarkEntity> mBookmarkList;
    private Context mContext;
    private Boolean isVisible = true;
    private List<BookmarkEntity> lockedBookmarkList;
    private DataRepository mDataRepository;

    /**
     * 검색 필터링 전 리스트
     */
    private List<BookmarkEntity> mUnfilterBookmarkList;

    public MainAdapter(Context context) {
        this.mBookmarkList = new ArrayList<>();
        lockedBookmarkList = new ArrayList<>();
        mContext = context;
        //mDataRepository = ((BasicApp) getApplication()).getDataRepository();
        mDataRepository = ((BasicApp)((MainActivity) mContext).getApplication()).getDataRepository();
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            MainRecyclerItemBinding mainRecyclerItemBinding = MainRecyclerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                /*.inflate(LayoutInflater.from(parent.getContext()), R.layout.main_recycler_item,
                        parent, false);*/
            return new MainViewHolder(mainRecyclerItemBinding);
    }

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        BookmarkEntity bookmarkEntity = mBookmarkList.get(position);

        if(bookmarkEntity.getmIsStared()) {
            holder.mainRecyclerItemBinding.mainStarBtn.setImageResource(R.drawable.ic_star_black_24dp);
        }

        holder.imageVisible(isVisible);

        holder.mainRecyclerItemBinding.mainImageView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("url", bookmarkEntity.getmUrl());
            mContext.startActivity(intent);
        });

        switch (bookmarkEntity.getmColor()) {
            case 1:
                holder.mainRecyclerItemBinding.bookmarkItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGreen));
                holder.mainRecyclerItemBinding.mainTagEdit.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.colorGreen));
                break;
            case 2:
                holder.mainRecyclerItemBinding.bookmarkItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorRed));
                holder.mainRecyclerItemBinding.mainTagEdit.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.colorRed));
                break;
            case 3:
                holder.mainRecyclerItemBinding.bookmarkItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                holder.mainRecyclerItemBinding.mainTagEdit.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.colorPrimary));
                break;
            case 4:
                holder.mainRecyclerItemBinding.bookmarkItem.setBackgroundColor(R.color.white);
                holder.mainRecyclerItemBinding.mainTagEdit.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.white));
                break;
        }

        holder.mainRecyclerItemBinding.mainTagEdit.setTags(bookmarkEntity.getmTags().split(" "));

        holder.mainRecyclerItemBinding.mainTagEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.d("test", v.toString());
                return false;
            }
        });

        holder.mainRecyclerItemBinding.mainTitleView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("url", bookmarkEntity.getmUrl());
            mContext.startActivity(intent);
        });

        holder.mainRecyclerItemBinding.mainStarBtn.setOnClickListener(v -> {
            if(bookmarkEntity.getmIsStared()) { //즐겨찾기 되어있으면
                bookmarkEntity.setmIsStared(false);
                holder.mainRecyclerItemBinding.mainStarBtn.setImageResource(R.drawable.ic_star_border_black_24dp);
            } else {
                bookmarkEntity.setmIsStared(true);
                holder.mainRecyclerItemBinding.mainStarBtn.setImageResource(R.drawable.ic_star_black_24dp);
            }
        });

        holder.mainRecyclerItemBinding.mainLockBtn.setOnClickListener(v -> {
            if(bookmarkEntity.getmIsLocked()) {
                openAlertDialog(mContext, bookmarkEntity, position, 0);
            } else {
                openAlertDialog(mContext, bookmarkEntity, position, 1);
            }
        });

        holder.bind(bookmarkEntity);
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

    @Override
    public int getItemCount() {
        return mBookmarkList.size();
    }

    @Override
    public void onItemSwipe(int position) {
        mBookmarkList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onRightClick(int position, RecyclerView.ViewHolder viewHolder) {
        BookmarkEntity bookmarkEntity = mBookmarkList.get(position);
        mDataRepository.delete(bookmarkEntity);

        // 위젯도 삭제 !!
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                WidgetDB db = Room.databaseBuilder(mContext, WidgetDB.class, "widget").build();
                WidgetDB2 db2 = Room.databaseBuilder(mContext, WidgetDB2.class, "widget_list").build();
                db.widgetDao().deleteByUrl(bookmarkEntity.mUrl);
                db2.widgetDao2().deleteByUrl(bookmarkEntity.mUrl);
                Intent intent = new Intent(mContext, My_Widget_Provider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                mContext.sendBroadcast(intent);
                Intent intent2 = new Intent(mContext, My_Widget_Provider2.class);
                intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                mContext.sendBroadcast(intent2);
            }
        });
        t.start();

        mBookmarkList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onLeftClick(int position, RecyclerView.ViewHolder viewHolder) {
        Intent intent = new Intent(mContext, PopupActivity.class);
        intent.putExtra("bookmark", mBookmarkList.get(position));
        intent.putExtra("activity", "main");
        mContext.startActivity(intent);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()){
                    mBookmarkList = mUnfilterBookmarkList;
                }else {
                    ArrayList<BookmarkEntity> filteringList = new ArrayList<>();
                    for(int i = 0 ; i < mUnfilterBookmarkList.size() ; i++){
                        BookmarkEntity bookmarkEntity = mUnfilterBookmarkList.get(i);
                        String title = bookmarkEntity.mTitle;
                        String tags = bookmarkEntity.mTags;
                        if(title.toLowerCase().contains(charString.toLowerCase())
                                || tags.toLowerCase().contains(charString.toLowerCase())){
                            filteringList.add(bookmarkEntity);
                        }
                    }
                    mBookmarkList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mBookmarkList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mBookmarkList = (ArrayList<BookmarkEntity>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        final MainRecyclerItemBinding mainRecyclerItemBinding;

        public MainViewHolder(MainRecyclerItemBinding mainItemBinding) {
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

    public void setmUnfilterBookmarkList(List<BookmarkEntity> mUnfilterBookmarkList) {
        this.mUnfilterBookmarkList = mUnfilterBookmarkList;
    }

    public List<BookmarkEntity> getmUnfilterBookmarkList() {
        return mUnfilterBookmarkList;
    }

    public void searchFinish(){
        this.mBookmarkList = mUnfilterBookmarkList;
    }
}
