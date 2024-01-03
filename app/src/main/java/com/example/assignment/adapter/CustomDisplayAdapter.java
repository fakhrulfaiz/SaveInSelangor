package com.example.assignment.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.assignment.R;

import java.util.ArrayList;
import java.util.List;

public class CustomDisplayAdapter extends ArrayAdapter<String> {

    private final List<String> p1List;
    private final List<String> detailsList;
    private String fragmentType; // Member variable to store fragment type

    public CustomDisplayAdapter(Context context, List<String> p1List, List<String> detailsList, String fragmentType) {
        super(context, 0, p1List);
        this.p1List = p1List;
        this.detailsList = detailsList;
        this.fragmentType = fragmentType;
    }

    @Override
    public int getCount() {
        return p1List.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_display, parent, false);
        }

        // Get the current items from both lists
        String p1Item = p1List.get(position);
        String detailsItem = detailsList.get(position);

        // Bind data to the layout
        TextView p1TextView = convertView.findViewById(R.id.p1Text);
        TextView detailTextView = convertView.findViewById(R.id.detailText);
        TextView numberTextView = convertView.findViewById(R.id.numberText);


        p1TextView.setText(p1Item);
        detailTextView.setText(detailsItem);

        // Customize the view based on the fragment type
        if ("rightsContent".equals(fragmentType)) {

            numberTextView.setVisibility(View.GONE);

            // Assuming you want to add 16dp of padding to the start
            int paddingStartInDp = 8;
            int paddingStartInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, paddingStartInDp, getContext().getResources().getDisplayMetrics());

// Set padding to the start (left side) of the TextView
            detailTextView.setPadding(paddingStartInPx, 0, 0, 0);

        } else if ("guides".equals(fragmentType)) {
            String number = position + 1 + ".";
            numberTextView.setText(number);
            // Do something specific for the "guideData" fragment
        }

        return convertView;
    }
}


