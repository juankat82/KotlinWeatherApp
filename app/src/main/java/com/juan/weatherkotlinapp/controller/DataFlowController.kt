package com.juan.weatherkotlinapp.controller

import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.juan.weatherkotlinapp.database.TABLE
import com.juan.weatherkotlinapp.database.WeatherColumns
import com.juan.weatherkotlinapp.database.WeatherWrapper
import com.juan.weatherkotlinapp.model.Weather

class DataFlowController (weatherDataBase:SQLiteDatabase, sharedPreferences: SharedPreferences) {

    private val mWeatherDatabase: SQLiteDatabase
    private val mSharedPreferences: SharedPreferences

    init
    {
        mWeatherDatabase=weatherDataBase
        mSharedPreferences=sharedPreferences
    }

    fun writeToDatabase(weather: Weather)
    {
        if (weather!=null)
        {
            mWeatherDatabase.execSQL("DELETE FROM "+ TABLE)
            var cv=ContentValues()
            cv.put("LOCATION",weather.mLocation)
            cv.put("LATITUDE",weather.mLatitude)
            cv.put("LONGITUDE",weather.mLongitude)
            cv.put("TIMESTAMP",weather.mTimeStamp)
            cv.put("WEATHER_CONDITION",weather.mWeatherCondition)
            cv.put("ICON_NAME",weather.iconName)
            cv.put("TEMPERATURE",weather.temperature)
            cv.put("WIND_SPEED",weather.windSpeed)
            cv.put("WIND_DIRECTION",weather.windDirection);
            mWeatherDatabase.insert(TABLE,null,cv)
        }
    }


    fun readFromDatabase(whereClause:String, whereArgs:Array<String>) : Weather?
    {
        val cursor=mWeatherDatabase.query(TABLE,null,whereClause,whereArgs,null,null,null)
        val wrapper= WeatherWrapper(cursor)
        return wrapper.getWeatherData(cursor)
    }


    fun getWeatherFromSharedPreferences() : Weather?
    {
        if (mSharedPreferences!=null) {
            val location= mSharedPreferences.getString("LOCATION", "")
            val latitude = mSharedPreferences.getFloat("LATITUDE", 0f)
            val longitude = mSharedPreferences.getFloat("LONGITUDE", 0f)
            val timeStamp = mSharedPreferences.getLong("TIMESTAMP", 0)
            val weatherCondition = mSharedPreferences.getString("WEATHER_CONDITION", "")
            val iconName = mSharedPreferences.getString("ICON_NAME", "")
            val temperature = mSharedPreferences.getFloat("TEMPERATURE", 0f)
            val windSpeed = mSharedPreferences.getFloat("WIND_SPEED", 0f)
            val windDirection = mSharedPreferences.getString("WIND_DIRECTION", "")

            val weather = Weather()
            weather.mLocation=location!!
            weather.mLatitude=latitude
            weather.mLongitude=longitude
            weather.iconName=iconName!!
            weather.mTimeStamp=timeStamp
            weather.mWeatherCondition=weatherCondition!!
            weather.temperature=temperature
            weather.windSpeed=windSpeed
            weather.windDirection=windDirection!!
            return weather
        }
        else
            return null
    }

    fun writeSharedPreferences(weather:Weather)
    {
        if (weather!=null) {
            val editor = mSharedPreferences.edit()
            editor.putString("LOCATION", weather.mLocation)
            editor.putFloat("LATITUDE", weather.mLatitude)
            editor.putFloat("LONGITUDE", weather.mLongitude)
            editor.putLong("TIMESTAMP", weather.mTimeStamp)
            editor.putString("WEATHER_CONDITION", weather.mWeatherCondition)
            editor.putString("ICON_NAME", weather.iconName)
            editor.putFloat("TEMPERATURE", weather.temperature)
            editor.putFloat("WIND_SPEED", weather.windSpeed)
            editor.putString("WIND_DIRECTION", weather.windDirection)
            editor.apply()
        }
    }
}