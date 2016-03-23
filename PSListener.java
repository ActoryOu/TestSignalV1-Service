package edu.nctu.wirelab.testsignalv1;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PSListener extends PhoneStateListener {
    private static final String TagName = "PSListener";
    public static String PhoneState = "IDLE"; //IDLE, OFFHOOK, RINGING

    public static int CallNum = 0, CallExcessNum = 0;
    public static long CallStartAt = 0, CallStayCellAt = 0, CallHoldingTime = 0, ExcessLife = 0;
    public static double AvgCallHoldTime = 0, AvgExcessLife = 0, TtlCallHoldTime = 0, TtlExcessLife = 0;
    public static boolean FirstCallCell = false;

    private static Date LogTime;
    private static SimpleDateFormat LogTimesdf = new SimpleDateFormat("HH:mm:ss:SSS");

    @Override
    public void onCallStateChanged(int state, String incomingNumber){
        super.onCallStateChanged(state, incomingNumber);
        LogTime = null;
        LogTime = new Date();

        switch(state){
            case TelephonyManager.CALL_STATE_IDLE:
                if( PhoneState.equals("OFFHOOK") ){
                    CallHoldingTime = System.currentTimeMillis() - CallStartAt;
                    TtlCallHoldTime = TtlCallHoldTime + CallHoldingTime;
                    HandoverOccur(); //calculate the excess time if the cell is NOT changed since the call came
                }
                PhoneState = "IDLE";
                FirstCallCell = false;
                try {
                    RunIntentService2.recordwriter.write(JsonParser.PhoneStateToJson() + "\n,\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                CallNum = CallNum+1;
                CallStartAt = System.currentTimeMillis();
                CallStayCellAt = CallStartAt;
                FirstCallCell = true;
                PhoneState = "OFFHOOK";
                try {
                    RunIntentService2.recordwriter.write(JsonParser.PhoneStateToJson() + "\n,\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                PhoneState = "RINGING";
                break;
        }

        //ask ShowMsg to change the ViewText
        String Msg = "PhoneStateContent";
        if( SignalStrengthListener.CellInfoType.equals("LTE") ){
            Msg = "Event: PhoneStateChanged" +
                    "\nTime: " + System.currentTimeMillis() +
                    "\nPhoneState: " + PhoneState +
                    "\nCellID: " + SignalStrengthListener.AtCellID +
                    "\nCallHoldingTime: " + PSListener.CallHoldingTime +
                    "\nExcessLife: " + PSListener.ExcessLife;
        }
        else if(SignalStrengthListener.CellInfoType.equals("Wcdma")){
            Msg = "Event: PhoneStateChanged" +
                    "\nTime: " + System.currentTimeMillis() +
                    "\nPhoneState: " + PhoneState +
                    "\nCellID: " + SignalStrengthListener.WcdmaAtCellID +
                    "\nCallHoldingTime: " + PSListener.CallHoldingTime +
                    "\nExcessLife: " + PSListener.ExcessLife;
        }
        ShowMsg.NoticeChange(ShowMsg.PhoneState, Msg);
    }

    public static void HandoverOccur(){
        if( PhoneState.equals("OFFHOOK") && FirstCallCell ){
            CallExcessNum = CallExcessNum + 1;
            ExcessLife = System.currentTimeMillis() - CallStartAt;
            TtlExcessLife = TtlExcessLife + ExcessLife;
            FirstCallCell = false;
        }
//        Log.d(TagName, PhoneState);

//        LogTime = null;
//        LogTime = new Date();
//        RunIntentService2.filewriter.write("Handover occur!!" +
//                "\nPhone State Info: " + PhoneState +
//                "\nLogTime(HH:mm:ss:SSS):" + LogTimesdf.format(LogTime) +
//                "\nCellID:" + SignalStrengthListener.AtCellID +
//                "\n---------------------------------------------------------\n\n");
    }

    public static void CalculateAvg(){
        if( CallNum!=0 )
            AvgCallHoldTime = TtlCallHoldTime/CallNum;
        if( CallExcessNum!=0 )
            AvgExcessLife = TtlExcessLife/CallExcessNum;
    }

    private void initBfRun() {
        CallNum = 0;
        CallExcessNum = 0;
        CallStartAt = 0;
        CallStayCellAt = 0;
        CallHoldingTime = 0;
        ExcessLife = 0;
        AvgCallHoldTime = 0;
        AvgExcessLife = 0;
        TtlCallHoldTime = 0;
        TtlExcessLife = 0;
        FirstCallCell = false;
    }

    public void startService(TelephonyManager tm){
        initBfRun();

        tm.listen(this, PSListener.LISTEN_CALL_STATE);
    }

    public void stopService(TelephonyManager tm){
        tm.listen(this, PSListener.LISTEN_NONE);
    }
}
