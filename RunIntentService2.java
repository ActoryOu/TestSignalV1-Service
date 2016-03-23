package edu.nctu.wirelab.testsignalv1;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RunIntentService2 extends IntentService {
    private static final String TagName = "RunIntentService2";
    public static boolean RunFlag = false;

    //the variables here change by SignalStrengthListener
    private SignalStrengthListener sslistener;
    //the variables here change by SignalStrengthListener

    //the variables here change by PSListener(inherit from PhoneStateListener)
    private PSListener pslistener;
    //the variables here change by PSListener(inherit from PhoneStateListener)

    //the variables here change by Runnable "SenseAllCellRunnable"
    private Handler SenseHandler;
    public static String AllCellInfo;
    //the variables here change by Runnable "SenseAllCellRunnable"

    //the variables here change by
    public static LocationUpdater locationupdater;

    private TrafficStatsGuard tsg;

    private BatteryInfoReceiver batteryInfoReceiver;

    public static FileWriter recordwriter;
    public static File file;
    public static String LogFileSheerPath, RecordFileSheerPath;

    SimpleDateFormat sdf;
    Date LogDate;

    public static Date LogTime;
    public static SimpleDateFormat LogTimesdf;

    public RunIntentService2() {
        super("RunIntentService2");
    }

    public void onCreate(){
        Log.d(TagName, "onCreate");
        super.onCreate();

        initVar();
    }

    public void onDestroy(){
        Log.d(TagName, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TagName, "onHandleIntent");
        CreateLogAndWriter();

        WhichShouldStart();
        while(true){
            if( RunFlag==false ){
                StopRun();
                return;
            }

            try {
                Thread.sleep(MainActivity.FlashInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void CreateLogAndWriter(){
        LogTime = null;
        LogDate = new Date();
        String FilePrefix, RecordFilePrefix;
        FilePrefix = MainActivity.LogPrefix.concat( (sdf.format(LogDate)));
        RecordFilePrefix = MainActivity.RecordPrefix.concat((sdf.format(LogDate)));
        Log.d(TagName, "FilePrefix:"+FilePrefix+" RecordFilePrefix:"+RecordFilePrefix);
        file = new File(MainActivity.LOGPATH);
        if(!file.exists()){
            if( file.mkdir() ){
                Log.d(TagName, "create logs dir");
            }
        }

        try {
            LogFileSheerPath = MainActivity.LOGPATH+FilePrefix;
            RecordFileSheerPath = MainActivity.LOGPATH+RecordFilePrefix;
            recordwriter = new FileWriter(RecordFileSheerPath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void WhichShouldStart(){
        locationupdater.startGPS();

        ShowMsg.NoticeChange(ShowMsg.AtCellInfo, "AtInfoContent");
        ShowMsg.NoticeChange(ShowMsg.PhoneState, "PhoneStateContent");
        ShowMsg.NoticeChange(ShowMsg.TrafficInfo, "Traffic Content");
        ShowMsg.NoticeChange(ShowMsg.AllCellInfo, "All Cell Content");

        if( MainSetting.AtInfoSwitch ){
            sslistener.startService((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
        }
        if( MainSetting.NbtInfoSwitch ){
            SenseHandler.post(SenseAllCellRunnable);
        }
        if( MainSetting.PhoneStateSwitch ){
            pslistener.startService((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
        }
        if( MainSetting.TrafficSwitch ){
            tsg.TaskStart();
        }

        registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void StopRun(){
        Log.d(TagName, "StopRun");
        sslistener.stopService((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
        pslistener.stopService((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
        SenseHandler.removeCallbacks(SenseAllCellRunnable);
        tsg.TrafficStatsGuardFinish();
        unregisterReceiver(batteryInfoReceiver);

        locationupdater.stopGPS();
    }

    public void initVar(){
        sslistener = new SignalStrengthListener();
        pslistener = new PSListener();
        locationupdater = new LocationUpdater( RunIntentService2.this );

        SenseHandler = new Handler();
        AllCellInfo = null;

        tsg = new TrafficStatsGuard();

        batteryInfoReceiver = new BatteryInfoReceiver();

        sdf = new SimpleDateFormat("yyyyMMddHHmm");
        LogTimesdf = new SimpleDateFormat("HH:mm:ss:SSS");
    }

    private Runnable SenseAllCellRunnable = new Runnable() {
        public void run(){
            LogTime = null;
            LogTime = new Date();
            //Log.d(TagName, "SenseAllCellRunnable");
            List<CellInfo> cellInfoList;
            cellInfoList = MainActivity.tm.getAllCellInfo();
            AllCellInfo = null;
            AllCellInfo = "";

            if( cellInfoList==null ){
                return;
            }

            for ( CellInfo cellInfo : cellInfoList) {
                String onecellinfo = cellInfo.toString();
                //Log.d(TagName, "onecellinfo:"+onecellinfo);
                String[] cellinfoparts = onecellinfo.split(" ");
                for( String str:cellinfoparts ){
                    AllCellInfo = AllCellInfo.concat(str + "\n");
                }
                AllCellInfo = AllCellInfo.concat("\n");
            }

            //Log.d(TagName, "AllCellInfo:" + AllCellInfo);
            //ask ShowMsg to change the ViewText
            ShowMsg.NoticeChange(ShowMsg.AllCellInfo, AllCellInfo);

            SenseHandler.postDelayed(SenseAllCellRunnable, MainActivity.FlashInterval);
        }
    };
}
