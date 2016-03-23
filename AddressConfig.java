package edu.nctu.wirelab.testsignalv1;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AddressConfig {
    private static final String TagName = "AddressConfig";
    public static String myUserName = null;

    public static void SetUserName(String username){
        myUserName = username;
    }

    public static void SaveConfigTo(String path){
        //Log.d(TagName, "SaveConfigTo: " + path);

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));

            out.write("username="+myUserName);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean LoadConfigFrom(String path){
        //Log.d(TagName, "LoadConfigFrom: " + path);
        File file = new File(path);
        int flag = 0;

        if(!file.exists()){
            return false;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                Log.d(TagName, "line:"+line);
                String[] token = line.split("=");
                if( token[0].equals("username") && token.length>=2 ){
                    //Log.d(TagName, "token[0].equals(\"email\")");
                    myUserName = token[1];
                    flag = 1;
                    //Log.d(TagName, "myEmail:"+myEmail);
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if( flag==1 ) {
            return true;
        }
        else{
            Log.d(TagName, "lack of email in the config");
            return false;
        }
    }
}
