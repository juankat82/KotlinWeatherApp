package com.juan.weatherkotlinapp.database

const val TABLE="WEATHER_TABLE"
class WeatherColumns {

        companion object Columns
        {
            const val LOCATION="LOCATION"
            const val LATITUDE="LATITUDE"
            const val LONGITUDE="LONGITUDE"
            const val TIMESTAMP="TIMESTAMP"
            const val WEATHER_CONDITION="WEATHER_CONDITION"
            const val ICON_NAME="ICON_NAME"
            const val TEMPERATURE="TEMPERATURE"
            const val WIND_SPEED="WIND_SPEED"
            const val WIND_DIRECTION="WIND_DIRECTION"
        }
}