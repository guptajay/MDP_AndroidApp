package com.jaygupta.mdpgroup10;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
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
    private Button refresh, reset;
    private Button moveForward, moveRight, moveLeft;
    private ImageButton micBtn;

    private final String TAG = "Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants CONSTANTS;

        startService(new Intent(MainActivity.this, BluetoothChatService.class));

        // Initialization of the Maze
        mazeRecView = findViewById(R.id.mazeRecView);
        mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);

        IntentFilter bluetoothStateChangeFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(updateBluetoothStatus, bluetoothStateChangeFilter);

        IntentFilter bluetoothConnectionChangeFilter = new IntentFilter("ConnectionStatus");
        LocalBroadcastManager.getInstance(this).registerReceiver(updateBluetoothStatus, bluetoothConnectionChangeFilter);


        LocalBroadcastManager.getInstance(this).registerReceiver(robotStatusUpdate, new IntentFilter("robotStatusUpdate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mazeUpdate, new IntentFilter("mazeUpdate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(botUpdate, new IntentFilter("botUpdate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(gridObstacles, new IntentFilter("gridObstacles"));
        LocalBroadcastManager.getInstance(this).registerReceiver(changeBotPosition, new IntentFilter("changeBotPosition"));
        LocalBroadcastManager.getInstance(this).registerReceiver(updateExploredGrid, new IntentFilter("exploredPath"));

        // mazeRecView.setHasFixedSize(true);
        mazeCells = new ArrayList<>();
        Util.initMaze(mazeCells);
        adapter = new mazeRecViewAdapter(this, mazeCells);

        mazeRecView.setAdapter(adapter);
        mazeRecView.setLayoutManager(new GridLayoutManager(this, 15));
        mazeRecView.setItemAnimator(null);

        // Initialization of the Bot
        Util.initBot(mazeCells);

        // Initialization of the Goal Area
        Util.initGoal(mazeCells);
        adapter.notifyDataSetChanged();

        drive = new RobotDrive(mazeCells, adapter, this, mBluetoothConnection);
        // Set Shared Preferences Strings Default
        if (PreferencesHelper.loadData(MainActivity.this, getResources().getString(R.string.f1_key)) == "Not Found") {
            PreferencesHelper.saveData(MainActivity.this, "F1 String", getResources().getString(R.string.f1_key));
        }

        if (PreferencesHelper.loadData(MainActivity.this, getResources().getString(R.string.f2_key)) == "Not Found") {
            PreferencesHelper.saveData(MainActivity.this, "F2 String", getResources().getString(R.string.f2_key));
        }

        refresh = findViewById(R.id.refresh);
        reset = findViewById(R.id.reset);
        manualSwitch = findViewById(R.id.manulAutoControl);
        moveForward = findViewById(R.id.moveForwardBtn);
        moveLeft = findViewById(R.id.moveLeftBtn);
        moveRight = findViewById(R.id.moveRightBtn);
        micBtn = findViewById(R.id.micBtn);

        /*

        String resultString = Util.gridTest("{\"grid\" : \"01000000000000F00000000000400007E0000000000000001F80000780000000000004000800\"}",false);
        String explored = Util.gridTest("{\"exploredPath\" : \"00400080010000000000003F000000000000400100040F000000000380000000080010002000\"}",true);
        Log.d(TAG,"Test: " + resultString);


            Matcher loc = Pattern.compile("\\(([^)]+)\\)").matcher(resultString);
            ArrayList<String> receivedArray = new ArrayList<>();
            while (loc.find()) {
                receivedArray.add(loc.group(1));
            }
            ArrayList<String> tempObstacleList = new ArrayList<>();
            tempObstacleList.addAll(Util.obstacleList);
            tempObstacleList.removeAll(receivedArray);
            receivedArray.removeAll(Util.obstacleList);
            System.out.println("To be Removed" + tempObstacleList);
            System.out.println("To be Added" + receivedArray);
            for (String s : receivedArray) {
                int pos = Util.setObstacle(mazeCells, s, "");
                adapter.notifyItemChanged(pos);
            }
            for (String s : tempObstacleList) {
                int pos = Util.removeObstacle(mazeCells, s);
                adapter.notifyItemChanged(pos);
            }
            Matcher loc_1 = Pattern.compile("\\(([^)]+)\\)").matcher(explored);
            ArrayList<String> receivedArray_1 = new ArrayList<>();
            while (loc_1.find()) {
                receivedArray_1.add(loc_1.group(1));
            }
            for (String s : receivedArray_1) {
                //Update this line
                int pos = Util.setExploredArea(mazeCells, s);
                adapter.notifyItemChanged(pos);
            }
            Snackbar.make(findViewById(android.R.id.content), "Update Number" + i, Snackbar.LENGTH_SHORT).show();

         */

        reset.setOnClickListener(v -> {
            for(int i = 0; i < 300; i++) {
                mazeCells.get(i).setBgColor(R.color.maze);
                mazeCells.get(i).setTextColor(R.color.mazeCellText);
            }
            // Initialization of the Bot
            Util.initBot(mazeCells);

            // Initialization of the Goal Area
            Util.initGoal(mazeCells);

            Util.setHeading("right");
            Util.setStartPoint("0,0");

            adapter.notifyDataSetChanged();

            Snackbar.make(findViewById(android.R.id.content), "The grid has been reset!", Snackbar.LENGTH_SHORT).show();

        });

        if (!manualSwitch.isChecked()) {
            refresh.setEnabled(false);
            moveForward.setEnabled(false);
            moveLeft.setEnabled(false);
            moveRight.setEnabled(false);
            micBtn.setEnabled(false);
        }

        manualSwitch.setOnClickListener(v -> {
            if (manualSwitch.isChecked()) {
                refresh.setEnabled(true);
                moveForward.setEnabled(true);
                moveLeft.setEnabled(true);
                moveRight.setEnabled(true);
                micBtn.setEnabled(true);
            }
            else {
                refresh.setEnabled(false);
                moveForward.setEnabled(false);
                moveLeft.setEnabled(false);
                moveRight.setEnabled(false);
                micBtn.setEnabled(false);
            }
        });

        refresh.setOnClickListener(v -> {

            byteArr = "sendArena".getBytes(charset);
            mBluetoothConnection.write(byteArr);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> messageList = Util.getManualListItems();
                    for (String message : messageList) {
                        if (message.contains("mov")) {
                            Matcher action = Pattern.compile("\\(([^)]+)\\)").matcher(message);
                            while (action.find()) {
                                if (action.group(1).equals("forward"))
                                    drive.moveBotForward(findViewById(android.R.id.content));
                                else if (action.group(1).equals("left"))
                                    drive.moveBotLeft(findViewById(android.R.id.content), false);
                                else if (action.group(1).equals("right"))
                                    drive.moveBotRight(findViewById(android.R.id.content), false);
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
                        } else if (message.contains("grid")) {
                            String receivedMessage = Util.gridTest(message.substring(10), false);
                            Matcher loc = Pattern.compile("\\(([^)]+)\\)").matcher(receivedMessage);
                            ArrayList<String> receivedArray = new ArrayList<>();
                            while (loc.find()) {
                                receivedArray.add(loc.group(1));
                            }
                            ArrayList<String> tempObstacleList = new ArrayList<>();
                            tempObstacleList.addAll(Util.obstacleList);
                            tempObstacleList.removeAll(receivedArray);
                            receivedArray.removeAll(Util.obstacleList);
                            System.out.println("To be Removed" + tempObstacleList);
                            System.out.println("To be Added" + receivedArray);
                            for (String s : receivedArray) {
                                int pos = Util.setObstacle(mazeCells, s, "");
                                adapter.notifyItemChanged(pos);
                            }
                            for (String s : tempObstacleList) {
                                int pos = Util.removeObstacle(mazeCells, s);
                                adapter.notifyItemChanged(pos);
                            }
                            Util.obstacleList.addAll(receivedArray);
                            Util.obstacleList.removeAll(tempObstacleList);
                        }
                    }
                    Util.clearAllManualMessages();
                }
            }, 1000);

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
            onResume();
        }
    };


    public void voiceCommand(View view) {

        if (connStatus.equalsIgnoreCase(Constants.BLUETOOTH_CONNECTED)) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("English (US)", "en_US"));
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Provide instructions");
            try {
                startActivityForResult(intent, Constants.REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        } else {
            Snackbar.make(view, Constants.BLUETOOTH_NOT_CONNECTED, Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    parseVoiceInput(result.get(0));
                }
                break;
        }
    }

    private void parseVoiceInput(String string) {
        string = string.toLowerCase();
        String status;
        if (string.contains(Constants.VOICE_MOV)) {
            if (string.contains(Constants.VOICE_FORWARD)) {
                drive.moveBotForward(findViewById(android.R.id.content));
                status = Constants.VOICE_FORWARD_STATUS;
            } else if (string.contains(Constants.VOICE_RIGHT)) {
                drive.moveBotRight(findViewById(android.R.id.content),false);
                status = Constants.VOICE_RIGHT_STATUS;
            } else if (string.contains(Constants.VOICE_LEFT)) {
                drive.moveBotLeft(findViewById(android.R.id.content),false);
                status = Constants.VOICE_LEFT_STATUS;
            } else
                status = Constants.VOICE_ERROR_STATUS;

        } else
            status = Constants.VOICE_ERROR_STATUS;

        Util.setStatus(MainActivity.this, status);
    }

    public void moveForward(View view) {
        drive.moveBotForward(view);
    }

    public void moveLeft(View view) {
        drive.moveBotLeft(view, false);
    }

    public void moveRight(View view) {
        drive.moveBotRight(view, false);
    }

    public void setStrings(View view) {
        f1String = (TextInputLayout) dialog.findViewById(R.id.stringF1);
        f2String = (TextInputLayout) dialog.findViewById(R.id.stringF2);
        PreferencesHelper.saveData(this, f1String.getEditText().getText().toString(), getResources().getString(R.string.f1_key));
        PreferencesHelper.saveData(this, f2String.getEditText().getText().toString(), getResources().getString(R.string.f2_key));
        Snackbar.make(view, "F1/F2 strings updated successfully", Snackbar.LENGTH_LONG).show();
    }

    public void sendStringF1(View view) {
        byteArr = PreferencesHelper.loadData(this, getResources().getString(R.string.f1_key)).getBytes(charset);
        if (mBluetoothConnection.getBluetoothConnectionStatus()) {
            mBluetoothConnection.write(byteArr);
            Snackbar.make(view, "String F1 Sent", Snackbar.LENGTH_SHORT).show();
        } else
            Snackbar.make(view, Constants.BLUETOOTH_NOT_CONNECTED, Snackbar.LENGTH_SHORT).show();
    }

    public void sendStringF2(View view) {
        byteArr = PreferencesHelper.loadData(this, getResources().getString(R.string.f2_key)).getBytes(charset);

        if (mBluetoothConnection.getBluetoothConnectionStatus()) {
            mBluetoothConnection.write(byteArr);
            Snackbar.make(view, "String F2 Sent", Snackbar.LENGTH_SHORT).show();
        } else
            Snackbar.make(view, Constants.BLUETOOTH_NOT_CONNECTED, Snackbar.LENGTH_SHORT).show();
    }

    public void startFastestPath(View view) {
        byteArr = getResources().getString(R.string.start_fastest_path).getBytes(charset);
        if (mBluetoothConnection.getBluetoothConnectionStatus()) {
            mBluetoothConnection.write(byteArr);
            Snackbar.make(view, "Fastest Path Started", Snackbar.LENGTH_SHORT).show();
        } else
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

        } else if (item.getItemId() == R.id.bluetooth_chat) {
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

    public BroadcastReceiver updateExploredGrid = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equalsIgnoreCase("exploredPath")) {
                String currentBotPos = Util.getStartPoint();
                ArrayList<String> botPos = new ArrayList<String>(){{
                    add(currentBotPos);
                    add((Integer.parseInt(String.valueOf(currentBotPos.charAt(0))) + 1) + "," + currentBotPos.charAt(2));
                    add((Integer.parseInt(String.valueOf(currentBotPos.charAt(0))) + 2) + "," + currentBotPos.charAt(2));
                    add(currentBotPos.charAt(0) + "," + (Integer.parseInt(String.valueOf(currentBotPos.charAt(2))) + 1));
                    add(currentBotPos.charAt(0) + "," + (Integer.parseInt(String.valueOf(currentBotPos.charAt(2))) + 2));
                    add((Integer.parseInt(String.valueOf(currentBotPos.charAt(0))) + 1) + "," + (Integer.parseInt(String.valueOf(currentBotPos.charAt(2))) + 1));
                    add((Integer.parseInt(String.valueOf(currentBotPos.charAt(0))) + 1) + "," + (Integer.parseInt(String.valueOf(currentBotPos.charAt(2))) + 2));
                    add((Integer.parseInt(String.valueOf(currentBotPos.charAt(0))) + 2) + "," + (Integer.parseInt(String.valueOf(currentBotPos.charAt(2))) + 1));
                    add((Integer.parseInt(String.valueOf(currentBotPos.charAt(0))) + 2) + "," + (Integer.parseInt(String.valueOf(currentBotPos.charAt(2))) + 2));
                }};;
                String receivedMessage = intent.getStringExtra("receivedMessage");
                System.out.println(receivedMessage);
                Matcher loc = Pattern.compile("\\(([^)]+)\\)").matcher(receivedMessage);
                ArrayList<String> receivedArray = new ArrayList<>();
                while (loc.find()) {
                    receivedArray.add(loc.group(1));
                }
                for (String s : receivedArray) {
                    //Update this line
                    if(!botPos.contains(s)) {
                        int pos = Util.setExploredArea(mazeCells, s);
                        adapter.notifyItemChanged(pos);
                    }
                }
                }
        }
    };

    public BroadcastReceiver robotStatusUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equalsIgnoreCase("robotStatusUpdate")) {
                String receivedMessage = intent.getStringExtra("receivedMessage");
                System.out.println(receivedMessage);

                assert receivedMessage != null;
                Matcher status = Pattern.compile("\\(([^)]+)\\)").matcher(receivedMessage);
                while (status.find()) {
                    System.out.println(status.group(1));
                    Util.setStatus(MainActivity.this, status.group(1));
                }
            }

        }
    };

    public BroadcastReceiver changeBotPosition = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equalsIgnoreCase("changeBotPosition")) {
                String receivedMessage = intent.getStringExtra("receivedMessage");


                assert receivedMessage != null;
                Matcher newCor = Pattern.compile("\\[(.*?)]").matcher(receivedMessage);
                ArrayList<String> coordinates=null;
                while (newCor.find()) {
                    coordinates = new ArrayList<>(Arrays.asList(Objects.requireNonNull(newCor.group(1)).split(",")));
                }
                    int currentPosition = Util.getPositionFromCoordinate(Util.getStartPoint(), mazeCells);
                assert coordinates != null;
                int x = Integer.parseInt(coordinates.get(0));
                    int y = 17 - Integer.parseInt(coordinates.get(1).replaceAll(" ", ""));
                    int heading = Integer.parseInt(coordinates.get(2).replaceAll(" ", ""));
                    int pos = Util.getPositionFromCoordinate(x + "," + y, mazeCells);
                    Log.d(TAG, "Bot Change" + x + " " + y + " " + pos);

                    if (pos != currentPosition) {
                        // Remove current position & add new position
                        for (int i = 0; i <= 2; i++) {
                            mazeCells.get(currentPosition + i).setBgColor(R.color.maze);
                            mazeCells.get(pos + i).setBgColor(R.color.bot);
                            adapter.notifyItemChanged(currentPosition + i);
                            adapter.notifyItemChanged(pos + i);
                        }

                        for (int i = 15; i >= 13; i--) {
                            mazeCells.get(currentPosition - i).setBgColor(R.color.maze);
                            mazeCells.get(pos - i).setBgColor(R.color.bot);
                            adapter.notifyItemChanged(currentPosition - i);
                            adapter.notifyItemChanged(pos - i);
                        }

                        for (int i = 30; i >= 28; i--) {
                            mazeCells.get(currentPosition - i).setBgColor(R.color.maze);
                            mazeCells.get(pos - i).setBgColor(R.color.bot);
                            adapter.notifyItemChanged(currentPosition - i);
                            adapter.notifyItemChanged(pos - i);
                        }
                        Util.setStartPoint(mazeCells.get(pos).getCellName());
                        mazeCells.get(pos - 29).setBgColor(R.color.heading);
                        Util.setHeading("forward");
                    }

                    System.out.println(heading);
                    System.out.println(Util.getHeading());

                int orientation = heading - Util.orientation;
                Log.d(TAG, "orientation: " + orientation);
                if(orientation == 90 || orientation ==  -270){
                    //move right
                    //Util.setStatus(MainActivity.this, "Turning Right");
                    drive.moveBotRight(findViewById(android.R.id.content), true);

                }
                else if (orientation == -90 || orientation ==  270) {
                    // move left
                    //Util.setStatus(MainActivity.this, "Turning Left");
                    drive.moveBotLeft(findViewById(android.R.id.content), true);
                }

                Util.orientation=heading;
                }
            }

    };

    public BroadcastReceiver mazeUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equalsIgnoreCase("mazeUpdate")) {
                manualSwitch = findViewById(R.id.manulAutoControl);
                if (!manualSwitch.isChecked()) {
                    Util.removeManualMessage();
                    String receivedMessage = intent.getStringExtra("receivedMessage");
                    assert receivedMessage != null;
                    Matcher loc = Pattern.compile("\\(([^)]+)\\)").matcher(receivedMessage);
                    Matcher obsNum = Pattern.compile("\\[(.*?)]").matcher(receivedMessage);
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

    public BroadcastReceiver gridObstacles = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equalsIgnoreCase("gridObstacles")) {
                manualSwitch = findViewById(R.id.manulAutoControl);
                if (!manualSwitch.isChecked()) {
                    Util.removeManualMessage();
                    String receivedMessage = intent.getStringExtra("receivedMessage");
                    System.out.println(receivedMessage);
                    assert receivedMessage != null;
                    Matcher loc = Pattern.compile("\\(([^)]+)\\)").matcher(receivedMessage);
                    ArrayList<String> receivedArray = new ArrayList<>();
                    while (loc.find()) {
                        receivedArray.add(loc.group(1));
                    }
                    ArrayList<String> tempObstacleList = new ArrayList<>(Util.obstacleList);
                    tempObstacleList.removeAll(receivedArray);
                    receivedArray.removeAll(Util.obstacleList);
                    System.out.println("To be Removed" + tempObstacleList);
                    System.out.println("To be Added" + receivedArray);
                    for (String s : receivedArray) {
                        int pos = Util.setObstacle(mazeCells, s, "");
                        adapter.notifyItemChanged(pos);
                    }
                    for (String s : tempObstacleList) {
                        int pos = Util.removeObstacle(mazeCells, s);
                        adapter.notifyItemChanged(pos);
                    }
                    Util.obstacleList.addAll(receivedArray);
                    Util.obstacleList.removeAll(tempObstacleList);
                }
            }
        }
    };

    public BroadcastReceiver botUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equalsIgnoreCase("botUpdate")) {
                manualSwitch = findViewById(R.id.manulAutoControl);
                if (!manualSwitch.isChecked()) {
                    Util.removeManualMessage();
                    String receivedMessage = intent.getStringExtra("receivedMessage");

                    System.out.println("Bot Movement Detected");

                    assert receivedMessage != null;
                    Matcher action = Pattern.compile("\\(([^)]+)\\)").matcher(receivedMessage);
                    Matcher num = Pattern.compile("\\[(.*?)]").matcher(receivedMessage);

                    System.out.println(action);
                    System.out.println(num);

                    while (action.find()) {
                        while(num.find()) {
                            if (Objects.equals(action.group(1), "F"))
                                for (int i = 0; i < Integer.parseInt(num.group(1)); i++)
                                    drive.moveBotForward(findViewById(android.R.id.content));
                            else if (Objects.equals(action.group(1), "L"))
                                drive.moveBotLeft(findViewById(android.R.id.content), false);
                            else if (Objects.equals(action.group(1), "R"))
                                drive.moveBotRight(findViewById(android.R.id.content), false);
                            else if (Objects.equals(action.group(1), "B")) {
                                drive.moveBotRight(findViewById(android.R.id.content), false);
                                drive.moveBotRight(findViewById(android.R.id.content), false);
                            }
                        }
                    }
                }
            }
        }
    };
}
