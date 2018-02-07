package com.led.led;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jesse on 2017/9/25.
 */

public class MyDBHelper extends SQLiteOpenHelper {


    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE  TABLE pollution " +
                "(_id INTEGER PRIMARY KEY  NOT NULL , " +
                "date VARCHAR(20)  ," +
                "time VARCHAR(20)  ," +
                "pm25 VARCHAR(10)  ," +
                "pm50 VARCHAR(10)  ," +
                "pm10 VARCHAR(10))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
