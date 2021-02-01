package com.jaygupta.mdpgroup10;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.jaygupta.mdpgroup10.adapter.mazeRecViewAdapter;
import com.jaygupta.mdpgroup10.bluetooth_services.BluetoothConnectionService;
import com.jaygupta.mdpgroup10.bluetooth_services.BluetoothUI;
import com.jaygupta.mdpgroup10.utils.Constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants CONSTANTS;

        // Initialization of the Maze
        mazeRecView = findViewById(R.id.mazeRecView);

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

    public void moveForward(View view) {
        int currentPosition = Util.getPositionFromCoordinate(Util.getStartPoint(), mazeCells);

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
    }

    public void moveLeft(View view) {
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
    }

    public void moveRight(View view) {
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
        mBluetoothConnection.write(byteArr);
        Snackbar.make(view, "String F1 Sent", Snackbar.LENGTH_LONG).show();
    }

    public void sendStringF2(View view) {
        byteArr = PreferencesHelper.loadData(this, getResources().getString(R.string.f2_key)).getBytes(charset);
        mBluetoothConnection.write(byteArr);
        Snackbar.make(view, "String F2 Sent", Snackbar.LENGTH_LONG).show();
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
            Intent intent = new Intent(this, BluetoothUI.class);
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
      
}
