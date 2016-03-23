package edu.nctu.wirelab.testsignalv1;

import android.net.TrafficStats;
import android.os.Handler;
import android.util.Log;

public class TrafficStatsGuard extends TrafficStats {
    private final String TagName = "TrafficStatsGuard";
    private String TrafficMsg;
    private long initRxBytes, initTxBytes;
    Handler TrafficHandler;

    public TrafficStatsGuard(){
        super();
        TrafficHandler = new Handler();
    }
    public void TaskStart(){
        TrafficHandler.post(SenseTrafficRunnable);
        initRxBytes = getTotalRxBytes();
        initTxBytes = getTotalTxBytes();
    }
    public void TrafficStatsGuardFinish(){
        TrafficHandler.removeCallbacks(SenseTrafficRunnable);
    }

    private Runnable SenseTrafficRunnable = new Runnable() {
        public void run() {
            TrafficMsg = "getMobileRxBytes: "+getMobileRxBytes()+
                    "\ngetMobileTxBytes: "+getMobileTxBytes()+
                    "\ngetTotalRxBytes: "+getTotalRxBytes()+
                    "\ngetTotalTxBytes: "+getTotalTxBytes();
            //Log.d(TagName, TrafficMsg);
            ShowMsg.NoticeChange(ShowMsg.TrafficInfo, TrafficMsg);
            TrafficHandler.postDelayed(SenseTrafficRunnable, MainActivity.FlashInterval);
        }
    };
}