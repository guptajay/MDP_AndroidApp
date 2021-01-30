package com.jaygupta.mdpgroup10.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.jaygupta.mdpgroup10.R;
import com.jaygupta.mdpgroup10.Util;
import com.jaygupta.mdpgroup10.mazeCell;

import java.util.ArrayList;

public class mazeRecViewAdapter extends RecyclerView.Adapter<mazeRecViewAdapter.ViewHolder> {

    private ArrayList<mazeCell> cells;
    private Context context;
    private CharSequence[] selectItem = new CharSequence[]{"Fastest-Path Waypoint", "Start Coordinate"};

    public mazeRecViewAdapter(Context context, ArrayList<mazeCell> cells) {
        this.context = context;
        this.cells = cells;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maze_tile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String s = String.valueOf(position);
        //holder.mazeCell.setText(s);
        holder.mazeCell.setText(cells.get(position).getCellName());
        holder.mazeCellItem.setBackgroundResource(cells.get(position).getBgColor());

        holder.parent.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle("Set selected coordinate [" + cells.get(position).getCellName() + "] as");

            builder.setSingleChoiceItems(selectItem, -1, (dialog, which) -> {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        Util.setWayPoint(cells.get(position).getCellName());
                        Snackbar.make(holder.parent, "Coordinate [" + Util.getWayPoint() + "] set as " + selectItem[which], Snackbar.LENGTH_LONG).show();
                        break;
                    case 1:
                        int currentPosition = Util.getPositionFromCoordinate(Util.getStartPoint(), cells);

                        // Remove current position & add new position
                        for (int i = 0; i <= 2; i++) {
                            cells.get(currentPosition + i).setBgColor(R.color.maze);
                            cells.get(position + i).setBgColor(R.color.bot);
                            notifyItemChanged(currentPosition + i);
                            notifyItemChanged(position + i);
                        }

                        for (int i = 15; i >= 13; i--) {
                            cells.get(currentPosition - i).setBgColor(R.color.maze);
                            cells.get(position - i).setBgColor(R.color.bot);
                            notifyItemChanged(currentPosition - i);
                            notifyItemChanged(position - i);
                        }

                        for (int i = 30; i >= 28; i--) {
                            cells.get(currentPosition - i).setBgColor(R.color.maze);
                            cells.get(position - i).setBgColor(R.color.bot);
                            notifyItemChanged(currentPosition - i);
                            notifyItemChanged(position - i);
                        }
                        Util.setStartPoint(cells.get(position).getCellName());
                        cells.get(position - 29).setBgColor(R.color.heading);
                        Util.setHeading("forward");
                        Snackbar.make(holder.parent, "Coordinate [" + Util.getStartPoint() + "] set as " + selectItem[which], Snackbar.LENGTH_LONG).show();
                        break;
                }
            });
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return cells.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mazeCell;
        private CardView parent;
        private RelativeLayout mazeCellItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mazeCell = itemView.findViewById(R.id.mazeCell);
            parent = itemView.findViewById(R.id.mazeCellLayout);
            mazeCellItem = itemView.findViewById(R.id.mazeCellItem);
        }
    }
}
