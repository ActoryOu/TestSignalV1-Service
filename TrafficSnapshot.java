package edu.nctu.wirelab.testsignalv1;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.TrafficStats;
import android.util.Log;

import java.util.HashMap;

public class TrafficSnapshot{
    String TagName = "TrafficSnapshot";
    HashMap<Integer, TrafficRecord> apps = new HashMap<Integer, TrafficRecord>();
    HashMap<Integer, TrafficRecord> delta;//AppName <-> delta usage (calculate by function CalculateUsage)

    TrafficSnapshot(Context ctxt) {
        HashMap<Integer, String> appNames=new HashMap<Integer, String>();

        for (ApplicationInfo app : ctxt.getPackageManager().getInstalledApplications(0)) {
            appNames.put(app.uid, app.packageName);
        }

        for (Integer uid : appNames.keySet()) {
            apps.put(uid, new TrafficRecord(uid, appNames.get(uid)));
//            Log.d(TagName, "uid:"+uid+"  AppName:"+appNames.get(uid));
        }
    }

    public class TrafficRecord{
        long tx=0;
        long rx=0;
        String tag=null;

        TrafficRecord() {
            tx= TrafficStats.getTotalTxBytes();
            rx=TrafficStats.getTotalRxBytes();
        }

        TrafficRecord(int uid, String tag) {
            tx=TrafficStats.getUidTxBytes(uid);
            rx=TrafficStats.getUidRxBytes(uid);
            this.tag=tag;
        }
    }

    public void CalculateUsage( TrafficSnapshot previous ){
        delta = new HashMap<Integer, TrafficRecord>();

        if( apps!=null && previous.apps!=null ){
            for( Integer uid : apps.keySet() ){
                String appName = apps.get(uid).tag;

                TrafficRecord tr = new TrafficRecord();
                if( previous.apps.get(uid).tag!=null && previous.apps.get(uid).tag.equals(appName) ){
                    tr.tx=apps.get(uid).tx-previous.apps.get(uid).tx;
                    tr.rx=apps.get(uid).rx-previous.apps.get(uid).rx;
                    tr.tag = appName;
                    delta.put(uid, tr);
                }
                else{
                    tr.tx=apps.get(uid).tx;
                    tr.rx=apps.get(uid).rx;
                    tr.tag = appName;
                    delta.put(uid, tr);
                }
            }
        }
    }
}
