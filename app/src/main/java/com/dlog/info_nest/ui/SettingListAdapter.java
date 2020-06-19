package com.dlog.info_nest.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.SettingItemBinding;

import java.util.ArrayList;

public class SettingListAdapter extends BaseAdapter {
    private ArrayList<SettingListItem> settingListItems;

    public SettingListAdapter(ArrayList<SettingListItem> settingListItems) {
        this.settingListItems = settingListItems;
    }

    @Override
    public int getCount() {
        return settingListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return settingListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SettingItemBinding settingItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.setting_item, parent, false);
        settingItemBinding.setSettingitem(settingListItems.get(position));
        return settingItemBinding.getRoot();
    }
}
