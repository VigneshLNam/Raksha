package com.devlover.g_raksha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    private String Value;
    public static final String DATABASE_NAME = "Rakshag.db";
    public static final String Table_NAME = "User";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    private String CREATE_USER_TABLE = "CREATE TABLE " + Table_NAME + "(" + COLUMN_USER_NAME + " TEXT PRIMARY KEY,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS '"+Table_NAME+"'");
        onCreate(db);
    }

    public boolean register(String name,String user,String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_NAME , name);
        contentValues.put(COLUMN_USER_EMAIL, user);
        contentValues.put(COLUMN_USER_PASSWORD, password);
        db.insert(Table_NAME,null,contentValues);
        return true;
    }

    public ArrayList ifAvailable(){
        ArrayList<String> values = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_USER_NAME,
                COLUMN_USER_EMAIL,
                COLUMN_USER_PASSWORD
        };
        Cursor cursor = db.query(Table_NAME,columns,null,null,null,null,null);
        int cursorCount = cursor.getCount();
        if (cursorCount == 1) {
            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndexOrThrow(COLUMN_USER_NAME);
                String fName = cursor.getString(index);
                int index1 = cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL);
                String email = cursor.getString(index1);
                int index2 = cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD);
                String pword = cursor.getString(index2);
                values.add(fName);
                values.add(email);
                values.add(pword);
            }
        }
        return values;
    }

    public boolean checkUser (String name){
        if (Value.equals(name)){
            return true;
        }
        return false;
    }

    public boolean login(String user,String password) {
        ArrayList<String> values = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_USER_NAME
        };
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {user,password};
        Cursor cursor = db.query(Table_NAME,columns,selection,selectionArgs,null,null,null);
        int cursorCount = cursor.getCount();
        while (cursor.moveToNext()){
            int index = cursor.getColumnIndexOrThrow(COLUMN_USER_NAME);
            String fName = cursor.getString(index);
            values.add(fName);
        }
        String nUser = values.get(0);
        this.Value = nUser;
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

}
