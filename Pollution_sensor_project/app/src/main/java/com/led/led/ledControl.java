package com.led.led;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class ledControl extends ActionBarActivity {

    Button readdata,btn_insert;
    ImageButton disconnectBTN,Btn_otherarea,Btn_statistic,Btn_infotmation;
    TextView pm25,pm5,pm10;

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  //SPP UUID. Look for it

    int counter;
    InputStream mmInputStream;
    byte[] readBuffer;
    volatile boolean stopWorker;
    int readBufferPosition;
    Thread workerThread;

    //宣告資料庫
     MyDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //隱藏狀態列
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);
        getSupportActionBar().hide();//隱藏標題列

        helper = new MyDBHelper(this, "pollution", null, 1); //創建資料庫

        Intent newint = getIntent();    //Intent取得資料
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device


        //元件連結
        btn_insert=(Button)findViewById(R.id.buttonIST);
        readdata=(Button)findViewById(R.id.readdata);
        disconnectBTN = (ImageButton)findViewById(R.id.imageButton6);
        Btn_otherarea=(ImageButton)findViewById(R.id.imageButton3) ;
        Btn_statistic=(ImageButton)findViewById(R.id.imageButton4);
        Btn_infotmation=(ImageButton)findViewById(R.id.imageButton);



        new ConnectBT().execute(); //開始多執行緒
        //以下全部都是BUTTON事件監聽
        //以下全部都是BUTTON事件監聽
        //以下全部都是BUTTON事件監聽

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                store_now_pollute_value();

                  msg("add data success:");


            }
        });

        //讀取資料的按鈕監聽
        readdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    sendreaddata();

                    beginListenForData();   //利用thread不斷讀取資料
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //到其他空污按鈕監聽
        Btn_otherarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ledControl.this, OtherArea.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });

        //統計的按鈕監聽
        Btn_statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(ledControl.this, Statistic.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

            }
        });

        //資訊的按鈕監聽
        Btn_infotmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(ledControl.this, Information.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

            }
        });





        //返回到裝置配對按鈕監聽
        disconnectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Disconnect(); //close connection
            }
        });



    }



    void beginListenForData() throws IOException {
        mmInputStream = btSocket.getInputStream();
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() //處理資料地方
                                    {
                                        public void run()
                                        {
                                            //讀取空汙值所在


                                              String[] AfterSplit = data.split(",");
                                            ((TextView)findViewById(R.id.textView13)).setText(AfterSplit[0]);
                                            ((TextView)findViewById(R.id.textView14)).setText(AfterSplit[1]);
                                            ((TextView)findViewById(R.id.textView15)).setText(AfterSplit[2]);



                                            findViewById(R.id.image1).setVisibility(View.GONE);
                                            findViewById(R.id.image2).setVisibility(View.GONE);
                                            findViewById(R.id.image3).setVisibility(View.GONE);
                                            findViewById(R.id.image4).setVisibility(View.GONE);
                                            double result=Double.parseDouble(AfterSplit[1]);

                                              if(result<=15)
                                              {
                                                  findViewById(R.id.image1).setVisibility(View.VISIBLE);
                                              }
                                              else if(result<=35)
                                              {
                                                  findViewById(R.id.image2).setVisibility(View.VISIBLE);
                                              }
                                              else if(result<=65)
                                              {
                                                  findViewById(R.id.image3).setVisibility(View.VISIBLE);
                                              }
                                              else
                                              {
                                                  findViewById(R.id.image4).setVisibility(View.VISIBLE);
                                              }
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }


    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }

    private  void sendreaddata()    //丟傳送PM2.5指令給Arduino
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("ttt".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                 myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                 BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                 btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                 btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }


    //回傳字串陣列  str[0]年,str[1]月,str[2]日,str[3]時,str[4]分,str[5]秒
    private String[] getTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = formatter.format(curDate);
        String [] split_str=str.split("-");
        return split_str;
    }

    private void store_now_pollute_value()
    {
        String[] str_time=getTime(); //取得系統時間
        String date=str_time[0]+"/"+str_time[1]+"/"+str_time[2];    //日期 ex:2017/10/4
        String time=str_time[3]+":"+str_time[4]+":"+str_time[5];    //時間 ex: 14:23:23

        //取得目前空汙值(pm2.5,pm5.0, pm10)
        String[] pollute_value={((TextView)findViewById(R.id.textView13)).getText().toString(),
                                ((TextView)findViewById(R.id.textView14)).getText().toString(),
                                ((TextView)findViewById(R.id.textView15)).getText().toString()};

        SQLiteDatabase db = helper.getWritableDatabase();   //開啟資料庫寫入
        ContentValues cv = new ContentValues(); //打包數據

        cv.put("date", date);
        cv.put("time",time);
        cv.put("pm25", pollute_value[0]);
        cv.put("pm50", pollute_value[1]);
        cv.put("pm10", pollute_value[2]);

        db.insert("pollution",null,cv);

    }
}
