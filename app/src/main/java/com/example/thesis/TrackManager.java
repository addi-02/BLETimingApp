package com.example.thesis;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TrackManager {
    private ArrayList<Track> tracks;
    private Map<String, Map<String, ArrayList<Double>>> trackResults;
    private Map<String, Map<String, ArrayList<String>>> timestamps;
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

    public void initializeTracks() {
        Map<String, ArrayList<Double>> initialValues = new HashMap<>();
        initialValues.put("start", new ArrayList<Double>());
        initialValues.put("finish", new ArrayList<Double>());

        Map<String, ArrayList<String>> initialTimestamps = new HashMap<>();
        initialTimestamps.put("start", new ArrayList<String>());
        initialTimestamps.put("finish", new ArrayList<String>());
        for(Track track : tracks) {
            trackResults.put(track.getTrackName(), initialValues);
            timestamps.put(track.getTrackName(), initialTimestamps);
        }
    }

    public void addResult(Track track, String checkpoint, int result) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        trackResults.get(track.getTrackName()).get(checkpoint).add((double)result);
        timestamps.get(track.getTrackName()).get(checkpoint).add(timestamp.toString());
    }

    public ArrayList<Double> getTrackResult(Track track, String checkpoint) {
        return trackResults.get(track.getTrackName()).get(checkpoint);
    }
    public ArrayList<String> getTrackTimestamps(Track track, String checkpoint) {
        return timestamps.get(track.getTrackName()).get(checkpoint);
    }
}
