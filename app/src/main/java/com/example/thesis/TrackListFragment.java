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
import java.util.Arrays;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrackListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrackListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TrackAdapter adapter;
    ArrayList<String> trackNameList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrackListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrackListFragment newInstance(String param1, String param2) {
        TrackListFragment fragment = new TrackListFragment();
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
        View v = inflater.inflate(R.layout.fragment_track_list, container, false);
        RecyclerView trackRecycler = v.findViewById(R.id.rv_tracks);
        loadTrackList();
        adapter = new TrackAdapter(trackNameList, position -> {
            trackNameList.remove(position);
            adapter.notifyItemRemoved(position);
        });

        trackRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        trackRecycler.setAdapter(adapter);
        Button addTrack = v.findViewById(R.id.button_add_track);
        addTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputName = (EditText)v.findViewById(R.id.edit_track_name);
                String track = inputName.getText().toString();
                trackNameList.add(track);
                adapter.notifyItemInserted(trackNameList.size()-1);
                inputName.setText("");
                saveTrackList(trackNameList);
            }
        });

        adapter.setOnItemClickListener(new TrackAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String currName = trackNameList.get(position);
                Fragment editFragment = TrackFragment.newInstance(new Track(currName, new ArrayList<>(), new ArrayList<>()));
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout3, editFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return v;
    }

    public void saveTrackList(ArrayList<String> newTracks) {
        SharedPreferences prefs = requireContext().getSharedPreferences("track_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("track_names", TextUtils.join(",", newTracks));
        editor.apply();
    }

    public void loadTrackList() {
        SharedPreferences prefs = requireContext().getSharedPreferences("track_prefs", Context.MODE_PRIVATE);
        String tracks = prefs.getString("track_names", "");

        trackNameList.addAll(Arrays.asList(tracks.split(",")));
    }
}