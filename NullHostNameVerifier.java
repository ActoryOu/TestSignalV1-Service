package edu.nctu.wirelab.testsignalv1;
import android.util.Log;

import javax.net.ssl.HostnameVerifier ;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;

public class NullHostNameVerifier implements HostnameVerifier {
    private final String TagName = "NullHostNameVerifier";

    @Override
    public boolean verify(String hostname, SSLSession session) {
//        Log.d(TagName, "Approving certificate for " + hostname);
        return true;
    }

    private boolean verifyIpAddress(String ipAddress, X509Certificate certificate) {
        return true;
    }
}