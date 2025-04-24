package com.example.thesis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsLabelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsLabelFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResultsLabelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultsLabelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultsLabelFragment newInstance(String param1, String param2) {
        ResultsLabelFragment fragment = new ResultsLabelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnNameEnteredListener {
        void onNameEntered(String label);
    }

    private OnNameEnteredListener listener;
    private String filterName;

    public ResultsLabelFragment(String filterName, OnNameEnteredListener listener) {
        this.filterName = filterName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        final EditText input = new EditText(requireContext());
        input.setHint("Enter label");

        builder.setTitle("Name this result")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String enteredName = input.getText().toString().trim();
                    if (!enteredName.isEmpty()) {
                        listener.onNameEntered(enteredName);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results_label, container, false);
    }
}