package com.example.thesis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MacAdapter extends RecyclerView.Adapter<MacAdapter.MacViewHolder> {

    private ArrayList<MacAddress> macList;
    private OnDeleteClickListener deleteClickListener;
    public interface OnDeleteClickListener {
        void onDelete(int position);
    }
    public MacAdapter(ArrayList<MacAddress> macList, OnDeleteClickListener listener) {
        this.macList = macList;
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public MacViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mac_address, parent, false);
        return new MacViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MacViewHolder holder, int position) {
        holder.textAddress.setText(macList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return macList.size();
    }

    class MacViewHolder extends RecyclerView.ViewHolder{
        TextView textAddress;
        Button btnDelete;
        public MacViewHolder(@NonNull View itemView) {
            super(itemView);
            textAddress = itemView.findViewById(R.id.text_address);
            btnDelete = itemView.findViewById(R.id.button_delete);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                int position = getAdapterPosition();
                @Override
                public void onClick(View view) {
                    deleteClickListener.onDelete(position);
                }
            });
        }
    }
}
