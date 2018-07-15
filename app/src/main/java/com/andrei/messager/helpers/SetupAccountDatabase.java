package com.andrei.messager.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class SetupAccountDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "account";
    public static final int DB_VERSION = 1;
    public static final String ACC_ID = "acc_id";
    public static final String ROLE = "role";
    public static final String EMAIL = "email";

    public SetupAccountDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("on create !!!!!!");
        System.out.println("on create !!!!!!");
        System.out.println("on create !!!!!!");
        System.out.println("on create !!!!!!");
        System.out.println("on create !!!!!!");
        db.execSQL("CREATE TABLE " + DB_NAME + " (" + ACC_ID + " TEXT, " +
                "" + ROLE + " TEXT, " +
                "" + EMAIL + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insert(ContentValues contentValues) {
        boolean insertSuccessful = false;

        SQLiteDatabase db = this.getWritableDatabase();
        insertSuccessful = db.insert(DB_NAME, null,  contentValues) > 0;
        db.close();
        return insertSuccessful;
    }

    public boolean deleteAccountById(String id) {
        boolean insertSuccessful = false;

        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "acc_id = ?";
        //TODO HERE CAN BE ISSUE
        String[] whereArgs = {id};
        insertSuccessful = db.delete(DB_NAME, whereClause,  whereArgs) > 0;
        db.close();
        return insertSuccessful;
    }

    public HashMap<String, String> getAccountDetails() {
        System.out.println("getAccountDetails !!!!!!!");
        HashMap map = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_NAME,
                new String[]{ACC_ID, ROLE, EMAIL},
                null, null, null, null, null);
        String id = "id";
        String email = "email";
        String role = "role";

        if (cursor.moveToFirst()) {
            id = cursor.getString(0);
            role = cursor.getString(1);
            email = cursor.getString(2);
        }
        cursor.close();
        db.close();
        map.put(ACC_ID, id);
        map.put(ROLE, role);
        map.put(EMAIL, email);
        return map;
    }
}
