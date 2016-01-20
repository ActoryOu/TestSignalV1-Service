package edu.nctu.wirelab.testsignalv1;

import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SignalStrengthListener extends PhoneStateListener {
    private final String TagName = "SignalStrengthListener";
    private long curTestTime=0, preTestTime=0;
    private List<CellInfo> cellInfoList;
    private String Msg;
    public static int AtCellRSRP, AtCellID, AtCellMCC, AtCellMNC, AtCellPCI, AtCellTAC, AtCellRSSI, AtCellRSRQ;
    public static long AtCellTimeInterval;

    private Date LogTime;
    private SimpleDateFormat LogTimesdf = new SimpleDateFormat("HH:mm:ss:SSS");

    public SignalStrengthListener(){
        preTestTime = System.currentTimeMillis();
        AtCellRSRP = 0;
        AtCellID = 0;
        AtCellMCC = 0;
        AtCellMNC = 0;
        AtCellPCI = 0;
        AtCellTAC = 0;
        AtCellRSSI = 0;
        AtCellTimeInterval = 0;
    }

    @Override
    public void onSignalStrengthsChanged(android.telephony.SignalStrength signalstrength) {
//            The parts[] array will then contain these elements:
//
//            part[0] = "Signalstrength:"  _ignore this, it's just the title_
//            parts[1] = GsmSignalStrength
//            parts[2] = GsmBitErrorRate
//            parts[3] = CdmaDbm
//            parts[4] = CdmaEcio
//            parts[5] = EvdoDbm
//            parts[6] = EvdoEcio
//            parts[7] = EvdoSnr
//            parts[8] = LteSignalStrength -> if( 0<=parts[8]<=99 ) RSSI = parts[8]*2 - 113   else if( 1000<=parts[8]<=199 ) RSSI = parts[8] - 216
//            parts[9] = LteRsrp
//            parts[10] = LteRsrq
//            parts[11] = LteRssnr
//            parts[12] = LteCqi
//            parts[13] = gsm|lte
//            parts[14] = _not really sure what this number is_
//            Log.d(TagName, "onSSChanged start");
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
                if (cellInfo instanceof CellInfoLte && cellInfo.isRegistered()) {
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
                    AtCellTimeInterval = curTestTime - preTestTime;

                    Log.d(TagName, JsonParser.ParseToJson());

                    MainActivity.filewriter.write("Attached BS Info:\nLogTime(HH:mm:ss:SSS):" + LogTimesdf.format(LogTime) + "\n" +
                            "RSRP:" + AtCellRSRP + '\n' +
                            "CellID:" + AtCellID + '\n' +
                            "CellMCC:" + AtCellMCC + '\n' +
                            "CellMNC:" + AtCellMNC + '\n' +
                            "CellPCI:" + AtCellPCI + '\n' +
                            "CellTAC:" + AtCellTAC + '\n' +
                            "CellRSRQ:" + AtCellRSRQ + '\n' +
                            "TimeInterval:" + AtCellTimeInterval + "\n---------------------------------------------------------\n\n");

                    //ask ShowMsg to change the ViewText
                    Msg = "RSRP(dbm): " + String.valueOf(SignalStrengthListener.AtCellRSRP) +
                            "\nRSSI(dbm): " + String.valueOf(SignalStrengthListener.AtCellRSSI) +
                            "\nCellID: " + String.valueOf(SignalStrengthListener.AtCellID) +
                            "\nCellMCC: " + String.valueOf(SignalStrengthListener.AtCellMCC) +
                            "\nCellMNC: " + String.valueOf(SignalStrengthListener.AtCellMNC) +
                            "\nCellPCI: " + String.valueOf(SignalStrengthListener.AtCellPCI) +
                            "\nCellTAC: " + String.valueOf(SignalStrengthListener.AtCellTAC) +
                            "\nCellRSRQ:" + String.valueOf(SignalStrengthListener.AtCellRSRQ) +
                            "\nTimeInterval: " + String.valueOf(SignalStrengthListener.AtCellTimeInterval);
                    ShowMsg.NoticeChange(ShowMsg.AtCellInfo, Msg);

                    preTestTime = curTestTime;
                }
            }
        }
        catch (Exception e){
            Log.d(TagName, "Exception: " + e.toString());
        }
    }
}
