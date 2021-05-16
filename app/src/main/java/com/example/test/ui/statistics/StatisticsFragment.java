package com.example.test.ui.statistics;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.test.R;
import com.example.test.ui.statistics.statfragment.CountryFragment;
import com.example.test.ui.statistics.statfragment.DecadeFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatisticsFragment extends Fragment {
    private StatisticsViewModel statisticsViewModel;
    private ArrayList<String[]> movieListForStat;
    private TabLayout tabLayoutStats, tabCountry, tabDecade, tabViewedFilm;
    ViewPager viewPager;
    HashMap<String, Integer> movieDecadeStats, movieCountryStats;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();



public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        ArrayList<String> list = new ArrayList<>();
        movieListForStat = new ArrayList<>();
        FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("---------------------------------------------------");
        System.out.println(uid.getUid());
        System.out.println("---------------------------------------------------");
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        db.collection("users").document(uid.getUid()).collection("watched").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("please wait");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("DOCS", document.getId() + " => " + document.getString("imdbID"));
                    String imdb = document.getString("imdbID");
                    String year = document.getString("year");
                    String country = document.getString("country");

                    String[] l = new String[]{imdb, year, country};
                    String[] list1 = new String[]{imdb, year, country};
                    movieListForStat.add(list1);
                    list.add(imdb);


                    System.out.println(imdb);
                    System.out.println("list1  " + list1[0]);


                }
                System.out.println("---------------------------------------------------------------------");
                System.out.println(movieListForStat);
                System.out.println("taille: " + movieListForStat.size());
                System.out.println("taille2: " + list.size());
                System.out.println("taille: " + movieListForStat.size());
                System.out.println("taille2: " + list.size());
                System.out.println("WTF");
                movieDecadeStats = new HashMap<>();
                movieCountryStats = new HashMap<>();

                getStats();



                tabLayoutStats = root.findViewById(R.id.tabLayoutStats);
                tabCountry = root.findViewById(R.id.tabCountry);
                tabDecade = root.findViewById(R.id.tabDecade);
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

                progressDialog.hide();
                progressDialog.cancel();

            } else {
                Log.d("DOCS", "Error getting documents: ", task.getException());
            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
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