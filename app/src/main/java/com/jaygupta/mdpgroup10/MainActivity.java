package com.jaygupta.mdpgroup10;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mazeRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mazeRecView = findViewById(R.id.mazeRecView);
        ArrayList<mazeCell> mazeCells = new ArrayList<>();
        for (int i = 0; i < 300; i++)
            mazeCells.add(new mazeCell(String.valueOf(i)));

        mazeRecViewAdapter adapter = new mazeRecViewAdapter(this);
        adapter.setCells(mazeCells);
        mazeRecView.setAdapter(adapter);
        mazeRecView.setLayoutManager(new GridLayoutManager(this, 15));
    }
}