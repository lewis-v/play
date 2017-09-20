package com.yw.play.flash;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yw.play.R;
import com.yw.play.main.MainActivity;

public class FlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        handler.sendEmptyMessageDelayed(1,1000);

    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Intent intent = new Intent(FlashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
