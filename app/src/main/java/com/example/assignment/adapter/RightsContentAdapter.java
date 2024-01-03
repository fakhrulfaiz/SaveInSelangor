package com.example.assignment.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.example.assignment.R;
import com.example.assignment.data.RightContentData;
import com.example.assignment.data.RightData;

import java.io.Serializable;
import java.util.List;

public class RightsContentAdapter extends ArrayAdapter<RightData> {

    private final List<RightContentData> rightDataList;

    public RightsContentAdapter(Context context, List<RightContentData> rightDataList) {
        super(context, 0);
        this.rightDataList = rightDataList;

    }

    @Override
    public int getCount() {
        return rightDataList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_rights_content, parent, false);
        }

        RightContentData data = rightDataList.get(position);

        // Bind data to the layout
        TextView p1TextView = convertView.findViewById(R.id.titleText);
        TextView numberTextView = convertView.findViewById(R.id.numberText);

        // Set the text to something related to the RightData

        p1TextView.setText(data.getContentTitle());
        String number = position + 1 + ".";
        numberTextView.setText(number);

        View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDisplayRightFragment(data, finalConvertView);
            }
        });
        return convertView;
    }
    private void openDisplayRightFragment(RightContentData data, View convertView) {
        if (getContext() instanceof AppCompatActivity) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", (Serializable) data);

            bundle.putString("dataType", "rightContentData"); // Add identifier

            Navigation.findNavController(convertView).navigate(R.id.displayListFragment, bundle);
        }
    }
}
