package edu.nctu.wirelab.testsignalv1;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class HttpsConnection extends AsyncTask<String, Void, String> {
    private static final String TagName = "HttpsConnection";
    private String configPath;
    private Context mycontext;
    private Certificate ca;
    private SSLContext sslcontext;
    private String SendMethod = null; //SendMethod can be "GET" or "POST"
    private String GETvariables = null, POSTvariables = null;
    private String myUserName = null;

    public HttpsConnection(Context context) {
        mycontext = context;

        try {
            configPath = "/data/data/" + mycontext.getPackageName() + "/config";

            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(mycontext.getAssets().open("apache.pem"));
            ca = cf.generateCertificate(caInput);
            caInput.close();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, tmf.getTrustManagers(), null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        String URLfile = "";
        String ResponseStr="";
        try {
            if( SendMethod!=null && SendMethod.equals("GET") ){
                URLfile = params[0].concat("?"+GETvariables);
            }
            else{
                URLfile = params[0];
            }

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            url = new URL("https", "140.113.216.37", URLfile); // ("https", "140.113.216.37", "/abc/def") -> https://140.113.216.37/abc/def
            Log.d(TagName, "URLfile:" + URLfile);

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(sslcontext.getSocketFactory());
            urlConnection.setHostnameVerifier(new NullHostNameVerifier());
            if( SendMethod!=null && SendMethod.equals("GET") ){
                urlConnection.setRequestMethod("GET");
            }
            else if( SendMethod!=null && SendMethod.equals("POST") ){
                urlConnection.setRequestMethod("POST");
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(POSTvariables);
                wr.flush();
                wr.close();
            }

            InputStream in = urlConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d(TagName, "line:" + line);
                ResponseStr = ResponseStr.concat(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TagName, "The website may be crashed");
            e.printStackTrace();
        }
        return ResponseStr;
    }

    public void setMethod(String method, String variables){
        SendMethod = method;

        SplitAndGetVariables(variables);

        if( SendMethod.equals("GET") ){
            GETvariables = variables;
            POSTvariables = null;
        }
        else if( SendMethod.equals("POST") ){
            POSTvariables = variables;
            GETvariables = null;
        }
        else{
            SendMethod = null;
            POSTvariables = null;
            GETvariables = null;
        }
    }

    public void SplitAndGetVariables(String variables){
        String[] token = variables.split("&");

        for( String t : token ){
            if(t.contains("username")){
//                Log.d(TagName, "t:"+t);
                String[] subtoken = t.split("=");
                myUserName = subtoken[1];
//                Log.d(TagName, "myUserName:"+myUserName);
            }
        }
    }

    protected void onPostExecute(String result)
    {
        if( result.compareTo("create a user successfully")==0 ){
            AddressConfig.SetUserName(myUserName);
            AddressConfig.SaveConfigTo(configPath);
            JsonParser.SetAccount(myUserName);
        }
        else if( result.compareTo("login successfully")==0 ){
            AddressConfig.SetUserName(myUserName);
            AddressConfig.SaveConfigTo(configPath);
            JsonParser.SetAccount(myUserName);
        }
        else if( result.compareTo("login unsuccessfully")==0 ){

        }
        else if( result.compareTo("Error")==0 ){
            result = "registration failed";
        }
        ShowDialogMsg.showDialog(result);
    }
}
