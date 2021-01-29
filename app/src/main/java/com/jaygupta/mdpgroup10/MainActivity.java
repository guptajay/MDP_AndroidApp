package com.jaygupta.mdpgroup10;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jaygupta.mdpgroup10.adapter.mazeRecViewAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mazeRecView;
    private CharSequence[] bluetoothDevices;
    ArrayList<mazeCell> mazeCells;
    mazeRecViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization of the Maze
        mazeRecView = findViewById(R.id.mazeRecView);
        mazeCells = new ArrayList<>();
        Util.initMaze(mazeCells);

        adapter = new mazeRecViewAdapter(this);
        adapter.setCells(mazeCells);

        mazeRecView.setAdapter(adapter);
        mazeRecView.setLayoutManager(new GridLayoutManager(this, 15));

        // Initialization of the Bot
        Util.initBot(mazeCells);
        // Initialization of the Goal Area
        Util.initGoal(mazeCells);
        adapter.notifyDataSetChanged();
    }

    public void moveForward(View view) {
        String mazeColor = "#FFCCCB";
        String botColor = "#FF726F";
        String heading = "#940008";
        int currentPosition = Util.getPositionFromCoordinate(Util.getStartPoint(), mazeCells);
        for(int i = 0; i <= 2; i++) {
            mazeCells.get(currentPosition + i).setBgColor(mazeColor);
            mazeCells.get(currentPosition - 45 + i).setBgColor(botColor);
            adapter.notifyItemChanged(currentPosition+i);
            adapter.notifyItemChanged(currentPosition - 45 + i);
        }
        mazeCells.get(currentPosition - 44).setBgColor(heading);
        mazeCells.get(currentPosition - 29).setBgColor(botColor);
        adapter.notifyItemChanged(currentPosition - 44);
        adapter.notifyItemChanged(currentPosition -29);
        Util.setStartPoint(mazeCells.get(currentPosition - 15).getCellName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        CharSequence[] infoItem = new CharSequence[]{"Fastest-Path Waypoint: [" + Util.getWayPoint() + "]", "Start Coordinate: [" + Util.getStartPoint() + "]"};
        switch (item.getItemId()) {
            case R.id.information:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Information");
                builder.setItems(infoItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                return true;


            case R.id.bluetooth:
                Intent intent = new Intent(this, BluetoothUserInterface.class);
                startActivity(intent);
// BT Dialog 
//             case R.id.bluetooth:
//                 bluetoothDevices = new CharSequence[]{"Device 1 ID", "Device 2 ID", "Device 3 ID"};
//                 MaterialAlertDialogBuilder bluetooth_builder = new MaterialAlertDialogBuilder(this);
//                 bluetooth_builder.setTitle("Connect to a Bluetooth Device");
//                 bluetooth_builder.setSingleChoiceItems(bluetoothDevices, -1, new DialogInterface.OnClickListener() {
//                     @Override
//                     public void onClick(DialogInterface dialog, int which) {
//                         dialog.dismiss();
//                         Snackbar.make(getWindow().getDecorView().findViewById(R.id.mainContainer), "Bluetooth device " + bluetoothDevices[which] + " connected", Snackbar.LENGTH_LONG).show();
//                     }
//                 });
//                 bluetooth_builder.show();
//                 return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}