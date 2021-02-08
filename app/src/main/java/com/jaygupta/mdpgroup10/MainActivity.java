package com.jaygupta.mdpgroup10;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.jaygupta.mdpgroup10.adapter.mazeRecViewAdapter;
import com.jaygupta.mdpgroup10.bluetooth_services.BluetoothChatService;
import com.jaygupta.mdpgroup10.bluetooth_services.BluetoothChatUI;
import com.jaygupta.mdpgroup10.bluetooth_services.BluetoothConnectionService;
import com.jaygupta.mdpgroup10.bluetooth_services.BluetoothConnectionUI;
import com.jaygupta.mdpgroup10.utils.Constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mazeRecView;
    private CharSequence[] bluetoothDevices;
    ArrayList<mazeCell> mazeCells;
    mazeRecViewAdapter adapter;
    private String connStatus;
    Handler reconnectionHandler;
    private Dialog dialog;
    private TextInputLayout f1String;
    private TextInputLayout f2String;
    BluetoothConnectionService mBluetoothConnection;
    byte[] byteArr;
    Charset charset = StandardCharsets.UTF_8;
    private RobotDrive drive;
    private SwitchCompat manualSwitch;
    private Button refresh;
    private Button moveForward, moveRight, moveLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants CONSTANTS;

        startService(new Intent(MainActivity.this, BluetoothChatService.class));

        // Initialization of the Maze
        mazeRecView = findViewById(R.id.mazeRecView);
        mBluetoothConnection=new BluetoothConnectionService(MainActivity.this);

        IntentFilter bluetoothStateChangeFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(updateBluetoothStatus,bluetoothStateChangeFilter);


        LocalBroadcastManager.getInstance(this).registerReceiver(robotStatusUpdate, new IntentFilter("robotStatusUpdate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mazeUpdate, new IntentFilter("mazeUpdate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(botUpdate, new IntentFilter("botUpdate"));

        // mazeRecView.setHasFixedSize(true);
        mazeCells = new ArrayList<>();
        Util.initMaze(mazeCells);
        adapter = new mazeRecViewAdapter(this, mazeCells);

        mazeRecView.setAdapter(adapter);
        mazeRecView.setLayoutManager(new GridLayoutManager(this, 15));

        // Initialization of the Bot
        Util.initBot(mazeCells);

        // Initialization of the Goal Area
        Util.initGoal(mazeCells);
        adapter.notifyDataSetChanged();

        // Set Shared Preferences Strings Default
        if(PreferencesHelper.loadData(MainActivity.this, getResources().getString(R.string.f1_key)) == "Not Found") {
            PreferencesHelper.saveData(MainActivity.this, "F1 String", getResources().getString(R.string.f1_key));
        }

        if(PreferencesHelper.loadData(MainActivity.this, getResources().getString(R.string.f2_key)) == "Not Found") {
            PreferencesHelper.saveData(MainActivity.this, "F2 String", getResources().getString(R.string.f2_key));
        }

        refresh = findViewById(R.id.refresh);
        manualSwitch = findViewById(R.id.manulAutoControl);
        moveForward = findViewById(R.id.moveForwardBtn);
        moveLeft = findViewById(R.id.moveLeftBtn);
        moveRight = findViewById(R.id.moveRightBtn);

        if(!manualSwitch.isChecked()) {
            refresh.setEnabled(false);
        }

        manualSwitch.setOnClickListener(v -> {
            if(manualSwitch.isChecked())
            refresh.setEnabled(true);
            else refresh.setEnabled(false);
        });

        refresh.setOnClickListener(v -> {
            ArrayList<String> messageList = Util.getManualListItems();
            for(String message : messageList) {
                if(message.contains("mov")) {
                    Matcher action = Pattern.compile("\\(([^)]+)\\)").matcher(message);
                    while (action.find()) {
                        if (action.group(1).equals("forward"))
                            drive.moveBotForward(findViewById(android.R.id.content));
                        else if (action.group(1).equals("left"))
                            drive.moveBotLeft(findViewById(android.R.id.content));
                        else if (action.group(1).equals("right"))
                            drive.moveBotRight(findViewById(android.R.id.content));
                    }
                } else if (message.contains("obs")) {
                    Matcher loc = Pattern.compile("\\(([^)]+)\\)").matcher(message);
                    Matcher obsNum = Pattern.compile("\\[(.*?)\\]").matcher(message);
                    while (loc.find()) {
                        while (obsNum.find()) {
                            int pos = Util.setObstacle(mazeCells, loc.group(1), obsNum.group(1));
                            adapter.notifyItemChanged(pos);
                        }
                    }
                }
            }
            Util.clearAllManualMessages();
        });

        drive = new RobotDrive(mazeCells, adapter, this, mBluetoothConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connStatus = mBluetoothConnection.getBluetoothStatus();
        invalidateOptionsMenu();
    }

    private BroadcastReceiver updateBluetoothStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           onResume();
        }

    };


    public void moveForward(View view) {
        drive.moveBotForward(view);
    }

    public void moveLeft(View view) {
        drive.moveBotLeft(view);
    }

    public void moveRight(View view) {
        drive.moveBotRight(view);
    }

    public void setStrings(View view) {
        f1String = (TextInputLayout) dialog.findViewById(R.id.stringF1);
        f2String = (TextInputLayout) dialog.findViewById(R.id.stringF2);
        PreferencesHelper.saveData(this, f1String.getEditText().getText().toString(), getResources().getString(R.string.f1_key));
        PreferencesHelper.saveData(this, f2String.getEditText().getText().toString(), getResources().getString(R.string.f2_key));
        Snackbar.make(view , "F1/F2 strings updated successfully", Snackbar.LENGTH_LONG).show();
    }

    public void sendStringF1(View view) {
        byteArr = PreferencesHelper.loadData(this, getResources().getString(R.string.f1_key)).getBytes(charset);
        if(mBluetoothConnection.getBluetoothConnectionStatus()){
            mBluetoothConnection.write(byteArr);
            Snackbar.make(view, "String F1 Sent", Snackbar.LENGTH_SHORT).show();
        }
        else
            Snackbar.make(view, Constants.BLUETOOTH_NOT_CONNECTED, Snackbar.LENGTH_SHORT).show();
    }

    public void sendStringF2(View view) {
        byteArr = PreferencesHelper.loadData(this, getResources().getString(R.string.f2_key)).getBytes(charset);

        if(mBluetoothConnection.getBluetoothConnectionStatus()){
            mBluetoothConnection.write(byteArr);
            Snackbar.make(view, "String F2 Sent", Snackbar.LENGTH_SHORT).show();
        }
        else
            Snackbar.make(view, Constants.BLUETOOTH_NOT_CONNECTED, Snackbar.LENGTH_SHORT).show();
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
        if (item.getItemId() == R.id.information) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Information");
            builder.setItems(infoItem, (dialog, which) -> {

            });
            builder.show();
            return true;
        } else if (item.getItemId() == R.id.bluetooth) {
            Intent intent = new Intent(this, BluetoothConnectionUI.class);
            startActivity(intent);
            return true;

        } else if (item.getItemId() == R.id.bluetooth_chat){
            Intent intent = new Intent(this, BluetoothChatUI.class);
            startActivity(intent);
            return true;


        } else if (item.getItemId() == R.id.configureStrings) {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.configure_strings);
            dialog.setTitle("Configure Strings");

            f1String = (TextInputLayout) dialog.findViewById(R.id.stringF1);
            f1String.getEditText().setText(PreferencesHelper.loadData(this, getResources().getString(R.string.f1_key)));

            f2String = (TextInputLayout) dialog.findViewById(R.id.stringF2);
            f2String.getEditText().setText(PreferencesHelper.loadData(this, getResources().getString(R.string.f2_key)));

            dialog.show();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    public BroadcastReceiver robotStatusUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equalsIgnoreCase("robotStatusUpdate")) {
                String receivedMessage = intent.getStringExtra("receivedMessage");
                System.out.println(receivedMessage);

                Matcher status = Pattern.compile("\\(([^)]+)\\)").matcher(receivedMessage);
                while(status.find()) {
                    System.out.println(status.group(1));
                    Util.setStatus(MainActivity.this, status.group(1));
                }
            }

        }
    };

    public BroadcastReceiver mazeUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equalsIgnoreCase("mazeUpdate")) {
                manualSwitch = findViewById(R.id.manulAutoControl);
                if(!manualSwitch.isChecked()) {
                    Util.removeManualMessage();
                    String receivedMessage = intent.getStringExtra("receivedMessage");
                    Matcher loc = Pattern.compile("\\(([^)]+)\\)").matcher(receivedMessage);
                    Matcher obsNum = Pattern.compile("\\[(.*?)\\]").matcher(receivedMessage);
                    while (loc.find()) {
                        while (obsNum.find()) {
                            int pos = Util.setObstacle(mazeCells, loc.group(1), obsNum.group(1));
                            adapter.notifyItemChanged(pos);
                        }
                    }
                }
            }
        }
    };

    public BroadcastReceiver botUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equalsIgnoreCase("botUpdate")) {
                manualSwitch = findViewById(R.id.manulAutoControl);
                if(!manualSwitch.isChecked()) {
                    Util.removeManualMessage();
                    String receivedMessage = intent.getStringExtra("receivedMessage");
                    Matcher action = Pattern.compile("\\(([^)]+)\\)").matcher(receivedMessage);
                    while (action.find()) {
                        if(action.group(1).equals("forward"))
                            drive.moveBotForward(findViewById(android.R.id.content));
                        else if(action.group(1).equals("left"))
                            drive.moveBotLeft(findViewById(android.R.id.content));
                        else if(action.group(1).equals("right"))
                            drive.moveBotRight(findViewById(android.R.id.content));
                    }
                }
            }
        }
    };

//    @Override
//    protected void onPause() {
//        super.onPause();
//        try {
//            unregisterReceiver(updateBluetoothStatus);
//            LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(robotStatusUpdate);
//            LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mazeUpdate);
//        } catch(IllegalArgumentException e){
//            e.printStackTrace();
//        }
//    }

      
}
