package com.culture.inha.cultureline.Home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.culture.inha.cultureline.Api.ApiInterface;
import com.culture.inha.cultureline.BaseActivity;
import com.culture.inha.cultureline.DataSet.Categories;
import com.culture.inha.cultureline.DataSet.QuestionSet;
import com.culture.inha.cultureline.GlobalDataSet;
import com.culture.inha.cultureline.Lib.ActivityResultEvent;
import com.culture.inha.cultureline.Lib.DecodeBitMap;
import com.culture.inha.cultureline.Lib.EventBus;
import com.culture.inha.cultureline.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.culture.inha.cultureline.Api.ApiInterface.baseUrl;
import static com.culture.inha.cultureline.Home.MainBoardFragment.RC_POST_QUESTION;

/**
 * Created by Jaeseok on 2018-07-08.
 */

public class HomeActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupToolbar();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupToolbar() {
//        layoutSearch = findViewById(R.id.layout_Search);
//        layoutSearch.inflateMenu(R.menu.menu_search);
//        layoutSearch.getMenu().findItem(R.id.action_search).setIcon(
//                new BitmapDrawable(
//                        DecodeBitMap.decodeSampledBitmapFromResource(getResources(), R.drawable.search, 180, 160)));
//        SearchView mSearchView = (SearchView) layoutSearch.getMenu().findItem(R.id.action_search).getActionView();

        AppCompatImageView imgLogo = findViewById(R.id.imgLogo);
        imgLogo.setImageBitmap(
                DecodeBitMap.decodeSampledBitmapFromResource(getResources(), R.drawable.title_logo, 416, 160));
        AppCompatImageView imgSearch = findViewById(R.id.imgSearch);
        imgSearch.setImageBitmap(DecodeBitMap.decodeSampledBitmapFromResource(getResources(), R.drawable.search, 180, 160));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            EventBus.getInstance().post(ActivityResultEvent.create(requestCode, resultCode, data));
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return MainBoardFragment.newInstance(position);
            } else if (position == 1) {
                return FreeBoardFragment.newInstance(position);
            } else {
                return ProfileFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "질문 답변";
                case 1:
                    return "자유게시판";
                case 2:
                    return "프로필";
            }
            return null;
        }
    }
}
