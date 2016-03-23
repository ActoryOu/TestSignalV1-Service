package edu.nctu.wirelab.testsignalv1;

import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPController extends AsyncTask<Void, String, Integer> {
    private final String TagName = "FTPController";
    FTPClient ftpclient;

    public boolean connectFTPServer(){
        try {
            ftpclient.connect("140.113.216.37", 21);
            if( ftpclient.login("wirelab", "wirelab020") ){
                if( !FTPReply.isPositiveCompletion(ftpclient.getReplyCode()) ){
                    ftpclient.disconnect();
                    return false;
                }
            }
            else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        if( connectFTPServer() ){
            try {
                //ftpclient.makeDirectory("/file/data");
                ftpclient.changeWorkingDirectory("/file/data");

                ftpclient.setBufferSize(1024);
                ftpclient.setControlEncoding("UTF-8");
                ftpclient.enterLocalPassiveMode();
                ftpclient.setFileType(FTP.BINARY_FILE_TYPE);

                File folder = new File(MainActivity.LOGPATH);
                String[] filelist = folder.list();
                boolean showflag = true;
                for( int i=0;i<filelist.length;i++ ){
                    if( !filelist[i].contains("uploaded") ){
                        if( filelist[i].contains(".sig") ){
                            ftpclient.changeWorkingDirectory("/file/sig");
                        }
                        else{
                            ftpclient.changeWorkingDirectory("/file/data");
                        }
                        FileInputStream fileInputStream = new FileInputStream(MainActivity.LOGPATH+filelist[i]);
                        ftpclient.storeFile(filelist[i], fileInputStream);
                        File thefile = new File(MainActivity.LOGPATH, filelist[i]);
                        thefile.renameTo(new File( MainActivity.LOGPATH, "uploaded"+filelist[i] ));

                        fileInputStream.close();
                    }

                    float percent = (i/(float)filelist.length)*100;
                    if( percent > 50 && showflag ){
                        publishProgress("50%");
                        showflag=false;
                    }
                    //Log.d(TagName, "filename: "+filelist[i]);
                }

                ftpclient.logout();
                ftpclient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                //showDialog("Connection error");
                return -1;
            }
            return 0;
        }
        return -1;
    }

    @Override
    protected void onPreExecute(){
        ftpclient = new FTPClient();
    }

    @Override
    protected void onProgressUpdate(String... progress ){
        ShowDialogMsg.showDialog(progress[0]);
    }

    @Override
    protected void onPostExecute(Integer Result){
        if( Result==0 )
            ShowDialogMsg.showDialog("100%");
        else
            ShowDialogMsg.showDialog("Connection error");
    }

    public void removeFolder(String FolderPath){
        File folder = new File(FolderPath);
        String[] filelist = folder.list();

        for( int i=0;i<filelist.length;i++ ){
            File file = new File(FolderPath+filelist[i]);
            file.delete();
        }
        ShowDialogMsg.showDialog("Remove 100%");
    }

    public void removeFile(String FilePath){
        File file = new File(FilePath);
        file.delete();
    }
}
