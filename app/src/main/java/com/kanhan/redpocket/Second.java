package com.kanhan.redpocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by USER on 2016/12/13.
 */

public class Second extends Activity {
    TextView textShow ;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page2);

        textShow = (TextView)findViewById(R.id.textShow);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("ID");
        String message = bundle.getString("Message");
        String s= "訊息"+ message + "\n" +
                "ID" + id;
        textShow.setText(s);
    }

}
