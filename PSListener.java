package edu.nctu.wirelab.testsignalv1;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PSListener extends PhoneStateListener {
    private final String TagName = "PSListener";
    public static String PhoneState = "IDLE"; //IDLE, OFFHOOK, RINGING

    private Date LogTime;
    private SimpleDateFormat LogTimesdf = new SimpleDateFormat("HH:mm:ss:SSS");

    @Override
    public void onCallStateChanged(int state, String incomingNumber){
        super.onCallStateChanged(state, incomingNumber);
        LogTime = null;
        LogTime = new Date();

        switch(state){
            case TelephonyManager.CALL_STATE_IDLE:
                PhoneState = "IDLE";
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                PhoneState = "OFFHOOK";
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                PhoneState = "RINGING";
                break;
        }

        try {
            MainActivity.filewriter.write("Phone State Info: " +  PhoneState + "\nLogTime(HH:mm:ss:SSS):" + LogTimesdf.format(LogTime) + "\n---------------------------------------------------------\n\n");
            //ask ShowMsg to change the ViewText
            ShowMsg.NoticeChange(ShowMsg.PhoneState, PhoneState);
            Log.d(TagName, PhoneState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
