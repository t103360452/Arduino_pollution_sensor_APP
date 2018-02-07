package com.led.led;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

public class Information extends ActionBarActivity {

    ImageButton Btn_now,Btn_statistic,Btn_otherarea,Btn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        getSupportActionBar().hide();

        // ID連結
        Btn_now=(ImageButton)findViewById(R.id.imageButton2) ;
        Btn_statistic=(ImageButton)findViewById(R.id.imageButton4);
        Btn_otherarea=(ImageButton)findViewById(R.id.imageButton3);
        Btn_setting=(ImageButton)findViewById(R.id.imageButton6);


        //now的按鈕監聽
        Btn_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        //統計的按鈕監聽
        Btn_statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(0, 0);
                Intent i = new Intent(Information.this, Statistic.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

            }
        });

        //到其他空污按鈕監聽
        Btn_otherarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
                Intent i = new Intent(Information.this, OtherArea.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });

        //返回設定的按鈕監聽
        Btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });



    }
}
