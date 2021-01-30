package com.jaygupta.mdpgroup10;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jaygupta.mdpgroup10.adapter.mazeRecViewAdapter;

import java.util.ArrayList;
import com.jaygupta.mdpgroup10.utils.Constants;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mazeRecView;
    private CharSequence[] bluetoothDevices;
    private String connStatus;
    Handler reconnectionHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants CONSTANTS;

        // Initialization of the Maze
        mazeRecView = findViewById(R.id.mazeRecView);
        ArrayList<mazeCell> mazeCells = new ArrayList<>();
        Util.initMaze(mazeCells);

        mazeRecViewAdapter adapter = new mazeRecViewAdapter(this);
        adapter.setCells(mazeCells);

        mazeRecView.setAdapter(adapter);
        mazeRecView.setLayoutManager(new GridLayoutManager(this, 15));

        // Initialization of the Bot
        Util.initBot(mazeCells);
        // Initialization of the Goal Area
        Util.initGoal(mazeCells);
        adapter.notifyDataSetChanged();



    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetoothStatus();

    }

    private void checkBluetoothStatus() {
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            connStatus= Constants.BLUETOOTH_DISABLED;
        }
        if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
            if(!BluetoothConnectionService.BluetoothConnectionStatus)
                connStatus=Constants.BLUETOOTH_DISCONNECTED;
            else{
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
                if (sharedPreferences.contains("connStatus"))
                    connStatus = sharedPreferences.getString("connStatus", "");
            }
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.bluetooth_status);
        item.setTitle(this.connStatus);
        return super.onPrepareOptionsMenu(menu);
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
                Intent intent = new Intent(this, BluetoothUI.class);
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