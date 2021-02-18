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
import com.jaygupta.mdpgroup10.bluetooth_services.BluetoothConnectionService;
import com.jaygupta.mdpgroup10.mazeCell;
import com.jaygupta.mdpgroup10.utils.Constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class mazeRecViewAdapter extends RecyclerView.Adapter<mazeRecViewAdapter.ViewHolder> {

    private ArrayList<mazeCell> cells;
    private Context context;
    private final Charset charset = StandardCharsets.UTF_8;
    private byte[] byteArr;
    private CharSequence[] selectItem = new CharSequence[]{"Fastest-Path Waypoint", "Start Coordinate"};
    private BluetoothConnectionService bluetoothConnection;

    public mazeRecViewAdapter(Context context, ArrayList<mazeCell> cells) {
        this.context = context;
        this.cells = cells;
        bluetoothConnection = new BluetoothConnectionService(context);
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
        holder.mazeCell.setText(s);
        //holder.mazeCell.setText(cells.get(position).getDisplayName());
        holder.mazeCellItem.setBackgroundResource(cells.get(position).getBgColor());
        holder.mazeCell.setTextColor(context.getResources().getColor(cells.get(position).getTextColor()));
    }

    @Override
    public int getItemCount() {
        return cells.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mazeCell;
        private CardView parent;
        private RelativeLayout mazeCellItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mazeCell = itemView.findViewById(R.id.mazeCell);
            parent = itemView.findViewById(R.id.mazeCellLayout);
            mazeCellItem = itemView.findViewById(R.id.mazeCellItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle("Set selected coordinate [" + cells.get(pos).getCellName() + "] as");

            builder.setSingleChoiceItems(selectItem, -1, (dialog, which) -> {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        String s = context.getResources().getString(R.string.bluetooth_waypoint) + "FPW (" + cells.get(pos).getCellName() + ")";
                        byteArr = s.getBytes(charset);
                        if (bluetoothConnection.getBluetoothConnectionStatus()) {
                            bluetoothConnection.write(byteArr);
                            Util.setWayPoint(cells.get(pos).getCellName());
                            Snackbar.make(v, "Coordinate [" + Util.getWayPoint() + "] set as " + selectItem[which], Snackbar.LENGTH_LONG).show();
                        } else
                            Snackbar.make(v, Constants.BLUETOOTH_NOT_CONNECTED, Snackbar.LENGTH_SHORT).show();
                        break;
                    case 1:
                        String s1 = context.getResources().getString(R.string.bluetooth_startpoint) + "SP (" + cells.get(pos).getCellName() + ")";
                        byteArr = s1.getBytes(charset);
                        if (bluetoothConnection.getBluetoothConnectionStatus()) {
                            bluetoothConnection.write(byteArr);
                            int currentPosition = Util.getPositionFromCoordinate(Util.getStartPoint(), cells);
                            // Remove current position & add new position
                            for (int i = 0; i <= 2; i++) {
                                cells.get(currentPosition + i).setBgColor(R.color.maze);
                                cells.get(pos + i).setBgColor(R.color.bot);
                                notifyItemChanged(currentPosition + i);
                                notifyItemChanged(pos + i);
                            }

                            for (int i = 15; i >= 13; i--) {
                                cells.get(currentPosition - i).setBgColor(R.color.maze);
                                cells.get(pos - i).setBgColor(R.color.bot);
                                notifyItemChanged(currentPosition - i);
                                notifyItemChanged(pos - i);
                            }

                            for (int i = 30; i >= 28; i--) {
                                cells.get(currentPosition - i).setBgColor(R.color.maze);
                                cells.get(pos - i).setBgColor(R.color.bot);
                                notifyItemChanged(currentPosition - i);
                                notifyItemChanged(pos - i);
                            }
                            Util.setStartPoint(cells.get(pos).getCellName());
                            cells.get(pos - 29).setBgColor(R.color.heading);
                            Util.setHeading("forward");
                            Snackbar.make(v, "Coordinate [" + Util.getStartPoint() + "] set as " + selectItem[which], Snackbar.LENGTH_LONG).show();
                        } else
                            Snackbar.make(v, Constants.BLUETOOTH_NOT_CONNECTED, Snackbar.LENGTH_SHORT).show();
                        break;
                }
            });
            builder.show();
        }
    }
}
