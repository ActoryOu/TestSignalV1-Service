package edu.nctu.wirelab.testsignalv1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import java.io.IOException;

public class BatteryInfoReceiver extends BroadcastReceiver {
    private static final String TagName = "BatteryInfoReceiver";
    public static int health = 0;
    public static int icon_small = 0;
    public static int level = 0;
    public static int plugged = 0;
    public static boolean present = false;
    public static int scale = 0;
    public static int status = 0;
    public static String technology = "0";
    public static int temperature = 0;
    public static int voltage = 0;

    @Override
    public void onReceive(Context context, Intent intent){
        health= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
        icon_small= intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL,0);
        level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
        plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
        present= intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
        scale= intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        status= intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
        technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
        temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

//        Log.d(TagName, "Health: "+health+"\n"+
//                "Icon Small:"+icon_small+"\n"+
//                "Level: "+level+"\n"+
//                "Plugged: "+plugged+"\n"+
//                "Present: "+present+"\n"+
//                "Scale: "+scale+"\n"+
//                "Status: "+status+"\n"+
//                "Technology: "+technology+"\n"+
//                "Temperature: "+temperature+"\n"+
//                "Voltage: "+voltage+"\n");
    }
}
