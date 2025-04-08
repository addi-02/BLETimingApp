package com.example.thesis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TracksFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<MacAddress> macListStart = new ArrayList<>();
    private ArrayList<MacAddress> macListFinish = new ArrayList<>();
    private MacAdapter adapterStart;
    private String trackName = "Test";
    private MacAdapter adapterFinish;
    public TracksFragment() {
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
    public static TracksFragment newInstance(String param1, String param2) {
        TracksFragment fragment = new TracksFragment();
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
        View v = inflater.inflate(R.layout.fragment_tracks, container, false);
        RecyclerView macAddressesStart = v.findViewById(R.id.rv_mac_addresses_start);
        MacAddress mcStart = new MacAddress("hejsan");
        macListStart = loadMacListStart();
        macListFinish = loadMacListFinish();
        adapterStart = new MacAdapter(macListStart, position -> {
            macListStart.remove(position);
            adapterStart.notifyItemRemoved(position);
            saveMacListStart();
        });

        macAddressesStart.setLayoutManager(new LinearLayoutManager(getContext()));
        macAddressesStart.setAdapter(adapterStart);

        RecyclerView macAddressesFinish = v.findViewById(R.id.rv_mac_addresses_finish);
        MacAddress mcFinish = new MacAddress("hejdå");
        adapterFinish = new MacAdapter(macListFinish, position -> {
            macListFinish.remove(position);
            adapterFinish.notifyItemRemoved(position);
            saveMacListFinish();
        });

        Button btnStartAddress = v.findViewById(R.id.button_add_start_address);
        btnStartAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = (EditText)v.findViewById(R.id.edit_start_address);
                String address = name.getText().toString();
                macListStart.add(new MacAddress(address));
                adapterStart.notifyItemInserted(macListStart.size()-1);
                saveMacListStart();
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
                saveMacListFinish();
                name.setText("");
            }
        });

        macAddressesFinish.setLayoutManager(new LinearLayoutManager(getContext()));
        macAddressesFinish.setAdapter(adapterFinish);

        return v;
    }

    private void saveMacListStart() {
        SharedPreferences prefs = requireContext().getSharedPreferences("mac_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        ArrayList<String> macs = new ArrayList<>();
        for (MacAddress item : macListStart) {
            macs.add(item.getAddress());
        }
        editor.putString(trackName+"_start", TextUtils.join(",", macs));
        editor.apply();
    }

    private void saveMacListFinish() {
        SharedPreferences prefs = requireContext().getSharedPreferences("mac_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        ArrayList<String> macs = new ArrayList<>();
        for (MacAddress item : macListFinish) {
            macs.add(item.getAddress());
        }
        editor.putString(trackName+"_finish", TextUtils.join(",", macs));
        editor.apply();
    }

    private ArrayList<MacAddress> loadMacListStart() {
        SharedPreferences prefs = requireContext().getSharedPreferences("mac_prefs", Context.MODE_PRIVATE);
        String saved = prefs.getString(trackName+"_start", "");
        ArrayList<MacAddress> list = new ArrayList<>();
        if (!saved.isEmpty()) {
            for (String mac : saved.split(",")) {
                list.add(new MacAddress(mac));
            }
        }
        return list;
    }

    private ArrayList<MacAddress> loadMacListFinish() {
        SharedPreferences prefs = requireContext().getSharedPreferences("mac_prefs", Context.MODE_PRIVATE);
        String saved = prefs.getString(trackName+"_finish", "");
        ArrayList<MacAddress> list = new ArrayList<>();
        if (!saved.isEmpty()) {
            for (String mac : saved.split(",")) {
                list.add(new MacAddress(mac));
            }
        }
        return list;
    }
}