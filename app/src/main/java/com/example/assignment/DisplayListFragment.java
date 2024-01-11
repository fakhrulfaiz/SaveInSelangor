package com.example.assignment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.assignment.adapter.CustomDisplayAdapter;
import com.example.assignment.adapter.RightsContentAdapter;
import com.example.assignment.data.GuideData;
import com.example.assignment.data.RightContentData;
import com.example.assignment.data.RightData;

import org.w3c.dom.Text;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private GuideData guideData;
    private RightContentData rightContentData;
    private RightData rightData;

    public DisplayListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param guideData Parameter 1.
     * @return A new instance of fragment DisplayListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayListFragment newInstance(GuideData guideData) {
        DisplayListFragment fragment = new DisplayListFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("guideData", (Serializable) guideData);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Serializable data = getArguments().getSerializable("data");
            String dataType = getArguments().getString("dataType");

            if ("rightContentData".equals(dataType) && data instanceof RightContentData) {
                // Handle RightData
                rightContentData = (RightContentData) data;
            } else if ("guideData".equals(dataType) && data instanceof GuideData) {
                // Handle GuideData
                guideData = (GuideData) data;
            }else if ("rightData".equals(dataType) && data instanceof RightData) {
                // Handle GuideData
                rightData = (RightData) data;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = view.findViewById(R.id.textview);
        ListView listView = view.findViewById(R.id.listview);

        // Set the title to the TextView
        if (guideData != null) {
            textView.setText(guideData.getTitle());

            // Create the custom adapter and set it to the ListView
            CustomDisplayAdapter adapter = new CustomDisplayAdapter(requireContext(), guideData.getP1(), guideData.getDetails(), "guides" );
            listView.setAdapter(adapter);
        }
        if (rightContentData != null) {
            textView.setText(rightContentData.getContentTitle());


            // Create the custom adapter and set it to the ListView
            CustomDisplayAdapter adapter = new CustomDisplayAdapter(requireContext(), rightContentData.getP1(), rightContentData.getContentDescriptionList(), "rightsContent");
            Log.d("DisplayListFragment", "onViewCreated: " + rightContentData.getContentDescriptionList().toString());
            listView.setAdapter(adapter);
        } if (rightData != null) {
            textView.setText(rightData.getTitle());


            // Create the custom adapter and set it to the ListView
            RightsContentAdapter adapter = new RightsContentAdapter(requireContext(), rightData.getContentList());
            listView.setAdapter(adapter);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear the data to release memory
        clearData();
    }

    private void clearData() {
        // Clear the data variables to release memory
//        guideData = null;
//        rightContentData = null;
//        rightData = null;
    }
}