package com.jaygupta.mdpgroup10.utils;

/*

Declare constants here
 */

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

}
