package com.flota.inicializacion.trans_init.trans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.newpos.libpay.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Julian on 18/06/2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    Context ctx;
    String name;
    //Variables
    DbHelper helper;
    SQLiteDatabase db;
    private static final String TAG = "DbHelper.java";

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        ctx = context;
        this.name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Método no implementado
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Método no implementado
    }

    //Metodos para DB
    public void openDb(String dbName) {
        helper = new DbHelper(ctx, dbName, null, 1);
        db = helper.getWritableDatabase();

    }

    public void closeDb() {
        db.close();
    }

    public long registerStr(String table, String field, String data) {
        ContentValues values = new ContentValues();
        values.put(field, data);
        return db.insert(table, null, values);
    }

    public long registerInt(String table, String field, int data) {
        ContentValues values = new ContentValues();
        values.put(field, data);
        return db.insert(table, null, values);
    }

    public void deleteTableContent(String table) {
        db.execSQL("DELETE FROM " + table);
        db.execSQL("VACUUM");
    }

    public long register(String table, Map<String, String> data) {
        ContentValues values = new ContentValues();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            Logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            values.put(entry.getKey(),entry.getValue());
        }

        return db.insert(table, null, values);
    }

    public void execSql(String s) {
        db.execSQL(s);
    }

    public Cursor rawQuery(String sql) {
        return db.rawQuery(sql, null);
    }

    public Cursor rawQuery(String sql, String[] args) {
        return db.rawQuery(sql, args);
    }
}
