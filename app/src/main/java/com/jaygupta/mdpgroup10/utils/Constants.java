package com.jaygupta.mdpgroup10.utils;

/*

Declare constants here
 */

import java.util.HashMap;
import java.util.UUID;

public final class Constants {

    public static final UUID uuid= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String BLUETOOTH_DISABLED="Disabled";
    public static final String BLUETOOTH_DISCONNECTED="Disconnected";
    public static final String BLUETOOTH_CONNECTED = "Connected";
    public static final String BLUETOOTH_NOT_CONNECTED = "Bluetooth not connected";


    public static final String VOICE_MOV = "mov";
    public static final String VOICE_FORWARD = "forw";
    public static final String VOICE_RIGHT = "rig";
    public static final String VOICE_LEFT = "lef";


    public static final String VOICE_MOV_STATUS = "Moving ";
    public static final String VOICE_FORWARD_STATUS =  VOICE_MOV_STATUS + "Forward";
    public static final String VOICE_RIGHT_STATUS =  VOICE_MOV_STATUS + "Right";
    public static final String VOICE_LEFT_STATUS = VOICE_MOV_STATUS + "Left";
    public static final String VOICE_ERROR_STATUS = "Invalid Command";


    public static final int REQUEST_CODE_SPEECH_INPUT = 1;

    public static final HashMap<String,String> HEX_TABLE = new HashMap<String, String>() {{
        put("0", "0000");
        put("1", "0001");
        put("2", "0010");
        put("3", "0011");
        put("4", "0100");
        put("5", "0101");
        put("6", "0110");
        put("7", "0111");
        put("8", "1000");
        put("9", "1001");
        put("A", "1010");
        put("B", "1011");
        put("C", "1100");
        put("D", "1101");
        put("E", "1110");
        put("F", "1111");
        put("a", "1010");
        put("b", "1011");
        put("c", "1100");
        put("d", "1101");
        put("e", "1110");
        put("f", "1111");


        put("0000","0");
        put("0001","1");
        put("0010","2");
        put("0011","3");
        put("0100","4");
        put("0101","5");
        put("0110","6");
        put("0111","7");
        put("1000","8");
        put("1001","9");
        put("1010","A");
        put("1011","B");
        put("1100","C");
        put("1101","D");
        put("1110","E");
        put("1111","F");
    }};

}
