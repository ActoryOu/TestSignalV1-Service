package edu.nctu.wirelab.testsignalv1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends ActionBarActivity {
    private final String TagName = "MainActivity";
    private TextView LogPathContent;
    private Button SettingButton, StartServiceButton, StopServiceButton, ShowMsgButton, SignalMapButton, UploadButton, RemoveLogsButton, ImportKeyButton, SignInSIgnUpButton;
    public static String LOGPATH, configPath;
    public static String LogPrefix, RecordPrefix;
    public static TelephonyManager tm;
    public static long StartServiceTime;

    //a delay control all the sense info delay, such as all cell info, location update, traffic throughput
    public static int FlashInterval = 1000; //in ms

    private LocationUpdater lu;

    //the traffic monitor that monitors all apps data usage
    public static TrafficSnapshot latest, previous;

    //the cypher for signing the data that is used to upload to server
    public static OutCypher moutcypher;

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
                if( RunIntentService2.RunFlag==false ){
                    initBfRun();

                    lu = new LocationUpdater(MainActivity.this);
                    if( !lu.isOpenGps() ){
                        ShowGPSClosedMsg();
                    }
                    lu = null;
                    RunIntentService2.RunFlag = true;
                    startService(new Intent(MainActivity.this, RunIntentService2.class));
                }
            }
        });
        StopServiceButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                try {
                    //calculate the Avg values
                    SignalStrengthListener.CalculateAvg();
                    PSListener.CalculateAvg();
                    latest = new TrafficSnapshot(MainActivity.this);
                    latest.CalculateUsage(previous);
                    //write the avg value to the file
                    //Log.d(TagName, JsonParser.CallInfoToJson());
                    if( RunIntentService2.recordwriter!=null ){
                        RunIntentService2.recordwriter.write(JsonParser.AppsInfoToJson() + "\n]\n");
                        RunIntentService2.recordwriter.close();
                        RunIntentService2.recordwriter = null;

                        //get the signature of the data and write it to the file "file.sig"
                        moutcypher.openAndWriteFileInBytes(moutcypher.SignString( BrowseFile.getStringFromFile(RunIntentService2.RecordFileSheerPath) ) , RunIntentService2.RecordFileSheerPath+".sig" );
                    }

                    RunIntentService2.RunFlag = false;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
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
        ImportKeyButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ImportKey.class);

                startActivity(intent);
            }
        });
        UploadButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                FTPController ftpcontroller = new FTPController();
                ftpcontroller.execute();
            }
        });
        RemoveLogsButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                FTPController ftpcontroller = new FTPController();
                ftpcontroller.removeFolder(MainActivity.LOGPATH);
            }
        });

        SignInSIgnUpButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SignUpSignIn.class);

                startActivity(intent);
                finish();
            }
        });
    }

    private void initBfRun() {
        JsonParser.FileFirstWrite = true;

        previous = null;
        previous = new TrafficSnapshot(this);
        latest = null;

        StartServiceTime = System.currentTimeMillis();
    }

    public void initVar(){
        LogPathContent = (TextView)findViewById(R.id.LogPathContent);
        SettingButton = (Button)findViewById(R.id.SettingButton);
        StartServiceButton = (Button)findViewById(R.id.StartServiceButton);
        StopServiceButton = (Button)findViewById(R.id.StopServiceButton);
        ShowMsgButton = (Button)findViewById(R.id.ShowMsgButton);
        SignalMapButton = (Button)findViewById(R.id.SignalMapButton);
        UploadButton = (Button)findViewById(R.id.UploadButton);
        RemoveLogsButton = (Button)findViewById(R.id.RemoveLogsButton);
        ImportKeyButton = (Button)findViewById(R.id.ImportKeyButton);
        SignInSIgnUpButton = (Button)findViewById(R.id.SignInSIgnUpButton);

        LOGPATH = new String("/data/data/" + getPackageName()) + "/logs/";
        configPath = "/data/data/" + getPackageName() + "/config";
        if( AddressConfig.LoadConfigFrom(configPath)==false ){
            AddressConfig.SetUserName("DefaultUser");
        }
        JsonParser.SetAccount(AddressConfig.myUserName);

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        LogPrefix = new String("NCTU");
        RecordPrefix = new String("RECORD");

        moutcypher = new OutCypher();
        ShowDialogMsg.mcontext = getApplicationContext();
    }

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

    public void ShowGPSClosedMsg(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("GPS Checker")
                .setMessage("Please Turn Your GPS Service on")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!lu.isOpenGps()) {
                            ShowGPSClosedMsg();
                        }
                    }
                }).show();
    }

}
