package edu.nctu.wirelab.testsignalv1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private final String TagName = "MainActivity";
    private TextView LogPathContent;
    private Button SettingButton, StartServiceButton, StopServiceButton, ShowMsgButton, SignalMapButton;
    private static String LOGPATH;
    private static String LogPrefix;
    private static TelephonyManager tm;
    //the variables here change by SignalStrengthListener
    private SignalStrengthListener sslistener;
    //the variables here change by SignalStrengthListener

    //the variables here change by PSListener(inherit from PhoneStateListener)
    private PSListener pslistener;
    //the variables here change by PSListener(inherit from PhoneStateListener)

    //the variables here change by Runnable "SenseAllCellRunnable"
    private Handler SenseHandler;
    private final int FlashInterval = 3000; //sense the cell info every 3 seconds
    public static String AllCellInfo;
    //the variables here change by Runnable "SenseAllCellRunnable"

    //the variables here change by
    private LocationUpdater locationupdater;


    SimpleDateFormat sdf;
    Date LogDate;
    public static FileWriter filewriter;
    public static File file;

    Date LogTime;
    SimpleDateFormat LogTimesdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVar();
        LogPathContent.setText(LOGPATH);

        ShowMsgButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ShowMsg.class);

                startActivity(intent);
            }
        });
        SettingButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MainSetting.class);

                startActivity(intent);
            }
        });
        StartServiceButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                try {
                    LogTime = null;
                    LogDate = new Date();
                    String FilePrefix;
                    FilePrefix = LogPrefix.concat( (sdf.format(LogDate))+".txt");
                    Log.d(TagName, "FilePrefix:"+FilePrefix);
                    file = new File(LOGPATH);
                    if(!file.exists()){
                        if( file.mkdir() ){
                            Log.d(TagName, "create logs dir");
                        }
                    }
                    filewriter = new FileWriter(LOGPATH+FilePrefix, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if( MainSetting.AtInfoSwitch ){
                    ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(sslistener, SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS);
                }
                if( MainSetting.NbtInfoSwitch ){
                    SenseHandler.post(SenseAllCellRunnable);
                }
                if( MainSetting.PhoneStateSwitch ){
                    ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(pslistener, PSListener.LISTEN_CALL_STATE);
                }
            }
        });
        StopServiceButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(sslistener, SignalStrengthListener.LISTEN_NONE);
                ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(pslistener, PSListener.LISTEN_NONE);
                SenseHandler.removeCallbacks(SenseAllCellRunnable);
                try {
                    filewriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        SignalMapButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SignalMaps.class);

                startActivity(intent);
            }
        });

        if( !locationupdater.isOpenGps() ){
            ShowGPSClosedMsg();
        }
    }

    public void ShowGPSClosedMsg(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("GPS Checker")
                .setMessage("Please Turn Your GPS Service on")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if( !locationupdater.isOpenGps() ){
                            ShowGPSClosedMsg();
                        }
                    }
                }).show();
    }

    public void initVar(){
        LogPathContent = (TextView)findViewById(R.id.LogPathContent);
        SettingButton = (Button)findViewById(R.id.SettingButton);
        StartServiceButton = (Button)findViewById(R.id.StartServiceButton);
        StopServiceButton = (Button)findViewById(R.id.StopServiceButton);
        ShowMsgButton = (Button)findViewById(R.id.ShowMsgButton);
        SignalMapButton = (Button)findViewById(R.id.SignalMapButton);

        LOGPATH = new String("/data/data/" + getPackageName()) + "/logs/";

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        sslistener = new SignalStrengthListener();
        pslistener = new PSListener();
        locationupdater = new LocationUpdater( (LocationManager)getSystemService(Context.LOCATION_SERVICE) );

        SenseHandler = new Handler();

        LogPrefix = new String("NCTU");
        sdf = new SimpleDateFormat("yyyyMMddHHmm");
        LogTimesdf = new SimpleDateFormat("HH:mm:ss:SSS");
        AllCellInfo = null;
    }

    private Runnable SenseAllCellRunnable = new Runnable() {
        public void run(){
            LogTime = null;
            LogTime = new Date();
            //Log.d(TagName, "SenseAllCellRunnable");
            List<CellInfo> cellInfoList;
            cellInfoList = tm.getAllCellInfo();
            AllCellInfo = null;
            AllCellInfo = "";

            for (CellInfo cellInfo : cellInfoList) {
                String onecellinfo = cellInfo.toString();
                //Log.d(TagName, "onecellinfo:"+onecellinfo);
                String[] cellinfoparts = onecellinfo.split(" ");
                for( String str:cellinfoparts ){
                    AllCellInfo = AllCellInfo.concat(str + "\n");
                }
                AllCellInfo = AllCellInfo.concat("\n");
            }
            try {
                filewriter.write("All Cell Info:\n" + "LogTime(HH:mm:ss:SSS):" + LogTimesdf.format(LogTime) + "\n" + AllCellInfo + "---------------------------------------------------------\n\n");

                //Log.d(TagName, "AllCellInfo:" + AllCellInfo);
                //ask ShowMsg to change the ViewText
                ShowMsg.NoticeChange(ShowMsg.AllCellInfo, AllCellInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }

            SenseHandler.postDelayed(SenseAllCellRunnable, FlashInterval);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static TelephonyManager getTelephonyManager(){
        return tm;
    }
}
