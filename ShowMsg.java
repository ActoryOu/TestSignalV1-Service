package edu.nctu.wirelab.testsignalv1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class ShowMsg extends ActionBarActivity {
    public static final String AtCellInfo = "AtCellInfo";
    public static final String PhoneState = "PhoneState";
    public static final String AllCellInfo = "AllCellInfo";
    private static TextView AtInfoContent;
    private static TextView PhoneStateContent;
    private static TextView AllCellInfoContent;
    private static String AtInfoString = null;
    private static String PhoneStateString = null;
    private static String AllCellInfoString = null;
    private static boolean isLive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_msg);

        initVar();
        isLive = true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        isLive = false;
    }
    @Override
    protected void onStop(){
        super.onStop();
        isLive = false;
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        isLive = false;
    }
    @Override
    protected void onResume(){
        super.onResume();
        isLive = true;
        if( AtInfoString!=null ) AtInfoContent.setText(AtInfoString);
        if( PhoneStateString!=null ) PhoneStateContent.setText(PhoneStateString);
        if( AllCellInfoString!=null ) AllCellInfoContent.setText(AllCellInfoString);
    }

    public static void NoticeChange( String WhoKnock, String Msg ){
        switch(WhoKnock){
            case AtCellInfo:
                AtInfoString = Msg;
                if( isLive ) AtInfoContent.setText(Msg);
                break;
            case PhoneState:
                PhoneStateString = Msg;
                if( isLive ) PhoneStateContent.setText(Msg);
                break;
            case AllCellInfo:
                AllCellInfoString = Msg;
                if( isLive ) AllCellInfoContent.setText(Msg);
                break;
        }
    }

    public void initVar(){
        AtInfoContent = (TextView)findViewById(R.id.AtInfoContent);
        PhoneStateContent = (TextView)findViewById(R.id.PhoneStateContent);
        AllCellInfoContent = (TextView)findViewById(R.id.AllCellInfoContent);
        if( AtInfoString!=null ) AtInfoContent.setText(AtInfoString);
        if( PhoneStateString!=null ) PhoneStateContent.setText(PhoneStateString);
        if( AllCellInfoString!=null ) AllCellInfoContent.setText(AllCellInfoString);

        AllCellInfoContent.setMovementMethod(new ScrollingMovementMethod());
    }
}
