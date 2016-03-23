package edu.nctu.wirelab.testsignalv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ImportKey extends AppCompatActivity {
    private Button BrowseButton, ImportButton;
    private TextView KeyPathTextView;
    public static String KeyPath = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_key);

        BrowseButton = (Button)findViewById(R.id.BrowseButton);
        ImportButton = (Button)findViewById(R.id.ImportButton);
        KeyPathTextView = (TextView)findViewById(R.id.KeyPathTextView);
        BrowseButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(ImportKey.this, BrowseFile.class);

                startActivity(intent);
            }
        });

        ImportButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                try {
                    MainActivity.moutcypher.setKey(BrowseFile.getStringFromFile(KeyPath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        KeyPathTextView.setText("KeyPath:"+KeyPath);
    }

    @Override
    protected void onResume() {
        super.onResume();

        KeyPathTextView.setText("KeyPath:"+KeyPath);
    }
}
