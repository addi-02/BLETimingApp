package com.example.thesis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private ArrayList<String> trackNames;
    private TrackAdapter.OnDeleteClickListener deleteClickListener;
    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public TrackAdapter(ArrayList<String> trackNames, TrackAdapter.OnDeleteClickListener listener) {
        this.trackNames = trackNames;
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public TrackAdapter.TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
        return new TrackAdapter.TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackAdapter.TrackViewHolder holder, int position) {
        holder.textAddress.setText(trackNames.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return trackNames.size();
    }

    class TrackViewHolder extends RecyclerView.ViewHolder{
        TextView textAddress;
        Button btnDelete;

        Button btnEdit;
        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            textAddress = itemView.findViewById(R.id.text_address);
            btnDelete = itemView.findViewById(R.id.button_delete);
            btnEdit = itemView.findViewById(R.id.button_edit);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deleteClickListener.onDelete(position);
                    }
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
