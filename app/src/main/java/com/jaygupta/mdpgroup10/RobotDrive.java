package com.jaygupta.mdpgroup10;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.jaygupta.mdpgroup10.adapter.mazeRecViewAdapter;
import com.jaygupta.mdpgroup10.bluetooth_services.BluetoothConnectionService;
import com.jaygupta.mdpgroup10.utils.Constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RobotDrive {

    private int currentPosition;
    private ArrayList<mazeCell> mazeCells;
    private mazeRecViewAdapter adapter;
    private byte[] byteArr;
    private Context context;
    private final Charset charset = StandardCharsets.UTF_8;
    private BluetoothConnectionService mBluetoothConnection;

    public RobotDrive(ArrayList<mazeCell> mazeCells, mazeRecViewAdapter adapter, Context context, BluetoothConnectionService mBluetoothConnection) {
        this.mazeCells = mazeCells;
        this.adapter = adapter;
        this.context = context;
        this.mBluetoothConnection = mBluetoothConnection;
    }

    public void setCurrentPosition() {
        this.currentPosition = Util.getPositionFromCoordinate(Util.getStartPoint(), mazeCells);
    }

    public void moveBotForward(View view) {
        byteArr = context.getResources().getString(R.string.bluetooth_move_forward).getBytes(charset);
        if (mBluetoothConnection.getBluetoothConnectionStatus()) {
            mBluetoothConnection.write(byteArr);

            setCurrentPosition();

            if (Util.getHeading().equals("forward")) {
                for (int i = 0; i <= 2; i++) {
                    mazeCells.get(currentPosition + i).setBgColor(R.color.maze);
                    mazeCells.get(currentPosition - 45 + i).setBgColor(R.color.bot);
                    adapter.notifyItemChanged(currentPosition + i);
                    adapter.notifyItemChanged(currentPosition - 45 + i);
                }

                mazeCells.get(currentPosition - 44).setBgColor(R.color.heading);
                mazeCells.get(currentPosition - 29).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition - 44);
                adapter.notifyItemChanged(currentPosition - 29);
                Util.setStartPoint(mazeCells.get(currentPosition - 15).getCellName());

            } else if (Util.getHeading().equals("left")) {

                mazeCells.get(currentPosition + 2).setBgColor(R.color.maze);
                mazeCells.get(currentPosition - 13).setBgColor(R.color.maze);
                mazeCells.get(currentPosition - 28).setBgColor(R.color.maze);
                adapter.notifyItemChanged(currentPosition + 2);
                adapter.notifyItemChanged(currentPosition - 13);
                adapter.notifyItemChanged(currentPosition - 28);

                mazeCells.get(currentPosition - 16).setBgColor(R.color.heading);
                adapter.notifyItemChanged(currentPosition - 16);

                mazeCells.get(currentPosition - 1).setBgColor(R.color.bot);
                mazeCells.get(currentPosition - 31).setBgColor(R.color.bot);
                mazeCells.get(currentPosition - 15).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition - 1);
                adapter.notifyItemChanged(currentPosition - 31);
                adapter.notifyItemChanged(currentPosition - 15);

                Util.setStartPoint(mazeCells.get(currentPosition - 1).getCellName());

            } else if (Util.getHeading().equals("back")) {

                for (int i = 30; i >= 28; i--) {
                    mazeCells.get(currentPosition - i).setBgColor(R.color.maze);
                    mazeCells.get(currentPosition - 13 + i).setBgColor(R.color.bot);
                    adapter.notifyItemChanged(currentPosition - i);
                    adapter.notifyItemChanged(currentPosition - 13 + i);
                }

                mazeCells.get(currentPosition + 16).setBgColor(R.color.heading);
                mazeCells.get(currentPosition + 1).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition + 16);
                adapter.notifyItemChanged(currentPosition + 1);
                Util.setStartPoint(mazeCells.get(currentPosition + 15).getCellName());

            } else if (Util.getHeading().equals("right")) {

                mazeCells.get(currentPosition + 3).setBgColor(R.color.bot);
                mazeCells.get(currentPosition - 13).setBgColor(R.color.bot);
                mazeCells.get(currentPosition - 27).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition + 3);
                adapter.notifyItemChanged(currentPosition - 13);
                adapter.notifyItemChanged(currentPosition - 27);

                mazeCells.get(currentPosition - 12).setBgColor(R.color.heading);
                adapter.notifyItemChanged(currentPosition - 12);

                mazeCells.get(currentPosition).setBgColor(R.color.maze);
                mazeCells.get(currentPosition - 30).setBgColor(R.color.maze);
                mazeCells.get(currentPosition - 15).setBgColor(R.color.maze);
                adapter.notifyItemChanged(currentPosition);
                adapter.notifyItemChanged(currentPosition - 30);
                adapter.notifyItemChanged(currentPosition - 15);

                Util.setStartPoint(mazeCells.get(currentPosition + 1).getCellName());
            }
        } else
            Snackbar.make(view, Constants.BLUETOOTH_NOT_CONNECTED, Snackbar.LENGTH_SHORT).show();
    }

    public void moveBotLeft(View view) {

        byteArr =context.getResources().getString(R.string.bluetooth_move_left).getBytes(charset);
        if (mBluetoothConnection.getBluetoothConnectionStatus()) {
            mBluetoothConnection.write(byteArr);

            setCurrentPosition();

            int currentPosition = Util.getPositionFromCoordinate(Util.getStartPoint(), mazeCells);
            if (Util.getHeading().equals("forward")) {
                mazeCells.get(currentPosition - 15).setBgColor(R.color.heading);
                mazeCells.get(currentPosition - 29).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition - 15);
                adapter.notifyItemChanged(currentPosition - 29);
                Util.setHeading("left");
            } else if (Util.getHeading().equals("left")) {
                mazeCells.get(currentPosition + 1).setBgColor(R.color.heading);
                mazeCells.get(currentPosition - 15).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition + 1);
                adapter.notifyItemChanged(currentPosition - 15);
                Util.setHeading("back");
            } else if (Util.getHeading().equals("back")) {
                mazeCells.get(currentPosition - 13).setBgColor(R.color.heading);
                mazeCells.get(currentPosition + 1).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition + 1);
                adapter.notifyItemChanged(currentPosition - 13);
                Util.setHeading("right");
            } else {
                mazeCells.get(currentPosition - 29).setBgColor(R.color.heading);
                mazeCells.get(currentPosition - 13).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition - 29);
                adapter.notifyItemChanged(currentPosition - 13);
                Util.setHeading("forward");
            }

            // Moved



        } else
            Snackbar.make(view, Constants.BLUETOOTH_NOT_CONNECTED, Snackbar.LENGTH_SHORT).show();
    }

    public void moveBotRight(View view) {

        byteArr = context.getResources().getString(R.string.bluetooth_move_right).getBytes(charset);
        if (mBluetoothConnection.getBluetoothConnectionStatus()) {
            mBluetoothConnection.write(byteArr);

            setCurrentPosition();

            int currentPosition = Util.getPositionFromCoordinate(Util.getStartPoint(), mazeCells);
            if (Util.getHeading().equals("forward")) {
                mazeCells.get(currentPosition - 13).setBgColor(R.color.heading);
                mazeCells.get(currentPosition - 29).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition - 13);
                adapter.notifyItemChanged(currentPosition - 29);
                Util.setHeading("right");
            } else if (Util.getHeading().equals("right")) {
                mazeCells.get(currentPosition + 1).setBgColor(R.color.heading);
                mazeCells.get(currentPosition - 13).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition + 1);
                adapter.notifyItemChanged(currentPosition - 13);
                Util.setHeading("back");
            } else if (Util.getHeading().equals("back")) {
                mazeCells.get(currentPosition - 15).setBgColor(R.color.heading);
                mazeCells.get(currentPosition + 1).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition + 1);
                adapter.notifyItemChanged(currentPosition - 15);
                Util.setHeading("left");
            } else {
                mazeCells.get(currentPosition - 29).setBgColor(R.color.heading);
                mazeCells.get(currentPosition - 15).setBgColor(R.color.bot);
                adapter.notifyItemChanged(currentPosition - 29);
                adapter.notifyItemChanged(currentPosition - 15);
                Util.setHeading("forward");
            }

        } else
            Snackbar.make(view, Constants.BLUETOOTH_NOT_CONNECTED, Snackbar.LENGTH_SHORT).show();
    }
}
