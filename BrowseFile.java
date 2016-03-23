package edu.nctu.wirelab.testsignalv1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BrowseFile extends AppCompatActivity {
    private static final String TagName = "BrowseFile";
    private ListView listView;
    private String FilePath;
    private File[] filelist;
    private final int FileShiftUnit = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_file);

        listView = (ListView) findViewById(R.id.listView);

        FilePath = "/";

        ChangeDirectory();

        //ListView onClick
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                //Log.d(TagName, "position:"+position+"  name:"+filelist[position]);
                if( filelist[position].isDirectory() ){
                    FilePath = filelist[position].getAbsolutePath();
                    Log.d(TagName, "FilePath:"+FilePath);
                    ChangeDirectory();
                }
                else{
                    ImportKey.KeyPath = filelist[position].getAbsolutePath();
                    onBackPressed();
                }
            } //end onItemClick
        }); //end setOnItemClickListener
    }

    private void ChangeDirectory(){
        File dir = new File(FilePath);
        filelist = dir.listFiles();
        ArrayAdapter<String> adapter;
        if( dir.canRead()==false ){
            String[] theNamesOfFiles = new String[0];
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, theNamesOfFiles);
        }
        else {
            String[] theNamesOfFiles = new String[filelist.length];

            //add ".." for back to upper layer
            //theNamesOfFiles[0] = "..";
            for (int i = 0; i < theNamesOfFiles.length; i++) {
                theNamesOfFiles[i] = filelist[i].getName();
                if (filelist[i].isDirectory())
                    theNamesOfFiles[i] = theNamesOfFiles[i] + "/";
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, theNamesOfFiles);
        }

        listView.setAdapter(adapter);
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
