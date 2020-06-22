package com.dlog.info_nest.utilities;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * thread 열어서 실행되어야함.
 * url의 meta와 title을 크롤링한다.
 */
public class Domparser {
    private String url;
    public Domparser(String url){
        this.url = url;
    }
    public String getDoc(){
        try{
            StringBuilder allWordString = new StringBuilder();
            int allLinesCnt = 0;

            org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
            //meta content 크롤링
            Elements elements = doc.getElementsByTag("meta");
            for(Element e : elements){
                String content = e.attr("content");
                //한글이 아니면 제거
                content = content.replaceAll("[^\\uAC00-\\uD7AF\\u1100-\\u11FF\\u3130-\\u318F]+", " ");
                //공백으로만 구성된 문자열 제거
                if (content.matches("^\\s+$")) {
                    content = "";
                }
                if (content != null && content.length() != 0) {
                    Log.d("TTT", content);
                    allWordString.append(content);
                    allWordString.append(". ");
                    allLinesCnt++;
                }
            }
            //title 크롤링
            allWordString.append(doc.select("head > title").text());
            allLinesCnt++;
            if(allLinesCnt == 1) {
                allWordString.append(". " + allWordString.toString());
            }

            return  allWordString.toString();
        }catch (Exception e){
            Log.d("TTT","jsoup error : " + e.toString());
            return "";
        }
    }
    public String getTitle() throws IOException {

        org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
        Elements elements = doc.getElementsByTag("meta");
        return doc.select("head > title").text();
    }

}
