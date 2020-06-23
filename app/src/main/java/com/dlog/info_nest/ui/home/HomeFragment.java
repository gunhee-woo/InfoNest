package com.dlog.info_nest.ui.home;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.icu.text.Edits;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.dlog.info_nest.BasicApp;
import com.dlog.info_nest.MainActivity;
import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.HomeFragmentBinding;
import com.dlog.info_nest.db.AppDatabase;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.ui.PopupActivity;
import com.dlog.info_nest.utilities.Code;
import com.dlog.info_nest.utilities.Domparser;
import com.dlog.info_nest.utilities.HtmlWriter;
import com.dlog.info_nest.utilities.UrlCrawling;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.moxun.tagcloudlib.view.TagCloudView;

import net.alhazmy13.wordcloud.WordCloud;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dlog.info_nest.utilities.AppExecutorsHelperKt.runOnDiskIO;
import static com.dlog.info_nest.utilities.AppExecutorsHelperKt.runOnMain;

public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";

    private HomeFragmentBinding mHomeFragmentBinding;
    private TextTagsAdapter mTextTagsAdapter;
    private HomeViewModel mHomeViewModel;

    private HashMap<String, Integer> hashMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mHomeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false);
        mTextTagsAdapter = new TextTagsAdapter();
        mHomeFragmentBinding.homeWordcloudView.setAdapter(mTextTagsAdapter);
        //클립보드에 url이 저장되어있으면 북마크 추가창을 띄운다.
        String clipData = BasicApp.prefsManager.getClipboardDataPrefs();
        if(clipData.matches("^http(s)?:.*")){
            Intent intent = new Intent(getActivity(), PopupActivity.class);
            intent.putExtra("url", clipData);
            Domparser dp = new Domparser(clipData);
            final String[] title = new String[1];
            try {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            title[0] = dp.getTitle();
                        } catch (IOException e) {
                            //do nothing
                        }
                    }
                });
                t.start();
                t.join();
                if(title[0] != null) {
                    intent.putExtra("title", title[0]);
                    intent.putExtra("activity", "webView");
                    startActivity(intent);
                }
            } catch (InterruptedException e) {
                //do nothing . 그냥 북마크 추가창 띄우지 않기
            }
            BasicApp.prefsManager.setClipboardDataPrefs("");

        }

        return mHomeFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        subscribeUi(mHomeViewModel.getmBookmarks());
    }

    private void subscribeUi(LiveData<List<BookmarkEntity>> liveData) {
        // Update the list when the data changes
        liveData.observe(getViewLifecycleOwner(), bookmarks -> {
            if (bookmarks != null) {
                mTextTagsAdapter.setTextTag(bookmarks);
                initNounsHashMap(bookmarks);
                set2DWordCloudView();
                setBarChart();
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            //            // sync.
            mHomeFragmentBinding.executePendingBindings();
        });
    }

    public void initNounsHashMap(List<BookmarkEntity> bookmarkEntities) {
        hashMap = new HashMap<>();
        ArrayList<String> nouns = new ArrayList<>();
        for(BookmarkEntity bookmarkEntity : bookmarkEntities) {
            nouns.addAll(Arrays.asList(bookmarkEntity.getmTags().split(" ")));
        }
        for(String noun : nouns) {
            if(hashMap.containsKey(noun)) {
                int count = hashMap.get(noun);
                hashMap.put(noun, count + 1);
            } else {
                hashMap.put(noun, 1);
            }
        }
    }

    public void set2DWordCloudView() {
        List<WordCloud> wordClouds = new ArrayList<>();
        for(HashMap.Entry<String, Integer> entry : hashMap.entrySet()) {
            WordCloud wordCloud = new WordCloud(entry.getKey(), entry.getValue());
            wordClouds.add(wordCloud);
        }
        mHomeFragmentBinding.home2dWorddcloudView.setDataSet(wordClouds);
        mHomeFragmentBinding.home2dWorddcloudView.setSize(300, 300);
        mHomeFragmentBinding.home2dWorddcloudView.setColors(ColorTemplate.MATERIAL_COLORS);
        mHomeFragmentBinding.home2dWorddcloudView.notifyDataSetChanged();
    }

    public void setBarChart() {
        HashMap<String, Integer> top10Nouns = new HashMap<>();
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

        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();

        for(Map.Entry<String, Integer> entry : top10Nouns.entrySet()) {
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));
        }

        Column column = cartesian.column(data);
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Top 10 KeyWords");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("KeyWord");
        cartesian.yAxis(0).title("Frequency");

        mHomeFragmentBinding.homeChartView.setChart(cartesian);

    }

    @Override
    public void onDestroyView() {
        mHomeFragmentBinding = null;
        mTextTagsAdapter = null;
        super.onDestroyView();
    }

}
