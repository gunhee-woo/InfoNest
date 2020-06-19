package com.dlog.info_nest.ui.main;

import android.graphics.drawable.Drawable;

public class DrawerListItem {
    private int drawable;
    private String title;

    public DrawerListItem(int drawable, String title) {
        this.drawable = drawable;
        this.title = title;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
