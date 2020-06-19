package com.dlog.info_nest.model;

import java.util.List;

public interface Bookmark {
    String getmTitle();
    String getmUrl();
    String getmTags();
    String getmDate();
    int getmColor();
    List<String> getmNouns();
    byte[] getmImage();
    boolean getmIsLocked();
    boolean getmIsStared();
}
