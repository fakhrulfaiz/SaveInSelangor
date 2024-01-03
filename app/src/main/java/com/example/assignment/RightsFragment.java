package com.example.assignment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.assignment.adapter.CustomAdapter;
import com.example.assignment.data.GuideData;
import com.example.assignment.data.RightData;
import com.example.assignment.reader.GuideReader;
import com.example.assignment.reader.RightReader;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RightsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RightsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RightsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RightsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RightsFragment newInstance(String param1, String param2) {
        RightsFragment fragment = new RightsFragment();
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
        return inflater.inflate(R.layout.fragment_rights, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.listview);
        List<RightData> guideDataList = RightReader.readRightData(requireContext());


        CustomAdapter<RightData> adapter = new CustomAdapter<>(requireContext(), guideDataList);
        listView.setAdapter(adapter);



    }
}