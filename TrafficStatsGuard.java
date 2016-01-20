package edu.nctu.wirelab.testsignalv1;

import android.net.TrafficStats;

import java.util.Timer;
import java.util.TimerTask;

public class TrafficStatsGuard extends TrafficStats {
    private String TrafficMsg;
    private Timer timer;
    private final int timerDelay = 3000;

    public TrafficStatsGuard(){
        super();
    }
    public void TaskStart(){
        timer = new Timer();
        timer.schedule(new TimerToNotify(), timerDelay, timerDelay);
    }
    public void TrafficStatsGuardFinish(){
        timer.cancel();
    }

    private class TimerToNotify extends TimerTask {
        @ Override
        public void run() {
            TrafficMsg = "getMobileRxBytes: "+getMobileRxBytes()+
                    "\ngetMobileTxBytes: "+getMobileTxBytes();
            ShowMsg.NoticeChange(ShowMsg.TrafficInfo, TrafficMsg);
        }
    }
}
