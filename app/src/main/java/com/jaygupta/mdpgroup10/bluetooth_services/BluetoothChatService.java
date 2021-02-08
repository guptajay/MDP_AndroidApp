package com.jaygupta.mdpgroup10.bluetooth_services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.jaygupta.mdpgroup10.Util.setManualListItems;
import static com.jaygupta.mdpgroup10.Util.setMessageListItems;


/*

This function executes in the background and collects all the messages from the bluetooth transmitter which are constantly added
to the static array list in the Utils class

 */
public class BluetoothChatService extends Service {
    @Nullable

    ListView messagesListView;
    //ArrayList<String> messageListItems;
    ArrayAdapter<String> messageListAdapter;
    static final String TAG = "Bluetooth Chat Service";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"Activity Started");
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceived, new IntentFilter("incomingMessage"));
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    public BroadcastReceiver messageReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "On BroadCastReceiver");
            if (intent != null && intent.getAction().equalsIgnoreCase("incomingMessage")) {
                String receivedMessage = intent.getStringExtra("receivedMessage");
                setMessageListItems("Received: " + receivedMessage);
                setManualListItems("Received: " + receivedMessage);
                Log.d(TAG,"Message received " + receivedMessage);


                //send broadcast to main Activity for updating status
                messageDelivery(receivedMessage);

            }
        }
    };

    private void messageDelivery(String receivedMessage) {

        if(receivedMessage.contains("status")){
            Intent robotStatusUpdateIntent = new Intent("robotStatusUpdate");
            robotStatusUpdateIntent.putExtra("receivedMessage", receivedMessage);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(robotStatusUpdateIntent);
        } else if(receivedMessage.contains("obs")){
            Intent mazeUpdateIntent = new Intent("mazeUpdate");
            mazeUpdateIntent.putExtra("receivedMessage", receivedMessage);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(mazeUpdateIntent);
        } else if(receivedMessage.contains("mov")){
            Intent botMoveIntent = new Intent("botUpdate");
            botMoveIntent.putExtra("receivedMessage", receivedMessage);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(botMoveIntent);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
