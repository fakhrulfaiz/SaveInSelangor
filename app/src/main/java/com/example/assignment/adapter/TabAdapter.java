package com.example.assignment.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.assignment.NewsFragment;
import com.example.assignment.helper.NewsApiHelper;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitles = new ArrayList<>();
    private List<NewsApiHelper.NewsItem> newsList; // Added variable to hold the news data

    public TabAdapter(FragmentManager fm, Lifecycle lifecycle) {
        super(fm, lifecycle);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitles.add(title);
    }
    public void setNewsList(List<NewsApiHelper.NewsItem> newsList) {
        this.newsList = newsList;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragmentList.get(position);

        // Pass the newsList to the NewsFragment if it is the NewsFragment
        if (fragment instanceof NewsFragment) {
            Bundle args = new Bundle();
            args.putParcelableArrayList("newsList", new ArrayList<>(newsList));
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }
}
