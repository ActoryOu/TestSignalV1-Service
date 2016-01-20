package edu.nctu.wirelab.testsignalv1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainSetting extends ActionBarActivity {
    ListView list;
    private final String TagName = "MainSetting";
    private SparseBooleanArray tempchoice;
    public static boolean AtInfoSwitch = true, NbtInfoSwitch = true, PhoneStateSwitch = true, TrafficSwitch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);

        list = (ListView) findViewById(R.id.list);
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
                    switch(key){
                        case 0:
                            if( tempchoice.get(i) ) AtInfoSwitch = true;
                            else AtInfoSwitch = false;
                            break;
                        case 1:
                            if( tempchoice.get(i) ) NbtInfoSwitch = true;
                            else NbtInfoSwitch = false;
                            break;
                        case 2:
                            if( tempchoice.get(i) ) PhoneStateSwitch = true;
                            else PhoneStateSwitch = false;
                            break;
                        case 3:
                            if( tempchoice.get(i) ) TrafficSwitch = true;
                            else TrafficSwitch = false;
                            break;
                    }
                }
            }
        });
    }
}
