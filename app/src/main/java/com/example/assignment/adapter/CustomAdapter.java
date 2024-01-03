package com.example.assignment.adapter;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assignment.DisplayListFragment;
import com.example.assignment.MainActivity;
import com.example.assignment.data.Data;
import com.example.assignment.data.GuideData;
import com.example.assignment.data.RightContentData;
import com.example.assignment.data.RightData;
import com.example.assignment.helper.CallHelper;
import com.example.assignment.R;
import com.example.assignment.data.CardViewData;
import com.example.assignment.helper.EmailHelper;
import com.google.android.material.transition.platform.MaterialFade;

import java.io.Serializable;
import java.util.List;

public class CustomAdapter<T extends Data> extends BaseAdapter {
    private final Context context;
    private List<T> dataList;
    private int itemLayout; // The resource ID of your list_cardview.xml
    private int numItems; // The number of items you want to display


    public CustomAdapter(Context context, List<T> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public CustomAdapter(Context context, int itemLayout, int numItems) {
        this.context = context;
        this.itemLayout = itemLayout;
        this.numItems = numItems;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = null;
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_cardview, parent, false);
        }

        T item = dataList.get(position);

//        CardViewData cardViewData = (CardViewData) item;
//        TextView titleTextView = convertView.findViewById(R.id.title);
//        TextView detailsTextView = convertView.findViewById(R.id.detail);
//        TextView phoneBtn = convertView.findViewById(R.id.phone);
//        TextView emailBtn = convertView.findViewById(R.id.email);
//        LinearLayout layout2 = convertView.findViewById(R.id.layout2);
//        titleTextView.setText(cardViewData.getTitle() != null ? cardViewData.getTitle() : "");
//        detailsTextView.setText(cardViewData.getDetails() != null ? cardViewData.getDetails() : "");
//        phoneBtn.setText(cardViewData.getPhone() != null ? "Phone" : "");
//        emailBtn.setText(cardViewData.getEmail() != null ? "Email" : "");
//
//        phoneBtn.setOnClickListener(v -> CallHelper.callService(context, cardViewData.getPhone()));
//        emailBtn.setOnClickListener(v -> EmailHelper.sendEmail(context, cardViewData.getEmail(), "", ""));
//

        if (item instanceof CardViewData) {
            Log.i("CustomAdapter", "Panggil");
            bindCardViewData((CardViewData) item, convertView);
        } else if (item instanceof GuideData) {
            bindGuideData((GuideData) item, convertView);
        }else if (item instanceof RightData) {
            bindRightsData((RightData) item, convertView);
        }

        return convertView;
    }

    private void bindRightsData(RightData rightData, View convertView) {
        TextView titleTextView = convertView.findViewById(R.id.title);
        titleTextView.setText(rightData.getTitle() != null ? rightData.getTitle() : "");

        convertView.setOnClickListener(v ->
                openDisplayRightFragment(rightData,convertView));
    }

    private void bindCardViewData(CardViewData cardViewData, View convertView) {
        
        TextView titleTextView = convertView.findViewById(R.id.title);
        TextView detailsTextView = convertView.findViewById(R.id.detail);
        TextView phoneBtn = convertView.findViewById(R.id.phone);
        TextView emailBtn = convertView.findViewById(R.id.email);
        LinearLayout layout2 = convertView.findViewById(R.id.layout2);
        titleTextView.setText(cardViewData.getTitle() != null ? cardViewData.getTitle() : "");
        detailsTextView.setText(cardViewData.getDetails() != null ? cardViewData.getDetails() : "");
        phoneBtn.setText(cardViewData.getPhone() != null ? "Phone" : "");
        emailBtn.setText(cardViewData.getEmail() != null ? "Email" : "");

        phoneBtn.setOnClickListener(v -> CallHelper.callService(context, cardViewData.getPhone()));
        emailBtn.setOnClickListener(v -> EmailHelper.sendEmail(context, cardViewData.getEmail(), "", ""));

        convertView.setOnClickListener(v -> expand(convertView));
    }

    private void bindGuideData(GuideData guideData, View convertView) {
        TextView titleTextView = convertView.findViewById(R.id.title);
        titleTextView.setText(guideData.getTitle() != null ? guideData.getTitle() : "");

        convertView.setOnClickListener(v ->
                openDisplayGuideFragment(guideData,convertView));
    }
    private void openDisplayRightFragment(RightData rightData, View convertView) {
        if (context instanceof AppCompatActivity) {
            Bundle bundle = new Bundle();

            bundle.putSerializable("data", (Serializable) rightData);

            bundle.putString("dataType", "rightData"); // Add identifier

            Navigation.findNavController(convertView).navigate(R.id.displayListFragment, bundle);
        }
    }

    private void openDisplayGuideFragment(GuideData guideData, View convertView) {
        if (context instanceof AppCompatActivity) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", (Serializable) guideData);
            bundle.putString("dataType", "guideData"); // Add identifier

            Navigation.findNavController(convertView).navigate(R.id.displayListFragment, bundle);
        }
    }



    public void expand(View view) {
        CardView cardView = (CardView) view;
        TextView detailsText = cardView.findViewById(R.id.detail);
        LinearLayout layout = cardView.findViewById(R.id.layout);
        LinearLayout layout2 = cardView.findViewById(R.id.layout2);

        int v = (detailsText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        int v2 = (layout2.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        TransitionManager.beginDelayedTransition(layout, new MaterialFade());
        detailsText.setVisibility(v);
        layout2.setVisibility(v2);
    }
}
