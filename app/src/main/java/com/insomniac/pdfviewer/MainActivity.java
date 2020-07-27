package com.insomniac.pdfviewer;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.insomniac.pdfviewer.fragments.ActivityCaller;
import com.insomniac.pdfviewer.fragments.PDFFragment;
import com.insomniac.pdfviewer.fragments.RecentFragment;
import com.insomniac.pdfviewer.fragments.models.Recent;
import com.insomniac.pdfviewer.utils.RecentDatabaseHeper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCaller {


    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    private RecentFragment recentFragment = new RecentFragment();
    private PDFFragment pdFragment = new PDFFragment();

    RecentDatabaseHeper recentDatabaseHeper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recentDatabaseHeper = new RecentDatabaseHeper(this);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (0 == position) {
                    recentFragment.updateRecents();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public void inserInto(Recent recent) {
        recentDatabaseHeper.insertIntoResent(recent);
    }

    @Override
    public void inserInto(File recent) {
        recentDatabaseHeper.insertIntoResent(recent);
    }

    @Override
    public List<Recent> getAllRecent() {
        return recentDatabaseHeper.getAllRecent();
    }


    class PagerAdapter extends FragmentStatePagerAdapter {

        List<String> pagerAdapterList;

        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            pagerAdapterList = Arrays.asList(getString(R.string.recent), getString(R.string.all_pdf));
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return recentFragment;
            } else {
                return pdFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return pagerAdapterList.get(position);
        }
    }

}
