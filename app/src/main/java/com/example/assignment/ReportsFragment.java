package com.example.assignment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.assignment.adapter.CustomAdapter;
import com.example.assignment.data.CardViewData;
import com.example.assignment.data.ReportData;
import com.example.assignment.reader.ReportReader;
import com.google.android.material.transition.platform.MaterialFade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView callPolice,callAmbulance,callBomba;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReportsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportsFragment newInstance(String param1, String param2) {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.listview);
        List<ReportData> reportContacts = ReportReader.readReportContacts(requireContext());


        // Create a list of CardViewData instances with different content
        List<CardViewData> cardViewDataList = new ArrayList<>();

        for (ReportData data : reportContacts){
            cardViewDataList.add(new CardViewData(data.getCategory(), data.getOrganization(),data.getPhone() ,data.getEmail()));
        }
//        cardViewDataList.add(new CardViewData(getResources().getString(R.string.cardView), getResources().getString(R.string.cardViewdetails)));
//        cardViewDataList.add(new CardViewData(getResources().getString(R.string.textView), getResources().getString(R.string.textViewdetails)));
//        cardViewDataList.add(new CardViewData(getResources().getString(R.string.recyclerView), getResources().getString(R.string.recyclerViewdetails)));

//        CustomAdapter adapter = new CustomAdapter(this, R.layout.list_cardview, 3); // Display 3 items
        CustomAdapter<CardViewData> adapter = new CustomAdapter(requireContext(), cardViewDataList);
        listView.setAdapter(adapter);



    }
    public void expand(View view) {
        CardView cardView = (CardView) view; // Convert the view to a CardView
        TextView detailsText = cardView.findViewById(R.id.detail); // Find the TextView within the CardView
        LinearLayout layout = cardView.findViewById(R.id.layout); // Find the first LinearLayout within the CardView
        LinearLayout layout2 = cardView.findViewById(R.id.layout2); // Find the second LinearLayout within the CardView

        int v = (detailsText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        int v2 = (layout2.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        TransitionManager.beginDelayedTransition(layout, new MaterialFade());
        detailsText.setVisibility(v);
        layout2.setVisibility(v2);
    }
}