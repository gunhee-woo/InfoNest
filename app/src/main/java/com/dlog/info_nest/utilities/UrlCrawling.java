package com.dlog.info_nest.utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import static com.dlog.info_nest.utilities.AppExecutorsHelperKt.runOnDiskIO;


public class UrlCrawling {
    Document document;

    public UrlCrawling(String url) {
        runOnDiskIO(() -> {
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> getUrlToNouns() {
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        List<String> originNouns = new ArrayList<>();
        KomoranResult analyzeResultList = komoran.analyze(document.text());
        for(Token token : analyzeResultList.getTokenList()) {
            if(token.getPos().equals("NNG") || token.getPos().equals("NNP") || token.getPos().equals("NNB")) {
                originNouns.add(token.getMorph());
            }
        }
        return originNouns;
    }

    public List<String> getUrlToTop10NounsArray() {
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        HashMap<String, Integer> hashMap = new HashMap<>();
        ArrayList<String> top10Nouns = new ArrayList<>();
        KomoranResult analyzeResultList = komoran.analyze(document.text());
        for(Token token : analyzeResultList.getTokenList()) {
            if(token.getPos().equals("NNG") || token.getPos().equals("NNP") || token.getPos().equals("NNB")) {
                if(hashMap.containsKey(token.getMorph())) {
                    int count = hashMap.get(token.getMorph());
                    hashMap.put(token.getMorph(), count + 1);
                } else {
                    hashMap.put(token.getMorph(), 1);
                }
            }
        }
        List<Map.Entry<String, Integer>> list = new LinkedList<>(hashMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() { // value 내림차순 정렬, 같으면 key 오름차순 정렬
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int comparision = (o1.getValue() - o2.getValue()) * -1;
                return comparision == 0 ? o1.getKey().compareTo(o2.getKey()) : comparision;
            }
        });
        int ix = 0;
        for(Iterator<Map.Entry<String, Integer>> iter = list.iterator(); iter.hasNext();){
            if(ix == 10)
                break;
            Map.Entry<String, Integer> entry = iter.next();
            top10Nouns.add(entry.getKey());
            ix++;
        }
        return top10Nouns;
    }

    public HashMap<String, Integer> getUrlToTop10Nouns() {
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        HashMap<String, Integer> hashMap = new HashMap<>();
        HashMap<String, Integer> top10Nouns = new HashMap<>();
        KomoranResult analyzeResultList = komoran.analyze(document.text());
        for(Token token : analyzeResultList.getTokenList()) {
            if(token.getPos().equals("NNG") || token.getPos().equals("NNP") || token.getPos().equals("NNB")) {
                if(hashMap.containsKey(token.getMorph())) {
                    int count = hashMap.get(token.getMorph());
                    hashMap.put(token.getMorph(), count + 1);
                } else {
                    hashMap.put(token.getMorph(), 1);
                }
            }
        }
        List<Map.Entry<String, Integer>> list = new LinkedList<>(hashMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() { // value 내림차순 정렬, 같으면 key 오름차순 정렬
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int comparision = (o1.getValue() - o2.getValue()) * -1;
                return comparision == 0 ? o1.getKey().compareTo(o2.getKey()) : comparision;
            }
        });
        int ix = 0;
        for(Iterator<Map.Entry<String, Integer>> iter = list.iterator(); iter.hasNext();){
            if(ix == 10)
                break;
            Map.Entry<String, Integer> entry = iter.next();
            top10Nouns.put(entry.getKey(), entry.getValue());
            ix++;
        }
        return top10Nouns;
    }

    public String getUrlToImageUrl() {
        Element ogImage = document.select("meta[property=og:image]").first();
        String imageUrl = ogImage.attr("content");
        return imageUrl;
    }

    public static void addDelay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
            ignored.printStackTrace();
        }
    }
}
