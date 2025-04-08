package com.example.thesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Track {

    private Map<String, Map<String, ArrayList<MacAddress>>> track = new HashMap<>();


    public Track(String name, ArrayList<MacAddress> start, ArrayList<MacAddress> finish) {
        Map<String, ArrayList<MacAddress>> trackBeacons = new HashMap<>();
        trackBeacons.put("start", start);
        trackBeacons.put("finish", finish);
        this.track.put(name, trackBeacons);
    }

    public Map<String, Map<String, ArrayList<MacAddress>>> getTrack() {
        return this.track;
    }
}
