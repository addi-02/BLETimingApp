package com.example.thesis;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StopwatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StopwatchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int currCheckpoint;
    private Timestamp startTime;
    private Timestamp finishTime;
    public StopwatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StopwatchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StopwatchFragment newInstance(String param1, String param2) {
        StopwatchFragment fragment = new StopwatchFragment();
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
        View v = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        TextView textStart = v.findViewById(R.id.text_start);
        TextView textFinish = v.findViewById(R.id.text_finish);
        TextView textTime = v.findViewById(R.id.text_time);
        Button btnTakeTime = v.findViewById(R.id.button_take_time);
        Button btnClear = v.findViewById(R.id.button_clear);

        btnTakeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currCheckpoint%2 == 0) {
                    startTime = new Timestamp(System.currentTimeMillis());
                    textStart.setText(startTime.toString());
                } else {
                    finishTime = new Timestamp(System.currentTimeMillis());
                    textFinish.setText(finishTime.toString());
                    long elapsedTime = finishTime.getTime() - startTime.getTime();
                    long minute = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
                    long second = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) - TimeUnit.MINUTES.toSeconds(minute);
                    long millisecond = elapsedTime % 1000;

                    String formattedTime = String.format("%d:%02d.%03d", minute, second, millisecond);
                    textTime.setText(formattedTime);
                }
                currCheckpoint++;
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textStart.setText("");
                textFinish.setText("");
                currCheckpoint = 0;
            }
        });
        return v;
    }
}