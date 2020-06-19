package com.dlog.info_nest.ui.home;

import android.icu.text.Edits;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.dlog.info_nest.MainActivity;
import com.dlog.info_nest.R;
import com.dlog.info_nest.databinding.HomeFragmentBinding;
import com.dlog.info_nest.db.AppDatabase;
import com.dlog.info_nest.db.entity.BookmarkEntity;
import com.dlog.info_nest.utilities.UrlCrawling;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.moxun.tagcloudlib.view.TagCloudView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mHomeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false);
        mTextTagsAdapter = new TextTagsAdapter();
        mHomeFragmentBinding.homeWordcloudView.setAdapter(mTextTagsAdapter);
        return mHomeFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        subscribeUi(mHomeViewModel.getmBookmarks());

        ArrayList NoOfEmp = new ArrayList();

        NoOfEmp.add(new BarEntry(945f, 0));
        NoOfEmp.add(new BarEntry(1040f, 1));
        NoOfEmp.add(new BarEntry(1133f, 2));
        NoOfEmp.add(new BarEntry(1240f, 3));
        NoOfEmp.add(new BarEntry(1369f, 4));
        NoOfEmp.add(new BarEntry(1487f, 5));
        NoOfEmp.add(new BarEntry(1501f, 6));
        NoOfEmp.add(new BarEntry(1645f, 7));
        NoOfEmp.add(new BarEntry(1578f, 8));
        NoOfEmp.add(new BarEntry(1695f, 9));

        BarDataSet bardataset = new BarDataSet(NoOfEmp, "word");

        mHomeFragmentBinding.homeBarchartView.animateY(5000);
        BarData data = new BarData(bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        mHomeFragmentBinding.homeBarchartView.setData(data);
    }

    private void subscribeUi(LiveData<List<BookmarkEntity>> liveData) {
        // Update the list when the data changes
        liveData.observe(getViewLifecycleOwner(), bookmarks -> {
            if (bookmarks != null) {
                mTextTagsAdapter.setTextTag(bookmarks);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            //            // sync.
            mHomeFragmentBinding.executePendingBindings();
        });
    }

    @Override
    public void onDestroyView() {
        mHomeFragmentBinding = null;
        mTextTagsAdapter = null;
        super.onDestroyView();
    }

}
