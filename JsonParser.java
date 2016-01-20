package edu.nctu.wirelab.testsignalv1;

import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
    private static String UploadAuthor = "Author";
    private static String UploadEmail = "johnsmith@example.com";

    public static String AtCellInfoToJson(){
        JSONObject obj = new JSONObject();
        JSONArray ary = new JSONArray();

        try {
            obj.put("AppicationType", "Android");
            obj.put("ApplicationVersion", Build.VERSION.RELEASE);
            obj.put("UploadAuthor", UploadAuthor);
            obj.put("UploadEmail", UploadEmail);
            obj.put("IMEI", MainActivity.tm.getDeviceId());

            JSONObject cellinfo = new JSONObject();

            cellinfo.put("Time", System.currentTimeMillis() );
            cellinfo.put("CellID", SignalStrengthListener.AtCellID);
            cellinfo.put("CellMCC", SignalStrengthListener.AtCellMCC);
            cellinfo.put("CellMNC", SignalStrengthListener.AtCellMNC);
            cellinfo.put("CellPCI", SignalStrengthListener.AtCellPCI);
            cellinfo.put("CellTAC", SignalStrengthListener.AtCellTAC);
            cellinfo.put("RSSI", SignalStrengthListener.AtCellRSSI);
            cellinfo.put("SINR", "null");
            cellinfo.put("RSRQ", SignalStrengthListener.AtCellRSRQ);
            cellinfo.put("RSRP", SignalStrengthListener.AtCellRSRP);
            ary.put(cellinfo);

            obj.put("CellularInfo", ary);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    public static String TrafficInfoToJson(){
        JSONObject obj = new JSONObject();

        try {
            obj.put("AppicationType", "Android");
            obj.put("ApplicationVersion", Build.VERSION.RELEASE);
            obj.put("UploadAuthor", UploadAuthor);
            obj.put("UploadEmail", UploadEmail);
            obj.put("IMEI", MainActivity.tm.getDeviceId());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    public static String CallInfoToJson(){
        JSONObject obj = new JSONObject();

        try {
            obj.put("AppicationType", "Android");
            obj.put("ApplicationVersion", Build.VERSION.RELEASE);
            obj.put("UploadAuthor", UploadAuthor);
            obj.put("UploadEmail", UploadEmail);
            obj.put("IMEI", MainActivity.tm.getDeviceId());
            obj.put("Time", System.currentTimeMillis() );

            obj.put("HoldTime", SignalStrengthListener.AvgCellHoldTime);
            obj.put("ResideTime", SignalStrengthListener.AvgCellResideTime);
            obj.put("ExcessLife", PSListener.AvgExcessLife);
            obj.put("CS/PS", "CS");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }
}
