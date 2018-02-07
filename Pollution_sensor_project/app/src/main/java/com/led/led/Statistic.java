package com.led.led;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;

public class Statistic extends ActionBarActivity {
    ListView showdata;
    ImageButton Btn_now,Btn_otherarea,Btn_infotmation,Btn_setting;
    EditText input_year,input_month,input_day;
    LineChart chart;
    Button showpollute;
    //宣告資料庫
    MyDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        getSupportActionBar().hide();

        helper = new MyDBHelper(this, "pollution", null, 1); //創建資料表

        // ID連結
        showpollute=(Button)findViewById(R.id.showpollute);
        Btn_now=(ImageButton)findViewById(R.id.imageButton2) ;
        Btn_otherarea=(ImageButton)findViewById(R.id.imageButton3);
        Btn_infotmation=(ImageButton)findViewById(R.id.imageButton);
        Btn_setting=(ImageButton)findViewById(R.id.imageButton6);
        showdata=(ListView)findViewById(R.id.listt);
        chart = (LineChart) findViewById(R.id.chart);
        input_year=(EditText)findViewById(R.id.editText_year);
        input_month=(EditText)findViewById(R.id.editText_month);
        input_day=(EditText)findViewById(R.id.editText_day);


        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM pollution",null);

        //設定ListView顯示
        myAdapter adapter = new myAdapter(c);
        showdata.setAdapter(adapter);


        showpollute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChart();
            }
        });


        //now的按鈕監聽
        Btn_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);

            }
        });

        //到其他空污按鈕監聽
        Btn_otherarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
                Intent i = new Intent(Statistic.this, OtherArea.class);
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
                Intent i = new Intent(Statistic.this, Information.class);
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

    private void setChart()
    {
        String year=input_year.getText().toString();
        String month=input_month.getText().toString();
        String day=input_day.getText().toString();
        String Date=Create_date_string(year,month,day);
       if(Date!="error_date")   //檢查年月日是否輸入錯誤
       {
           SQLiteDatabase db = helper.getReadableDatabase();   //開啟讀取模式
           String sql="SELECT * FROM `pollution` WHERE `date` = '"+Date+"'";
           Cursor result=db.rawQuery(sql,null);
           if(result.getCount()!=0) //資料庫有該日期資料
           {
               List<Entry> entries_pm25 = new ArrayList<Entry>();
               List<Entry> entries_pm5 = new ArrayList<Entry>();
               List<Entry> entries_pm10 = new ArrayList<Entry>();
               for(int i=0;i<result.getCount();i++)
               {
                   result.moveToPosition(i);
                   String str_time=result.getString(2);    //取得時間 hh:mm:ss
                   String [] strtime=str_time.split(":");

                   float x_value =Float.parseFloat(strtime[0])+Float.parseFloat(strtime[1])/60+Float.parseFloat(strtime[2])/3600;
                   entries_pm25.add(new Entry(x_value,Float.parseFloat(result.getString(3))));
                   entries_pm5.add(new Entry(x_value,Float.parseFloat(result.getString(4))));
                   entries_pm10.add(new Entry(x_value,Float.parseFloat(result.getString(5))));

               }
               Collections.sort(entries_pm25, new EntryXComparator());
               Collections.sort(entries_pm5, new EntryXComparator());
               Collections.sort(entries_pm10, new EntryXComparator());

               LineDataSet dataSet_pm25 = new LineDataSet(entries_pm25, "PM2.5");
               LineDataSet dataSet_pm5 = new LineDataSet(entries_pm5, "PM5");
               LineDataSet dataSet_pm10 = new LineDataSet(entries_pm10, "PM10");

               //設定顏色
               dataSet_pm25.setColor(getResources().getColor(R.color.red));
               dataSet_pm25.setCircleColor(getResources().getColor(R.color.red));

               dataSet_pm5.setColor(getResources().getColor(R.color.green));
               dataSet_pm5.setCircleColor(getResources().getColor( R.color.green));

               dataSet_pm10.setColor(getResources().getColor( R.color.blue));
               dataSet_pm10.setCircleColor(getResources().getColor( R.color.blue));



               List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
               dataSets.add(dataSet_pm25);
               dataSets.add(dataSet_pm5);
               dataSets.add(dataSet_pm10);

               LineData lineData = new LineData(dataSets);
               XAxis xAxis = chart.getXAxis();

               xAxis.setAxisMaximum(25);
               xAxis.setPosition(BOTTOM);
               chart.setData(lineData);
               chart.invalidate(); // refresh



           }else {//查不到資料
               Toast toast=Toast.makeText(this,"查無資料",Toast.LENGTH_LONG);
               toast.show();
           }

       }
       else {
           Toast toast=Toast.makeText(this,"格式錯誤",Toast.LENGTH_LONG);
           toast.show();
       }


    }
    private String Create_date_string(String year,String month,String day) //檢查年月日是否輸入錯誤
    {
        String Str_date="";
        try {
            int y=Integer.parseInt(year);
            int m=Integer.parseInt(month);
            int d=Integer.parseInt(day);
            if (y>2050 || y<1995) return "error_date";
            if (d>31 || d<=0)  return "error_date";
            if (m>12 || m<=0)   return "error_date";
            Str_date+=String.valueOf(year);
            Str_date+="/";
            if(m<10) Str_date+=("0"+String.valueOf(m)+"/");
            else     Str_date+=(String.valueOf(m)+"/");

            if(d<10) Str_date+=("0"+String.valueOf(d));
            else     Str_date+=String.valueOf(d);

        }catch (NumberFormatException err)
        {
            return "error_date";
        }


      return Str_date;
    }

    public class myAdapter  extends BaseAdapter{

        Cursor myAdapterCursor;

        public myAdapter(Cursor c)
        {
            myAdapterCursor = c;
            myAdapterCursor.moveToFirst();

        }

        @Override
        public int getCount() {
            return myAdapterCursor.getCount();
        }

        @Override
        public Object getItem(int i) {
            myAdapterCursor.moveToPosition(i);
            return myAdapterCursor.getString(1);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
          view = getLayoutInflater().inflate(R.layout.listshow_layout,viewGroup,false);

            TextView textView_date=(TextView)view.findViewById(R.id.date);
            TextView textView_pm5=(TextView)view.findViewById(R.id.PM5);
            TextView textView_pm25=(TextView)view.findViewById(R.id.PM25);
            TextView textView_pm10=(TextView)view.findViewById(R.id.PM10);

            myAdapterCursor.moveToPosition(i);
            String str_date=myAdapterCursor.getString(1)+
                       "   "+myAdapterCursor.getString(2);

            textView_date.setText(str_date);
            textView_pm25.setText(myAdapterCursor.getString(3));
            textView_pm5.setText(myAdapterCursor.getString(4));
            textView_pm10.setText(myAdapterCursor.getString(5));



            return view;
        }
    }
}
