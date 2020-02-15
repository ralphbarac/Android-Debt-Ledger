package com.example.thomasmorphew.roomies;

import android.database.sqlite.SQLiteDatabase;

public class Database {

    private SQLiteDatabase database = null;

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }
}
