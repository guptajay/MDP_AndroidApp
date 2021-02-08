package com.jaygupta.mdpgroup10;

import android.app.Activity;
import android.widget.TextView;

import java.util.ArrayList;

public class Util {


    static ArrayList<String> messageListItems=new ArrayList<String>();
    static ArrayList<String> manualListItems=new ArrayList<String>();

    static String wayPoint = "Not Selected";
    static String startPoint = "0,0";
    static String heading = "forward";


    public static ArrayList<String> getMessageListItems() {
        return messageListItems;
    }

    public static void setMessageListItems(String message) {
        messageListItems.add(message);
    }

    public static ArrayList<String> getManualListItems() {
        return manualListItems;
    }

    public static void setManualListItems(String message) {
        manualListItems.add(message);
    }

    public static void setWayPoint(String wP) {
        wayPoint = wP;
    }

    public static void setStartPoint(String sP) {
        startPoint = sP;
    }

    public static String getWayPoint() {
        return wayPoint;
    }

    public static String getStartPoint() {
        return startPoint;
    }

    public static String getHeading() {return heading; }

    public static void setHeading(String head) { heading = head; }

    public static void removeManualMessage() {
        manualListItems.remove(manualListItems.size() - 1);
    }

    public static void clearAllManualMessages() {
        manualListItems.clear();
    }

    public static void initMaze(ArrayList<mazeCell> m) {
        for (int y = 19; y >= 0; y--)
            for (int x = 0; x < 15; x++)
                m.add(new mazeCell(x + "," + y, R.color.maze, x + "," + y, R.color.mazeCellText));
    }

    public static void initBot(ArrayList<mazeCell> m) {
        for (int y = 0; y <= 2; y++)
            for (int x = 0; x <= 2; x++)
                updateBgColor(x + "," + y, m, R.color.bot);
        updateBgColor("1,2", m, R.color.heading);
    }

    public static void initGoal(ArrayList<mazeCell> m) {
        for (int y = 17; y <= 19; y++)
            for (int x = 12; x <= 14; x++)
                updateBgColor(x + "," + y, m, R.color.goal);
    }

    private static int updateBgColor(String location, ArrayList<mazeCell> mazeCells, int color) {
        int i = 0;
        for(mazeCell mC : mazeCells) {
            if(mC.getCellName() != null && mC.getCellName().equals(location))
                mC.setBgColor(color);
            i++;
        }
        return 0;
    }

    public static int getPositionFromCoordinate(String location, ArrayList<mazeCell> mazeCells) {
        int i = 0;
        for(mazeCell mC : mazeCells) {
            if(mC.getCellName() != null && mC.getCellName().equals(location))
                return i;
            i++;
        }
        return 0;
    }

    public static void setStatus(Activity activity, String s) {
        TextView robotStatus = activity.findViewById(R.id.botStateText);
        robotStatus.setText(s);
    }

    public static int setObstacle(ArrayList<mazeCell> mazeCells, String position, String obsNum) {
        int pos = getPositionFromCoordinate(position, mazeCells);
        mazeCells.get(pos).setDisplayName(obsNum);
        mazeCells.get(pos).setBgColor(R.color.black);
        mazeCells.get(pos).setTextColor(R.color.white);
        return pos;
    }
}
