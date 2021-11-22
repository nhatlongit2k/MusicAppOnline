package com.example.musicapp

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context?, name: String?, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    fun QueryData(sql: String?) {
        val database = writableDatabase
        database.execSQL(sql)
    }

    fun GetData(sql: String?): Cursor {
        val database = readableDatabase
        return database.rawQuery(sql, null)
    }

    override fun onCreate(db: SQLiteDatabase) {}
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
}