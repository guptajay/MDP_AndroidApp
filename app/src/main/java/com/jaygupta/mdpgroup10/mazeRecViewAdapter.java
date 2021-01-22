package com.jaygupta.mdpgroup10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class mazeRecViewAdapter extends RecyclerView.Adapter<mazeRecViewAdapter.ViewHolder> {

    private ArrayList<mazeCell> cells = new ArrayList<>();
    private Context context;

    public mazeRecViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maze_tile, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mazeCell.setText(cells.get(position).getCellName());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, cells.get(position).getCellName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cells.size();
    }

    public void setCells(ArrayList<mazeCell> cells) {
        this.cells = cells;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mazeCell;
        private RelativeLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mazeCell = itemView.findViewById(R.id.mazeCell);
            parent = itemView.findViewById(R.id.mazeCellLayout);
        }
    }
}
