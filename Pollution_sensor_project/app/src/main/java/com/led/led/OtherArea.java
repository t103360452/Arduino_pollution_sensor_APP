package com.led.led;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

public class OtherArea extends ActionBarActivity {
    ImageButton Btn_now,Btn_statistic,Btn_infotmation,Btn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //隱藏上面的StatusBar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_area);
        getSupportActionBar().hide();   //隱藏標題

        // ID連結
        Btn_now=(ImageButton)findViewById(R.id.imageButton2) ;
        Btn_statistic=(ImageButton)findViewById(R.id.imageButton4);
        Btn_infotmation=(ImageButton)findViewById(R.id.imageButton);
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
                Intent i = new Intent(OtherArea.this, Statistic.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

            }
        });

        //資訊的按鈕監聽
        Btn_infotmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(0, 0);
                Intent i = new Intent(OtherArea.this, Information.class);
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
