package com.example.raksha;

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
    public static final String DATABASE_NAME = "Raksha.db";
    public static final String Table_NAME = "User";
    public static final String DEV_NAME = "Device";
    public static final String USER_INFO = "user_info";
    public static final String EMER_INFO = "emer_info";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String DEVICE_ID = "device_id";
    private static final String ADDRESS = "address";
    private static final String PHONE_NO = "phone_no";
    private static final String DATE = "date";
    private static final String PRI_MAIL = "Pri_mail";
    private static final String FPHONE_NO = "fath_phone_no";
    private static final String MPHONE_NO = "moth_phone_no";
    private static final String GPHONE_NO = "guard_phone_no";

    private String CREATE_USER_TABLE = "CREATE TABLE " + Table_NAME + "(" + COLUMN_USER_NAME + " TEXT PRIMARY KEY,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";
    private String CREATE_DEVICE = "CREATE TABLE " + DEV_NAME + "(" + COLUMN_USER_NAME + " TEXT," + DEVICE_ID + " TEXT" + ")";
    private String CREATE_USER_INFO = "CREATE TABLE " + USER_INFO + "(" + COLUMN_USER_NAME + " TEXT," + COLUMN_USER_EMAIL + " TEXT," +
            ADDRESS + " TEXT," + PHONE_NO + " TEXT," + DATE + " TEXT," + PRI_MAIL + " TEXT"+")";
    private String CREATE_EMER_INFO = "CREATE TABLE " + EMER_INFO + "(" + COLUMN_USER_NAME + " TEXT," + COLUMN_USER_EMAIL + " TEXT," +
            FPHONE_NO + " TEXT," + MPHONE_NO + " TEXT," + GPHONE_NO + " TEXT" + ")";
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_DEVICE);
        db.execSQL(CREATE_USER_INFO);
        db.execSQL(CREATE_EMER_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS '"+Table_NAME+"'");
        db.execSQL("DROP TABLE IF EXISTS '"+DEV_NAME+"'");
        db.execSQL("DROP TABLE IF EXISTS '"+USER_INFO+"'");
        db.execSQL("DROP TABLE IF EXISTS '"+EMER_INFO+"'");
        onCreate(db);
    }

    public boolean register(String name,String user,String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ContentValues cV = new ContentValues();
        ContentValues pV = new ContentValues();
        cV.put(COLUMN_USER_NAME , name);
        cV.put(DEVICE_ID, "00");
        contentValues.put(COLUMN_USER_NAME , name);
        contentValues.put(COLUMN_USER_EMAIL, user);
        contentValues.put(COLUMN_USER_PASSWORD, password);
        pV.put(COLUMN_USER_NAME , name);
        pV.put(COLUMN_USER_EMAIL, user);
        pV.put(FPHONE_NO, "");
        pV.put(MPHONE_NO, "");
        pV.put(GPHONE_NO, "");
        db.insert(Table_NAME,null,contentValues);
        db.insert(DEV_NAME,null,cV);
        db.insert(EMER_INFO,null,pV);
        return true;
    }

    public ArrayList Insertinfo(){
        ArrayList<String> User_info = ifinfo();
        return User_info;
    }

    public boolean insertinfo(String name, String email, String address, String phoneno, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_NAME, name);
        contentValues.put(COLUMN_USER_EMAIL, email);
        contentValues.put(ADDRESS, address);
        contentValues.put(PHONE_NO, phoneno);
        contentValues.put(DATE, date);
        contentValues.put(PRI_MAIL,"");
        db.insert(USER_INFO, null, contentValues);
        return true;
    }

    public ArrayList ifinfo(){
        ArrayList<String> values = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_USER_NAME,
                COLUMN_USER_EMAIL,
                ADDRESS,
                PHONE_NO,
                DATE,
                PRI_MAIL
        };
        Cursor cursor = db.query(USER_INFO,columns,null,null,null,null,null);
        int cursorCount = cursor.getCount();
        if (cursorCount == 1) {
            while (cursor.moveToNext()) {
                int index2 = cursor.getColumnIndexOrThrow(ADDRESS);
                String address = cursor.getString(index2);
                int index3 = cursor.getColumnIndexOrThrow(PHONE_NO);
                String phno = cursor.getString(index3);
                int index4 = cursor.getColumnIndexOrThrow(DATE);
                String date = cursor.getString(index4);
                int index5 = cursor.getColumnIndexOrThrow(PRI_MAIL);
                String primary_mail = cursor.getString(index5);
                values.add(address);
                values.add(phno);
                values.add(date);
                values.add(primary_mail);
            }
        }
        return values;
    }

    public boolean updatepri_mail(String name,String mail){
        SQLiteDatabase myDB = this.getWritableDatabase();
        myDB.execSQL("UPDATE " + USER_INFO + " SET " + PRI_MAIL + " =  '"+mail+"'  WHERE " + COLUMN_USER_NAME + " =  '"+ name +"'");
        return true;
    }

    public boolean updateuserI(String name,String address,String phoneno, String date) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        myDB.execSQL("UPDATE " + USER_INFO + " SET " + ADDRESS + " =  '"+address+"' ," + PHONE_NO + " =  '"+phoneno+"' ," + DATE + " =  '"+date+"'  WHERE " + COLUMN_USER_NAME + " =  '"+ name +"'");
        return true;
    }

    public ArrayList ifphone(){
        ArrayList<String> values = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                FPHONE_NO,
                MPHONE_NO,
                GPHONE_NO
        };
        Cursor cursor = db.query(EMER_INFO,columns,null,null,null,null,null);
        int cursorCount = cursor.getCount();
        if (cursorCount == 1) {
            while (cursor.moveToNext()) {
                int index2 = cursor.getColumnIndexOrThrow(FPHONE_NO);
                String fph = cursor.getString(index2);
                int index3 = cursor.getColumnIndexOrThrow(MPHONE_NO);
                String mph = cursor.getString(index3);
                int index4 = cursor.getColumnIndexOrThrow(GPHONE_NO);
                String gph = cursor.getString(index4);
                values.add(fph);
                values.add(mph);
                values.add(gph);
            }
        }
        return values;
    }

    public boolean updatefphone(String name,String fph){
        SQLiteDatabase myDB = this.getWritableDatabase();
        myDB.execSQL("UPDATE " + EMER_INFO + " SET " + FPHONE_NO + " =  '"+fph+"'  WHERE " + COLUMN_USER_NAME + " =  '"+ name +"'");
        return true;
    }

    public boolean updatemphone(String name,String mph){
        SQLiteDatabase myDB = this.getWritableDatabase();
        myDB.execSQL("UPDATE " + EMER_INFO + " SET " + MPHONE_NO + " =  '"+mph+"'  WHERE " + COLUMN_USER_NAME + " =  '"+ name +"'");
        return true;
    }

    public boolean updategphone(String name,String gph){
        SQLiteDatabase myDB = this.getWritableDatabase();
        myDB.execSQL("UPDATE " + EMER_INFO + " SET " + GPHONE_NO + " =  '"+gph+"'  WHERE " + COLUMN_USER_NAME + " =  '"+ name +"'");
        return true;
    }

    public boolean update(String name,String device) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        myDB.execSQL("UPDATE " + DEV_NAME + " SET " + DEVICE_ID + " =  '"+device+"'  WHERE " + COLUMN_USER_NAME + " =  '"+ name +"'");
        return true;
    }

    public ArrayList getdevice(){
        ArrayList<String> values = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_USER_NAME,
                DEVICE_ID
        };
        Cursor cursor = db.query(DEV_NAME,columns,null,null,null,null,null);
        int cursorCount = cursor.getCount();
        if (cursorCount == 1) {
            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndexOrThrow(COLUMN_USER_NAME);
                String fName = cursor.getString(index);
                int index1 = cursor.getColumnIndexOrThrow(DEVICE_ID);
                String devid = cursor.getString(index1);
                values.add(fName);
                values.add(devid);
            }
        }
        return values;
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
