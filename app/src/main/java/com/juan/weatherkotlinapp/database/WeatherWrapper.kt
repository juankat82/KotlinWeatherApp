package com.juan.weatherkotlinapp.database

import android.database.Cursor
import android.database.CursorWrapper
import android.util.Log
import com.juan.weatherkotlinapp.model.Weather

class WeatherWrapper(cursor:Cursor) : CursorWrapper (cursor){

    private var mCursor: Cursor
    private lateinit var mWeather:Weather

    init {
        mCursor=cursor
    }
    fun getWeatherData(cursor: Cursor) : Weather?
    {
        mCursor=cursor;
        mWeather=Weather()

        if (mCursor.getCount()==0)
        {
            return null;
        }
        else
        {
            try
            {
                mCursor.moveToFirst();
                mWeather.mLocation=mCursor.getString(mCursor.getColumnIndex(WeatherColumns.LOCATION))
                mWeather.mLatitude=mCursor.getFloat(mCursor.getColumnIndex(WeatherColumns.LATITUDE))
                mWeather.mLongitude=mCursor.getFloat(mCursor.getColumnIndex(WeatherColumns.LONGITUDE))
                mWeather.mTimeStamp=mCursor.getLong(mCursor.getColumnIndex(WeatherColumns.TIMESTAMP))
                mWeather.mWeatherCondition=mCursor.getString(mCursor.getColumnIndex(WeatherColumns.WEATHER_CONDITION))
                mWeather.iconName=mCursor.getString(mCursor.getColumnIndex(WeatherColumns.ICON_NAME))
                mWeather.temperature=mCursor.getFloat(mCursor.getColumnIndex(WeatherColumns.TEMPERATURE))
                mWeather.windSpeed=mCursor.getFloat(mCursor.getColumnIndex(WeatherColumns.WIND_SPEED))
                mWeather.windDirection=mCursor.getString(mCursor.getColumnIndex(WeatherColumns.WIND_DIRECTION))
                mCursor.moveToNext();
            }
            finally
            {
                mCursor.close();
            }
            return mWeather;
        }
    }
}