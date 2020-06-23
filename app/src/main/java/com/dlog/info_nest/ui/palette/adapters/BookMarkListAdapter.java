package com.dlog.info_nest.ui.palette.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dlog.info_nest.R;
import com.dlog.info_nest.db.entity.BookmarkEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookMarkListAdapter extends RecyclerView.Adapter<BookMarkListAdapter.ViewHolder> implements Filterable {
    /**
     * 필터링되지 않은 원본 리스트
     */
    private List<BookmarkEntity> mUnFilterBookmarkList;
    /**
     * 필터링된 리스트
     */
    private List<BookmarkEntity> mFilterBookmarkList;
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    public BookMarkListAdapter(List<BookmarkEntity> list){
        this.mFilterBookmarkList = list;
        this.mUnFilterBookmarkList = list;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()){
                    mFilterBookmarkList = mUnFilterBookmarkList;
                }else {
                    ArrayList<BookmarkEntity> filteringList = new ArrayList<>();
                    for(int i = 0 ; i < mUnFilterBookmarkList.size() ; i++){
                        BookmarkEntity bookmarkEntity = mUnFilterBookmarkList.get(i);
                        String title = bookmarkEntity.mTitle;
                        String tags = bookmarkEntity.mTags;
                        if(title.toLowerCase().contains(charString.toLowerCase())
                                || tags.toLowerCase().contains(charString.toLowerCase())){
                            filteringList.add(bookmarkEntity);
                        }
                    }
                    mFilterBookmarkList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilterBookmarkList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilterBookmarkList = (ArrayList<BookmarkEntity>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_rcyl_item, parent, false);;
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_title.setText(mFilterBookmarkList.get(position).mTitle);
        holder.txt_url.setText(mFilterBookmarkList.get(position).mUrl);
    }

    @Override
    public int getItemCount() {
        return mFilterBookmarkList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_title;
        TextView txt_url;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_bookmark_title);
            txt_url = itemView.findViewById(R.id.txt_bookmark_url);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });

        }
    }
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public List<BookmarkEntity> getmFilterBookmarkList() {
        return mFilterBookmarkList;
    }
}
