package com.example.test.ui.statistics.statfragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DecadeFragment extends Fragment {
    private BarChart barchart;
    private HashMap<String, Integer> map;
    private ArrayList<String> decadeArray = new ArrayList<>();
    private ArrayList<BarEntry> noByDecadeArray = new ArrayList<>();

    public DecadeFragment() {
    }
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            map = (HashMap<String, Integer>) getArguments().getSerializable("hashmap");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_decade, container, false);

        barchart = root.findViewById(R.id.barChartDecade);
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
                noByDecadeArray.add(new BarEntry(entry.getValue(), i));
                decadeArray.add(entry.getKey());
                i++;
            }
            BarDataSet barDataSet = new BarDataSet(noByDecadeArray, "Film viewed");
            BarData data = new BarData(decadeArray, barDataSet);
            data.setValueTextSize(10);
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            XAxis xAxis = barchart.getXAxis();
            xAxis.setLabelsToSkip(0);

           // xAxis.setTextColor(Color.WHITE);
           // xAxis.setValueTextColors(Color.WHITE);
            //barchart.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            getActivity().runOnUiThread(()-> {
                barchart.setData(data);
                p.hide();
                p.cancel();
            });
        });
    }
}