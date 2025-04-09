package com.example.thesis;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class TrackManager {
    private ArrayList<Track> tracks;
    private Context context;

    public TrackManager(Context context) {
        this.tracks = new ArrayList<>();
        this.context = context;
        loadTracks();
    }

    public ArrayList<Track> getTracks() {
        return this.tracks;
    }

    public void loadTracks(){
        SharedPreferences prefs = context.getSharedPreferences("track_prefs", Context.MODE_PRIVATE);
        String tracks = prefs.getString("track_names", "");
        for(String name : Arrays.asList(tracks.split(","))) {
            this.tracks.add(new Track(name, new ArrayList<>(), new ArrayList<>()));
        }
    }
}
