package com.jaygupta.mdpgroup10;

import java.util.ArrayList;

public class Util {

    static String wayPoint;
    static String currentPoint = "0,0";

    public static void setWayPoint(String wP) {
        wayPoint = wP;
    }

    public static void setCurrentPoint(String sP) {
        currentPoint = sP;
    }

    public static String getWayPoint() {
        return wayPoint;
    }

    public static String getCurrentPoint() {
        return currentPoint;
    }

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

    private static int getPositionFromCoordinate(String location, ArrayList<mazeCell> mazeCells) {
        int i = 0;
        for(mazeCell mC : mazeCells) {
            if(mC.getCellName() != null && mC.getCellName().equals(location))
                return i;
            i++;
        }
        return 0;
    }

    public static void setSingleCellByPosition(int position, ArrayList<mazeCell> mazeCells, String color) {
        mazeCells.get(position).setBgColor(color);
    }

    public static void changeBotPosition(int position, ArrayList<mazeCell> mazeCells) {
        String botColor = "#FF726F";
        String mazeColor = "#FFCCCB";

        int currentPosition = getPositionFromCoordinate(getCurrentPoint(), mazeCells);

        // Remove current position & add new position
        for(int i = 0; i <= 2; i++) {
            mazeCells.get(currentPosition+i).setBgColor(mazeColor);
            mazeCells.get(position+i).setBgColor(botColor);
        }

        for(int i = 15; i >= 13; i--) {
            mazeCells.get(currentPosition-i).setBgColor(mazeColor);
            mazeCells.get(position-i).setBgColor(botColor);
        }

        for(int i = 30; i >= 28; i--) {
            mazeCells.get(currentPosition - i).setBgColor(mazeColor);
            mazeCells.get(position - i).setBgColor(botColor);
        }

        setCurrentPoint(mazeCells.get(position).getCellName());
        mazeCells.get(position-29).setBgColor("#940008");
    }
}
