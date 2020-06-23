package com.dlog.info_nest.utilities;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperListener {
    void onItemSwipe(int position);
    void onRightClick(int position, RecyclerView.ViewHolder viewHolder);
    void onLeftClick(int position, RecyclerView.ViewHolder viewHolder);
}
