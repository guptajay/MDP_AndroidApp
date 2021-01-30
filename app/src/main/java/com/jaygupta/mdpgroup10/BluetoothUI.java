package com.jaygupta.mdpgroup10;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



import android.Manifest;
import android.app.ProgressDialog;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothUI extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BluetoothPopUp";
    private String connStatus;
    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mNewBTDevices;
    public ArrayList<BluetoothDevice> mPairedBTDevices;
    public DeviceListAdapter mNewDevlceListAdapter;
    public DeviceListAdapter mPairedDevlceListAdapter;
    TextView connStatusTextView;
    ListView otherDevicesListView;
    ListView pairedDevicesListView;
    Button connectBtn;
    Button searchBtn;


    AlertDialog alertDialog;


    TextView messageReceivedTextView;
    TextView bluetoothStatusTextView;



    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    BluetoothConnectionService mBluetoothConnection;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothDevice mBTDevice;

    boolean retryConnection = false;
    Handler reconnectionHandler = new Handler();

    Runnable reconnectionRunnable = new Runnable() {
        @Override
        public void run() {
            // Magic here
            try {
                if (BluetoothConnectionService.BluetoothConnectionStatus == false) {
                    startBTConnection(mBTDevice, myUUID);
                    Toast.makeText(BluetoothUI.this, "Reconnection Success", Toast.LENGTH_SHORT).show();

                }
                reconnectionHandler.removeCallbacks(reconnectionRunnable);
                retryConnection = false;
            } catch (Exception e) {
                Toast.makeText(BluetoothUI.this, "Failed to reconnect, trying in 5 second", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String str = intent.getStringExtra("receivedMessage");
                messageReceivedTextView.setText(str);

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_user_interface);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        messageReceivedTextView=findViewById(R.id.messageReceivedTextView);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        otherDevicesListView = (ListView) findViewById(R.id.otherDevicesListView);
        pairedDevicesListView = (ListView) findViewById(R.id.pairedDevicesListView);
        mNewBTDevices = new ArrayList<>();
        mPairedBTDevices = new ArrayList<>();

        connectBtn = (Button) findViewById(R.id.connectBtn);
        searchBtn=(Button) findViewById(R.id.searchBtn);
        connectBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);


        //checkBluetoothStatus();


        IntentFilter stateIntentFilter = new IntentFilter(mBluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(checkBluetoothStatus, stateIntentFilter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        IntentFilter filter2 = new IntentFilter("ConnectionStatus");
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver5, filter2);


        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("incomingMessage"));



        otherDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBluetoothAdapter.cancelDiscovery();
                pairedDevicesListView.setAdapter(mPairedDevlceListAdapter);

                String deviceName = mNewBTDevices.get(i).getName();
                String deviceAddress = mNewBTDevices.get(i).getAddress();
                Log.d(TAG, "onItemClick: A device is selected.");
                Log.d(TAG, "onItemClick: DEVICE NAME: " + deviceName);
                Log.d(TAG, "onItemClick: DEVICE ADDRESS: " + deviceAddress);


                Log.d(TAG, "onItemClick: Initiating pairing with " + deviceName);
                mNewBTDevices.get(i).createBond();

                mBluetoothConnection = new BluetoothConnectionService(BluetoothUI.this);
                mBTDevice = mNewBTDevices.get(i);

            }
        });

        pairedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBluetoothAdapter.cancelDiscovery();
                otherDevicesListView.setAdapter(mNewDevlceListAdapter);

                String deviceName = mPairedBTDevices.get(i).getName();
                String deviceAddress = mPairedBTDevices.get(i).getAddress();
                Log.d(TAG, "onItemClick: A device is selected.");
                Log.d(TAG, "onItemClick: DEVICE NAME: " + deviceName);
                Log.d(TAG, "onItemClick: DEVICE ADDRESS: " + deviceAddress);

                mBluetoothConnection = new BluetoothConnectionService(BluetoothUI.this);
                mBTDevice = mPairedBTDevices.get(i);
            }
        });



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

    private BroadcastReceiver checkBluetoothStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Bluetooth state changed");

            if(!mBluetoothAdapter.isEnabled())
                connStatus="Disconnected";
            invalidateOptionsMenu();
        }

    };

    private void checkBluetoothStatus() {

        sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("connStatus"))
            connStatus = sharedPreferences.getString("connStatus", "");
        invalidateOptionsMenu();
    }


    public void connectBluetooth(){
        if(mBTDevice ==null)
        {
            Toast.makeText(BluetoothUI.this, "Please Select a Device before connecting.", Toast.LENGTH_LONG).show();
        }
        else {
            startConnection();
        }
    }

    public void searchDevices(){
        Log.d(TAG, "toggleButton: Scanning for unpaired devices.");
        mNewBTDevices.clear();
        if(mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Snackbar.make(findViewById(android.R.id.content),"Please turn on Bluetooth first!", Snackbar.LENGTH_SHORT).show();
            }
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
                Log.d(TAG, "toggleButton: Cancelling Discovery.");

                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(foundDeviceBroadcastReceiver, discoverDevicesIntent);


            } else if (!mBluetoothAdapter.isDiscovering()) {
                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(foundDeviceBroadcastReceiver, discoverDevicesIntent);
            }
            mPairedBTDevices.clear();
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            Log.d(TAG, "toggleButton: Number of paired devices found: "+ pairedDevices.size());
            for(BluetoothDevice d : pairedDevices){
                Log.d(TAG, "Paired Devices: "+ d.getName() +" : " + d.getAddress());
                mPairedBTDevices.add(d);
                mPairedDevlceListAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, mPairedBTDevices);
                pairedDevicesListView.setAdapter(mPairedDevlceListAdapter);
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
            if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
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
                mNewBTDevices.add(device);
                Log.d(TAG, "onReceive: "+ device.getName() +" : " + device.getAddress());
                mNewDevlceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mNewBTDevices);
                otherDevicesListView.setAdapter(mNewDevlceListAdapter);

            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BOND_BONDED.");
                    Toast.makeText(BluetoothUI.this, "Successfully paired with " + mDevice.getName(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BluetoothUI.this, "Device now connected to "+mDevice.getName(), Toast.LENGTH_LONG).show();
                editor.putString("connStatus", "Connected to " + mDevice.getName());
                connStatus="Connected to " + mDevice.getName();
            }
            else if(status.equals("disconnected") && retryConnection == false){
                Log.d(TAG, "mBroadcastReceiver5: Disconnected from "+mDevice.getName());
                Toast.makeText(BluetoothUI.this, "Disconnected from "+mDevice.getName(), Toast.LENGTH_LONG).show();
                mBluetoothConnection = new BluetoothConnectionService(BluetoothUI.this);
//                mBluetoothConnection.startAcceptThread();


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
            editor.commit();
            invalidateOptionsMenu();
        }
    };

    public void startConnection(){
        startBTConnection(mBTDevice,myUUID);
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
        data.putExtra("myUUID",myUUID);
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