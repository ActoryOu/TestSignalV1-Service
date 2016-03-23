package edu.nctu.wirelab.testsignalv1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainSetting extends ActionBarActivity {
    ListView list;
    private final String TagName = "MainSetting";
    private SparseBooleanArray tempchoice;
    public static boolean AtInfoSwitch = true, NbtInfoSwitch = true, PhoneStateSwitch = true, TrafficSwitch = true;
    private static boolean TempAtInfoSwitch = true, TempNbtInfoSwitch = true, TempPhoneStateSwitch = true, TempTrafficSwitch = true;
    private EditText LogPrefixEditText, IntervalEditText;
    private Button ConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);

        LogPrefixEditText = (EditText) findViewById(R.id.LogPrefixEditText);
        IntervalEditText = (EditText) findViewById(R.id.IntervalEditText);
        ConfirmButton = (Button) findViewById(R.id.ConfirmButton);
        list = (ListView) findViewById(R.id.list);

        TempAtInfoSwitch = AtInfoSwitch;
        TempNbtInfoSwitch = NbtInfoSwitch;
        TempPhoneStateSwitch = PhoneStateSwitch;
        TempTrafficSwitch = TrafficSwitch;
        IntervalEditText.setText(String.valueOf(MainActivity.FlashInterval), TextView.BufferType.EDITABLE);
        LogPrefixEditText.setText(MainActivity.RecordPrefix, TextView.BufferType.EDITABLE);

        String[] liststr = new String[] { "Sense Attached Info", "Sense All Info", "Sense Phone State", "Sense PS Traffic" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, liststr);

        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setAdapter(adapter);
        list.setItemChecked(0, AtInfoSwitch);
        list.setItemChecked(1, NbtInfoSwitch);
        list.setItemChecked(2, PhoneStateSwitch);
        list.setItemChecked(3, TrafficSwitch);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                AbsListView list = (AbsListView) adapterView;
                //Log.d(TagName, "onItemClick");
                tempchoice = list.getCheckedItemPositions();
                for (int i = 0; i < tempchoice.size(); i++) {
                    //Log.d(TagName, "i:"+i+" get(i):"+tempchoice.get(i)+" keyat(i):"+tempchoice.keyAt(i));
                    int key = tempchoice.keyAt(i);
                    switch (key) {
                        case 0:
                            if (tempchoice.get(i)) TempAtInfoSwitch = true;
                            else TempAtInfoSwitch = false;
                            break;
                        case 1:
                            if (tempchoice.get(i)) TempNbtInfoSwitch = true;
                            else TempNbtInfoSwitch = false;
                            break;
                        case 2:
                            if (tempchoice.get(i)) TempPhoneStateSwitch = true;
                            else TempPhoneStateSwitch = false;
                            break;
                        case 3:
                            if (tempchoice.get(i)) TempTrafficSwitch = true;
                            else TempTrafficSwitch = false;
                            break;
                    }
                }
            }
        });

        ConfirmButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String inputInterval = IntervalEditText.getText().toString();
                if( !inputInterval.isEmpty() ) {
//                    Log.d(TagName, "inputInterval:"+inputInterval);
                    MainActivity.FlashInterval = Integer.valueOf(inputInterval);
                }

                String LogPrefix = LogPrefixEditText.getText().toString();
                if( !LogPrefix.isEmpty() ) {
//                    Log.d(TagName, "LogPrefix:"+LogPrefix);
                    MainActivity.RecordPrefix = LogPrefix;
                }

                AtInfoSwitch = TempAtInfoSwitch;
                NbtInfoSwitch = TempNbtInfoSwitch;
                PhoneStateSwitch = TempPhoneStateSwitch;
                TrafficSwitch = TempTrafficSwitch;

                onBackPressed();
            }
        });
    }
}
