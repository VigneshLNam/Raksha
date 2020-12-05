package com.example.raksha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Message_dbHelp extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "R_Message.db";
    public static final String Table_NAME = "Message";
    public static final String REFERENCE = "Reference";
    public static final String SMESSAGE = "SJson_Me";
    public static final String RMESSAGE = "RJson_Me";
    public static final String SL_NO = "Sl_No";

    private String CREATE_MESSAGEDB = "CREATE TABLE " + Table_NAME + "(" +SL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT," + REFERENCE + " TEXT," + SMESSAGE + " TEXT," + RMESSAGE + " TEXT" + ")";

    public Message_dbHelp(@Nullable Context context) {
        super(context,  DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MESSAGEDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '"+Table_NAME+"'");
        onCreate(db);
    }

    public void M_DB(String refer,ArrayList sender,ArrayList reciever){
        Gson gson = new Gson();
        String Sender_Mess = gson.toJson(sender);
        String Reciever_Mess = gson.toJson(reciever);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(REFERENCE,refer);
        contentValues.put(SMESSAGE,Sender_Mess);
        contentValues.put(RMESSAGE,Reciever_Mess);
        db.insert(Table_NAME,null,contentValues);
    }

    public ArrayList sender(String refer){
        ArrayList<String> sender_list = new ArrayList<>();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        SQLiteDatabase myDB = this.getReadableDatabase();
        String[] columns = {
                SMESSAGE
        };
        String selection = REFERENCE + " = ?";
        String[] selectionArgs = {refer};
        Cursor cursor = myDB.query(Table_NAME,columns,selection,selectionArgs,null,null,null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndexOrThrow(SMESSAGE);
            String address = cursor.getString(index);
            ArrayList<String> messageobject = gson.fromJson(address,type);
            sender_list.addAll(messageobject);
        }
        return sender_list;
    }

    public ArrayList reciever(String refer){
        ArrayList<String> reciever_list = new ArrayList<>();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        SQLiteDatabase myDB = this.getReadableDatabase();
        String[] columns = {
                RMESSAGE
        };
        String selection = REFERENCE + " = ?";
        String[] selectionArgs = {refer};
        Cursor cursor = myDB.query(Table_NAME,columns,selection,selectionArgs,null,null,null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndexOrThrow(RMESSAGE);
            String address = cursor.getString(index);
            ArrayList<String> messageobject = gson.fromJson(address,type);
            reciever_list.addAll(messageobject);
        }
        return reciever_list;
    }

    public boolean deletechat(String refer){
        SQLiteDatabase myDB = this.getWritableDatabase();
        myDB.execSQL("DELETE " + " FROM " + Table_NAME + " WHERE " + REFERENCE + " = '" + refer +"'");
        return true;
    }
}