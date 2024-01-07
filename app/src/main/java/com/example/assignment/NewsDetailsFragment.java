package com.example.assignment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.webkit.WebViewClientCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsDetailsFragment extends Fragment {

    private static final String ARG_URL = "url";

    private String newsUrl;

    public NewsDetailsFragment() {
        // Required empty public constructor
    }

    public static NewsDetailsFragment newInstance(String url) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            newsUrl = getArguments().getString(ARG_URL);
            Log.d("NewsDetailsFragment", "onCreate: " + newsUrl);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_details, container, false);

        WebView webView = view.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
        webView.setWebViewClient(new WebViewClientCompat(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Handle URL loading here
                Log.d("NewsDetailsFragment", "Loading URL: " + request.getUrl().toString());
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        webView.loadUrl(newsUrl);


        return view;
    }
}
