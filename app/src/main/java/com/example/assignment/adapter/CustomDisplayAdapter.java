package com.example.assignment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.assignment.R;

import java.util.List;

public class CustomDisplayAdapter extends ArrayAdapter<String> {

    private final List<String> p1List;
    private final List<String> detailsList;

    public CustomDisplayAdapter(Context context, List<String> p1List, List<String> detailsList) {
        super(context, 0, p1List);
        this.p1List = p1List;
        this.detailsList = detailsList;
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
        String number = position + 1 + ".";
        numberTextView.setText(number);

        p1TextView.setText(p1Item);
        detailTextView.setText(detailsItem);

        return convertView;
    }
}
