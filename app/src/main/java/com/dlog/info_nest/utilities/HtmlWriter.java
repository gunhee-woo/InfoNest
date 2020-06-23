package com.dlog.info_nest.utilities;

import android.content.Intent;
import android.util.Log;

import com.dlog.info_nest.db.entity.BookmarkEntity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

public class HtmlWriter {
    private List<BookmarkEntity> bookmarkList;
    public HtmlWriter(List<BookmarkEntity> bookmarkEntityList){
        this.bookmarkList = bookmarkEntityList;
    }
    public String writeHtml(){
        String time = String.valueOf(System.currentTimeMillis());
        time = time.substring(0,time.length()-4);

        Document doc = Jsoup.parse("<html></html>");

        doc.body().appendElement("h1");
        //h1
        doc.body().child(0).appendText("Bookmarks");

        doc.body().appendElement("dl");
        //dl > p make
        doc.body().child(1).appendElement("p");


        //dl > dt make
        doc.body().child(1).appendElement("dt");

        //dl > dt > h3 make
        doc.body().child(1).child(1).appendElement("h3");

        //dl > dt > h3 attributes setting
        doc.body().child(1).child(1).child(0).attr("add_date", time);
        doc.body().child(1).child(1).child(0).attr("last_modified", time);
        doc.body().child(1).child(1).child(0).attr("personal_toolbar_folder", "true");
        doc.body().child(1).child(1).child(0).appendText("북마크바");

        //dl > dt > dl make
        doc.body().child(1).child(1).appendElement("dl");
        //dl > dt > dl > p make
        doc.body().child(1).child(1).child(1).appendElement("p");

        for (int i = 0 ; i < bookmarkList.size() ; i ++){
            //여기서 부터 북마크 데이터
            //dl > dt > dl > dt make
            doc.body().child(1).child(1).child(1).appendElement("dt");
            //dl > dt > dl > dt > a make
            doc.body().child(1).child(1).child(1).child(i+1).appendElement("a");

            doc.body().child(1).child(1).child(1).child(i+1).child(0).attr("href", bookmarkList.get(i).mUrl);
            doc.body().child(1).child(1).child(1).child(i+1).child(0).attr("add_date", time);
            doc.body().child(1).child(1).child(1).child(i+1).child(0).appendText(bookmarkList.get(i).mTitle);
        }


        Log.d("TTT",doc.toString());
        return doc.toString();
    }


}
