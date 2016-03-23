package edu.nctu.wirelab.testsignalv1;

import android.content.Context;
import android.widget.Toast;

public class ShowDialogMsg {
    public static Context mcontext;

    public static void showDialog(String msg){
        Toast.makeText(mcontext, msg, Toast.LENGTH_SHORT).show(); //show the msg in 200ms
    }
}
