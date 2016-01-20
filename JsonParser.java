package edu.nctu.wirelab.testsignalv1;

import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

    public static String ParseToJson(){
        JSONObject obj = new JSONObject();
        JSONArray ary = new JSONArray();
        String UploadAuthor = "Author";
        String UploadEmail = "johnsmith@example.com";

        try {
            obj.put("AppicationType", "Android");
            obj.put("ApplicationVersion", Build.VERSION.RELEASE);
            obj.put("UploadAuthor", UploadAuthor);
            obj.put("UploadEmail", UploadEmail);

            JSONObject cellinfo = new JSONObject();

            cellinfo.put("TimeStamp", System.currentTimeMillis() );
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
}
