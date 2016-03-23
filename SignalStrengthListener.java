package edu.nctu.wirelab.testsignalv1;

import android.annotation.TargetApi;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SignalStrengthListener extends PhoneStateListener {
    private final String TagName = "SignalStrengthListener";
    private long curTestTime=0, preTestTime=0;
    private List<CellInfo> cellInfoList;
    private String Msg;

    //CellInfoType (LTE or Wcdma)
    public static String CellInfoType;
    //LTE info
    public static int AtCellRSRP, AtCellID, AtCellMCC, AtCellMNC, AtCellPCI, AtCellTAC, AtCellRSSI, AtCellRSRQ;
    public static long AtCellTimeInterval;
    //Wcdma info
    public static int WcdmaAtCellPsc, WcdmaAtCellLac, WcdmaAtCellID, WcdmaAtCellMCC, WcdmaAtCellMNC, WcdmaAtCellSignalStrength;

    public static int PassCellNum = 0;
    public static int NowCellID = 0;
    public static long StayCellAt = 0, CellHoldTime = 0;
    public static double AvgCellResidenceTime = 0, AvgCellHoldTime, TtlCellResidenceTime = 0;

    private Date LogTime;
    private SimpleDateFormat LogTimesdf = new SimpleDateFormat("HH:mm:ss:SSS");

    public SignalStrengthListener(){

    }

    public static void CalculateAvg(){
        if( PassCellNum!=0 )
            AvgCellResidenceTime = TtlCellResidenceTime/PassCellNum;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onSignalStrengthsChanged(android.telephony.SignalStrength signalstrength) {
        try {
            curTestTime = System.currentTimeMillis();
            LogTime = null;
            LogTime = new Date();
            Log.d(TagName, "curTestTime:"+curTestTime);
            String ltestr = signalstrength.toString();
            String[] parts = ltestr.split(" ");
            Log.d(TagName, "mGsmSignalStrength:" + parts[1] +
                            " mGsmBitErrorRate:" + parts[2] +
                            " mCdmaDbm:" + parts[3] +
                            " mCdmaEcio:" + parts[4] +
                            " mEvdoDbm:" + parts[5] +
                            " mEvdoEcio:" + parts[6] +
                            " mEvdoSnr:" + parts[7] +
                            " mLteSignalStrength:" + parts[8] +
                            " mLteRsrp:" + parts[9] +
                            " mLteRsrq:" + parts[10] +
                            " mLteRssnr:" + parts[11] +
                            " mLteCqi:" + parts[12] +
                            " mTdScdmaRscp:" + parts[13] +
                            " (gsm|lte : cdma)?:" + parts[14]
            );

            AtCellRSRQ = Integer.valueOf(parts[10]);
            if (Integer.valueOf(parts[8]) >= 0 && Integer.valueOf(parts[8]) <= 99)
                AtCellRSSI = Integer.valueOf(parts[8]) * 2 - 113;
            else if (Integer.valueOf(parts[8]) >= 100 && Integer.valueOf(parts[8]) <= 199)
                AtCellRSSI = Integer.valueOf(parts[8]) - 216;
            else
                AtCellRSSI = Integer.MAX_VALUE;
            //String ltersrp = parts[9];

            cellInfoList = MainActivity.getTelephonyManager().getAllCellInfo();
            for (CellInfo cellInfo : cellInfoList) {
                if ( cellInfo.isRegistered()) {
                    //Log.d(TagName, "isRegistered");
                    if( cellInfo instanceof CellInfoLte ){
                        CellInfoType = "LTE";
                        // cast to CellInfoLte and call all the CellInfoLte methods you need
                        // gets RSRP cell signal strength:
                        AtCellRSRP = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();

                        // Gets the LTE cell identity: (returns 28-bit Cell Identity, Integer.MAX_VALUE if unknown)
                        AtCellID = ((CellInfoLte) cellInfo).getCellIdentity().getCi();

                        // Gets the LTE MCC: (returns 3-digit Mobile Country Code, 0..999, Integer.MAX_VALUE if unknown)
                        AtCellMCC = ((CellInfoLte) cellInfo).getCellIdentity().getMcc();

                        // Gets theLTE MNC: (returns 2 or 3-digit Mobile Network Code, 0..999, Integer.MAX_VALUE if unknown)
                        AtCellMNC = ((CellInfoLte) cellInfo).getCellIdentity().getMnc();

                        // Gets the LTE PCI: (returns Physical Cell Id 0..503, Integer.MAX_VALUE if unknown)
                        AtCellPCI = ((CellInfoLte) cellInfo).getCellIdentity().getPci();

                        // Gets the LTE TAC: (returns 16-bit Tracking Area Code, Integer.MAX_VALUE if unknown)
                        AtCellTAC = ((CellInfoLte) cellInfo).getCellIdentity().getTac();

                        Log.d(TagName, "RSRP:" + AtCellRSRP +
                                " cellID:" + AtCellID +
                                " cellMcc:" + AtCellMCC +
                                " cellMnc:" + AtCellMNC +
                                " cellPci:" + AtCellPCI +
                                " cellTac:" + AtCellTAC);
                    }
                    else if (cellInfo instanceof CellInfoWcdma) {
                        CellInfoType = "Wcdma";
                        WcdmaAtCellSignalStrength = ((CellInfoWcdma) cellInfo).getCellSignalStrength().getDbm();

                        WcdmaAtCellID = ((CellInfoWcdma) cellInfo).getCellIdentity().getCid();

                        WcdmaAtCellMCC = ((CellInfoWcdma) cellInfo).getCellIdentity().getMcc();

                        WcdmaAtCellMNC = ((CellInfoWcdma) cellInfo).getCellIdentity().getMnc();

                        WcdmaAtCellPsc = ((CellInfoWcdma) cellInfo).getCellIdentity().getPsc();

                        WcdmaAtCellLac = ((CellInfoWcdma) cellInfo).getCellIdentity().getLac();

                        Log.d(TagName, "WcdmaAtCellSignalStrength:" + WcdmaAtCellSignalStrength +
                                " WcdmaAtCellID:" + WcdmaAtCellID +
                                " WcdmaAtCellMCC:" + WcdmaAtCellMCC +
                                " WcdmaAtCellMNC:" + WcdmaAtCellMNC +
                                " WcdmaAtCellPsc:" + WcdmaAtCellPsc +
                                " WcdmaAtCellLac:" + WcdmaAtCellLac);
                    }
                    AtCellTimeInterval = curTestTime - preTestTime;

                    RunIntentService2.recordwriter.write(JsonParser.AtCellInfoToJson() + "\n,\n");
                    if( CellInfoType.equals("LTE") && AtCellID!=NowCellID ){
                        HandoverOccur();
                    }
                    else if( CellInfoType.equals("Wcdma") && WcdmaAtCellID!=NowCellID ){
                        HandoverOccur();
                    }

                    //ask ShowMsg to change the ViewText
                    Msg = "AppicationType: Android" +
                            "\nApplicationVersion: " + Build.VERSION.RELEASE +
                            "\nAccount: " + JsonParser.Account +
                            "\nPassword: " + JsonParser.Password +
                            "\nequipmentId: " + MainActivity.tm.getDeviceId() +
                            "\nEvent: Measurement" +
                            "\nBatteryLevel:" + BatteryInfoReceiver.level;
                    if( LocationUpdater.userlocation!=null ){
                        Msg = Msg.concat("Lat: " + String.valueOf(LocationUpdater.userlocation.getLatitude()) +
                                "\nLng: " + String.valueOf(LocationUpdater.userlocation.getLongitude()) );
                    }
                    else{
                        Msg = Msg.concat("Lat: Unknown" +
                                "\nLng: Unknown");
                    }
                    if( CellInfoType.equals("LTE") ){
                        Msg = Msg.concat("\nTime: " + curTestTime +
                                "\nCellInfoType: " + String.valueOf(SignalStrengthListener.CellInfoType) +
                                "\nCellID: " + String.valueOf(SignalStrengthListener.AtCellID) +
                                "\nCellMCC: " + String.valueOf(SignalStrengthListener.AtCellMCC) +
                                "\nCellMNC: " + String.valueOf(SignalStrengthListener.AtCellMNC) +
                                "\nCellPCI: " + String.valueOf(SignalStrengthListener.AtCellPCI) +
                                "\nCellTAC: " + String.valueOf(SignalStrengthListener.AtCellTAC) +
                                "\nRSSI(dbm): " + String.valueOf(SignalStrengthListener.AtCellRSSI) +
                                "\nSINR: null" +
                                "\nRSRQ: " + String.valueOf(SignalStrengthListener.AtCellRSRQ) +
                                "\nRSRP(dbm): " + String.valueOf(SignalStrengthListener.AtCellRSRP) +
                                "\nTimeInterval: " + String.valueOf(SignalStrengthListener.AtCellTimeInterval));
                    }
                    else if( CellInfoType.equals("Wcdma") ){
                        Msg = Msg.concat("\nTime: " + curTestTime +
                                "\nCellInfoType: " + String.valueOf(SignalStrengthListener.CellInfoType) +
                                "\nCellID: " + String.valueOf(SignalStrengthListener.WcdmaAtCellID) +
                                "\nCellMCC: " + String.valueOf(SignalStrengthListener.WcdmaAtCellMCC) +
                                "\nCellMNC: " + String.valueOf(SignalStrengthListener.WcdmaAtCellMNC) +
                                "\nCellPsc: " + String.valueOf(SignalStrengthListener.WcdmaAtCellPsc) +
                                "\nCellLAC: " + String.valueOf(SignalStrengthListener.WcdmaAtCellLac) +
                                "\nSignalStrength(dbm): " + String.valueOf(SignalStrengthListener.WcdmaAtCellSignalStrength) +
                                "\nTimeInterval: " + String.valueOf(SignalStrengthListener.AtCellTimeInterval));
                    }
                    ShowMsg.NoticeChange(ShowMsg.AtCellInfo, Msg);

                    preTestTime = curTestTime;
                    break;
                }
            }
        }
        catch (Exception e){
            Log.d(TagName, "Exception: " + e.toString());
        }
    }

    private void HandoverOccur(){
        Log.d(TagName, "Handover");
        if( CellInfoType.equals("LTE") && AtCellID!=NowCellID ){
            NowCellID = AtCellID;
        }
        else if( CellInfoType.equals("Wcdma") && WcdmaAtCellID!=NowCellID ){
            NowCellID = WcdmaAtCellID;
        }
        PassCellNum = PassCellNum+1;
        if( StayCellAt==0 ){
            StayCellAt = curTestTime;
        }
        else{
            CellHoldTime = curTestTime - StayCellAt;
            TtlCellResidenceTime = TtlCellResidenceTime + CellHoldTime;
            StayCellAt = curTestTime;
        }

        if( MainSetting.PhoneStateSwitch )
            PSListener.HandoverOccur();

        try {
            RunIntentService2.recordwriter.write(JsonParser.HandoverInfoToJson() + "\n,\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initBfRun() {
        preTestTime = System.currentTimeMillis();
        AtCellRSRP = 0;
        AtCellID = 0;
        AtCellMCC = 0;
        AtCellMNC = 0;
        AtCellPCI = 0;
        AtCellTAC = 0;
        AtCellRSSI = 0;
        AtCellTimeInterval = 0;
        CellInfoType = "LTE";

        WcdmaAtCellPsc = 0;
        WcdmaAtCellLac = 0;
        WcdmaAtCellID = 0;
        WcdmaAtCellMCC = 0;
        WcdmaAtCellMNC = 0;
        WcdmaAtCellSignalStrength = 0;

        PassCellNum = 0;
        NowCellID = 0;
        StayCellAt = 0;
        CellHoldTime = 0;
        AvgCellResidenceTime = 0;
        AvgCellHoldTime = 0;
        TtlCellResidenceTime = 0;
    }

    public void startService(TelephonyManager tm){
        initBfRun();
        tm.listen(this, SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS);
    }

    public void stopService(TelephonyManager tm){
        tm.listen(this, SignalStrengthListener.LISTEN_NONE);
    }
}
