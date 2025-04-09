package com.example.thesis;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrackFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_TRACK = "arg_track";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<MacAddress> macListStart = new ArrayList<>();
    private ArrayList<MacAddress> macListFinish = new ArrayList<>();
    private MacAdapter adapterStart;
    private String trackName = "Test";
    private Track track;
    private MacAdapter adapterFinish;
    public TrackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TracksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrackFragment newInstance(Track track) {
        TrackFragment fragment = new TrackFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRACK, track);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && track == null) {
            track = (Track) getArguments().getSerializable(ARG_TRACK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tracks, container, false);
        RecyclerView macAddressesStart = v.findViewById(R.id.rv_mac_addresses_start);
        track = new Track("addi", new ArrayList<>(), new ArrayList<>());
        track.setContext(requireContext());
        track.loadMacList();
        trackName = track.getTrackName();
        macListStart = track.getAddresses("start");
        macListFinish = track.getAddresses("finish");

        adapterStart = new MacAdapter(macListStart, position -> {
            macListStart.remove(position);
            adapterStart.notifyItemRemoved(position);
            track.saveMacListStart(macListStart);
        });

        macAddressesStart.setLayoutManager(new LinearLayoutManager(getContext()));
        macAddressesStart.setAdapter(adapterStart);

        RecyclerView macAddressesFinish = v.findViewById(R.id.rv_mac_addresses_finish);
        adapterFinish = new MacAdapter(macListFinish, position -> {
            macListFinish.remove(position);
            adapterFinish.notifyItemRemoved(position);
            track.saveMacListFinish(macListFinish);
        });

        macAddressesFinish.setLayoutManager(new LinearLayoutManager(getContext()));
        macAddressesFinish.setAdapter(adapterFinish);

        Button btnStartAddress = v.findViewById(R.id.button_add_start_address);
        btnStartAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = (EditText)v.findViewById(R.id.edit_start_address);
                String address = name.getText().toString();
                macListStart.add(new MacAddress(address));
                adapterStart.notifyItemInserted(macListStart.size()-1);
                track.saveMacListStart(macListStart);
                name.setText("");
            }
        });

        Button btnFinishAddress = v.findViewById(R.id.button_add_finish_address);
        btnFinishAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = (EditText)v.findViewById(R.id.edit_finish_address);
                String address = name.getText().toString();
                macListFinish.add(new MacAddress(address));
                adapterFinish.notifyItemInserted(macListFinish.size()-1);
                track.saveMacListFinish(macListFinish);
                name.setText("");
            }
        });



        return v;
    }


}