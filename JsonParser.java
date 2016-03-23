package edu.nctu.wirelab.testsignalv1;

import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class JsonParser {
    private static final String TagName = "JsonParser";
    public static String Account = "Account";
    public static String Password = "Password";
    public static boolean FileFirstWrite = true;

    public static void SetAccount(String account){
        Account = account;
    }

    public static String AtCellInfoToJson(){
        JSONObject obj = new JSONObject();
        JSONArray ary = new JSONArray();

        try {
            if( FileFirstWrite ) {
                RunIntentService2.recordwriter.write("[\n");
                FileFirstWrite = false;
            }

            obj.put("AppicationType", "Android");
            obj.put("ApplicationVersion", Build.VERSION.RELEASE);
            obj.put("Account", Account);
            obj.put("Password", Password);
            obj.put("equipmentId", MainActivity.tm.getDeviceId());
            obj.put("Event", "Measurement");
            obj.put("BatteryLevel", BatteryInfoReceiver.level);
            if( LocationUpdater.userlocation!=null ){
                obj.put("Lat", LocationUpdater.userlocation.getLatitude());
                obj.put("Lng", LocationUpdater.userlocation.getLongitude());
            }
            else{
                obj.put("Lat", "Unknown");
                obj.put("Lng", "Unknown");
            }

            JSONObject cellinfo = new JSONObject();

            if( SignalStrengthListener.CellInfoType.equals("LTE") ){
                cellinfo.put("Time", System.currentTimeMillis() );
                cellinfo.put("CellInfoType", SignalStrengthListener.CellInfoType);
                cellinfo.put("CellID", SignalStrengthListener.AtCellID);
                cellinfo.put("CellMCC", SignalStrengthListener.AtCellMCC);
                cellinfo.put("CellMNC", SignalStrengthListener.AtCellMNC);
                cellinfo.put("CellPCI", SignalStrengthListener.AtCellPCI);
                cellinfo.put("CellTAC", SignalStrengthListener.AtCellTAC);
                cellinfo.put("RSSI", SignalStrengthListener.AtCellRSSI);
                cellinfo.put("SINR", "null");
                cellinfo.put("RSRQ", SignalStrengthListener.AtCellRSRQ);
                cellinfo.put("RSRP", SignalStrengthListener.AtCellRSRP);
            }
            else if( SignalStrengthListener.CellInfoType.equals("Wcdma") ){
                cellinfo.put("Time", System.currentTimeMillis() );
                cellinfo.put("CellInfoType", SignalStrengthListener.CellInfoType);
                cellinfo.put("CellID", SignalStrengthListener.WcdmaAtCellID);
                cellinfo.put("CellMCC", SignalStrengthListener.WcdmaAtCellMCC);
                cellinfo.put("CellMNC", SignalStrengthListener.WcdmaAtCellMNC);
                cellinfo.put("CellPSC", SignalStrengthListener.WcdmaAtCellPsc);
                cellinfo.put("CellLAC", SignalStrengthListener.WcdmaAtCellLac);
                cellinfo.put("SignalStrength", SignalStrengthListener.WcdmaAtCellSignalStrength);
            }
            ary.put(cellinfo);

            obj.put("CellularInfo", ary);
            return obj.toString(2);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String HandoverInfoToJson(){
        JSONObject obj = new JSONObject();

        try {
            if( FileFirstWrite ) {
                RunIntentService2.recordwriter.write("[\n");
                FileFirstWrite = false;
            }

            obj.put("AppicationType", "Android");
            obj.put("ApplicationVersion", Build.VERSION.RELEASE);
            obj.put("Account", Account);
            obj.put("Password", Password);
            obj.put("equipmentId", MainActivity.tm.getDeviceId());
            obj.put("Event", "Handover");
            obj.put("BatteryLevel", BatteryInfoReceiver.level);
            if( LocationUpdater.userlocation!=null ){
                obj.put("Lat", LocationUpdater.userlocation.getLatitude());
                obj.put("Lng", LocationUpdater.userlocation.getLongitude());
            }
            else{
                obj.put("Lat", "Unknown");
                obj.put("Lng", "Unknown");
            }

            obj.put("Time", System.currentTimeMillis() );
            obj.put("CellResidenceTime", SignalStrengthListener.CellHoldTime);

            return obj.toString(2);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String PhoneStateToJson(){
        JSONObject obj = new JSONObject();

        try {
            if( FileFirstWrite ) {
                RunIntentService2.recordwriter.write("[\n");
                FileFirstWrite = false;
            }

            obj.put("AppicationType", "Android");
            obj.put("ApplicationVersion", Build.VERSION.RELEASE);
            obj.put("Account", Account);
            obj.put("Password", Password);
            obj.put("equipmentId", MainActivity.tm.getDeviceId());
            obj.put("Event", "PhoneStateChanged");
            obj.put("BatteryLevel", BatteryInfoReceiver.level);

            obj.put("Time", System.currentTimeMillis());
            obj.put("PhoneState", PSListener.PhoneState );
            if( SignalStrengthListener.CellInfoType.equals("LTE") )
                obj.put("CellID", SignalStrengthListener.AtCellID );
            else if( SignalStrengthListener.CellInfoType.equals("Wcdma") )
                obj.put("CellID", SignalStrengthListener.WcdmaAtCellID );
            if( PSListener.PhoneState.equals("IDLE") ) {
                obj.put("CallHoldingTime", PSListener.CallHoldingTime);
                obj.put("ExcessLife", PSListener.ExcessLife);
            }

            return obj.toString(2);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String AppsInfoToJson(){
        JSONObject obj = new JSONObject();

        try {
            if( FileFirstWrite ) {
                RunIntentService2.recordwriter.write("[\n");
                FileFirstWrite = false;
            }

            obj.put("AppicationType", "Android");
            obj.put("ApplicationVersion", Build.VERSION.RELEASE);
            obj.put("Account", Account);
            obj.put("Password", Password);
            obj.put("equipmentId", MainActivity.tm.getDeviceId());
            obj.put("Event", "AppsInfo");
            obj.put("BatteryLevel", BatteryInfoReceiver.level);

            obj.put("Time", System.currentTimeMillis());
            obj.put("LastTime", System.currentTimeMillis() - MainActivity.StartServiceTime );
            //get the apps' usage
            JSONArray ary = new JSONArray();
            for( Integer appuid: MainActivity.latest.delta.keySet() ){
                if( MainActivity.latest.apps.get(appuid).tx==0 ){
                    continue;
                }
                String appName = MainActivity.latest.delta.get(appuid).tag;
                TrafficSnapshot.TrafficRecord trafficrecord = MainActivity.latest.delta.get(appuid);
                //Log.d(TagName, "appName:" + appName);

                JSONObject appinfo = new JSONObject();
                appinfo.put("AppName", appName);
                appinfo.put("TotalTx", MainActivity.latest.apps.get(appuid).tx);
                appinfo.put("TotalRx", MainActivity.latest.apps.get(appuid).rx);
                appinfo.put("DeltaTx", trafficrecord.tx);
                appinfo.put("DeltaRx", trafficrecord.rx);

                ary.put(appinfo);
            }
            obj.put("AppsInfo", ary);

            return obj.toString(2);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
