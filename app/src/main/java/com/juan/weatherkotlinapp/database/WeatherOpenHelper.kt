package com.juan.weatherkotlinapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private val VERSION = 1
private val DATABASE_NAME = "weatherdatabase.db"

class WeatherOpenHelper(mContext: Context) : SQLiteOpenHelper(mContext, DATABASE_NAME,null, VERSION) {

    private val context:Context

    init {
        context=mContext
    }

    override fun onCreate(db: SQLiteDatabase) =
        db.execSQL("CREATE TABLE "+TABLE+" ("+
                WeatherColumns.LOCATION+", "+
                WeatherColumns.LATITUDE+", "+
                WeatherColumns.LONGITUDE+", "+
                WeatherColumns.TIMESTAMP+", "+
                WeatherColumns.WEATHER_CONDITION+", "+
                WeatherColumns.ICON_NAME+", "+
                WeatherColumns.TEMPERATURE+", "+
                WeatherColumns.WIND_SPEED+", "+
                WeatherColumns.WIND_DIRECTION+")")


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}