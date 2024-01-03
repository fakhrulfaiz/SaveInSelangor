package com.example.assignment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.assignment.adapter.NewsAdapter;
import com.example.assignment.helper.NewsApiHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment{

    private ListView listView;
    private NewsAdapter newsAdapter;

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        // Retrieve the news list from arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("newsList")) {
            List<NewsApiHelper.NewsItem> newsList = args.getParcelableArrayList("newsList");
            listView = view.findViewById(R.id.news_listview);
            newsAdapter = new NewsAdapter(requireContext(), newsList);
            listView.setAdapter(newsAdapter);
        }
//        // Fetch news data
//        NewsApiHelper newsApiHelper = new NewsApiHelper();
//        List<NewsApiHelper.NewsItem> newsList = newsApiHelper.fetchData();

//        listView = view.findViewById(R.id.news_listview);
//        newsAdapter = new NewsAdapter(requireContext(), newsList);
//        listView.setAdapter(newsAdapter);

        return view;
    }


}
