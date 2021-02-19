package com.jaygupta.mdpgroup10.bluetooth_services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jaygupta.mdpgroup10.R;
import com.jaygupta.mdpgroup10.Util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static com.jaygupta.mdpgroup10.Util.getMessageListItems;
import static com.jaygupta.mdpgroup10.Util.setMessageListItems;

public class BluetoothChatUI extends AppCompatActivity {


    ListView messagesListView;
    ArrayAdapter<String> messageListAdapter;
    BluetoothConnectionService bluetoothConnectionService;
    static final String TAG = "BluetoothChat";

    Button sendBtn;
    EditText sendInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chat);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Bluetooth Communication");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        bluetoothConnectionService = new BluetoothConnectionService(this);
        sendInput=findViewById(R.id.sendInput);
        sendBtn = findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {sendData();}});

    }

    private void sendData() {

        if(bluetoothConnectionService.getBluetoothConnectionStatus()){
        String sentText= sendInput.getText().toString();
        setMessageListItems(sentText);
        messageListAdapter.notifyDataSetChanged();
        messagesListView.setSelection(messageListAdapter.getCount()-1);
        bluetoothConnectionService.write(sentText.getBytes(StandardCharsets.UTF_8));
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        messagesListView = findViewById(R.id.listView_chat);

        ArrayList <String> messages = Util.getMessageListItems();
        messageListAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);
        messagesListView.setAdapter(messageListAdapter);

        Log.d(TAG, "On Resume");
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceived, new IntentFilter("incomingMessage"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        try{
            LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceived);
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Failed to unregister received activity");
        }

    }

    public BroadcastReceiver messageReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "On BroadCastReceiver");
            if (intent != null && intent.getAction().equalsIgnoreCase("incomingMessage")) {
                getMessageListItems();

                messageListAdapter.notifyDataSetChanged();
                messagesListView.setSelection(messageListAdapter.getCount()-1);
            }
        }
    };

}