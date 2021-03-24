package com.jaygupta.mdpgroup10;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Util {

    private static String TAG = "UTIL DEBUG";

    static ArrayList<String> messageListItems = new ArrayList<String>();
    static ArrayList<String> manualListItems = new ArrayList<String>();
    static ArrayList<String> obstacleList = new ArrayList<String>();
    static ArrayList<String> imagesList = new ArrayList<String>();

    public static ArrayList<String> getImagesList() {
        return imagesList;
    }

    public static void addImagesList(String img) {
        imagesList.add(img);
    }

    static String wayPoint = "Not Selected";
    static String startPoint = "0,0";
    static String heading = "right";
    static int orientation = 0;
    static ArrayList<Integer> outOfBoundsLeft = new ArrayList<Integer>(Arrays.asList(30, 45, 60, 75, 90, 105, 120, 135, 150, 165, 180, 195, 210, 225, 245, 255, 270, 285));
    static ArrayList<Integer> outOfBoundsRight = new ArrayList<Integer>(Arrays.asList(42, 57, 72, 87, 102, 117, 132, 147, 162, 177, 192, 207, 222, 237, 252, 267, 282, 297));


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

    public static String getHeading() {
        return heading;
    }

    public static void setHeading(String head) {
        heading = head;
    }

    public static void removeManualMessage() {
        manualListItems.remove(manualListItems.size() - 1);
    }

    public static void clearAllManualMessages() {
        manualListItems.clear();
    }

    public static void initMaze(ArrayList<mazeCell> m) {
        for (int y = 19; y >= 0; y--)
            for (int x = 0; x < 15; x++)
                m.add(new mazeCell(x + "," + y, R.color.maze, x + "," + y, R.color.mazeCellText, false, false));
    }

    public static void initBot(ArrayList<mazeCell> m) {
        for (int y = 0; y <= 2; y++)
            for (int x = 0; x <= 2; x++)
                updateBgColor(x + "," + y, m, R.color.bot);
        updateBgColor("2,1", m, R.color.heading);
    }

    public static void initGoal(ArrayList<mazeCell> m) {
        for (int y = 17; y <= 19; y++)
            for (int x = 12; x <= 14; x++)
                updateBgColor(x + "," + y, m, R.color.goal);
    }

    private static int updateBgColor(String location, ArrayList<mazeCell> mazeCells, int color) {
        int i = 0;
        for (mazeCell mC : mazeCells) {
            if (mC.getCellName() != null && mC.getCellName().equals(location))
                mC.setBgColor(color);
            i++;
        }
        return 0;
    }

    public static int getPositionFromCoordinate(String location, ArrayList<mazeCell> mazeCells) {
        int i = 0;
        for (mazeCell mC : mazeCells) {
            if (mC.getCellName() != null && mC.getCellName().equals(location))
                return i;
            i++;
        }
        return 0;
    }

    public static void setStatus(Activity activity, String s) {
        TextView robotStatus = activity.findViewById(R.id.botStateText);
        robotStatus.setText(s);
    }

    public static int setObstacle(ArrayList<mazeCell> mazeCells, String position, String obsNum, String heading) {
        int pos = getPositionFromCoordinate(position, mazeCells);
        System.out.println("ObsNum Length: " + obsNum.length());
        if(obsNum.length() > 0) {
            System.out.println("Setting Numbered Obstacle");
            System.out.println(obsNum);
            mazeCells.get(pos).setDisplayName(obsNum);
            mazeCells.get(pos).setTextColor(R.color.white);
            mazeCells.get(pos).setBgColor(R.color.black);
            /*
            if(Integer.parseInt(heading) == 0) {
                mazeCells.get(pos + 15).setDisplayName("▲");
                mazeCells.get(pos + 15).setTextColor(R.color.black);
            } else if(Integer.parseInt(heading) == 1) {
                mazeCells.get(pos + 15).setDisplayName("▲");
                mazeCells.get(pos).setTextColor(R.color.black);
            }
             */
        } else {
            System.out.println("Setting Non-Numbered Obstacle");
            mazeCells.get(pos).setBgColor(R.color.black);
            mazeCells.get(pos).setTextColor(R.color.black);
            mazeCells.get(pos).setObstacle(true);
        }

        return pos;
    }

    public static int setExploredArea(ArrayList<mazeCell> mazeCells, String position, boolean setColor) {
        int pos = getPositionFromCoordinate(position, mazeCells);
        if(setColor)
            mazeCells.get(pos).setBgColor(R.color.explored);
        mazeCells.get(pos).setExplored(true);
        return pos;
    }

    public static int removeObstacle(ArrayList<mazeCell> mazeCells, String position) {
        int pos = getPositionFromCoordinate(position, mazeCells);
        mazeCells.get(pos).setDisplayName(position);
        mazeCells.get(pos).setBgColor(R.color.maze);
        mazeCells.get(pos).setTextColor(R.color.mazeCellText);
        mazeCells.get(pos).setObstacle(false);
        return pos;
    }

    public static String generateMDFString_1(ArrayList<mazeCell> mazeCells) {


        ArrayList<Integer> listRows = new ArrayList<Integer>(Arrays.asList(0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150, 165, 180, 195, 210, 225, 245, 255, 270, 285));
        Collections.reverse(listRows);

        String MDF = "";

        for (Integer num : listRows) {
            for(int i = num; i < num + 15; i++) {
                if(mazeCells.get(i).getExplored() == true) {
                    MDF += "1";
                } else {
                    MDF += "0";
                }
            }
        }
        return MDF;
    }

    public static String generateObsString_1(ArrayList<mazeCell> mazeCells) {
        String MDF = "";
        for(int i = 0; i < 300; i++) {
            if(mazeCells.get(i).getObstacle() == true) {
                MDF += "1";
            } else {
                MDF += "0";
            }
        }
        return MDF;
    }

    public static String generateHexMDF(String MDF) {
        MDF  = "11" + MDF;
        MDF = MDF + "11";
        return MDF;
    }

    public static String generateMDF_2(ArrayList<String> exploredList, ArrayList<String> obstacleList) {
        System.out.println("Explored String" + exploredList);
        System.out.println("Obstacle String" + obstacleList);

        for(int index=0;index<exploredList.size();index++){
            if(exploredList.get(index).length() != obstacleList.get(index).length())
                System.out.println("invalid size");
        }

        String string = "";
        for(int elementIndex = 0; elementIndex < exploredList.size() ; elementIndex++) {
            for(int index = 0;index < exploredList.get(elementIndex).length();index++) {
                if(exploredList.get(elementIndex).charAt(index) == '1'){

                    if(obstacleList.get(elementIndex).charAt(index) == '1') {
                        string+='1';
                    }
                    else{
                        string+='0';
                    }
                }
            }
        }
        String stx = new BigInteger(string, 2).toString(16);
        return stx;
    }

    public static ArrayList formatMDFString_1(String MDF) {
        ArrayList<String> MDFString = new ArrayList<String>();
        int j = 1;
        String temp = "";
        for(int i = 0; i < 300; i++) {
            temp += MDF.substring(i, i+1);
            if(j % 15 == 0 && i != 0) {
                MDFString.add(temp);
                temp = "";
            }
            j++;
        }
        return MDFString;
    }


    public static String gridTest(String message, Boolean explored) {
        ArrayList<String> result = new ArrayList<>();

        if (message.length() < 7)
            return null;

        String amdString; //message.substring(2, 6).equals("grid"))
        if (!explored)
            amdString = message.substring(11, message.length() - 2);
        else
            amdString = message.substring(19, message.length() - 2);

        Log.d(TAG, "received HEX string: " + amdString);

        try {
            String binaryString = new BigInteger(amdString, 16).toString(2);

            Log.d(TAG,"HEX converted binary string: " + binaryString);

            int col = 15;
            int row = 19;
            binaryString=binaryString.substring(2,binaryString.length()-2);
            binaryString = new StringBuffer(binaryString).reverse().toString();

            Log.d(TAG,"Processed binary string: " + binaryString);

            for (char c : binaryString.toCharArray()) {
                if (col == 0) {
                    col = 15;
                    row--;
                }
                if (c == '1') {
                    String string = "obs (" + String.valueOf(col - 1) + "," + String.valueOf(row) + ")";
                    result.add(string);
                }
                col--;
            }

            String resultString = "";
            for (String s : result) {
                resultString += s + "\t";
            }

            return resultString;
        } catch (Exception e) {
            System.out.print("Unable to parse: " + message);
            System.out.print(e);

        }
        return "obs (5,5)";
    }
}

