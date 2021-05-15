package com.example.test.ui.statistics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.test.R;
import com.example.test.ui.statistics.statfragment.CountryFragment;
import com.example.test.ui.statistics.statfragment.DecadeFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatisticsFragment extends Fragment {
    private StatisticsViewModel statisticsViewModel;
    private ArrayList<String[]> movieListForStat = new ArrayList();
    private TabLayout tabLayoutStats, tabCountry, tabDecade, tabViewedFilm;
    ViewPager viewPager;
    HashMap<String, Integer> movieDecadeStats, movieCountryStats;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();



public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        ArrayList<String> list = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).collection("watched").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("DOCS", document.getId() + " => " + document.getString("imdbID"));
                    String imdb = document.getString("imdbID");
                    String year = document.getString("year");
                    String country = document.getString("country");

                    String[] l = new String[]{imdb, year, country};

                    movieListForStat.add(l);
                }
                System.out.println("---------------------------------------------------------------------");
                System.out.println(list);
            } else {
                Log.d("DOCS", "Error getting documents: ", task.getException());
            }
        });
        movieListForStat.add(new String[]{"tt0060196", "1926", "France, Spain, West Germany, USA"});
        movieListForStat.add(new String[]{"tt0060196", "1892", "Italy, Spain, West Germany, USA"});
        movieListForStat.add(new String[]{"tt0060196", "1936", "Russia, Spain, West Germany, USA"});
        movieListForStat.add(new String[]{"tt0060196", "1976", "Italy, Spain, USA"});
        movieListForStat.add(new String[]{"tt0060196", "1916", "Italy, West Germany, USA"});
        movieListForStat.add(new String[]{"tt0060196", "1906", "Italy, Spain"});
        movieListForStat.add(new String[]{"tt0060196", "1916", "Italy"});
        movieListForStat.add(new String[]{"tt0060196", "1986", "Italy, Spain, West Germany, USA"});

        movieDecadeStats = new HashMap<>();
        movieCountryStats = new HashMap<>();

        getStats();

        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        tabLayoutStats = root.findViewById(R.id.tabLayoutStats);
        tabCountry = root.findViewById(R.id.tabCountry);
        tabDecade = root.findViewById(R.id.tabDecade);
        tabViewedFilm = root.findViewById(R.id.tabViewedFilm);
        viewPager = root.findViewById(R.id.viewPager);

        setupViewPager(viewPager);

        tabLayoutStats.setupWithViewPager(viewPager);
        tabLayoutStats.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        return root;
    }

    private void getStats() {
        for (String[] ls : movieListForStat) {
            char[] decade = ls[1].toCharArray();
            decade[3] = '0';
            decade.toString();
            String d = new String(decade);
            if (movieDecadeStats.containsKey(d)) {
                movieDecadeStats.put(d, movieDecadeStats.get(d) + 1);
            } else movieDecadeStats.put(d, 1);
            String[] country = ls[2].split(",");
            for (String s : country) {
                if (movieCountryStats.containsKey(s.trim())) {
                    movieCountryStats.put(s.trim(), movieCountryStats.get(s.trim()) + 1);
                } else movieCountryStats.put(s.trim(), 1);
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        Adapter adapter = new Adapter(getChildFragmentManager());
        Fragment fragment1 = new CountryFragment();
        Fragment fragment2 = new DecadeFragment();

        bundle1.putSerializable("hashmap", movieCountryStats);
        bundle2.putSerializable("hashmap", movieDecadeStats);

        fragment1.setArguments(bundle1);
        fragment2.setArguments(bundle2);

        adapter.addFragment(fragment1, "Country");
        adapter.addFragment(fragment2, "Decade");

        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}