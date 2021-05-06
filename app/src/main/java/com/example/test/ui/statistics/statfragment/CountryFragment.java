package com.example.test.ui.statistics.statfragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CountryFragment extends Fragment {
    private PieChart pieChart;
    private HashMap<String, Integer> map;
    private ArrayList<String> countryArray = new ArrayList<>();
    private ArrayList<Entry> noByCountryArray = new ArrayList<>();
    private ArrayList<String[]> movieListForStat = new ArrayList();

    public CountryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d("MSG", "print1");
            map = (HashMap<String, Integer>) getArguments().getSerializable("hashmap");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_country, container, false);
        pieChart = root.findViewById(R.id.piechart);
        setData();
        return root;
    }

    private void setData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ProgressDialog p = new ProgressDialog(getActivity());
        p.setMessage("please wait");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();

        executor.execute(() -> {
            int i = 0;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                noByCountryArray.add(new BarEntry(entry.getValue(), i));
                countryArray.add(entry.getKey());
                i++;
            }
            PieDataSet barDataSet = new PieDataSet(noByCountryArray, "Film viewed");


            PieData data = new PieData(countryArray, barDataSet);
            data.setValueTextSize(10);
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            getActivity().runOnUiThread(()-> {
                pieChart.setData(data);
                p.hide();
                p.cancel();
            });
        });
    }
}