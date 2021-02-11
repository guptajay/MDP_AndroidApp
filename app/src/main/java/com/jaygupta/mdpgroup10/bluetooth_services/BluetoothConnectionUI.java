package com.jaygupta.mdpgroup10.bluetooth_services;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.snackbar.Snackbar;
import com.jaygupta.mdpgroup10.MainActivity;
import com.jaygupta.mdpgroup10.R;
import com.jaygupta.mdpgroup10.Util;
import com.jaygupta.mdpgroup10.utils.Constants;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnectionUI extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Bluetooth UI Activity";
    private String connStatus;
    BluetoothAdapter bluetoothAdapter;

    public ArrayList<BluetoothDevice> newBtDevices;
    public ArrayList<BluetoothDevice> pairedBtDevices;
    public DeviceListAdapter deviceListAdapter;
    public DeviceListAdapter pairedDeviceListAdapter;

    static Constants CONSTANTS;
    ListView otherDevicesListView;
    ListView pairedDevicesListView;
    Button connectBtn;
    Button searchBtn;



    AlertDialog alertDialog;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    BluetoothConnectionService mBluetoothConnection;
    private static final UUID btUUID = CONSTANTS.uuid;
    public static BluetoothDevice mBTDevice;

    boolean retryConnection = false;
    Handler reconnectionHandler = new Handler();

    Runnable reconnectionRunnable = new Runnable() {
        @Override
        public void run() {
            // Magic here
            try {
                if (BluetoothConnectionService.BluetoothConnectionStatus == false) {
                    startBTConnection(mBTDevice, btUUID);
                    //Toast.makeText(BluetoothConnectionUI.this,"Reconnection Success.", Toast.LENGTH_LONG).show();
                }
                reconnectionHandler.removeCallbacks(reconnectionRunnable);
                retryConnection = false;
                alertDialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(BluetoothConnectionUI.this,"Failed to reconnect, trying in 5 seconds.", Toast.LENGTH_SHORT).show();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_user_interface);

        initializeVariables();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Bluetooth Connection");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        IntentFilter bluetoothStateChangeFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(updateBluetoothStatus,bluetoothStateChangeFilter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        IntentFilter filter2 = new IntentFilter("ConnectionStatus");
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver5, filter2);


       // LocalBroadcastManager.getInstance(this).registerReceiver(messageReceived, new IntentFilter("incomingMessage"));




        otherDevicesListView.setOnItemClickListener((adapterView, view, i, l) -> {
            bluetoothAdapter.cancelDiscovery();
            pairedDevicesListView.setAdapter(pairedDeviceListAdapter);

            String deviceName = newBtDevices.get(i).getName();
            String deviceAddress = newBtDevices.get(i).getAddress();
            Log.d(TAG, "onItemClick: A device is selected.");
            Log.d(TAG, "onItemClick: DEVICE NAME: " + deviceName);
            Log.d(TAG, "onItemClick: DEVICE ADDRESS: " + deviceAddress);


            Log.d(TAG, "onItemClick: Initiating pairing with " + deviceName);
            newBtDevices.get(i).createBond();

            mBTDevice = newBtDevices.get(i);


        });

        pairedDevicesListView.setOnItemClickListener((adapterView, view, i, l) -> {
            bluetoothAdapter.cancelDiscovery();
            otherDevicesListView.setAdapter(deviceListAdapter);

            String deviceName = pairedBtDevices.get(i).getName();
            String deviceAddress = pairedBtDevices.get(i).getAddress();
            Log.d(TAG, "onItemClick: A device is selected.");
            Log.d(TAG, "onItemClick: DEVICE NAME: " + deviceName);
            Log.d(TAG, "onItemClick: DEVICE ADDRESS: " + deviceAddress);

            mBluetoothConnection = new BluetoothConnectionService(BluetoothConnectionUI.this);
            mBTDevice = pairedBtDevices.get(i);
        });


    }

    protected void initializeVariables(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        otherDevicesListView = (ListView) findViewById(R.id.otherDevicesListView);
        pairedDevicesListView = (ListView) findViewById(R.id.pairedDevicesListView);


        newBtDevices = new ArrayList<>();
        pairedBtDevices = new ArrayList<>();

        connectBtn = (Button) findViewById(R.id.connectBtn);
        searchBtn=(Button) findViewById(R.id.searchBtn);

        connectBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);

        mBluetoothConnection = new BluetoothConnectionService(BluetoothConnectionUI.this);

         alertDialog = new AlertDialog.Builder(this).create();
         alertDialog.setTitle("Connection interrupted");
         alertDialog.setMessage("Reconnecting");
         alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                 new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                     }
                 });
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
            Log.d(TAG,"Bluetooth state changed");
            onResume();
        }

    };


    public void connectBluetooth(){
        if(mBTDevice ==null)
        {
            Snackbar.make(findViewById(android.R.id.content),"Please Select a Device before connecting.", Snackbar.LENGTH_SHORT).show();
        }
        else {
            startConnection();
        }
    }

    public void searchDevices(){
        Log.d(TAG, "toggleButton: Scanning for unpaired devices.");
        newBtDevices.clear();
        if(bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Snackbar.make(findViewById(android.R.id.content),"Please turn on Bluetooth first!", Snackbar.LENGTH_SHORT).show();
            }
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
                Log.d(TAG, "toggleButton: Cancelling Discovery.");

                checkBTPermissions();

                bluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(foundDeviceBroadcastReceiver, discoverDevicesIntent);


            } else if (!bluetoothAdapter.isDiscovering()) {
                checkBTPermissions();

                bluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(foundDeviceBroadcastReceiver, discoverDevicesIntent);
            }
            pairedBtDevices.clear();
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            Log.d(TAG, "toggleButton: Number of paired devices found: "+ pairedDevices.size());
            for(BluetoothDevice d : pairedDevices){
                Log.d(TAG, "Paired Devices: "+ d.getName() +" : " + d.getAddress());
                pairedBtDevices.add(d);
                pairedDeviceListAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, pairedBtDevices);
                pairedDevicesListView.setAdapter(pairedDeviceListAdapter);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.bluetooth_status);
        item.setTitle(this.connStatus);
        return super.onPrepareOptionsMenu(menu);
    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");

        }
    }





    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }
            }
        }
    };

    private BroadcastReceiver foundDeviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                newBtDevices.add(device);
                Log.d(TAG, "onReceive: "+ device.getName() +" : " + device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, newBtDevices);
                otherDevicesListView.setAdapter(deviceListAdapter);

            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"mBroadcastReceiver4");

            final String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BOND_BONDED.");
                    Snackbar.make(findViewById(android.R.id.content),"Successfully paired with " + mDevice.getName(), Snackbar.LENGTH_SHORT).show();
                    mBTDevice = mDevice;
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BOND_BONDING.");
                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BOND_NONE.");
                }
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice mDevice = intent.getParcelableExtra("Device");
            String status = intent.getStringExtra("Status");
            sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();

            if(status.equals("connected")){
                try {
                    alertDialog.dismiss();
                } catch(NullPointerException e){
                    e.printStackTrace();
                }

                Log.d(TAG, "mBroadcastReceiver5: Device now connected to "+mDevice.getName());
                Snackbar.make(findViewById(android.R.id.content),"Device now connected to "+mDevice.getName(), Snackbar.LENGTH_SHORT).show();
                editor.putString("connStatus", "Connected to " + mDevice.getName());
                connStatus="Connected";
                editor.commit();
            }


            else if(status.equals("disconnected") && retryConnection == false){
                Log.d(TAG, "mBroadcastReceiver5: Disconnected from "+mDevice.getName());
                Snackbar.make(findViewById(android.R.id.content),"Disconnected from "+mDevice.getName(), Snackbar.LENGTH_SHORT).show();
                mBluetoothConnection = new BluetoothConnectionService(BluetoothConnectionUI.this);
                mBluetoothConnection.startAcceptThread();

                sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("connStatus", "Disconnected");
                connStatus="Disconnected";
                editor.commit();

                try {
                    alertDialog.show();
                }catch (Exception e){
                    Log.d(TAG, "BluetoothPopUp: mBroadcastReceiver5 Dialog show failure");
                }
                retryConnection = true;
                reconnectionHandler.postDelayed(reconnectionRunnable, 5000);


            }
            invalidateOptionsMenu();
        }
    };

    public void startConnection(){
        startBTConnection(mBTDevice, btUUID);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection");

        mBluetoothConnection.startClientThread(device, uuid);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
        try {
            unregisterReceiver(mBroadcastReceiver2);
            unregisterReceiver(foundDeviceBroadcastReceiver);
            unregisterReceiver(mBroadcastReceiver4);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver5);
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
        try {
            unregisterReceiver(updateBluetoothStatus);
            unregisterReceiver(mBroadcastReceiver2);
            unregisterReceiver(foundDeviceBroadcastReceiver);
            unregisterReceiver(mBroadcastReceiver4);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver5);
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("mBTDevice", mBTDevice);
        data.putExtra("myUUID", btUUID);
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.connectBtn:
                connectBluetooth();
                break;
            case R.id.searchBtn:
                searchDevices();
                break;

        }
    }

}