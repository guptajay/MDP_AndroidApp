package com.jaygupta.mdpgroup10;

import java.util.ArrayList;

public class Util {

    public static void initMaze(ArrayList<mazeCell> m) {
        for (int y = 19; y >= 0; y--)
            for (int x = 0; x < 15; x++)
                m.add(new mazeCell(x + "," + y, "#FFCCCB"));
    }

    public static void initBot(ArrayList<mazeCell> m) {
        String color = "#FF726F";
        for (int y = 0; y <= 2; y++)
            for (int x = 0; x <= 2; x++)
                updateBgColor(x + "," + y, m, color);
        updateBgColor("1,2", m, "#940008");
    }

    public static void initGoal(ArrayList<mazeCell> m) {
        String color = "#A6F1A6";
        for (int y = 17; y <= 19; y++)
            for (int x = 12; x <= 14; x++)
                updateBgColor(x + "," + y, m, color);
    }

    private static int updateBgColor(String location, ArrayList<mazeCell> mazeCells, String color) {
        int i = 0;
        for(mazeCell mC : mazeCells) {
            if(mC.getCellName() != null && mC.getCellName().equals(location))
                mC.setBgColor(color);
            i++;
        }
        return 0;
    }
}
