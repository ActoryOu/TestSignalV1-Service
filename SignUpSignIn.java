package edu.nctu.wirelab.testsignalv1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpSignIn extends AppCompatActivity {
    private final String TagName = "SignUpSignIn";
    private String configPath;
    private EditText EmailEditText, PwdEditText, PwdConfirmationEditText, LocationEditText, SexEditText, CareerEditText, UserNameEditText;
    private Button RegisterButton, LoginButton;
    private String Email=null, Pwd=null, PwdConfirm=null, Location=null, Sex=null, Career=null, UserName=null;

    public void initVar(){
        EmailEditText = (EditText)findViewById(R.id.EmailEditText);
        PwdEditText = (EditText)findViewById(R.id.PwdEditText);
        PwdConfirmationEditText = (EditText)findViewById(R.id.PwdConfirmationEditText);
        LocationEditText = (EditText)findViewById(R.id.LocationEditText);
        SexEditText = (EditText)findViewById(R.id.SexEditText);
        CareerEditText = (EditText)findViewById(R.id.CareerEditText);
        UserNameEditText = (EditText)findViewById(R.id.UserNameEditText);

        RegisterButton = (Button)findViewById(R.id.RegisterButton);
        LoginButton = (Button)findViewById(R.id.LoginButton);

        configPath = "/data/data/" + getPackageName() + "/config";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_sign_in);

        initVar();

        RegisterButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Email=EmailEditText.getText().toString();
                Pwd=PwdEditText.getText().toString();
                PwdConfirm=PwdConfirmationEditText.getText().toString();
                Location=LocationEditText.getText().toString();
                Sex=SexEditText.getText().toString();
                Career=CareerEditText.getText().toString();
                UserName=UserNameEditText.getText().toString();
                if( Email.equals("") || Pwd.equals("") || PwdConfirm.equals("") || UserName.equals("") ){
                    ShowDialogMsg.showDialog("The fields marked wtih '*' should be filled");
                    return;
                }
                else if( Pwd.length()<6 ){
                    ShowDialogMsg.showDialog("The number of Pwd should be 6-18 digits");
                    return;
                }
                else if( Pwd.compareTo(PwdConfirm)!=0 ){
                    ShowDialogMsg.showDialog("The Pwd is different from Pwd Confirmation. Pwd="+Pwd+"  PwdC="+PwdConfirm);
                    return;
                }
                String variables = "email="+Email+"&password="+Pwd+"&location="+Location+"&sex="+Sex+"&career="+Career+"&username="+UserName;
//                String variables = "email="+Email;
                Log.d(TagName, "variables:" + variables);

                HttpsConnection httpsconnection = new HttpsConnection(SignUpSignIn.this);
                httpsconnection.setMethod("GET", variables);
                httpsconnection.execute("/signal/api/signup");//exec with the URLfile such as "index.php" -> https://140.113.216.37/index.php

                final View item = LayoutInflater.from(SignUpSignIn.this).inflate(R.layout.register_comfirm_pop, null);
                new AlertDialog.Builder(SignUpSignIn.this)
                        .setTitle("Registration")
                        .setView(item)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText ConfirmationCodeEditText = (EditText) item.findViewById(R.id.ConfirmationCodeEditText);
                                String ConfirmationCode = ConfirmationCodeEditText.getText().toString();
                                ShowDialogMsg.showDialog("Confirmation Code:" + ConfirmationCode);

                                HttpsConnection httpsconnection = new HttpsConnection(SignUpSignIn.this);
                                httpsconnection.setMethod("GET", "confirmation="+ConfirmationCode);
                                httpsconnection.execute("/signal/api/signup");//exec with the URLfile such as "index.php" -> https://140.113.216.37/index.php
                            }
                        })
                        .show();
            }
        });
        LoginButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                final View item = LayoutInflater.from(SignUpSignIn.this).inflate(R.layout.login_pop, null);
                new AlertDialog.Builder(SignUpSignIn.this)
                        .setTitle("Login")
                        .setView(item)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ShowDialogMsg.showDialog("Cancel");
                            }
                        })
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText UserNameEditText = (EditText) item.findViewById(R.id.UserNameEditText);
                                EditText PwdEditText = (EditText) item.findViewById(R.id.PwdEditText);
                                UserName = UserNameEditText.getText().toString();
                                Pwd = PwdEditText.getText().toString();
                                ShowDialogMsg.showDialog("UserName:" + UserNameEditText.getText().toString() + "\nPwd:" + PwdEditText.getText().toString());

                                String variables = "username="+UserName+"&password="+Pwd;

                                HttpsConnection httpsconnection = new HttpsConnection(SignUpSignIn.this);
                                httpsconnection.setMethod("GET", variables);
                                httpsconnection.execute("/signal/api/login");//exec with the URLfile such as "index.php" -> https://140.113.216.37/index.php
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(SignUpSignIn.this, MainActivity.class);
        intent.setClass(SignUpSignIn.this, MainActivity.class);

        startActivity(intent);
        finish();
    }
}
