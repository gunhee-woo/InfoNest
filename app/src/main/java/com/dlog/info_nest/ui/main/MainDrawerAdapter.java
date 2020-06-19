package com.dlog.info_nest.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.MainDrawerItemBinding;

import java.util.ArrayList;

public class MainDrawerAdapter extends BaseAdapter {

    private ArrayList<DrawerListItem> drawerListItems;

    public MainDrawerAdapter(ArrayList<DrawerListItem> arrayList) {
        this.drawerListItems = arrayList;
    }

    @Override
    public int getCount() {
        return drawerListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return drawerListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MainDrawerItemBinding mainDrawerItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.main_drawer_item, parent, false);
        mainDrawerItemBinding.setDraweritem(drawerListItems.get(position));

        //mainDrawerItemBinding.setVariable(BR.draweritem, drawerListItems.get(position));
        return mainDrawerItemBinding.getRoot();
    }
}
