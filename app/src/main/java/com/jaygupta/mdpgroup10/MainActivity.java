package com.jaygupta.mdpgroup10;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mazeRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}